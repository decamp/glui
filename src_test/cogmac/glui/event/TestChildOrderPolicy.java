package cogmac.glui.event;

import static org.junit.Assert.*;

import org.junit.Test;
import cogmac.glui.*;


/**
 * @author decamp
 */
public class TestChildOrderPolicy {

    @Test
    public void testOrder() {
        Panel p = constructTree(4, 3, new int[]{0});
        
        GChildOrderTraversalPolicy pol = new GChildOrderTraversalPolicy();
        
        assertEquals(0, ((Panel)pol.getFirstComponent(p)).mIndex);
        assertEquals(39, ((Panel)pol.getLastComponent(p)).mIndex);
        
        Panel pp = p;
        
        for(int i = 0; i < 100; i++) {
            assertEquals(i % 40, pp.mIndex);
            pp = (Panel)pol.getComponentAfter(p, pp);
        }
        
        pp = p;
        
        for(int i = 0; i < 50; i++) {
            pp = (Panel)pol.getComponentBefore(p, pp);
            assertEquals(39 - i % 40, pp.mIndex);
        }
        
    }
    
    
    
    
    private Panel constructTree(int depth, int branches, int[] index) {
        if(depth == 0)
            return null;
        
        Panel ret = new Panel(index[0]++);
        ret.setFocusable(true);
        
        if(depth == 1)
            return ret;
        
        for(int i = 0; i < branches; i++) {
            ret.addChild(constructTree(depth - 1, branches, index));
        }
        
        return ret;
    }
    
    

    private static class Panel extends GPanel {
        
        final int mIndex;
        
        
        public Panel(int index) {
            mIndex = index;
        }
        
        
        public String toString() {
            return "Panel: " + mIndex;
        }
        
    }
    
    
}
