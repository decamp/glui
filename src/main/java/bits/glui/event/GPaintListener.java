package bits.glui.event;

import java.util.EventListener;
import bits.glui.GGraphics;


/**
 * @author decamp
 */
public interface GPaintListener extends EventListener {
    public void paint( GGraphics g );
}
