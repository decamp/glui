package cogmac.glui;

public interface EventDispatcher {
    
    public void repaint( GComponent source );
    
    public void applyLayout( GComponent source );
    
    public void requestFocus( GComponent source );
    
    public void transferFocusBackward( GComponent source );
    
    public void transferFocusForward( GComponent source );
    
    public void pushInputRoot( GComponent source );
    
    public void popInputRoot( GComponent source );
    
    public void propertyChanged( GComponent source, String prop, Object oldValue, Object newValue );
    
}
