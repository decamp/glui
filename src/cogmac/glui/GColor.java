package cogmac.glui;

import java.awt.Color;

import javax.media.opengl.GL;
import static javax.media.opengl.GL.*;

/**
 * @author decamp
 */
public class GColor {

    public static final GColor BLACK       = new GColor(0f,0f,0f);
    public static final GColor BLUE        = new GColor(Color.BLUE);
    public static final GColor CYAN        = new GColor(Color.CYAN);
    public static final GColor DARK_GRAY   = new GColor(Color.DARK_GRAY);
    public static final GColor GRAY        = new GColor(Color.GRAY);
    public static final GColor GREEN       = new GColor(Color.GREEN);
    public static final GColor LIGHT_GRAY  = new GColor(Color.LIGHT_GRAY);
    public static final GColor MAGENTA     = new GColor(Color.MAGENTA);
    public static final GColor ORANGE      = new GColor(Color.ORANGE);
    public static final GColor PINK        = new GColor(Color.PINK);
    public static final GColor RED         = new GColor(Color.RED);
    public static final GColor WHITE       = new GColor(1f,1f,1f);
    public static final GColor YELLOW      = new GColor(Color.YELLOW);
    public static final GColor CLEAR_WHITE = new GColor( 1f, 1f, 1f, 0f );
    public static final GColor CLEAR_BLACK = new GColor( 0f, 0f, 0f, 1f );
    
    
    public static GColor fromCurrent(GL gl) {
        float[] arr = new float[4];
        gl.glGetFloatv(GL_CURRENT_COLOR, arr, 0);
        return new GColor(arr[0], arr[1], arr[2], arr[3]);
    }
    
    public static GColor fromRgba(float[] rgba, int off) {
        return new GColor(rgba[off], rgba[off+1], rgba[off+2], rgba[off+3]);
    }
    
    public static GColor fromRgb(float[] rgb, int off) {
        return new GColor(rgb[off], rgb[off+1], rgb[off+2], 1f);
    }
        
    public static GColor fromRgba(double[] rgba, int off) {
        return new GColor((float)rgba[off], (float)rgba[off+1], (float)rgba[off+2], (float)rgba[off+3]);
    }
    
    public static GColor fromRgb(double[] rgb, int off) {
        return new GColor((float)rgb[off], (float)rgb[off+1], (float)rgb[off+2], 1f);
    }
    
    public static GColor fromRgba(int rgba) {
        return new GColor( (rgba >>> 24)         / 255f,
                           ((rgba >> 16) & 0xFF) / 255f,
                           ((rgba >>  8) & 0xFF) / 255f,
                           ((rgba      ) & 0xFF) / 255f );
    }
    
    public static GColor fromArgb(int argb) {
        return new GColor( ((argb >>  16) & 0xFF) / 255f,
                           ((argb >>   8) & 0xFF) / 255f,
                           ((argb       ) & 0xFF) / 255f,
                           ((argb >>> 24)         / 255f ));
    }
    
    public static GColor fromBgra(int bgra) {
        return new GColor( ((bgra >>   8) & 0xFF) / 255f,
                           ((bgra >>  16) & 0xFF) / 255f,
                           ((bgra >>> 24)       ) / 255f,
                           ((bgra       ) & 0xFF) / 255f);
    }
    
    public static GColor fromHsb(float hue, float saturation, float brightness) {
        return fromHsba(hue, saturation, brightness, 1.0f);
    }
    
    public static GColor fromHsba(float hue, float saturation, float brightness, float alpha) {
        if(saturation == 0)
            return new GColor(brightness, brightness, brightness, alpha);
        
        float h = (hue - (float)Math.floor(hue)) * 6.0f;
        float f = h - (float)Math.floor(h);
        float p = brightness * (1.0f - saturation);
        float q = brightness * (1.0f - saturation * f);
        float t = brightness * (1.0f - (saturation * (1.0f - f)));

        switch((int) h) {
        case 0:
            return new GColor(brightness, t, p, alpha);

        case 1:
            return new GColor(q, brightness, p, alpha);

        case 2:
            return new GColor(p, brightness, t, alpha);

        case 3:
            return new GColor(p, q, brightness, alpha);

        case 4:
            return new GColor(t, p, brightness, alpha);

        case 5:
            return new GColor(brightness, p, q, alpha);

        default:
            return new GColor(0, 0, 0, alpha);

        }
    }
    
    
    
    
    private final float mRed;
    private final float mGreen;
    private final float mBlue;
    private final float mAlpha;
    
    
    public GColor(float r, float g, float b) {
        this(r, g, b, 1f);
    }
    
    public GColor(float r, float g, float b, float a) {
        mRed   = r;
        mGreen = g;
        mBlue  = b;
        mAlpha = a;
    }

    public GColor(double r, double g, double b) {
        this(r, g, b, 1.0);
    }
    
    public GColor(double r, double g, double b, double a) {
        mRed   = (float)r;
        mGreen = (float)g;
        mBlue  = (float)b;
        mAlpha = (float)a;
    }
    
    public GColor(Color c) {
        mRed   = c.getRed()   / 255f;
        mGreen = c.getGreen() / 255f;
        mBlue  = c.getBlue()  / 255f;
        mAlpha = c.getAlpha() / 255f;
    }
    
    
    public float r() {
        return mRed;
    }
    
    public float g() {
        return mGreen;
    }
    
    public float b() {
        return mBlue;
    }
    
    public float a() {
        return mAlpha;
    }
    
    public GColor r(double r) {
        return new GColor((float)r, mGreen, mBlue, mAlpha);
    }
    
    public GColor g(double g) {
        return new GColor(mRed, (float)g, mBlue, mAlpha);
    }
    
    public GColor b(double b) {
        return new GColor(mRed, mGreen, (float)b, mAlpha);
    }
    
    public GColor a(double a) {
        return new GColor(mRed, mGreen, mBlue, (float)a);
    }
    
    
    
    public final void apply(GL gl) {
        gl.glColor4f(mRed, mGreen, mBlue, mAlpha);
    }
    
    public Color toAwt() {
        return new Color(mRed, mGreen, mBlue, mAlpha);
    }
    
}
