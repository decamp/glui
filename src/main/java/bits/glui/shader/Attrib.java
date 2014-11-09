package bits.glui.shader;

/**
 * @author Philip DeCamp
 */
public class Attrib {

    /**
     * The program interface, eg GL_UNIFORM, GL_PROGRAM_INPUT, GL_PROGRAM_OUTPUT, etc.
     */
    public int mInterface;

    /**
     * Index of resource.
     */
    public int mIndex;

    /**
     * Name of resource.
     */
    public String mName;

    /**
     * Type of element, eg GL_FLOAT, GL_FLOAT_VEC3, GL_FLOAT_MAT4x4, GL_SAMPLER_2D, etc.
     */
    public int mType;

    /**
     * Length of array, or 0 if resource is not an array.
     */
    public int mArray;


    public Attrib() {}



}
