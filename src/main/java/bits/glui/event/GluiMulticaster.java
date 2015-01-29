/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui.event;

import bits.draw3d.DrawEnv;

import java.awt.event.*;
import java.util.EventListener;


/**
 * Equivalent to {@link java.awt.AWTEventMulticaster}.
 * Concurrent data structure for event dispatch.
 * Has better runtime performance than AWTEventMulticaster,
 * but worse performance when removing listeners.
 * Does not make guarantees about order of elements.
 *
 * @author decamp
 */
public class GluiMulticaster implements ActionListener,
                                        GPaintListener, 
                                        GFocusListener,
                                        GComponentListener,
                                        GAncestorListener,
                                        GMouseListener, 
                                        GMouseMotionListener, 
                                        GMouseWheelListener,
                                        GKeyListener
{
    
    public static ActionListener add( ActionListener a, ActionListener b ) {
        return (ActionListener)addInternal( a, b );
    }
    
    
    public static ActionListener remove( ActionListener a, ActionListener b ) {
        return (ActionListener)removeInternal( a, b );
    }
    

    public static GPaintListener add( GPaintListener a, GPaintListener b ) {
        return (GPaintListener)addInternal( a, b );
    }
    
    
    public static GPaintListener remove( GPaintListener caster, GPaintListener listener ) {
        return (GPaintListener)removeInternal( caster, listener );
    }
    
    
    public static GComponentListener add( GComponentListener a, GComponentListener b ) {
        return (GComponentListener)addInternal( a, b );
    }
    
    
    public static GComponentListener remove( GComponentListener caster, GComponentListener listener ) {
        return (GComponentListener)removeInternal( caster, listener );
    }
    
    
    public static GAncestorListener add( GAncestorListener a, GAncestorListener b ) {
        return (GAncestorListener)addInternal( a, b );
    }
    
    
    public static GAncestorListener remove( GAncestorListener caster, GAncestorListener listener ) {
        return (GAncestorListener)removeInternal( caster, listener );
    }
    
    
    public static GMouseListener add( GMouseListener a, GMouseListener b ) {
        return (GMouseListener)addInternal( a, b );
    }
    
    
    public static GMouseListener remove( GMouseListener caster, GMouseListener listener ) {
        return (GMouseListener)removeInternal( caster, listener );
    }

    
    public static GMouseMotionListener add( GMouseMotionListener a, GMouseMotionListener b ) {
        return (GMouseMotionListener)addInternal( a, b );
    }
    
    
    public static GMouseMotionListener remove( GMouseMotionListener caster, GMouseMotionListener listener ) {
        return (GMouseMotionListener)removeInternal( caster, listener );
    }

    
    public static GMouseWheelListener add( GMouseWheelListener a, GMouseWheelListener b ) {
        return (GMouseWheelListener)addInternal( a, b );
    }
    
    
    public static GMouseWheelListener remove( GMouseWheelListener caster, GMouseWheelListener listener ) {
        return (GMouseWheelListener)removeInternal( caster, listener );
    }

    
    public static GFocusListener add( GFocusListener a, GFocusListener b ) {
        return (GFocusListener)addInternal( a, b );
    }
    
    
    public static GFocusListener remove(GFocusListener caster, GFocusListener listener) {
        return (GFocusListener)removeInternal(caster, listener);
    }
    
    
    public static GKeyListener add( GKeyListener a, GKeyListener b ) {
        return (GKeyListener)addInternal(a, b);
    }
    
    
    public static GKeyListener remove( GKeyListener caster, GKeyListener listener ) {
        return (GKeyListener)removeInternal( caster, listener );
    }


    
    public void actionPerformed( ActionEvent e ) {
        GluiMulticaster c = this;
        do {
            ((ActionListener)c.mListener).actionPerformed( e );
            c = c.mNext;
        } while( c != null );
    }
    
    
    public void paint( DrawEnv g ) {
        GluiMulticaster c = this;
        do {
            ((GPaintListener)c.mListener).paint( g );
            c = c.mNext;
        } while( c != null );
    }
    
    
    public void componentShown( GComponentEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GComponentListener)c.mListener).componentShown( e );
            c = c.mNext;
        } while( c != null );
    }
    
    
    public void componentHidden( GComponentEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GComponentListener)c.mListener).componentHidden( e );
            c = c.mNext;
        } while( c != null );
    }
    
    
    public void componentMoved( GComponentEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GComponentListener)c.mListener).componentMoved( e );
            c = c.mNext;
        } while( c != null );
    }

    
    public void componentResized(GComponentEvent e) {
        GluiMulticaster c = this;
        do {
            ((GComponentListener)c.mListener).componentResized( e );
            c = c.mNext;
        } while( c != null );
    }

    
    public void ancestorChanged( GAncestorEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GAncestorListener)c.mListener).ancestorChanged( e );
            c = c.mNext;
        } while( c != null );
    }
    
    
    public void ancestorMoved( GAncestorEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GAncestorListener)c.mListener).ancestorMoved( e );
            c = c.mNext;
        } while( c != null );
    }
    
    
    public void ancestorResized( GAncestorEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GAncestorListener)c.mListener).ancestorResized( e );
            c = c.mNext;
        } while( c != null );
    }
    
    
    public void mouseEntered( GMouseEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GMouseListener)c.mListener).mouseEntered( e );
            c = c.mNext;
        } while( c != null );
    }

    
    public void mouseExited( GMouseEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GMouseListener)c.mListener).mouseExited( e );
            c = c.mNext;
        } while( c != null );
    }

    
    public void mousePressed( GMouseEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GMouseListener)c.mListener).mousePressed( e );
            c = c.mNext;
        } while( c != null );
    }

    
    public void mouseReleased( GMouseEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GMouseListener)c.mListener).mouseReleased( e );
            c = c.mNext;
        } while( c != null );
    }

    
    public void mouseClicked( GMouseEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GMouseListener)c.mListener).mouseClicked( e );
            c = c.mNext;
        } while( c != null );
    }

    
    public void mouseMoved( GMouseEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GMouseMotionListener)c.mListener).mouseMoved( e );
            c = c.mNext;
        } while( c != null );
    }

    
    public void mouseDragged( GMouseEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GMouseMotionListener)c.mListener).mouseDragged( e );
            c = c.mNext;
        } while( c != null );
    }

    
    public void mouseWheelMoved( GMouseWheelEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GMouseWheelListener)c.mListener).mouseWheelMoved( e );
            c = c.mNext;
        } while( c != null );
    }

    
    public void focusGained( GFocusEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GFocusListener)c.mListener).focusGained( e );
            c = c.mNext;
        } while( c != null );
    }
    
    
    public void focusLost( GFocusEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GFocusListener)c.mListener).focusLost( e );
            c = c.mNext;
        } while( c != null );
    }

    
    public void keyPressed( GKeyEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GKeyListener)c.mListener).keyPressed( e );
            c = c.mNext;
        } while( c != null );
    }
    
    
    public void keyReleased( GKeyEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GKeyListener)c.mListener).keyReleased( e );
            c = c.mNext;
        } while( c != null );
    }
    
    
    public void keyTyped( GKeyEvent e ) {
        GluiMulticaster c = this;
        do {
            ((GKeyListener)c.mListener).keyTyped( e );
            c = c.mNext;
        } while( c != null );
    }



    private final GluiMulticaster mNext;
    private final EventListener mListener;
    
    
    protected GluiMulticaster( GluiMulticaster next, EventListener listener ) {
        mNext = next;
        mListener = listener;
    }
    
    
    
    protected static EventListener addInternal( EventListener a, EventListener b ) {
        if( a == null ) return b;
        if( b == null ) return a;
        
        if( a instanceof GluiMulticaster ) {
            if( b instanceof GluiMulticaster ) {
                // Reconstruct B to point at A.
                GluiMulticaster head = (GluiMulticaster)a;
                GluiMulticaster tail = (GluiMulticaster)b;
                
                do {
                    head = new GluiMulticaster( head, tail.mListener );
                    tail = tail.mNext;
                } while( tail != null );
                
                return head;
            }
            
            return new GluiMulticaster( (GluiMulticaster)a, b );
        }
        
        if( b instanceof GluiMulticaster ) {
            return new GluiMulticaster( (GluiMulticaster)b, a );
        }
        
        // Both need new GluiMulticaster to wrap them.
        return new GluiMulticaster( new GluiMulticaster( null, b ), a );
    }
    
    
    protected static EventListener removeInternal( EventListener caster, EventListener old ) {
        if( caster == null || old == null ) {
            return caster;
        }
            
        if( caster instanceof GluiMulticaster ) {
            GluiMulticaster head = (GluiMulticaster)caster;
            GluiMulticaster pos  = head;
            
            // Find link to remove.
            while( pos != null && pos.mListener != old ) {
                pos = pos.mNext;
            }
            
            if( pos == null ) {
                // Not found.
                return head;
            }
            
            if( pos == head ) {
                // Remove the head.
                head = head.mNext;
                
                // Check if remaining sequence contains single listener.
                if( head != null && head.mNext == null ) {
                    // Remaining sequence contains single listener,
                    // which may be returned directly.
                    return head.mListener;
                }
                
                return head;
            }
             
            // Reconstruct chain without pos link.
            GluiMulticaster tail = pos.mNext;
            
            if( head.mNext == pos && tail == null ) {
                // head is the only link.
                return head.mListener;
            }
                
            // Reconstruct head--pos subsequence.
            do {
                tail = new GluiMulticaster( tail, head.mListener );
                head = head.mNext;
            } while( head != pos );
            
            return tail;
        } 
        
        if( caster == old ) {
            // Remove single listener.
            return null;
        }
        
        // Not found.
        return caster;
    }

}
