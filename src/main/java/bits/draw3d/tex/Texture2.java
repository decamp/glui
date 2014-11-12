package bits.draw3d.tex;

import bits.draw3d.DrawEnv;
import bits.draw3d.DrawUtil;


import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static javax.media.opengl.GL.*;


/**
 * @author decamp
  */
public final class Texture2 extends AbstractTexture {


    private ByteBuffer mBuf = null;


    public Texture2() {
        super( GL_TEXTURE_2D, GL_TEXTURE_BINDING_2D );
        param( GL_TEXTURE_MIN_FILTER, GL_LINEAR );
    }


    public void buffer( BufferedImage image ) {
        if( image == null ) {
            buffer( null, 0, 0, 0, -1, -1 );
        } else {
            int[] format = new int[4];
            ByteBuffer buf = DrawUtil.imageToByteBuffer( image, null, format );
            buffer( buf, format[0], format[1], format[2], image.getWidth(), image.getHeight() );
        }
    }


    public synchronized void buffer( ByteBuffer buf,
                                     int intFormat,
                                     int format,
                                     int dataType,
                                     int w,
                                     int h )
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
            super.size( w, h );
            mBuf = buf.duplicate();
        }

        fireAlloc();
    }

    @Override
    public void dispose( DrawEnv g ) {
        super.dispose( g );
        mBuf = null;
    }

    @Override
    protected synchronized void doAlloc( DrawEnv g ) {
        g.mGl.glTexImage2D( GL_TEXTURE_2D,
                            0, //level
                            internalFormat(),
                            width(),
                            height(),
                            0, // border
                            format(),
                            dataType(),
                            mBuf );
        mBuf = null;
    }

}
