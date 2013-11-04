package bits.glui.event;

import java.awt.event.MouseWheelEvent;

import bits.glui.GComponent;


/**
 * @author decamp
 */
public class GMouseWheelEvent extends GMouseEvent {
    
    
    public static final int WHEEL_BLOCK_SCROLL = MouseWheelEvent.WHEEL_BLOCK_SCROLL;
    public static final int WHEEL_UNIT_SCROLL  = MouseWheelEvent.WHEEL_UNIT_SCROLL;
    
    
    
    private final int mScrollType;
    private final int mScrollAmount;
    private final int mWheelRotation;

    
    public GMouseWheelEvent( GComponent source,
                             int id,
                             long timestampMicros,
                             int modifiers,
                             int x,
                             int y,
                             int clickCount,
                             boolean popupTrigger,
                             int scrollType,
                             int scrollAmount,
                             int wheelRotation )
    {
        super(source, id, timestampMicros, modifiers, x, y, clickCount, popupTrigger, NOBUTTON);
        mScrollType = scrollType;
        mScrollAmount = scrollAmount;
        mWheelRotation = wheelRotation;
    }
    
    
    
    public int getScrollType() {
        return mScrollType;
    }
    
    
    public int getScrollAmount() {
        return mScrollAmount;
    }
    
    
    public int getWheelRotation() {
        return mWheelRotation;
    }
    
    
    public int getUnitsToScroll() {
        return mScrollAmount * mWheelRotation;
    }
    

}
