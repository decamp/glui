package cogmac.glui.event;

import java.awt.event.FocusEvent;

import cogmac.glui.GComponent;

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
