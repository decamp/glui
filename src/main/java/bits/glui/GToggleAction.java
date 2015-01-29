/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * @author decamp
 */
public class GToggleAction extends AbstractAction implements GSelectable {

    public GToggleAction( boolean selected ) {
        super();
        putValue( SELECTED_KEY, selected );
    }


    public GToggleAction( String name, boolean selected ) {
        super( name );
        putValue( SELECTED_KEY, selected );
    }
    

    public GToggleAction( String name, Icon icon, boolean selected ) {
        super( name, icon );
        putValue( SELECTED_KEY, selected );
    }



    public void actionPerformed( ActionEvent e ) {
        setSelected( !isSelected() );
    }


    public boolean isSelected() {
        return (Boolean) getValue( SELECTED_KEY );
    }


    public void setSelected( boolean selected ) {
        if( selected == isSelected() )
            return;

        putValue( SELECTED_KEY, selected );
    }

}
