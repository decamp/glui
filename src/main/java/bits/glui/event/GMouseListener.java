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
public interface GMouseListener extends EventListener {
    public void mouseEntered(GMouseEvent e);
    public void mouseExited(GMouseEvent e);
    public void mousePressed(GMouseEvent e);
    public void mouseReleased(GMouseEvent e);
    public void mouseClicked(GMouseEvent e);
}
