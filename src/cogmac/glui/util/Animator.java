package cogmac.glui.util;

import java.awt.Component;


public class Animator {
    
    private final Component mTarget;
    private volatile long mMinMillisPerFrame = 0;
    private volatile Thread mThread = null;
    

    Animator(Component target) {
        mTarget = target;
    }
    
    

    public synchronized void start() {
        if(mThread != null)
            return;
        
        mThread = new Thread() {
            public void run() {
                runLoop();
            }
        };
        
        mThread.start();
    }

    
    public synchronized void stop() {
        if(mThread != null) {
            mThread = null;
            notifyAll();
        }
    }
    
    
    public synchronized void setMaxFramerate(float fps) {
        mMinMillisPerFrame = Math.round(1000.0f / fps);
        notifyAll();
    }

    
    
    private void runLoop() {
        long nextMillis = System.currentTimeMillis();

        while(true) {
            synchronized(Animator.this) {
                if(Thread.currentThread() != mThread)
                    break;
                
                long nowMillis = System.currentTimeMillis();
                long waitMillis  = nextMillis - nowMillis;
                nextMillis = nowMillis + mMinMillisPerFrame;
                
                if(waitMillis > 10L) {
                    try{
                        Thread.sleep(waitMillis);
                    }catch(InterruptedException ex) {}
                }
            }

            mTarget.repaint();
        }
    }

}
