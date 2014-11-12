package bits.draw3d;

import bits.util.gui.Images;

import java.nio.*;
import javax.media.opengl.*;
import java.awt.image.BufferedImage;
import static javax.media.opengl.GL3.*;

/**
 * @author Philip DeCamp
 */
public class DrawUtil {

    /**
     * @throws GLException if {@code gl.glGetError() != GL_NO_ERROR}
     */
    public static void checkErr( GL gl ) throws GLException {
        int err = gl.glGetError();
        if( err == 0 ) {
            return;
        }
        String msg = String.format( "Err 0x%08X: %s", err, errString( err ) );
        throw new GLException( msg );
    }


    public static String errString( int err ) {
        switch( err ) {
        case GL_NO_ERROR:
            return "No error";
        case GL_INVALID_ENUM:
            return "Invalid enum";
        case GL_INVALID_VALUE:
            return "Invalid value";
        case GL_INVALID_OPERATION:
            return "Invalid operation";
        case GL_STACK_OVERFLOW:
            return "Stack overflow";
        case GL_STACK_UNDERFLOW:
            return "Stack underflow";
        case GL_OUT_OF_MEMORY:
            return "Out of memory";
        default:
            return "Unknown error";
        }
    }


    public static ByteBuffer alloc( int size ) {
        return ByteBuffer.allocateDirect( size ).order( ByteOrder.nativeOrder() );
    }


    public static FloatBuffer allocFloats( int size ) {
        return alloc( size * 4 ).asFloatBuffer();
    }


    public static ByteBuffer ensureCap( ByteBuffer buf, int size ) {
        if( buf == null || buf.capacity() < size ) {
            return alloc( size );
        }
        buf.clear();
        return buf;
    }


    public static FloatBuffer ensureCap( FloatBuffer buf, int size ) {
        int cap = buf.capacity();
        if( buf == null || buf.capacity() < size ) {
            return allocFloats( size );
        }
        buf.clear();
        return buf;
    }

    /**
     * Returns equivalent OpenGL internalFormat, format and data types for a BufferedImage.
     * (e.g., GL_BGRA and GL_UNSIGNED_BYTE). It will also specify if the ordering of
     * the DataBuffer component values must be reversed to achieve a GL-compatible format.
     *
     * @param image Some image
     * @param out4  Length-4 array to hold output. On return: <br>
     *              out3[0] will hold INPUT FORMAT for image. <br>
     *              out3[1] will hold FORMAT for image. <br>
     *              out3[2] will hold DATA TYPE for image.
     *              out3[3] will equal 1 if component values must be swapped (reverse-ordered), otherwise 0.
     * @return true if equivalent format and data type were found
     */
    public static boolean getTextureFormat( BufferedImage image, int[] out4 ) {
        switch( image.getType() ) {
        case BufferedImage.TYPE_USHORT_GRAY:
            out4[0] = GL_RED;
            out4[1] = GL_RED;
            out4[2] = GL_UNSIGNED_SHORT;
            out4[3] = 0;
            return true;

        case BufferedImage.TYPE_BYTE_GRAY:
            out4[0] = GL_RED;
            out4[1] = GL_RED;
            out4[2] = GL_UNSIGNED_BYTE;
            out4[3] = 0;
            return true;

        case BufferedImage.TYPE_INT_BGR:
            out4[0] = GL_RGBA;
            out4[1] = GL_BGRA;
            out4[2] = GL_UNSIGNED_BYTE;
            out4[3] = 0;
            return true;

        case BufferedImage.TYPE_3BYTE_BGR:
            out4[0] = GL_RGB;
            out4[1] = GL_BGR;
            out4[2] = GL_UNSIGNED_BYTE;
            out4[3] = 0;
            return true;

        case BufferedImage.TYPE_INT_RGB:
            out4[0] = GL_RGBA;
            out4[1] = GL_RGBA;
            out4[2] = GL_UNSIGNED_BYTE;
            out4[3] = 0;
            return true;

        case BufferedImage.TYPE_USHORT_555_RGB:
            out4[0] = GL_RGB;
            out4[1] = GL_RGB;
            out4[2] = GL_UNSIGNED_SHORT_5_5_5_1;
            out4[3] = 0;
            return true;

        case BufferedImage.TYPE_USHORT_565_RGB:
            out4[0] = GL_RGB;
            out4[1] = GL_RGB;
            out4[2] = GL_UNSIGNED_SHORT_5_5_5_1;
            out4[3] = 0;
            return true;

        case BufferedImage.TYPE_4BYTE_ABGR:
        case BufferedImage.TYPE_4BYTE_ABGR_PRE:
            return false;

        case BufferedImage.TYPE_INT_ARGB:
        case BufferedImage.TYPE_INT_ARGB_PRE:
            out4[0] = GL_RGBA;
            out4[1] = GL_BGRA;
            out4[2] = GL_UNSIGNED_BYTE;
            out4[3] = 1;
            return true;

        case BufferedImage.TYPE_BYTE_BINARY:
        case BufferedImage.TYPE_BYTE_INDEXED:
        case BufferedImage.TYPE_CUSTOM:
        default:
            return false;
        }
    }

    /**
     * Converts a BufferedImage to a 32-bit BGRA format and places it into
     * a directly allocated java.nio.ByteBuffer.
     *
     * @param image    Input image to convert.
     * @param optWork  [Optional] array that may be used if {@code workSpace.length >= image.getWidth() }.
     * @return Directly allocated ByteBuffer containing pixels in BGRA format and sRGB color space.
     */
    public static ByteBuffer imageToBgraBuffer( BufferedImage image, int[] optWork ) {
        int w = image.getWidth();
        int h = image.getHeight();
        int[] row = optWork != null && optWork.length >= w ? optWork : new int[w];

        ByteBuffer ret = ByteBuffer.allocateDirect( ( w * h ) * 4 );
        ret.order( ByteOrder.LITTLE_ENDIAN );
        IntBuffer ib = ret.asIntBuffer();
        for( int i = 0; i < h; i++ ) {
            image.getRGB( 0, i, w, 1, row, 0, w );
            ib.put( row, 0, w );
        }
        ret.order( ByteOrder.nativeOrder() );

        return ret;
    }

    /**
     * Converts a BufferedImage to a directly allocated java.nio.ByteBuffer.
     * This method will first check to see if the image can be ported directly to a
     * GL compatible format. If so, it will dump the data directly without conversion
     * via {@link bits.util.gui.Images#dataToByteBuffer}. If not, it will convert the image via
     * {@link #imageToBgraBuffer}.
     *
     * @param optOutFormat  Holds GL enum values for buffe representation. See {@link #getTextureFormat}.
     * @return Directly allocated byte buffer holding image.
     */
    public static ByteBuffer imageToByteBuffer( BufferedImage image, int[] optOutFormat ) {
        return imageToByteBuffer( image, null, optOutFormat );
    }

    /**
     * Converts a BufferedImage to a directly allocated java.nio.ByteBuffer.
     * This method will first check to see if the image can be ported directly to a
     * GL compatible format. If so, it will dump the data directly without conversion
     * via {@link #dataToByteBuffer}. If not, it will convert the image via
     * {@link #imageToBgraBuffer}.
     *
     * @param optOutFormat  Holds GL enum values for buffe representation. See {@link #getTextureFormat}.
     * @param optWorkspace  Optional array that may be used if <code>workSpace.length &gt;= image.getWidth()</code>.
     * @return Directly allocated byte buffer holding image.
     */
    public static ByteBuffer imageToByteBuffer( BufferedImage image, int[] optWorkspace, int[] optOutFormat ) {
        if( optOutFormat == null || optOutFormat.length < 4 ) {
            optOutFormat = new int[4];
        }

        if( !getTextureFormat( image, optOutFormat ) ) {
            optOutFormat[0] = GL_RGBA;
            optOutFormat[1] = GL_BGRA;
            optOutFormat[2] = GL_UNSIGNED_BYTE;
            optOutFormat[3] = 0;
            return imageToBgraBuffer( image, optWorkspace );
        }

        ByteOrder order;
        if( optOutFormat[2] == GL_UNSIGNED_BYTE || optOutFormat[2] == GL_BYTE ) {
            order = optOutFormat[3] == 0 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        } else {
            if( optOutFormat[3] == 0 ) {
                order = ByteOrder.nativeOrder();
            } else {
                optOutFormat[0] = GL_RGBA;
                optOutFormat[1] = GL_BGRA;
                optOutFormat[2] = GL_UNSIGNED_BYTE;
                optOutFormat[3] = 0;
                return imageToBgraBuffer( image, optWorkspace );
            }
        }

        ByteBuffer ret = Images.dataToByteBuffer( image.getData().getDataBuffer(), order );
        ret.order( ByteOrder.nativeOrder() );
        return ret;
    }


}
