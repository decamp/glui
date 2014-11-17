/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui;


/**
 * @author decamp
 */
public interface GFocusTraversalPolicy {
    public GComponent getFirstComponent( GComponent root );
    public GComponent getLastComponent( GComponent root );
    public GComponent getDefaultComponent( GComponent root );

    public GComponent getComponentAfter( GComponent root, GComponent comp );
    public GComponent getComponentBefore( GComponent root, GComponent comp );
}
