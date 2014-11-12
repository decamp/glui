package bits.draw3d;

import bits.draw3d.shader.*;
import bits.glui.GGraphics;
import bits.math3d.*;

import java.nio.ByteBuffer;

import javax.media.opengl.*;
import static javax.media.opengl.GL3.*;


/**
 * @author Philip DeCamp
 */
public class DrawStream {

    private static final String SHADER_PATH = "glsl/bits/draw3d/shader";
    private static final int    BIT_COLOR   = 1 << 0;
    private static final int    BIT_NORM    = 1 << 1;
    private static final int    BIT_TEX     = 1 << 2;
    private static final int    BIT_MAX     = 1 << 3;

    private static final int   DEFAULT_BUF_SIZE = 64 * 1024;
    private static final float SCALE_FLOAT      = 255f / 1f;

    private final int[] mVbo = { 0 };
    private final int[] mIbo = { 0 };
    private final ByteBuffer mVertBuf;
    private final ByteBuffer mIndBuf;

    private final Config mConfig   = new Config();
    private final Vert   mVert     = new Vert();

    private GGraphics mG;

    private VertWriter[] mVertWriters = new VertWriter[BIT_MAX];
    private IndWriter    mQuadIndexer = new QuadIndWriter();


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
        mIndBuf  = DrawUtil.alloc( bufSize / 16 * 6 / 4 );
    }



    public void init( GGraphics g ) {
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
        mConfig.mColor = color;
        mConfig.mTex = tex;
        mConfig.mNorm = norm;
    }


    public void beginPoints() {
        begin( getVertWriter(), null, GL_POINTS, 1 );
    }


    public void beginLines() {
        begin( getVertWriter(), null, GL_LINES, 2 );
    }


    public void beginLineStrip() {
        begin( getVertWriter(), null, GL_LINE_STRIP, 2 );
    }


    public void beginTris() {
        begin( getVertWriter(), null, GL_TRIANGLES, 3 );
    }


    public void beginTriStrip() {
        begin( getVertWriter(), null, GL_TRIANGLE_STRIP, 3 );
    }


    public void beginQuads() {
        begin( getVertWriter(), mQuadIndexer, GL_TRIANGLES, 4 );
    }


    public void beginQuadStrip() {
        begin( getVertWriter(), null, GL_TRIANGLE_STRIP, 4 );
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


    private VertWriter getVertWriter() {
        int flags = 0;
        VertWriter writer = null;

        if( mConfig.mTex ) {
            flags |= BIT_TEX;
            if( mConfig.mNorm ) {
                flags |= BIT_NORM;
                if( mConfig.mColor ) {
                    flags |= BIT_COLOR;
                    writer = mVertWriters[ flags ];
                    if( writer == null ) {
                        writer = new ColorNormTexWriter( mG, mVbo[0] );
                        mVertWriters[ flags ] = writer;
                    }
                    return writer;

                } else {
                    writer = mVertWriters[ flags ];
                    if( writer == null ) {
                        writer = new NormTexWriter( mG, mVbo[0] );
                        mVertWriters[ flags ] = writer;
                    }
                    return writer;
                }

            } else if( mConfig.mColor ) {
                flags |= BIT_COLOR;
                writer = mVertWriters[ flags ];
                if( writer == null ) {
                    writer = new ColorTexWriter( mG, mVbo[0] );
                    mVertWriters[ flags ] = writer;
                }
                return writer;

            } else {
                writer = mVertWriters[ flags ];
                if( writer == null ) {
                    writer = new TexWriter( mG, mVbo[0] );
                    mVertWriters[ flags ] = writer;
                }
                return writer;

            }
        } else {
            flags |= BIT_COLOR;
            writer = mVertWriters[ flags ];
            if( writer == null ) {
                writer = new ColorWriter( mG, mVbo[0] );
                mVertWriters[ flags ] = writer;
            }
            return writer;
        }
    }



    private static Program createProgram( GGraphics g, String vertPath, String geomPath, String fragPath ) {
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


    private static int colorPointer( GGraphics g, Program prog, int off, int stride ) {
        int index = prog.attrib( ShaderUtil.ATT_COLOR ).mLocation;
        g.mGl.glVertexAttribPointer( index, 4, GL_UNSIGNED_BYTE, true, stride, off );
        g.mGl.glEnableVertexAttribArray( index );
        return off + 4;
    }


    private static int texPointer( GGraphics g, Program prog, int off, int stride ) {
        int index = prog.attrib( ShaderUtil.ATT_TEX_COORD0 ).mLocation;
        g.mGl.glVertexAttribPointer( index, 4, GL_FLOAT, false, stride, off );
        g.mGl.glEnableVertexAttribArray( index );
        return off + 16;
    }


    private static int normPointer( GGraphics g, Program prog, int off, int stride ) {
        int index = prog.attrib( ShaderUtil.ATT_NORMAL ).mLocation;
        g.mGl.glVertexAttribPointer( index, 3, GL_FLOAT, false, stride, off );
        g.mGl.glEnableVertexAttribArray( index );
        return off + 12;
    }


    private static int vertPointer( GGraphics g, Program prog, int off, int stride ) {
        int index = prog.attrib( ShaderUtil.ATT_VERTEX ).mLocation;
        g.mGl.glVertexAttribPointer( index, 4, GL_FLOAT, false, stride, off );
        g.mGl.glEnableVertexAttribArray( index );
        return off + 16;
    }



    private static class Config {
        public boolean mColor = true;
        public boolean mNorm  = false;
        public boolean mTex   = false;


        public Config() {}


        @Override
        public boolean equals( Object obj ) {
            if( !( obj instanceof Config ) ) {
                return false;
            }
            Config c = (Config)obj;
            return mColor == c.mColor &&
                   mNorm  == c.mNorm  &&
                   mTex   == c.mTex;
        }

        @Override
        public int hashCode() {
            int ret = mTex ? 1 : 0;
            if( mNorm ) {
                ret |= 0x2;
            }
            if( mColor ) {
                ret |= 0x4;
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

        public BaseWriter( GGraphics g, int vbo, String vertPath, String geomPath, String fragPath ) {
            mProg = createProgram( g, vertPath, geomPath, fragPath );
            GL2ES3 gl = g.mGl;
            gl.glGenVertexArrays( 1, mVao, 0 );
            gl.glBindVertexArray( mVao[0] );
            gl.glBindBuffer( GL_ARRAY_BUFFER, vbo );
        }

        @Override
        public void bind( GGraphics g ) {
            mProg.bind( g );
            g.mGl.glBindVertexArray( mVao[0] );
        }

        @Override
        public void unbind( GGraphics g ) {
            g.mGl.glBindVertexArray( 0 );
            mProg.unbind( g );
        }

    }


    private static final class ColorWriter extends BaseWriter {

        public ColorWriter( GGraphics g, int vbo ) {
            super( g, vbo, "Color.vert", null, "Color.frag" );
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

        public TexWriter( GGraphics g, int vbo ) {
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

        public ColorTexWriter( GGraphics g, int vbo ) {
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

        public NormTexWriter( GGraphics g, int vbo ) {
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

        public ColorNormTexWriter( GGraphics g, int vbo ) {
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
