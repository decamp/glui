package bits.glui.util;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import static javax.media.opengl.GL2ES2.*;

/**
 * @author Philip DeCamp
 */
public class GException extends GLException {

    /**
     * @throws GException if {@code gl.glGetError() != GL_NO_ERROR}
     */
    public static void check( GL gl ) throws GException {
        int err = gl.glGetError();
        if( err == 0 ) {
            return;
        }
        throw new GException( err );
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



    private final int mErrorCode;


    public GException( String msg ) {
        this( -1, msg, null );
    }


    public GException( int code ) {
        super( String.format( "Err 0x%08X: %s", code, errString( code ) ) );
        mErrorCode = code;
    }


    public GException( int code, String msg ) {
        this( code, msg, null );
    }


    public GException( int code, String message, Throwable t ) {
        super( message, t );
        mErrorCode = code;
    }



    public int errorCode() {
        return mErrorCode;
    }


}
