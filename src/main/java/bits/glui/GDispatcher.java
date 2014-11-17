/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui;

import bits.glui.event.*;

public interface GDispatcher {
    public void fireLayout( GComponent source );
    public void firePaint( GComponent source );
    public void fireRequestFocus( GComponent source );
    public void fireTransferFocusBackward( GComponent source );
    public void fireTransferFocusForward( GComponent source );
    public void firePushInputRoot( GComponent source );
    public void firePopInputRoot( GComponent source );
    public void fireComponentEvent( GComponentEvent event );
    public void fireAncestorEvent( GAncestorEvent event );
    
    public void firePropertyChange( GComponent source, String prop, Object oldValue, Object newValue );

    public void fireRunnable( Runnable run );

    public boolean ignoreRepaints();
    public void ignoreRepaints( boolean ignoreRepaints );

}
