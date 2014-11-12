package bits.draw3d.tex;

import bits.glui.GGraphics;

import java.nio.ByteBuffer;
import static javax.media.opengl.GL2GL3.*;


/**
 * @author decamp
 */
public final class Texture3 extends AbstractTexture {


    private ByteBuffer mBuf = null;


    public Texture3() {
        super( GL_TEXTURE_3D, GL_TEXTURE_BINDING_3D );
    }


    public synchronized void buffer( ByteBuffer buf,
                                     int intFormat,
                                     int format,
                                     int dataType,
                                     int w,
                                     int h,
                                     int depth )
    {
        if( buf == null ) {
            if( mBuf == null ) {
                return;
            }
            super.format( -1, -1, -1 );
            super.size( -1, -1 );
            super.depth( -1 );
            mBuf = null;
        } else {
            super.format( intFormat, format, dataType );
            super.size( w, h );
            super.depth( depth );
            mBuf = buf.duplicate();
        }

        fireAlloc();
    }

    @Override
    protected void doAlloc( GGraphics g ) {
        g.mGl.glTexImage3D( GL_TEXTURE_3D,
                            0, // Level
                            internalFormat(),
                            width(),
                            height(),
                            depth(),
                            0,
                            format(),
                            dataType(),
                            mBuf );
        mBuf = null;
    }

}
