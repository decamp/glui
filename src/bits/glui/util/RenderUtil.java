package bits.glui.util;

import java.awt.Color;
import java.awt.image.*;
import java.nio.*;


/**
 * @author decamp
 */
public final class RenderUtil {
    
    
    /**
     * Converts a BufferedImage to a directly allocated java.nio.ByteBuffer.
     * @param image
     * @return
     */
    public static ByteBuffer imageToByteBuffer(BufferedImage image) {
        DataBuffer in = image.getData().getDataBuffer();
        int type = in.getDataType();
        int elSize = DataBuffer.getDataTypeSize(type);
        int count = in.getSize();
        
        ByteBuffer ret = ByteBuffer.allocateDirect((count * elSize + 7) / 8);
        ret.order(ByteOrder.nativeOrder());
        
        switch(type) {
        case DataBuffer.TYPE_BYTE:
        {
            for(int i = 0; i < in.getNumBanks(); i++) {
                ret.put(((DataBufferByte)in).getData(i));
            }
            ret.flip();
            break;
        }
            
        case DataBuffer.TYPE_INT:
        {
            IntBuffer b = ret.asIntBuffer();
            
            for(int i = 0; i < in.getNumBanks(); i++) {
                b.put(((DataBufferInt)in).getData(i));
            }
            
            break;
        }
        
        case DataBuffer.TYPE_FLOAT:
        {
            FloatBuffer b = ret.asFloatBuffer();
            
            for(int i = 0; i < in.getNumBanks(); i++) {
                b.put(((DataBufferFloat)in).getData(i));
            }
            
            break;
        }
            
        case DataBuffer.TYPE_DOUBLE:
        {
            DoubleBuffer b = ret.asDoubleBuffer();
            
            for(int i = 0; i < in.getNumBanks(); i++) {
                b.put(((DataBufferDouble)in).getData(i));
            }
            
            break;
        }
        
        case DataBuffer.TYPE_SHORT:
        {
            ShortBuffer b = ret.asShortBuffer();
            
            for(int i = 0; i < in.getNumBanks(); i++) {
                b.put(((DataBufferShort)in).getData(i));
            }
            
            break;
        }
        
        case DataBuffer.TYPE_USHORT:
        {
            ShortBuffer b = ret.asShortBuffer();
            
            for(int i = 0; i < in.getNumBanks(); i++) {
                b.put(((DataBufferUShort)in).getData(i));
            }
            
            break;
        }
        
        default:
            throw new IllegalArgumentException("Unknown data buffer type: " + type);
        }
        
        return ret;
    }

    
    
    
    /**
     * @deprecated Use cogmac.math3d.Pots
     */
    public static int higherPowerOfTwo(int val) {
        if(val <= 0)
            return 1;
        
        val = (val >>  1) | val;
        val = (val >>  2) | val;
        val = (val >>  4) | val;
        val = (val >>  8) | val;
        val = (val >> 16) | val;
        
        return val + 1;
    }

    /**
     * @deprecated Use cogmac.math3d.Pots 
     */
    public static int ceilPowerOfTwo(int val) {
        if(val <= 0)
            return 1;
        
        return higherPowerOfTwo(val - 1);
    }

    /**
     * @deprecated Use cogmac.math3d.Pots
     */
    public static int lowerPowerOfTwo(int val) {
        if(val <= 1)
            return 1;
        
        return higherPowerOfTwo(val - 1) >> 1;
    }

    /**
     * @deprecated Use cogmac.math3d.Pots 
     */
    public static int floorPowerOfTwo(int val) {
        if(val <= 1)
            return 1;
        
        return higherPowerOfTwo(val) >> 1;
    }
    
    /**
     * @deprecated Use something else.  Nothing in Glui uses this method (GColor now being used), 
     * and Draw3d has a designated class for dumb color conversions. 
     */
    public static float[] colorToArray(Color c) {
        return new float[]{ c.getRed() / 255f, 
                            c.getGreen() / 255f, 
                            c.getBlue() / 255f, 
                            c.getAlpha() / 255f};   
    }

    
    private RenderUtil() {}
    
}
