package bits.glui.event;

import java.awt.event.KeyEvent;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author decamp
 */
public class GenerateKeyEventFields {
    
    
    public static void main(String[] args) throws Exception {
        generateKeyEventFields();
    }
    
    
    private static void generateKeyEventFields() throws Exception {
        Field[] fields = KeyEvent.class.getDeclaredFields();
        
        final int mods = Modifier.STATIC | Modifier.PUBLIC;
        
        Arrays.sort(fields, new Comparator<Field>() {
            public int compare(Field a, Field b) {
                return a.getName().compareTo(b.getName());
            }
        });
        
        
        for(Field f: fields) {
            if((f.getModifiers() & mods) != mods)
                continue;
            
            
            System.out.print("public static final ");
            System.out.format("%-4s %-30s = KeyEvent.%s;\n", f.getType(), f.getName(), f.getName());
            
        }
        
    }
    
}
