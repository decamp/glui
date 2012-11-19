package cogmac.glui.event;

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

import cogmac.glui.GGraphics;


/**
 * @author decamp
 */
public class GluiMulticaster extends AWTEventMulticaster implements GPaintListener, 
                                                                    GFocusListener,
                                                                    GComponentListener, 
                                                                    GMouseListener, 
                                                                    GMouseMotionListener, 
                                                                    GMouseWheelListener,
                                                                    GKeyListener
{

    public static GPaintListener add(GPaintListener a, GPaintListener b) {
        return (GPaintListener)addInternalGlui(a, b);
    }
    
    
    public static GPaintListener remove(GPaintListener caster, GPaintListener listener) {
        return (GPaintListener)removeInternal(caster, listener);
    }
    
    
    public static GComponentListener add(GComponentListener a, GComponentListener b) {
        return (GComponentListener)addInternalGlui(a, b);
    }
    
    
    public static GComponentListener remove(GComponentListener caster, GComponentListener listener) {
        return (GComponentListener)removeInternal(caster, listener);
    }

    
    public static GMouseListener add(GMouseListener a, GMouseListener b) {
        return (GMouseListener)addInternalGlui(a, b);
    }
    
    
    public static GMouseListener remove(GMouseListener caster, GMouseListener listener) {
        return (GMouseListener)removeInternal(caster, listener);
    }

    
    public static GMouseMotionListener add(GMouseMotionListener a, GMouseMotionListener b) {
        return (GMouseMotionListener)addInternalGlui(a, b);
    }
    
    
    public static GMouseMotionListener remove(GMouseMotionListener caster, GMouseMotionListener listener) {
        return (GMouseMotionListener)removeInternal(caster, listener);
    }

    
    public static GMouseWheelListener add(GMouseWheelListener a, GMouseWheelListener b) {
        return (GMouseWheelListener)addInternalGlui(a, b);
    }
    
    
    public static GMouseWheelListener remove(GMouseWheelListener caster, GMouseWheelListener listener) {
        return (GMouseWheelListener)removeInternal(caster, listener);
    }

    
    public static GFocusListener add(GFocusListener a, GFocusListener b) {
        return (GFocusListener)addInternalGlui(a, b);
    }
    
    
    public static GFocusListener remove(GFocusListener caster, GFocusListener listener) {
        return (GFocusListener)removeInternal(caster, listener);
    }
    
    
    public static GKeyListener add(GKeyListener a, GKeyListener b) {
        return (GKeyListener)addInternalGlui(a, b);
    }
    
    
    public static GKeyListener remove(GKeyListener caster, GKeyListener listener) {
        return (GKeyListener)removeInternal(caster, listener);
    }


    
    
    public void paint(GGraphics g) {
        ((GPaintListener)a).paint(g);
        ((GPaintListener)b).paint(g);
    }
    
    
    
    
    public void componentShown(GComponentEvent e) {
        ((GComponentListener)a).componentShown(e);
        ((GComponentListener)b).componentShown(e);
    }
    
    
    public void componentHidden(GComponentEvent e) {
        ((GComponentListener)a).componentHidden(e);
        ((GComponentListener)b).componentHidden(e);
    }
    
    
    public void componentMoved(GComponentEvent e) {
        ((GComponentListener)a).componentMoved(e);
        ((GComponentListener)b).componentMoved(e);
    }

    
    public void componentResized(GComponentEvent e) {
        ((GComponentListener)a).componentResized(e);
        ((GComponentListener)b).componentResized(e);
    }

    
    public void mouseEntered(GMouseEvent e) {
        ((GMouseListener)a).mouseEntered(e);
        ((GMouseListener)b).mouseEntered(e);
    }

    
    public void mouseExited(GMouseEvent e) {
        ((GMouseListener)a).mouseExited(e);
        ((GMouseListener)b).mouseExited(e);
    }

    
    public void mousePressed(GMouseEvent e) {
        ((GMouseListener)a).mousePressed(e);
        ((GMouseListener)b).mousePressed(e);
    }

    
    public void mouseReleased(GMouseEvent e) {
        ((GMouseListener)a).mouseReleased(e);
        ((GMouseListener)b).mouseReleased(e);
    }

    
    public void mouseClicked(GMouseEvent e) {
        ((GMouseListener)a).mouseClicked(e);
        ((GMouseListener)b).mouseClicked(e);
    }

    
    public void mouseMoved(GMouseEvent e) {
        ((GMouseMotionListener)a).mouseMoved(e);
        ((GMouseMotionListener)b).mouseMoved(e);
    }

    
    public void mouseDragged(GMouseEvent e) {
        ((GMouseMotionListener)a).mouseDragged(e);
        ((GMouseMotionListener)b).mouseDragged(e);
    }

    
    public void mouseWheelMoved(GMouseWheelEvent e) {
        ((GMouseWheelListener)a).mouseWheelMoved(e);
        ((GMouseWheelListener)b).mouseWheelMoved(e);
    }

    
    public void focusGained(GFocusEvent e) {
        ((GFocusListener)a).focusGained(e);
        ((GFocusListener)b).focusGained(e);
    }
    
    
    public void focusLost(GFocusEvent e) {
        ((GFocusListener)a).focusLost(e);
        ((GFocusListener)b).focusLost(e);
    }

    
    public void keyPressed(GKeyEvent e) {
        ((GKeyListener)a).keyPressed(e);
        ((GKeyListener)b).keyPressed(e);
    }
    
    
    public void keyReleased(GKeyEvent e) {
        ((GKeyListener)a).keyReleased(e);
        ((GKeyListener)b).keyReleased(e);
    }
    
    
    public void keyTyped(GKeyEvent e) {
        ((GKeyListener)a).keyTyped(e);
        ((GKeyListener)b).keyTyped(e);
    }
    
    
    
    protected GluiMulticaster(EventListener a, EventListener b) {
        super(a, b);
    }


    protected static EventListener addInternalGlui(EventListener a, EventListener b) {
        if (a == null)  return b;
        if (b == null)  return a;
        return new GluiMulticaster(a, b);
    }


}
