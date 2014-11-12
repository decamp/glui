package bits.draw3d.shader;

/**
 * @author Philip DeCamp
 */
public class ProgramResource {

    /**
     * The program interface, eg GL_UNIFORM, GL_PROGRAM_INPUT, GL_PROGRAM_OUTPUT, etc.
     */
    public final int mInterface;

    /**
     * Index of resource.
     *
     * For uniforms, index is is used for queries, while location is used for writes.
     * For attributes, index and location should be the same.
     */
    public final int mIndex;

    /**
     * Location of resource.
     *
     * For uniforms, index is is used for queries, while location is used for writes.
     * For attributes, index and location should be the same.
     */
    public final int mLocation;

    /**
     * Name of resource.
     */
    public final String mName;

    /**
     * Type of element, eg GL_FLOAT, GL_FLOAT_VEC3, GL_FLOAT_MAT4x4, GL_SAMPLER_2D, etc.
     */
    public final int mType;

    /**
     * Length of array, or 0 if resource is not an array.
     */
    public final int mArrayLen;



    public ProgramResource( int inter, int index, int location, String name, int type, int arrayLen ) {
        mInterface = inter;
        mIndex     = index;
        mLocation  = location;
        mName      = name;
        mType      = type;
        mArrayLen  = arrayLen;
    }


}
