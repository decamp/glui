package cogmac.glui;

import java.awt.Font;
import java.util.*;

import cogmac.glui.event.*;


/**
 * @author decamp
 */
public interface GComponent {

    public static final String PROP_ENABLED            = "enabled";
    public static final String PROP_DISPLAYED          = "displayed";
    public static final String PROP_HAS_MOUSE_LISTENER = "hasMouseListener";
    public static final String PROP_HAS_KEY_LISTENER   = "hasKeyListener";
    
    public void addChild( GComponent pane );
    public void removeChild( GComponent pane );
    public void clearChildren();
    public List<GComponent> getChildren();
    public void setLayout( GLayout layout );
    public GComponent getParent();
    
    public Box bounds();
    public Box absoluteBounds();
    public int x();
    public int y();
    public int width();
    public int height();
    public boolean contains( int x, int y );
    public GComponent position( int x, int y );
    public GComponent bounds( int x, int y, int w, int h );
    public GComponent size( int w, int y );
    public void setVisible( boolean visible );
    public boolean isVisible();
    public void setEnabled( boolean enable );
    public boolean isEnabled();
    
    /**
     * @return true iff these conditions are all true: <br/>
     *        This component has been placed in a component hierarchy with valid root.
     *        This component is visible.
     *        All ancestors of this component are visible.
     */
    public boolean isDisplayed();
    
    public GComponent foreground( GColor color );
    public GColor foreground();
    public GComponent background( GColor color );
    public GColor background();
    public GComponent font( Font font );
    public Font font();
    
    public void addComponentListener( GComponentListener listener );
    public void removeComponentListener( GComponentListener listener );
    public void addAncestorListener( GAncestorListener listener );
    public void removeAncestorListener( GAncestorListener listener );
    public void addFocusListener( GFocusListener listener );
    public void removeFocusListener( GFocusListener listener );
    public void addMouseListener( GMouseListener listener );
    public void removeMouseListener( GMouseListener listener );
    public void addMouseMotionListener( GMouseMotionListener listener );
    public void removeMouseMotionListener( GMouseMotionListener listener );
    public void addMouseWheelListener( GMouseWheelListener listener );
    public void removeMouseWheelListener( GMouseWheelListener listener );
    public void addKeyListener( GKeyListener listener );
    public void removeKeyListener( GKeyListener listener );
    public void addPaintListener( GPaintListener listener );
    public void removePaintListener( GPaintListener listener );
    
    public boolean hasKeyListener();
    public boolean hasMouseListener();
    
    public boolean hasFocus();
    public boolean requestFocus();
    public void transferFocusBackward();
    public void transferFocusForward();
    public void startModal();
    public void stopModal();
    
    public GComponent componentAt( int x, int y );
    public GComponent displayedComponentAt( int x, int y );
    public GComponent mouseFocusableComponentAt( int x, int y );
    
    public void applyLayout();
    public boolean needsLayout();
    public void repaint();
    public boolean needsRepaint();
    
    /**
     * Methods that should only be called by the parent component.
     */
    public void treeProcessParentChanged( GDispatcher dispatcher, GComponent parent );
    public void treeProcessAncestorMoved( GComponent source );
    public void treeProcessAncestorResized( GComponent source );
    public void treeProcessParentShown();
    public void treeProcessParentHidden();
    
    public void processLayout();
    public void processPaint( GGraphics g );
    public void processComponentEvent( GComponentEvent e );
    public void processAncestorEvent( GAncestorEvent e );
    public void processFocusEvent( GFocusEvent e );
    public void processMouseEvent( GMouseEvent e );
    public void processMouseMotionEvent( GMouseEvent e );
    public void processMouseWheelEvent( GMouseWheelEvent e );
    public void processKeyEvent( GKeyEvent e );
    
}
