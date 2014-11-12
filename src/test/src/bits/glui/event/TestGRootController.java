package bits.glui.event;

import bits.glui.*;

import javax.swing.*;
import java.awt.*;


/**
 * @author Philip DeCamp
 */
public class TestGRootController {

    public static void main( String[] args ) throws Exception {
        test1();
    }


    static void test1() throws Exception {
        final GRootController root = GRootController.create();
        final GLabel label = new GLabel( "aaOHa" );
        Font font = new Font( "Verdana", Font.PLAIN, 18 );
        label.font( font );

        root.rootPane().addChild( label );
        label.setBackground( .2f, .2f, .3f, 1f );

        root.rootPane().setLayout( new GLayout() {
            public void layoutPane( GComponent pane ) {
                Rect bounds = new Rect();
                pane.absoluteBounds( bounds );
                label.bounds( 20, bounds.height() - 60, bounds.width() - 40, 40 );
            }
        } );

        JFrame frame = frame( root, 1280, 1024 );
        frame.setVisible( true );
        root.startAnimator( 30.0 );

        Thread.sleep( 500L );
        System.out.println( root.rootPane().absoluteBounds() );
        System.out.println( root.rootPane().width() + "\t" + root.rootPane().height() );
    }


    static JFrame frame( GRootController root, int w, int h ) {
        JFrame frame = new JFrame( "Test" );
        Dimension size = new Dimension( 1280, 1024 );
        frame.getContentPane().setPreferredSize( size );
        frame.pack();

        Component comp = root.component();
        comp.setSize( size );
        comp.setMinimumSize( size );
        frame.add( comp );
        frame.setLocationRelativeTo( null );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        return frame;
    }

}
