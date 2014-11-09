package bits.glui.shader;

import bits.glui.util.GException;

import java.util.*;

import javax.media.opengl.*;
import static javax.media.opengl.GL4.*;


/**
 * @author Philip DeCamp
 */
public class Program {

    private final List<Shader> mShaders = new ArrayList<Shader>( 3 );
    private final Map<String, ProgramResource> mAttribs  = new HashMap<String, ProgramResource>();
    private final Map<String, ProgramResource> mUniforms = new HashMap<String, ProgramResource>();

    private int mId = 0;


    public Program() {}



    public int id() {
        return mId;
    }


    public ProgramResource attrib( String name ) {
        return mAttribs.get( name );
    }


    public ProgramResource uniform( String name ) {
        return mUniforms.get( name );
    }


    public void addShader( Shader shader ) {
        mShaders.add( shader );
    }


    public void init( GL2ES2 gl ) {
        mId = gl.glCreateProgram();
        for( Shader s: mShaders ) {
            s.init( gl );
            gl.glAttachShader( mId, s.id() );
        }
        gl.glLinkProgram( mId );
        GException.check( gl );

        for( ProgramResource p: ShaderUtil.listAttributes( gl, mId ) ) {
            mAttribs.put( p.mName, p );
        }
        for( ProgramResource p: ShaderUtil.listUniforms( gl, mId ) ) {
            mUniforms.put( p.mName, p );
        }
        GException.check( gl );
    }


    public void bind( GL2ES2 gl ) {
        gl.glUseProgram( mId );
    }


    public void unbind( GL2ES2 gl ) {
        gl.glUseProgram( 0 );
    }


}
