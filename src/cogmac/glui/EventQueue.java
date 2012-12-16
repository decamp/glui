package cogmac.glui;


class EventQueue {

    private final int PRIORITY_REPAINT = 0;
    private final int PRIORITY_LAYOUT  = 1;
    private final int PRIORITY_OTHER   = 2;
    private final int PRIORITY_MAX     = 3;
    
    private final Queue[] mQueues = new Queue[PRIORITY_MAX];
    
    private Item mItemPool    = null;
    private int mItemPoolSize = 0;
    
    private GComponent mRoot;
    
    
    
    synchronized void queuePaint( GComponent source ) {
        Queue q = mQueues[PRIORITY_REPAINT];
        
        // Currently, all paint requests repaint entire screen.
        // So there's no point in multiple repaint requests.
        if( q.mHead != null ) 
            return;
        
        Item item = getItem();
        item.mSource    = source;
        item.mProcessor = PROCESS_PAINT;
        q.offer( item );
    }
    
    synchronized void queueApplyLayout( GComponent source ) {
        Queue q = mQueues[PRIORITY_LAYOUT];
        Item item = q.mHead;
        
        if( item != null ) {
            if( item.mSource == source ) {
                return;
            } else {
                item.mSource = mRoot; 
            }
        }
        
        item = getItem();
        item.mSource = source;
        item.mProcessor = PROCESS_APPLY_LAYOUT;
        mQueues[PRIORITY_OTHER].offer( item );
        
    }
    
    synchronized void queueRequestFocus( GComponent source ) {
        Item item = getItem();
        item.mSource    = source;
        item.mProcessor = PROCESS_REQUEST_FOCUS;
        mQueues[PRIORITY_OTHER].offer( item );
    }
    
    synchronized void queueTransferFocusBackward( GComponent source ) {
        Item item = getItem();
        item.mSource    = source;
        item.mProcessor = PROCESS_TRANSFER_FOCUS_FORWARD;
        mQueues[PRIORITY_OTHER].offer( item );
    }
    
    synchronized void queueTransferFocusForward( GComponent source ) {
        Item item = getItem();
        item.mSource    = source;
        item.mProcessor = PROCESS_TRANSFER_FOCUS_BACKWARD;
        mQueues[PRIORITY_OTHER].offer( item );
    }
    
    synchronized void queuePushInputRoot( GComponent source ) {
        Item item = getItem();
        item.mSource    = source;
        item.mProcessor = PROCESS_PUSH_INPUT_ROOT;
        mQueues[PRIORITY_OTHER].offer( item );
    }
    
    synchronized void queuePopInputRoot( GComponent source ) {
        Item item = getItem();
        item.mSource    = source;
        item.mProcessor = PROCESS_POP_INPUT_ROOT;
        mQueues[PRIORITY_OTHER].offer( item );
    }
    
    
    

    private Item getItem() {
        if( mItemPool == null ) {
            return new Item();
        }
        
        Item ret = mItemPool;
        mItemPool = mItemPool.mNext;
        mItemPoolSize--;
        ret.mNext = null;
        return ret;
    }
    
    
    
    private static final class Item {
        
        Item mNext;
        
        GComponent mSource;
        String mStringParam;
        Object mObject1;
        Object mObject2;
        int mInt1;
        int mInt2;
        Processor mProcessor;
        
        public void clear() {
            mNext        = null;
            mSource      = null;
            mStringParam = null;
            mObject1     = null;
            mObject2     = null;
        }
        
    }
    
    
    private static final class Queue {
        Item mHead = null;
        Item mTail = null;
        
        void offer( Item item ) {
            if( mHead == null ) {
                mHead = mTail = item;
                return;
            }
            
            mTail.mNext = item;
            mTail = item;
        }
        
    }


    private static interface Processor {
        void process( EventDispatcher dispatch, Item item );
    }

    
    private static final Processor PROCESS_PAINT = new Processor() {
        public void process( EventDispatcher dispatch, Item item ) {
            dispatch.repaint( item.mSource );
        }
    };
    
    private static final Processor PROCESS_APPLY_LAYOUT = new Processor() {
        public void process( EventDispatcher dispatch, Item item ) {
            dispatch.applyLayout( item.mSource );
        }
    };
    
    private static final Processor PROCESS_REQUEST_FOCUS = new Processor() {
        public void process( EventDispatcher dispatch, Item item ) {
            dispatch.requestFocus( item.mSource );
        }
    };
    
    private static final Processor PROCESS_TRANSFER_FOCUS_BACKWARD = new Processor() {
        public void process( EventDispatcher dispatch, Item item ) {
            dispatch.transferFocusBackward( item.mSource );
        }
    };
    
    private static final Processor PROCESS_TRANSFER_FOCUS_FORWARD = new Processor() {
        public void process( EventDispatcher dispatch, Item item ) {
            dispatch.transferFocusForward( item.mSource );
        }
    };
    private static final Processor PROCESS_PUSH_INPUT_ROOT = new Processor() {
        public void process( EventDispatcher dispatch, Item item ) {
            dispatch.pushInputRoot( item.mSource );
        }
    };
    private static final Processor PROCESS_POP_INPUT_ROOT = new Processor() {
        public void process( EventDispatcher dispatch, Item item ) {
            dispatch.popInputRoot( item.mSource );
        }
    };

      
}