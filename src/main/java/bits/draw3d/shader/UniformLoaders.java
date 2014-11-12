package bits.draw3d.shader;

import bits.glui.GGraphics;
import bits.math3d.Mat;
import bits.math3d.Mat4;

import java.nio.FloatBuffer;

/**
 * @author Philip DeCamp
 */
public class UniformLoaders {

    public static final String PROJ_MAT          = "projMat";
    public static final String INV_PROJ_MAT      = "invProjMat";
    public static final String VIEW_MAT          = "viewMat";
    public static final String INV_VIEW_MAT      = "invViewMat";
    public static final String PROJ_VIEW_MAT     = "projViewMat";
    public static final String INV_PROJ_VIEW_MAT = "invProjViewMat";
    public static final String NORM_MAT          = "normMat";
    public static final String INV_NORM_MAT      = "invNormMat";
    public static final String VIEWPORT          = "viewport";
    public static final String VIEWPORT_MAT      = "viewportMat";
    public static final String INV_VIEWPORT_MAT  = "invViewportMat";
    public static final String COLOR_MAT         = "colorMat";
    public static final String INV_COLOR_MAT     = "invColorMat";
    public static final String TEX_MAT           = "texMat";
    public static final String INV_TEX_MAT       = "invTexMat";

    public static final String TEX_UNIT0         = "texUnit0";
    public static final String TEX_UNIT1         = "texUnit1";
    public static final String TEX_UNIT2         = "texUnit2";
    public static final String TEX_UNIT3         = "texUnit3";
    public static final String TEX_UNIT4         = "texUnit4";
    public static final String TEX_UNIT5         = "texUnit5";
    public static final String TEX_UNIT6         = "texUnit6";
    public static final String TEX_UNIT7         = "texUnit7";



    public static DrawTask loaderFor( ProgramResource res ) {
        String name = res.mName.intern();
        if( name == PROJ_MAT ) {
            return new ProjMat( res.mLocation );
        } else if( name == INV_PROJ_MAT ) {
            return new InvProjMat( res.mLocation );
        } else if( name == VIEW_MAT ) {
            return new ViewMat( res.mLocation );
        } else if( name == INV_VIEW_MAT ) {
            return new InvViewMat( res.mLocation );
        } else if( name == PROJ_VIEW_MAT ) {
            return new ProjViewMat( res.mLocation );
        } else if( name == INV_PROJ_VIEW_MAT ) {
            return new InvProjViewMat( res.mLocation );
        } else if( name == NORM_MAT ) {
            return new NormMat( res.mLocation );
        } else if( name == INV_NORM_MAT ) {
            return new InvNormMat( res.mLocation );
        }

        return null;
    }


    public static void addAvailableLoaders( Program prog ) {
        for( ProgramResource res: prog.uniforms().values() ) {
            DrawTask task = loaderFor( res );
            if( task != null ) {
                prog.addBindTask( task );
            }
        }
    }


    /**
     * Sets all sampler uniforms of the form "texUnitXXX" to value "XXX".
     * @param prog Must be bound
     */
    public static void setDefaultTexUnits( GGraphics g, Program prog ) {
        for( ProgramResource res: prog.uniforms().values() ) {
            String name = res.mName;
            if( !name.startsWith( "texUnit" ) ) {
                continue;
            }
            try {
                int n = Integer.parseInt( name.substring( 7 ) );
                g.mGl.glUniform1i( res.mLocation, n );
                g.checkErr();
            } catch( NumberFormatException ignored ) {}
        }
        g.checkErr();
    }


    public static final class ProjMat implements DrawTask {
        private final int mLocation;
        public ProjMat( int location ) {
            mLocation = location;
        }

        public void run( GGraphics g ) {
            FloatBuffer buf = g.mWorkFloats;
            buf.clear();
            Mat.put( g.mProj.get(), buf );
            buf.flip();
            g.mGl.glUniformMatrix4fv( mLocation, 1, false, buf );
        }
    }


    public static final class InvProjMat implements DrawTask {
        private final int mLocation;
        public InvProjMat( int location ) {
            mLocation = location;
        }

        public void run( GGraphics g ) {
            Mat4 mat = g.mWorkMat4;
            FloatBuffer buf = g.mWorkFloats;

            Mat.invert( g.mProj.get(), mat );
            buf.clear();
            Mat.put( mat, buf );
            buf.flip();
            g.mGl.glUniformMatrix4fv( mLocation, 1, false, buf );
        }
    }


    public static final class ViewMat implements DrawTask {
        private final int mLocation;
        public ViewMat( int location ) {
            mLocation = location;
        }

        public void run( GGraphics g ) {
            Mat4 mat = g.mWorkMat4;
            FloatBuffer buf = g.mWorkFloats;
            buf.clear();
            Mat.put( mat, buf );
            buf.flip();
            g.mGl.glUniformMatrix4fv( mLocation, 1, false, buf );
        }
    }


    public static final class InvViewMat implements DrawTask {
        private final int mLocation;
        public InvViewMat( int location ) {
            mLocation = location;
        }

        public void run( GGraphics g ) {
            Mat4 mat = g.mWorkMat4;
            FloatBuffer buf = g.mWorkFloats;

            Mat.invert( g.mView.get(), mat );
            buf.clear();
            Mat.put( mat, buf );
            buf.flip();
            g.mGl.glUniformMatrix4fv( mLocation, 1, false, buf );
        }
    }


    public static final class ProjViewMat implements DrawTask {
        private final int mLocation;
        public ProjViewMat( int location ) {
            mLocation = location;
        }

        public void run( GGraphics g ) {
            Mat4 mat = g.mWorkMat4;
            FloatBuffer buf = g.mWorkFloats;
            Mat.mult( g.mProj.get(), g.mView.get(), mat );
            buf.clear();
            Mat.put( mat, buf );
            buf.flip();
            g.mGl.glUniformMatrix4fv( mLocation, 1, false, buf );
        }
    }


    public static final class InvProjViewMat implements DrawTask {
        private final int mLocation;
        public InvProjViewMat( int location ) {
            mLocation = location;
        }

        public void run( GGraphics g ) {
            Mat4 mat = g.mWorkMat4;
            FloatBuffer buf = g.mWorkFloats;

            Mat.mult( g.mProj.get(), g.mView.get(), mat );
            Mat.invert( mat, mat );
            buf.clear();
            Mat.put( mat, buf );
            buf.flip();
            g.mGl.glUniformMatrix4fv( mLocation, 1, false, buf );
        }
    }


    public static final class NormMat implements DrawTask {
        private final int mLocation;
        public NormMat( int location ) {
            mLocation = location;
        }

        public void run( GGraphics g ) {
            // normMat = transpose( inverse( modelView ) )

            Mat4 mat = g.mWorkMat4;
            Mat.invert( g.mView.get(), mat );
            FloatBuffer buf = g.mWorkFloats;
            buf.clear();
            // Only load transpose of top 3x3.
            buf.put( mat.m00 ).put( mat.m01 ).put( mat.m02 );
            buf.put( mat.m10 ).put( mat.m11 ).put( mat.m12 );
            buf.put( mat.m20 ).put( mat.m21 ).put( mat.m22 );
            buf.flip();
            g.mGl.glUniformMatrix4fv( mLocation, 1, false, buf );
        }
    }


    public static final class InvNormMat implements DrawTask {
        private final int mLocation;
        public InvNormMat( int location ) {
            mLocation = location;
        }

        public void run( GGraphics g ) {
            Mat4 mat = g.mWorkMat4;
            Mat.invert( g.mView.get(), mat );
            FloatBuffer buf = g.mWorkFloats;
            buf.clear();
            // Only load transpose of top 3x3.
            buf.put( mat.m00 ).put( mat.m01 ).put( mat.m02 );
            buf.put( mat.m10 ).put( mat.m11 ).put( mat.m12 );
            buf.put( mat.m20 ).put( mat.m21 ).put( mat.m22 );
            buf.flip();
            g.mGl.glUniformMatrix4fv( mLocation, 1, false, buf );
        }
    }


}
