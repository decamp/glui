package cogmac.glui.text;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.regex.*;
import java.util.List;
import java.util.*;


/**
 * TODO: Some of these methods may be more efficient once
 * the JVM has more fully integrated escape analysis. At 
 * that point, the use of alternate implementations for 
 * CharSequence and char arrays may be less optimal than
 * using a single implementation and wrapping parameters
 * where necessary, which would reduce the amount of code.
 * <p>
 * Also, it may eventually be necessary to produce a separate
 * FontMetrics implementation in order to handle kerning more
 * efficiently. Right now, kerning support in this library
 * is pretty limited for pfa and pfb fonts, and it's hard to
 * tell what AWT is actually doing with TT and OTF fonts. 
 * 
 * @author decamp
 */
public class FontUtil {

    private static final BufferedImage METRIC_IMAGE = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );
    private static final Graphics2D METRIC_GRAPHICS = (Graphics2D)METRIC_IMAGE.getGraphics();
    private static final Pattern WORD_PAT           = Pattern.compile( "\\S++" );
    

    public static FontMetrics metrics( Font font ) {
        return METRIC_GRAPHICS.getFontMetrics( font );
    }
    
    
    public static FontRenderContext renderContext() {
        return METRIC_GRAPHICS.getFontRenderContext();
    }
    
    
    public static float charsWidth( Font font, CharSequence s ) {
        return charsWidth( metrics( font ), s, 0, s.length() );
    }
    
    
    public static float charsWidth( Font font, CharSequence s, int off, int len ) {
        return charsWidth( metrics( font ), s, off, len );
    }
    
    
    public static float charsWidth( Font font, char[] chars, int off, int len ) {
        return charsWidth( metrics( font ), chars, off, len );
    }


    public static float charsWidth( FontMetrics metrics, CharSequence seq ) {
        return charsWidth( metrics, seq, 0, seq.length() );
    }


    public static float charsWidth( FontMetrics metrics, CharSequence seq, int off, int len ) {
        int ret = 0;
        for( int i = 0; i < len; i++ ) {
            ret += metrics.charWidth( seq.charAt( off + len ) );
        }
        return ret;
        
        /* 
        // This version assumed that kerning information was actually available.
        // Unfortunately, there's no evidence that Java can provide kerning info at this time.
        if( len == 0 ) {
            return 0;
        }
        
        char[] arr = { seq.charAt( off ), 0 };
        float width = metrics.charWidth( arr[0] );
        float prevAdvance = width;

        for( int i = 1; i < len; i++ ) {
            arr[1] = seq.charAt( off + i );
            float advance = metrics.charsWidth( arr, 0, 2 );
            prevAdvance = advance - prevAdvance;
            width += prevAdvance;
            arr[0] = arr[1];
        }
    
        return width;
        */
    }


    public static float charsWidth( FontMetrics metrics, char[] chars, int off, int len ) {
        int ret = 0;
        for( int i = 0; i < len; i++ ) {
            ret += metrics.charWidth( chars[i+off] );
        }
        return ret;
        
        /*
        // Alas, there's little point since Java does not seem to provide kerning info.
        if( len == 0 ) {
            return 0;
        }

        float width = metrics.charWidth( chars[off] );
        float prevAdvance = width;

        for( int i = 1; i < len; i++ ) {
            float advance = metrics.charsWidth( chars, off + i - 1, 2 );
            prevAdvance   = advance - prevAdvance;
            width += prevAdvance;
        }

        return width;
        */
    }
    
    
    /**
     * Determines how many characters may be laid in sequence 
     * before reaching a set width.
     * 
     * @param metrics   Font metrics used for layout..
     * @param chars     Input array of chars.
     * @param off       Offset into array of chars.
     * @param len       Number of chars in input.
     * @param maxWidth  Maximum width for layout.
     * @param outWidth  <code>outWidth[0]</code> will hold the exact width
     *                  of the fitted characters on return. May be <code>null</code>
     *                  May be <code>null</code>.
     *                  
     * @return The number of characters that fit within the specified width.
     */
    public static int findWidth( FontMetrics metrics, 
                                 char[] chars, 
                                 int off, 
                                 int len, 
                                 float maxWidth, 
                                 float[] outWidth ) 
    {
        int i = 0;
        int lineWidth = 0;
        for( ; i < len; i++ ) {
            int charWidth = metrics.charWidth( chars[i + off] );
            lineWidth += charWidth;
            if( lineWidth > maxWidth ) {
                lineWidth -= charWidth;
                break;
            }
        }
        
        if( outWidth != null ) {
            outWidth[0] = lineWidth;
        }
        
        return i;
        
        /*
        // Version to use if kerning tables become available.
        if( len == 0 ) {
            if( outWidth != null ) { 
                outWidth[0] = 0f;
            }
            return 0;
        }
        
        float width = metrics.charWidth( chars[off] );
        if( width > maxWidth ) {
            if( outWidth != null ) {
                outWidth[0] = 0f;
            }
            return 0;
        }
        
        float prevAdvance = width;
        
        for( int i = 1; i < len; i++ ) {
            float advance = metrics.charsWidth( chars, off + i - 1, 2 );
            prevAdvance   = advance - prevAdvance;
            width += prevAdvance;
            
            if( width > maxWidth ) {
                if( outWidth != null ) {
                    outWidth[0] = width - prevAdvance;
                }
                return i;
            }
        }
        
        if( outWidth != null ) {
            outWidth[0] = width;
        }
        return len;
        */
    }
    
    
    /**
     * Determines how many characters may be laid in sequence 
     * before reaching a set width.
     * 
     * @param metrics   Font metrics used for layout..
     * @param seq       Input CharSequence to layout.
     * @param off       Offset into seq where input starts.
     * @param len       Number of chars in input.
     * @param maxWidth  Maximum width for layout.
     * @param outWidth  <code>outWidth[0]</code> will hold the exact width
     *                  the exact width of the fitted characters on return.
     *                  May be <code>null</code>.
     *                  
     * @return The number of characters that fit within the specified width.
     */
    public static int findWidth( FontMetrics metrics, 
                                 CharSequence seq, 
                                 int off, 
                                 int len, 
                                 float maxWidth, 
                                 float[] outWidth )
    {
        int i = 0;
        int lineWidth = 0;
        for( ; i < len; i++ ) {
            int charWidth = metrics.charWidth( seq.charAt( i + off ) );
            lineWidth += charWidth;
            if( lineWidth > maxWidth ) {
                lineWidth -= charWidth;
                break;
            }
        }
        
        if( outWidth != null ) {
            outWidth[0] = lineWidth;
        }
        
        return i;
        
        /*
        // Version to use if kerning tables become available.
        if( len == 0 ) {
            if( outWidth != null ) {
                outWidth[0] = 0f;
            }
            return 0;
        }
        
        char[] arr = { 0, seq.charAt( off ) };
        float width = metrics.charsWidth( arr, 1, 1 );
        if( width > maxWidth ) {
            if( outWidth[0] != 0f ) {
                outWidth[0] = 0f;
            }
            return 0;
        }
        
        float prevAdvance = width;
        
        for( int i = 1; i < len; i++ ) {
            arr[0] = arr[1];
            arr[1] = seq.charAt( i + off );
            float advance = metrics.charsWidth( arr, 0, 2 );
            prevAdvance   = advance - prevAdvance;
            width += prevAdvance;
            
            if( width > maxWidth ) {
                if( outWidth != null ) {
                    outWidth[0] = width - prevAdvance;
                }
                return i;
            }
        }
        
        if( outWidth != null ) {
            outWidth[0] = width;
        }
        return len;
        */
    }
    
    
    /**
     * Computes the right-edges of every character in a string. This is much
     * more efficient than calling charsWidth() on many substrings to find
     * intermediate positions of characters. Note that the first position is
     * implied to be 0, so on return, the output array holds only the right edge
     * of each glyph.
     * 
     * @param metrics
     * @param seq
     * @param out         Holds the right position of each character in the input
     *                    sequence. <code>out.length >= seq.length()</code>
     * @param outOffset   Offset into <code>out</code> where output will be written.
     */
    public static void charPositions( FontMetrics metrics, CharSequence seq, float[] out, int outOffset ) {
        charPositions( metrics, seq, 0, seq.length(), out, outOffset );
    }


    /**
     * Computes the right-edges of every character in a string. This is much
     * more efficient than calling charsWidth() on many substrings to find
     * intermediate positions of characters. Note that the first position is
     * implied to be 0, so on return, the output array holds only the right edge
     * of each glyph.
     * 
     * @param metrics
     * @param seq
     * @param off         Offset into sequence where input begins.
     * @param len         Length of input to use.
     * @param out         Holds the right position of each character in the input
     *                    sequence. <code>out.length >= seq.length()</code>
     * @param outOffset   Offset into <code>out</code> where output will be written.
     */
    public static void charPositions( FontMetrics metrics,
                                      CharSequence seq,
                                      int off,
                                      int len,
                                      float[] out,
                                      int outOffset ) 
    {
        int pos = 0;
        for( int i = 0; i < len; i++ ) {
            pos += metrics.charWidth( seq.charAt( i + off ) );
            out[ i + outOffset ] = pos;
        }
        
        /*
        // Only useful when kerning tables become available.
        if( len <= 0 ) {
            return;
        }
        char[] arr = { seq.charAt( off ), 0 };
        float width = metrics.charWidth( arr[0] );
        float prevAdvance = width;
        out[outOffset++] = width;

        for( int i = 1; i < len; i++ ) {
            arr[1] = seq.charAt( off + i );
            float advance = metrics.charsWidth( arr, 0, 2 );
            prevAdvance = advance - prevAdvance;
            width += prevAdvance;
            arr[0] = arr[1];

            out[outOffset++] = width;
        }
        */
    }


    /**
     * Computes the right-edges of every character in a string. This is much
     * more efficient than calling charsWidth() on many substrings to find
     * intermediate positions of characters. Note that the first position is
     * implied to be 0, so on return, the output array holds only the right edge
     * of each glyph.
     * 
     * @param metrics
     * @param chars
     * @param off         Offset into chars where input begins.
     * @param len         Length of input to use.
     * @param out         Holds the right position of each character in the input
     *                    sequence. <code>out.length >= seq.length()</code>
     * @param outOffset   Offset into <code>out</code> where output will be written.
     */
    public static void charPositions( FontMetrics metrics, char[] chars, int off, int len, float[] out, int outOffset ) {
        int width = 0;
        for( int i = 0; i < len; i++ ) {
            width += metrics.charWidth( chars[ i + off ] );
            out[ i + outOffset ] = width;
        }
        
        /*
        // Not useful until kerning tables are available.
        if( len <= 0 ) {
            return;
        }

        float width = metrics.charWidth( chars[off] );
        float prevAdvance = width;
        out[outOffset++] = width;

        for( int i = 1; i < len; i++ ) {
            float advance = metrics.charsWidth( chars, off + i - 1, 2 );
            prevAdvance = advance - prevAdvance;
            width += prevAdvance;

            out[outOffset++] = width;
        }
        */
    }
    
    
    /**
     * Splits a single string into multiple lines to fit into defined space. Splits are made
     * at word boundaries.
     * 
     * @param text       Input text to split.
     * @param metrics    Metrics being used to render font.
     * @param lineWidth  Width of line into which the text must fit.
     * 
     * @return A lost of Strings, with each string representing a single line.
     */
    public static List<String> splitAtWordBoundaries( String text, FontMetrics metrics, float lineWidth ) {
        final float spaceWidth = metrics.charWidth( ' ' );
        final StringBuilder s  = new StringBuilder();
        Matcher m = WORD_PAT.matcher( text );
        
        boolean newLine = true;
        float pos       = 0f;

        List<String> ret = new ArrayList<String>( 5 );
        
        while( m.find() ) {
            float advance = FontUtil.charsWidth( metrics, m.group( 0 ) );

            if( !newLine && pos + spaceWidth + advance > lineWidth ) {
                ret.add( s.toString() );
                s.setLength( 0 );
                pos = 0;
                newLine = true;
            }

            if( newLine ) {
                newLine = false;
            } else {
                s.append( ' ' );
                pos += spaceWidth;
            }

            s.append( m.group( 0 ) );
            pos += advance;
        }

        if( s.length() > 0 ) {
            ret.add( s.toString() );
        }

        return ret;
    }

}
