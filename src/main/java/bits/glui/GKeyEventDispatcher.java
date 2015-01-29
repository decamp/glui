package bits.glui;

import bits.glui.event.GKeyEvent;

/**
 * Equivalent to {@link java.awt.KeyEventDispatcher}.
 *
 * Cooperates with current KeyboardboardFocusManager to target and dispatch event.
 *
 * @see java.awt.KeyEventDispatcher
 */
public interface GKeyEventDispatcher {
    public boolean dispatchKeyEvent( GKeyEvent e );
}
