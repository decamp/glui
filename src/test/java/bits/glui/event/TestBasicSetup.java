/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui.event;

import bits.draw3d.Rect;
import bits.glui.*;
import bits.math3d.Vec4;

import javax.swing.*;
import java.awt.*;


/**
 * @author Philip DeCamp
 */
public class TestBasicSetup {

    public static void main( String[] args ) throws Exception {
        test1();
    }


    static void test1() throws Exception {
        final GRootController root = GRootController.create();
        final Font font = new Font( "Verdana", Font.PLAIN, 18 );
        final Vec4 background = new Vec4( 0.2f, 0.2f, 0.3f, 1f );

        final GLabel label = new GLabel( "Test of the GLabel and FontTexture classes." );
        label.horizontalAlignment( 0.0f, 0.5f );
        label.setFont( font );
        label.setBackground( background );
        root.rootPane().addChild( label );

        final GButton button = new GButton( "Button" );
        button.setFont( font );
        button.setBackground( background );
        root.rootPane().addChild( button );

        final GToggleButton toggle = new GToggleButton( "Toggle" );
        toggle.setFont( font );
        toggle.setBackground( background );
        root.rootPane().addChild( toggle );

        final GCheckBox checkBox = new GCheckBox( "Something", true );
        checkBox.setFont( font );
        label.setBackground( background );
        root.rootPane().addChild( checkBox );

        final GTextField field = new GTextField( 256, "Testing" );
        field.setFont( font );
        field.setBackground( background );
        root.rootPane().addChild( field );

        root.rootPane().setLayout( new GLayout() {
            public void layoutPane( GComponent pane ) {
                Rect bounds = new Rect();
                pane.getAbsoluteBounds( bounds );
                int y = bounds.height() - 60;
                label.setBounds( 20, y, bounds.width() - 40, 40 );
                y -= 60;
                button.setBounds( 20, y, bounds.width() - 40, 40 );
                y -= 60;
                toggle.setBounds( 20, y, bounds.width() - 40, 40 );
                y -= 60;
                checkBox.setBounds( 20, y, bounds.width() - 40, 40 );
                y -= 60;
                field.setBounds( 20, y, bounds.width() - 40, 40 );
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
