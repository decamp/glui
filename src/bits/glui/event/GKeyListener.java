package bits.glui.event;

import java.util.EventListener;

/**
 * @author decamp
 */
public interface GKeyListener extends EventListener {
    public void keyPressed(GKeyEvent e);
    public void keyReleased(GKeyEvent e);
    public void keyTyped(GKeyEvent e);
}
