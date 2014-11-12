package bits.draw3d.tex;

import bits.draw3d.DrawResource;
import bits.draw3d.DrawUnit;
import bits.glui.GGraphics;

/**
 * Interface for objects that act as textures, FBOs, or RBOs.
 * <p>
 * The Texture interface provides calls for using the
 * object directly as a texture, FBO, or RBO. Additionally,
 * it extends DrawNode and may be used directly within a
 * scene graph if desired. When used as a DrawNode, the
 * primary pushDraw() behavior binds the Texture to the
 * current Framebuffer, and the primary popDraw() behavior
 * reverts the binding to the previous state.
 * 
 * @author decamp
 */
public interface Texture extends DrawUnit {

    /**
     * @return Examples: GL_TEXTURE_2D, GL_TEXTURE_3D, GL_RENDERBUFFER, etc.
     */
    public int target();
    
    /**
     * @return the current ID of this object, or 0 if not initialized.
     */
    public int id();
    
    /**
     * Internal format value used for glTexImage* commands.  
     * 
     * @return Examples: GL_RGBA, GL_DEPTH_COMPONENT16, etc.
     */
    public int internalFormat();
    
    /**
     * @return format of samples in main memory.
     */
    public int format();
    
    /**
     * @return datatype of components in main memory.
     */
    public int dataType();
    
    /**
     * Sets the size of the Texture buffer.  By default,
     * the size is -1,-1, meaning that the Texture does
     * not attempt to allocate storage for itself.  Changing
     * this value causes this Texture to allocate storage on
     * the next pushDraw(), bind(), or init() event.
     *  
     * @param w  Width of buffer, in pixels.
     * @param h  Height of buffer, in pixels.
     */
    public void size( int w, int h );
    
    /**
     * @return currently defined width of underlying buffer, or -1 if undefined.
     */
    public int width();
    
    /**
     * @return currently defined height of underlying buffer, or -1 if undefined. 
     */
    public int height();
    
    /**
     * @return true if this Texture has a defined size; <tt>width() >= 0 && height() >= 0</tt>.
     */
    public boolean hasSize(); 
    
    /**
     * Specifies whether the Texture should automatically allocate storage
     * to match the size of its context.  If enabled, setSize(w,h) will
     * be called on each call to reshape(gld, x, y, w, h).
     * <p>  
     * Default is {@code false}.
     */
    public void resizeOnReshape( boolean resizeOnReshape );

    /**
     * @return true iff resizeOnReshape is enabled.
     * @see #resizeOnReshape
     */
    public boolean resizeOnReshape();
    
    /**
     * (Optional method).  Sets the depth (number of image layers) for this Texture.
     * Only matters for 3D textures.  Calling this method may cause texture to 
     * reallocate storage.
     */
    public void depth( int depth );

    /**
     * @return depth of this Texture.  Unless TEXTURE_3D, probably 1.
     */
    public int depth();
    
    /**
     * @param key  Key of texture param 
     * @return currently specified value of that texture param, or <tt>null</tt> if not defined.
     */
    public Integer param( int key );
    
    /**
     * Sets parameter textures, ala glTexParameter.
     */
    public void param( int key, int value );

    /**
     * Sets the format of the Texture.  Calling this method may cause the
     * texture to reallocate storage if it has a defined size.  
     * 
     * @param internalFormat  Same as "internalFormat" param used in glTexImage* commands.
     * @param format          Same as "format" param used in glTexImage* commands.
     * @param dataType        Same as "dataType" param used in glTexImage* commands.
     */
    public void format( int internalFormat, int format, int dataType );

    /**
     * Initializes the Texture. SHOULD be called automatically as
     * necessary by {@link #bind(GGraphics)}.{@code init()} has the following behavior:<ul>
     * <li>If this Texture has no id, generates an id.</li>
     * <li>If this Texture has a defined size, allocates a buffer using the current format.</li>
     * </ul>
     */
    public void init( GGraphics g );

    /**
     * Disposes this Texture's resources.
     */
    public void dispose( GGraphics g );

    /**
     * Binds this Texture to it's designated target.
     */
    public void bind( GGraphics g );

    /**
     * Binds this Texture's designated target to 0.
     */
    public void unbind( GGraphics g );

    /**
     * Makes specified texture unit active and binds this texture to that unit.
     */
    public void bind( GGraphics g, int unit );

    /**
     * Makes specified texture unit active and unbinds this texture from that unit.
     */
    public void unbind( GGraphics g, int unit );

    /**
     * If autoResizeOnReshape(), produces a call to <tt>size(w, h)</tt>.
     */
    public void reshape( GGraphics g, int x, int y, int w, int h );

}
