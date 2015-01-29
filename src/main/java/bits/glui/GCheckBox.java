/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui;

import java.awt.event.*;
import javax.media.opengl.*;

import bits.draw3d.DrawStream;
import bits.draw3d.DrawEnv;
import bits.draw3d.text.FontTexture;


public class GCheckBox extends GButton implements GSelectable {
    
    private boolean mSelected = false;


    public GCheckBox( String label ) {
        this( label, false );
    }


    public GCheckBox( String label, boolean selected ) {
        super( label );
        mSelected = selected;
        addActionListener( new ActionHandler() );
    }



    public void setSelected( boolean selected ) {
        mSelected = selected;
    }


    public boolean isSelected() {
        return mSelected;
    }

    @Override
    public void paintComponent( DrawEnv g ) {
        final DrawStream s = g.drawStream();
        final bits.math3d.Vec4 v = g.mWorkVec4;
        final int w = width();
        final int h = height();
        s.config( true, false, false );

        if( getBackground( v ) ) {
            s.beginTriStrip();
            s.color( v );
            s.tex( 0, 0 );
            s.vert( 0, 0 );
            s.tex( 1, 0 );
            s.vert( w, 0 );
            s.tex( 0, 1 );
            s.vert( 0, h );
            s.tex( 1, 1 );
            s.vert( w, h );
            s.end();
        }

        getForeground( v );
        g.mLineWidth.apply( 1f );
        s.color( v );
        s.beginLineLoop();
        final int margin = 2;
        s.beginLineLoop();
        s.vert( margin, margin );
        s.vert( h - margin, margin );
        s.vert( h - margin, h - margin );
        s.vert( margin, h - margin );
        s.end();

        if( mSelected ) {
            s.beginLines();
            s.vert( margin * 2, margin * 2 );
            s.vert( h - margin * 2, h - margin * 2 );
            s.vert( margin * 2, h - margin * 2 );
            s.vert( h - margin * 2, margin * 2 );
            s.end();
        }

        FontTexture font = g.fontManager().getFontTexture( getFont(), GLContext.getCurrent() );
        font.bind( g );
        float yy = Math.round( ( h - ( font.getAscent() - font.getDescent() ) ) * 0.5f );
        font.renderChars( g, h, yy, 0, text() );
        font.unbind( g );
    }


    private final class ActionHandler implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            mSelected = !mSelected;
            repaint();
        }
    }

}