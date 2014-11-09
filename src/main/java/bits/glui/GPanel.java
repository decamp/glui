package bits.glui;

import java.awt.Font;
import java.awt.event.*;
import java.util.*;

import static javax.media.opengl.GL.*;
import javax.media.opengl.GL;

import bits.glui.event.*;


/**
 * @author decamp
 */
public class GPanel implements GComponent {

    private static final GColor DEFAULT_FOREGROUND = GColor.WHITE;
    private static final GColor DEFAULT_BACKGROUND = null;
    private static final Font   DEFAULT_FONT       = new Font( "Verdana", Font.PLAIN, 12 );

    private GDispatcher mDispatcher = null;
    private GComponent  mParent     = null;
    private final List<GComponent> mChildren;
    private final List<GComponent> mSafeChildren;

    private GLayout mLayout = null;

    private int  mX              = 0;
    private int  mY              = 0;
    private int  mW              = 1;
    private int  mH              = 1;
    private Rect mAbsoluteBounds = Rect.fromBounds( 0, 0, 1, 1 );

    private GColor mForeground = DEFAULT_FOREGROUND;
    private GColor mBackground = DEFAULT_BACKGROUND;
    private Font   mFont       = DEFAULT_FONT;

    private boolean mDisplayed = false;
    private boolean mVisible   = true;
    private boolean mEnabled   = true;
    private boolean mHasFocus  = false;

    private boolean mNeedsPaint  = false;
    private boolean mNeedsLayout = false;

    private GComponentListener   mComponentCaster   = null;
    private GAncestorListener    mAncestorCaster    = null;
    private GFocusListener       mFocusCaster       = null;
    private GPaintListener       mPaintCaster       = null;
    private GMouseListener       mMouseCaster       = null;
    private GMouseMotionListener mMouseMotionCaster = null;
    private GMouseWheelListener  mMouseWheelCaster  = null;
    private GKeyListener         mKeyCaster         = null;


    public GPanel() {
        this( new ArrayList<GComponent>() );
    }


    protected GPanel( List<GComponent> children ) {
        mChildren = children;
        mSafeChildren = Collections.unmodifiableList( mChildren );
    }


    @Override
    public synchronized void addChild( GComponent child ) {
        if( mChildren.contains( child ) ) {
            return;
        }
        
        mChildren.add( child );
        childAdded( child );
    }
    
    @Override
    public synchronized void removeChild( GComponent child ) {
        if( mChildren.remove( child ) ) {
            childRemoved( child );
        }
    }
    
    @Override
    public synchronized void clearChildren() {
        if( mChildren.isEmpty() ) {
            return;
        }
        
        for( GComponent p : mChildren ) {
            childRemoved( p );
        }
        
        mChildren.clear();
    }
    
    @Override
    public List<GComponent> children() {
        return mSafeChildren;
    }

    @Override
    public void setLayout( GLayout layout ) {
        mLayout = layout;
    }

    @Override
    public GComponent parent() {
        return mParent;
    }
    
    
    @Override
    public Rect bounds() {
        return Rect.fromBounds( mX, mY, mW, mH );
    }

    @Override
    public Rect absoluteBounds() {
        Rect ret = mAbsoluteBounds;
        if( ret != null ) {
            return ret;
        }
        
        GComponent parent = mParent;
        if( parent != null ) {
            ret = parent.absoluteBounds();
        }
        
        synchronized( this ) {
            if( ret == null ) {
                ret = Rect.fromBounds( mX, mY, mW, mH );
            } else {
                ret = Rect.fromBounds( ret.x() + mX, ret.y() + mY, mW, mH );
            }
            
            mAbsoluteBounds = ret;
        }
        
        return ret;
    }

    @Override
    public int x() {
        return mX;
    }

    @Override
    public int y() {
        return mY;
    }

    @Override
    public int width() {
        return mW;
    }

    @Override
    public int height() {
        return mH;
    }
    
    @Override
    public boolean contains( int x, int y ) {
        return x >= 0 && y >= 0 && x < mW && y < mH;
    }
    
    @Override
    public GComponent position( int x, int y ) {
        return bounds( x, y, width(), height() );
    }
    
    @Override
    public GComponent size( int w, int h ) {
        return bounds( x(), y(), w, h );
    }
    
    @Override
    public synchronized GComponent bounds( int x, int y, int w, int h ) {
        boolean moved   = x != mX || y != mY;
        boolean resized = w != mW || h != mH;
        
        if( moved || resized ) {
            mX = x;
            mY = y;
            mW = w;
            mH = h;
            mAbsoluteBounds = null;
            
            if( moved ) {
                treeProcessAncestorMoved( this );
            }
            
            if( resized ) {
                treeProcessAncestorResized( this );
                applyLayout();
            }
        }
        
        return this;
    }
    
    @Override
    public synchronized void setVisible( boolean visible ) {
        if( visible == mVisible ) {
            return;
        }

        mVisible = visible;
        if( visible ) {
            treeProcessParentShown();
        } else {
            treeProcessParentHidden();
        }
    }
    
    @Override
    public boolean isVisible() {
        return mVisible;
    }
    
    @Override
    public synchronized void setEnabled( boolean enable ) {
        if( enable == mEnabled ) {
            return;
        }
        
        mEnabled = enable;
        
        if( mDispatcher != null ) {
            mDispatcher.firePropertyChange( this, PROP_ENABLED, !enable, enable );
        }
    }
    
    @Override
    public boolean isEnabled() {
        return mEnabled;
    }
    
    @Override
    public boolean isDisplayed() {
        return mDisplayed;
    }
    
    
    @Override
    public GPanel font( Font font ) {
        mFont = font == null ? DEFAULT_FONT : font;
        return this;
    }

    @Override
    public Font font() {
        return mFont;
    }

    @Override
    public GPanel foreground( GColor foreground ) {
        mForeground = foreground == null ? DEFAULT_FOREGROUND : foreground;
        return this;
    }

    @Override
    public GColor foreground() {
        return mForeground;
    }

    @Override
    public GPanel background( GColor background ) {
        mBackground = background;
        return this;
    }

    @Override
    public GColor background() {
        return mBackground;
    }

    
    @Override
    public synchronized void addComponentListener( GComponentListener listener ) {
        mComponentCaster = GluiMulticaster.add( mComponentCaster, listener );
    }
    
    @Override
    public synchronized void removeComponentListener( GComponentListener listener ) {
        mComponentCaster = GluiMulticaster.remove( mComponentCaster, listener );
    }
    
    @Override
    public synchronized void addAncestorListener( GAncestorListener listener ) {
        mAncestorCaster = GluiMulticaster.add( mAncestorCaster, listener );
    }
    
    @Override
    public synchronized void removeAncestorListener( GAncestorListener listener ) {
        mAncestorCaster = GluiMulticaster.remove( mAncestorCaster, listener );
    }
    
    @Override
    public synchronized void addFocusListener( GFocusListener listener ) {
        mFocusCaster = GluiMulticaster.add( mFocusCaster, listener );
    }
    
    @Override
    public synchronized void removeFocusListener( GFocusListener listener ) {
        mFocusCaster = GluiMulticaster.remove( mFocusCaster, listener );
    }
    
    @Override
    public synchronized void addMouseListener( GMouseListener listener ) {
        boolean prev = hasMouseListener();
        mMouseCaster = GluiMulticaster.add( mMouseCaster, listener );
        if( mDispatcher != null && prev != hasMouseListener() ) {
            mDispatcher.firePropertyChange( this, PROP_HAS_MOUSE_LISTENER, false, true );
        }
    }
    
    @Override
    public synchronized void removeMouseListener( GMouseListener listener ) {
        boolean prev = hasMouseListener();
        mMouseCaster = GluiMulticaster.remove( mMouseCaster, listener );
        if( mDispatcher != null && prev != hasMouseListener() ) {
            mDispatcher.firePropertyChange( this, PROP_HAS_MOUSE_LISTENER, true, false );
        }
    }
    
    @Override
    public synchronized void addMouseMotionListener( GMouseMotionListener listener ) {
        boolean prev = hasMouseListener();
        mMouseMotionCaster = GluiMulticaster.add( mMouseMotionCaster, listener );
        if( mDispatcher != null && prev != hasMouseListener() ) {
            mDispatcher.firePropertyChange( this, PROP_HAS_MOUSE_LISTENER, false, true );
        }
    }
    
    @Override
    public synchronized void removeMouseMotionListener( GMouseMotionListener listener ) {
        boolean prev = hasMouseListener();
        mMouseMotionCaster = GluiMulticaster.remove( mMouseMotionCaster, listener );
        if( mDispatcher != null && prev != hasMouseListener() ) {
            mDispatcher.firePropertyChange( this, PROP_HAS_MOUSE_LISTENER, true, false );
        }
    }

    @Override
    public synchronized void addMouseWheelListener( GMouseWheelListener listener ) {
        boolean prev = hasMouseListener();
        mMouseWheelCaster = GluiMulticaster.add( mMouseWheelCaster, listener );
        if( mDispatcher != null && prev != hasMouseListener() ) {
            mDispatcher.firePropertyChange( this, PROP_HAS_MOUSE_LISTENER, false, true );
        }
    }

    @Override
    public synchronized void removeMouseWheelListener( GMouseWheelListener listener ) {
        boolean prev = hasMouseListener();
        mMouseWheelCaster = GluiMulticaster.remove( mMouseWheelCaster, listener );
        if( mDispatcher != null && prev != hasMouseListener() ) {
            mDispatcher.firePropertyChange( this, PROP_HAS_MOUSE_LISTENER, true, false );
        }
    }

    @Override
    public synchronized void addKeyListener( GKeyListener listener ) {
        boolean prev = hasKeyListener();
        mKeyCaster = GluiMulticaster.add( mKeyCaster, listener );
        if( mDispatcher != null && prev != hasKeyListener() ) {
            mDispatcher.firePropertyChange( this, PROP_HAS_KEY_LISTENER, false, true );
        }
    }
    
    @Override
    public synchronized void removeKeyListener( GKeyListener listener ) {
        boolean prev = hasKeyListener();
        mKeyCaster = GluiMulticaster.remove( mKeyCaster, listener );
        if( mDispatcher != null && prev != hasKeyListener() ) {
            mDispatcher.firePropertyChange( this, PROP_HAS_KEY_LISTENER, true, false );
        }
    }

    @Override
    public synchronized void addPaintListener( GPaintListener listener ) {
        mPaintCaster = GluiMulticaster.add( mPaintCaster, listener );
    }

    @Override
    public synchronized void removePaintListener( GPaintListener listener ) {
        mPaintCaster = GluiMulticaster.remove( mPaintCaster, listener );
    }
    
    
    @Override
    public synchronized boolean hasKeyListener() {
        return mKeyCaster != null;
    }
    
    @Override
    public synchronized boolean hasMouseListener() {
        return mMouseCaster != null ||
               mMouseMotionCaster != null ||
               mMouseWheelCaster != null;
    }
    
    
    @Override
    public boolean hasFocus() {
        return mHasFocus;
    }
    
    @Override
    public synchronized boolean requestFocus() {
        if( mDispatcher == null || !GToolkit.isKeyboardFocusable( this ) ) {
            return false;
        }
                
        mDispatcher.fireRequestFocus( this );
        return true;
    }
    
    @Override
    public void transferFocusBackward() {
        GDispatcher d = mDispatcher;
        if( d != null ) {
            d.fireTransferFocusBackward( this );
        }
    }
    
    @Override
    public void transferFocusForward() {
        GDispatcher d = mDispatcher;
        if( d != null ) {
            d.fireTransferFocusForward( this );
        }
    }
    
    @Override
    public void startModal() {
        GDispatcher d = mDispatcher;
        if( d != null ) {
            d.firePushInputRoot( this );
        }
    }
    
    @Override
    public void stopModal() {
        GDispatcher d = mDispatcher;
        if( d != null ) {
            d.firePopInputRoot( this );
        }
    }
    
    
    @Override
    public synchronized GComponent componentAt( int x, int y ) {
        if( !contains( x, y ) ) {
            return null;
        }

        int size = mChildren.size();

        while( size-- > 0 ) {
            GComponent child = mChildren.get( size );
            GComponent ret = child.componentAt( x - child.x(), y - child.y() );
            if( ret != null ) {
                return ret;
            }
        }
        
        return this;
    }
    
    @Override
    public synchronized GComponent displayedComponentAt( int x, int y ) {
        if( !mDisplayed || !contains( x, y ) ) {
            return null;
        }

        int size = mChildren.size();
        while( size-- > 0 ) {
            GComponent child = mChildren.get( size );
            GComponent ret   = child.displayedComponentAt( x - child.x(), y - child.y() );
            if( ret != null ) {
                return ret;
            }
        }

        return this;
    }
    
    @Override
    public synchronized GComponent mouseFocusableComponentAt( int x, int y ) {
        if( !mDisplayed || !contains( x, y ) ) {
            return null;
        }
        
        int size = mChildren.size();
        while( size-- > 0 ) {
            GComponent child = mChildren.get( size );
            GComponent ret   = child.mouseFocusableComponentAt( x - child.x(), y - child.y() );
            if( ret != null ) {
                return ret;
            }
        }
        
        return GToolkit.isMouseFocusable( this ) ? this : null;
    }
    
    @Override
    public synchronized void applyLayout() {
        if( mNeedsLayout || mLayout == null && mChildren.isEmpty() ) {
            return;
        }
        
        mNeedsLayout = true;
        
        if( mDispatcher != null ) {
            mDispatcher.fireLayout( this );
        }
    }

    @Override
    public boolean needsLayout() {
        return mNeedsLayout;
    }

    @Override
    public synchronized void repaint() {
        if( mNeedsPaint || mDispatcher == null ) {
            return;
        }
        mNeedsPaint = true;
        mDispatcher.firePaint( this );
    }
    
    @Override
    public boolean needsRepaint() {
        return mNeedsPaint;
    }

    @Override
    public GDispatcher dispatcher() {
        return mDispatcher;
    }
    
    
    @Override
    public synchronized void treeProcessParentChanged( GDispatcher dispatcher, GComponent parent ) {
        if( dispatcher == mDispatcher && parent == mParent ) {
            return;
        }

        GDispatcher out = dispatcher != null ? dispatcher : mDispatcher;
        mDispatcher     = dispatcher;
        mParent         = parent;
        mAbsoluteBounds = null;
        
        // Notify ancestor has changed.
        if( out != null && mAncestorCaster != null ) {
            GAncestorEvent e = new GAncestorEvent( this, GAncestorEvent.ANCESTOR_CHANGED, parent );
            out.fireAncestorEvent( e );
        }
        
        updateDisplayed( out );
        mNeedsLayout = false;
        mNeedsPaint  = false;
        applyLayout();
        
        if( mDisplayed ) {
            repaint();
        }
        
        mDispatcher = dispatcher;
        if( !mChildren.isEmpty() ) {
            for( GComponent c: mChildren ) {
                c.treeProcessParentChanged( dispatcher, this );
            }
        }
    }
    
    @Override
    public synchronized void treeProcessAncestorMoved( GComponent source ) {
        mAbsoluteBounds = null;
    
        if( mDispatcher != null ) {
            if( source == this ) {
                if( mComponentCaster != null ) {
                    GComponentEvent e = new GComponentEvent( this, GComponentEvent.COMPONENT_MOVED );
                    mDispatcher.fireComponentEvent( e );
                }
            } else if( mAncestorCaster != null ) {
                GAncestorEvent e = new GAncestorEvent( this, GAncestorEvent.ANCESTOR_MOVED, source );
                mDispatcher.fireAncestorEvent( e );
            }
        }
        
        if( !mChildren.isEmpty() ) {
            for( GComponent c: mChildren ) {
                c.treeProcessAncestorMoved( source );
            }
        }
    }
    
    @Override
    public synchronized void treeProcessAncestorResized( GComponent source ) {
        mAbsoluteBounds = null;
        
        if( mDispatcher != null ) {
            if( source == this ) {
                if( mComponentCaster != null ) {
                    GComponentEvent e = new GComponentEvent( this, GComponentEvent.COMPONENT_RESIZED );
                    mDispatcher.fireComponentEvent( e );
                }
            } else if( mAncestorCaster != null ) {
                GAncestorEvent e = new GAncestorEvent( this, GAncestorEvent.ANCESTOR_RESIZED, source );
                mDispatcher.fireAncestorEvent( e );
            }
        } 
        
        if( !mChildren.isEmpty() ) {
            for( GComponent c: mChildren ) {
                c.treeProcessAncestorResized( source );
            }
        }
    }
    
    @Override
    public synchronized void treeProcessParentShown() {
        if( updateDisplayed() && !mChildren.isEmpty() ) {
            for( GComponent c: mChildren ) {
                c.treeProcessParentShown();
            }
        }
    }
    
    @Override
    public synchronized void treeProcessParentHidden() {
        if( updateDisplayed() && !mChildren.isEmpty() ) { 
            for( GComponent c: mChildren ) {
                c.treeProcessParentHidden();
            }
        }
    }
    
    
    @Override
    public void treeProcessLayout() {
        GLayout m;
        
        synchronized( this ) {
            m = mLayout;
            mNeedsLayout = false;
        }
        
        if( m != null ) {
            m.layoutPane( this );
        }
        
        synchronized( this ) {
            for( GComponent p: mChildren ) {
                p.treeProcessLayout();
            }
        }
    }
    
    @Override
    public void processPaint( GGraphics g ) {
        if( !mDisplayed ) {
            return;
        }
        mNeedsPaint = false;
        paintComponent( g );
        paintChildren( g );
    }
    
    @Override
    public void processComponentEvent( GComponentEvent e ) {
        GComponentListener c = mComponentCaster;
        if( c == null ) {
            return;
        }

        switch( e.id() ) {
        case GComponentEvent.COMPONENT_MOVED:
            c.componentMoved( e );
            break;

        case GComponentEvent.COMPONENT_RESIZED:
            c.componentResized( e );
            break;

        case GComponentEvent.COMPONENT_SHOWN:
            c.componentShown( e );
            break;

        case GComponentEvent.COMPONENT_HIDDEN:
            c.componentHidden( e );
            break;
        }
    }
    
    @Override
    public void processAncestorEvent( GAncestorEvent e ) {
        GAncestorListener c = mAncestorCaster;
        if( c == null ) {
            return;
        }
        
        switch( e.id() ) {
        case GAncestorEvent.ANCESTOR_CHANGED:
            c.ancestorChanged( e );
            break;
            
        case GAncestorEvent.ANCESTOR_MOVED:
            c.ancestorMoved( e );
            break;
            
        case GAncestorEvent.ANCESTOR_RESIZED:
            c.ancestorResized( e );
            break;
        }
    }
    
    @Override
    public void processFocusEvent( GFocusEvent e ) {
        GFocusListener f = mFocusCaster;
        
        switch( e.id() ) {
        case GFocusEvent.FOCUS_GAINED:
            mHasFocus = true;
            if( f != null ) {
                f.focusGained( e );
            }
            break;
        case GFocusEvent.FOCUS_LOST:
            mHasFocus = false;
            if( f != null ) {
                f.focusLost( e );
            }
            break;
        }
    }

    @Override
    public void processMouseEvent( GMouseEvent e ) {
        GMouseListener m = mMouseCaster;
        if( m == null ) {
            return;
        }

        switch( e.id() ) {
        case GMouseEvent.MOUSE_PRESSED:
            m.mousePressed( e );
            break;
        case GMouseEvent.MOUSE_RELEASED:
            m.mouseReleased( e );
            break;
        case GMouseEvent.MOUSE_CLICKED:
            m.mouseClicked( e );
            break;
        case GMouseEvent.MOUSE_ENTERED:
            m.mouseEntered( e );
            break;
        case GMouseEvent.MOUSE_EXITED:
            m.mouseExited( e );
            break;
        }

        e.consume();
    }

    @Override
    public void processMouseMotionEvent( GMouseEvent e ) {
        GMouseMotionListener m = mMouseMotionCaster;
        if( m == null ) {
            return;
        }

        if( e.id() == MouseEvent.MOUSE_MOVED ) {
            m.mouseMoved( e );
        } else {
            m.mouseDragged( e );
        }

        e.consume();
    }
    
    @Override
    public void processMouseWheelEvent( GMouseWheelEvent e ) {
        GMouseWheelListener m = mMouseWheelCaster;
        if( m == null ) {
            return;
        }

        m.mouseWheelMoved( e );
        e.consume();
    }
    
    @Override
    public void processKeyEvent( GKeyEvent e ) {
        GKeyListener m = mKeyCaster;
        if( m == null ) {
            return;
        }

        switch( e.id() ) {
        case GKeyEvent.KEY_PRESSED:
            m.keyPressed( e );
            break;
        case GKeyEvent.KEY_RELEASED:
            m.keyReleased( e );
            break;
        case GKeyEvent.KEY_TYPED:
            m.keyTyped( e );
            break;
        }

        e.consume();
    }
    
    
    protected void paintComponent( GGraphics g ) {
        GPaintListener c = mPaintCaster;
        if( c != null ) {
            c.paint( g );
        }
    }


    protected void paintChildren( GGraphics g ) {
        if( mChildren.isEmpty() ) {
            return;
        }
        
        for( GComponent p : mChildren ) {
            if( p.isDisplayed() ) {
                prepareView( g, p );
                p.processPaint( g );
            }
        }
    }


    protected void prepareView( GGraphics g, GComponent p ) {
        GL gl = g.gl();

        Rect b = p.absoluteBounds();
        Rect viewport = g.contextViewport();

        int x = b.x() - viewport.x();
        int y = b.y() - viewport.y();
        int w = b.width();
        int h = b.height();

        gl.glMatrixMode( GL_PROJECTION );
        gl.glLoadIdentity();
        gl.glOrtho( 0, w, 0, h, -1, 1 );
        gl.glMatrixMode( GL_MODELVIEW );
        gl.glLoadIdentity();

        // gl.glTranslated(b.minX(), b.minY(), 0.0);
        // gl.glViewport((int)b.minX(), (int)b.minY(), (int)b.spanX(),
        // (int)b.spanY());
        gl.glViewport( x, y, w, h );
        // gl.glScissor(x, y, w, h);
    }


    protected void childAdded( GComponent child ) {
        child.treeProcessParentChanged( mDispatcher, this );
        applyLayout();        
    }


    protected void childRemoved( GComponent child ) {
        child.treeProcessParentChanged( null, null );
        applyLayout();
    }
    
    
    private synchronized boolean updateDisplayed() {
        return updateDisplayed( mDispatcher );
    }


    private boolean updateDisplayed( GDispatcher out ) {
        boolean displayed = mDispatcher!= null &&
                            mVisible &&
                            ( mParent == null || mParent.isDisplayed() );
        
        if( mDisplayed == displayed ) {
            return false;
        }
        
        mDisplayed = displayed;
        
        if( out != null ) {
            if( mComponentCaster != null ) {
                int id = displayed ? GComponentEvent.COMPONENT_SHOWN : GComponentEvent.COMPONENT_HIDDEN;
                out.fireComponentEvent( new GComponentEvent( this, id ) );
            }
            
            out.firePropertyChange( this, PROP_DISPLAYED, !displayed, displayed );
        }
        
        return true;
    }
        
}