package bits.glui;


import java.awt.Component;
import javax.media.opengl.*;

import bits.glui.text.FontManager;
import bits.glui.util.*;
import static javax.media.opengl.GL.*;


/**
 * @author Philip DeCamp
 */
public final class GRootController {


    public static GRootController newInstance() {
        return newInstance( null, null );
    }


    public static GRootController newInstance( GLCapabilities glc, FontManager fontManager ) {
        if( glc == null ) {
            glc = new GLCapabilities();
            glc.setHardwareAccelerated( true );
            glc.setStencilBits( 8 );
            glc.setDepthBits( 16 );
            glc.setSampleBuffers( true );
            glc.setNumSamples( 4 );
        }

        if( fontManager == null ) {
            fontManager = new FontManager();
        }

        return new GRootController( glc, fontManager );
    }


    private final GLCanvas         mCanvas;
    private final GLEventHandler   mHandler;
    private final GEventController mCont;
    private final InitNode         mInit;
    private final FontManager      mFontManager;
    private final Graphics         mGraphics;


    private Animator mAnimator = null;


    private GRootController( GLCapabilities glc, FontManager fontManager ) {
        mCanvas      = new GLCanvas( glc );
        mHandler     = new GLEventHandler();
        mCont        = new GEventController( mCanvas, null );
        mInit        = new InitNode( mCanvas );
        mFontManager = fontManager == null ? new FontManager() : fontManager;
        mGraphics    = new Graphics( mFontManager );

        mCanvas.addGLEventListener( mHandler );
        new AwtEventTranslator( mCanvas, mCont.humanInputController() );
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
        return mCont.pane();
    }

    public GEventController eventController() {
        return mCont;
    }


    public void setAnimator( Animator anim ) {
        synchronized( this ) {
            if( mAnimator != null ) {
                mAnimator.stop();
                mAnimator = null;
            }
            mAnimator = anim;
        }
    }

    public void startAnimator( double targetFps ) {
        synchronized( this ) {
            if( mAnimator == null ) {
                mAnimator = new LimitAnimator( mCanvas );
            }

            mAnimator.target( (float)targetFps );
            mCont.dispatcher().ignoreRepaints( true );
            mAnimator.start();
        }
    }

    public void stopAnimator() {
        synchronized( this ) {
            if( mAnimator == null ) {
                return;
            }

            mAnimator.stop();
            mCont.dispatcher().ignoreRepaints( false );
        }
    }


    public int getClearBits() {
        return mInit.getClearBits();
    }

    public void setClearBits( int bits ) {
        mInit.setClearBits( bits );
    }

    public void addClearBits( int bits ) {
        mInit.addClearBits( bits );
    }

    public void removeClearBits( int bits ) {
        mInit.removeClearBits( bits );
    }

    public float[] getClearColor() {
        return mInit.getClearColor();
    }

    public void setClearColor( float r, float g, float b, float a ) {
        mInit.setClearColor( r, g, b, a );
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

    public void setErrorCallback( ErrorCallback err ) {
        mHandler.setErrorCallback( err );
    }


    public void generateUpdates( GLAutoDrawable gld ) {
        generateUpdates( gld,  null );
    }


    public void generateUpdates( GLAutoDrawable gld, Box contextViewport ) {
        GL gl = gld.getGL();
        Graphics g = mGraphics;
        g.mGld = gld;
        g.mGl = gl;

        if( contextViewport == null ) {
            contextViewport = g.mContextViewport;
            int w = gld.getWidth();
            int h = gld.getHeight();

            if( contextViewport == null ||
                contextViewport.x() != 0 ||
                contextViewport.y() != 0 ||
                contextViewport.width() != w ||
                contextViewport.height() != h )
            {
                contextViewport = Box.fromBounds( 0, 0, w, h );
            }
        }

        g.mContextViewport = contextViewport;
        mCont.processAll( g );
    }


    private final class GLEventHandler implements GLEventListener {

        private ErrorCallback mErr = null;

        public void init( GLAutoDrawable gld ) {
            try {
                mInit.init( gld );
            } catch( Exception ex ) {
                handle( ex );
            }
        }

        public void reshape( GLAutoDrawable gld, int x, int y, int w, int h ) {
            try {
                mInit.reshape( gld, x, y, w, h );
                mCont.pane().bounds( x, y, w, h );
            } catch( Exception ex ) {
                handle( ex );
            }
        }

        public void display( GLAutoDrawable gld ) {
            try {
                mInit.push( gld.getGL() );
                generateUpdates( gld );
                mInit.pop( gld.getGL() );
            } catch( Exception ex ) {
                handle( ex );
            }
        }

        public void displayChanged( GLAutoDrawable gld, boolean arg1, boolean arg2 ) {}

        synchronized void setErrorCallback( ErrorCallback err ) {
            mErr = err;
        }

        private void handle( Exception ex ) {
            if( mErr != null ) {
                mErr.error( ex );
            } else if( ex instanceof RuntimeException ) {
                throw ((RuntimeException)ex);
            } else {
                ex.printStackTrace();
            }
        }

    }


    public static final class InitNode {

        private final GLCanvas mCanvas;

        private       int     mClearBits  = GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT;
        private final float[] mClearColor = { 0, 0, 0, 1 };
        private       int     mDrawBuffer = GL_BACK;

        private int     mDoubleBuffered = 0; //-1 false, 0 unknown, 1 true
        private boolean mAutoFlush      = true;
        private boolean mAutoSwap       = true;
        private boolean mDoAutoFlush    = false;


        private InitNode( GLCanvas canvas ) {
            mCanvas = canvas;
        }


        public boolean autoFlush() {
            return mAutoFlush;
        }


        public void autoFlush( boolean autoFlush ) {
            mAutoFlush = autoFlush;
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


        public void setClearBits( int bits ) {
            mClearBits = bits;
        }


        public void addClearBits( int bit ) {
            mClearBits |= bit;
        }


        public void removeClearBits( int bits ) {
            mClearBits &= ~bits;
        }


        public float[] getClearColor() {
            return mClearColor.clone();
        }


        public void setClearColor( float r, float g, float b, float a ) {
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
            gl.glEnable( GL_BLEND );

            gl.glDepthFunc( GL_LESS );
            gl.glEnable( GL_DEPTH_TEST );

            gl.glDisable( GL_LOGIC_OP );
            gl.glClearStencil( 0 );

            gl.glDisable( GL_LINE_SMOOTH );
            gl.glHint( GL_LINE_SMOOTH_HINT, GL_NICEST );

            gl.glDisable( GL_DITHER );
            gl.glDisable( GL_FOG );
            gl.glDisable( GL_LIGHTING );
            gl.glDisable( GL_STENCIL_TEST );
            gl.glDisable( GL_TEXTURE_1D );
            gl.glDisable( GL_TEXTURE_2D );

            gl.glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP );
            gl.glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP );

            gl.glAlphaFunc( GL_NOTEQUAL, 0 );
            gl.glEnable( GL_ALPHA_TEST );

            gl.glPixelTransferi( GL_MAP_COLOR, GL_FALSE );
            gl.glPixelTransferi( GL_RED_SCALE, 1 );
            gl.glPixelTransferi( GL_RED_BIAS, 0 );
            gl.glPixelTransferi( GL_GREEN_SCALE, 1 );
            gl.glPixelTransferi( GL_GREEN_BIAS, 0 );
            gl.glPixelTransferi( GL_BLUE_SCALE, 1 );
            gl.glPixelTransferi( GL_BLUE_BIAS, 0 );

            gl.glCullFace( GL_BACK );
            gl.glFrontFace( GL_CCW );
            gl.glDisable( GL_CULL_FACE );

            gl.glMatrixMode( GL_PROJECTION );
            gl.glLoadIdentity();
            gl.glMatrixMode( GL_MODELVIEW );
            gl.glLoadIdentity();

            gl.glClear( mClearBits );
        }


        public void reshape( GLAutoDrawable gld, int x, int y, int w, int h ) {
            GL gl = gld.getGL();
            gl.glViewport( x, y, w, h );
        }


        public void push( GL gl ) {
            gl.glDrawBuffer( mDrawBuffer );

            if( mClearBits != 0 ) {
                gl.glClearColor( mClearColor[0], mClearColor[1], mClearColor[2], mClearColor[3] );
                gl.glClear( mClearBits );
            }
        }


        public void pop( GL gl ) {
            if( mDoAutoFlush ) {
                gl.glFlush();
            }
        }


        private void updateAutoFlush() {
            mDoAutoFlush = mAutoFlush && !(mAutoFlush && mDoubleBuffered == 1);
        }

    }


    public static interface ErrorCallback {
        public void error( Throwable t );
    }


    private static final class Graphics implements GGraphics {

        final FontManager mFontManager;

        GLAutoDrawable mGld = null;
        GL mGl = null;
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
