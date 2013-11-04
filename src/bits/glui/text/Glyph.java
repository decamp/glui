package bits.glui.text;

import java.awt.FontMetrics;


/**
 * Manages the char/glyph mappings.
 * 
 * @author decamp
 */
final class Glyph {

    public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz" +
                                            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                                            "0123456789" +
                                            "`~!@#$%^&*()-_=+" +
                                            "[]{}\\|" +
                                            ";:'\"" +
                                            ",<.>/? ";
    
    public static final char MIN_CHAR;
    public static final char MAX_CHAR;
    public static final int  BLANK_GLYPH;
    
    
    static {
        int len  = CHARACTERS.length();
        char min = 'a';
        char max = 'a';

        for( int i = 0; i < len; i++ ) {
            char c = CHARACTERS.charAt( i );
            
            if( c < min ) {
                min = c;
            }

            if( c > max ) {
                max = c;
            }
        }

        MIN_CHAR    = min;
        MAX_CHAR    = (char)( max + 1 );
        BLANK_GLYPH = ' ' - min;
    }
    
    
    
    static Glyph[] newTable() {
        return new Glyph[MAX_CHAR - MIN_CHAR];
    }
    
    
    static void tableFillNullsWithBlank( Glyph[] table ) {
        for( int i = 0; i < table.length; i++ ) {
            if( table[i] == null ) {
                table[i] = table[BLANK_GLYPH];
            }
        }
    }
    
    
    static Glyph tableGet( Glyph[] table, char c ) {
        if( c < MIN_CHAR || c >= MAX_CHAR ) {
            return table[BLANK_GLYPH];
        }

        return table[c - MIN_CHAR];
    }
    
    
    static void tablePut( Glyph[] table, char c, Glyph glyph ) {
        if( c < MIN_CHAR || c >= MAX_CHAR )
            return;

        table[c - MIN_CHAR] = glyph;
    }
    
    
    static float[] computeKerningTable( FontMetrics metrics, char c ) {
        float[] ret = new float[MAX_CHAR - MIN_CHAR];
        char[] work = { c, '\0' };

        float advance0 = metrics.charWidth( c );

        for( int i = 0; i < CHARACTERS.length(); i++ ) {
            char c1        = CHARACTERS.charAt( i );
            float advance1 = metrics.charWidth( c1 );

            work[1]    = c1;
            float kern = metrics.charsWidth( work, 0, 2 ) - advance0 - advance1;
            
            ret[c1 - MIN_CHAR] = kern;
        }

        return ret;
    }
    
    
    final char    mChar;
    final float   mAdvance;
    final float[] mKernTable;

    final float   mX0;
    final float   mY0;
    final float   mX1;
    final float   mY1;

    final float   mU0;
    final float   mV0;
    final float   mU1;
    final float   mV1;


    Glyph( char c,
           float advance,
           float[] kernTable,
           float x0,
           float y0,
           float x1,
           float y1,
           float u0,
           float v0,
           float u1,
           float v1 ) 
    {
        mChar      = c;
        mAdvance   = advance;
        mKernTable = kernTable;

        mU0 = u0;
        mV0 = v0;
        mU1 = u1;
        mV1 = v1;
        mX0 = x0;
        mY0 = y0;
        mX1 = x1;
        mY1 = y1;
    }
    
    
    float getKern( char c ) {
        try {
            return mKernTable[c - MIN_CHAR];
        } catch( ArrayIndexOutOfBoundsException ex ) {
            //Bad practice here, but I don't think this ever happens.
            return 0f;
        }
    }
    
}
