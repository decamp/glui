package bits.glui;


import javax.media.opengl.*;
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
        mQueue     = new EventQueue( mRoot );
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


    public void processAll( GGraphics graphics ) {
        processEvents();
        processPaint( graphics );
    }


    public void processEvents() {
        mQueue.processAllEvents( mProcessor );
    }


    public void processPaint( GGraphics g ) {
        GL gl = g.gl();
        Box bounds   = mRoot.absoluteBounds();
        Box viewport = g.contextViewport();

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

        int xx = bounds.x() - viewport.x();
        int yy = bounds.y() - viewport.y();
        int ww = bounds.width();
        int hh = bounds.height();

        gl.glScissor( xx, yy, ww, hh );
        gl.glViewport( xx, yy, ww, hh );

        mRoot.processPaint( g );
        gl.glPopAttrib();

    }

}
