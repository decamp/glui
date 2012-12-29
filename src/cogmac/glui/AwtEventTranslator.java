package cogmac.glui;

import java.awt.Component;
import java.awt.event.*;


/**
 * Sends AWT Mouse Events to EventProcessor. 
 * 
 * @author decamp
 */
class AwtEventTranslator implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
    
    
    private final Component mSource;
    private final EventProcessor mRoot; 
    
    
    public AwtEventTranslator( Component source, EventProcessor processor ) {
        mSource = source;
        mRoot   = processor;
        
        mSource.addMouseListener( this );
        mSource.addMouseMotionListener( this );
        mSource.addMouseWheelListener( this );
        mSource.addKeyListener( this );
    }

    
    
    public void mousePressed( MouseEvent e ) {
        mRoot.processMousePressedEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }


    public void mouseReleased( MouseEvent e ) {
        mRoot.processMouseReleasedEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }
    
    
    public void mouseClicked( MouseEvent e ) {
        mRoot.processMouseClickedEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }


    public void mouseEntered( MouseEvent e ) {
        mRoot.processMouseEnteredEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }


    public void mouseExited( MouseEvent e ) {
        mRoot.processMouseExitedEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }

    
    public void mouseMoved( MouseEvent e ) {
        mRoot.processMouseMovedEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }


    public void mouseDragged( MouseEvent e ) {
        mRoot.processMouseDraggedEvent( e, e.getX(), mSource.getHeight() - 1 - e.getY() );
    }

    
    public void mouseWheelMoved( MouseWheelEvent e ) {
        mRoot.processMouseWheelMovedEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }

    
    public void keyPressed( KeyEvent e ) {
        mRoot.processKeyPressedEvent(e);
    }
    
    
    public void keyReleased( KeyEvent e ) {
        mRoot.processKeyReleasedEvent( e );
    }
    
    
    public void keyTyped( KeyEvent e ) {
        mRoot.processKeyTypedEvent( e );
    }
    


    public void dispose() {
        mSource.removeMouseListener(this);
        mSource.removeMouseMotionListener(this);
        mSource.removeMouseWheelListener(this);
    }
    
}
