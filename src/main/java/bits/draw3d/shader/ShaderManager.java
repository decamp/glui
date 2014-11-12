package bits.draw3d.shader;

import bits.util.Resources;
import bits.util.Streams;

import java.io.File;
import java.io.IOException;


/**
 * @author Philip DeCamp
 */
public class ShaderManager {


    public Shader loadSource( int type, String source ) {
        return new Shader( type, source );
    }


    public Shader loadResource( int type, String resourcePath ) {
        String source = null;
        try {
            source = Resources.readString( resourcePath );
        } catch( IOException e ) {
            throw new LinkageError( "Could not run shader: " + resourcePath );
        }

        if( source == null ) {
            throw new LinkageError( "Could not find shader: " + resourcePath );
        }
        return loadSource( type, source );
    }


    public Shader loadFile( int type, File file ) {
        try {
            String s = Streams.readString( file );
            return loadSource( type, s );
        } catch( IOException e ) {
            throw new LinkageError( "Could not run shader: " + file.getPath() );
        }
    }

}
