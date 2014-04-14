package bits.glui;

import java.awt.Font;
import javax.media.opengl.*;

import bits.glui.event.GPaintListener;
import bits.glui.text.FontTexture;
import static javax.media.opengl.GL.*;

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
        GL gl = g.gl();
        int w = width();
        int h = height();
        GColor foreground = foreground();
        GColor background = background();
        
        if( background != null ) {
            background.apply( gl );
            gl.glBegin( GL_QUADS );
            gl.glVertex2i( 0, 0 );
            gl.glVertex2i( w, 0 );
            gl.glVertex2i( w, h );
            gl.glVertex2i( 0, h );
            gl.glEnd();
        }
        
        String s = text();
        FontTexture font = g.fontManager().getFontTexture( font() );
        
        if( mUpdateLabel ) {
            if( s == null || s.length() == 0 )
                return;
            
            mUpdateLabel = false;
            mLabelX = font.getCharsWidth( s ) * mHorDst;
            mLabelY = ( font.getAscent() + font.getDescent() ) * mVertDst + font.getDescent();
        }
        
        foreground.apply( gl );
        gl.glPushMatrix();
        
        float x = Math.round( w * mHorSrc - mLabelX );
        float y = Math.round( h * mVertSrc - mLabelY );
        
        font.push( gl );
        font.renderChars( gl, x, y, 0, s );
        font.pop( gl );
        
        gl.glPopMatrix();
    }

}
