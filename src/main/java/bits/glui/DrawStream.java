package bits.glui;

import bits.glui.util.GException;
import bits.math3d.Vec4;

import javax.media.opengl.*;
import java.nio.ByteBuffer;

import static javax.media.opengl.GL2ES2.*;


/**
 * @author Philip DeCamp
 */
public class DrawStream {

    private static final int   DEFAULT_BUF_SIZE = 1024 * 8;
    private static final float SCALE_FLOAT       = 255f / 1f;

    private boolean mColorOn = false;
    private boolean mTexOn   = false;
    private boolean mNormOn  = false;

    private int mColor = 0xFFFFFFFF;
    private final Vec4 mTex   = new Vec4();
    private final Vec4 mNorm  = new Vec4();

    private int[] mVbo = { 0 };
    private int[] mVao = { 0 };
    private int mByteCap;
    private int mVertRem;
    private int mVertCap;

    private GGraphics  mG;
    private ByteBuffer mBuf;


    public DrawStream() {
        this( DEFAULT_BUF_SIZE );
    }


    public DrawStream( int bufSize ) {
        mByteCap = bufSize;
    }



    void init( GGraphics g ) {
        GL3 gl = g.mGl;
        gl.glGenBuffers( 1, mVbo, 0 );
        gl.glBindBuffer( GL_ARRAY_BUFFER, mVbo[0] );
        gl.glBufferData( GL_ARRAY_BUFFER, mByteCap, null, GL3.GL_STREAM_DRAW );
        gl.glBindBuffer( GL_ARRAY_BUFFER, 0 );
        GException.check( gl );

        gl.glGenVertexArrays( 1, mVao, 0 );
        gl.glBindVertexArray( mVao[0] );
        gl.glBindBuffer( GL_ARRAY_BUFFER, mVbo[0] );


        //Bind the VBO and setup pointers for the VAO
        glBindBuffer( GL_ARRAY_BUFFER, VBOID[0] );
        glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, sizeof(TVertex_VC), BUFFER_OFFSET(0));
        glVertexAttribPointer(3, 4, GL_UNSIGNED_BYTE, GL_TRUE, sizeof(TVertex_VC), BUFFER_OFFSET(sizeof(float)*3));
        glEnableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        //Bind the IBO for the VAO
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, IBOID[0]);

        //Bind the VBO and setup pointers for the VAO
        glBindBuffer(GL_ARRAY_BUFFER, VBOID[0]);
        glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, sizeof(TVertex_VC), BUFFER_OFFSET(0));
        glVertexAttribPointer(3, 4, GL_UNSIGNED_BYTE, GL_TRUE, sizeof(TVertex_VC), BUFFER_OFFSET(sizeof(float)*3));
        glEnableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        //Bind the IBO for the VAO
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, IBOID[0]);


    }



    public void attributes( boolean color, boolean tex, boolean norm ) {
        mColorOn = color;
        mTexOn   = tex;
        mNormOn  = norm;
    }


    public void beginPoints() {


    }


    public void beginLines() {

    }


    public void beginLineStrip() {

    }


    public void beginTriangleStrip() {

    }


    public void beginTriangles() {


    }


    public void beginQuads() {

    }


    public void beginQuadStrip() {


    }


    public void end() {


    }




    public void color3ub( int red, int green, int blue ) {
        color4ub( red, green, blue, 0xFF );
    }


    public void color4ub( int red, int green, int blue, int alpha ) {
        mColor = ( red   << 24 & 0xFF000000 ) |
                 ( green << 16 & 0x00FF0000 ) |
                 ( blue  <<  8 & 0x0000FF00 ) |
                 ( alpha       & 0x000000FF );
    }


    public void color3f( float red, float green, float blue ) {
        color4f( red, green, blue, 1f );
    }


    public void color4f( float red, float green, float blue, float alpha ) {
        color4ub( (int)( SCALE_FLOAT * red ),
                  (int)( SCALE_FLOAT * green ),
                  (int)( SCALE_FLOAT * blue ),
                  (int)( SCALE_FLOAT * alpha ) );
    }


    public void color3fv( float[] col ) {
        color4f( col[0], col[1], col[2], 1f );
    }


    public void color4fv( float[] col ) {
        color4f( col[0], col[1], col[2], 1f );
    }


    public void vertex2i( int x, int y ) {
        vertex4f( x, y, 0, 1 );
    }


    public void vertex2f( float x, float y ) {
        vertex4f( x, y, 0, 1 );
    }


    public void vertex3i( int x, int y, int z ) {
        vertex4f( x, y, z, 1 );
    }


    public void vertex3f( float x, float y, float z ) {
        vertex4f( x, y, z, 1 );
    }


    public void vertex4i( int x, int y, int z, int w ) {
        vertex4f( x, y, z, w );
    }


    public void vertex4f( float x, float y, float z, float w ) {



        mBuf.putInt( mColor );


    }



    private static interface Loader {
        public int stride();
        public void load();
    }


    private class Loader {
        private final int mColorNum;
        private final int mTexNum;
        private final int mNormNum;

        Loader( int colorNum, int texNum, int normNum ) {
            mColorNum = colorNum;
            mTexNum   = texNum;
            mNormNum  = normNum;
        }


        public void init( GL3 g ) {
            //g.glPolygonMode(
            //g.client

        }


    }


    private final Loader ON_ON_ON = new Loader() {

        public void run() {


        }
    }


}
