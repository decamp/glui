package bits.draw3d.shader;

import bits.draw3d.DrawUtil;
import bits.util.ref.Refable;

import javax.media.opengl.GL2ES2;


/**
 * @author Philip DeCamp
 */
public class Shader implements Refable {

    private final int mShaderType;

    private String mSource;

    private int mId = 0;
    private int mRefCount = 1;


    public Shader( int shaderType, String source ) {
        mShaderType = shaderType;
        mSource = source;
    }


    public int id() {
        return mId;
    }


    public int shaderType() {
        return mShaderType;
    }


    public void init( GL2ES2 gl ) {
        if( mId == 0 ) {
            mId = ShaderUtil.compile( gl, mShaderType, mSource );
            DrawUtil.checkErr( gl );
        }
    }


    public void dispose( GL2ES2 gl ) {
        if( mId != 0 ) {
            return;
        }
        gl.glDeleteShader( mId );
        mId = 0;
    }

    @Override
    public boolean ref() {
        if( mRefCount++ > 0 ) {
            return true;
        }
        mRefCount = 0;
        return false;
    }

    @Override
    public void deref() {
        if( --mRefCount < 0 ) {
            mRefCount = 0;
        }
    }

    @Override
    public int refCount() {
        return mRefCount;
    }

}
