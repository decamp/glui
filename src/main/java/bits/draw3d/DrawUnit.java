package bits.draw3d;

import bits.glui.GGraphics;


/**
 * @author Philip DeCamp
 */
public interface DrawUnit extends DrawResource {
    public void bind( GGraphics g );
    public void unbind( GGraphics g );
}
