package cogmac.glui.event;

import java.awt.Component;
import java.awt.event.*;

import cogmac.glui.GRootPane;

/**
 * Sends AWT Mouse Events to RootPane 
 * 
 * @author decamp
 */
public class AwtEventTranslator implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
    
    
    private final Component mSource;
    private final GRootPane mRoot; 
    
    
    public AwtEventTranslator(Component source, GRootPane dest) {
        mSource = source;
        mRoot = dest;
        
        mSource.addMouseListener(this);
        mSource.addMouseMotionListener(this);
        mSource.addMouseWheelListener(this);
        mSource.addKeyListener(this);
    }

    
    
    public void mousePressed(MouseEvent e) {
        mRoot.translateMousePressedEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }


    public void mouseReleased(MouseEvent e) {
        mRoot.translateMouseReleasedEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }
    
    
    public void mouseClicked(MouseEvent e) {
        mRoot.translateMouseClickedEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }


    public void mouseEntered(MouseEvent e) {
        mRoot.translateMouseEnteredEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }


    public void mouseExited(MouseEvent e) {
        mRoot.translateMouseExitedEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }

    
    public void mouseMoved(MouseEvent e) {
        mRoot.translateMouseMovedEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }


    public void mouseDragged(MouseEvent e) {
        mRoot.translateMouseDraggedEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }

    
    public void mouseWheelMoved(MouseWheelEvent e) {
        mRoot.translateMouseWheelMovedEvent(e, e.getX(), mSource.getHeight() - 1 - e.getY());
    }

    
    public void keyPressed(KeyEvent e) {
        mRoot.translateKeyPressedEvent(e);
    }
    
    
    public void keyReleased(KeyEvent e) {
        mRoot.translateKeyReleasedEvent(e);
    }
    
    
    public void keyTyped(KeyEvent e) {
        mRoot.translateKeyTypedEvent(e);
    }
    


    public void dispose() {
        mSource.removeMouseListener(this);
        mSource.removeMouseMotionListener(this);
        mSource.removeMouseWheelListener(this);
    }
    
}
