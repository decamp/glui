package bits.draw3d;


/**
 * @author Philip DeCamp
 */
public interface DrawUnit extends DrawResource {
    public void bind( DrawEnv g );
    public void unbind( DrawEnv g );
}
