package bits.draw3d;

import bits.glui.GGraphics;


/**
 * @author Philip DeCamp
// */
public interface DrawResource {
    public void init( GGraphics g );
    public void dispose( GGraphics g );
}
