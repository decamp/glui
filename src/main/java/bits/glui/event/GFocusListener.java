package bits.glui.event;

import java.util.EventListener;

/**
 * @author decamp
 */
public interface GFocusListener extends EventListener {
    public void focusGained(GFocusEvent e);
    public void focusLost(GFocusEvent e);
}
