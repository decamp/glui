/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui;

import java.awt.*;

import bits.draw3d.DrawStream;
import bits.draw3d.DrawEnv;
import bits.draw3d.text.FontTexture;
import bits.math3d.Vec4;

/**
 * @author Philip DeCamp
 */
public class GLabel extends GPanel {

    private String mText;
    private float mHorSrc   = 0.5f;
    private float mHorDst   = 0.5f;
    private float mVertSrc  = 0.5f;
    private float mVertDst  = 0.5f;
    
    private boolean mUpdateLabel = true;
    private float mLabelX = 0f;
    private float mLabelY = 0f;

    final Vec4 mWorkFore = new Vec4();
    final Vec4 mWorkBack = new Vec4();

    
    public GLabel( String text ) {
        mText = text;
    }

    

    public GLabel text( String text ) {
        mText = text;
        mUpdateLabel = true;
        return this;
    }

    
    public String text() {
        return mText;
    }

    
    public GLabel horizontalAlignment( float foregroundPoint, float backgroundPoint ) {
        mHorSrc = foregroundPoint;
        mHorDst = backgroundPoint;
        mUpdateLabel = true;
        return this;
    }

    
    public GLabel verticalAlignment( float foregroundPoint, float backgroundPoint ) {
        mVertSrc = foregroundPoint;
        mVertDst = backgroundPoint;
        mUpdateLabel = true;
        return this;
    }

    @Override
    public GLabel setFont( Font font ) {
        super.setFont( font );
        mUpdateLabel = true;
        return this;
    }

    @Override
    public void paintComponent( DrawEnv d ) {
        getForeground( mWorkFore );
        getBackground( mWorkBack );
        paintLabel( d, mWorkBack, mWorkFore, 0, 0 );
    }


    protected void paintLabel( DrawEnv d, Vec4 background, Vec4 foreground, int offX, int offY ) {
        DrawStream s = d.drawStream();
        s.config( true, false, false );

        int w = width();
        int h = height();

        if( background != null ) {
            s.config( true, false, false );
            s.beginTriStrip();
            s.color( background );
            s.vert( 0, 0 );
            s.vert( w, 0 );
            s.vert( 0, h );
            s.vert( w, h );
            s.end();
        }

        if( foreground == null ) {
            return;
        }

        String text = text();
        FontTexture font = d.fontManager().getFontTexture( getFont() );

        if( mUpdateLabel ) {
            if( s == null || text.isEmpty() ) {
                return;
            }
            mUpdateLabel = false;
            mLabelX = font.getCharsWidth( text ) * mHorSrc;
            mLabelY = ( font.getAscent() + font.getDescent() ) * mVertSrc - font.getDescent();
        }

        s.color( foreground );
        float x = Math.round( w * mHorDst - mLabelX )  + offX;
        float y = Math.round( h * mVertDst - mLabelY ) + offY;
        font.beginRenderChars( d );
        font.renderChars( d, x, y, 0, text );
        font.endRenderChars( d );
    }

}
