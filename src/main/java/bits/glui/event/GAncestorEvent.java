/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui.event;

import java.awt.event.HierarchyEvent;

import bits.glui.GComponent;


public class GAncestorEvent extends GEvent {

    public static final int ANCESTOR_FIRST    = HierarchyEvent.HIERARCHY_FIRST;
    public static final int ANCESTOR_LAST     = HierarchyEvent.HIERARCHY_LAST;
    public static final int ANCESTOR_CHANGED  = HierarchyEvent.PARENT_CHANGED;
    public static final int ANCESTOR_MOVED    = HierarchyEvent.ANCESTOR_MOVED;
    public static final int ANCESTOR_RESIZED  = HierarchyEvent.ANCESTOR_RESIZED;
    
    
    private final GComponent mAncestor;
    
    
    public GAncestorEvent( GComponent source, int id, GComponent ancestor ) {
        super( source, id );
        mAncestor = ancestor;
    }
    
    
    @Override
    public GComponent source() {
        return (GComponent)super.source();
    }
    
    public GComponent ancestor() {
        return mAncestor;
    }
        
}
