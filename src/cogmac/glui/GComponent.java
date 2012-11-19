package cogmac.glui;

import java.awt.Font;
import java.util.*;
import cogmac.glui.event.*;


/**
 * @author decamp
 */
public interface GComponent {
    
    public void addChild(GComponent pane);
    public void removeChild(GComponent pane);
    public void clearChildren();
    public List<GComponent> getChildren();
    public void setLayout(GLayout layout);
    public GComponent getParent();
    
    public Box bounds();
    public Box absoluteBounds();
    public int x();
    public int y();
    public int width();
    public int height();
    public boolean contains(int x, int y);
    public GComponent position(int x, int y);
    public GComponent bounds(int x, int y, int w, int h);
    public GComponent size(int w, int y);
    public void setVisible(boolean visible);
    public boolean isVisible();
    public void setEnabled(boolean enable);
    public boolean isEnabled();
    
    public GComponent foreground(GColor color);
    public GColor foreground();
    public GComponent background(GColor color);
    public GColor background();
    public GComponent font(Font font);
    public Font font();
    
    public void setFocusable(boolean focusable);
    public boolean isFocusable();
    public void setRequestFocusEnabled(boolean requestFocus);
    public boolean isRequestFocusEnabled();
    public boolean requestFocus();
    public void transferFocusBackward();
    public void transferFocusForward();
    
    public void startModal();
    public void stopModal();
    
    public void addComponentListener(GComponentListener listener);
    public void removeComponentListener(GComponentListener listener);
    public void addFocusListener(GFocusListener listener);
    public void removeFocusListener(GFocusListener listener);
    public void addMouseListener(GMouseListener listener);
    public void removeMouseListener(GMouseListener listener);
    public void addMouseMotionListener(GMouseMotionListener listener);
    public void removeMouseMotionListener(GMouseMotionListener listener);
    public void addMouseWheelListener(GMouseWheelListener listener);
    public void removeMouseWheelListener(GMouseWheelListener listener);
    public void addKeyListener(GKeyListener listener);
    public void removeKeyListener(GKeyListener listener);
    public void addPaintListener(GPaintListener listener);
    public void removePaintListener(GPaintListener listener);
    
    public GComponent componentAt(int x, int y);
    
    public void updateLayout();
    public boolean needsLayout();
    public void repaint();
    public boolean needsRepaint();
    
    /**
     * Methods that should only be called by the parent pane.
     */
    public void setParent(GComponent pane);
    public void processLayoutEvent();
    public void processPaintEvent(GGraphics g);
    public void processComponentEvent(GComponentEvent e);
    public void processFocusEvent(GFocusEvent e);
    public void processMouseEvent(GMouseEvent e);
    public void processMouseMotionEvent(GMouseEvent e);
    public void processMouseWheelEvent(GMouseWheelEvent e);
    public void processKeyEvent(GKeyEvent e);
    
    
    /**
     * Methods that should only be called by a child pane.
     */
    public void fireLayoutRequest(GComponent source);
    public void fireDrawRequest(GComponent source);
    public void fireRequestFocus(GComponent source);
    public void fireTransferFocusBackward(GComponent source);
    public void fireTransferFocusForward(GComponent source);
    public void firePushInputRoot(GComponent source);
    public void firePopInputRoot(GComponent source);
    
}
