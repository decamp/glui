package cogmac.glui.event;

import java.util.EventListener;

/**
 * @author decamp
 */
public interface GComponentListener extends EventListener {
    public void componentShown(GComponentEvent e);
    public void componentHidden(GComponentEvent e);
    public void componentMoved(GComponentEvent e);
    public void componentResized(GComponentEvent e);
}
