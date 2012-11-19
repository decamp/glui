package cogmac.glui.event;

import java.util.EventListener;

/**
 * @author decamp
 */
public interface GMouseListener extends EventListener {
    public void mouseEntered(GMouseEvent e);
    public void mouseExited(GMouseEvent e);
    public void mousePressed(GMouseEvent e);
    public void mouseReleased(GMouseEvent e);
    public void mouseClicked(GMouseEvent e);
}
