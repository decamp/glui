package cogmac.glui;

import java.awt.Component;
import java.awt.event.*;
import java.util.*;
import cogmac.glui.event.*;


/**
 * @author decamp
 */
class EventProcessor {

    
    private static final int ALL_BUTTON_MASK = MouseEvent.BUTTON1_DOWN_MASK |
                                               MouseEvent.BUTTON2_DOWN_MASK |
                                               MouseEvent.BUTTON3_DOWN_MASK;
    
    private static final int[] BUTTON_MASKS = { GMouseEvent.BUTTON1_DOWN_MASK,
                                                GMouseEvent.BUTTON2_DOWN_MASK,
                                                GMouseEvent.BUTTON3_DOWN_MASK };

    private static final int[] BUTTON_IDS = { GMouseEvent.BUTTON1,
                                              GMouseEvent.BUTTON2,
                                              GMouseEvent.BUTTON3 };

    private final Component  mAwtOwner;
    private final GComponent mOwner;
    
    private GComponent mInputRoot = null;
    private GComponent mFocus     = null;
    private final Stack<InputFrame> mInputStack = new Stack<InputFrame>();
    
    private int mMouseX    = Integer.MIN_VALUE;
    private int mMouseY    = Integer.MIN_VALUE;
    private int mMouseMods = 0;
    
    private GComponent mMouseLocation    = null;
    private GComponent mMouseButtonFocus = null;
    private GComponent mMouseClickFocus  = null;
    
    
    private GFocusTraversalPolicy mFocusPolicy = new GChildOrderTraversalPolicy();

    
    
    EventProcessor( Component awtOwner, GComponent owner ) {
        mAwtOwner  = awtOwner;
        mOwner     = owner;
        mInputRoot = owner;
    }
    
    
    public void validate() {
        validateRoot();
        validateFocus( mOwner, false );
        validateMouse();
    }
    

    public void processMousePressedEvent( MouseEvent e, int ex, int ey ) {
        cache( e, ex, ey );

        if( mMouseButtonFocus == null ) {
            mMouseButtonFocus = mMouseLocation;
        }

        if( mMouseButtonFocus != null ) {
            resendMouse( e, MouseEvent.MOUSE_PRESSED, mMouseButtonFocus );
        } else {
            // Mouse has been pressed over no focusable component,
            // so nothing will receive click events.
            mMouseClickFocus = null;
        }
    }
    
    
    public void processMouseReleasedEvent( MouseEvent e, int ex, int ey ) {
        cache( e, ex, ey );
        
        if( mMouseButtonFocus != null ) {
            resendMouse( e, MouseEvent.MOUSE_RELEASED, mMouseButtonFocus );

            if( ( e.getModifiersEx() & ALL_BUTTON_MASK) == 0 ) {
                mMouseClickFocus = mMouseButtonFocus;
                mMouseButtonFocus = null;
            }
        }
    }


    public void processMouseClickedEvent( MouseEvent e, int ex, int ey ) {
        cache( e, ex, ey );

        if( mMouseClickFocus != null ) {
            resendMouse( e, MouseEvent.MOUSE_CLICKED, mMouseClickFocus );
        }
    }


    public void processMouseEnteredEvent( MouseEvent e, int ex, int ey ) {
        cache( e, ex, ey );
        updateMouseLocation( e );
    }
    
    
    public void processMouseExitedEvent( MouseEvent e, int ex, int ey ) {
        cache( e, ex, ey );
        setMouseLocationComponent( e, null );
    }
    
    
    public void processMouseMovedEvent( MouseEvent e, int ex, int ey ) {
        cache( e, ex, ey );

        if( updateMouseLocation( e ) ) {
            resendMouseMotion( e, -1, mMouseLocation );
        }
    }


    public void processMouseDraggedEvent( MouseEvent e, int ex, int ey ) {
        cache( e, ex, ey );

        updateMouseLocation( e );

        if( mMouseButtonFocus != null ) {
            resendMouseMotion( e, -1, mMouseButtonFocus );
        }
    }


    public void processMouseWheelMovedEvent( MouseWheelEvent e, int ex, int ey ) {
        cache( e, ex, ey );

        if( mMouseLocation != null ) {
            resendMouseWheel( e, -1, mMouseLocation );
        }
    }


    public void processKeyPressedEvent( KeyEvent e ) {
        GComponent focus = mFocus;
        if( focus != null ) {
            resendKey( e, -1, focus );
        }
    }
    
    
    public void processKeyReleasedEvent( KeyEvent e ) {
        GComponent focus = mFocus;
        if( focus != null ) {
            resendKey( e, -1, focus );
        }
    }


    public void processKeyTypedEvent( KeyEvent e ) {
        GComponent focus = mFocus;
        if( focus != null ) {
            resendKey( e, -1, focus );
        }
    }


    
    public void processPaint( GComponent source ) {
        if( mAwtOwner != null ) {
            mAwtOwner.repaint();
        }
    }
    
    
    public void processLayout( GComponent source ) {
        source.treeProcessLayout();
    }
    
    
    public void processRequestFocus( GComponent source ) {
        GComponent focus = mFocus;

        if( mAwtOwner != null && !mAwtOwner.hasFocus() ) {
            mAwtOwner.requestFocus();
        }

        if( focus == source ) {
            return;
        }

        if( source == null || isChild( mInputRoot, source ) ) {
            changeFocus( source, source, false );
        }
    }
    
    
    public void processTransferFocusBackward( GComponent source ) {
        GComponent focus = mFocus;
        GComponent root = mInputRoot;

        if( focus != source )
            return;

        GComponent target = mFocusPolicy.getComponentBefore( root, focus );

        if( target != focus ) {
            changeFocus( source, target, false );
        }
    }
    
    
    public void processTransferFocusForward( GComponent source ) {
        GComponent focus = mFocus;
        GComponent root = mInputRoot;

        if( focus != source )
            return;

        GComponent target = mFocusPolicy.getComponentAfter( root, focus );

        if( target != focus ) {
            changeFocus( source, target, false );
        }
    }


    public void processPushInputRoot( GComponent source ) {
        GComponent root = mInputRoot;

        if( source == null || source == root || !isChild( root, source ) )
            return;

        mInputStack.push( new InputFrame( root, mFocus ) );
        mInputRoot = source;

        // Update focus.
        initFocus( source, true );
        
        // Update mouse focus.
        validateMouse();
    }


    public void processPopInputRoot( GComponent source ) {
        int frame = mInputStack.size() - 1;
        if( frame < 0 )
            return;

        if( source != mInputRoot ) {
            while( frame > 0 ) {
                if( mInputStack.get( frame ).mRoot == source )
                    break;

                frame--;
            }

            if( frame == 0 )
                return;

            frame--;
        }

        // Pop input stack until desired frame is reached.
        while( mInputStack.size() > frame ) {
            InputFrame f = mInputStack.pop();
            mInputRoot = f.mRoot;

            changeFocus( source, f.mFocus, false );
        }

        // Update mouse focus.
        validateMouse();
    }
    
    
    public void processPropertyChange( GComponent source, String prop, Object oldValue, Object newValue ) {
        if( prop == GComponent.PROP_DISPLAYED || prop == GComponent.PROP_ENABLED ) {
            validateMouse();
            validateFocus( source, false ); 
        } else if( prop == GComponent.PROP_HAS_MOUSE_LISTENER ) {
            validateMouse();
        } else if( prop == GComponent.PROP_HAS_KEY_LISTENER ) {
            validateFocus( source, false );
        }
    }
    
    
    
    
    private void validateRoot() {
        while( !isChild( mOwner, mInputRoot ) && !mInputStack.isEmpty() ) {
            InputFrame f = mInputStack.pop();
            mInputRoot = f.mRoot;
            changeFocus( mOwner, f.mFocus, false );
        }
    }
    
    
    private void validateFocus( GComponent source, boolean temporary ) {
        GComponent root  = mInputRoot;
        GComponent focus = mFocus;
        
        if( focus == null || !GToolkit.isKeyboardFocusable( focus ) || !isChild( root, focus ) ) {
            initFocus( source, temporary );
        }
    }
    
    
    private void validateMouse() {
        GComponent root  = mInputRoot;
        GComponent focus = mMouseButtonFocus;
        
        if( focus != null && ( !GToolkit.isMouseFocusable( focus ) || !isChild( root, focus ) ) ) {
            forceMouseRelease( focus );
            mMouseButtonFocus = null;
            mMouseClickFocus  = null;
        }
        
        initMouseLocation();
        
        //focus = mMouseLocation;
        //
        //if( focus == null || ( !GToolkit.isMouseFocusable( focus ) || !isChild( root, focus ) ) ) {
        //    initMouseLocation();
        //}
    }
    
    
    private void initFocus( GComponent source, boolean temporary ) {
        GComponent target = mInputRoot;
        target = mFocusPolicy.getDefaultComponent( target );

        if( target != mFocus ) {
            changeFocus( source, target, temporary );
        }
    }


    private void changeFocus( GComponent source, GComponent target, boolean temporary ) {
        GComponent c = mFocus;
        if( c == target )
            return;

        if( source == null )
            source = mOwner;

        mFocus = null;

        GFocusEvent e;

        if( c != null ) {
            e = new GFocusEvent( source, GFocusEvent.FOCUS_LOST, temporary, target );
            c.processFocusEvent( e );
        }

        if( target != null ) {
            e = new GFocusEvent( source, GFocusEvent.FOCUS_GAINED, temporary, c );
            target.processFocusEvent( e );
        }

        mFocus = target;
    }



    private void cache( MouseEvent e, int ex, int ey ) {
        mMouseX = ex;
        mMouseY = ey;
        mMouseMods = e.getModifiers() | e.getModifiersEx();
    }


    private void sendMouse( int id, GComponent source ) {
        Box bounds = source.absoluteBounds();

        GMouseEvent e = new GMouseEvent( source,
                id,
                System.currentTimeMillis() * 1000L,
                mMouseMods,
                mMouseX - bounds.x(),
                mMouseY - bounds.y(),
                0,
                false,
                0 );

        source.processMouseEvent( e );
    }


    
    
    private void forceMouseRelease( GComponent source ) {
        Box bounds = source.absoluteBounds();
        int ex     = mMouseX - bounds.x();
        int ey     = mMouseY - bounds.y();
        int mods   = mMouseMods;
        long when  = System.currentTimeMillis() * 1000L;
        
        for( int i = 0; i < BUTTON_MASKS.length; i++ ) {
            if( ( mMouseMods & BUTTON_MASKS[i] ) != 0 ) {
                mods &= ~BUTTON_MASKS[i];
                
                GMouseEvent e = new GMouseEvent( source,
                        GMouseEvent.MOUSE_RELEASED,
                        when,
                        mods,
                        ex,
                        ey,
                        0,
                        false,
                        BUTTON_IDS[i] );
            }
        }
    }


    private void resendMouse( MouseEvent e, int newId, GComponent source ) {
        Box bounds = source.absoluteBounds();

        GMouseEvent e2 = new GMouseEvent( source,
                newId < 0 ? e.getID() : newId,
                e.getWhen() * 1000L,
                mMouseMods,
                mMouseX - bounds.x(),
                mMouseY - bounds.y(),
                e.getClickCount(),
                e.isPopupTrigger(),
                e.getButton() );

        source.processMouseEvent( e2 );

        if( e2.isConsumed() ) {
            e.consume();
        }
    }


    private void resendMouseMotion( MouseEvent e, int newId, GComponent source ) {
        Box bounds = source.absoluteBounds();

        GMouseEvent e2 = new GMouseEvent( source,
                newId < 0 ? e.getID() : newId,
                e.getWhen() * 1000L,
                mMouseMods,
                mMouseX - bounds.x(),
                mMouseY - bounds.y(),
                e.getClickCount(),
                e.isPopupTrigger(),
                e.getButton() );

        source.processMouseMotionEvent( e2 );

        if( e2.isConsumed() ) {
            e.consume();
        }
    }
    
    
    private void resendMouseWheel( MouseWheelEvent e, int newId, GComponent source ) {
        Box bounds = source.absoluteBounds();


        GMouseWheelEvent e2 = new GMouseWheelEvent( source,
                newId < 0 ? e.getID() : newId,
                e.getWhen() * 1000L,
                mMouseMods,
                mMouseX - bounds.x(),
                mMouseY - bounds.y(),
                e.getClickCount(),
                e.isPopupTrigger(),
                e.getScrollType(),
                e.getScrollAmount(),
                e.getWheelRotation() );

        source.processMouseWheelEvent( e2 );

        if( e2.isConsumed() ) {
            e.consume();
        }
    }
    
    
    private void setMouseLocationComponent( MouseEvent e, GComponent comp ) {
        GComponent source = mMouseLocation;

        if( comp == source )
            return;

        if( source != null ) {
            resendMouse( e, MouseEvent.MOUSE_EXITED, source );
        }

        mMouseLocation = comp;

        if( comp != null ) {
            resendMouse( e, MouseEvent.MOUSE_ENTERED, comp );
        }
    }
    
    
    private void setMouseLocationComponent( GComponent comp ) {
        GComponent source = mMouseLocation;

        if( comp == source )
            return;

        if( source != null ) {
            sendMouse( MouseEvent.MOUSE_EXITED, source );
        }

        mMouseLocation = comp;

        if( comp != null ) {
            sendMouse( MouseEvent.MOUSE_ENTERED, comp );
        }
    }
    
    
    private boolean updateMouseLocation( MouseEvent e ) {
        GComponent c = mMouseLocation;
        GComponent root = mInputRoot;
        Box bounds = root.absoluteBounds();

        final int ex = mMouseX;
        final int ey = mMouseY;

        if( c == null ) {
            c = root.mouseFocusableComponentAt( ex - bounds.x(), ey - bounds.y() );
            if( c == null ) {
                mMouseX = Integer.MIN_VALUE;
                return false;
            }

            setMouseLocationComponent( e, c );
            return true;

        } else {
            Box b = c.absoluteBounds();
            int x = ex - b.x();
            int y = ey - b.y();

            GComponent d = c.mouseFocusableComponentAt( x, y );
            if( c == d )
                return true;

            if( d == null )
                d = root.mouseFocusableComponentAt( ex - bounds.x(), ey - bounds.y() );

            setMouseLocationComponent( e, d );
            return d != null;
        }

    }
    
    
    private boolean initMouseLocation() {
        GComponent root = mInputRoot;
        final int ex = mMouseX;
        final int ey = mMouseY;

        if( root == null ) {
            setMouseLocationComponent( null );
            return false;
        }

        Box bounds = root.absoluteBounds();
        GComponent target = root.mouseFocusableComponentAt( ex - bounds.x(), ey - bounds.y() );
        if( target == null ) {
            setMouseLocationComponent( null );
            return false;
        }

        setMouseLocationComponent( target );
        return true;
    }


    private void resendKey( KeyEvent e, int newId, GComponent source ) {
        GKeyEvent e2 = new GKeyEvent( source,
                newId < 0 ? e.getID() : newId,
                e.getWhen() * 1000L,
                e.getModifiers() | e.getModifiersEx(),
                e.getKeyCode(),
                e.getKeyChar(),
                e.getKeyLocation() );

        source.processKeyEvent( e2 );

        if( e2.isConsumed() ) {
            e.consume();
        }
    }


    private boolean isChild( GComponent root, GComponent comp ) {
        if( root == null )
            return false;

        while( comp != null ) {
            if( comp == root )
                return true;

            comp = comp.parent();
        }

        return false;
    }



    private static class InputFrame {

        final GComponent mRoot;
        final GComponent mFocus;

        InputFrame( GComponent root, GComponent focus ) {
            mRoot = root;
            mFocus = focus;
        }

    }

}
