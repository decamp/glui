package cogmac.glui.text;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.*;
import java.nio.*;
import java.util.*;

import javax.media.opengl.GL;
import static javax.media.opengl.GL.*;
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
 * FontTexture essentially maps pixels onto GL coordinates, so if you create
 * a FontTexture with a Font with pointsize 12.0 that maps to 16 pixels high,
 * the FontTexture will render that Font 16.0 GL units tall. You can alter
 * height by scaling your matrix stack, but keep in mind the resolution of the
 * texture is set and the quality of the font rendering will be diminished.
 * If you want zero distortation, you should use an orthographic projection
 * with bounds equal to the resolution of your canvas, and make sure the 
 * that your lines of text are rendered at integral coordinates.
 * <p>
 * 
 * @author Philip DeCamp
 */
public class FontTexture {

    public static final float DEFAULT_BOX_MARGIN    = 5.0f;
    private static final GLU  GLU_INST              = new GLU();
    
    
    private final Font              mFont;
    private final FontMetrics       mMetrics;

    private final ImageTextureNode  mTexture;
    private final Glyph[]           mGlyphs       = Glyph.newTable();
    private final int[]             mBlendRevert  = { 0 };


    public FontTexture( Font font ) {
        mFont    = font;
        mMetrics = FontUtil.metrics( font );
        
        int s = 256;
        FontRenderContext context = FontUtil.renderContext();
        
        // Brute force size determination.  Whatevs.  
        while( !layoutGlyphs( context, s, s, null ) ) {
            s <<= 1;

            if( s > 1024 * 4 ) {
                throw new InstantiationError( "Font size too large for memory: " + mFont.getSize() );
            }
        }
        
        BufferedImage im = new BufferedImage( s, s, BufferedImage.TYPE_BYTE_GRAY );

        Graphics2D g = (Graphics2D)im.getGraphics();
        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        g.setFont( mFont );
        g.setBackground( Color.BLACK );
        g.clearRect( 0, 0, s, s );
        g.setColor( Color.WHITE );
        
        layoutGlyphs( g.getFontRenderContext(), s, s, g );
        
        ByteBuffer buf = bufferGrayscale( im );
        mTexture       = new ImageTextureNode( buf, im.getWidth(), im.getHeight() );
    }


    /**
     * You MUST call this method before using texture for rendering.
     * 
     * @param gl
     */
    public void push( GL gl ) {
        gl.glGetIntegerv( GL_BLEND, mBlendRevert, 0 );
        gl.glEnable( GL_BLEND );

        if( mTexture == null ) {
            load( gl );
        }

        mTexture.pushDraw( gl );
    }

    /**
     * You MUST call this method after you are done using texture for rendering.
     * 
     * @param gl
     */
    public void pop( GL gl ) {
        if( mTexture == null )
            return;

        mTexture.popDraw( gl );

        if( mBlendRevert[0] == 0 ) {
            gl.glDisable( GL_BLEND );
        }
    }

    /**
     * This method will be called automatically, but you can call it 
     * manually for initialization scheduling purposes.
     * 
     * @param gl
     */
    public void load( GL gl ) {
        mTexture.init( gl );
    }
    
    /**
     * Call to unload resources. After unloaded, the texture CANNOT be used again.
     * 
     * @param gl
     */
    public void unload( GL gl ) {
        mTexture.dispose( gl );
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
        return Glyph.tableGet( mGlyphs, c ).mAdvance;
    }

    /**
     * Computes width of sequence of characters. Because FontTexture
     * precomputes kerning tables for each glyph, this is probably
     * faster than using FontUtil.charsWidth(), and accounts for
     * any glyphs that the FontTexture may have failed to rasterize.
     * 
     * @param chars
     * @param off
     * @param len
     * @return
     */
    public float getCharsWidth( char[] chars, int off, int len ) {
        float maxWidth = 0f;
        Glyph prev     = null;

        float width = 0f;
        
        for( int i = 0; i < len; i++ ) {
            char c = chars[i + off];

            if( c == '\n' ) {
                if( width > maxWidth ) {
                    maxWidth = width;
                }

                width = 0f;
                prev  = null;
                continue;
            }

            Glyph g = Glyph.tableGet( mGlyphs, c );
            width += g.mAdvance;
            
            if( prev != null ) {
                width += prev.getKern( g.mChar );
            }
            
            prev = g;
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
     * 
     * @param chars
     * @return
     */
    public float getCharsWidth( CharSequence chars ) {
        final int len  = chars.length();
        float maxWidth = 0f;
        Glyph prev     = null;

        float width = 0f;

        for( int i = 0; i < len; i++ ) {
            char c = chars.charAt( i );

            if( c == '\n' ) {
                if( width > maxWidth ) {
                    maxWidth = width;
                }

                width = 0f;
                prev  = null;
                continue;
            }

            Glyph g = Glyph.tableGet( mGlyphs, c );
            width += g.mAdvance;
            
            if( prev != null ) {
                width += prev.getKern( g.mChar );
            }
            
            prev = g;
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
     * 
     * @param gl
     * @param chars
     * @param off
     * @param len
     */
    public void renderChars( GL gl, char[] chars, int off, int len ) {
        renderChars( gl, 0.0f, 0.0f, 0.0f, chars, off, len );
    }
    
    /**
     * Renders character sequence to screen using [x, y, z]
     * as the start of the baseline.
     * <p> 
     * You MUST push the FontTexture before calling this method.
     * The characters will be rendered using the current GL color.
     * 
     * @param gl
     * @param chars
     * @param off
     * @param len
     */
    public void renderChars( GL gl, 
                             float x, 
                             float y, 
                             float z,
                             char[] chars,
                             int off,
                             int len ) 
    {
        float xx        = x;
        float yy        = y;
        Glyph prevGlyph = null;
        
        gl.glBegin( GL_QUADS );

        for( int i = 0; i < len; i++ ) {
            char c = chars[i + off];

            if( c == '\n' ) {
                xx = x;
                yy -= mMetrics.getHeight();
                prevGlyph = null;
                continue;
            }
            
            Glyph g = Glyph.tableGet( mGlyphs, c );
            
            if( prevGlyph != null ) {
                xx += prevGlyph.mAdvance + prevGlyph.getKern( g.mChar );
            }

            gl.glTexCoord2f( g.mU0, g.mV0 );
            gl.glVertex3f( g.mX0 + xx, g.mY0 + yy, z );

            gl.glTexCoord2f( g.mU1, g.mV0 );
            gl.glVertex3f( g.mX1 + xx, g.mY0 + yy, z );
            
            gl.glTexCoord2f( g.mU1, g.mV1 );
            gl.glVertex3f( g.mX1 + xx, g.mY1 + yy, z );
            
            gl.glTexCoord2f( g.mU0, g.mV1 );
            gl.glVertex3f( g.mX0 + xx, g.mY1 + yy, z );
            
            prevGlyph = g;
        }

        gl.glEnd();

        
    }
    
    /**
     * Renders character sequence to screen using [0, 0, 0]
     * as the start of the baseline.
     * <p>
     * You MUST push the FontTexture before calling this method.
     * The characters will be rendered using the current GL color.
     * 
     * @param gl
     * @param chars
     */
    public void renderChars( GL gl, CharSequence chars ) {
        renderChars( gl, 0.0f, 0.0f, 0.0f, chars );
    }
    
    /**
     * Renders character sequence to screen using [x, y, z]
     * as the start of the baseline.
     * <p> 
     * You MUST push the FontTexture before calling this method.
     * The characters will be rendered using the current GL color.
     * 
     * @param gl
     * @param chars
     */
    public void renderChars( GL gl, float x, float y, float z, CharSequence chars ) {
        
        final int len = chars.length();
        
        float xx        = x;
        float yy        = y;
        Glyph prevGlyph = null;
        
        gl.glBegin( GL_QUADS );

        for( int i = 0; i < len; i++ ) {
            char c = chars.charAt( i );

            if( c == '\n' ) {
                xx = x;
                yy -= mMetrics.getHeight();
                prevGlyph = null;
                continue;
            }
            
            Glyph g = Glyph.tableGet( mGlyphs, c );
            
            if( prevGlyph != null ) {
                xx += prevGlyph.mAdvance + prevGlyph.getKern( g.mChar );
            }

            gl.glTexCoord2f( g.mU0, g.mV0 );
            gl.glVertex3f( g.mX0 + xx, g.mY0 + yy, z );

            gl.glTexCoord2f( g.mU1, g.mV0 );
            gl.glVertex3f( g.mX1 + xx, g.mY0 + yy, z );
            
            gl.glTexCoord2f( g.mU1, g.mV1 );
            gl.glVertex3f( g.mX1 + xx, g.mY1 + yy, z );
            
            gl.glTexCoord2f( g.mU0, g.mV1 );
            gl.glVertex3f( g.mX0 + xx, g.mY1 + yy, z );
            
            prevGlyph = g;
        }

        gl.glEnd();

    }
    
    
    
    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Box will be rendered using current GL color.
     * 
     * @param gl
     * @param s
     * @param margin  Margin by with box will exceed bounds of text.
     */
    public void renderBox( GL gl, CharSequence s, float margin ) {
        renderBox( gl, 0.0f, 0.0f, 0.0f, getCharsWidth( s ), margin );
    }
    
    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Box will be rendered using current GL color.
     * 
     * @param gl
     * @param x
     * @param y
     * @param z
     * @param s
     * @param margin
     */
    public void renderBox( GL gl, float x, float y, float z, CharSequence s, float margin ) {
        renderBox( gl, x, y, z, getCharsWidth( s ), margin );
    }
    
    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Box will be rendered using current GL color.
     * 
     * @param gl
     * @param chars
     * @param off
     * @param len
     * @param margin
     */
    public void renderBox( GL gl, char[] chars, int off, int len, float margin ) {
        float width = getCharsWidth( chars, off, len );
        renderBox( gl, width, margin );
    }

    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Box will be rendered using current GL color.
     * 
     * @param gl
     * @param x
     * @param y
     * @param z
     * @param chars
     * @param off
     * @param len
     * @param margin
     */
    public void renderBox( GL gl, float x, float y, float z, char[] chars, int off, int len, float margin ) {
        renderBox( gl, x, y, z, getCharsWidth( chars, off, len ), margin );
    }

    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Box will be rendered using current GL color.
     * 
     * @param gl
     * @param width
     * @param margin
     */
    public void renderBox( GL gl, float width, float margin ) {
        renderBox( gl, 0.0f, 0.0f, 0.0f, width, margin );
    }
    
    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Box will be rendered using current GL color.
     * 
     * @param gl
     * @param x
     * @param y
     * @param z
     * @param width
     * @param margin
     */
    public void renderBox( GL gl, float x, float y, float z, float width, float margin ) {
        float descent = getDescent();
        float ascent  = getAscent();
        
        gl.glBegin( GL_QUADS );
        gl.glVertex3f( x - margin        , y - descent, z );
        gl.glVertex3f( x + width + margin, y - descent, z );
        gl.glVertex3f( x + width + margin, y + ascent,  z );
        gl.glVertex3f( x - margin        , y + ascent,  z );
        gl.glEnd();
    }

    
    
    
    
    /**
     * @deprecated Use renderBox( GL, CharSequence, float );
     */
    public void renderBox( GL gl, CharSequence s ) {
        renderBox( gl, s, DEFAULT_BOX_MARGIN );
    }
    
    /**
     * @deprecated User renderBox( GL, char[], int, int, float );
     */
    public void renderBox( GL gl, char[] chars, int off, int len ) {
        renderBox( gl, chars, off, len, DEFAULT_BOX_MARGIN );
    }

    /**
     * @deprecated Use getCharsWidth(chars, off, len);
     */
    public float getCharWidth( char[] chars, int off, int len ) {
        return getCharsWidth( CharBuffer.wrap( chars, off, len ) );
    }

    /**
     * @deprecated Use getCharsWidth(s);
     */
    public float getStringWidth( String s ) {
        return getCharsWidth( s );
    }

    /**
     * @deprecated use renderChars(gl, s);
     */
    public void renderString( GL gl, String s ) {
        renderChars( gl, s );
    }



    private boolean layoutGlyphs( FontRenderContext context, int w, int h, Graphics2D g ) {
        final CharSequence chars = Glyph.CHARACTERS;
        final int margin = 1;

        int x = 0;
        int y = 0;
        int len = chars.length();

        final int ascent = mMetrics.getMaxAscent();
        final int descent = mMetrics.getMaxDescent();
        int lineHeight = 0;

        if( lineHeight > h ) {
            return false;
        }

        if( g != null ) {
            Arrays.fill( mGlyphs, null );
        }

        final char[] carr = { '\0' };

        for( int i = 0; i < len; i++ ) {
            char c  = chars.charAt( i );
            carr[0] = c;
            float advance = mMetrics.charWidth( c );

            Rectangle rect = mFont.createGlyphVector( context, carr ).getOutline().getBounds();
            rect = new Rectangle( rect.x - margin, rect.y - margin, rect.width + margin * 2, rect.height + margin * 2 );
            
            final float cx = rect.x;
            final float cy = rect.y;
            final float cw = rect.width;
            final float ch = rect.height;

            if( x + cw > w ) {
                if( x == 0 ) {
                    return false;
                }

                x = 0;
                y += lineHeight;
                lineHeight = 0;
            }

            if( ch > lineHeight ) {
                lineHeight = rect.height;
                if( y + lineHeight > h ) {
                    return false;
                }
            }

            if( g != null ) {
                g.drawChars( carr, 0, 1, x - rect.x, y - rect.y );
                float[] kernTable = Glyph.computeKerningTable( mMetrics, c );

                Glyph glyph = new Glyph( c,
                                         advance,
                                         kernTable,
                                         rect.x, -( cy + ch ), cx + cw, -cy,
                                         (float)x / w, ( y + ch ) / h, ( x + cw ) / w, (float)y / h );

                Glyph.tablePut( mGlyphs, c, glyph );
            }

            x += rect.width;
        }
        
        Glyph.tableFillNullsWithBlank( mGlyphs );
        
        return true;
    }
    
    
    
    private static ByteBuffer bufferGrayscale( BufferedImage image ) {
        DataBuffer in = image.getData().getDataBuffer();
        int count     = in.getSize() * in.getNumBanks();
        
        if( DataBuffer.getDataTypeSize( in.getDataType() ) != 8 ) {
            throw new IllegalArgumentException( "Invalid image type. Image must by 8-bit grayscale." );
        }
        
        ByteBuffer ret = ByteBuffer.allocateDirect( ( count + 7 ) / 8 * 8 );
        ret.order( ByteOrder.nativeOrder() );
        
        for( int i = 0; i < in.getNumBanks(); i++ ) {
            ret.put( ((DataBufferByte)in).getData( i ) );
        }
        
        ret.flip();
        return ret;
    }
    
    
    
    private static class ImageTextureNode {
        
        private final int[] mId       = { 0 };
        private final int[] mRevert   = { 0, 0 };
        
        private final int  mWidth;
        private final int  mHeight;
        
        private ByteBuffer mBuf   = null;
        private boolean mNeedInit = true;
        
        
        public ImageTextureNode( ByteBuffer buf, int w, int h ) {
            mBuf    = buf;
            mWidth  = w;
            mHeight = h;
        }
        
        
        
        void dispose( GL gl ) {
            if( mId[0] != 0 ) {
                gl.glDeleteTextures( 1, mId, 0 );
                mId[0] = 0;
            }
            
            mBuf      = null;
            mNeedInit = false;
        }
        
        void bind( GL gl ) {
            if( mNeedInit ) {
                init( gl );
            }

            gl.glBindTexture( GL_TEXTURE_2D, mId[0] );
        }
        
        void unbind( GL gl ) {
            gl.glBindTexture( GL_TEXTURE_2D, 0 );
        }
        
        void pushDraw( GL gl ) {
            gl.glGetIntegerv( GL_TEXTURE_2D, mRevert, 0 );
            gl.glGetIntegerv( GL_TEXTURE_BINDING_2D, mRevert, 1 );
            bind( gl );
            gl.glEnable( GL_TEXTURE_2D );
        }
        
        void popDraw( GL gl ) {
            if( mRevert[0] == 0 ) {
                gl.glDisable( GL_TEXTURE_2D );
            }
            gl.glBindTexture( GL_TEXTURE_2D, mRevert[1] );
        }
        
        
        
        private synchronized void init( GL gl ) {
            if( !mNeedInit ) {
                return;
            }
            
            mNeedInit = false;
            
            gl.glGenTextures( 1, mId, 0 );
            if( mId[0] == 0 ) {
                throw new RuntimeException( "Failed to allocate texture." );
            }
            
            gl.glBindTexture( GL_TEXTURE_2D, mId[0] );
            gl.glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR );
            gl.glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );
            
            if( mBuf != null ) {
                GLU_INST.gluBuild2DMipmaps( GL_TEXTURE_2D, GL_ALPHA, mWidth, mHeight, GL_ALPHA, GL_UNSIGNED_BYTE, mBuf );
                mBuf = null;
            }
        }
        
    }

}
