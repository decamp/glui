package bits.glui;

import java.io.Serializable;


/**
 * Rect is a utility class based by the Pygame Rect module.
 * <p/>
 * Rect objects are immutable. Boxes always define positive spaces and will
 * never return a negative width or height.  Attempting to define a Rect
 * with a negative dimension will result in a Rect with positive dimensions
 * that covers the same region of space.
 * <p/>
 * Both the horizontal and vertical ranges of a Rect are computed as
 * half-open sets. That is, the left and top edges are on the Rect
 * interior, whereas the right and bottom edges are on the Rect exterior.
 * As is standard, if the box has zero size, it does not intersect with
 * any point or other box.
 *
 * @author Philip DeCamp
 */
public class Rect implements Serializable {

    static final long serialVersionUID = 4624999276134559686L;


    /**
     * Create a new Rect by specifying left and top edges and width and height
     * dimensions.
     *
     * @param minX  The left edge of the Rect.
     * @param minY  The top edge of the Rect.
     * @param spanX The width of the Rect.
     * @param spanY The height of the Rect.
     * @return a new Rect object with the specified edges and dimensions.
     */
    public static Rect fromBounds( int minX, int minY, int spanX, int spanY ) {
        return new Rect( minX, minY, minX + spanX, minY + spanY );
    }

    /**
     * Create a new Rect by specifying location of all four edges.
     *
     * @param minX The left edge of the Rect.
     * @param minY The top edge of the Rect.
     * @param maxX The right edge of the Rect.
     * @param maxY The bottom edge of the Rect.
     */
    public static Rect fromEdges( int minX, int minY, int maxX, int maxY ) {
        return new Rect( minX, minY, maxX, maxY );
    }

    /**
     * Creates a new Rect by specifying center and size.
     */
    public static Rect fromCenter( int centerX, int centerY, int spanX, int spanY ) {
        centerX -= spanX / 2;
        centerY -= spanY / 2;
        return new Rect( centerX, centerY, centerX + spanX, centerY + spanY );
    }

    /** Minimum X coord */
    public int x0;
    /** Minimum Y coord */
    public int y0;
    /** Maximum X coord */
    public int x1;
    /** Maximum Y coord */
    public int y1;


    public Rect() {}

    
    public Rect( int minX, int minY, int maxX, int maxY ) {
        if( minX <= maxX ) {
            x0 = minX;
            x1 = maxX;
        } else {
            x0 = maxX;
            x1 = minX;
        }

        if( minY <= maxY ) {
            y0 = minY;
            y1 = maxY;
        } else {
            y0 = maxY;
            y1 = minY;
        }
    }


    public Rect( Rect copy ) {
        set( copy );
    }


    /****** GET ******/

    /**
     * @return the X-coordinate of this Rect's position; the minimum X value in the box.
     */
    public int x() {
        return x0;
    }

    /**
     * @return the Y-coordinate of this Rect's position; the minimum Y value in the box.
     */
    public int y() {
        return y0;
    }

    /**
     * @return the max X value of this box.
     */
    public int maxX() {
        return x1;
    }

    /**
     * @return the max Y value of this Rect.
     */
    public int maxY() {
        return y1;
    }

    /**
     * @return the center point between the left and right edges.
     */
    public int centerX() {
        return (x0 + x1) / 2;
    }

    /**
     * @return the center point between the bottom and top edges.
     */
    public int centerY() {
        return (y0 + y1) / 2;
    }

    /******* SET ********/

    public void set( Rect copy ) {
        x0 = copy.x0;
        y0 = copy.y0;
        x1 = copy.x1;
        y1 = copy.y1;
    }

    /**
     * Set min dimensions of rect.
     */
    public void position( int x, int y ) {
        x1 = ( x1 - x0 ) + x;
        y1 = ( y1 - y0 ) + y;
        x0 = x;
        y0 = y;
    }

    /**
     * Sets center position of box.
     *
     * @param x X-coord of center point.
     * @param y Y-coord of center point.
     */
    public void center( int x, int y ) {
        int sx = x1 - x0;
        int sy = y1 - y0;
        x0 = x - sx / 2;
        x1 = x0 + sx;
        y0 = y - sy / 2;
        y1 = y0 + sy;
    }


    /****** SIZE ******/

    /**
     * @return the width of this Rect. May be negative.
     */
    public int width() {
        return x1 - x0;
    }

    /**
     * @return the height of this Rect. May be negative.
     */
    public int height() {
        return y1 - y0;
    }

    /**
     * @return the absolute area of this Rect.
     */
    public int area() {
        return Math.abs( (x1 - x0) * (y1 - y0) );
    }

    /**
     * Creates a new Rect with same position but the specified dimensions.
     *
     * @return new Rect
     */
    public Rect size( int width, int height ) {
        return Rect.fromBounds( x0, y0, x0 + width, y0 + height );
    }


    /****** TRANSFORMATIONS ******/

    /**
     * Fits thiis rectangly completely inside the argument Rect.
     * If the Rect is too large to fit inside, it is centered
     * inside the argument Rect, but its size is not changed.
     *
     * @param rect Rect in which to fit {@code this} Rect.
     */
    public void clamp( Rect rect ) {
        int minx, maxx, miny, maxy;

        if( x0 < rect.x0 ) {
            minx = rect.x0;
            maxx = minx + x1 - x0;

            if( maxx > rect.x1 ) {
                minx = (rect.x0 + rect.x1 + x0 - x1) / 2;
                maxx = minx + x1 - x0;
            }

        } else if( x1 > rect.x1 ) {
            maxx = rect.x1;
            minx = maxx + x0 - x1;

            if( minx < rect.x0 ) {
                minx = (rect.x0 + rect.x1 + x0 - x1) / 2;
                maxx = minx + x1 - x0;
            }

        } else {
            minx = x0;
            maxx = x1;
        }

        if( y0 < rect.y0 ) {
            miny = rect.y0;
            maxy = miny + y1 - y0;

            if( maxy > rect.y1 ) {
                miny = (rect.y0 + rect.y1 + y0 - y1) / 2;
                maxy = miny + y1 - y0;
            }

        } else if( y1 > rect.y1 ) {
            maxy = rect.y1;
            miny = maxy + y0 - y1;

            if( miny < rect.y0 ) {
                miny = (rect.y0 + rect.y1 + y0 - y1) / 2;
                maxy = miny + y1 - y0;
            }

        } else {
            miny = y0;
            maxy = y1;
        }

        x0 = minx;
        y0 = miny;
        x1 = maxx;
        y1 = maxy;
    }

    /**
     * Centers this Rect inside another Rect and scales the size until the two
     * Boxs share borders, but does not affect the aspect ratio.
     *
     * @param rect Boxangle into which to fit this Rect.
     */
    public void fit( Rect rect ) {
        if( width() * rect.height() > rect.width() * height() ) {
            int height = (int)((double)(height() * rect.width()) / width() + 0.5);
            int margin = (rect.height() - height) / 2;
            x0 = rect.x0;
            y0 = rect.y0 + margin;
            x1 = rect.x1;
            y1 = rect.y0 + margin + height;

        } else {
            int width = (int)((double)(width() * rect.height()) / height() + 0.5);
            int margin = (rect.width() - width) / 2;
            x0 = rect.x0 + margin;
            y0 = rect.y0;
            x1 = rect.x0 + margin + width;
            y1 = rect.y1;
        }
    }

    /**
     * Scales the size of the Rect without changing the center point.
     *
     * @param scaleX Horizontal scale factor.
     * @param scaleY Vertical scale factor.
     */
    public void inflate( double scaleX, double scaleY ) {
        int w = (int)Math.round( ( x1 - x0 ) * scaleX );
        int h = (int)Math.round( ( y1 - y0 ) * scaleY );
        x0 = centerX() - w / 2;
        y1 = centerY() - h / 2;
        x1 = x0 + w;
        y1 = y0 + h;
    }

    /**
     * Scales the size of the box without changing the center point.
     *
     * @param scaleX Horizontal scale factor.
     * @param scaleY Vertical scale factor.
          */
    public void inflate( int scaleX, int scaleY ) {
        int w = width() * scaleX;
        int h = height() * scaleY;
        x0 = centerX() - w / 2;
        y0 = centerY() - h / 2;
        x1 = x0 + w;
        y1 = y0 + h;
    }

    /**
     * Computes intersection between {@code a} and {@code b}.
     * If boxes do not overlap on a given dimension, then the output will
     * be located entirely within {@code this} on the side nearest to
     * {@code rect} and will have zero size. For example, the calling
     * <code>new Rect(0,0,1,1).intersect( new Rect(  2, 0, 3, 1 ), out )</code>
     * will result in
     * {@code out = [ 1, 0, 1, 1 ]}.
     *
     * @return true iff boxes contain non-zero overlap.
     */
    public boolean intersect( Rect rect ) {
        boolean nonZero = true;
        x0 = x0 >= rect.x0 ? x0 :
             x1 >= rect.x0 ? x1 : rect.x0;
        x1 = x1 <= rect.x1 ? x1 : rect.x1;
        if( x0 >= x1 ) {
            x1 = x0;
            nonZero = false;
        }

        y0 = y0 >= rect.y0 ? y0 :
             y1 >= rect.y0 ? y1 : rect.y0;
        y1 = y1 <= rect.y1 ? y1 : rect.y1;
        if( y0 >= y1 ) {
            y1 = y0;
            nonZero = false;
        }

        return nonZero;
    }

    /**
     * Computes the smallest rect which contains completely this Rect and
     * the specified Rect.
     *
     * @return Rect representing union of {@code this} and {@code rect}.
     */
    public void union( Rect rect ) {
        if( rect.x0 < x0 ) x0 = rect.x0;
        if( rect.y0 < y0 ) y0 = rect.y0;
        if( rect.x1 > x1 ) x1 = rect.x1;
        if( rect.y1 > y1 ) y1 = rect.y1;
    }

    /**
     * Multiplies location and size.
     *
     * @param multX Amount to multiply the width and left edge.
     * @param multY Amount to multiply the height and top edge.
     */
    public void scale( int multX, int multY ) {
        x0 *= multX;
        y0 *= multY;
        x1 *= multX;
        y1 *= multY;
    }

    /**
     * Moves the Rect.
     *
     * @param dx Amount to mouseMoved the Rect horizontally.
     * @param dy Amount to mouseMoved the Rect vertically.
     */
    public void translate( int dx, int dy ) {
        x0 += dx;
        y0 += dy;
        x1 += dx;
        y1 += dy;
    }


    /****** TESTS ******/

    /**
     * Tests if Rect intersects with a given point.
     *
     * @param x The x coordinate of a point.
     * @param y The y coordinate of a point.
     * @return true iff the point lies within the Rect.
     */
    public boolean intersects( int x, int y ) {
        return x >= x0 &&
               x < x1 &&
               y >= y0 &&
               y < y1;
    }

    /**
     * Tests if this Rect has any intersection with other Rect.
     *
     * @param rect A rect to check for overlap.
     * @return true if Boxes have any intersection.
     */
    public boolean intersects( Rect rect ) {
        return x1 > rect.x0 &&
               y1 > rect.y0 &&
               x0 < rect.x1 &&
               y0 < rect.y1;
    }



    public boolean equals( Object obj ) {
        if( !(obj instanceof Rect) ) {
            return false;
        }

        Rect b = (Rect)obj;
        return x0 == b.x0 && y0 == b.y0 && x1 == b.x1 && y1 == b.y1;
    }

    @Override
    public int hashCode() {
        return x0 ^ x1 ^ y0 ^ y1;
    }


    public String toString() {
        return "Rect [" + x0 + ", " + y0 + "][ w:" + ( x0 + x1 ) + ", h:" + (y0 + y1) + "]";
    }

}
