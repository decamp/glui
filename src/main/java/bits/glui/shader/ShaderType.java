package bits.glui.shader;

import static javax.media.opengl.GL3.*;


/**
 * @author decamp
 */
public enum ShaderType {

    VERTEX     ( GL_VERTEX_SHADER  ),
    GEOMETRY   ( GL_GEOMETRY_SHADER ),
    FRAGMENT   ( GL_FRAGMENT_SHADER );


    private final int mGlCode;

    ShaderType( int glslCode ) {
        mGlCode = glslCode;
    }


    public int glCode() {
        return mGlCode;
    }

}
