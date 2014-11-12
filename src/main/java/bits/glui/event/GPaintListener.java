package bits.glui.event;

import java.util.EventListener;
import bits.draw3d.DrawEnv;


/**
 * @author decamp
 */
public interface GPaintListener extends EventListener {
    public void paint( DrawEnv g );
}
