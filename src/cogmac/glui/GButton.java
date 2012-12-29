package cogmac.glui;

import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;

import cogmac.glui.event.GMouseEvent;
import cogmac.glui.event.GMouseListener;
import cogmac.glui.event.GluiMulticaster;


/**
 * @author decamp
 */
public class GButton extends GLabel {

    private ActionListener mCaster = null;
    
    
    public GButton( String text ) {
        super( text );
        addMouseListener( new MouseListenerImpl() );
    }
    
    public GButton( Action action ) {
        super( "" );
        addMouseListener( new MouseListenerImpl() );
        
        if( action != null ) {
            Object n = action.getValue( Action.NAME );
            if( n != null )
                text( n.toString() );
            
            action.addPropertyChangeListener( new ChangeListenerImpl() );
            addActionListener( action );
        }
    }
    
    
    
    public void addActionListener( ActionListener al ) {
        mCaster = GluiMulticaster.add( mCaster, al );
    }
    
    public void removeActionListener( ActionListener al ) {
        mCaster = GluiMulticaster.remove( mCaster, al );
    }
    
    
    
    private final class MouseListenerImpl implements GMouseListener { 

        public void mouseClicked( GMouseEvent e ) {
            if( mCaster == null || !isEnabled() )
                return;
            
            mCaster.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "" ) );
        }

        public void mousePressed( GMouseEvent e ) {}

        public void mouseReleased( GMouseEvent e ) {}

        public void mouseEntered( GMouseEvent e ) {}

        public void mouseExited( GMouseEvent e ) {}

    }    

    
    private final class ChangeListenerImpl implements PropertyChangeListener {

        public void propertyChange( PropertyChangeEvent e ) {
            if( e.getPropertyName() == Action.NAME ) {
                text( e.getNewValue().toString() );
            }
        }
        
    }

}
