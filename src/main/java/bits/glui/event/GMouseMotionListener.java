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
public interface GMouseMotionListener extends EventListener {
    public void mouseMoved(GMouseEvent e);
    public void mouseDragged(GMouseEvent e);
}
