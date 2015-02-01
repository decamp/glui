package bits.glui;

import bits.draw3d.DrawEnv;
import bits.math3d.Vec4;

import java.beans.PropertyChangeListener;

public class GToggleButton extends GButton implements GSelectable {

    private final GSelectGroup  mGroup;
    private final GToggleAction mAction;

    private ButtonPalette mPalette;

    private boolean mRollover = false;
    private boolean mPressed  = false;
    private boolean mArmed    = false;


    public GToggleButton( String text ) {
        this( new GToggleAction( text, false ), null );
    }


    public GToggleButton( String text, GSelectGroup group ) {
        this( new GToggleAction( text, false ), group );
    }


    public GToggleButton( GToggleAction action ) {
        this( action, null );
    }


    public GToggleButton( GToggleAction action, GSelectGroup group ) {
        super( action );
        mPalette = ButtonPalette.DEFAULT;
        mGroup   = group;
        mAction  = action;
        if( group != null ) {
            group.add( mAction );
        }
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


    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        mAction.addPropertyChangeListener( listener );
    }


    public void removePropertyChangeListener( PropertyChangeListener listener ) {
        mAction.removePropertyChangeListener( listener );
    }

    @Override
    public GToggleAction getAction() {
        return mAction;
    }

    @Override
    public void paintComponent( DrawEnv d ) {
        Vec4 foreground;
        Vec4 background;
        int offX = 0;
        int offY = 0;

        if( !isEnabled() || !mAction.isEnabled() ) {
            foreground = mPalette.mDisabledForeground;
            background = mPalette.mDisabledBackground;
        } else if( isDepressed() ) {
            foreground = mPalette.mPressedForeground;
            background = mPalette.mPressedBackground;
            offX = 1;
            offY = -1;
        } else if( mAction.isSelected() ) {
            foreground = mPalette.mSelectedForeground;
            background = mPalette.mSelectedBackground;
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

}
