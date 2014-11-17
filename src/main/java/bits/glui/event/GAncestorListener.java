/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui.event;

import java.util.EventListener;

public interface GAncestorListener extends EventListener {
    public void ancestorChanged( GAncestorEvent e );
    public void ancestorMoved( GAncestorEvent e );
    public void ancestorResized( GAncestorEvent e );
}
