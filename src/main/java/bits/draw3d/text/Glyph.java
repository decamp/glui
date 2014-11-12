package bits.draw3d.text;

class Glyph {
    
    final char mChar;
    final float mAdvance;
    
    // Lower-left corner.
    final int mX0;
    final int mY0;
    final float mS0;
    final float mT0;
    
    // Upper-right corner.
    final int mX1;
    final int mY1;
    final float mS1;
    final float mT1;
    
    
    Glyph( char c,
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
        mChar = c;
        mAdvance = advance;
        mX0 = x0;
        mY0 = y0;
        mS0 = s0;
        mT0 = t0;
        mX1 = x1;
        mY1 = y1;
        mS1 = s1;
        mT1 = t1;
    }
    

}
