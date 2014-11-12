package bits.draw3d.text;

import bits.draw3d.*;
import bits.draw3d.tex.Mipmap2;
import bits.draw3d.DrawEnv;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.*;
import java.nio.*;

import static javax.media.opengl.GL3.*;
import javax.media.opengl.glu.GLU;


/**
 * Manages resources for a single font and prints text to screen. This class
 * uses java.awt graphics to rasterize the fonts at a defined resolution defined
 * by the size of the font, then transfers the rasterized glyphs to an OpenGL
 * texture that can used for fairly efficient printing. AWT is also used to
 * compute kernings during rendering.
 * <p>
 * Note that because the entire typeface is rasterized, each instance of this
 * class can cost significant memory resources. This class is meant to be used
 * in conjuncture with FontManager to minimize the number of textures generated.
 * <p>
 * FontTexture essentially maps pixels onto GL coordinates, so if you alloc
 * a FontTexture with a Font with pointsize 12.0 that maps to 16 pixels high,
 * the FontTexture will render that Font 16.0 GL units tall. You can alter
 * height by scaling your matrix stack, but keep in mind the resolution of the
 * texture is setOn and the quality of the font rendering will be diminished.
 * If you want zero distortation, you should use an orthographic projection
 * with bounds equal to the resolution of your canvas, and make sure the 
 * that your lines of text are rendered at integral coordinates.
 * <p>
 * 
 * @author Philip DeCamp
 */
public class FontTexture implements DrawUnit {

    private static final GLU GLU_INST = new GLU();


    private final Font        mFont;
    private final FontMetrics mMetrics;
    private final GlyphMap    mGlyphs;
    private final Mipmap2     mTexture;

    private final int[] mBlendRevert = { 0 };


    public FontTexture( Font font ) {
        this( font, CharSet.DEFAULT );
    }


    public FontTexture( Font font, CharSet chars ) {
        mFont = font;
        mMetrics = FontUtil.metrics( font );
        mGlyphs = GlyphMaps.newGlyphMap( chars );

        int dim = 256;
        int margin = 4;
        computeGlyphSizes( mMetrics, margin, mGlyphs );

        // Brute force size determination.  Whatevs.
        while( !layoutGlyphs( dim, dim, mGlyphs, null ) ) {
            dim <<= 1;
            if( dim > 1024 * 4 ) {
                throw new InstantiationError( "Font size too large for memory: " + mFont.getSize() );
            }
        }

        BufferedImage im = new BufferedImage( dim, dim, BufferedImage.TYPE_BYTE_GRAY );
        Graphics2D g = (Graphics2D)im.getGraphics();
        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setFont( mFont );
        g.setBackground( Color.BLACK );
        g.setColor( Color.WHITE );

        layoutGlyphs( dim, dim, mGlyphs, g );
        mGlyphs.optimize();

        mTexture = new Mipmap2();
        mTexture.param( GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR );
        mTexture.param( GL_TEXTURE_SWIZZLE_R, GL_ONE );
        mTexture.param( GL_TEXTURE_SWIZZLE_G, GL_ONE );
        mTexture.param( GL_TEXTURE_SWIZZLE_B, GL_ONE );
        mTexture.param( GL_TEXTURE_SWIZZLE_A, GL_RED );
        mTexture.buffer( im );
    }



    /**
     * You MUST call this method before using texture for rendering.
     */
    public void bind( DrawEnv g ) {
        g.drawStream().config( true, true, false );
        g.mBlend.push();
        g.mBlend.set( true );
        if( mTexture == null ) {
            init( g );
        }
        mTexture.bind( g );
    }

    /**
     * You MUST call this method after you are done using texture for rendering.
     */
    public void unbind( DrawEnv g ) {
        if( mTexture == null ) {
            return;
        }
        mTexture.unbind( g );
        g.mBlend.pop();
    }

    /**
     * This method will be called automatically, but you can call it 
     * manually for initialization scheduling purposes.
     */
    public void init( DrawEnv g ) {
        mTexture.init( g );
        mTexture.bind( g );
        mTexture.unbind( g );
        g.checkErr();
    }
    
    /**
     * Call to unload resources. After unloaded, the texture CANNOT be used again.
     */
    public void dispose( DrawEnv g ) {
        mTexture.dispose( g );
    }

    
    public boolean isLoaded() {
        return true;
    }



    public Font getFont() {
        return mFont;
    }

    
    public String getName() {
        return mFont.getName();
    }

    
    public String getFamily() {
        return mFont.getFamily();
    }

    
    public int getStyle() {
        return mFont.getStyle();
    }

    
    public float getPointSize() {
        return mFont.getSize2D();
    }

    
    public float getHeight() {
        return mMetrics.getHeight();
    }

    
    public float getAscent() {
        return mMetrics.getAscent();
    }

    
    public float getDescent() {
        return mMetrics.getDescent();
    }

    
    public float getLeading() {
        return mMetrics.getLeading();
    }

    
    public float getCharWidth( char c ) {
        return mGlyphs.get( c ).mAdvance;
    }

    /**
     * Computes width of sequence of characters. Because FontTexture
     * precomputes sizes, this is probably faster than using 
     * FontUtil.charsWidth(), and accounts for any glyphs that the 
     * FontTexture may have failed to rasterize. It also handles
     * newlines and returns max length of any line.
     */
    public float getCharsWidth( char[] chars, int off, int len ) {
        float maxWidth = 0f;
        float width    = 0f;
        
        for( int i = 0; i < len; i++ ) {
            char c = chars[i + off];
            
            if( c == '\n' ) {
                if( width > maxWidth ) {
                    maxWidth = width;
                }
                width = 0f;
                continue;
            }
            
            Glyph g = mGlyphs.get( c );
            width += g.mAdvance;
        }
        
        if( width > maxWidth ) {
            maxWidth = width;
        }
        
        return maxWidth;
    }
    
    /**
     * Computes width of sequence of characters. Because FontTexture
     * precomputes kerning tables for each glyph, this is probably
     * faster than using FontUtil.charsWidth(), and accounts for any
     * glyphs that the FontTexture may have failed to rasterize.
     */
    public float getCharsWidth( CharSequence chars ) {
        final int len  = chars.length();
        float maxWidth = 0f;
        float width    = 0f;

        for( int i = 0; i < len; i++ ) {
            char c = chars.charAt( i );
            if( c == '\n' ) {
                if( width > maxWidth ) {
                    maxWidth = width;
                }

                width = 0f;
                continue;
            }

            Glyph g = mGlyphs.get( c );
            width += g.mAdvance;
        }
        
        if( width > maxWidth ) {
            maxWidth = width;
        }

        return maxWidth;
    }
    
    
    
    /**
     * Renders character array to screen using [0, 0, 0]
     * as the start of the baseline.
     * <p>
     * You MUST push the FontTexture before calling this method.
     * The characters will be rendered using the current GL color.
     */
    public void renderChars( DrawEnv g, char[] chars, int off, int len ) {
        renderChars( g, 0.0f, 0.0f, 0.0f, chars, off, len );
    }

    /**
     * Renders character sequence to screen using [x, y, z]
     * as the start of the baseline.
     * <p> 
     * You MUST push the FontTexture before calling this method.
     * The characters will be rendered using the current GL color.
     */
    public void renderChars( DrawEnv gg,
                             float x, 
                             float y, 
                             float z,
                             char[] chars,
                             int off,
                             int len ) 
    {
        float xx = x;
        float yy = y;
        DrawStream s = gg.drawStream();
        s.beginQuads();

        for( int i = 0; i < len; i++ ) {
            char c = chars[i + off];
            if( c == '\n' ) {
                xx = x;
                yy -= mMetrics.getHeight();
                continue;
            }

            Glyph g = mGlyphs.get( c );
            s.tex( g.mS0, g.mT0 );
            s.vert( g.mX0 + xx, g.mY0 + yy, z );
            s.tex( g.mS1, g.mT0 );
            s.vert( g.mX1 + xx, g.mY0 + yy, z );
            s.tex( g.mS1, g.mT1 );
            s.vert( g.mX1 + xx, g.mY1 + yy, z );
            s.tex( g.mS0, g.mT1 );
            s.vert( g.mX0 + xx, g.mY1 + yy, z );
            xx += g.mAdvance;
        }

        s.end();
    }
    
    /**
     * Renders character sequence to screen using [0, 0, 0]
     * as the start of the baseline.
     * <p>
     * You MUST push the FontTexture before calling this method.
     * The characters will be rendered using the current GL color.
     */
    public void renderChars( DrawEnv g, CharSequence chars ) {
        renderChars( g, 0.0f, 0.0f, 0.0f, chars );
    }

    /**
     * Renders character sequence to screen using [x, y, z]
     * as the start of the baseline.
     * <p> 
     * You MUST push the FontTexture before calling this method.
     * The characters will be rendered using the current GL color.
     */
    public void renderChars( DrawEnv gg, float x, float y, float z, CharSequence chars ) {
        final int len = chars.length();
        float xx = x;
        float yy = y;
        DrawStream s = gg.drawStream();

        s.beginQuads();

        for( int i = 0; i < len; i++ ) {
            char c = chars.charAt( i );
            if( c == '\n' ) {
                xx = x;
                yy -= mMetrics.getHeight();
                continue;
            }

            Glyph g = mGlyphs.get( c );
            s.tex( g.mS0, g.mT0 );
            s.vert( g.mX0 + xx, g.mY0 + yy, z );
            s.tex( g.mS1, g.mT0 );
            s.vert( g.mX1 + xx, g.mY0 + yy, z );
            s.tex( g.mS1, g.mT1 );
            s.vert( g.mX1 + xx, g.mY1 + yy, z );
            s.tex( g.mS0, g.mT1 );
            s.vert( g.mX0 + xx, g.mY1 + yy, z );

            xx += g.mAdvance;
        }

        s.end();
   }
    
    
    
    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Rect will be rendered using current GL color.
     * 
     * @param margin  Margin by with box will exceed bounds of text.
     */
    public void renderBox( DrawEnv g, CharSequence s, float margin ) {
        renderBox( g, 0.0f, 0.0f, 0.0f, getCharsWidth( s ), margin );
    }
    
    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Rect will be rendered using current GL color.
     */
    public void renderBox( DrawEnv g, float x, float y, float z, CharSequence s, float margin ) {
        renderBox( g, x, y, z, getCharsWidth( s ), margin );
    }
    
    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Rect will be rendered using current GL color.
     */
    public void renderBox( DrawEnv g, char[] chars, int off, int len, float margin ) {
        float width = getCharsWidth( chars, off, len );
        renderBox( g, width, margin );
    }

    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Rect will be rendered using current GL color.
     */
    public void renderBox( DrawEnv g, float x, float y, float z, char[] chars, int off, int len, float margin ) {
        renderBox( g, x, y, z, getCharsWidth( chars, off, len ), margin );
    }

    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Rect will be rendered using current GL color.
     */
    public void renderBox( DrawEnv g, float width, float margin ) {
        renderBox( g, 0.0f, 0.0f, 0.0f, width, margin );
    }

    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Rect will be rendered using current GL color.
     */
    public void renderBox( DrawEnv g, float x, float y, float z, float width, float margin ) {
        DrawStream s = g.drawStream();
        float descent = getDescent();
        float ascent  = getAscent();

        s.beginQuads();
        s.vert( x - margin, y - descent, z );
        s.vert( x + width + margin, y - descent, z );
        s.vert( x + width + margin, y + ascent, z );
        s.vert( x - margin, y + ascent, z );
        s.end();
    }

    
    
    private static void computeGlyphSizes( FontMetrics metrics, int margin, GlyphMap glyphs ) {
        final Font font = metrics.getFont();
        final FontRenderContext context = metrics.getFontRenderContext();
        final CharSequence chars = glyphs.chars();
        final int len = chars.length();
        final char[] carr = new char[1];
        
        for( int i = 0; i < len; i++ ) {
            char c = chars.charAt( i );
            carr[0] = c;
            float advance  = metrics.charWidth( c );
            // TODO: Check if this can be done faster.
            // I'm not sure, but I think I tried getMaxCharBounds() and it wasn't pixel accurate.
            Rectangle rect = font.createGlyphVector( context, carr ).getOutline().getBounds();
            glyphs.put( c, 
                        advance,
                        rect.x - margin,
                        -rect.y - rect.height - margin,
                        0,
                        0,
                        rect.x + rect.width + margin,
                        -rect.y + margin,
                        0,
                        0 );
        }
    }
    
    
    private static boolean layoutGlyphs( int texWidth, int texHeight, GlyphMap glyphs, Graphics2D g ) {
        final CharSequence chars = glyphs.chars();
        final int len = chars.length();
        final char[] carr = new char[1];
        
        int x = 0;
        int y = 0;
        int lineHeight = 0;
        
        for( int i = 0; i < len; i++ ) {
            char c  = chars.charAt( i );
            carr[0] = c;
            Glyph glyph = glyphs.get( c );
            
            int bx = glyph.mX0;
            int by = glyph.mY0;
            int bw = ( glyph.mX1 - glyph.mX0 );
            int bh = ( glyph.mY1 - glyph.mY0 );
            
            // Check if character will fit horizontally on line.
            if( x + bw > texWidth ) {
                // If single character does not fit on line,
                // no point going to next line.
                if( x == 0 ) {
                    return false;
                }
                // Go to next line.
                x = 0;
                y += lineHeight;
                lineHeight = 0;
            }
            // Check if character will fit vertically into texture.
            if( bh > lineHeight ) {
                lineHeight = bh;
                if( y + lineHeight > texHeight ) {
                    return false;
                }
            } 
            // Draw glyph if there's a graphics object.
            if( g != null ) {
                g.drawChars( carr, 0, 1, x - bx, texHeight - y + by ); 
                
                // Update texture bounds on glyph.
                glyphs.put( c,
                            glyph.mAdvance,
                            bx,
                            by,
                            (float)x / texWidth,
                            1f - (float)y / texHeight,
                            bx + bw,
                            by + bh,
                            (float)( x + bw ) / texWidth,
                            1f - (float)( y + bh ) / texHeight );
            }
            
            x += bw;
        }
        
        return true;
    }
    
    
    private static ByteBuffer bufferGrayscale( BufferedImage image ) {
        DataBuffer in = image.getData().getDataBuffer();
        int count     = in.getSize() * in.getNumBanks();
        
        if( DataBuffer.getDataTypeSize( in.getDataType() ) != 8 ) {
            throw new IllegalArgumentException( "Invalid image keyTyped. Image must by 8-bit grayscale." );
        }
        
        ByteBuffer ret = ByteBuffer.allocateDirect( ( ( count + 7 ) / 8 ) * 8 );
        ret.order( ByteOrder.nativeOrder() );
        
        for( int i = 0; i < in.getNumBanks(); i++ ) {
            ret.put( ((DataBufferByte)in).getData( i ) );
        }
        
        ret.flip();
        return ret;
    }
    

}
