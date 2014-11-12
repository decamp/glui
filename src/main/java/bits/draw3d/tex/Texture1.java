package bits.draw3d.tex;

import bits.draw3d.DrawEnv;

import java.nio.ByteBuffer;

import static javax.media.opengl.GL2GL3.*;


/**
 * @author decamp
 */
public final class Texture1 extends AbstractTexture {


    private ByteBuffer mBuf = null;


    public Texture1() {
        super( GL_TEXTURE_1D, GL_TEXTURE_BINDING_1D );
        param( GL_TEXTURE_MIN_FILTER, GL_LINEAR );
    }


    public synchronized void buffer( ByteBuffer buf, 
                                     int intFormat, 
                                     int format, 
                                     int dataType, 
                                     int width )
    {
        if( buf == null ) {
            if( mBuf == null ) {
                return;
            }
            
            super.format( -1, -1, -1 );
            super.size( -1, -1 );
            mBuf = null;
        } else {
            super.format( intFormat, format, dataType );
            super.size( width, 1 );
            mBuf = buf;
        }
        
        fireAlloc();
    }

    @Override
    public void dispose( DrawEnv g  ) {
        super.dispose( g );
        mBuf = null;
    }

    @Override
    protected void doAlloc( DrawEnv g ) {
        g.mGl.glTexImage1D( GL_TEXTURE_1D,
                            0, //level
                            internalFormat(),
                            width(),
                            0, //border
                            format(),
                            dataType(),
                            mBuf );
    }

}
