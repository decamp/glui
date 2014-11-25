/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui;


import java.awt.Component;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;

import bits.draw3d.*;
import bits.draw3d.text.FontManager;
import bits.draw3d.util.Animator;
import bits.draw3d.util.LimitAnimator;

import static javax.media.opengl.GL.*;


/**
 * @author Philip DeCamp
 */
public final class GRootController {


    public static GRootController create() {
        return create( null );
    }


    public static GRootController create( GLCapabilities glc ) {
        if( glc == null ) {
            GLProfile profile = GLProfile.get( GLProfile.GL3 );
            glc = new GLCapabilities( profile );
            glc.setHardwareAccelerated( true );
            glc.setStencilBits( 8 );
            glc.setDepthBits( 24 );
            glc.setSampleBuffers( true );
            glc.setNumSamples( 4 );
        }

        return new GRootController( glc );
    }


    private final GLCanvas         mCanvas;
    private final GLEventHandler   mHandler;
    private final GEventController mCont;
    private final InitNode         mInit;
    private final DrawEnv          mDrawEnv;

    private Animator mAnimator = null;


    private GRootController( GLCapabilities glc ) {
        mCanvas = new GLCanvas( glc );
        mHandler = new GLEventHandler();
        mCont = new GEventController( mCanvas, null );
        mInit = new InitNode( mCanvas );
        mDrawEnv = new DrawEnv();

        mCanvas.addGLEventListener( mHandler );
        new AwtEventTranslator( mCanvas, mCont.humanInputController() );
    }


    /**
     * Note that if a frame is not being rendered, the DrawEnv may not be valid and changes
     * to state may be overwritten. Try to avoid using this method, and to avoid caching
     * DrawEnv objects.
     */
    public DrawEnv drawEnv() {
        return mDrawEnv;
    }


    public FontManager fontManager() {
        return mDrawEnv.mFontMan;
    }


    public ShaderManager shaderManager() {
        return mDrawEnv.mShaderMan;
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


    public void generateUpdates( GLAutoDrawable gld, Rect optContextViewport ) {
        mDrawEnv.init( gld, optContextViewport );
        mInit.push( mDrawEnv );
        mCont.processAll( mDrawEnv );
        mInit.pop( mDrawEnv );
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


        public void init( DrawEnv g ) {
            if( g.mGld.getChosenGLCapabilities().getDoubleBuffered() ) {
                mDoubleBuffered = 1;
                mCanvas.setAutoSwapBufferMode( mAutoSwap );
                mDrawBuffer = GL_BACK;
            } else {
                mDoubleBuffered = -1;
                mCanvas.setAutoSwapBufferMode( false );
                mDrawBuffer = GL_FRONT;
            }
            updateAutoFlush();

            g.mProj.identity();
            g.mView.identity();
            g.mColorMat.identity();
            g.mTexMat.identity();

            g.mBlend.apply( true, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA );
            g.mColorMask.apply( true, true, true, true );
            g.mGl.glCullFace( GL_BACK );
            g.mGl.glFrontFace( GL_CCW );
            g.mCullFace.apply( false );
            g.mDepthMask.apply( true );
            g.mDepthTest.apply( true, GL_LESS );
            g.mPolygonOffset.apply( false );
            g.mScissorTest.apply( false );
            g.mStencilTest.apply( false, GL_ALWAYS, 0, 0xFFFFFFFF );
            g.mStencilOp.apply( GL_KEEP, GL_KEEP, GL_KEEP );

            g.mGl.glClear( mClearBits );
        }


        public void reshape( GLAutoDrawable gld, int x, int y, int w, int h ) {
            GL gl = gld.getGL();
            gl.glViewport( x, y, w, h );
        }


        public void push( DrawEnv g ) {
            g.mGl.glDrawBuffer( mDrawBuffer );
            if( mClearBits != 0 ) {
                g.mGl.glClearColor( mClearColor[0], mClearColor[1], mClearColor[2], mClearColor[3] );
                g.mGl.glClear( mClearBits );
            }
        }


        public void pop( DrawEnv g ) {
            if( mDoAutoFlush ) {
                g.mGl.glFlush();
            }
        }


        private void updateAutoFlush() {
            mDoAutoFlush = mAutoFlush && !(mAutoFlush && mDoubleBuffered == 1);
        }

    }


    public static interface ErrorCallback {
        public void error( Throwable t );
    }


    private final class GLEventHandler implements GLEventListener {

        private ErrorCallback mErr = null;

        public void init( GLAutoDrawable gld ) {
            try {
                GL gl = gld.getGL();
                mDrawEnv.init( gld, null );
                mInit.init( mDrawEnv );
                mDrawEnv.checkErr();
            } catch( Exception ex ) {
                handle( ex );
            }
        }

        @Override
        public void dispose( GLAutoDrawable gld ) {
            //TODO: Complete disposal path?
            mDrawEnv.dispose( gld );
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
                generateUpdates( gld );
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


}
