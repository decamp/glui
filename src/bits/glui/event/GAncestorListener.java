package bits.glui.event;

import java.util.EventListener;

public interface GAncestorListener extends EventListener {
    public void ancestorChanged( GAncestorEvent e );
    public void ancestorMoved( GAncestorEvent e );
    public void ancestorResized( GAncestorEvent e );
}
