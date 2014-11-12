package bits.draw3d.text;

class GlyphMaps {

    static GlyphMap newGlyphMap() {
        return new FlatGlyphMap( CharSet.DEFAULT );
    }
    
    
    static GlyphMap newGlyphMap( CharSet chars ) {
        if( chars == null ) {
            chars = CharSet.DEFAULT;
        }
        return new FlatGlyphMap( chars );
    }
    
}
