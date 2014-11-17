/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui.event;

import java.awt.event.MouseEvent;

import bits.glui.GComponent;


/**
 * @author decamp
 */
public class GMouseEvent extends GInputEvent {

    public static final int BUTTON1        = MouseEvent.BUTTON1;
    public static final int BUTTON2        = MouseEvent.BUTTON2;
    public static final int BUTTON3        = MouseEvent.BUTTON3;
    public static final int MOUSE_CLICKED  = MouseEvent.MOUSE_CLICKED;
    public static final int MOUSE_DRAGGED  = MouseEvent.MOUSE_DRAGGED;
    public static final int MOUSE_ENTERED  = MouseEvent.MOUSE_ENTERED;
    public static final int MOUSE_EXITED   = MouseEvent.MOUSE_EXITED;
    public static final int MOUSE_FIRST    = MouseEvent.MOUSE_FIRST;
    public static final int MOUSE_LAST     = MouseEvent.MOUSE_LAST;
    public static final int MOUSE_MOVED    = MouseEvent.MOUSE_MOVED;
    public static final int MOUSE_PRESSED  = MouseEvent.MOUSE_PRESSED;
    public static final int MOUSE_RELEASED = MouseEvent.MOUSE_RELEASED;
    public static final int MOUSE_WHEEL    = MouseEvent.MOUSE_WHEEL;
    public static final int NOBUTTON       = MouseEvent.NOBUTTON;

    private       int     mX;
    private       int     mY;
    private final int     mClickCount;
    private final boolean mPopupTrigger;
    private final int     mButton;


    public GMouseEvent( GComponent source,
                        int id,
                        long timestampMicros,
                        int modifiers,
                        int x,
                        int y,
                        int clickCount,
                        boolean popupTrigger,
                        int button )
    {
        super( source, id, timestampMicros, modifiers );
        mX = x;
        mY = y;
        mClickCount = clickCount;
        mPopupTrigger = popupTrigger;
        mButton = button;
    }


    public int getButton() {
        return mButton;
    }


    public int getClickCount() {
        return mClickCount;
    }


    public int getX() {
        return mX;
    }


    public int getY() {
        return mY;
    }


    public boolean isPopupTrigger() {
        return mPopupTrigger;
    }


    public void translatePoint( int x, int y ) {
        mX += x;
        mY += y;
    }


    public static String getMouseModifiersText( int modifiers ) {
        return MouseEvent.getMouseModifiersText( modifiers );
    }

}
