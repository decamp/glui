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
public interface GMouseWheelListener extends EventListener {
    public void mouseWheelMoved(GMouseWheelEvent e);
}
