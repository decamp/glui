package cogmac.glui.text;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.regex.*;
import java.util.List;
import java.util.*;


/**
 * @author decamp
 */
public class FontUtil {

    private static final BufferedImage METRIC_IMAGE = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );
    private static final Pattern WORD_PAT           = Pattern.compile( "\\S++" );
    
    

    public static FontMetrics metrics( Font font ) {
        return METRIC_IMAGE.getGraphics().getFontMetrics( font );
    }
    
    
    public static FontRenderContext renderContext() {
        return ((Graphics2D)METRIC_IMAGE.getGraphics()).getFontRenderContext();
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
        if( len == 0 )
            return 0;

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
    }


    public static float charsWidth( FontMetrics metrics, char[] chars, int off, int len ) {
        if( len == 0 )
            return 0;

        float width = metrics.charWidth( chars[off] );
        float prevAdvance = width;

        for( int i = 1; i < len; i++ ) {
            float advance = metrics.charsWidth( chars, off + i - 1, 2 );
            prevAdvance   = advance - prevAdvance;
            width += prevAdvance;
        }

        return width;
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
        if( len <= 0 )
            return;

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
        if( len <= 0 )
            return;

        float width = metrics.charWidth( chars[off] );
        float prevAdvance = width;
        out[outOffset++] = width;

        for( int i = 1; i < len; i++ ) {
            float advance = metrics.charsWidth( chars, off + i - 1, 2 );
            prevAdvance = advance - prevAdvance;
            width += prevAdvance;

            out[outOffset++] = width;
        }
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
        final StringBuilder s = new StringBuilder();
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
