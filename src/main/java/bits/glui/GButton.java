package bits.glui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import bits.draw3d.DrawEnv;
import bits.glui.event.*;
import bits.math3d.Vec4;


/**
 * @author decamp
 */
public class GButton extends GLabel {

    private final Action mAction;
    private String mActionCommand = null;

    private boolean mMouseOver = false;
    private boolean mDepressed = false;

    private ActionListener mCaster = null;
    private ButtonPalette mPalette;


    public GButton( String text ) {
        super( text );
        mAction = null;
        init();
    }


    public GButton( Action action ) {
        super( "" );
        if( action != null ) {
            Object n = action.getValue( Action.NAME );
            if( n != null ) {
                text( n.toString() );
            }
            action.addPropertyChangeListener( new ChangeListenerImpl() );
            addActionListener( action );
            mAction = action;
        } else {
            mAction = null;
        }
        init();
    }


    private void init() {
        horizontalAlignment( 0.0f, 0.1f );
        setSize( 50, 25 );
        setButtonPalette( ButtonPalette.DEFAULT );
        addMouseListener( new MouseListenerImpl() );
    }



    public Action getAction() {
        return mAction;
    }


    public ButtonPalette getButtonPalette() {
        return mPalette;
    }


    public void setButtonPalette( ButtonPalette bp ) {
        mPalette = bp;
        setFont( bp.mFont );
        setForeground( bp.mForeground );
        setBackground( bp.mBackground );
    }


    public void addActionListener( ActionListener al ) {
        mCaster = GluiMulticaster.add( mCaster, al );
    }


    public void removeActionListener( ActionListener al ) {
        mCaster = GluiMulticaster.remove( mCaster, al );
    }


    public String getActionCommand() {
        return mActionCommand;
    }


    public void setActionCommand( String actionCommand ) {
        mActionCommand = actionCommand;
    }

    @Override
    public void paintComponent( DrawEnv d ) {
        Vec4 foreground;
        Vec4 background;
        int offX = 0;
        int offY = 0;

        if( !isEnabled() || mAction != null && !mAction.isEnabled() ) {
            foreground = mPalette.mDisabledForeground;
            background = mPalette.mDisabledBackground;
        } else if( isDepressed() ) {
            foreground = mPalette.mPressedForeground;
            background = mPalette.mPressedBackground;
            offX = 1;
            offY = -1;
        } else if( isMouseOver() ) {
            foreground = mPalette.mRolloverForeground;
            background = mPalette.mRolloverBackground;
        } else {
            foreground = mWorkFore;
            background = mWorkBack;
            getForeground( foreground );
            getBackground( background );
        }

        paintLabel( d, background, foreground, offX, offY );
    }


    public boolean isMouseOver() {
        return mMouseOver;
    }


    public boolean isDepressed() {
        return mDepressed;
    }



    protected void fireClickAction() {
        String cmd = mActionCommand;
        if( cmd == null ) {
            cmd = text();
            if( cmd == null ) {
                cmd = "";
            }
        }
        fireAction( cmd );
    }


    protected void fireAction( String actionCommand ) {
        ActionListener caster = mCaster;
        if( caster == null ) {
            return;
        }
        caster.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, actionCommand ) );
    }


    private final class ChangeListenerImpl implements PropertyChangeListener {
        public void propertyChange( PropertyChangeEvent e ) {
            if( e.getPropertyName() == Action.NAME ) {
                text( e.getNewValue().toString() );
            }
        }
    }


    private final class MouseListenerImpl implements GMouseListener {

        private boolean mArmed    = false;

        public void mouseClicked( GMouseEvent e ) {}

        public void mousePressed( GMouseEvent e ) {
            if( e.getButton() != GMouseEvent.BUTTON1 ) {
                return;
            }
            if( isEnabled() ) {
                mDepressed = true;
                mArmed   = mMouseOver;
                repaint();
            }
        }

        public void mouseReleased( GMouseEvent e ) {
            if( e.getButton() != GMouseEvent.BUTTON1 ) {
                return;
            }
            mDepressed = false;
            if( mArmed ) {
                mArmed = false;
                fireClickAction();
            }

            repaint();
        }

        public void mouseEntered( GMouseEvent e ) {
            mMouseOver = true;
            repaint();
        }

        public void mouseExited( GMouseEvent e ) {
            mMouseOver = false;
            mArmed = false;
            repaint();
        }

    }

}
