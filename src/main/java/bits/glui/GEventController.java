/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui;

import bits.draw3d.DrawEnv;
import bits.draw3d.Rect;
import bits.math3d.Mat;
import java.awt.*;
import static com.jogamp.opengl.GL.*;


/**
 * The lowest-level public interface available for
 * firing events. This can be used in place
 * of GRootController if you want to setup your own
 * GLContext.
 *
 * @author Philip DeCamp
 */
public final class GEventController {

    private final GLayeredPanel  mRoot;
    private final EventQueue     mQueue;
    private final GKeyboardFocusManager mFocusMan;
    private final EventProcessor mProcessor;


    public GEventController( Component optParent, GLayeredPanel optRootPane ) {
        mRoot      = optRootPane != null ? optRootPane : new GLayeredPanel();
        mQueue     = new EventQueue( mRoot, optParent );
        mFocusMan  = new GKeyboardFocusManager( mRoot, optParent );
        mProcessor = new EventProcessor( optParent, mRoot, mFocusMan );
        mRoot.treeProcessParentChanged( mQueue, null );
    }


    public GLayeredPanel pane() {
        return mRoot;
    }


    public GDispatcher dispatcher() {
        return mQueue;
    }

    /**
     * Any calls made to this class should be made on the same thread used
     * to dispatch events.
     */
    public GHumanInputController humanInputController() {
        return mProcessor;
    }


    public GKeyboardFocusManager keyboardFocusManager() {
        return mFocusMan;
    }


    public void processAll( DrawEnv graphics ) {
        processEvents();
        processPaint( graphics );
    }


    public void processEvents() {
        mQueue.processAllEvents( mProcessor );
    }


    public void processPaint( DrawEnv d ) {
        Rect bounds   = new Rect();
        mRoot.getAbsoluteBounds( bounds );
        Rect viewport = d.mContextViewport;

        Mat.identity( d.mProj.get() );
        d.mView.setOrtho( 0, bounds.width(), 0, bounds.height(), -1, 1 );

        d.mDepthTest.push();
        d.mDepthTest.apply( false, GL_LESS );
        d.mStencilTest.push();
        d.mStencilTest.apply( false );
        d.mBlend.push();
        d.mBlend.apply( true, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA );

        int xx = bounds.x() - viewport.x();
        int yy = bounds.y() - viewport.y();
        int ww = bounds.width();
        int hh = bounds.height();

        d.mScissorTest.push();
        d.mScissorTest.apply( true, xx, yy, ww, hh );
        d.mViewport.push();
        d.mViewport.apply( xx, yy, ww, hh );

        try {
            mRoot.processPaint( d );
        } finally {
            d.mViewport.pop();
            d.mScissorTest.pop();
            d.mBlend.pop();
            d.mStencilTest.pop();
            d.mDepthTest.pop();
        }
    }

}
