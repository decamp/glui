package bits.glui;

import bits.glui.event.*;


class EventQueue implements GDispatcher {
    
    private static final int PRIORITY_REPAINT = 0;
    private static final int PRIORITY_LAYOUT  = 1;
    private static final int PRIORITY_OTHER   = 2;
    private static final int PRIORITY_MAX     = 3;
    
    private final GComponent mRoot;
    
    private final Queue[] mQueues = new Queue[PRIORITY_MAX];
    
    private Item mItemPool    = null;
    private int mItemPoolSize = 0;
    private boolean mIgnoreRepaints = false;
    

    EventQueue( GComponent root ) {
        mRoot = root;
        
        for( int i = 0; i < mQueues.length; i++ ) {
            mQueues[i] = new Queue();
        }
    }
    
    
    public boolean ignoreRepaints() {
        return mIgnoreRepaints;
    }


    public void ignoreRepaints( boolean ignoreRepaints ) {
        mIgnoreRepaints = ignoreRepaints;
    }
    
    
    public synchronized void firePaint( GComponent source ) {
        if( mIgnoreRepaints )
            return;
        
        Queue q = mQueues[PRIORITY_REPAINT];
        
        // Currently, all paint requests repaint entire screen.
        // So there's no point in multiple repaint requests.
        if( q.mHead != null ) 
            return;
        
        Item item = getItem();
        item.mSource = source;
        item.mCall   = PROCESS_PAINT;
        q.offer( item );
    }


    public synchronized void fireLayout( GComponent source ) {
        Queue q = mQueues[PRIORITY_LAYOUT];
        Item item = q.mHead;
        
        if( item != null ) {
            if( item.mSource != source ) {
                item.mSource = mRoot;
            }
            
            return;
        }
        
        item = getItem();
        item.mSource = source;
        item.mCall = PROCESS_APPLY_LAYOUT;
        q.offer( item );
    }


    public synchronized void fireRequestFocus( GComponent source ) {
        Item item = getItem();
        item.mSource    = source;
        item.mCall = PROCESS_REQUEST_FOCUS;
        mQueues[PRIORITY_OTHER].offer( item );
    }


    public synchronized void fireTransferFocusBackward( GComponent source ) {
        Item item = getItem();
        item.mSource    = source;
        item.mCall = PROCESS_TRANSFER_FOCUS_FORWARD;
        mQueues[PRIORITY_OTHER].offer( item );
    }


    public synchronized void fireTransferFocusForward( GComponent source ) {
        Item item = getItem();
        item.mSource    = source;
        item.mCall = PROCESS_TRANSFER_FOCUS_BACKWARD;
        mQueues[PRIORITY_OTHER].offer( item );
    }


    public synchronized void firePushInputRoot( GComponent source ) {
        Item item = getItem();
        item.mSource    = source;
        item.mCall = PROCESS_PUSH_INPUT_ROOT;
        mQueues[PRIORITY_OTHER].offer( item );
    }


    public synchronized void firePopInputRoot( GComponent source ) {
        Item item = getItem();
        item.mSource    = source;
        item.mCall = PROCESS_POP_INPUT_ROOT;
        mQueues[PRIORITY_OTHER].offer( item );
    }


    public synchronized void fireComponentEvent( GComponentEvent event ) {
        Item item = getItem();
        item.mObject1 = event;
        item.mCall = PROCESS_COMPONENT_EVENT;
        mQueues[PRIORITY_OTHER].offer( item );
    }


    public synchronized void fireAncestorEvent( GAncestorEvent event ) {
        Item item = getItem();
        item.mObject1 = event;
        item.mCall = PROCESS_ANCESTOR_EVENT;
        mQueues[PRIORITY_OTHER].offer( item );
    }


    public synchronized void firePropertyChange( GComponent source, String prop, Object oldValue, Object newValue ) {
        Item item = getItem();
        item.mSource  = source;
        item.mString  = prop;
        item.mObject1 = oldValue;
        item.mObject2 = newValue;
        item.mCall    = PROCESS_PROPERTY_CHANGE;
        mQueues[PRIORITY_OTHER].offer( item );
    }


    public synchronized void fireRunnable( Runnable r ) {
        Item item = getItem();
        item.mObject1 = r;
        item.mCall    = PROCESS_RUN;
        mQueues[PRIORITY_OTHER].offer( item );
    }

    
    boolean processAllEvents( EventProcessor processor ) {
        Item item   = null;
        boolean ret = false;
        
        while( true ) {
            synchronized( this ) {
                // Return item first so there's only one sync block.
                if( item != null ) {
                    offerItem ( item );
                    item = null;
                }
                
                for( int n = PRIORITY_MAX - 1; item == null && n >= 0; n-- ) {
                    item = mQueues[n].remove();
                }
            }
            
            if( item == null ) {
                return ret;
            }
            
            ret = true;
            
            try {
                item.mCall.call( processor, item );
            } catch( Exception ex ) {
                ex.printStackTrace();
            }
        }
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
    
    
    private void offerItem( Item item ) {
        if( mItemPoolSize == 0 ) {
            item.clear();
            mItemPool = item;
            mItemPoolSize = 1;
        } else if( mItemPoolSize < 128 ) {
            item.clear();
            item.mNext = mItemPool;
            mItemPoolSize++;
        }
    }
    
    
    
    private static final class Item {
        
        Item mNext;
        
        Call mCall;
        GComponent mSource;
        String mString;
        Object mObject1;
        Object mObject2;
        
        
        public void clear() {
            mNext    = null;
            mSource  = null;
            mString  = null;
            mObject1 = null;
            mObject2 = null;
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
        
        Item remove() {
            if( mHead == null )
                return null;
            
            Item ret = mHead;
            
            if( mHead.mNext == null ) {
                mHead = mTail = null;
            } else {
                mHead = mHead.mNext;
            }
            
            return ret;
        }
        
    }

    
    private static interface Call {
        void call( EventProcessor processor, Item item );
    }

    
    private static final Call PROCESS_PAINT = new Call() {
        public void call( EventProcessor processor, Item item ) {
            processor.processPaint( item.mSource );
        }
    };
    
    private static final Call PROCESS_APPLY_LAYOUT = new Call() {
        public void call( EventProcessor processor, Item item ) {
            processor.processLayout( item.mSource );
        }
    };
    
    private static final Call PROCESS_REQUEST_FOCUS = new Call() {
        public void call( EventProcessor processor, Item item ) {
            processor.processRequestFocus( item.mSource );
        }
    };
    
    private static final Call PROCESS_TRANSFER_FOCUS_BACKWARD = new Call() {
        public void call( EventProcessor processor, Item item ) {
            processor.processTransferFocusBackward( item.mSource );
        }
    };
    
    private static final Call PROCESS_TRANSFER_FOCUS_FORWARD = new Call() {
        public void call( EventProcessor processor, Item item ) {
            processor.processTransferFocusForward( item.mSource );
        }
    };
    
    private static final Call PROCESS_PUSH_INPUT_ROOT = new Call() {
        public void call( EventProcessor processor, Item item ) {
            processor.processPushInputRoot( item.mSource );
        }
    };
    
    private static final Call PROCESS_POP_INPUT_ROOT = new Call() {
        public void call( EventProcessor processor, Item item ) {
            processor.processPopInputRoot( item.mSource );
        }
    };
 
    private static final Call PROCESS_PROPERTY_CHANGE = new Call() {
        public void call( EventProcessor processor, Item item ) {
            processor.processPropertyChange( item.mSource, item.mString, item.mObject1, item.mObject2 );
        }
    };
    
    private static final Call PROCESS_COMPONENT_EVENT = new Call() {
        public void call( EventProcessor processor, Item item ) {
            GComponentEvent e = (GComponentEvent)item.mObject1;
            e.source().processComponentEvent( e );
        }
    };
    
    private static final Call PROCESS_ANCESTOR_EVENT = new Call() {
        public void call( EventProcessor processor, Item item ) {
            GAncestorEvent e = (GAncestorEvent)item.mObject1;
            e.source().processAncestorEvent( e );
        }
    };
    
    private static final Call PROCESS_RUN = new Call() {
        public void call( EventProcessor processor, Item item ) {
            ((Runnable)item.mObject1).run();
        }
    };
    
    
}