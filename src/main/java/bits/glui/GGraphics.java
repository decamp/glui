package bits.glui;

import javax.media.opengl.*;

import bits.draw3d.*;
import bits.draw3d.shader.ShaderManager;
import bits.glui.text.FontManager;
import bits.math3d.*;

import java.nio.*;


/**
 * @author decamp
 */
public class GGraphics {

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

    public final RenderState.Blend         mBlend         = new RenderState.Blend( this );
    public final RenderState.BlendColor    mBlendColor    = new RenderState.BlendColor( this );
    public final RenderState.ColorMask     mColorMask     = new RenderState.ColorMask( this );
    public final RenderState.CullFace      mCullFace      = new RenderState.CullFace( this );
    public final RenderState.DepthMask     mDepthMask     = new RenderState.DepthMask( this );
    public final RenderState.DepthTest     mDepthTest     = new RenderState.DepthTest( this );
    public final RenderState.PolygonOffset mPolygonOffset = new RenderState.PolygonOffset( this );
    public final RenderState.ScissorTest   mScissorTest   = new RenderState.ScissorTest( this );
    public final RenderState.StencilTest   mStencilTest   = new RenderState.StencilTest( this );
    public final RenderState.StencilOp     mStencilOp     = new RenderState.StencilOp( this );
    public final RenderState.Viewport      mViewport      = new RenderState.Viewport( this );

    public final ShaderManager mShaderMan;
    public final FontManager   mFontMan;

    public final Vec2        mWorkVec2   = new Vec2();
    public final Vec3        mWorkVec3   = new Vec3();
    public final Vec4        mWorkVec4   = new Vec4();
    public final Mat3        mWorkMat3   = new Mat3();
    public final Mat4        mWorkMat4   = new Mat4();
    public final ByteBuffer  mWorkBytes  = DrawUtil.alloc( 16 * 4 );
    public final FloatBuffer mWorkFloats = DrawUtil.allocFloats( 16 );

    private final DrawStream mStream;
    private final int[]  mTexUnits = new int[8];


    private RenderState[] mRenderStates = { mBlend, mBlendColor, mColorMask,
                                            mCullFace, mDepthMask, mDepthTest,
                                            mPolygonOffset, mScissorTest, mStencilTest,
                                            mStencilOp, mViewport };

    public GGraphics() {
        mFontMan   = new FontManager();
        mShaderMan = new ShaderManager();
        mStream    = new DrawStream();
    }



    public void checkErr() {
        DrawUtil.checkErr( mGl );
    }


    public DrawStream drawStream() {
        return mStream;
    }

    /**
     * GGraphics keeps a texture registry. This method will bind a texture id to a given texture unit.
     * Users don't have to use this registry. However, DrawStream uses it, so anyone trying to
     * use DrawStream will need to use this method to bind textures in most cases.     *
     *
     * @param unit       Texture number from [0, 8).
     * @param textureId  Id of texture to bind to unit.
     */
    public void texUnit( int unit, int textureId ) {
        mTexUnits[unit] = textureId;
    }

    /**
     * GGraphics keeps a texture registry. Returns the texture bound to a given texture unit.
     *
     * @param unit       Texture unit number from [0, 8).
     */
    public int texUnit( int unit ) {
        return mTexUnits[unit];
    }


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
