package cogmac.glui;


import java.awt.Component;

import javax.media.opengl.*;

import static javax.media.opengl.GL.*;

import cogmac.glui.text.FontManager;


/**
 * @author Philip DeCamp
 */
public final class GRootController {

    
    public static GRootController newInstance() {
        return newInstance(null, null);
    }
    
    
    public static GRootController newInstance(GLCapabilities glc, FontManager fontManager) {
        if(glc == null) {
            glc = new GLCapabilities();
            glc.setHardwareAccelerated(true);
            glc.setStencilBits(8);
            glc.setDepthBits(16);
            glc.setSampleBuffers(true);
            glc.setNumSamples(4);
        }
        
        if(fontManager == null)
            fontManager = new FontManager();
        
        return new GRootController( glc, fontManager );
    }
    
    
    private final GLCanvas mCanvas;
    private final InitNode mInit;
    private final FontManager mFontManager;
    private final Graphics mGraphics;
    
    private final EventQueue mQueue;
    private final EventProcessor mProcessor;
    private final GLayeredPanel mRoot;
    
    private Animator mAnimator;
    
    
    private GRootController( GLCapabilities glc, FontManager fontManager ) {
        mCanvas      = new GLCanvas( glc );
        mInit        = new InitNode( mCanvas );
        mFontManager = fontManager == null ? new FontManager() : fontManager;
        mGraphics    = new Graphics( mFontManager );
        
        mRoot        = new GLayeredPanel();
        mQueue       = new EventQueue( mRoot );
        mProcessor   = new EventProcessor( mCanvas, mRoot );
        mRoot.treeProcessParentChanged( mQueue, null );
        
        mCanvas.addGLEventListener( new GLEventHandler() );
        new AwtEventTranslator( mCanvas, mProcessor );
    }

    
    
    public FontManager fontManager() {
        return mFontManager;
    }
    
    public Component component() {
        return mCanvas;
    }
    
    public GLAutoDrawable drawable() {
        return mCanvas;
    }

    public GLayeredPanel rootPane() {
        return mRoot;
    }
    
    public void startAnimator( double maxFps ) {
        synchronized( this ) {
            if( mAnimator != null )
                return;
            
            mQueue.ignoreRepaints( true );
            long micros = (long)( 1000000.0 / maxFps ); 
            mAnimator = new Animator( mCanvas, micros );
            mAnimator.start();
        }
    }
    
    public void stopAnimator() {
        synchronized(this) {
            if(mAnimator == null)
                return;
            
            mAnimator.stopAnimator();
            mAnimator = null;
            mQueue.ignoreRepaints( false );
        }
    }
    
    
    
    public int getClearBits() {
        return mInit.getClearBits();
    }
    
    public void setClearBits(int bits) {
        mInit.setClearBits(bits);
    }

    public void addClearBits(int bits) {
        mInit.addClearBits(bits);
    }
    
    public void removeClearBits(int bits) {
        mInit.removeClearBits(bits);
    }

    public float[] getClearColor() {
        return mInit.getClearColor();
    }
    
    public void setClearColor(float r, float g, float b, float a) {
        mInit.setClearColor(r, g, b, a);
    }

    public boolean autoFlush() {
        return mInit.autoFlush();
    }
    
    public void autoFlush( boolean autoFlush ) {
        mInit.autoFlush( autoFlush );
    }
    
    public boolean autoSwapBufferMode() {
        return mInit.autoSwap();
    }
    
    public void autoSwapBufferMode( boolean autoSwap ) {
        mInit.autoSwap( autoSwap );
    }
    
    
    public void generateUpdates( GLAutoDrawable gld ) {
        generateUpdates( gld, mRoot.absoluteBounds(), null );
    }
    
    public void generateUpdates( GLAutoDrawable gld, Box contextViewport ) {
        generateUpdates( gld, mRoot.absoluteBounds(), contextViewport );
    }
    
    private void generateUpdates( GLAutoDrawable gld, Box bounds, Box contextViewport ) {
        mQueue.processAllEvents( mProcessor );
        
        GL gl = gld.getGL();
        
        Graphics g = mGraphics;
        g.mGld = gld;
        g.mGl  = gl;
        
        if( contextViewport == null ) {
            Box b = contextViewport = g.mContextViewport;
            int w = gld.getWidth();
            int h = gld.getHeight();
            
            if( b != null && b.x() == 0 && b.y() == 0 && b.width() == w && b.height() == h ) {
                contextViewport = b;
            } else {
                contextViewport = g.mContextViewport = Box.fromBounds( 0, 0, w, h );
            }
            
        } else {
            g.mContextViewport = contextViewport;
        }
        
        gl.glPushAttrib( GL_ALL_ATTRIB_BITS );

        gl.glMatrixMode( GL_PROJECTION );
        gl.glLoadIdentity();
        gl.glOrtho( 0, bounds.maxX(), 0, bounds.maxY(), -1, 1 );

        gl.glMatrixMode( GL_MODELVIEW );
        gl.glLoadIdentity();
        
        gl.glDisable( GL_DEPTH_TEST );
        gl.glDisable( GL_STENCIL_TEST );
        gl.glEnable( GL_BLEND );
        gl.glBlendFuncSeparate( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA );
        gl.glEnable( GL_SCISSOR_TEST );
        gl.glEnable( GL_ALPHA_TEST );
        
        gl.glScissor( bounds.x() - contextViewport.x(),
                      bounds.y() - contextViewport.y(),
                      bounds.width(),
                      bounds.height() );

        gl.glViewport( bounds.x() - contextViewport.x(),
                       bounds.y() - contextViewport.y(),
                       bounds.width(),
                       bounds.height() );
        
        mRoot.processPaint( g );
        
        gl.glPopAttrib();
        
    }
    
    
    
    private final class GLEventHandler implements GLEventListener {

        public void init( GLAutoDrawable gld ) {
            mInit.init( gld );
        }

        public void reshape( GLAutoDrawable gld, int x, int y, int w, int h ) {
            mInit.reshape(gld, x, y, w, h);
            mRoot.bounds(x, y, w, h);
        }
        
        public void display( GLAutoDrawable gld ) {
            mInit.push( gld.getGL() );
            generateUpdates( gld );
            mInit.pop( gld.getGL() );
        }

        public void displayChanged( GLAutoDrawable gld, boolean arg1, boolean arg2 ) {}

    }
    
    
    public final class InitNode {

        private final GLCanvas mCanvas;
        
        private int mClearBits            = GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT;
        private final float[] mClearColor = { 0,0,0,1 };
        private int mDrawBuffer           = GL_BACK;
        
        private int mDoubleBuffered       = 0; //-1 false, 0 unknown, 1 true
        private boolean mAutoFlush        = true;
        private boolean mAutoSwap         = true;
        private boolean mDoAutoFlush      = false;
        
        
        private InitNode( GLCanvas canvas ) {
            mCanvas = canvas;
        }
        
        
        
        public boolean autoFlush() {
            return mAutoFlush;
        }
        
        
        public void autoFlush( boolean autoFlush ) {
            mAutoFlush   = autoFlush;
            updateAutoFlush();
        }
        
        
        public boolean autoSwap() {
            return mAutoSwap;
        }
        
        
        public void autoSwap( boolean autoSwap ) {
            mAutoSwap = autoSwap;
            
            if( mDoubleBuffered >= 0 ) {
                mCanvas.setAutoSwapBufferMode( autoSwap );
            } else {
                mCanvas.setAutoSwapBufferMode( false );
            }
            
            updateAutoFlush();
        }
        
        
        public int getClearBits() {
            return mClearBits;
        }
        
        
        public void setClearBits(int bits) {
            mClearBits = bits;
        }

        
        public void addClearBits(int bit) {
            mClearBits |= bit;
        }
        
        
        public void removeClearBits(int bits) {
            mClearBits &= ~bits;
        }

        
        public float[] getClearColor() {
            return mClearColor.clone();
        }
        
        
        public void setClearColor(float r, float g, float b, float a) {
            mClearColor[0] = r;
            mClearColor[1] = g;
            mClearColor[2] = b;
            mClearColor[3] = a;
        }
        
        
        public void init( GLAutoDrawable gld ) {
            
            if( gld.getChosenGLCapabilities().getDoubleBuffered() ) {
                mDoubleBuffered = 1;
                mCanvas.setAutoSwapBufferMode( mAutoSwap );
                mDrawBuffer = GL_BACK;
            } else {
                mDoubleBuffered = -1;
                mCanvas.setAutoSwapBufferMode( false );
                mDrawBuffer = GL_FRONT;
            }
            
            updateAutoFlush();
            
            GL gl = gld.getGL();
            gl.glBlendFuncSeparate( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA );
            gl.glEnable(GL_BLEND);
            
            gl.glDepthFunc(GL_LESS);
            gl.glEnable(GL_DEPTH_TEST);
            
            gl.glDisable(GL_LOGIC_OP);
            gl.glClearStencil(0);
            
            gl.glDisable(GL_LINE_SMOOTH);
            gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            
            gl.glDisable(GL_DITHER);
            gl.glDisable(GL_FOG);
            gl.glDisable(GL_LIGHTING);
            gl.glDisable(GL_STENCIL_TEST);
            gl.glDisable(GL_TEXTURE_1D);
            gl.glDisable(GL_TEXTURE_2D);
            
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
            
            gl.glAlphaFunc(GL_NOTEQUAL, 0);
            gl.glEnable(GL_ALPHA_TEST);

            gl.glPixelTransferi(GL_MAP_COLOR, GL_FALSE);
            gl.glPixelTransferi(GL_RED_SCALE, 1);
            gl.glPixelTransferi(GL_RED_BIAS, 0);
            gl.glPixelTransferi(GL_GREEN_SCALE, 1);
            gl.glPixelTransferi(GL_GREEN_BIAS, 0);
            gl.glPixelTransferi(GL_BLUE_SCALE, 1);
            gl.glPixelTransferi(GL_BLUE_BIAS, 0);

            gl.glCullFace( GL_BACK );
            gl.glFrontFace( GL_CCW );
            gl.glDisable( GL_CULL_FACE );

            gl.glMatrixMode( GL_PROJECTION );
            gl.glLoadIdentity();
            gl.glMatrixMode( GL_MODELVIEW );
            gl.glLoadIdentity();

            gl.glClear( mClearBits );
        }
        
        
        public void reshape(GLAutoDrawable gld, int x, int y, int w, int h) {
            GL gl = gld.getGL();
            gl.glViewport(x, y, w, h);
        }
        
        
        public void push(GL gl) {
            gl.glDrawBuffer( mDrawBuffer );
            
            if( mClearBits != 0 ) {
                gl.glClearColor( mClearColor[0], mClearColor[1], mClearColor[2], mClearColor[3] );
                gl.glClear( mClearBits );
            }
        }

        
        public void pop(GL gl) {
            if( mDoAutoFlush ) {
                gl.glFlush();
            }
        }
        
        
        private void updateAutoFlush() {
            mDoAutoFlush = mAutoFlush && !(mAutoFlush && mDoubleBuffered == 1 );
        }
        
    }
    
    
    private static class Animator extends Thread {

        private final GLCanvas mTarget;
        private final long mMinMillisPerFrame;
        
        private boolean mKill = false;
        
        
        Animator( GLCanvas target, long minMicrosPerFrame ) {
            mTarget = target;
            mMinMillisPerFrame = minMicrosPerFrame / 1000L;
        }
            
        
        public void run() {
            long nextMillis = System.currentTimeMillis();
            
            while(true) {
                synchronized(this) {
                    if(mKill) {
                        break;
                    }
                    
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
        
        
        public synchronized void stopAnimator() {
            mKill = true;
            notify();
        }
        
    }
    
    
    private static final class Graphics implements GGraphics {

        final FontManager mFontManager;
        
        GLAutoDrawable mGld = null;
        GL mGl  = null;
        Box mContextViewport = null;
        
        
        Graphics( FontManager fontManager ) {
            mFontManager = fontManager;
        }


        public GL gl() {
            return mGl;
        }

        public FontManager fontManager() {
            return mFontManager;
        }

        public GLAutoDrawable drawable() {
            return mGld;
        }

        public Box contextViewport() {
            return mContextViewport;
        }

    }
    
}
