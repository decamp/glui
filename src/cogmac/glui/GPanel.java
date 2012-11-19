package cogmac.glui;

import java.awt.Font;
import java.awt.event.*;
import java.util.*;

import static javax.media.opengl.GL.*;
import javax.media.opengl.GL;

import cogmac.glui.event.*;


/**
 * @author decamp
 */
public class GPanel implements GComponent {
    
    private static final GColor DEFAULT_FOREGROUND = new GColor(1,1,1,1);
    private static final GColor DEFAULT_BACKGROUND = null;
    private static final Font DEFAULT_FONT         = new Font("Verdana", Font.PLAIN, 12);
    
    private final List<GComponent> mChildren     = new ArrayList<GComponent>();
    private final List<GComponent> mSafeChildren = Collections.unmodifiableList(mChildren);
    private GComponent mParent = null;
    private GLayout mLayout = null;
        
    private int mX = 0;
    private int mY = 0;
    private int mW = 0;
    private int mH = 0;
    private Box mAbsoluteBounds = Box.fromBounds(0, 0, 0, 0);
    
    private GColor mForeground = DEFAULT_FOREGROUND;
    private GColor mBackground = DEFAULT_BACKGROUND;
    private Font mFont         = DEFAULT_FONT;
    
    private boolean mVisible = true;
    private boolean mEnabled = true;
    private boolean mFocusable    = false;
    private boolean mRequestFocus = true;
    
    private boolean mNeedsLayout = false;
    private boolean mNeedsPaint = false;

    private GComponentListener mComponentCaster = null;
    private GFocusListener mFocusCaster = null;
    private GPaintListener mPaintCaster = null;
    private GMouseListener mMouseCaster = null;
    private GMouseMotionListener mMouseMotionCaster = null;
    private GMouseWheelListener mMouseWheelCaster = null;
    private GKeyListener mKeyCaster = null;
    
    
    
    public void addChild(GComponent pane) {
        if(mChildren.contains(pane))
            return;
        
        mChildren.add(pane);
        pane.setParent(this);
        updateLayout();
    }
    
    public void removeChild(GComponent pane) {
        if(mChildren.remove(pane)) {
            pane.setParent(null);
            updateLayout();
        }
    }
        
    public void clearChildren() {
        if(mChildren.isEmpty())
            return;
        
        for(GComponent p: mChildren)
            p.setParent(null);
        
        mChildren.clear();
        updateLayout();
    }
    
    public List<GComponent> getChildren() {
        return mSafeChildren;
    }
    
    public void setLayout(GLayout layout) {
        mLayout = layout;
    }
    
    public GComponent getParent() {
        return mParent;
    }
    
    
    
    public Box bounds() {
        return Box.fromBounds(mX, mY, mW, mH);
    }
    
    public Box absoluteBounds() {
        Box ret = mAbsoluteBounds;
        
        if(ret == null) {
            GComponent parent = mParent;
            
            if(parent == null) {
                ret = Box.fromBounds(mX, mY, mW, mH);
            }else{
                ret = parent.absoluteBounds();
                ret = Box.fromBounds(ret.x() + mX, ret.y() + mY, mW, mH);
            }
            
            mAbsoluteBounds = ret;
        }
        
        return ret;
    }
    
    public int x() {
        return mX;
    }
    
    public int y() {
        return mY;
    }
    
    public int width() {
        return mW;
    }
    
    public int height() {
        return mH;
    }
    
    public boolean contains(int x, int y) {
        if(!mVisible)
            return false;
        
        return x >= 0 && y >= 0 && x < mW && y < mH;
    }

    public GComponent position(int x, int y) {
        if(mX != x || mY != y) {
            mX = x;
            mY = y;
            mAbsoluteBounds = null;
            notifyNewBounds(true, false);
        }
            
        return this;
    }
    
    public GComponent bounds(int x, int y, int w, int h) {
        boolean moved = x != mX || y != mY;
        boolean resized = w != mW || h != mH;
        
        if(moved || resized) {
            mX = x;
            mY = y;
            mW = w;
            mH = h;
            mAbsoluteBounds = null;
            updateLayout();
            notifyNewBounds(moved, resized);
        }
        
        return this;
    }

    public GComponent size(int w, int h) {
        if(w != mW || h != mH) {
            mAbsoluteBounds = null;
            mW = w;
            mH = h;
            updateLayout();
            notifyNewBounds(false, true);
       }
     
        return this;
    }
    
    public void setVisible(boolean visible) {
        if(visible == mVisible)
            return;
        
        mVisible = visible;
        if(mComponentCaster == null)
            return;
        
        int code = visible ? ComponentEvent.COMPONENT_SHOWN : ComponentEvent.COMPONENT_HIDDEN;
        processComponentEvent(new GComponentEvent(this, code));
    }
    
    public boolean isVisible() {
        return mVisible;
    }

    public void setEnabled(boolean enable) {
        if(enable == mEnabled)
            return;
        
        mEnabled = enable;
    }

    public boolean isEnabled() {
        return mEnabled;
    }
    
    
    public GPanel font(Font font) {
        mFont = font == null ? DEFAULT_FONT : font;
        return this;
    }

    public Font font() {
        return mFont;
    }
    
    public GPanel foreground(GColor foreground) {
        mForeground = foreground == null ? DEFAULT_FOREGROUND : foreground;
        return this;
    }
    
    public GColor foreground() {
        return mForeground;
    }
    
    public GPanel background(GColor background) {
        mBackground = background;
        return this;
    }
    
    public GColor background() {
        return mBackground;
    }
    
    
    public void setFocusable(boolean focusable) {
        mFocusable = focusable;
        transferFocusForward(); 
    }
    
    public boolean isFocusable() {
        return mFocusable;
    }
    
    public void setRequestFocusEnabled(boolean requestFocus) {
        mRequestFocus = requestFocus;
    }
    
    public boolean isRequestFocusEnabled() {
        return mRequestFocus;
    }
    
    public boolean requestFocus() {
        if(!GToolkit.isFocusable(this))
            return false;
        
        fireRequestFocus(this);
        return true;
    }

    public void transferFocusBackward() {
        fireTransferFocusBackward(this);
    }
    
    public void transferFocusForward() {
        fireTransferFocusForward(this);
    }
    
    
    public void startModal() {
        firePushInputRoot(this);
    }
    
    public void stopModal() {
        firePopInputRoot(this);
    }
    
    
    public void addComponentListener(GComponentListener listener) {
        mComponentCaster = GluiMulticaster.add(mComponentCaster, listener);
    }
    
    public void removeComponentListener(GComponentListener listener) {
        mComponentCaster = GluiMulticaster.remove(mComponentCaster, listener);
    }
   
    public void addFocusListener(GFocusListener listener) {
        mFocusCaster = GluiMulticaster.add(mFocusCaster, listener);
    }
    
    public void removeFocusListener(GFocusListener listener) {
        mFocusCaster = GluiMulticaster.remove(mFocusCaster, listener);
    }
    
    public void addMouseListener(GMouseListener listener) {
        mMouseCaster = GluiMulticaster.add(mMouseCaster, listener);
    }
    
    public void removeMouseListener(GMouseListener listener) {
        mMouseCaster = GluiMulticaster.remove(mMouseCaster, listener);
    }
    
    public void addMouseMotionListener(GMouseMotionListener listener) {
        mMouseMotionCaster = GluiMulticaster.add(mMouseMotionCaster, listener);
    }
    
    public void removeMouseMotionListener(GMouseMotionListener listener) {
        mMouseMotionCaster = GluiMulticaster.remove(mMouseMotionCaster, listener);
    }
    
    public void addMouseWheelListener(GMouseWheelListener listener) {
        mMouseWheelCaster = GluiMulticaster.add(mMouseWheelCaster, listener);
    }
    
    public void removeMouseWheelListener(GMouseWheelListener listener) {
        mMouseWheelCaster = GluiMulticaster.remove(mMouseWheelCaster, listener);
    }
    
    public void addKeyListener(GKeyListener listener) {
        mKeyCaster = GluiMulticaster.add(mKeyCaster, listener);
    }
    
    public void removeKeyListener(GKeyListener listener) {
        mKeyCaster = GluiMulticaster.remove(mKeyCaster, listener);
    }
    
    public void addPaintListener(GPaintListener listener) {
        mPaintCaster = GluiMulticaster.add(mPaintCaster, listener);
    }

    public void removePaintListener(GPaintListener listener) {
        mPaintCaster = GluiMulticaster.remove(mPaintCaster, listener);
    }
    
    
    
    public GComponent componentAt(int x, int y) {
        if(!contains(x, y))
            return null;
        
        int size = mChildren.size();
        
        while(size-- > 0) {
            GComponent child = mChildren.get(size);
            GComponent ret = child.componentAt(x - child.x(), y - child.y());
            if(ret != null) {
                return ret;
            }
        }
        
        return this;
    }
    
    
    
    public void updateLayout() {
        if(mNeedsLayout)
            return;
        
        mNeedsLayout = true;
        fireLayoutRequest(this);
    }

    public boolean needsLayout() {
        return mNeedsLayout;
    }
    
    public void repaint() {
        if(mNeedsPaint)
            return;
        
        mNeedsPaint = true;
        fireDrawRequest(this);
    }
    
    public boolean needsRepaint() {
        return mNeedsPaint;
    }

    
    protected void paint(GGraphics g) {
        paintComponent(g);
        paintChildren(g);
    }
    
    protected void paintComponent(GGraphics g) {
        GPaintListener c = mPaintCaster;
        if(c != null)
            c.paint(g);
    }
    
    protected void paintChildren(GGraphics g) {
        if(mChildren.isEmpty())
            return;
        
        GL gl = g.gl();

        for(GComponent p: mChildren) {
            prepareView(g, p);
            p.processPaintEvent(g);
        }
    }
    
    protected void prepareView(GGraphics g, GComponent p) {
        GL gl = g.gl();
        
        Box b = p.absoluteBounds();
        Box viewport = g.contextViewport();
        
        int x = b.x() - viewport.x();
        int y = b.y() - viewport.y();
        int w = b.width();
        int h = b.height();

        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, w, 0, h, -1, 1);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        
        //gl.glTranslated(b.minX(), b.minY(), 0.0);
        //gl.glViewport((int)b.minX(), (int)b.minY(), (int)b.spanX(), (int)b.spanY());
        gl.glViewport(x, y, w, h);
        //gl.glScissor(x, y, w, h);   
    }
    
    
    
    public void setParent(GComponent pane) {
        mParent = pane;
    }

    public void processLayoutEvent() {
        mNeedsLayout = false;
        GLayout m = mLayout;

        Box b = (mParent == null ? bounds() : mParent.bounds());
        mAbsoluteBounds = Box.fromBounds(mX + b.x(), mY + b.y(), mW, mH);
        
        if(m != null)
            m.layoutPane(this);
            
        if(!mChildren.isEmpty()) {
            for(GComponent c: mChildren) {
                c.processLayoutEvent();
            }
        }
    }

    public void processPaintEvent(GGraphics g) {
        mNeedsPaint = false;
        if(!mVisible)
            return;
        
        paint(g);
    }
    
    public void processComponentEvent(GComponentEvent e) {
        GComponentListener c = mComponentCaster;
        if(c == null)
            return;
        
        switch(e.id()) {
        case GComponentEvent.COMPONENT_MOVED:
            c.componentMoved(e);
            break;
            
        case GComponentEvent.COMPONENT_RESIZED:
            c.componentResized(e);
            break;
        
        case GComponentEvent.COMPONENT_SHOWN:
            c.componentShown(e);
            break;
            
        case GComponentEvent.COMPONENT_HIDDEN:
            c.componentHidden(e);
            break;
        }   
    }
    
    public void processFocusEvent(GFocusEvent e) {
        GFocusListener f = mFocusCaster;
        if(f == null)
            return;
        
        switch(e.id()) {
        case GFocusEvent.FOCUS_GAINED:
            f.focusGained(e);
            break;
        case GFocusEvent.FOCUS_LOST:
            f.focusLost(e);
            break;
        }
    }
    
    public void processMouseEvent(GMouseEvent e) {
        GMouseListener m = mMouseCaster;
        if(m == null)
            return;
        
        switch(e.id()) {
        case GMouseEvent.MOUSE_PRESSED:
            m.mousePressed(e);
            break;
        case GMouseEvent.MOUSE_RELEASED:
            m.mouseReleased(e);
            break;
        case GMouseEvent.MOUSE_CLICKED:
            m.mouseClicked(e);
            break;
        case GMouseEvent.MOUSE_ENTERED:
            m.mouseEntered(e);
            break;
        case GMouseEvent.MOUSE_EXITED:
            m.mouseExited(e);
            break;
        }
        
        e.consume();
    }
    
    public void processMouseMotionEvent(GMouseEvent e) {
        GMouseMotionListener m = mMouseMotionCaster;
        if(m == null)
            return;
        
        if(e.id() == MouseEvent.MOUSE_MOVED) {
            m.mouseMoved(e);
        }else{
            m.mouseDragged(e);
        }
        
        e.consume();
    }
    
    public void processMouseWheelEvent(GMouseWheelEvent e) {
        GMouseWheelListener m = mMouseWheelCaster;
        if(m == null)
            return;
        
        m.mouseWheelMoved(e);
        e.consume();
    }

    public void processKeyEvent(GKeyEvent e) {
        GKeyListener m = mKeyCaster;
        if(m == null)
            return;
        
        switch(e.id()) {
        case GKeyEvent.KEY_PRESSED:
            m.keyPressed(e);
            break;
        case GKeyEvent.KEY_RELEASED:
            m.keyReleased(e);
            break;
        case GKeyEvent.KEY_TYPED:
            m.keyTyped(e);
            break;
        }
        
        e.consume();
    }
    
    
    
    public void fireLayoutRequest(GComponent source) {
        if(mParent != null) {
            mParent.fireLayoutRequest(source);
        }
    }
    
    public void fireDrawRequest(GComponent source) {
        if(mParent != null) {
            mParent.fireDrawRequest(source);
        }
    }

    public void fireRequestFocus(GComponent source) {
        if(mParent != null) {
            mParent.fireRequestFocus(source);
        }
    }
    
    public void fireTransferFocusBackward(GComponent source) {
        if(mParent != null) {
            mParent.fireTransferFocusBackward(source);
        }
    }
    
    public void fireTransferFocusForward(GComponent source) {
        if(mParent != null) {
            mParent.fireTransferFocusForward(source);
        }
    }
    
    public void firePushInputRoot(GComponent source) {
        if(mParent != null) {
            mParent.firePushInputRoot(source);
        }
    }
    
    public void firePopInputRoot(GComponent source) {
        if(mParent != null) {
            mParent.firePopInputRoot(source);
        }
    }
        
    
    
    private void notifyNewBounds(boolean moved, boolean resized) {
        //TODO: This isn't remotely proper; 
        //I just didn't feel like adding an event queue to GRootPane yet.
        if(mComponentCaster == null)
            return;
        
        if(moved)
            processComponentEvent(new GComponentEvent(this, GComponentEvent.COMPONENT_MOVED));
        
        if(resized)
            processComponentEvent(new GComponentEvent(this, GComponentEvent.COMPONENT_RESIZED));
            
    }
    

}

