package cogmac.glui;

/**
 * @author decamp
 */
public class GToolkit {
    
    public static boolean isKeyboardFocusable(GComponent comp) {
        return comp.hasKeyListener() && comp.isEnabled() && comp.isDisplayed();
    }
    
    public static boolean isMouseFocusable( GComponent comp ) {
        return comp.hasMouseListener() && comp.isEnabled() && comp.isDisplayed();
    }
    

}
