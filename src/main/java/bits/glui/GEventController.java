package bits.glui;

import bits.draw3d.DrawEnv;
import bits.math3d.Mat;
import java.awt.*;
import static javax.media.opengl.GL.*;


/**
 * Pretty much the lowest-level public interface for
 * direct event-driving. This can be used in place
 * of GRootController if you want to setup your own
 * GLContext.
 *
 * @author Philip DeCamp
 */
public final class GEventController {

    private final GLayeredPanel  mRoot;
    private final EventQueue     mQueue;
    private final EventProcessor mProcessor;


    public GEventController( Component optParent, GLayeredPanel optRootPane ) {
        mRoot      = optRootPane != null ? optRootPane : new GLayeredPanel();
        mQueue     = new EventQueue( mRoot, optParent );
        mProcessor = new EventProcessor( optParent, mRoot );
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


    public void processAll( DrawEnv graphics ) {
        processEvents();
        processPaint( graphics );
    }


    public void processEvents() {
        mQueue.processAllEvents( mProcessor );
    }


    public void processPaint( DrawEnv g ) {
        Rect bounds   = new Rect();
        mRoot.absoluteBounds( bounds );
        Rect viewport = g.mContextViewport;

        Mat.identity( g.mProj.get() );
        g.mView.setOrtho( 0, bounds.width(), 0, bounds.height(), -1, 1 );

        g.mDepthTest.push();
        g.mDepthTest.set( false, GL_LESS );
        g.mStencilTest.push();
        g.mStencilTest.set( false );
        g.mBlend.push();
        g.mBlend.set( true, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA );

        int xx = bounds.x() - viewport.x();
        int yy = bounds.y() - viewport.y();
        int ww = bounds.width();
        int hh = bounds.height();

        g.mScissorTest.push();
        g.mScissorTest.set( true, xx, yy, ww, hh );
        g.mViewport.push();
        g.mViewport.set( xx, yy, ww, hh );

        try {
            mRoot.processPaint( g );
        } finally {
            g.mViewport.pop();
            g.mScissorTest.pop();
            g.mBlend.pop();
            g.mStencilTest.pop();
            g.mDepthTest.pop();
        }
    }

}
