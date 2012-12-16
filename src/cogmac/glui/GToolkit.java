package cogmac.glui;

/**
 * @author decamp
 */
public class GToolkit {
    
    public static boolean isKeyboardFocusable(GComponent comp) {
        return comp.hasKeyboardListener() && comp.isEnabled() && comp.isVisible();
    }
    
    public static boolean isMouseFocusable( GComponent comp ) {
        return comp.hasMouseListener() && comp.isEnabled() && comp.isVisible();
    }
    

}
