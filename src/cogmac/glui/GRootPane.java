package cogmac.glui;

import static javax.media.opengl.GL.*;

import java.awt.Component;
import java.awt.event.*;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import cogmac.glui.text.FontManager;



/**
 * @author decamp
 */
public class GRootPane extends GPanel {

    private Graphics mGraphics;
    private GComponent mLayoutRoot  = null;
    private boolean mIgnoreRepaints = false;
    
    private final GInputDispatcher mDispatcher; 
    
    
    public GRootPane() {
        this( null, null );
    }
    
    
    public GRootPane( FontManager fontManager ) {
        this( null, fontManager );
    }
    
    
    public GRootPane( Component awtOwner, FontManager fontManager ) {
        mDispatcher = new GInputDispatcher( awtOwner, this);
        
        if(fontManager == null)
            fontManager = new FontManager();
        
        mGraphics = new Graphics(fontManager);
    }
    
    
    
    public void generateUpdates(GLAutoDrawable gld) {
        Box bounds = absoluteBounds();
        generateUpdates(gld, bounds, Box.fromBounds(0, 0, gld.getWidth(), gld.getHeight()));
    }
    
    
    public void generateUpdates(GLAutoDrawable gld, Box contextViewport) {
        if(contextViewport == null) {
            generateUpdates(gld);
        }
        
        Box bounds = absoluteBounds();
        generateUpdates(gld, bounds, contextViewport);
    }
    
    
    private void generateUpdates(GLAutoDrawable gld, Box bounds, Box contextViewport) {
        GComponent lay = mLayoutRoot;
        mLayoutRoot = null;
        
        if(lay != null) {
            lay.processLayoutEvent();
            mDispatcher.validate();
        }
        
        final GL gl = gld.getGL();
        mGraphics.mGld             = gld;
        mGraphics.mGl              = gl;
        mGraphics.mContextViewport = contextViewport;
        
        gl.glPushAttrib(GL_ALL_ATTRIB_BITS);

        gl.glMatrixMode(GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0, bounds.maxX(), 0, bounds.maxY(), -1, 1);
        
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        gl.glDisable(GL_DEPTH_TEST);
        gl.glDisable(GL_STENCIL_TEST);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL_SCISSOR_TEST);
        gl.glEnable(GL_ALPHA_TEST);
    
        gl.glScissor( bounds.x() - contextViewport.x(), 
                      bounds.y() - contextViewport.y(),
                      bounds.width(), 
                      bounds.height() );
      
        gl.glViewport( bounds.x() - contextViewport.x(), 
                       bounds.y() - contextViewport.y(), 
                       bounds.width(), 
                       bounds.height() );
        
        processPaintEvent(mGraphics);
        
        gl.glMatrixMode(GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPopMatrix();
        gl.glPopAttrib();
    }
    
    

    public void translateMousePressedEvent(MouseEvent e, int ex, int ey) {
        mDispatcher.translateMousePressedEvent(e, ex, ey);
    }
    
    
    public void translateMouseReleasedEvent(MouseEvent e, int ex, int ey) {
        mDispatcher.translateMouseReleasedEvent(e, ex, ey);
    }
    
    
    public void translateMouseClickedEvent(MouseEvent e, int ex, int ey) {
        mDispatcher.translateMouseClickedEvent(e, ex, ey);
    }

    
    public void translateMouseEnteredEvent(MouseEvent e, int ex, int ey) {
        mDispatcher.translateMouseEnteredEvent(e, ex, ey);
    }
    
    
    public void translateMouseExitedEvent(MouseEvent e, int ex, int ey) {
        mDispatcher.translateMouseExitedEvent(e, ex, ey);
    }
    
    
    public void translateMouseMovedEvent(MouseEvent e, int ex, int ey) {
        mDispatcher.translateMouseMovedEvent(e, ex, ey);
    }

    
    public void translateMouseDraggedEvent(MouseEvent e, int ex, int ey) {
        mDispatcher.translateMouseDraggedEvent(e, ex, ey);
    }

    
    public void translateMouseWheelMovedEvent(MouseWheelEvent e, int ex, int ey) {
        mDispatcher.translateMouseWheelMovedEvent(e, ex, ey);
    }
    
    
    public void translateKeyPressedEvent(KeyEvent e) {
        mDispatcher.translateKeyPressedEvent(e);
    }
    
    
    public void translateKeyReleasedEvent(KeyEvent e) {
        mDispatcher.translateKeyReleasedEvent(e);
    }
    
    
    public void translateKeyTypedEvent(KeyEvent e) {
        mDispatcher.translateKeyTypedEvent(e);
    }
    
    
 
    @Override
    public void fireLayoutRequest(GComponent source) {
        if(source == null)
            return;
        
        //TODO: Dumbest optimization scheme possible.
        //If there's any possible confusion as to which 
        //components need laying out, do everything.
        if(mLayoutRoot == null) {
            mLayoutRoot = source;
        }else if(source != mLayoutRoot) {
            mLayoutRoot = this;
        }
            
    }
    
    
    @Override
    public void fireDrawRequest(GComponent source) {
        mDispatcher.processRepaintRequest( source );
    }
    
    
    @Override
    public void fireRequestFocus(GComponent source) {
        mDispatcher.processFocusRequest(source);
    }
    
    
    @Override
    public void fireTransferFocusBackward(GComponent source) {
        mDispatcher.processTransferFocusBackward(source);
    }
    
    
    @Override
    public void fireTransferFocusForward(GComponent source) {
        mDispatcher.processTransferFocusForward(source);
    }
    
    
    @Override
    public void firePushInputRoot(GComponent source) {
        mDispatcher.processPushInputRoot(source);
    }
    
    
    @Override
    public void firePopInputRoot(GComponent source) {
        mDispatcher.processPopInputRoot(source);
    }
    
    
    
    
    private static final class Graphics implements GGraphics {
        
        final FontManager mFontManager;
        
        GLAutoDrawable mGld       = null;
        GL mGl                    = null;
        Box mContextViewport = null;

        
        Graphics(FontManager fontManager) {
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