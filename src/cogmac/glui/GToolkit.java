package cogmac.glui;

/**
 * @author decamp
 */
public class GToolkit {
    
    public static boolean isFocusable(GComponent comp) {
        return comp.isFocusable() && comp.isEnabled() && comp.isVisible();
    }

}
