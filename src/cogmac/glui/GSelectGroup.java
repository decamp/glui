package cogmac.glui;

import java.util.*;
import cogmac.glui.GSelectable;


/**
 * Similar to javax.swing.ButtonGroup. Enforces a mutually exclusive
 * selection on a group of items. That is, if you have a group of N
 * items and only one can be "selected" at a given time, a GSelectGroup
 * make setSelected calls.  
 * 
 * @author decamp
  */
public class GSelectGroup {
    
    private final List<GSelectable> mItems = new ArrayList<GSelectable>(5);
    private GSelectable mSelected = null;
    
    
    public void add( GSelectable s ) {
        if( s == null ) {
            return;
        }
        
        mItems.add( s );
        
        if( s.isSelected() ) {
            if( mSelected == null ) {
                mSelected = s;
            } else {
                s.setSelected( false );
            }
        }
    }
    
    
    public void remove( GSelectable s ) {
        if( s == null ) {
            return;
        }
        
        mItems.remove( s );
        if( s == mSelected ) {
            mSelected = null;
        }
    }
    
    
    public void setSelected( GSelectable item, boolean select ) {
        if( item == mSelected ) {
            if( item != null && !select ) {
                item.setSelected( false );
                mSelected = null;
            }
        } else if( select ) {
            GSelectable prev = mSelected;
            mSelected = item;
            
            if( prev != null ) {
                prev.setSelected( false );
            }
            
            if( item != null ) {
                item.setSelected( true );
            }
        }
    }
    
    
    public boolean isSelected( Object obj ) {
        return obj == mSelected;
    }
    
    
    public int size() {
        return mItems.size();
    }

    
    public List<GSelectable> items() {
        return new ArrayList<GSelectable>( mItems );
    }
    

}
