package bits.glui;

import javax.media.opengl.*;

import bits.glui.text.FontManager;
import bits.math3d.MatStack;


/**
 * @author decamp
 */
public abstract class GGraphics {

    public GL3            mGl;
    public GLAutoDrawable mGld;

    public final MatStack mView = new MatStack();
    public final MatStack mProj = new MatStack();

    /**
     * Holds the size of the entire target render space,
     * including those areas that might be rendered in a
     * different context or process.
     */
    public final Rect mContextViewport = new Rect();
    public final FontManager mFontManager;

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


    public GGraphics() {
        mFontManager = new FontManager();
    }


    public void init( GLAutoDrawable gld, Rect optContextViewport ) {
        mGld = gld;
        mGl  = (GL3)gld.getGL();
        if( optContextViewport != null ) {
            mContextViewport.set( optContextViewport );
        }
    }


    public FontManager fontManager() {
        return mFontManager;
    }

}
