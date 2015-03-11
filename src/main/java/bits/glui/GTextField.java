/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui;

import javax.media.opengl.*;

import bits.draw3d.DrawEnv;
import bits.draw3d.DrawStream;
import bits.glui.event.*;
import bits.draw3d.text.FontTexture;
import bits.math3d.Vec4;


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
    public void paintComponent( DrawEnv g ) {
        final DrawStream s = g.drawStream();
        final int w = width();
        final int h = height();
        final Vec4 v = g.mWorkVec4;

        s.config( true, false, false );
        if( getBackground( v ) ) {
            s.color( v );
            s.beginQuads();
            s.vert( 0, 0 );
            s.vert( w, 0 );
            s.vert( w, h );
            s.vert( 0, h );
            s.end();
        }

        getForeground( v );
        s.color( v );
        FontTexture font = g.fontManager().getFontTexture( getFont(), GLContext.getCurrent() );

        float yy = Math.round( 0.5f * ( h - font.getAscent() + font.getDescent() ) );
        font.bind( g );
        font.renderChars( g, Math.round( font.getHeight() * 0.2f ), yy, 0, mDrawText );

        int tw;
        if( mDrawText.length() >= mMaxLength ) {
            tw = (int)font.getCharsWidth( mDrawText.toCharArray(), 0, mMaxLength - 1 );
        }else{
            tw = (int)font.getCharsWidth( mDrawText );
        }

        font.unbind( g );
        s.config( true, false, false );
        getForeground( v );

        if( mHasFocus ) {
            s.color( v.x, v.y, v.z, 0.3f );
            s.beginQuads();
            s.vert( tw + 5, 3 );
            s.vert( tw + 10, 3 );
            s.vert( tw + 10, h - 3 );
            s.vert( tw + 5, h - 3 );
            s.end();
        }

        s.color( v );
        g.mLineWidth.apply( 1f );
        s.beginLineLoop();
        s.vert( 1, 1 );
        s.vert( w - 1, 1 );
        s.vert( w - 1, h - 1 );
        s.vert( 1, h - 1 );
        s.end();
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
                } else if( c == 8 ) {
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
