package bits.draw3d.text;

class FlatGlyphMap implements GlyphMap {
    
    private final CharSequence mChars;
    private final char mMin;
    private final char mMax;
    private final int mUnknown;
    
    private final Glyph[] mGlyphs;
    
    
    FlatGlyphMap( CharSet chars ) {
        mChars   = chars.chars();
        mMin     = chars.min();
        mMax     = chars.max();
        mUnknown = chars.charAt( chars.unknown() ) - mMin;
        mGlyphs  = new Glyph[ mMax - mMin + 1 ];
    }
    
    
    
    public Glyph get( char c ) {
        try {
            return mGlyphs[ c - mMin ];
        } catch( ArrayIndexOutOfBoundsException ex ) {
            return mGlyphs[ mUnknown ];
        }
    }
    
    public Glyph put( char c,
                      float advance,
                      int x0,
                      int y0,
                      float s0,
                      float t0,
                      int x1,
                      int y1,
                      float s1,
                      float t1 )
    {
        Glyph g = new Glyph( c, advance, x0, y0, s0, t0, x1, y1, s1, t1 );
        mGlyphs[ c - mMin ] = g;
        return g;
    }
    
    public void optimize() {
        Glyph g = mGlyphs[mUnknown];
        
        for( int i = 0; i < mGlyphs.length; i++ ) {
            if( mGlyphs[i] == null ) {
                mGlyphs[i] = g;
            }
        }
    }
    
    
    public Glyph unknownGlyph() { 
        return mGlyphs[ mUnknown ];
    }
    
    
    public CharSequence chars() {
        return mChars;
    }
    
    public char getChar( int index ) {
        return mChars.charAt( index );
    }
    
    
    public String toString() {
        return mChars.toString();
    }

}
