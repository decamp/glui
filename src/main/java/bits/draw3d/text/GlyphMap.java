package bits.draw3d.text;

interface GlyphMap {
    
    public Glyph get( char c );
    
    public Glyph put( char c, 
                      float advance,
                      int x0,
                      int y0,
                      float s0,
                      float t0,
                      int x1,
                      int y1,
                      float s1,
                      float t1 );
    
    /**
     * After constructing the table through a series of
     * {@code put()} commands, {@code optimize()}
     * must be called to prepare the table for use.
     */
    public void optimize();
    
    /**
     * @return glyph to use for unrecognized characters.
     */
    public Glyph unknownGlyph();
    
    public CharSequence chars();
    public char getChar( int index );
}
