package cogmac.glui.event;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * @author decamp
 */
public class TestFocusAwt {

    
    public static void main(String[] args) {
        test();
    }

    
    private static void test() {
        
        JFrame frame = new JFrame();
        frame.setSize( 1024, 1024 + 30 );
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout( null );
        frame.setVisible(true);
        
        
        int w = frame.getContentPane().getWidth();
        int h = frame.getContentPane().getHeight();
        
        frame.setContentPane( newPanel( 0, 0, w, h, 3, 3, 3, new int[]{0} ) );
    }
    
    
    
    private static Panel newPanel( int x, int y, int w, int h, int rows, int cols, int depth, int[] index ) {
        Panel p = new Panel( index[0]++ );
        p.setBounds( x, y, w, h );
        
        if(depth == 1)
            return p;
        
        final int margin = 10;
        
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                int x0 = (w - margin) * col / cols + margin * 2;
                int x1 = (w - margin) * (col + 1) / cols - margin;
                int y0 = (h - margin) * row / rows + margin * 2;
                int y1 = (h - margin) * (row + 1) / rows - margin; 
                
                p.add(newPanel(x0, y0, x1 - x0, y1 - y0, rows, cols, depth - 1, index));
            }
        }
        
        return p;
    }
    
    
    
    private static class Panel extends JPanel implements MouseListener, FocusListener, KeyListener {
    
        private final int mIndex;
        private boolean mHasFocus  = false;
        private boolean mHasMouse  = false;
        private boolean mMouseDown = false;
        private boolean mMouse3Down = false;
        
        
        public Panel( int index ) {
            setLayout( null );
            
            mIndex = index;
            
            addMouseListener( this );
            addFocusListener( this );
            addKeyListener( this );
            
            setFocusable( true );
            setOpaque( false );
        }

        
        
        @Override
        public void paintComponent( Graphics gg ) {
            
            float r = mMouse3Down ? 1 : 0;
            float g = mHasMouse  ? 1 : 0;
            float b = mMouseDown ? 1 : 0;
            
            Graphics2D gl = (Graphics2D)gg;
            gl.setColor( new Color( r, g, b ) );
            gl.fillRect( 0, 0, getWidth() - 1, getHeight() - 1 );
                        
            if(!mHasFocus) {
                gl.setColor( Color.GRAY );
                gl.setStroke( new BasicStroke( 1f ) );
            }else{
                gl.setColor( Color.WHITE );
                gl.setStroke( new BasicStroke( 2f ) );
            }
            
            gl.drawRect( 0, 0, getWidth() - 1, getHeight() - 1 );
        }


        public void mouseEntered(MouseEvent e) {
            mHasMouse = true;
            repaint();
        }


        public void mouseExited(MouseEvent e) {
            mHasMouse = false;
            repaint();
        }


        public void mousePressed(MouseEvent e) {
            if(e.getButton() == 1) {
                mMouseDown = true;
                
            }else if(e.getButton() == 3) {
                mMouse3Down = true;
            }
        
            requestFocus();
            repaint();
        }


        public void mouseReleased(MouseEvent e) {
            if(e.getButton() == 1) {
                mMouseDown = false;
            } else if( e.getButton() == 3 ) {
                mMouse3Down = false;
            }
            
            repaint();
        }


        public void mouseClicked(MouseEvent e) {}


        public void focusGained(FocusEvent e) {
            mHasFocus = true;
            repaint();
        }


        public void focusLost(FocusEvent e) {
            mHasFocus = false;
            repaint();
        }


        public void keyPressed(KeyEvent e) {
            System.out.println(mIndex + " pressed " + e.getKeyChar() + "\t" + e.getKeyCode());
        }


        public void keyReleased(KeyEvent e) {
            System.out.println(mIndex + " released " + e.getKeyChar() + "\t" + e.getKeyCode());
        }


        public void keyTyped(KeyEvent e) {
            System.out.println(mIndex + " typed " + e.getKeyChar() + "\t" + e.getKeyCode());
        }

        
        public String toString() {
            return "Panel " + mIndex;
        }

    }
    
}
