package bits.glui;

import bits.math3d.Vec;
import bits.math3d.Vec4;

import java.util.ArrayList;
import java.util.List;


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


    public List<Vec4> listColors() {
        ArrayList<Vec4> ret = new ArrayList<Vec4>();
        ret.add( mBackground );
        ret.add( mForeground );
        ret.add( mRolloverBackground );
        ret.add( mRolloverForeground );
        ret.add( mPressedBackground  );
        ret.add( mPressedForeground  );
        ret.add( mSelectedBackground );
        ret.add( mSelectedForeground );
        ret.add( mDisabledBackground );
        ret.add( mDisabledForeground );
        return ret;
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
