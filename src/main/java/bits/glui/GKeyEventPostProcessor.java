package bits.glui;

import bits.glui.event.GKeyEvent;


/**
 * Equivalent to {@link java.awt.KeyEventDispatcher}.
 *
 * Cooperates with current KeyboardboardFocusManager to resolve unconsumed key events.
 *
 * @see java.awt.KeyEventDispatcher
 */
public interface GKeyEventPostProcessor {
    public boolean postProcessKeyEvent( GKeyEvent e );
}
