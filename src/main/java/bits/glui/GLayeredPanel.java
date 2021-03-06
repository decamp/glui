/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui;

import java.util.*;


/**
 * Like GPanel, but provides the ability to organize children
 * into layers. Child components are ordered first by layer order,
 * and second by insertion order. By default, children are added to
 * layer 0. Layer 1 is "above" of layer 0. Layer -1 is "below" layer 0.
 * 
 * @author Philip DeCamp
 */
public class GLayeredPanel extends GPanel {
    
    private final List<GComponent> mChildren;
    private final List<Integer> mLayers;
    
    
    public GLayeredPanel() {
        this( new ArrayList<GComponent>() );
    }
    
    private GLayeredPanel( List<GComponent> children ) {
        super( children );
        mChildren = children;
        mLayers = new ArrayList<Integer>();
    }
    
    
    @Override
    public void addChild( GComponent child ) {
        addChild( 0, child );
    }


    public synchronized void addChild( int layer, GComponent child ) {
        if( mChildren.contains( child ) )
            return;
        
        int idx = 0;
    
        for( ; idx < mLayers.size(); idx++ ) {
            if( mLayers.get( idx ).intValue() > layer ) {
                break;
            }
        }
        
        mChildren.add( idx, child );
        mLayers.add( idx, layer );
        childAdded( child );
    }
    
    @Override
    public synchronized void removeChild( GComponent child ) {
        int idx = mChildren.indexOf( child );
        if( idx < 0 ) 
            return;
        
        child = mChildren.remove( idx );
        mLayers.remove( idx );
        childRemoved( child );
    }
    
    @Override
    public synchronized void clearChildren() {
        if( mChildren.isEmpty() )
            return;
        
        for( GComponent p : mChildren ) {
            childRemoved( p );
        }
        
        mChildren.clear();
        mLayers.clear();
    }

    @Override
    public synchronized GComponent mouseFocusableComponentAt( int x, int y ) {
        if( !hasMouseFocusableComponent() || !contains( x, y ) ) {
            return null;
        }

        int size = mChildren.size();
        Integer topLayer = null;

        while( size-- > 0 ) {
            GComponent child = mChildren.get( size );
            Integer    layer = mLayers.get( size );

            if( topLayer == null ) {
                if( child.hasMouseFocusableComponent() ) {
                    topLayer = layer;
                } else {
                    continue;
                }
            } else if( layer.intValue() != topLayer.intValue() ) {
                break;
            }

            GComponent ret = child.mouseFocusableComponentAt( x - child.x(), y - child.y() );
            if( ret != null ) {
                return ret;
            }
        }

        return GToolkit.isMouseFocusable( this ) ? this : null;
    }

}
