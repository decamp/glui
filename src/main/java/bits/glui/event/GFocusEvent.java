/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui.event;

import java.awt.event.FocusEvent;

import bits.glui.GComponent;


/**
 * @author decamp
 */
public class GFocusEvent extends GEvent {
    
    public static final int FOCUS_FIRST  = FocusEvent.FOCUS_FIRST;
    public static final int FOCUS_GAINED = FocusEvent.FOCUS_GAINED;
    public static final int FOCUS_LAST   = FocusEvent.FOCUS_LAST;
    public static final int FOCUS_LOST   = FocusEvent.FOCUS_LAST;
    
    private final boolean mIsTemp;
    private final GComponent mOpposite;
    
    
    public GFocusEvent(GComponent source, int id) {
        super(source, id);
        mIsTemp = false;
        mOpposite = null;
    }

    
    public GFocusEvent(GComponent source, int id, boolean temporary) {
        super(source, id);
        mIsTemp = temporary;
        mOpposite = null;
    }
    
    
    public GFocusEvent(GComponent source, int id, boolean temporary, GComponent opposite) {
        super(source, id);
        mIsTemp = temporary;
        mOpposite = opposite;
    }
    
    
    
    public GComponent source() {
        return (GComponent)super.source();
    }
    
    
    public boolean isTemporary() {
        return mIsTemp;
    }
    
    
    public GComponent opposite() {
        return mOpposite;
    }

}
