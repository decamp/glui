package cogmac.glui.event;

import java.util.EventListener;

/**
 * @author decamp
 */
public interface GMouseMotionListener extends EventListener {
    public void mouseMoved(GMouseEvent e);
    public void mouseDragged(GMouseEvent e);
}
