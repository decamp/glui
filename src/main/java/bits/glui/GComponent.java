package bits.glui;

import java.awt.Font;
import java.util.*;

import bits.glui.event.*;
import bits.math3d.Vec4;


/**
 * @author decamp
 */
public interface GComponent {

    public static final String PROP_ENABLED            = "enabled";
    public static final String PROP_DISPLAYED          = "displayed";
    public static final String PROP_HAS_MOUSE_LISTENER = "hasMouseListener";
    public static final String PROP_HAS_KEY_LISTENER   = "hasKeyListener";
    
    public GComponent parent();
    public List<GComponent> children();
    public void addChild( GComponent pane );
    public void removeChild( GComponent pane );
    public void clearChildren();

    public void bounds( Rect out );
    /**
     * Retrieves the absolute bounds of a component, or the bounds within the frame.
     * If the component is not installed, it's relative bounds will be reported is its absolute bounds.
     *
     * @param out Receives absolute bounds of component.
     */
    public void absoluteBounds( Rect out );
    public Rect absoluteBounds();
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
     *        This component has been placed in a component hierarchy with valid pane.
     *        This component is visible.
     *        All ancestors of this component are visible.
     */
    public boolean isDisplayed();

    /**
     * @param out Receives current foreground color.
     * @return true if foreground color is defined, otherwise {@code out} will not be modified.
     */
    public boolean foreground( Vec4 out );

    /**
     * @param color Foreground color, or {@code null} for none.
     * @return this
     */
    public GComponent setForeground( Vec4 color );

    /**
     * Sets foreground color.
     *
     * @return this
     */
    public GComponent setForeground( float red, float green, float blue, float alpha );

    /**
     * @param out Receives current foreground color.
     * @return true if foreground color is defined, otherwise {@code out} will not be modified.
     */
    public boolean background( Vec4 out );

    /**
     * @param color Background color, or {@code null} for none.
     * @return this
     */
    public GComponent setBackground( Vec4 color );

    /**
     * Sets background color.
     *
     * @return this
     */
    public GComponent setBackground( float red, float green, float blue, float alpha );


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
    
    public void setLayout( GLayout layout );
    public void applyLayout();
    public boolean needsLayout();
    
    public GComponent componentAt( int x, int y );
    public GComponent displayedComponentAt( int x, int y );
    public GComponent mouseFocusableComponentAt( int x, int y );
    
    public void repaint();
    public boolean needsRepaint();
    
    public GDispatcher dispatcher();
    
    /**
     * Methods that should only be called by parent component.
     */
    public void treeProcessParentChanged( GDispatcher dispatcher, GComponent parent );
    public void treeProcessAncestorMoved( GComponent source );
    public void treeProcessAncestorResized( GComponent source );
    public void treeProcessParentShown();
    public void treeProcessParentHidden();
    
    /**
     * Methods that should only be called by dispatcher.
     */
    public void treeProcessLayout();
    public void processPaint( GGraphics g );
    public void processComponentEvent( GComponentEvent e );
    public void processAncestorEvent( GAncestorEvent e );
    public void processFocusEvent( GFocusEvent e );
    public void processMouseEvent( GMouseEvent e );
    public void processMouseMotionEvent( GMouseEvent e );
    public void processMouseWheelEvent( GMouseWheelEvent e );
    public void processKeyEvent( GKeyEvent e );
    
}
