package cogmac.glui;


/**
 * @author decamp
 */
public interface GFocusTraversalPolicy {

    public GComponent getFirstComponent(GComponent root);
    public GComponent getLastComponent(GComponent root);
    public GComponent getDefaultComponent(GComponent root);
    
    public GComponent getComponentAfter(GComponent root, GComponent comp);
    public GComponent getComponentBefore(GComponent root, GComponent comp);
    
}
