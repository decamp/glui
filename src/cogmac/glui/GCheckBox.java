package cogmac.glui;

import java.awt.*;
import java.awt.event.*;

import javax.media.opengl.*;

import static javax.media.opengl.GL.*;

import cogmac.glui.text.FontTexture;


public class GCheckBox extends GButton implements GSelectable {

    private static final int DEFAULT_FONT_SIZE = 12;
    private static final Font DEFAULT_FONT     = new Font("Verdana", Font.PLAIN, DEFAULT_FONT_SIZE);
    
    private boolean mSelected = false;
    

    public GCheckBox(String label) {
        this(label, false);
    }
    
    
    public GCheckBox(String label, boolean selected) {
        super(label);
        
        mSelected = selected;
        
        addActionListener(new ActionHandler());
    }
    
    

    public void setSelected(boolean selected) {
        mSelected = selected;
    }
    
    public boolean isSelected() {
        return mSelected;
    }
    
    @Override
    public void paint(GGraphics g) {
        final GL gl = g.gl();

        final int w = width();
        final int h = height();

        GColor background = background();
        GColor foreground = foreground();
        
        if(background != null) {
            background.apply(gl);
            gl.glBegin(GL_QUADS);
            gl.glVertex2i(0, 0);
            gl.glVertex2i(w, 0);
            gl.glVertex2i(w, h);
            gl.glVertex2i(0, h);
            gl.glEnd();
        }
        
        foreground().apply(gl);
        
        final int margin = 2;

        gl.glLineWidth(1f);
        gl.glBegin(GL_LINE_LOOP);
        gl.glVertex2i(margin, margin);
        gl.glVertex2i(h - margin, margin);
        gl.glVertex2i(h - margin, h - margin);
        gl.glVertex2i(margin, h - margin);
        gl.glEnd();

        if(mSelected) {
            gl.glBegin(GL_LINES);
            gl.glVertex2i(margin * 2, margin * 2);
            gl.glVertex2i(h - margin * 2, h - margin * 2);
            gl.glVertex2i(margin * 2, h - margin * 2);
            gl.glVertex2i(h - margin * 2, margin * 2);
            gl.glEnd();
        }

        FontTexture tex = g.fontManager().getFontTexture(font(), GLContext.getCurrent());
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glTranslated(h, (int)(h - tex.getAscent() * 0.66f) / 2, 0);
        
        tex.push(gl);
        tex.renderChars(gl, text());
        tex.pop(gl);

        gl.glPopMatrix();
    }

    
    
    private final class ActionHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            mSelected = !mSelected;
            repaint();
        }
    }
    
}