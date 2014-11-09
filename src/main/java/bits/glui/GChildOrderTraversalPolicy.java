package bits.glui;

import java.util.*;


/**
 * @author decamp
 */
public class GChildOrderTraversalPolicy implements GFocusTraversalPolicy {


    public GComponent getFirstComponent( GComponent root ) {
        if( GToolkit.isKeyboardFocusable( root ) )
            return root;

        for( GComponent c : root.children() ) {
            GComponent ret = getFirstComponent( c );
            if( ret != null ) {
                return ret;
            }
        }

        return null;
    }


    public GComponent getLastComponent( GComponent root ) {
        List<GComponent> children = root.children();

        for( int i = children.size() - 1; i >= 0; i-- ) {
            GComponent ret = getLastComponent( children.get( i ) );
            if( ret != null ) {
                return ret;
            }
        }

        return GToolkit.isKeyboardFocusable( root ) ? root : null;
    }


    public GComponent getDefaultComponent( GComponent root ) {
        return null;
    }


    public GComponent getComponentAfter( GComponent root, GComponent comp ) {

        // Check children.
        for( GComponent c : comp.children() ) {
            GComponent ret = getFirstComponent( c );
            if( ret != null ) {
                return ret;
            }
        }

        // Check if possible to get parent.
        if( comp == root )
            return GToolkit.isKeyboardFocusable( root ) ? root : null;

        GComponent parent = comp.parent();
        if( parent == null )
            return null;

        GComponent ret = getComponentAfter( root, parent, comp );
        if( ret != null )
            return ret;

        // Nothing in the remaining components. Sweep through from the
        // beginning.
        return getFirstComponent( root );
    }


    public GComponent getComponentBefore( GComponent root, GComponent comp ) {

        // Check if possible to get parent.
        if( comp != root ) {
            GComponent parent = comp.parent();
            if( parent == null )
                return null;

            GComponent ret = getComponentBefore( root, parent, comp );
            if( ret != null )
                return ret;
        }

        // Nope. Sweep through from the end.
        return getLastComponent( root );
    }



    private GComponent getComponentAfter( GComponent root, GComponent comp, GComponent start ) {
        while( true ) {
            // Check children.
            Iterator<GComponent> iter = comp.children().iterator();

            // Find starting position.
            if( start != null ) {
                while( iter.hasNext() ) {
                    if( iter.next() == start ) {
                        break;
                    }
                }
            }

            // Check remaining children.
            while( iter.hasNext() ) {
                GComponent ret = getFirstComponent( iter.next() );
                if( ret != null ) {
                    return ret;
                }
            }

            // Nope. Move to parent.
            if( comp == root )
                return null;

            GComponent parent = comp.parent();
            if( parent == null )
                return null;

            start = comp;
            comp = parent;
        }
    }


    private GComponent getComponentBefore( GComponent root, GComponent comp, GComponent start ) {
        while( true ) {
            // Check children.
            List<GComponent> children = comp.children();
            int i = children.size();

            // Find starting position.
            if( start != null ) {
                while( --i >= 0 ) {
                    if( children.get( i ) == start ) {
                        break;
                    }
                }
            }

            // Check remaining children.
            while( --i >= 0 ) {
                GComponent ret = getLastComponent( children.get( i ) );
                if( ret != null ) {
                    return ret;
                }
            }

            // Nope. Check node itself.
            if( GToolkit.isKeyboardFocusable( comp ) )
                return comp;

            // Nope. Move to parent.
            if( comp == root )
                return null;

            GComponent parent = comp.parent();
            if( parent == null )
                return null;

            start = comp;
            comp = parent;
        }
    }


}
