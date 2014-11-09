package bits.glui;

import java.awt.Component;
import java.awt.event.*;


/**
 * Sends AWT Mouse Events to EventProcessor.
 *
 * @author decamp
 */
class AwtEventTranslator implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private final Component mSource;
    private final GHumanInputController mCont;

    public AwtEventTranslator( Component source, GHumanInputController cont ) {
        mSource = source;
        mCont   = cont;

        mSource.addMouseListener( this );
        mSource.addMouseMotionListener( this );
        mSource.addMouseWheelListener( this );
        mSource.addKeyListener( this );
    }


    public void mousePressed( MouseEvent e ) {
        if( mCont.mousePressed( e.getWhen() * 1000L,
                                modsFor( e ),
                                e.getButton(),
                                e.isPopupTrigger() ) )
        {
            e.consume();
        }
    }


    public void mouseReleased( MouseEvent e ) {
        if( mCont.mouseReleased( e.getWhen() * 1000L,
                                 modsFor( e ),
                                 e.getButton(),
                                 e.isPopupTrigger(),
                                 true ) )

        {
            e.consume();
        }
    }


    public void mouseClicked( MouseEvent e ) {}


    public void mouseEntered( MouseEvent e ) {
        if( mCont.mouseEntered( e.getWhen() * 1000L, modsFor( e ), e.getX(), e.getY() ) ) {
            e.consume();
        }
    }


    public void mouseExited( MouseEvent e ) {
        if( mCont.mouseExited( e.getWhen() * 1000L, modsFor( e ) ) ) {
            e.consume();
        }
    }


    public void mouseMoved( MouseEvent e ) {
        if( mCont.mouseMoved( e.getWhen() * 1000L,
                                   modsFor( e ),
                                   e.getX(),
                                   mSource.getHeight() - 1 - e.getY() ) )
        {
            e.consume();
        }
    }


    public void mouseDragged( MouseEvent e ) {
        if( mCont.mouseMoved( e.getWhen() * 1000L,
                                   modsFor( e ),
                                   e.getX(),
                                   mSource.getHeight() - 1 - e.getY() ) )
        {
            e.consume();
        }
    }


    public void mouseWheelMoved( MouseWheelEvent e ) {
        if( mCont.mouseWheelMoved( e.getWhen() * 1000L,
                                   modsFor( e ),
                                   e.getScrollType(),
                                   e.getScrollAmount(),
                                   e.getWheelRotation() ) )
        {
            e.consume();
        }
    }


    public void keyPressed( KeyEvent e ) {
        if( mCont.keyPressed( e.getWhen() * 1000L,
                              modsFor( e ),
                              e.getKeyCode(),
                              e.getKeyChar(),
                              e.getKeyLocation() ) )
        {
            e.consume();
        }
    }


    public void keyReleased( KeyEvent e ) {
        if( mCont.keyReleased( e.getWhen() * 1000L,
                               modsFor( e ),
                               e.getKeyCode(),
                               e.getKeyChar(),
                               e.getKeyLocation() ) )
        {
            e.consume();
        }
    }


    public void keyTyped( KeyEvent e ) {
        if( mCont.keyTyped( e.getWhen() * 1000L,
                            modsFor( e ),
                            e.getKeyCode(),
                            e.getKeyChar(),
                            e.getKeyLocation() ) )
        {
            e.consume();
        }
    }


    public void dispose() {
        mSource.removeMouseListener( this );
        mSource.removeMouseMotionListener( this );
        mSource.removeMouseWheelListener( this );
    }


    private static int modsFor( InputEvent e ) {
        return e.getModifiers() | e.getModifiersEx();
    }

}
