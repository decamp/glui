package cogmac.glui.event;

/**
 * @author decamp
 */
public class GEvent {

    public static final int COMPONENT_EVENT_MASK = 0x00000003;
    
    
    private final Object mSource;
    private final int mId;
    private boolean mConsumed = false;

    
    public GEvent(Object source, int id) {
        mSource = source;
        mId = id;
    }
    

    
    public Object source() {
        return mSource;
    }
    
    public int id() {
        return mId;
    }

    
    protected void consume() {
        mConsumed = true;
    }
    
    protected boolean isConsumed() {
        return mConsumed;
    }

}
