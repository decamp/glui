package bits.glui.event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static java.lang.System.out;


/**
 * @author Philip DeCamp
 */
public class TestAwtEvents {


    public static void main( String[] args ) throws Exception {
        test1();
    }


    static void test1() {
        final JFrame frame = new JFrame();
        final JPanel pane   = new JPanel();
        final JPanel panel0 = new JPanel();
        final JPanel panel1 = new JPanel();

        panel0.setBackground( Color.GRAY );
        panel1.setBackground( Color.DARK_GRAY );
        panel0.setFocusable( true );
        panel1.setFocusable( true );

        pane.add( panel0 );
        pane.add( panel1 );
        pane.setLayout( new LayoutManager() {
            @Override
            public void addLayoutComponent( String s, Component component ) {}

            @Override
            public void removeLayoutComponent( Component component ) {}

            @Override
            public Dimension preferredLayoutSize( Container container ) {
                return null;
            }

            @Override
            public Dimension minimumLayoutSize( Container container ) {
                return null;
            }

            @Override
            public void layoutContainer( Container container ) {
                int w = container.getWidth();
                int h = container.getHeight();
                panel0.setBounds( 0, 0, w / 2, h );
                panel1.setBounds( w / 2, 0, w - w / 2, h );
                System.out.println( w + "\t" + h );
            }
        } );
        frame.setContentPane( pane );

        MouseHandler h = new MouseHandler();
        panel0.addMouseListener( h );
        panel0.addMouseMotionListener( h );
        panel0.addMouseWheelListener( h );
        panel0.addKeyListener( h );
        panel1.addMouseListener( h );
        panel1.addMouseMotionListener( h );
        panel1.addMouseWheelListener( h );
        panel1.addKeyListener( h );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 512, 512 );
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
    }


    private static class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {


        @Override
        public void mouseClicked( MouseEvent event ) {
            out.println( "    clicked:  " + format( event ) );
        }

        @Override
        public void mousePressed( MouseEvent event ) {
            out.println( "  pressed:  " + format( event ) );
        }

        @Override
        public void mouseReleased( MouseEvent event ) {
            out.println( "  released:  " + format( event ) );
        }

        @Override
        public void mouseEntered( MouseEvent event ) {
            out.println( "entered:  " + format( event ) );
        }

        @Override
        public void mouseExited( MouseEvent event ) {
            out.println( "exited:  " + format( event ) );
        }

        @Override
        public void mouseDragged( MouseEvent event ) {
            out.println( "    dragged:  " + format( event ) );
        }

        @Override
        public void mouseMoved( MouseEvent event ) {
            out.println( "    moved:  " + format( event ) );
        }

        @Override
        public void mouseWheelMoved( MouseWheelEvent event ) {
            out.println( "wheel:  " + format( event ) );
        }

        @Override
        public void keyTyped( KeyEvent event ) {
            out.println( "    typed: " + format( event ) );
        }

        @Override
        public void keyPressed( KeyEvent event ) {
            out.println( "  pressed: " + format( event ) );
        }

        @Override
        public void keyReleased( KeyEvent event ) {
            out.println( "  released: " + format( event ) );
        }


        String format( KeyEvent e ) {
            String extra = String.format( "  code:%d  '%s'  %d", e.getKeyCode(), "" + e.getKeyChar(), e.getKeyLocation() );
            return format( e, extra );
        }


        String format( MouseEvent e ) {
            String extra = String.format( "(% 3d,% 3d) cc:%d  pop:%b", e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger() );
            return format( e, extra );
        }


        String format( InputEvent e, String extra ) {
            String mods   = String.format( "%16s", Integer.toBinaryString( e.getModifiers()   ) ).replace( ' ', '0' );
            String exMods = String.format( "%16s", Integer.toBinaryString( e.getModifiersEx() ) ).replace( ' ', '0' );
            return String.format( "%d %s\n    mods:   %s\n    modsEx: %s\n",
                                  e.getID(),
                                  extra,
                                  mods,
                                  exMods );
        }


    }

}

