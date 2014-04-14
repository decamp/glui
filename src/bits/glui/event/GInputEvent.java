package bits.glui.event;

import java.awt.Toolkit;
import java.awt.event.InputEvent;

import bits.glui.GComponent;


/**
 * @author decamp
 */
public abstract class GInputEvent extends GEvent {

    public static final int SHIFT_DOWN_MASK   = InputEvent.SHIFT_DOWN_MASK;
    public static final int CTRL_DOWN_MASK    = InputEvent.CTRL_DOWN_MASK;
    public static final int META_DOWN_MASK    = InputEvent.META_DOWN_MASK;
    public static final int ALT_DOWN_MASK     = InputEvent.ALT_DOWN_MASK;
    public static final int BUTTON1_DOWN_MASK = InputEvent.BUTTON1_DOWN_MASK;
    public static final int BUTTON2_DOWN_MASK = InputEvent.BUTTON2_DOWN_MASK;
    public static final int BUTTON3_DOWN_MASK = InputEvent.BUTTON3_DOWN_MASK;

    static final int FIRST_HIGH_BIT      = 1 << 14;
    static final int HIGH_MODIFIERS_MASK = ~(FIRST_HIGH_BIT - 1);


    private final long mTimestampMicros;
    private final int  mModifiers;


    protected GInputEvent( GComponent source, int id, long timestampMicros, int modifiers ) {
        super( source, id );
        mTimestampMicros = timestampMicros;
        mModifiers = modifiers;
    }


    public int getModifiers() {
        return mModifiers;
    }


    public long getTimestampMicros() {
        return mTimestampMicros;
    }


    public boolean isShiftDown() {
        return (mModifiers & SHIFT_DOWN_MASK) != 0;
    }


    public boolean isControlDown() {
        return (mModifiers & CTRL_DOWN_MASK) != 0;
    }


    public boolean isAltDown() {
        return (mModifiers & ALT_DOWN_MASK) != 0;
    }


    public boolean isMetaDown() {
        return (mModifiers & META_DOWN_MASK) != 0;
    }

    @Override
    public void consume() {
        super.consume();
    }

    @Override
    public boolean isConsumed() {
        return super.isConsumed();
    }


    public static String getModifiersText( int modifiers ) {
        StringBuilder s = new StringBuilder();

        if( (modifiers & META_DOWN_MASK) != 0 ) {
            s.append( Toolkit.getProperty( "AWT.meta", "Meta" ) );
            s.append( "+" );
        }

        if( (modifiers & CTRL_DOWN_MASK) != 0 ) {
            s.append( Toolkit.getProperty( "AWT.control", "Ctrl" ) );
            s.append( "+" );
        }

        if( (modifiers & ALT_DOWN_MASK) != 0 ) {
            s.append( Toolkit.getProperty( "AWT.alt", "Alt" ) );
            s.append( "+" );
        }

        if( (modifiers & SHIFT_DOWN_MASK) != 0 ) {
            s.append( Toolkit.getProperty( "AWT.shift", "Shift" ) );
            s.append( "+" );
        }

        if( (modifiers & BUTTON1_DOWN_MASK) != 0 ) {
            s.append( Toolkit.getProperty( "AWT.button1", "Button1" ) );
            s.append( "+" );
        }

        if( (modifiers & BUTTON2_DOWN_MASK) != 0 ) {
            s.append( Toolkit.getProperty( "AWT.button2", "Button2" ) );
            s.append( "+" );
        }

        if( (modifiers & BUTTON3_DOWN_MASK) != 0 ) {
            s.append( Toolkit.getProperty( "AWT.button3", "Button3" ) );
            s.append( "+" );
        }

        if( s.length() > 0 ) {
            //remove trailing '+'
            s.setLength( s.length() - 1 );
        }

        return s.toString();
    }

}
