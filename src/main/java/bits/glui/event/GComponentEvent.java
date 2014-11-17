/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui.event;

import java.awt.event.ComponentEvent;

import bits.glui.GComponent;


/**
 * @author decamp
 */
public class GComponentEvent extends GEvent {

    public static final int COMPONENT_FIRST   = ComponentEvent.COMPONENT_FIRST;
    public static final int COMPONENT_LAST    = ComponentEvent.COMPONENT_LAST;
    public static final int COMPONENT_MOVED   = ComponentEvent.COMPONENT_MOVED;
    public static final int COMPONENT_RESIZED = ComponentEvent.COMPONENT_RESIZED;
    public static final int COMPONENT_SHOWN   = ComponentEvent.COMPONENT_SHOWN;
    public static final int COMPONENT_HIDDEN  = ComponentEvent.COMPONENT_HIDDEN;
    
    
    public GComponentEvent( GComponent source, int id ) {
        super( source, id );
    }

    

    @Override
    public GComponent source() {
        return (GComponent)super.source();
    }
    
}
