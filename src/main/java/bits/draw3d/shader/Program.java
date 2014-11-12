package bits.draw3d.shader;

import bits.draw3d.*;
import bits.draw3d.DrawEnv;

import java.util.*;
import javax.media.opengl.*;


/**
 * @author Philip DeCamp
 */
public class Program implements DrawUnit {

    private final List<Shader> mShaders = new ArrayList<Shader>( 3 );
    private final Map<String, ProgramResource> mAttribs  = new HashMap<String, ProgramResource>();
    private final Map<String, ProgramResource> mUniforms = new HashMap<String, ProgramResource>();

    private int mId = 0;
    private List<DrawTask> mOnBind = null;


    public Program() {}



    public int id() {
        return mId;
    }


    public ProgramResource attrib( String name ) {
        return mAttribs.get( name );
    }


    public Map<String,ProgramResource> attribs() {
        return mAttribs;
    }


    public ProgramResource uniform( String name ) {
        return mUniforms.get( name );
    }


    public Map<String,ProgramResource> uniforms() {
        return mUniforms;
    }


    public void addShader( Shader shader ) {
        mShaders.add( shader );
    }


    public void addBindTask( DrawTask task ) {
        if( mOnBind == null ) {
            mOnBind = new ArrayList<DrawTask>( 6 );
        }
        mOnBind.add( task );
    }


    public void init( DrawEnv g ) {
        GL2ES2 gl = g.mGl;
        mId = gl.glCreateProgram();
        for( Shader s: mShaders ) {
            s.init( gl );
            gl.glAttachShader( mId, s.id() );
        }
        gl.glLinkProgram( mId );
        DrawUtil.checkErr( gl );

        for( ProgramResource p: ShaderUtil.listAttributes( gl, mId ) ) {
            mAttribs.put( p.mName, p );
        }
        for( ProgramResource p: ShaderUtil.listUniforms( gl, mId ) ) {
            mUniforms.put( p.mName, p );
        }
        DrawUtil.checkErr( gl );
    }


    public void bind( DrawEnv g ) {
        g.mGl.glUseProgram( mId );
        List<DrawTask> list = mOnBind;
        if( list == null ) {
            return;
        }
        final int len = list.size();
        for( int i = 0; i < len; i++ ) {
            list.get( i ).run( g );
        }
    }


    public void unbind( DrawEnv g ) {
        g.mGl.glUseProgram( 0 );
    }


    public void dispose( DrawEnv g ) {
        if( mId != 0 ) {
            g.mGl.glDeleteProgram( mId );
            mId = 0;
        }
    }

}
