package bits.draw3d.tex;

import bits.draw3d.DrawEnv;

import javax.media.opengl.*;
import java.util.*;
import static javax.media.opengl.GL2GL3.*;


/**
 * @author decamp
 */
abstract class AbstractTexture implements Texture {

    private final int mTarget;
    private final int mBinding;
    private final int[] mId = { 0 };

    private int mIntFormat = GL_RGBA;
    private int mFormat    = GL_RGBA;
    private int mDataType  = GL_UNSIGNED_BYTE;

    private int     mWidth           = -1;
    private int     mHeight          = -1;
    private int     mDepth           = 1;
    private boolean mResizeOnReshape = false;

    private final Map<Integer, Integer> mParams = new HashMap<Integer, Integer>( 4 );

    private boolean mNeedInit  = true;
    private boolean mNeedAlloc = true;


    protected AbstractTexture( int target, int binding ) {
        mTarget = target;
        mBinding = binding;
    }


    public int target() {
        return mTarget;
    }

    public int id() {
        return mId[0];
    }

    public void format( int intFormat, int format, int dataType ) {
        if( intFormat == mIntFormat &&
            format == mFormat &&
            dataType == mDataType )
        {
            return;
        }

        mIntFormat = intFormat;
        mFormat = format;
        mDataType = dataType;
        fireAlloc();
    }

    public int internalFormat() {
        return mIntFormat;
    }

    public int format() {
        return mFormat;
    }

    public int dataType() {
        return mDataType;
    }

    public void size( int w, int h ) {
        if( mTarget == GL_TEXTURE_1D ) {
            if( w < 0 ) {
                w = -1;
                h = -1;
            } else {
                h = 1;
            }
        } else {
            if( w < 0 || h < 0 ) {
                w = -1;
                h = -1;
            }
        }

        if( w == mWidth && h == mHeight ) {
            return;
        }

        mWidth = w;
        mHeight = h;
        fireAlloc();
    }

    public int width() {
        return mWidth;
    }

    public int height() {
        return mHeight;
    }

    public boolean hasSize() {
        return mWidth >= 0 && mDepth >= 0;
    }

    public void resizeOnReshape( boolean enable ) {
        mResizeOnReshape = enable;
    }

    public boolean resizeOnReshape() {
        return mResizeOnReshape;
    }

    public void depth( int depth ) {
        if( mTarget != GL_TEXTURE_3D ) {
            return;
        }

        if( depth < 0 ) {
            depth = -1;
        }

        if( depth == mDepth ) {
            return;
        }

        mDepth = depth;
        mNeedInit = true;
        mNeedAlloc = true;
    }

    public int depth() {
        return mDepth;
    }

    public Integer param( int key ) {
        return mParams.get( key );
    }

    public void param( int key, int value ) {
        Integer prev = mParams.put( key, value );
        if( prev == null || prev != value ) {
            fireInit();
        }
    }

    @Override
    public void init( DrawEnv g ) {
        if( mNeedInit ) {
            doInit( g );
            unbind( g );
        }
    }

    @Override
    public void dispose( DrawEnv g ) {
        if( mId[0] != 0 ) {
            g.mGl.glDeleteTextures( 1, mId, 0 );
            mId[0] = 0;
        }
        mNeedInit  = true;
        mNeedAlloc = true;
    }

    @Override
    public void bind( DrawEnv g ) {
        if( mNeedInit ) {
            doInit( g );
        }
        g.mGl.glBindTexture( mTarget, mId[0] );
    }

    @Override
    public void bind( DrawEnv g, int unit ) {
        g.mGl.glActiveTexture( GL_TEXTURE0 + unit );
        bind( g );
    }

    @Override
    public void unbind( DrawEnv g ) {
        g.mGl.glBindTexture( mTarget, 0 );
    }

    @Override
    public void unbind( DrawEnv g, int unit ) {
        g.mGl.glActiveTexture( GL_TEXTURE0 + unit );
        unbind( g );
    }

    @Override
    public void reshape( DrawEnv g, int x, int y, int w, int h ) {
        if( resizeOnReshape() ) {
            size( w, h );
        }
    }


    protected void fireInit() {
        mNeedInit = true;
    }

    protected void fireAlloc() {
        mNeedInit = true;
        mNeedAlloc = true;
    }

    protected abstract void doAlloc( DrawEnv g  );


    private void doInit( DrawEnv g ) {
        if( !mNeedInit ) {
            return;
        }

        mNeedInit = false;
        GL2ES2 gl = g.mGl;

        if( mId[0] == 0 ) {
            gl.glGenTextures( 1, mId, 0 );
            if( mId[0] == 0 ) {
                throw new RuntimeException( "Failed to allocate texture." );
            }
        }

        gl.glBindTexture( mTarget, mId[0] );
        if( !mParams.isEmpty() ) {
            for( Map.Entry<Integer, Integer> e : mParams.entrySet() ) {
                gl.glTexParameteri( mTarget, e.getKey(), e.getValue() );
            }
        }

        if( mNeedAlloc ) {
            mNeedAlloc = false;
            if( hasSize() ) {
                doAlloc( g );
                g.checkErr();
            }
        }
    }

}
