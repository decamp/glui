package cogmac.glui;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import cogmac.glui.text.FontManager;


/**
 * @author decamp
 */
public interface GGraphics {
    public GL gl();
    public FontManager fontManager();
    public GLAutoDrawable drawable();
    public Box contextViewport();
}
