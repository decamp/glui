package bits.glui;

import bits.math3d.Vec;
import bits.math3d.Vec4;

public class ButtonPalette {

    public static final ButtonPalette DEFAULT = new ButtonPalette();

    public final Vec4 mBackground         = new Vec4();
    public final Vec4 mForeground         = new Vec4();
    public final Vec4 mRolloverBackground = new Vec4();
    public final Vec4 mRolloverForeground = new Vec4();
    public final Vec4 mPressedBackground  = new Vec4();
    public final Vec4 mPressedForeground  = new Vec4();
    public final Vec4 mSelectedBackground = new Vec4();
    public final Vec4 mSelectedForeground = new Vec4();
    public final Vec4 mDisabledBackground = new Vec4();
    public final Vec4 mDisabledForeground = new Vec4();


    public ButtonPalette() {}


    public void scaleAlpha( float scale ) {
        scaleAlpha( mBackground, scale );
        scaleAlpha( mForeground,         scale );
        scaleAlpha( mRolloverBackground, scale );
        scaleAlpha( mRolloverForeground, scale );
        scaleAlpha( mPressedBackground,  scale );
        scaleAlpha( mPressedForeground,  scale );
        scaleAlpha( mSelectedBackground, scale );
        scaleAlpha( mSelectedForeground, scale );
        scaleAlpha( mDisabledBackground, scale );
        scaleAlpha( mDisabledForeground, scale );
    }


    private static void scaleAlpha( Vec4 color, float scale ) {
        color.w = Math.max( 0, Math.min( 1, scale * color.w ) );
    }


    static {
        ButtonPalette b = DEFAULT;
        Vec.put( 1,     1,     1,     1,     b.mForeground );
        Vec.put( 0,     0,     0.15f, 0.5f,  b.mBackground );
        Vec.put( 1,     1,     1,     1,     b.mRolloverForeground );
        Vec.put( 0.4f,  0.4f,  0,     0.75f, b.mRolloverBackground );
        Vec.put( 1, 1,  1,     1,            b.mPressedForeground );
        Vec.put( 0.2f,  0.2f,  0,     0.75f, b.mPressedBackground );
        Vec.put( 1,     1,     1,     1,     b.mSelectedForeground );
        Vec.put( 0.15f, 0.15f, 0.6f,  0.5f,  b.mSelectedBackground );
        Vec.put( 0.5f,  0.5f,  0.5f,  0.5f,  b.mDisabledForeground );
        Vec.put( 0.1f,  0.1f,  0.1f,  0.5f,  b.mDisabledBackground );
    }

}
