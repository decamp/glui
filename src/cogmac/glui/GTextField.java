package cogmac.glui;

import javax.media.opengl.*;

import cogmac.glui.event.*;
import cogmac.glui.text.FontTexture;
import static javax.media.opengl.GL.*;

/**
 * Incredibly crappy right now.  Use only in emergency.
 * 
 * @author decamp
 */
public class GTextField extends GPanel {

    private final int mMaxLength;
    private final StringBuilder mText;
    private String mDrawText;

    private boolean mHasFocus = false;
    
    
    public GTextField( int maxLength ) {
        this( maxLength, "" );
    }
    
    
    public GTextField( int maxLength, String text ) {
        mMaxLength = maxLength;
        mText      = new StringBuilder(text);
        mDrawText  = text;
        
        addFocusListener( new GFocusListener() {
            
            public void focusGained( GFocusEvent e ) {
                mHasFocus = true;
            }

            public void focusLost( GFocusEvent e ) {
                mHasFocus = false;
            }

        });

        addKeyListener( new KeyHandler() );
        
        addMouseListener( new MouseHandler() );
        
    }


    
    public GTextField text( String text ) {
        mText.setLength( 0 );
        mText.append( text );
        
        if( mText.length() > mMaxLength ) {
            mText.setLength( mMaxLength );
        }
        
        mDrawText = text;
        return this;
    }
    
    
    public String text() {
        return mDrawText;
    }

    
    @Override
    public void paint( GGraphics g ) {
        GL gl = g.gl();
        final int w = width();
        final int h = height();

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

        GColor foreground = foreground();
        foreground.apply( gl );
        FontTexture font = g.fontManager().getFontTexture( font(), GLContext.getCurrent() );

        gl.glMatrixMode( GL_MODELVIEW );
        gl.glPushMatrix();
        gl.glTranslated( font.getHeight() * 0.1, (h - font.getAscent() + font.getDescent()) / 2.0, 0 );
        font.push( gl );
        font.renderChars( gl, mDrawText );

        int tw;

        if( mDrawText.length() >= mMaxLength ) {
            tw = (int)font.getCharsWidth( mDrawText.toCharArray(), 0, mMaxLength - 1 );
        }else{
            tw = (int)font.getCharsWidth( mDrawText );
        }

        font.pop( gl );
        gl.glPopMatrix();
        
        if( mHasFocus ) {
            gl.glColor4f( foreground.r(), foreground.g(), foreground.b(), 0.3f );
            gl.glBegin( GL_QUADS );
            gl.glVertex2i( tw + 5, 3 );
            gl.glVertex2i( tw + 10, 3 );
            gl.glVertex2i( tw + 10, h - 3 );
            gl.glVertex2i( tw + 5, h - 3 );
            gl.glEnd();
        }

        foreground.apply( gl );
        gl.glLineWidth( 1f );
        gl.glBegin( GL_LINE_LOOP );
        gl.glVertex2i( 1, 1 );
        gl.glVertex2i( w, 1 );
        gl.glVertex2i( w, h );
        gl.glVertex2i( 1, h );
        gl.glEnd();
    }
    
    
    private final class KeyHandler extends GKeyAdapter {
        
        @Override
        public void keyTyped( GKeyEvent e ) {
            if( e.id() == GKeyEvent.KEY_TYPED ) {
                char c = e.getKeyChar();

                if( c >= ' ' && c <= '~' ) {
                    if( mText.length() >= mMaxLength ) {
                        mText.setLength( mMaxLength - 1 );
                    }

                    mText.append( c );
                    mDrawText = mText.toString();
                }else if( c == 8 ) {
                    mText.setLength( Math.max( 0, mText.length() - 1 ) );
                    mDrawText = mText.toString();
                }
            }
            
            e.consume();
        }
        
    }

    
    private final class MouseHandler extends GMouseAdapter {
        @Override
        public void mousePressed( GMouseEvent e ) {
            requestFocus();
        }
    }
    
}
