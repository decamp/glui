package cogmac.glui.event;

import java.util.EventListener;

import cogmac.glui.GGraphics;


/**
 * @author decamp
 */
public interface GPaintListener extends EventListener {
    public void paint(GGraphics g);
}
