package cogmac.glui.text;

/**
 * Holds a set of characters and provides some basic information on the 
 * bounds of that set.
 * 
 * @author decamp
 */
public class CharSet {
    
    public static final CharSet DEFAULT = new CharSet( "abcdefghijklmnopqrstuvwxyz" +
                                                       "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                                                       "0123456789" +
                                                       "`~!@#$%^&*()-_=+" +
                                                       "[]{}\\|" +
                                                       ";:'\"" +
                                                       ",<.>/? " );    
    
    private final CharSequence mChars;
    private final char mMin;
    private final char mMax;
    private final int mUnknown;
    
    
    public CharSet( CharSequence chars ) {
        this( chars, -1 );
    }
    
    
    public CharSet( CharSequence chars, int unknown ) {
        final int len = chars.length();
        if( len == 0 ) {
            throw new IllegalArgumentException( "Cannot have empty character set." );
        }
        
        char min = Character.MAX_VALUE;
        char max = Character.MIN_VALUE;
        
        for( int i = 0; i < len; i++ ) {
            char c = chars.charAt( i );
            if( c < min ) {
                min = c;
            }
            if( c > max ) {
                max = c;
            }
            if( unknown < 0 && c == ' ' ) {
                unknown = i;
            }
        }
        
        mChars   = chars;
        mMin     = min;
        mMax     = max;
        mUnknown = unknown >= 0 ? unknown : 0;
    }
    
    
    
    public CharSequence chars() {
        return mChars;
    }
    
    
    public char min() {
        return mMin;
    }
    
    
    public char max() {
        return mMax;
    }
    
    /**
     * @return index of character to use as substitute for unrecognized characters.
     */
    public int unknown() {
        return mUnknown;
    }
    

}
