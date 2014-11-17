/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui.event;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.event.*;

public class DetachmentTest {
    
    public static void main( String[] args ) throws Exception {
        
        JFrame frame = new JFrame();
        JPanel parent = new JPanel();
        parent.setLayout( null );
        JPanel panel = new JPanel();
        
        
        panel.addComponentListener( new ComponentListener() {
            @Override
            public void componentHidden( ComponentEvent arg0 ) {
                System.out.println( "Hidden" );
            }

            @Override
            public void componentMoved( ComponentEvent arg0 ) {}

            @Override
            public void componentResized( ComponentEvent arg0 ) {}

            @Override
            public void componentShown( ComponentEvent arg0 ) {
                System.out.println( "Shown" );
            }   
        });
        
        panel.addAncestorListener( new AncestorListener() {

            @Override
            public void ancestorAdded( AncestorEvent arg0 ) {
                System.out.println( "Ancestor added" );
            }

            @Override
            public void ancestorMoved( AncestorEvent arg0 ) {
                System.out.println( "Ancestor mouseMoved" );
            }

            @Override
            public void ancestorRemoved( AncestorEvent arg0 ) {
                System.out.println(" Ancestor removed" );
            }
            
        });
        
        panel.addFocusListener( new FocusListener() {

            @Override
            public void focusGained( FocusEvent arg0 ) {
                System.out.println( "Focus gained." );
            }

            @Override
            public void focusLost( FocusEvent arg0 ) {
                System.out.println( "Focus lost." );
            }
            
            
        });
        
        panel.addPropertyChangeListener( new PropertyChangeListener() {
            @Override
            public void propertyChange( PropertyChangeEvent arg0 ) {
                System.out.println( "Prop: " + arg0.getPropertyName() );
            }
        });

        
        panel.setLayout( new LayoutManager() {
            
            @Override
            public void removeLayoutComponent( Component arg0 ) {}
            
            @Override
            public Dimension preferredLayoutSize( Container arg0 ) {
                return null;
            }
            
            @Override
            public Dimension minimumLayoutSize( Container arg0 ) {
                return null;
            }
            
            @Override
            public void layoutContainer( Container arg0 ) {
                System.out.println( "Layout" );
            }
            
            @Override
            public void addLayoutComponent( String arg0, Component arg1 ) {}
        } );       
        
        frame.setContentPane( parent );
        parent.add( panel );
        
        frame.setVisible( true );
        
        System.out.println( "####");
        //panel.setVisible( false );
        //panel.setVisible( true );
        panel.setFocusable( true );
        panel.requestFocus();
        
        frame.remove( parent );
        
        
    }

}
