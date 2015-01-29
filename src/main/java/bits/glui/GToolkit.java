/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui;

/**
 * @author decamp
 */
public class GToolkit {

    public static boolean isKeyboardFocusable( GComponent comp ) {
        return comp.hasKeyListener() && comp.isEnabled() && comp.isDisplayed();
    }

    public static boolean isMouseFocusable( GComponent comp ) {
        return comp.hasMouseListener() && comp.isEnabled() && comp.isDisplayed();
    }

    public static boolean isChild( GComponent parent, GComponent comp ) {
        if( parent == null ) {
            return false;
        }
        while( comp != null ) {
            if( comp == parent ) {
                return true;
            }
            comp = comp.parent();
        }
        return false;
    }

}
