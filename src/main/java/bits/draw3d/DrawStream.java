package bits.draw3d;

import bits.draw3d.shader.*;
import bits.math3d.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.*;
import static javax.media.opengl.GL3.*;


/**
 * Emulates the old OpenGL immediate mode rendering. Meant largely to help transition old code to new GL versions.
 *
 * @author Philip DeCamp
 */
public class DrawStream {

    private static final String SHADER_PATH = "glsl/bits/draw3d/shader";
    private static final int    BIT_COLOR   = 1 << 0;
    private static final int    BIT_NORM    = 1 << 1;
    private static final int    BIT_TEX     = 1 << 2;
    private static final int    BIT_MAX     = 1 << 3;

    private static final int GEOM_PASSTHROUGH    = 0;
    private static final int GEOM_LINES_TO_QUADS = 1;


    private static final int   DEFAULT_BUF_SIZE = 64 * 1024;
    private static final float SCALE_FLOAT      = 255f / 1f;

    private final int[] mVbo = { 0 };
    private final int[] mIbo = { 0 };
    private final ByteBuffer mVertBuf;
    private final ByteBuffer mIndBuf;

    private final Config mConfig = new Config();
    private final Vert   mVert   = new Vert();


    private DrawEnv mG;

    private final Config                  mKeyConfig   = new Config();
    private final Map<Config, VertWriter> mWriters     = new HashMap<Config, VertWriter>();
    private final IndWriter               mQuadIndexer = new QuadIndWriter();


    private VertWriter mActiveWriter  = null;
    private IndWriter  mActiveIndexer = null;
    private int        mActiveCap     = 0;
    private int        mActivePos     = 0;
    private int        mActiveMode    = 0;


    public DrawStream() {
        this( DEFAULT_BUF_SIZE );
    }


    public DrawStream( int bufSize ) {
        mVertBuf = DrawUtil.alloc( bufSize );
        mIndBuf = DrawUtil.alloc( bufSize / 16 * 6 / 4 );
    }


    public void init( DrawEnv g ) {
        mG = g;
        GL3 gl = g.mGl;

        if( mVbo[0] != 0 ) {
            return;
        }

        gl.glGenBuffers( 1, mVbo, 0 );
        gl.glBindBuffer( GL_ARRAY_BUFFER, mVbo[0] );
        gl.glBufferData( GL_ARRAY_BUFFER, mVertBuf.capacity(), null, GL_STREAM_DRAW );
        gl.glBindBuffer( GL_ARRAY_BUFFER, 0 );
        DrawUtil.checkErr( gl );

        gl.glGenBuffers( 1, mIbo, 0 );
        gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, mIbo[0] );
        gl.glBufferData( GL_ELEMENT_ARRAY_BUFFER, mIndBuf.capacity(), null, GL_STREAM_DRAW );
        gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, 0 );
        DrawUtil.checkErr( gl );
    }


    public void config( boolean color, boolean tex, boolean norm ) {
        mConfig.set( color, tex, norm, GEOM_PASSTHROUGH );
    }


    public void beginPoints() {
        mKeyConfig.set( mConfig );
        begin( getVertWriter( mKeyConfig ), null, GL_POINTS, 1 );
    }


    public void beginLines() {
        mKeyConfig.set( mConfig );
        if( mG.mLineWidth.mValue != 1f ) {
            mKeyConfig.setGeom( GEOM_LINES_TO_QUADS );
        }
        begin( getVertWriter( mKeyConfig ), null, GL_LINES, 2 );
    }


    public void beginLineStrip() {
        mKeyConfig.set( mConfig );
        if( mG.mLineWidth.mValue != 1f ) {
            mKeyConfig.setGeom( GEOM_LINES_TO_QUADS );
        }
        begin( getVertWriter( mKeyConfig ), null, GL_LINE_STRIP, 2 );
    }


    public void beginLineLoop() {
        mKeyConfig.set( mConfig );
        if( mG.mLineWidth.mValue != 1f ) {
            mKeyConfig.setGeom( GEOM_LINES_TO_QUADS );
        }
        begin( getVertWriter( mKeyConfig ), null, GL_LINE_LOOP, 2 );
    }


    public void beginTris() {
        mKeyConfig.set( mConfig );
        begin( getVertWriter( mKeyConfig ), null, GL_TRIANGLES, 3 );
    }


    public void beginTriStrip() {
        mKeyConfig.set( mConfig );
        begin( getVertWriter( mKeyConfig ), null, GL_TRIANGLE_STRIP, 3 );
    }


    public void beginQuads() {
        mKeyConfig.set( mConfig );
        begin( getVertWriter( mKeyConfig ), mQuadIndexer, GL_TRIANGLES, 4 );
    }


    public void beginQuadStrip() {
        mKeyConfig.set( mConfig );
        begin( getVertWriter( mKeyConfig ), null, GL_TRIANGLE_STRIP, 4 );
    }

    /**
     * @param vertWriter  Writer used to serialized DrawStream.Vert objects into VBO.
     * @param indexer     Writer used to write vertex indices into IBO.
     * @param mode        Draw mode, GL_LINES, GL_TRIANGLES, GL_TRIANGLE_STRIP, etc.
     * @param blockSize   Number of vertices that must be written consecutively without flushing between.
     *                    Points=1. Lines=2. Triangles=3. TriangleStrips=3. Quads=4. QuadStrips=4.
     */
    public void begin( VertWriter vertWriter, IndWriter indexer, int mode, int blockSize ) {
        mG.checkErr();

        mActiveMode = mode;
        mActiveWriter = vertWriter;
        mVertBuf.clear();
        int bytes = mVertBuf.capacity();
        int vertBytes = vertWriter.bytesPerVert();
        mActiveCap = ( bytes / ( vertBytes * blockSize ) ) * blockSize;
        mActivePos = 0;
        mActiveWriter.bind( mG );
        mActiveIndexer = indexer;
        if( indexer != null ) {
            indexer.reset();
            mIndBuf.clear();
            mG.mGl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, mIbo[0] );
        }
        DrawUtil.checkErr( mG.mGl );
    }


    public void end() {
        if( mActiveWriter == null ) {
            return;
        }
        if( mActivePos > 0 ) {
            flush();
        }
        if( mActiveIndexer != null ) {
            mG.mGl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, 0 );
            mActiveIndexer = null;
        }
        mActiveWriter.unbind( mG );
        DrawUtil.checkErr( mG.mGl );
    }


    public void color( int red, int green, int blue ) {
        color( red, green, blue, 0xFF );
    }


    public void color( int red, int green, int blue, int alpha ) {
        mVert.mColor = ( alpha << 24 & 0xFF000000 ) |
                       ( blue  << 16 & 0x00FF0000 ) |
                       ( green <<  8 & 0x0000FF00 ) |
                       ( red         & 0x000000FF );
    }


    public void color( int rgba ) {
        mVert.mColor = rgba;
    }


    public void color( float red, float green, float blue ) {
        color( red, green, blue, 1f );
    }


    public void color( float red, float green, float blue, float alpha ) {
        color( (int)(SCALE_FLOAT * red),
               (int)(SCALE_FLOAT * green),
               (int)(SCALE_FLOAT * blue),
               (int)(SCALE_FLOAT * alpha) );
    }


    public void color( Vec3 v ) {
        color( v.x, v.y, v.z, 1f );
    }


    public void color( Vec4 v ) {
        color( v.x, v.y, v.z, 1f );
    }


    public void norm( int x, int y, int z ) {
        norm( (float)x, (float)y, (float)z );
    }


    public void norm( float x, float y, float z ) {
        Vec3 v = mVert.mNorm;
        v.x = x;
        v.y = y;
        v.z = z;
    }


    public void norm( float[] v ) {
        tex( v[0], v[1], v[2], 1f );
    }


    public void tex( int x, int y ) {
        this.tex( (float)x, (float)y, 0f, 1f );
    }


    public void tex( float x, float y ) {
        tex( x, y, 0f, 1f );
    }


    public void tex( int x, int y, int z ) {
        tex( (float)x, (float)y, (float)z, 1f );
    }


    public void tex( float x, float y, float z ) {
        tex( x, y, z, 1 );
    }


    public void tex( int x, int y, int z, int w ) {
        tex( (float)x, (float)y, (float)z, (float)w );
    }


    public void tex( float x, float y, float z, float w ) {
        Vec4 v = mVert.mTex;
        v.x = x;
        v.y = y;
        v.z = z;
        v.w = w;
    }


    public void tex( Vec2 v ) {
        tex( v.x, v.y, 0f, 1f );
    }


    public void tex( Vec3 v ) {
        tex( v.x, v.y, v.z, 1f );
    }


    public void tex( Vec4 v ) {
        Vec.put( v, mVert.mTex );
    }


    public void vert( int x, int y ) {
        vert( (float)x, (float)y, 0f, 1f );
    }


    public void vert( float x, float y ) {
        vert( x, y, 0f, 1f );
    }


    public void vert( int x, int y, int z ) {
        vert( (float)x, (float)y, (float)z, 1f );
    }


    public void vert( float x, float y, float z ) {
        vert( x, y, z, 1 );
    }


    public void vert( int x, int y, int z, int w ) {
        vert( (float)x, (float)y, (float)z, (float)w );
    }


    public void vert( float x, float y, float z, float w ) {
        Vec.put( x, y, z, w, mVert.mVert );
        mActiveWriter.write( mVert, mVertBuf );
        if( mActiveIndexer != null ) {
            mActiveIndexer.write( mActivePos, mIndBuf );
        }
        if( ++mActivePos < mActiveCap ) {
            return;
        }
        flush();
    }


    public void vert( Vec3 v ) {
        vert( v.x, v.y, v.z, 1f );
    }


    public void vert( Vec4 v ) {
        vert( v.x, v.y, v.z, v.w );
    }


    public void pointSize( float f ) {
        mG.mGl.glPointSize( f );
    }



    private void flush() {
        mVertBuf.clear();
        mG.mGl.glBufferSubData( GL_ARRAY_BUFFER, 0, mVertBuf.remaining(), mVertBuf );
        if( mActiveIndexer == null ) {
            mG.mGl.glDrawArrays( mActiveMode, 0, mActivePos );
        } else {
            mIndBuf.clear();
            mG.mGl.glBufferSubData( GL_ELEMENT_ARRAY_BUFFER, 0, mIndBuf.remaining(), mIndBuf );
            mG.mGl.glDrawElements( mActiveMode, mActiveIndexer.count(), GL_UNSIGNED_INT, 0 );
        }
        mActivePos = 0;
    }


    private VertWriter getVertWriter( Config config ) {
        config.makeCanonical();
        VertWriter writer = mWriters.get( config );
        if( writer == null ) {
            writer = config.createWriter( mG, mVbo[0] );
            mWriters.put( new Config( config ), writer );
        }
        return writer;
    }



    private static Program createProgram( DrawEnv g, String vertPath, String geomPath, String fragPath ) {
        Program prog = new Program();
        prog.addShader( g.mShaderMan.loadResource( GL_VERTEX_SHADER, SHADER_PATH + '/' + vertPath ) );
        if( geomPath != null ) {
            prog.addShader( g.mShaderMan.loadResource( GL_GEOMETRY_SHADER, SHADER_PATH + '/' + geomPath ) );
        }
        prog.addShader( g.mShaderMan.loadResource( GL_FRAGMENT_SHADER, SHADER_PATH + '/' + fragPath ) );
        prog.init( g );
        UniformLoaders.addAvailableLoaders( prog );
        prog.bind( g );
        UniformLoaders.setDefaultTexUnits( g, prog );
        prog.unbind( g );
        g.checkErr();
        return prog;
    }


    private static int colorPointer( DrawEnv g, Program prog, int off, int stride ) {
        int index = prog.attrib( ShaderUtil.ATT_COLOR ).mLocation;
        g.mGl.glVertexAttribPointer( index, 4, GL_UNSIGNED_BYTE, true, stride, off );
        g.mGl.glEnableVertexAttribArray( index );
        return off + 4;
    }


    private static int texPointer( DrawEnv g, Program prog, int off, int stride ) {
        int index = prog.attrib( ShaderUtil.ATT_TEX_COORD0 ).mLocation;
        g.mGl.glVertexAttribPointer( index, 4, GL_FLOAT, false, stride, off );
        g.mGl.glEnableVertexAttribArray( index );
        return off + 16;
    }


    private static int normPointer( DrawEnv g, Program prog, int off, int stride ) {
        int index = prog.attrib( ShaderUtil.ATT_NORMAL ).mLocation;
        g.mGl.glVertexAttribPointer( index, 3, GL_FLOAT, false, stride, off );
        g.mGl.glEnableVertexAttribArray( index );
        return off + 12;
    }


    private static int vertPointer( DrawEnv g, Program prog, int off, int stride ) {
        int index = prog.attrib( ShaderUtil.ATT_VERTEX ).mLocation;
        g.mGl.glVertexAttribPointer( index, 4, GL_FLOAT, false, stride, off );
        g.mGl.glEnableVertexAttribArray( index );
        return off + 16;
    }



    private static class Config {

        private boolean mColor = false;
        private boolean mNorm  = false;
        private boolean mTex   = false;

        private int mGeom = GEOM_PASSTHROUGH;


        public Config() {}


        public Config( Config copy ) {
            set( copy );
        }



        public void set( Config copy ) {
            mColor = copy.mColor;
            mNorm  = copy.mNorm;
            mTex   = copy.mTex;
            mGeom  = copy.mGeom;
        }


        public void set( boolean color, boolean tex, boolean norm, int geom ) {
            mColor = color;
            mTex   = tex;
            mNorm  = norm;
            mGeom  = geom;
        }


        public void setGeom( int geom ) {
            mGeom = geom;
        }


        public void makeCanonical() {
            if( mGeom == GEOM_LINES_TO_QUADS ) {
                mColor = true;
                mNorm  = false;
                mTex   = false;
            } else if( !mTex ) {
                mColor = true;
                mNorm  = false;
            }
        }


        public VertWriter createWriter( DrawEnv d, int vbo ) {
            if( mGeom == GEOM_PASSTHROUGH ) {
                if( !mTex ) {
                    return new ColorWriter( d, vbo );
                }

                if( mColor ) {
                    if( mNorm ) {
                        return new ColorNormTexWriter( d, vbo );
                    } else {
                        return new ColorTexWriter( d, vbo );
                    }
                } else {
                    if( mNorm ) {
                        return new NormTexWriter( d, vbo );
                    } else {
                        return new TexWriter( d, vbo );
                    }
                }
            }

            if( mGeom == GEOM_LINES_TO_QUADS ) {
                Program prog = createProgram( d, "ColorGeom.vert", "ColorLinesToQuads.geom", "Color.frag" );
                return new ColorWriter( d, vbo, prog );
            }

            return null;
        }

        @Override
        public boolean equals( Object obj ) {
            if( !( obj instanceof Config ) ) {
                return false;
            }
            Config c = (Config)obj;
            return mColor == c.mColor &&
                   mNorm  == c.mNorm  &&
                   mTex   == c.mTex   &&
                   mGeom  == c.mGeom;

        }

        @Override
        public int hashCode() {
            int ret = mGeom;
            if( mTex ) {
                ret |= 0x08;
            }
            if( mNorm ) {
                ret |= 0x10;
            }
            if( mColor ) {
                ret |= 0x20;
            }
            return ret;
        }

    }


    public static final class Vert {
        public int mColor = 0xFFFFFFFF;
        public final Vec3 mNorm = new Vec3();
        public final Vec4 mTex  = new Vec4();
        public final Vec4 mVert = new Vec4();
    }


    public static interface VertWriter extends DrawUnit {
        public int bytesPerVert();
        public void write( Vert vert, ByteBuffer out );
    }


    private static abstract class BaseWriter extends DrawUnitAdapter implements VertWriter {
        final Program mProg;
        final int[] mVao = { 0 };

        public BaseWriter( DrawEnv g, int vbo, String vertPath, String geomPath, String fragPath ) {
            this( g, vbo, createProgram( g, vertPath, geomPath, fragPath ) );
        }

        public BaseWriter( DrawEnv g, int vbo, Program prog ) {
            mProg = prog;
            GL2ES3 gl = g.mGl;
            gl.glGenVertexArrays( 1, mVao, 0 );
            gl.glBindVertexArray( mVao[0] );
            gl.glBindBuffer( GL_ARRAY_BUFFER, vbo );
        }

        @Override
        public void bind( DrawEnv g ) {
            mProg.bind( g );
            g.mGl.glBindVertexArray( mVao[0] );
        }

        @Override
        public void unbind( DrawEnv g ) {
            g.mGl.glBindVertexArray( 0 );
            mProg.unbind( g );
        }

    }


    private static final class ColorWriter extends BaseWriter {

        public ColorWriter( DrawEnv g, int vbo ) {
            this( g, vbo, createProgram( g, "Color.vert", null, "Color.frag" ) );
        }

        public ColorWriter( DrawEnv g, int vbo, Program prog ) {
            super( g, vbo, prog );
            int off = 0;
            off = colorPointer( g, mProg, off, bytesPerVert() );
            off = vertPointer( g, mProg, off, bytesPerVert() );
            g.mGl.glBindVertexArray( 0 );
        }

        @Override
        public int bytesPerVert() {
            return 4 + 16;
        }

        @Override
        public void write( DrawStream.Vert vert, ByteBuffer out ) {
            out.putInt( vert.mColor );
            Vec.put( vert.mVert, out );
        }

    }


    private static final class TexWriter extends BaseWriter {

        public TexWriter( DrawEnv g, int vbo ) {
            super( g, vbo, "Tex.vert", null, "Tex.frag" );
            int off = 0;
            off = texPointer( g, mProg, off, bytesPerVert() );
            off = vertPointer( g, mProg, off, bytesPerVert() );
            g.mGl.glBindVertexArray( 0 );
        }

        @Override
        public int bytesPerVert() {
            return 16 + 16;
        }

        @Override
        public void write( Vert vert, ByteBuffer out ) {
            Vec.put( vert.mTex, out );
            Vec.put( vert.mVert, out );
        }

    }


    private static final class ColorTexWriter extends BaseWriter {

        public ColorTexWriter( DrawEnv g, int vbo ) {
            super( g, vbo, "ColorTex.vert", null, "ColorTex.frag" );
            int off = 0;
            off = colorPointer( g, mProg, off, bytesPerVert() );
            off = texPointer( g, mProg, off, bytesPerVert() );
            off = vertPointer( g, mProg, off, bytesPerVert() );
            g.mGl.glBindVertexArray( 0 );
        }

        @Override
        public int bytesPerVert() {
            return 4 + 16 + 16;
        }

        @Override
        public void write( Vert vert, ByteBuffer out ) {
            out.putInt( vert.mColor );
            Vec.put( vert.mTex, out );
            Vec.put( vert.mVert, out );
        }

    }


    private static final class NormTexWriter extends BaseWriter {

        public NormTexWriter( DrawEnv g, int vbo ) {
            super( g, vbo, "NormTex.vert", null, "Tex.frag" );
            int off = 0;
            off = normPointer( g, mProg, off, bytesPerVert() );
            off = texPointer( g, mProg, off, bytesPerVert() );
            off = vertPointer( g, mProg, off, bytesPerVert() );
            g.mGl.glBindVertexArray( 0 );
        }

        @Override
        public int bytesPerVert() {
            return 16 + 16 + 16;
        }

        @Override
        public void write( Vert vert, ByteBuffer out ) {
            Vec.put( vert.mNorm, out );
            Vec.put( vert.mTex, out );
            Vec.put( vert.mVert, out );
        }

    }


    private static final class ColorNormTexWriter extends BaseWriter {

        public ColorNormTexWriter( DrawEnv g, int vbo ) {
            super( g, vbo, "ColorNormTex.vert", null, "ColorTex.frag" );
            int off = 0;
            off = colorPointer( g, mProg, off, bytesPerVert() );
            off = normPointer( g, mProg, off, bytesPerVert() );
            off = texPointer( g, mProg, off, bytesPerVert() );
            off = vertPointer( g, mProg, off, bytesPerVert() );
            g.mGl.glBindVertexArray( 0 );
        }

        @Override
        public int bytesPerVert() {
            return 4 + 16 + 16 + 16;
        }

        @Override
        public void write( Vert vert, ByteBuffer out ) {
            out.putInt( vert.mColor );
            Vec.put( vert.mNorm, out );
            Vec.put( vert.mTex, out );
            Vec.put( vert.mVert, out );
        }

    }


    public static interface IndWriter {
        void reset();
        void write( int ind, ByteBuffer out );
        int count();
    }


    private static class QuadIndWriter implements IndWriter {
        int mCount = 0;
        int mPos   = 0;
        int[] mV   = { 0, 0, 0, 0 };

        public void reset() {
            mPos = 0;
            mCount = 0;
        }

        public void write( int ind, ByteBuffer out ) {
            mV[mPos++] = ind;
            if( mPos == 4 ) {
                out.putInt( mV[0] );
                out.putInt( mV[1] );
                out.putInt( mV[2] );
                out.putInt( mV[0] );
                out.putInt( mV[2] );
                out.putInt( mV[3] );
                mPos = 0;
                mCount += 6;
            }
        }

        public int count() {
            return mCount;
        }
    }

}
