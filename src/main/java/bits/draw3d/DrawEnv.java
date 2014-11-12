package bits.draw3d;

import javax.media.opengl.*;

import bits.draw3d.shader.ShaderManager;
import bits.glui.Rect;
import bits.draw3d.text.FontManager;
import bits.math3d.*;

import java.nio.*;


/**
 * @author decamp
 */
public class DrawEnv {

    public GL3            mGl;
    public GLAutoDrawable mGld;
    /**
     * Holds the size of the entire target render space,
     * including those areas that might be rendered in a
     * different context or process.
     */
    public final Rect mContextViewport = new Rect();

    public final MatStack mView     = new MatStack();
    public final MatStack mProj     = new MatStack();
    public final MatStack mColorMat = new MatStack();
    public final MatStack mTexMat   = new MatStack();

    public final DrawSetting.Blend         mBlend         = new DrawSetting.Blend( this );
    public final DrawSetting.BlendColor    mBlendColor    = new DrawSetting.BlendColor( this );
    public final DrawSetting.ColorMask     mColorMask     = new DrawSetting.ColorMask( this );
    public final DrawSetting.CullFace      mCullFace      = new DrawSetting.CullFace( this );
    public final DrawSetting.DepthMask     mDepthMask     = new DrawSetting.DepthMask( this );
    public final DrawSetting.DepthTest     mDepthTest     = new DrawSetting.DepthTest( this );
    public final DrawSetting.LineWidth     mLineWidth     = new DrawSetting.LineWidth( this );
    public final DrawSetting.PolygonOffset mPolygonOffset = new DrawSetting.PolygonOffset( this );
    public final DrawSetting.ScissorTest   mScissorTest   = new DrawSetting.ScissorTest( this );
    public final DrawSetting.StencilTest   mStencilTest   = new DrawSetting.StencilTest( this );
    public final DrawSetting.StencilOp     mStencilOp     = new DrawSetting.StencilOp( this );
    public final DrawSetting.Viewport      mViewport      = new DrawSetting.Viewport( this );

    public final ShaderManager mShaderMan;
    public final FontManager   mFontMan;

    public final Vec2        mWorkVec2   = new Vec2();
    public final Vec3        mWorkVec3   = new Vec3();
    public final Vec4        mWorkVec4   = new Vec4();
    public final Mat3        mWorkMat3   = new Mat3();
    public final Mat4        mWorkMat4   = new Mat4();
    public final ByteBuffer  mWorkBytes  = DrawUtil.alloc( 16 * 4 );
    public final FloatBuffer mWorkFloats = DrawUtil.allocFloats( 16 );
    public final Rect        mWorkRect   = new Rect();

    private final DrawStream mStream;


    private DrawSetting[] mSettings = { mBlend, mBlendColor, mColorMask,
                                        mCullFace, mDepthMask, mDepthTest,
                                        mPolygonOffset, mScissorTest, mStencilTest,
                                        mStencilOp, mViewport };

    public DrawEnv() {
        mFontMan = new FontManager();
        mShaderMan = new ShaderManager();
        mStream = new DrawStream();
    }


    public void checkErr() {
        DrawUtil.checkErr( mGl );
    }


    public DrawStream drawStream() {
        return mStream;
    }



    /**
     * Should be called every frame.

     * @param gld                   Sets the GLContext.
     * @param optContextViewport    Sets the viewport of the whole rendering if doing tiled rendering.
     */
    public void init( GLAutoDrawable gld, Rect optContextViewport ) {
        mGld = gld;
        mGl = (GL3)gld.getGL();
        if( optContextViewport != null ) {
            mContextViewport.set( optContextViewport );
        } else {
            mContextViewport.x0 = 0;
            mContextViewport.y0 = 0;
            mContextViewport.x1 = gld.getSurfaceWidth();
            mContextViewport.y1 = gld.getSurfaceHeight();
        }
        mStream.init( this );
    }


    public void dispose( GLAutoDrawable gld ) {}


    public FontManager fontManager() {
        return mFontMan;
    }

}
