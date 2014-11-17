/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

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
