/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui;

import java.awt.Component;
import java.awt.event.*;
import java.util.*;

import bits.draw3d.Rect;
import bits.glui.event.*;


/**
 * @author decamp
 */
class EventProcessor implements GHumanInputController {

    private static final int ALL_BUTTONS_MASK = MouseEvent.BUTTON1_DOWN_MASK |
                                                MouseEvent.BUTTON2_DOWN_MASK |
                                                MouseEvent.BUTTON3_DOWN_MASK;

    private static final long DOUBLE_CLICK_TIMEOUT = 500000L;


    private final Component             mAwtOwner;
    private final GComponent            mOwner;
    private final GKeyboardFocusManager mFocusMan;

    private final ModifierState   mMods;
    private final KeyController   mKeyCont;
    private final MouseController mMouseCont;

    private final Stack<InputFrame> mInputStack = new Stack<InputFrame>();
    private       GComponent        mRoot       = null;


    EventProcessor( Component awtOwner, GComponent owner, GKeyboardFocusManager focusMan ) {
        mAwtOwner = awtOwner;
        mOwner = owner;
        mRoot = owner;
        mFocusMan = focusMan;

        mMods = new ModifierState();
        mKeyCont = new KeyController( owner, mFocusMan, mMods );
        mMouseCont = new MouseController( mMods, owner );
    }
    
    
    public void validate() {
        InputFrame revert = null;

        while( !GToolkit.isChild( mOwner, mRoot ) && !mInputStack.isEmpty() ) {
            revert = mInputStack.pop();
            mRoot = revert.mRoot;
        }

        if( revert == null ) {
            return;
        }

        mFocusMan.setRoot( revert.mRoot, revert.mFocus );
        mMouseCont.setRoot( revert.mRoot );
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
        mFocusMan.transferFocus( source );
    }


    public void processTransferFocusBackward( GComponent source ) {
        mFocusMan.transferFocusBackward( source );
    }


    public void processTransferFocusForward( GComponent source ) {
        mFocusMan.transferFocusForward( source );
    }


    public void processPushInputRoot( GComponent source ) {
        GComponent root = mRoot;
        if( source == null || source == root || !GToolkit.isChild( root, source ) ) {
            return;
        }

        mInputStack.push( new InputFrame( root, mFocusMan.focusOwner() ) );
        mRoot = source;
        mMouseCont.setRoot( source );
        mFocusMan.setRoot( source, null );
    }


    public void processPopInputRoot( GComponent source ) {
        int idx = mInputStack.size() - 1;
        if( idx < 0 ) {
            return;
        }

        if( source != mRoot ) {
            while( idx > 0 ) {
                if( mInputStack.get( idx ).mRoot == source ) {
                    break;
                }
                idx--;
            }

            if( idx == 0 ) {
                return;
            }
            idx--;
        }

        // Pop input stack until desired frame is reached.
        while( mInputStack.size() > idx ) {
            InputFrame frame = mInputStack.pop();
            mRoot = frame.mRoot;
            mMouseCont.setRoot( frame.mRoot );
            mFocusMan.setRoot( frame.mRoot, frame.mFocus );
        }
    }


    public void processPropertyChange( GComponent source, String prop, Object oldValue, Object newValue ) {
        if( prop == GComponent.PROP_DISPLAYED || prop == GComponent.PROP_ENABLED ) {
            mFocusMan.validate( source );
            mMouseCont.validate();
        } else if( prop == GComponent.PROP_HAS_MOUSE_LISTENER ) {
            mMouseCont.validate();
        } else if( prop == GComponent.PROP_HAS_KEY_LISTENER ) {
            mFocusMan.validate( source );
        }
    }


    @Override
    public boolean keyTyped( long micros, int mods, int keyCode, char keyChar, int keyLoc ) {
        return mKeyCont.keyTyped( micros, mods, keyCode, keyChar, keyLoc );
    }

    @Override
    public boolean keyPressed( int keyCode, char keyChar, int keyLoc ) {
        return mKeyCont.keyPressed( keyCode, keyChar, keyLoc );
    }

    @Override
    public boolean keyPressed( long micros, int mods, int keyCode, char keyChar, int keyLoc ) {
        return mKeyCont.keyPressed( micros, mods, keyCode, keyChar, keyLoc );
    }

    @Override
    public boolean keyReleased( int keyCode, char keyChar, int keyLoc ) {
        return mKeyCont.keyReleased( keyCode, keyChar, keyLoc );
    }

    @Override
    public boolean keyReleased( long micros, int mods, int keyCode, char keyChar, int keyLoc ) {
        return mKeyCont.keyReleased( micros, mods, keyCode, keyChar, keyLoc );
    }

    @Override
    public boolean keyTyped( int keyCode, char keyChar, int keyLoc ) {
        return mKeyCont.keyTyped( keyCode, keyChar, keyLoc );
    }

    @Override
    public boolean mousePressed( int button ) {
        return mMouseCont.mousePressed( button );
    }

    @Override
    public boolean mousePressed( long micros, int mods, int button, boolean trigger ) {
        return mMouseCont.mousePressed( micros, mods, button, trigger );
    }

    @Override
    public boolean mouseReleased( int button, boolean genClick ) {
        return mMouseCont.mouseReleased( button, genClick );
    }

    @Override
    public boolean mouseReleased( long micros, int mods, int button, boolean trigger, boolean genClick ) {
        return mMouseCont.mouseReleased( micros, mods, button, trigger, genClick );
    }

    @Override
    public boolean mouseEntered( int x, int y ) {
        return mMouseCont.mouseEntered( x, y );
    }

    @Override
    public boolean mouseEntered( long micros, int mods, int x, int y ) {
        return mMouseCont.mouseEntered( micros, mods, x, y );
    }

    @Override
    public boolean mouseExited() {
        return mMouseCont.mouseExited();
    }

    @Override
    public boolean mouseExited( long micros, int mods ) {
        return mMouseCont.mouseExited( micros, mods );
    }

    @Override
    public boolean mouseMoved( int x, int y ) {
        return mMouseCont.mouseMoved( x, y );
    }

    @Override
    public boolean mouseMoved( long micros, int mods, int x, int y ) {
        return mMouseCont.mouseMoved( micros, mods, x, y );
    }

    @Override
    public boolean mouseWheelMoved( long micros, int mods, int scrollType, int scrollAmount, int wheelRotation ) {
        return mMouseCont.mouseWheelMoved( micros, mods, scrollType, scrollAmount, wheelRotation );
    }

    @Override
    public boolean mouseWheelMoved( int scrollType, int scrollAmount, int wheelRotation ) {
        return mMouseCont.mouseWheelMoved( scrollType, scrollAmount, wheelRotation );
    }


    private static class InputFrame {
        final GComponent mRoot;
        final GComponent mFocus;

        InputFrame( GComponent root, GComponent focus ) {
            mRoot = root;
            mFocus = focus;
        }
    }


    private static class ModifierState {

        private static final int LOCATION_MASK = 0x1F;

        private static final int KEYS_MASK = GInputEvent.SHIFT_DOWN_MASK |
                                             GInputEvent.CTRL_DOWN_MASK |
                                             GInputEvent.META_DOWN_MASK |
                                             GInputEvent.ALT_DOWN_MASK;

        private static final int BUTTONS_MASK = GInputEvent.BUTTON1_DOWN_MASK |
                                                GInputEvent.BUTTON2_DOWN_MASK |
                                                GInputEvent.BUTTON3_DOWN_MASK;

        // The copy of modal keys currently down, accounting for multiple
        // locations (ie, left and right shift are separate bits).
        // Each int here represents 32 possible locations of a given keyTyped of modal key.
        private int mShifts = 0;
        private int mCtrls  = 0;
        private int mMetas  = 0;
        private int mAlts   = 0;

        // The copy of modal keys currently down, regardless of location.
        private int mKeys = 0;

        // The copy of mouse buttons currently down.
        private int mButtons = 0;


        public int pressButton( int i ) {
            int mask;
            switch( i ) {
            case 0:
                mask = GMouseEvent.BUTTON1_DOWN_MASK;
                break;
            case 1:
                mask = GMouseEvent.BUTTON2_DOWN_MASK;
                break;
            case 2:
                mask = GMouseEvent.BUTTON3_DOWN_MASK;
                break;
            default:
                return mButtons | mKeys;
            }

            mButtons |= mask;
            return mButtons | mKeys;
        }


        public int releaseButton( int i ) {
            int mask;
            switch( i ) {
            case 0:
                mask = GMouseEvent.BUTTON1_DOWN_MASK;
                break;
            case 1:
                mask = GMouseEvent.BUTTON2_DOWN_MASK;
                break;
            case 2:
                mask = GMouseEvent.BUTTON3_DOWN_MASK;
                break;
            default:
                return mButtons | mKeys;
            }

            mButtons &= ~mask;
            return mButtons | mKeys;
        }


        public int buttonModifiers() {
            return mButtons;
        }


        public boolean anyButtonsDown() {
            return mButtons != 0;
        }


        public int mouseEventModifiers() {
            return mKeys | mButtons;
        }


        public int pressKey( int code, int location ) {
            // Ignore key locations beyond range.
            int loc = location & LOCATION_MASK;
            if( loc != location && loc == 0 ) {
                return mKeys;
            }

            switch( code ) {
            case GKeyEvent.VK_SHIFT:
                mShifts |= loc;
                mKeys   |= GInputEvent.SHIFT_DOWN_MASK;
                break;
            case GKeyEvent.VK_CONTROL:
                mCtrls  |= loc;
                mKeys   |= GInputEvent.CTRL_DOWN_MASK;
                break;
            case GKeyEvent.VK_META:
                mMetas  |= loc;
                mKeys   |= GInputEvent.META_DOWN_MASK;
                break;
            case GKeyEvent.VK_ALT:
                mAlts   |= loc;
                mKeys   |= GInputEvent.ALT_DOWN_MASK;
                break;
            default:
            }

            return mKeys;
        }


        public int releaseKey( int code, int location ) {
            // Ignore key locations beyond range.
            int loc = location & LOCATION_MASK;
            if( loc != location && loc == 0 ) {
                return mKeys;
            }

            switch( code ) {
            case GKeyEvent.VK_SHIFT:
                mShifts &= ~loc;
                if( mShifts == 0 ) {
                    mKeys &= ~GInputEvent.SHIFT_DOWN_MASK;
                }
                break;

            case GKeyEvent.VK_CONTROL:
                mCtrls &= ~loc;
                if( mCtrls == 0 ) {
                    mKeys &= ~GInputEvent.CTRL_DOWN_MASK;
                }
                break;

            case GKeyEvent.VK_META:
                mMetas &= ~loc;
                if( mMetas == 0 ) {
                    mKeys &= ~GInputEvent.META_DOWN_MASK;
                }
                break;

            case GKeyEvent.VK_ALT:
                mAlts   |= loc;
                if( mAlts == 0 ) {
                    mKeys &= ~GInputEvent.ALT_DOWN_MASK;
                }
                break;
            default:
            }

            return mKeys;
        }


        public int keyEventModifiers() {
            return mKeys;
        }


        public void setKeyModifiers( int mods ) {
            mKeys = mods & KEYS_MASK;
            mShifts  = 0;
            mCtrls   = 0;
            mMetas   = 0;
            mAlts    = 0;
        }


        public void setMouseModifiers( int mods ) {
            mButtons = mods & BUTTONS_MASK;
        }


        public void setAll( int mods ) {
            setKeyModifiers( mods );
            setMouseModifiers( mods );
        }

    }


    private static class KeyController {

        private final GComponent mRoot;
        private final GKeyboardFocusManager mFocusCont;
        private final ModifierState mMods;


        public KeyController( GComponent root, GKeyboardFocusManager focusCont, ModifierState mods ) {
            mRoot = root;
            mFocusCont = focusCont;
            mMods = mods;
        }


        public boolean keyPressed( int keyCode, char keyChar, int keyLoc ) {
            int mods = mMods.pressKey( keyCode, keyLoc );
            GComponent focus = mFocusCont.focusOwner();
            if( focus == null ) {
                focus = mRoot;
            }
            long micros = System.currentTimeMillis() * 1000L;
            int id = GKeyEvent.KEY_PRESSED;
            GKeyEvent ev = new GKeyEvent( focus, id, micros, mods, keyCode, keyChar, keyLoc );
            return mFocusCont.dispatchKeyEvent( ev );
        }


        public boolean keyPressed( long micros, int mods, int keyCode, char keyChar, int keyLoc ) {
            mMods.setKeyModifiers( mods );
            GComponent focus = mFocusCont.focusOwner();
            if( focus == null ) {
                focus = mRoot;
            }
            int id = GKeyEvent.KEY_PRESSED;
            GKeyEvent ev = new GKeyEvent( focus, id, micros, mods, keyCode, keyChar, keyLoc );
            return mFocusCont.dispatchKeyEvent( ev );
        }


        public boolean keyReleased( int keyCode, char keyChar, int keyLoc ) {
            int mods = mMods.releaseKey( keyCode, keyLoc );
            GComponent focus = mFocusCont.focusOwner();
            if( focus == null ) {
                focus = mRoot;
            }
            long micros = System.currentTimeMillis() * 1000L;
            int id = GKeyEvent.KEY_RELEASED;
            GKeyEvent ev = new GKeyEvent( focus, id, micros, mods, keyCode, keyChar, keyLoc );
            return mFocusCont.dispatchKeyEvent( ev );
        }


        public boolean keyReleased( long micros, int mods, int keyCode, char keyChar, int keyLoc ) {
            mMods.setKeyModifiers( mods );
            GComponent focus = mFocusCont.focusOwner();
            if( focus == null ) {
                focus = mRoot;
            }

            int id = GKeyEvent.KEY_RELEASED;
            GKeyEvent ev = new GKeyEvent( focus, id, micros, mods, keyCode, keyChar, keyLoc );
            return mFocusCont.dispatchKeyEvent( ev );
        }


        public boolean keyTyped( int keyCode, char keyChar, int keyLoc ) {
            int mods = mMods.keyEventModifiers();
            GComponent focus = mFocusCont.focusOwner();
            if( focus == null ) {
                focus = mRoot;
            }

            long micros = System.currentTimeMillis() * 1000L;
            int id = GKeyEvent.KEY_TYPED;
            GKeyEvent ev = new GKeyEvent( focus, id, micros, mods, keyCode, keyChar, keyLoc );
            return mFocusCont.dispatchKeyEvent( ev );
        }


        public boolean keyTyped( long micros, int mods, int keyCode, char keyChar, int keyLoc ) {
            mMods.setKeyModifiers( mods );
            GComponent focus = mFocusCont.focusOwner();
            if( focus == null ) {
                focus = mRoot;
            }

            int id = GKeyEvent.KEY_TYPED;
            GKeyEvent ev = new GKeyEvent( focus, id, micros, mods, keyCode, keyChar, keyLoc );
            return mFocusCont.dispatchKeyEvent( ev );
        }

    }


    private static class ClickCounter {
        private long mMicros;
        private int mCount  = 0;
        private int mOffset = 0;

        public int buttonDown( long t ) {
            if( t < mMicros || t > mMicros + DOUBLE_CLICK_TIMEOUT ) {
                clear();
            }
            mMicros = t;
            return ++mCount - mOffset;
        }

        public int buttonUp( long t ) {
            if( t < mMicros || t > mMicros + DOUBLE_CLICK_TIMEOUT ) {
                clear();
            }
            return mCount - mOffset;
        }

        public int current() {
            return mCount;
        }

        public void clear() {
            mCount = 0;
            mOffset = 0;
        }

    }


    private static class MouseController {

        private final ModifierState mMods;
        private final Rect mWorkRect = new Rect();

        private GComponent mRoot;
        private GComponent mMouseLocation;
        private GComponent mButtonFocus;
//        private GComponent mClickFocus;

        private int mClickFocusX = Integer.MIN_VALUE;
        private int mClickFocusY = Integer.MIN_VALUE;

        private int mMouseX;
        private int mMouseY;

        private final ClickCounter mClicker = new ClickCounter();


        public MouseController( ModifierState mods, GComponent root ) {
            mRoot = root;
            mMods = mods;
        }


        public boolean mousePressed( int button ) {
            GComponent source = focusPress( button );
            if( source == null ) {
                return false;
            }

            mMods.pressButton( button );
            long t = System.currentTimeMillis() * 1000L;
            int clickCount = mClicker.buttonDown( t );
            boolean trigger = button == GMouseEvent.BUTTON3;

            return process( source, GMouseEvent.MOUSE_PRESSED, t, button, clickCount, trigger );
        }


        public boolean mousePressed( long micros, int mods, int button, boolean trigger ) {
            mMods.setAll( mods );

            int clickCount = mClicker.buttonDown( micros );
            mClickFocusX = mMouseX;
            mClickFocusY = mMouseY;

            GComponent source = focusPress( button );
            if( source == null ) {
                return false;
            }
            return process( source, GMouseEvent.MOUSE_PRESSED, micros, button, clickCount, trigger );
        }


        public boolean mouseReleased( int button, boolean genClick ) {
            GComponent source = focusRelease( button );
            mMods.releaseButton( button );

            if( source == null ) {
                return false;
            }

            long micros = System.currentTimeMillis() * 1000L;
            int clickCount = mClicker.buttonUp( micros );

            boolean ret = process( source, GMouseEvent.MOUSE_RELEASED, micros, button, clickCount, false );
            if( genClick ) {
                process( source, GMouseEvent.MOUSE_CLICKED, micros, button, clickCount, false );
            }

            return ret;
        }


        public boolean mouseReleased( long micros, int mods, int button, boolean trigger, boolean genClick ) {
            mMods.setAll( mods );
            int clickCount = mClicker.buttonUp( micros );

            GComponent source = focusRelease( button );
            if( source == null ) {
                return false;
            }

            boolean ret = process( source, GMouseEvent.MOUSE_RELEASED, micros, button, clickCount, trigger );
            if( genClick && mMouseX == mClickFocusX && mMouseY == mClickFocusY ) {
                process( source, GMouseEvent.MOUSE_CLICKED, micros, button, clickCount, trigger );
            }

            return ret;
        }


        public boolean mouseEntered( int x, int y ) {
            return mouseMoved( x, y );
        }


        public boolean mouseEntered( long micros, int mods, int x, int y ) {
            return mouseMoved( micros, mods, x, y );
        }


        public boolean mouseExited() {
            GComponent prev = mMouseLocation;
            mMouseLocation = null;
            if( prev == null ) {
                return false;
            }
            return process( prev, GMouseEvent.MOUSE_EXITED, System.currentTimeMillis() * 1000L, 0, 0, false );
        }


        public boolean mouseExited( long micros, int mods ) {
            mMods.setAll( mods );

            GComponent prev = mMouseLocation;
            mMouseLocation = null;
            if( prev == null ) {
                return false;
            }

            return process( prev, GMouseEvent.MOUSE_EXITED, micros, 0, 0, false );
        }


        public boolean mouseMoved( int x, int y ) {
            long micros = System.currentTimeMillis() * 1000L;
            updatePosition( x, y, micros );

            GComponent source = mButtonFocus;
            if( source == null ) {
                source = mMouseLocation;
                if( source == null ) {
                    return false;
                }
            }

            int mods = mMods.mouseEventModifiers();
            int id = (mods & ALL_BUTTONS_MASK) == 0 ? GMouseEvent.MOUSE_MOVED : GMouseEvent.MOUSE_DRAGGED;

            return processMotion( source, id, micros );
        }


        public boolean mouseMoved( long micros, int mods, int x, int y ) {
            mMods.setAll( mods );

            updatePosition( x, y, micros );
            GComponent source = mButtonFocus;
            if( source == null ) {
                source = mMouseLocation;
                if( source == null ) {
                    return false;
                }
            }

            int id = (mods & ALL_BUTTONS_MASK) == 0 ? GMouseEvent.MOUSE_MOVED : GMouseEvent.MOUSE_DRAGGED;
            return processMotion( source, id, micros );
        }


        public boolean mouseWheelMoved( int scrollType, int scrollAmount, int wheelRotation ) {
            long micros = System.currentTimeMillis() * 1000L;
            GComponent source = mMouseLocation;
            if( source == null ) {
                return false;
            }
            return processWheel( source, micros, scrollType, scrollAmount, wheelRotation );
        }


        public boolean mouseWheelMoved( long micros, int mods, int scrollType, int scrollAmount, int wheelRotation ) {
            mMods.setAll( mods );
            GComponent source = mMouseLocation;
            if( source == null ) {
                return false;
            }
            return processWheel( source, micros, scrollType, scrollAmount, wheelRotation );
        }


        /**
         * Sets the input pane of the Controller. Only ancestors of the pane
         * will receive events.
         *
         * @param root Input pane
         */
        public void setRoot( GComponent root ) {
            mRoot = root;
            validate();
        }

        /**
         * Checks that components in transferFocus are still valid. Should be called on property changes. Is called automatically
         * by {@code setRoot()}.
         */
        public void validate() {
            GComponent root = mRoot;
            GComponent focus = mButtonFocus;

            if( focus != null && (!GToolkit.isMouseFocusable( focus ) || !GToolkit.isChild( root, focus )) ) {
                forceRelease();
                mButtonFocus = null;
                mClickFocusX = Integer.MIN_VALUE;
                mClickFocusY = Integer.MIN_VALUE;
            }

            long micros = System.currentTimeMillis() * 1000L;
            updatePosition( mMouseX, mMouseY, micros );
        }

        /**
         * Forces the controller to mouseReleased all mouse buttons.
         */
        public void forceRelease() {
            int mods = mMods.buttonModifiers();
            if( (mods & GMouseEvent.BUTTON1_DOWN_MASK) != 0 ) {
                mouseReleased( GMouseEvent.BUTTON1, false );
            }
            if( (mods & GMouseEvent.BUTTON2_DOWN_MASK) != 0 ) {
                mouseReleased( GMouseEvent.BUTTON2, false );
            }
            if( (mods & GMouseEvent.BUTTON3_DOWN_MASK) != 0 ) {
                mouseReleased( GMouseEvent.BUTTON3, false );
            }
        }


        private GComponent focusPress( int button ) {
            GComponent ret = mButtonFocus;
            if( ret == null ) {
                ret = mButtonFocus = mMouseLocation;
            }
            if( ret == null ) {
                return null;
            }
            return ret;
        }


        private GComponent focusRelease( int button ) {
            GComponent ret = mButtonFocus;
            if( ret == null ) {
                return mMouseLocation;
            }
            if( (mMods.buttonModifiers() & ~button) == 0 ) {
                mButtonFocus = null;
            }

            return ret;
        }


        private void updatePosition( int x, int y, long micros ) {
            if( x != mMouseX || y != mMouseY ) {
                mMouseX = x;
                mMouseY = y;
                mClicker.clear();
                mClickFocusX = Integer.MIN_VALUE;
                mClickFocusY = Integer.MIN_VALUE;
            }


            GComponent prev = mMouseLocation;
            GComponent focus = null;
            Rect bounds = mWorkRect;

            if( prev != null ) {
                prev.getAbsoluteBounds( bounds );
                int relx = x - bounds.x0;
                int rely = y - bounds.y0;
                focus = prev.mouseFocusableComponentAt( relx, rely );
            } else {
                GComponent comp = mRoot;
                comp.getAbsoluteBounds( bounds );
                int relx = x - bounds.x0;
                int rely = y - bounds.y0;
                focus = comp.mouseFocusableComponentAt( relx, rely );
            }

            if( focus == prev ) {
                return;
            }

            mMouseLocation = focus;
            if( prev != null ) {
                process( prev, GMouseEvent.MOUSE_EXITED, micros, 0, 0, false );
            }
            if( focus != null ) {
                process( focus, GMouseEvent.MOUSE_ENTERED, micros, 0, 0, false );
            }
        }


        private boolean process( GComponent source,
                                 int id,
                                 long micros,
                                 int button,
                                 int clickCount,
                                 boolean trigger )
        {
            Rect rect = mWorkRect;
            source.getAbsoluteBounds( rect );
            GMouseEvent e = new GMouseEvent( source,
                                             id,
                                             micros,
                                             mMods.mouseEventModifiers(),
                                             mMouseX - rect.x(),
                                             mMouseY - rect.y(),
                                             clickCount,
                                             trigger,
                                             button );

            source.processMouseEvent( e );
            return e.isConsumed();
        }


        private boolean processMotion( GComponent source, int id, long micros ) {
            Rect rect = mWorkRect;
            source.getAbsoluteBounds( mWorkRect );
            GMouseEvent e = new GMouseEvent( source,
                                             id,
                                             micros,
                                             mMods.mouseEventModifiers(),
                                             mMouseX - rect.x(),
                                             mMouseY - rect.y(),
                                             mClicker.current(),
                                             false,
                                             0 );
            source.processMouseMotionEvent( e );
            return e.isConsumed();
        }


        private boolean processWheel( GComponent source,
                                      long micros,
                                      int scrollType,
                                      int scrollAmount,
                                      int wheelRotation )
        {
            Rect rect = mWorkRect;
            source.getAbsoluteBounds( rect );
            GMouseWheelEvent e = new GMouseWheelEvent( source,
                                                       GMouseEvent.MOUSE_WHEEL,
                                                       micros,
                                                       mMods.mouseEventModifiers(),
                                                       mMouseX - rect.x(),
                                                       mMouseY - rect.y(),
                                                       mClicker.current(),
                                                       false,
                                                       scrollType,
                                                       scrollAmount,
                                                       wheelRotation );
            source.processMouseWheelEvent( e );
            return e.isConsumed();
        }


    }

}
