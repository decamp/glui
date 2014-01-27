package bits.glui.util;

public interface Animator {
    public void target( float fps );
    public void start();
    public void stop();
    public boolean isRunning();
}
