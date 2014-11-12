package bits.glui;

import java.awt.*;
import javax.media.opengl.*;
import static javax.media.opengl.GL3.*;

import bits.draw3d.DrawStream;
import bits.glui.text.FontTexture;
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

    
    public GLabel horizontalAlignment( float backgroundPoint, float foregroundPoint ) {
        mHorSrc = backgroundPoint;
        mHorDst = foregroundPoint;
        mUpdateLabel = true;
        return this;
    }

    
    public GLabel verticalAlignment( float backgroundPoint, float foregroundPoint ) {
        mVertSrc = backgroundPoint;
        mVertDst = foregroundPoint;
        mUpdateLabel = true;
        return this;
    }

    @Override
    public GLabel font( Font font ) {
        super.font( font );
        mUpdateLabel = true;
        return this;
    }

    @Override
    public void paintComponent( GGraphics g ) {
        DrawStream s = g.drawStream();

        int w = width();
        int h = height();
        Vec4 v = g.mWorkVec4;

        if( background( v ) ) {
            s.config( true, false, false );
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

        String txt = text();
        FontTexture font = g.mFontMan.getFontTexture( font() );
        if( mUpdateLabel ) {
            if( txt == null || txt.isEmpty() ) {
                return;
            }
            mUpdateLabel = false;
            mLabelX = font.getCharsWidth( txt ) * mHorDst;
            mLabelY = ( font.getAscent() + font.getDescent() ) * mVertDst + font.getDescent();
        }

        foreground( v );
        s.color( v );

        float x = Math.round( w * mHorSrc - mLabelX );
        float y = Math.round( h * mVertSrc - mLabelY );

        g.checkErr();
        //g.mGl.glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
        g.checkErr();

        //s.color( 0f, 1f, 0f, 1f );
        s.config( true, true, false );
        font.bind( g );

        font.renderChars( g, x, y, 0, "This is a test of teh santoeh unsc " );
        font.unbind( g );
    }

}
