package bits.glui;

import java.io.Serializable;


/**
 * Box is a utility class based by the Pygame Rect module.
 * <p>
 * Box objects are immutable. Boxes always define positive spaces and will
 * never return a negative width or height.  Attempting to define a Box
 * with a negative dimension will result in a Box with positive dimensions
 * that covers the same region of space.
 * <p>
 * Both the horizontal and vertical ranges of a Box are computed as
 * half-open sets. That is, the left and top edges are on the Box
 * interior, whereas the right and bottom edges are on the Box exterior.
 * As is standard, if the box has zero size, it does not intersect with
 * any point or other box.
 * 
 * @author Philip DeCamp
 */
public class Box implements Serializable {

    
    static final long serialVersionUID = 4624999276134559686L;
    
    
    /**
     * Create a new Box by specifying left and top edges and width and height
     * dimensions.
     * 
     * @param minX   The left edge of the Box.
     * @param minY   The top edge of the Box.
     * @param spanX  The width of the Box.
     * @param spanY  The height of the Box.
     * @returns a new Box object with the specified edges and dimensions.
     */
    public static Box fromBounds(int minX, int minY, int spanX, int spanY) {
        return new Box(minX, minY, minX + spanX, minY + spanY);
    }

    /**
     * Create a new Box by specifying location of all four edges.
     * 
     * @param minX   The left edge of the Box.
     * @param minY   The top edge of the Box.
     * @param maxX   The right edge of the Box.
     * @param maxY   The bottom edge of the Box.
     * @returns a new Box object with the specified edges.
     */
    public static Box fromEdges(int minX, int minY, int maxX, int maxY) {
        return new Box(minX, minY, maxX, maxY);
    }

    /**
     * Creates a new Box by specifying center and size.
     * 
     * @param centerX
     * @param centerY
     * @param spanX
     * @param spanY
     * @returns a new Box
     */
    public static Box fromCenter(int centerX, int centerY, int spanX, int spanY) {
        centerX -= spanX / 2;
        centerY -= spanY / 2;

        return new Box(centerX, centerY, centerX + spanX, centerY + spanY);
    }
    
    
    private final int mMinX;
    private final int mMinY;
    private final int mMaxX;
    private final int mMaxY;

    
    private Box(int minX, int minY, int maxX, int maxY) {
        if(minX <= maxX) {
            mMinX = minX;
            mMaxX = maxX;
        }else{
            mMinX = maxX;
            mMaxX = minX;
        }
        
        if(minY <= maxY) {
            mMinY = minY;
            mMaxY = maxY;
        }else{
            mMinY = maxY;
            mMaxY = minY;
        }
    }

    
    /****** POSITION ******/
    
    /**
     * @returns the X-coordinate of this Box's position; the minimum X value in the box.
     */
    public int x() {
        return mMinX;
    }

    /**
     * @returns the Y-coordinate of this Box's position; the minimum Y value in the box.
     */
    public int y() {
        return mMinY;
    }
    
    /**
     * @returns the max X value of this box.
     */
    public int maxX() {
        return mMaxX;
    }
    
    /**
     * @returns the max Y value of this Box.
     */
    public int maxY() {
        return mMaxY;
    }
    
    /**
     * @returns the center point between the left and right edges.
     */
    public int centerX() {
        return (mMinX + mMaxX) / 2;
    }

    /**
     * @returns the center point between the bottom and top edges.
     */
    public int centerY() {
        return (mMinY + mMaxY) / 2;
    }
    
    /**
     * Creates new Box with same dimensions but with the specified position.
     */
    public Box position(int x, int y) {
        return new Box(x, y, x + mMaxX - mMinX, y + mMaxY - mMinY);
    }
    
    /**
     * Creates new box with same dimensions but different center point.
     * 
     * @param x X-coord of center point.
     * @param y Y-coord of center point.
     * @returns new box object with specified center point.
     */
    public Box center(int x, int y) {
        return Box.fromCenter(x, y, width(), height());
    }
    
    
    /****** SIZE ******/

    /**
     * @returns the width of this Box. May be negative.
     */
    public int width() {
        return mMaxX - mMinX;
    }

    /**
     * @returns the height of this Box. May be negative.
     */
    public int height() {
        return mMaxY - mMinY;
    }

    /**
     * @returns the absolute area of this Box.
     */
    public int area() {
        return Math.abs((mMaxX - mMinX) * (mMaxY - mMinY));
    }

    /**
     * Creates a new Box with same position but the specified dimensions.
     * @param width
     * @param height
     * @return new Box
     */
    public Box size(int width, int height) {
        return Box.fromBounds(mMinX, mMinY, mMinX + width, mMinY + height);
    }

    
    /****** TRANSFORMATIONS ******/

    /**
     * Creates a new Box that has the size of <code>this</code> Box, but is
     * moved completely inside the argument Box. If the Box is too large
     * to fit inside, it is centered inside the argument Box, but its size is
     * not changed.
     * 
     * @param box Box in which to fit <code>this</code> Box.
     * @returns a new Box.
     */
    public Box clamp(Box box) {
        int left, right, top, bottom;

        if (mMinX < box.mMinX) {
            left = box.mMinX;
            right = left + mMaxX - mMinX;

            if (right > box.mMaxX) {
                left = (box.mMinX + box.mMaxX + mMinX - mMaxX) / 2;
                right = left + mMaxX - mMinX;
            }

        } else if (mMaxX > box.mMaxX) {
            right = box.mMaxX;
            left = right + mMinX - mMaxX;

            if (left < box.mMinX) {
                left = (box.mMinX + box.mMaxX + mMinX - mMaxX) / 2;
                right = left + mMaxX - mMinX;
            }

        } else {
            left = mMinX;
            right = mMaxX;
        }

        if (mMinY < box.mMinY) {
            top = box.mMinY;
            bottom = top + mMaxY - mMinY;

            if (bottom > box.mMaxY) {
                top = (box.mMinY + box.mMaxY + mMinY - mMaxY) / 2;
                bottom = top + mMaxY - mMinY;
            }

        } else if (mMaxY > box.mMaxY) {
            bottom = box.mMaxY;
            top = bottom + mMinY - mMaxY;

            if (top < box.mMinY) {
                top = (box.mMinY + box.mMaxY + mMinY - mMaxY) / 2;
                bottom = top + mMaxY - mMinY;
            }

        } else {
            top = mMinY;
            bottom = mMaxY;
        }

        return new Box(left, top, right, bottom);
    }

    /**
     * @returns the intersection between <code>this</code> Box and the
     *          Parameter Box. If the two Boxs do not overlap, a Box with 0
     *          size is returned.
     */
    public Box clip(Box box) {
        return new Box( Math.max(mMinX, box.mMinX), 
                        Math.max(mMinY, box.mMinY), 
                        Math.min(mMaxX, box.mMaxX), 
                        Math.min(mMaxY, box.mMaxY));
    }

    /**
     * Centers this Box inside another Box and scales the size until the two
     * Boxs share borders, but does not affect the aspect ratio.
     * 
     * @param box  Boxangle into which to fit this Box.
     * @returns new Box object that fits inside bounds.
     */
    public Box fit(Box box) {
        if(width() * box.height() > box.width() * height()) {
            int height = (int)((double)(height() * box.width()) / width() + 0.5);
            int margin = (box.height() - height) / 2;

            return new Box(box.x(), box.y() + margin, box.maxX(), box.y() + margin + height);
            
        }else{
            int width = (int)((double)(width() * box.height()) / height() + 0.5);
            int margin = (box.width() - width) / 2;
            
            return new Box(box.x() + margin, box.y(), box.x() + margin + width, box.maxY());
        }
    }

    /**
     * Scales the size of the Box without changing the center point.
     * 
     * @param scaleX  Horizontal scale factor.
     * @param scaleY  Vertical scale factor.
     * @returns new Box with scaled width and height.
     */
    public Box inflate(double scaleX, double scaleY) {
        return Box.fromCenter( centerX(), 
                               centerY(),
                               (int)Math.round(width() * scaleX),
                               (int)Math.round(height() * scaleY) );
    }
    
    /**
     * Scales the size of the box without changing the center point.
     * 
     * @param scaleX  Horizontal scale factor.
     * @param scaleY  Vertical scale factor.
     * @returns new Box with scaled width and height.
     */
    public Box inflate(int scaleX, int scaleY) {
        return Box.fromCenter(centerX(), centerY(), width() * scaleX, height() * scaleY);
    }
    
    /**
     * Computes the smallest box which contains completely this Box and 
     * the specified Box.
     * 
     * @returns Box representing union of <tt>this</tt> and <tt>box</tt>.
     */
    public Box union(Box box) {
        return new Box( Math.min(mMinX, box.mMinX), 
                        Math.min(mMinY, box.mMinY),
                        Math.max(mMaxX, box.mMaxX), 
                        Math.max(mMaxY, box.mMaxY) );
    }

    /**
     * Multiplies location and size.
     * 
     * @param multX Amount to multiply the width and left edge.
     * @param multY Amount to multiply the height and top edge.
     * @returns new Box object.
     */
    public Box scale(int multX, int multY) {
        return new Box(mMinX * multX, mMinY * multY, mMaxX * multX, mMaxY * multY);
    }

    /**
     * Moves the Box.
     * 
     * @param dx Amount to mouseMoved the Box horizantally.
     * @param dy Amount to mouseMoved the Box vertically.
     * @returns a new Box.
     */
    public Box translate(int dx, int dy) {
        return new Box(mMinX + dx, mMinY + dy, mMaxX + dx, mMaxY + dy);
    }

    
    /****** TESTS ******/

    /**
     * Tests if Box intersects with a given point.
     * 
     * @param x  The x coordinate of a point.
     * @param y  The y coordinate of a point.
     * @returns true iff the point lies within the Box.
     */
    public boolean intersects(int x, int y) {
        return x >= mMinX && 
               x < mMaxX && 
               y >= mMinY && 
               y < mMaxY;
    }

    /**
     * Tests if this Box has any intersection with other Box.
     * 
     * @param box A box to check for overlap.
     * @returns true if Boxes have any intersection.
     */
    public boolean intersects(Box box) {
        return mMaxX > box.mMinX &&
               mMaxY > box.mMinY &&
               mMinX < box.mMaxX &&
               mMinY < box.mMaxY;
    }

    
    
    public boolean equals(Object obj) {
        if(!(obj instanceof Box))
            return false;

        Box b = (Box)obj;
        return mMinX == b.mMinX && mMinY == b.mMinY && mMaxX == b.mMaxX && mMaxY == b.mMaxY;
    }
    
    
    @Override
    public int hashCode() {
        return (mMinX ^ mMaxX ^ mMinY ^ mMaxY);
    }

        
    public String toString() {
        StringBuilder b = new StringBuilder("Box [");
        b.append(mMinX);
        b.append(", ");
        b.append(mMinY);
        b.append(", ");
        b.append((mMaxX - mMinX));
        b.append(", ");
        b.append((mMaxY - mMinY));
        b.append("]");
        return b.toString();
    }

}
