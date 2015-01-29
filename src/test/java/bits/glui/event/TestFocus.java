/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui.event;

import javax.swing.JFrame;

import bits.draw3d.DrawEnv;
import bits.draw3d.DrawStream;
import bits.glui.*;

/**
* @author decamp
*/
public class TestFocus {


    public static void main( String[] args ) {
        test();
    }


    private static void test() {
        GRootController cont = GRootController.create();

        JFrame frame = new JFrame();
        frame.setSize( 1024, 1024 + 30 );
        frame.setLocationRelativeTo( null );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.add( cont.component() );

        frame.setVisible( true );

        int w = frame.getContentPane().getWidth();
        int h = frame.getContentPane().getHeight();

        cont.rootPane().addChild( newPanel( 0, 0, w, h, 3, 3, 3, new int[]{ 0 } ) );
        cont.startAnimator( 40.0 );
    }


    private static Panel newPanel( int x, int y, int w, int h, int rows, int cols, int depth, int[] index ) {
        Panel p = new Panel( index[0]++ );
        p.bounds( x, y, w, h );
        if( depth == 1 ) {
            return p;
        }

        final int margin = 10;
        for( int row = 0; row < rows; row++ ) {
            for( int col = 0; col < cols; col++ ) {
                int x0 = (w - margin) * col / cols + margin * 2;
                int x1 = (w - margin) * (col + 1) / cols - margin;
                int y0 = (h - margin) * row / rows + margin * 2;
                int y1 = (h - margin) * (row + 1) / rows - margin;

                p.addChild( newPanel( x0, y0, x1 - x0, y1 - y0, rows, cols, depth - 1, index ) );
            }
        }

        return p;
    }


    private static class Panel extends GPanel implements GMouseListener, GMouseMotionListener, GMouseWheelListener, GFocusListener, GKeyListener {

        private final int mIndex;
        private boolean mHasFocus = false;
        private boolean mHasMouse = false;
        private boolean mMouseDown = false;
        private boolean mMouse3Down = false;


        public Panel( int index ) {
            mIndex = index;
            addMouseListener( this );
            addMouseMotionListener( this );
            addMouseWheelListener( this );
            addFocusListener( this );
            addKeyListener( this );
        }


        @Override
        public void paintComponent( DrawEnv d ) {
            final DrawStream s = d.drawStream();
            float r = mMouse3Down ? 1 : 0;
            float g = mHasMouse ? 1 : 0;
            float b = mMouseDown ? 1 : 0;

            s.config( true, false, false );
            s.color( r, g, b );
            s.beginQuads();
            s.vert( 0, 0 );
            s.vert( width(), 0 );
            s.vert( width(), height() );
            s.vert( 0, height() );
            s.end();

            if( !mHasFocus ) {
                s.color( 0.5f, 0.5f, 0.5f );
                d.mLineWidth.apply( 1f );
            } else {
                s.color( 1f, 1f, 1f );
                d.mLineWidth.apply( 3f );
            }

            d.mCullFace.apply( true );
            s.beginLineLoop();
            s.vert( 0, 0 );
            s.vert( width(), 0 );
            s.vert( width(), height() );
            s.vert( 0, height() );
            s.end();
        }


        public void mouseEntered( GMouseEvent e ) {
            mHasMouse = true;
        }


        public void mouseExited( GMouseEvent e ) {
            mHasMouse = false;
        }


        public void mousePressed( GMouseEvent e ) {
            if( e.getButton() == 1 ) {
                mMouseDown = true;

            } else if( e.getButton() == 3 ) {
                mMouse3Down = true;

                if( (e.getModifiers() & GMouseEvent.META_DOWN_MASK) != 0 ) {
                    if( (e.getModifiers() & GMouseEvent.SHIFT_DOWN_MASK) == 0 ) {
                        System.out.println( "Start modal: " + mIndex );
                        startModal();
                    } else {
                        System.out.println( "Stop modal: " + mIndex );
                        stopModal();
                    }
                }
            }

            requestFocus();
        }


        public void mouseReleased( GMouseEvent e ) {
            if( e.getButton() == 1 ) {
                mMouseDown = false;
            } else if( e.getButton() == 3 ) {
                mMouse3Down = false;
            }
        }


        public void mouseClicked( GMouseEvent e ) {
            System.out.println( mIndex + " clicked : " + e.getClickCount() );
        }

        @Override
        public void mouseMoved( GMouseEvent e ) {}

        @Override
        public void mouseDragged( GMouseEvent e ) {
            System.out.println( this + "\t" + e.getX() + ", " + e.getY() );
        }


        public void focusGained( GFocusEvent e ) {
            System.out.println( "Has transferFocus: " + mIndex );
            mHasFocus = true;
        }


        public void focusLost( GFocusEvent e ) {
            mHasFocus = false;
        }


        public void keyPressed( GKeyEvent e ) {
            System.out.println( mIndex + " pressed " + e.getKeyChar() + "\t" + e.getKeyCode() );
        }


        public void keyReleased( GKeyEvent e ) {
            System.out.println( mIndex + " released " + e.getKeyChar() + "\t" + e.getKeyCode() );
        }


        public void keyTyped( GKeyEvent e ) {
            System.out.println( mIndex + " typed " + e.getKeyChar() + "\t" + e.getKeyCode() );
        }


        public String toString() {
            return "Panel " + mIndex;
        }

        @Override
        public void mouseWheelMoved( GMouseWheelEvent e ) {
            System.out.println( mIndex + " scrolled " + e.getScrollType() + "\t" + e.getScrollAmount() + "\t" + e.getWheelRotation() );
        }
    }

}
