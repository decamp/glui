//package bits.glui.util;
//
//import javax.media.opengl.*;
//
//import java.util.Arrays;
//
//import static javax.media.opengl.GL2.*;
//
//
///**
// * @author Philip DeCamp
// */
//public class GAttrib {
//
//    private static final int DEFAULT_CAP = 16;
//
//    public Blend       mBlend       = new Blend();
//    public BlendColor  mBlendColor  = new BlendColor();
//    public ColorMask   mColorMask   = new ColorMask();
//    public DepthMask   mDepthMask   = new DepthMask();
//    public DepthTest   mDepthTest   = new DepthTest();
//    public ScissorTest mScissorTest = new ScissorTest();
//    public StencilFunc mStencilFunc = new StencilFunc();
//    public StencilOp   mStencilOp   = new StencilOp();
//    public Viewport    mViewport    = new Viewport();
//
//    private Stack<Blend>       mBlendStack       = new Stack<Blend>      ( mBlend      , DEFAULT_CAP );
//    private Stack<BlendColor>  mBlendColorStack  = new Stack<BlendColor> ( mBlendColor , DEFAULT_CAP );
//    private Stack<ColorMask>   mColorMaskStack   = new Stack<ColorMask>  ( mColorMask  , DEFAULT_CAP );
//    private Stack<DepthMask>   mDepthMaskStack   = new Stack<DepthMask>  ( mDepthMask  , DEFAULT_CAP );
//    private Stack<DepthTest>   mDepthTestStack   = new Stack<DepthTest>  ( mDepthTest  , DEFAULT_CAP );
//    private Stack<ScissorTest> mScissorTestStack = new Stack<ScissorTest>( mScissorTest, DEFAULT_CAP );
//    private Stack<StencilFunc> mStencilFuncStack = new Stack<StencilFunc>( mStencilFunc, DEFAULT_CAP );
//    private Stack<StencilOp>   mStencilOpStack   = new Stack<StencilOp>  ( mStencilOp  , DEFAULT_CAP );
//    private Stack<Viewport>    mViewportStack    = new Stack<Viewport>   ( mViewport   , DEFAULT_CAP );
//
//
//
//    public void pushBlend() {
//        mBlend = mBlendStack.push();
//    }
//
//
//    public void blend( GL2ES2 gl, boolean on ) {
//        mBlend.mOn = on;
//        mBlend.apply( gl );
//    }
//
//
//    public void blend( GL2ES2 gl, boolean on, int src, int dst ) {
//        Blend a     = mBlend;
//        a.mOn       = on;
//        a.mSrcRgb   = src;
//        a.mSrcAlpha = src;
//        a.mDstRgb   = dst;
//        a.mDstAlpha = dst;
//        a.apply( gl );
//    }
//
//
//    public void blend( GL2ES2 gl, boolean on, int srcRgb, int dstRgb, int srcAlpha, int dstAlpha ) {
//        Blend a     = mBlend;
//        a.mOn       = on;
//        a.mSrcRgb   = srcRgb;
//        a.mDstRgb   = dstRgb;
//        a.mSrcAlpha = srcAlpha;
//        a.mDstAlpha = dstAlpha;
//        a.apply( gl );
//    }
//
//
//    public void popBlend( GL2ES2 gl ) {
//        mBlend = mBlendStack.pop();
//        mBlend.apply( gl );
//    }
//
//
//    public void pushBlendColor() {
//        mBlendColor = mBlendColorStack.push();
//    }
//
//
//    public void blendColor( GL2ES2 gl, float red, float green, float blue, float alpha ) {
//        BlendColor a = mBlendColor;
//        a.mRed   = red;
//        a.mGreen = green;
//        a.mBlue  = blue;
//        a.mAlpha = alpha;
//        a.apply( gl );
//    }
//
//
//    public void popBlendColor( GL2ES2 gl ) {
//        mBlendColor = mBlendColorStack.pop();
//        mBlendColor.apply( gl );
//    }
//
//
//    public void colorMask( boolean red, boolean green, boolean blue, boolean alpha ) {
//        ColorMask a = mColorMask;
//        a.mRed = red;
//        a.mGreen = green;
//        a.mBlue = blue;
//        a.mAlpha = alpha;
//    }
//
//
//
//    public interface Attrib {
//        public void apply( GL2ES2 gl );
//        public void push();
//        public void pop( GL2ES2 gl );
//        public void stackDepth();
//    }
//
//
//
//
//
//
//    interface Attrib2<T> {
//        Attrib2 alloc();
//        void   apply( GL2ES2 gl );
//        void   setOn( T a );
//    }
//
//
//    public static class Blend implements Attrib2<Blend> {
//        public boolean mOn       = false;
//        public int     mSrcRgb   = GL_ONE;
//        public int     mDstRgb   = GL_ZERO;
//        public int     mSrcAlpha = GL_ONE;
//        public int     mDstAlpha = GL_ZERO;
//
//        @Override
//        public Blend alloc() {
//            return new Blend();
//        }
//
//        @Override
//        public void apply( GL2ES2 gl ) {
//            if( mOn ) {
//                gl.glEnable( GL_BLEND );
//            } else {
//                gl.glDisable( GL_BLEND );
//            }
//            gl.glBlendFuncSeparate( mSrcRgb, mDstRgb, mSrcAlpha, mDstAlpha );
//        }
//
//        @Override
//        public void setOn( Blend a ) {
//            mOn = a.mOn;
//            mSrcRgb = a.mSrcRgb;
//            mDstRgb = a.mDstRgb;
//            mSrcAlpha = a.mSrcAlpha;
//            mDstAlpha = a.mDstAlpha;
//        }
//
//    }
//
//
//    public static class BlendColor implements Attrib2<BlendColor> {
//        public float mRed;
//        public float mGreen;
//        public float mBlue;
//        public float mAlpha;
//
//        @Override
//        public Attrib2 alloc() {
//            return new BlendColor();
//        }
//
//
//        public void apply( GL2ES2 gl ) {
//            gl.glBlendColor( mRed, mGreen, mBlue, mAlpha );
//        }
//
//
//        public void setOn( BlendColor a ) {
//            mRed = a.mRed;
//            mGreen = a.mGreen;
//            mBlue = a.mBlue;
//            mAlpha = a.mAlpha;
//        }
//
//    }
//
//
//    public static class ColorMask implements Attrib2<ColorMask> {
//        static Allocator<ColorMask> ALLOC = new Allocator<ColorMask>() {
//            public ColorMask alloc() { return new ColorMask(); }
//        };
//
//        public boolean mRed;
//        public boolean mGreen;
//        public boolean mBlue;
//        public boolean mAlpha;
//
//        @Override
//        public Attrib2 alloc() {
//            return new ColorMask();
//        }
//
//        @Override
//        public void apply( GL2ES2 gl ) {
//            gl.glColorMask( mRed, mGreen, mBlue, mAlpha );
//        }
//
//
//        public void setOn( ColorMask a ) {
//            mRed = a.mRed;
//            mGreen = a.mGreen;
//            mBlue = a.mBlue;
//            mAlpha = a.mAlpha;
//        }
//    }
//
//
//    public static class DepthMask implements Attrib2<DepthMask> {
//
//        public boolean mOn = false;
//
//        @Override
//        public Attrib2 alloc() {
//            return new DepthMask();
//        }
//
//        @Override
//        public void apply( GL2ES2 gl ) {
//            gl.glDepthMask( mOn );
//        }
//
//        public void setOn( DepthMask a ) {
//            mOn = a.mOn;
//        }
//
//    }
//
//
//    public static class DepthTest implements Attrib2<DepthTest> {
//        public boolean mOn   = false;
//        public int     mFunc = GL_LESS;
//
//        @Override
//        public Attrib2 alloc() {
//            return new DepthTest();
//        }
//
//        @Override
//        public void apply( GL2ES2 gl ) {
//            if( mOn ) {
//                gl.glEnable( GL_DEPTH_TEST );
//            } else {
//                gl.glDisable( GL_DEPTH_TEST );
//            }
//            gl.glDepthFunc( mFunc );
//        }
//
//
//        public void setOn( DepthTest a ) {
//            mOn   = a.mOn;
//            mFunc = a.mFunc;
//        }
//
//    }
//
//
//    public static class ScissorTest implements Attrib2<ScissorTest> {
//        public boolean mOn      = false;
//        public int[]   mScissor = { 0, 0, 1, 1 };
//
//        public void setOn( boolean on, int x, int y, int w, int h ) {
//            mOn = on;
//            mScissor[0] = x;
//            mScissor[1] = y;
//            mScissor[2] = w;
//            mScissor[3] = h;
//        }
//
//        @Override
//        public ScissorTest alloc() {
//            return new ScissorTest();
//        }
//
//        @Override
//        public void apply( GL2ES2 gl ) {
//            if( mOn ) {
//                gl.glEnable( GL_SCISSOR_TEST );
//            } else {
//                gl.glDisable( GL_SCISSOR_TEST );
//            }
//            gl.glScissor( mScissor[0], mScissor[1], mScissor[2], mScissor[3] );
//        }
//
//        @Override
//        public void setOn( ScissorTest a ) {
//            mOn = a.mOn;
//            System.arraycopy( a.mScissor, 0, mScissor, 0, 4 );
//        }
//    }
//
//
//    public static class StencilFunc implements Attrib2<StencilFunc> {
//        public boolean mOn        = false;
//        public int     mFrontFunc = GL_ALWAYS;
//        public int     mFrontRef  = 0;
//        public int     mFrontMask = 0xFFFFFFFF;
//        public int     mBackFunc  = GL_ALWAYS;
//        public int     mBackRef   = 0;
//        public int     mBackMask  = 0xFFFFFFFF;
//
//        @Override
//        public Attrib2 alloc() {
//            return new StencilFunc();
//        }
//
//        @Override
//        public void apply( GL2ES2 gl ) {
//            if( mOn ) {
//                gl.glEnable( GL_STENCIL_TEST );
//            } else {
//                gl.glDisable( GL_STENCIL_TEST );
//            }
//            gl.glStencilFuncSeparate( GL_FRONT, mFrontFunc, mFrontRef, mFrontMask );
//            gl.glStencilFuncSeparate( GL_BACK, mBackFunc, mBackRef, mBackMask );
//        }
//
//        @Override
//        public void setOn( StencilFunc a ) {
//            mOn = a.mOn;
//            mFrontFunc = a.mFrontFunc;
//            mFrontRef = a.mFrontRef;
//            mFrontMask = a.mFrontMask;
//            mBackFunc = a.mBackFunc;
//            mBackRef = a.mBackRef;
//            mBackMask = a.mBackMask;
//        }
//
//
//        public void setOn( boolean on, int func, int ref, int mask ) {
//            mOn = on;
//            mFrontFunc = mBackFunc = func;
//            mFrontRef = mBackRef = ref;
//            mFrontMask = mBackMask = mask;
//        }
//
//
//        public void setOn( boolean on ) {
//            mOn = on;
//        }
//
//
//        public void setFront( int frontFunc, int frontRef, int frontMask ) {
//            mFrontFunc = frontFunc;
//            mFrontRef = frontRef;
//            mFrontMask = frontMask;
//        }
//
//
//        public void setBack( int backFunc, int backRef, int backMask ) {
//            mBackFunc = backFunc;
//            mBackRef = backRef;
//            mBackMask = backMask;
//        }
//
//    }
//
//
//    public static class StencilOp implements Attrib2<StencilOp> {
//        static Allocator<StencilOp> ALLOC = new Allocator<StencilOp>() {
//            public StencilOp alloc() { return new StencilOp(); }
//        };
//
//        public int mFrontStencilFail = GL_KEEP;
//        public int mFrontDepthFail   = GL_KEEP;
//        public int mFrontPass        = GL_KEEP;
//        public int mBackStencilFail  = GL_KEEP;
//        public int mBackDepthFail    = GL_KEEP;
//        public int mBackPass         = GL_KEEP;
//
//        @Override
//        public StencilOp alloc() {
//            return new StencilOp();
//        }
//
//        public void apply( GL2ES2 gl ) {
//            gl.glStencilOpSeparate( GL_FRONT, mFrontStencilFail, mFrontDepthFail, mFrontPass );
//            gl.glStencilOpSeparate( GL_BACK, mBackStencilFail, mBackStencilFail, mBackPass );
//        }
//
//        public void setOn( StencilOp a ) {
//            mFrontStencilFail = a.mFrontStencilFail;
//            mFrontDepthFail = a.mFrontDepthFail;
//            mFrontPass = a.mFrontPass;
//            mBackStencilFail = a.mBackStencilFail;
//            mBackDepthFail = a.mBackDepthFail;
//            mBackPass = a.mBackPass;
//        }
//
//        public void setOn( int stencilFail, int depthFail, int pass ) {
//            mFrontStencilFail = mBackStencilFail = stencilFail;
//            mFrontDepthFail = mBackDepthFail = depthFail;
//            mFrontPass = mBackPass = pass;
//        }
//
//    }
//
//
//    public static class Viewport implements Attrib2<Viewport> {
//        static Allocator<Viewport> ALLOC = new Allocator<Viewport>() {
//            public Viewport alloc() { return new Viewport(); }
//        };
//
//        public int mX = 0;
//        public int mY = 0;
//        public int mW = 1;
//        public int mH = 1;
//
//        @Override
//        public Attrib2 alloc() {
//            return new Viewport();
//        }
//
//        @Override
//        public void apply( GL2ES2 gl ) {
//            gl.glViewport( mX, mY, mW, mH );
//        }
//
//        @Override
//        public void setOn( Viewport a ) {
//            mX = a.mX;
//            mY = a.mY;
//            mW = a.mW;
//            mH = a.mH;
//        }
//
//
//        public void setOn( int x, int y, int w, int h ) {
//            mX = x;
//            mY = y;
//            mW = w;
//            mH = h;
//        }
//
//    }
//
//
//    public static interface Allocator<T> {
//        public T alloc();
//    }
//
//
//    public static class Stack<T extends Attrib2> {
//        T[] mArr;
//        int mPos;
//
//        @SuppressWarnings( "unchecked" )
//        Stack( T attrib, int initialCapacity ) {
//            mArr = (T[])java.lang.reflect.Array.newInstance( attrib.getClass(), initialCapacity );
//            mArr[0] = attrib;
//            for( int i = 1; i < mArr.length; i++ ) {
//                mArr[i] = (T)attrib.alloc();
//            }
//        }
//
//
//        public T current() {
//            return mArr[mPos];
//        }
//
//        @SuppressWarnings( "unchecked" )
//        public T push() {
//            T prev = mArr[ mPos++ ];
//            ensureCapacity( mPos );
//            T ret = mArr[ mPos ];
//            ret.setOn( prev );
//            return ret;
//        }
//
//
//        public T pop() {
//            return mArr[--mPos];
//        }
//
//        @SuppressWarnings( "unchecked" )
//        public void ensureCapacity( int minCap ) {
//            if( minCap <= mArr.length ) {
//                return;
//            }
//
//            final int oldCap = mArr.length;
//            int newCap = ( oldCap * 3 ) / 2 + 1;
//            if( newCap < minCap ) {
//                newCap = minCap;
//            }
//            mArr = Arrays.copyOf( mArr, newCap );
//            T boot = mArr[0];
//            for( int i = oldCap; i < newCap; i++ ) {
//                mArr[i] = (T)boot.alloc();
//            }
//        }
//
//    }
//
//}
