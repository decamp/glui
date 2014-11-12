package bits.draw3d;

import bits.glui.GGraphics;

import javax.media.opengl.*;
import java.util.Arrays;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2GL3.*;


/**
 * @author Philip DeCamp
 */
public interface RenderState {

    public void push();
    public void pop();
    public void apply();
    public int  stackDepth();


    public static class Blend extends Stack<Blend> {
        public boolean mOn       = false;
        public int     mSrcRgb   = GL_ONE;
        public int     mDstRgb   = GL_ZERO;
        public int     mSrcAlpha = GL_ONE;
        public int     mDstAlpha = GL_ZERO;

        private final GGraphics mG;


        public Blend( GGraphics g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private Blend() {
            mG = null;
        }


        public void set( boolean on ) {
            mOn = on;
            apply();
        }


        public void set( boolean on, int src, int dst ) {
            mOn = on;
            mSrcRgb = src;
            mSrcAlpha = src;
            mDstRgb = dst;
            mDstAlpha = dst;
            apply();
        }


        public void set( boolean on, int srcRgb, int dstRgb, int srcAlpha, int dstAlpha ) {
            mOn       = on;
            mSrcRgb   = srcRgb;
            mDstRgb   = dstRgb;
            mSrcAlpha = srcAlpha;
            mDstAlpha = dstAlpha;
            apply();
        }

        @Override
        public void apply() {
            GL gl = mG.mGl;
            if( mOn ) {
                gl.glEnable( GL_BLEND );
            } else {
                gl.glDisable( GL_BLEND );
            }
            gl.glBlendFuncSeparate( mSrcRgb, mDstRgb, mSrcAlpha, mDstAlpha );
        }

        @Override
        Blend alloc() {
            return new Blend();
        }

        @Override
        void copy( Blend a ) {
            mOn       = a.mOn;
            mSrcRgb   = a.mSrcRgb;
            mDstRgb   = a.mDstRgb;
            mSrcAlpha = a.mSrcAlpha;
            mDstAlpha = a.mDstAlpha;
        }

    }


    public static class BlendColor extends Stack<BlendColor> {
        public float mRed;
        public float mGreen;
        public float mBlue;
        public float mAlpha;

        private final GGraphics mG;


        public BlendColor( GGraphics g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private BlendColor() {
            mG = null;
        }



        public void set( float red, float green, float blue, float alpha ) {
            mRed   = red;
            mGreen = green;
            mBlue  = blue;
            mAlpha = alpha;
            apply();
        }

        @Override
        public void apply() {
            mG.mGl.glBlendColor( mRed, mGreen, mBlue, mAlpha );
        }


        @Override
        BlendColor alloc() {
            return new BlendColor();
        }

        @Override
        void copy( BlendColor a ) {
            mRed = a.mRed;
            mGreen = a.mGreen;
            mBlue = a.mBlue;
            mAlpha = a.mAlpha;
        }
    }


    public static class ColorMask extends Stack<ColorMask> {
        public boolean mRed;
        public boolean mGreen;
        public boolean mBlue;
        public boolean mAlpha;

        private final GGraphics mG;


        public ColorMask( GGraphics g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private ColorMask() {
            mG = null;
        }


        public void set( boolean red, boolean green, boolean blue, boolean alpha ) {
            mRed = red;
            mGreen = green;
            mBlue = blue;
            mAlpha = alpha;
            apply();
        }

        @Override
        public void apply() {
            mG.mGl.glColorMask( mRed, mGreen, mBlue, mAlpha );
        }


        @Override
        ColorMask alloc() {
            return new ColorMask();
        }

        @Override
        void copy( ColorMask a ) {
            mRed = a.mRed;
            mGreen = a.mGreen;
            mBlue = a.mBlue;
            mAlpha = a.mAlpha;
        }
    }


    public static class CullFace extends Stack<CullFace> {
        public boolean mOn   = false;
        public int     mFunc = GL_LESS;

        private final GGraphics mG;


        public CullFace( GGraphics g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private CullFace() {
            mG = null;
        }


        public void set( boolean on ) {
            mOn = on;
            apply();
        }

        @Override
        public void apply() {
            GL gl = mG.mGl;
            if( mOn ) {
                gl.glEnable( GL_CULL_FACE );
            } else {
                gl.glDisable( GL_CULL_FACE );
            }
        }

        @Override
        CullFace alloc() {
            return new CullFace();
        }

        @Override
        void copy( CullFace a ) {
            mOn   = a.mOn;
        }
    }


    public static class DepthMask extends Stack<DepthMask> {

        public boolean mOn = false;

        private final GGraphics mG;


        public DepthMask( GGraphics g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private DepthMask() {
            mG = null;
        }


        public void set( boolean on ) {
            mOn = on;
            apply();
        }

        @Override
        public void apply() {
            mG.mGl.glDepthMask( mOn );
        }

        @Override
        DepthMask alloc() {
            return new DepthMask();
        }


        void copy( DepthMask copy ) {
            mOn = copy.mOn;
        }
    }


    public static class DepthTest extends Stack<DepthTest> {
        public boolean mOn   = false;
        public int     mFunc = GL_LESS;

        private final GGraphics mG;


        public DepthTest( GGraphics g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private DepthTest() {
            mG = null;
        }


        public void set( boolean on ) {
            mOn = on;
            apply();
        }


        public void set( boolean on, int func ) {
            mOn = on;
            mFunc = func;
            apply();
        }

        @Override
        public void apply() {
            GL gl = mG.mGl;
            if( mOn ) {
                gl.glEnable( GL_DEPTH_TEST );
            } else {
                gl.glDisable( GL_DEPTH_TEST );
            }
            gl.glDepthFunc( mFunc );
        }

        @Override
        DepthTest alloc() {
            return new DepthTest();
        }

        @Override
        void copy( DepthTest a ) {
            mOn   = a.mOn;
            mFunc = a.mFunc;
        }
    }


    public static class PolygonOffset extends Stack<PolygonOffset> {
        public boolean mFillOn  = false;
        public boolean mLineOn  = false;
        public boolean mPointOn = false;
        public float   mFactor  = 0f;
        public float   mUnits   = 0f;

        private final GGraphics mG;


        public PolygonOffset( GGraphics g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private PolygonOffset() {
            mG = null;
        }


        public void set( boolean on ) {
            mFillOn = mLineOn = mPointOn = on;
            apply();
        }


        public void set( boolean fillOn, boolean lineOn, boolean pointOn ) {
            mFillOn  = fillOn;
            mLineOn  = lineOn;
            mPointOn = pointOn;
            apply();
        }


        public void set( boolean on, float factor, float units ) {
            mFillOn = mLineOn = mPointOn = on;
            mFactor = factor;
            mUnits  = units;
            apply();
        }


        public void set( boolean fillOn, boolean lineOn, boolean pointOn, float factor, float units ) {
            mFillOn  = fillOn;
            mLineOn  = lineOn;
            mPointOn = pointOn;
            mFactor  = factor;
            mUnits   = units;
            apply();
        }

        @Override
        public void apply() {
            GL gl = mG.mGl;
            if( mFillOn ) {
                gl.glEnable( GL_POLYGON_OFFSET_FILL );
            } else {
                gl.glDisable( GL_POLYGON_OFFSET_FILL );
            }
            if( mLineOn ) {
                gl.glEnable( GL_POLYGON_OFFSET_LINE );
            } else {
                gl.glDisable( GL_POLYGON_OFFSET_LINE );
            }
            if( mPointOn ) {
                gl.glEnable( GL_POLYGON_OFFSET_POINT );
            } else {
                gl.glDisable( GL_POLYGON_OFFSET_POINT );
            }
            gl.glPolygonOffset( mFactor, mUnits );
        }

        @Override
        PolygonOffset alloc() {
            return new PolygonOffset();
        }

        @Override
        void copy( PolygonOffset a ) {
            mFillOn  = a.mFillOn;
            mLineOn  = a.mLineOn;
            mPointOn = a.mPointOn;
            mFactor  = a.mFactor;
            mUnits   = a.mUnits;
        }
    }


    public static class ScissorTest extends Stack<ScissorTest> {
        public boolean mOn      = false;
        public int[]   mScissor = { 0, 0, 1, 1 };

        private final GGraphics mG;


        public ScissorTest( GGraphics g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private ScissorTest() {
            mG = null;
        }

        public void set( boolean on ) {
            mOn = on;
            apply();
        }


        public void set( boolean on, int x, int y, int w, int h ) {
            mOn = on;
            mScissor[0] = x;
            mScissor[1] = y;
            mScissor[2] = w;
            mScissor[3] = h;
            apply();
        }

        @Override
        public void apply() {
            GL gl = mG.mGl;
            if( mOn ) {
                gl.glEnable( GL_SCISSOR_TEST );
            } else {
                gl.glDisable( GL_SCISSOR_TEST );
            }
            gl.glScissor( mScissor[0], mScissor[1], mScissor[2], mScissor[3] );
        }

        @Override
        ScissorTest alloc() {
            return new ScissorTest();
        }

        @Override
        void copy( ScissorTest a ) {
            mOn = a.mOn;
            System.arraycopy( a.mScissor, 0, mScissor, 0, 4 );
        }
    }


    public static class StencilTest extends Stack<StencilTest> {
        public boolean mOn        = false;
        public int     mFrontFunc = GL_ALWAYS;
        public int     mFrontRef  = 0;
        public int     mFrontMask = 0xFFFFFFFF;
        public int     mBackFunc  = GL_ALWAYS;
        public int     mBackRef   = 0;
        public int     mBackMask  = 0xFFFFFFFF;

        private final GGraphics mG;


        public StencilTest( GGraphics g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private StencilTest() {
            mG = null;
        }



        public void set( boolean on ) {
            mOn = on;
            apply();
        }


        public void set( boolean on, int func, int ref, int mask ) {
            mOn = on;
            mFrontFunc = mBackFunc = func;
            mFrontRef = mBackRef = ref;
            mFrontMask = mBackMask = mask;
            apply();
        }


        public void setFront( int frontFunc, int frontRef, int frontMask ) {
            mFrontFunc = frontFunc;
            mFrontRef = frontRef;
            mFrontMask = frontMask;
        }


        public void setBack( int backFunc, int backRef, int backMask ) {
            mBackFunc = backFunc;
            mBackRef = backRef;
            mBackMask = backMask;
        }

        @Override
        public void apply() {
            GL3 gl = mG.mGl;
            if( mOn ) {
                gl.glEnable( GL_STENCIL_TEST );
            } else {
                gl.glDisable( GL_STENCIL_TEST );
            }
            gl.glStencilFuncSeparate( GL_FRONT, mFrontFunc, mFrontRef, mFrontMask );
            gl.glStencilFuncSeparate( GL_BACK, mBackFunc, mBackRef, mBackMask );
        }


        @Override
        StencilTest alloc() {
            return new StencilTest();
        }

        @Override
        void copy( StencilTest copy ) {
            mOn = copy.mOn;
            mFrontFunc = copy.mFrontFunc;
            mFrontRef = copy.mFrontRef;
            mFrontMask = copy.mFrontMask;
            mBackFunc = copy.mBackFunc;
            mBackRef = copy.mBackRef;
            mBackMask = copy.mBackMask;
        }
    }


    public static class StencilOp extends Stack<StencilOp> {
        public int mFrontStencilFail = GL_KEEP;
        public int mFrontDepthFail   = GL_KEEP;
        public int mFrontPass        = GL_KEEP;
        public int mBackStencilFail  = GL_KEEP;
        public int mBackDepthFail    = GL_KEEP;
        public int mBackPass         = GL_KEEP;

        private final GGraphics mG;


        public StencilOp( GGraphics g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private StencilOp() {
            mG = null;
        }


        public void set( int stencilFail, int depthFail, int pass ) {
            mFrontStencilFail = mBackStencilFail = stencilFail;
            mFrontDepthFail = mBackDepthFail = depthFail;
            mFrontPass = mBackPass = pass;
            apply();
        }


        public void apply() {
            GL3 gl = mG.mGl;
            gl.glStencilOpSeparate( GL_FRONT, mFrontStencilFail, mFrontDepthFail, mFrontPass );
            gl.glStencilOpSeparate( GL_BACK, mBackStencilFail, mBackStencilFail, mBackPass );
        }

        @Override
        StencilOp alloc() {
            return new StencilOp();
        }

        @Override
        void copy( StencilOp a ) {
            mFrontStencilFail = a.mFrontStencilFail;
            mFrontDepthFail = a.mFrontDepthFail;
            mFrontPass = a.mFrontPass;
            mBackStencilFail = a.mBackStencilFail;
            mBackDepthFail = a.mBackDepthFail;
            mBackPass = a.mBackPass;
        }
    }


    public static class Viewport extends Stack<Viewport> {
        public int mX = 0;
        public int mY = 0;
        public int mW = 1;
        public int mH = 1;

        private final GGraphics mG;


        public Viewport( GGraphics g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private Viewport() {
            mG = null;
        }


        public void set( int x, int y, int w, int h ) {
            mX = x;
            mY = y;
            mW = w;
            mH = h;
            apply();
        }

        @Override
        public void apply() {
            mG.mGl.glViewport( mX, mY, mW, mH );
        }

        @Override
        Viewport alloc() {
            return new Viewport();
        }

        @Override
        void copy( Viewport a ) {
            mX = a.mX;
            mY = a.mY;
            mW = a.mW;
            mH = a.mH;
        }

    }


    static abstract class Stack<T extends Stack> implements RenderState {

        protected static final int DEFAULT_CAP = 8;

        Stack[] mArr;
        int mPos;

        Stack() {
            mArr = null;
            mPos = 0;
        }


        Stack( int initialCapacity ) {
            mArr = new Stack[ initialCapacity ];
            for( int i = 0; i < mArr.length; i++ ) {
                mArr[i] = alloc();
            }
        }


        @SuppressWarnings( "unchecked" )
        public void push() {
            ensureCapacity( mPos + 1 );
            mArr[mPos++].copy( this );
        }

        @SuppressWarnings( "unchecked" )
        public void pop() {
            copy( (T)mArr[--mPos] );
            apply();
        }


        public int stackDepth() {
            return mPos;
        }


        abstract T alloc();


        abstract void copy( T item );

        @SuppressWarnings( "unchecked" )
        void ensureCapacity( int minCap ) {
            if( minCap <= mArr.length ) {
                return;
            }

            final int oldCap = mArr.length;
            int newCap = ( oldCap * 3 ) / 2 + 1;
            if( newCap < minCap ) {
                newCap = minCap;
            }
            mArr = Arrays.copyOf( mArr, newCap );
            for( int i = oldCap; i < newCap; i++ ) {
                mArr[i] = alloc();
            }
        }
    }

}
