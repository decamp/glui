package bits.glui;

import bits.draw3d.DrawEnv;
import bits.glui.*;
import bits.glui.event.GMouseEvent;
import bits.glui.event.GMouseListener;
import bits.math3d.Vec4;

import javax.swing.*;
import java.beans.PropertyChangeListener;


public class ToggleButton extends AbstractButton implements GSelectable {

    private final GSelectGroup  mGroup;
    private final GToggleAction mAction;

    private ButtonPalette mPalette;

    private boolean mRollover = false;
    private boolean mPressed  = false;
    private boolean mArmed    = false;


    public ToggleButton( String text ) {
        this( new GToggleAction( text, false ), null );
    }


    public ToggleButton( String text, GSelectGroup group ) {
        this( new GToggleAction( text, false ), group );
    }


    public ToggleButton( GToggleAction action ) {
        this( action, null );
    }


    public ToggleButton( GToggleAction action, GSelectGroup group ) {
        super( action );
        mPalette = ButtonPalette.DEFAULT;
        mGroup   = group;
        mAction  = action;

        if( group != null ) {
            group.add( mAction );
        }

        addMouseListener( new MouseImpl() );
    }



    @Override
    public void paintComponent( DrawEnv d ) {
        Vec4 foreground = null;
        Vec4 background = null;

        if( !isEnabled() || !mAction.isEnabled() ) {
            foreground = mPalette.mDisabledForeground;
            background = mPalette.mDisabledBackground;
        } else if( mPressed ) {
            foreground = mPalette.mPressedForeground;
            background = mPalette.mPressedBackground;
        } else if( mRollover ) {
            foreground = mPalette.mRolloverForeground;
            background = mPalette.mRolloverBackground;
        } else if( mAction.isSelected() ) {
            foreground = mPalette.mSelectedForeground;
            background = mPalette.mSelectedBackground;
        } else {
            foreground = mPalette.mForeground;
            background = mPalette.mBackground;
        }

        paintButton( d, mPressed, background, foreground );
    }


    public boolean isSelected() {
        return mAction.isSelected();
    }


    public void setSelected( boolean selected ) {
        if( mGroup != null ) {
            mGroup.setSelected( mAction, selected );
        } else {
            mAction.setSelected( selected );
        }
    }


    public ButtonPalette palette() {
        return mPalette;
    }


    public void palette( ButtonPalette palette ) {
        mPalette = palette;
    }


    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        mAction.addPropertyChangeListener( listener );
    }


    public void removePropertyChangeListener( PropertyChangeListener listener ) {
        mAction.removePropertyChangeListener( listener );
    }


    public void fireToggleAction() {
        fireAction( "" );
    }



    private class MouseImpl implements GMouseListener {

        @Override
        public void mouseEntered( GMouseEvent e ) {
            mRollover = true;
            repaint();
        }

        @Override
        public void mouseExited( GMouseEvent e ) {
            mRollover = false;
            mArmed    = false;
            repaint();
        }

        @Override
        public void mousePressed( GMouseEvent e ) {
            if( e.getButton() != GMouseEvent.BUTTON1 ) {
                return;
            }

            if( isEnabled() ) {
                mPressed = true;
                mArmed = mRollover;
                repaint();
            }
        }

        @Override
        public void mouseReleased( GMouseEvent e ) {
            if( e.getButton() != GMouseEvent.BUTTON1 ) {
                return;
            }
            mPressed = false;
            if( mArmed ) {
                mArmed = false;
                fireToggleAction();
            }
            repaint();
        }

        @Override
        public void mouseClicked( GMouseEvent e ) {}

    }

}
