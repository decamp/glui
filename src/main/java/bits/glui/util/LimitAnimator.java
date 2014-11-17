/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui.util;

import java.awt.Component;


public class LimitAnimator implements Animator {

    private final Component mTarget;
    private volatile long mMinMillisPerFrame = 0;
    private volatile Thread mThread = null;

    
    public LimitAnimator( Component target ) {
        this( target, 0f );
    }
    
    public LimitAnimator( Component target, float maxFps ) {
        mTarget = target;
        target( maxFps );
    }
    


    @Override
    public synchronized void start() {
        if( mThread != null )
            return;

        mThread = new Thread() {
            public void run() {
                runLoop();
            }
        };

        mThread.start();
    }

    @Override
    public synchronized void stop() {
        if( mThread != null ) {
            mThread = null;
            notifyAll();
        }
    }

    @Override
    public boolean isRunning() {
        return mThread != null;
    }

    public synchronized void target( float fps ) {
        if( fps <= 0f ) {
            mMinMillisPerFrame = 0L;
        } else {
            mMinMillisPerFrame = Math.round( 1000.0f / fps );
        }
        notifyAll();
    }


    private void runLoop() {
        long nextMillis = System.currentTimeMillis();

        while( true ) {
            synchronized( LimitAnimator.this ) {
                if( Thread.currentThread() != mThread ) {
                    break;
                }

                long nowMillis  = System.currentTimeMillis();
                long waitMillis = nextMillis - nowMillis;
                nextMillis = nowMillis + mMinMillisPerFrame;

                if( waitMillis > 10L ) {
                    try {
                        Thread.sleep( waitMillis );
                    } catch( InterruptedException ex ) {}
                }
            }

            mTarget.repaint();
        }
    }

}
