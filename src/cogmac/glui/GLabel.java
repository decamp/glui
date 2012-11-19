package cogmac.glui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

import javax.media.opengl.*;

import cogmac.glui.event.GPaintListener;
import cogmac.glui.text.FontTexture;

import static javax.media.opengl.GL.*;


/**
 * @author decamp
 */
public class GLabel extends GPanel implements GPaintListener {

    private static final int DEFAULT_FONT_SIZE = 12;
    private static final Font DEFAULT_FONT = new Font("Verdana", Font.PLAIN, DEFAULT_FONT_SIZE);
    private static final GColor DEFAULT_FOREGROUND = new GColor(0,0,0);
    
    private ActionListener mCaster;
    private String mText;
    
    private float mHorPos   = 0.5f;
    private float mHorText  = 0.5f;
    private float mVertPos  = 0.5f;
    private float mVertText = 0.5f;
    
    
    public GLabel(String text) {
        mText = text;
        addPaintListener(this);
    }
    
    
    
    public GLabel text(String text) {
        mText = text;
        return this;
    }
    
    public String text() {
        return mText;
    }
        
    public GLabel horizontalAlignment(float backgroundPoint, float foregroundPoint) {
        mHorPos  = backgroundPoint;
        mHorText = foregroundPoint;
        return this;
    }  
    
    public GLabel verticalAlignment(float backgroundPoint, float foregroundPoint) {
        mVertPos  = backgroundPoint;
        mVertText = foregroundPoint;
        return this;
    }
    
    
    
    public void paint(GGraphics g) {
        GL gl = g.gl();
        FontTexture font = g.fontManager().getFontTexture(font(), GLContext.getCurrent());
        int w = width();
        int h = height();
        
        GColor foreground = foreground();
        GColor background = background();
        
        if(background != null) {
            background.apply(gl);
            gl.glBegin(GL_QUADS);
            gl.glVertex2i(0, 0);
            gl.glVertex2i(w, 0);
            gl.glVertex2i(w, h);
            gl.glVertex2i(0, h);
            gl.glEnd();
        }
        
        String s = mText;
        if(s == null || s.length() == 0)
            return;
        
        foreground.apply(gl);
        gl.glPushMatrix();
        
        float ht = font.getAscent() + font.getDescent();
        
        gl.glTranslatef( Math.round(w * mHorPos  - font.getCharsWidth(s) * mHorText), 
                         Math.round(h * mVertPos - ht * mVertText + font.getDescent()), 
                         0); 
        
        font.push(gl);
        font.renderChars(gl, s);
        font.pop(gl);
        
        gl.glPopMatrix();
    }



    @Deprecated
    public void setText(String text) {
        text(text);
    }
    
    @Deprecated
    public void setForeground(Color c) {
        if(c == null) {
            foreground((GColor)null);
        }else{
            foreground(new GColor(c));
        }
    }
    
    @Deprecated
    public void setBackground(Color c) {
        if(c == null) {
            background((GColor)null);
        }else{
            background(new GColor(c));
        }
    }

    @Deprecated
    public void setFont(Font font) {
        font(font);
    }

    @Deprecated
    public void setHorizontalAlignment(float backgroundPoint, float foregroundPoint) {
        horizontalAlignment(backgroundPoint, foregroundPoint);
    }  
    
    @Deprecated
    public void setVerticalAlignment(float backgroundPoint, float foregroundPoint) {
        verticalAlignment(backgroundPoint, foregroundPoint);
    }
    
}
