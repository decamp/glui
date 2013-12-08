package bits.glui.util;

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
    
    
    private RenderUtil() {}
    
}