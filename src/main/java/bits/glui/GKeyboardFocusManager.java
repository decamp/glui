package bits.glui;

import bits.glui.event.GFocusEvent;
import bits.glui.event.GKeyEvent;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;


/**
 * @author Philip DeCamp
 */
public class GKeyboardFocusManager implements GKeyEventDispatcher {
    private final GFocusTraversalPolicy mPolicy;
    private       Component             mAwtRoot;
    private       GComponent            mRoot;
    private       GComponent            mFocus;

    // TODO: Need concurrent data structure that preservers order.
    private final List<GKeyEventDispatcher>    mDispatchers    = new ArrayList<GKeyEventDispatcher>();
    private final List<GKeyEventPostProcessor> mPostProcessors = new ArrayList<GKeyEventPostProcessor>();


    public GKeyboardFocusManager( GComponent root, Component optAwtOwner ) {
        mPolicy = new GChildOrderTraversalPolicy();
        mRoot = root;
        mAwtRoot = optAwtOwner;
    }


    @Override
    public boolean dispatchKeyEvent( GKeyEvent e ) {
        if( !mDispatchers.isEmpty() ) {
            for( GKeyEventDispatcher k: mDispatchers ) {
                if( k.dispatchKeyEvent( e ) ) {
                    return true;
                }
            }
        }

        GComponent focus = focusOwner();
        if( focus != null ) {
            focus.processKeyEvent( e );
            if( e.isConsumed() ) {
                return true;
            }
        }

        if( !mPostProcessors.isEmpty() ) {
            for( GKeyEventPostProcessor p: mPostProcessors ) {
                if( p.postProcessKeyEvent( e ) ) {
                    return true;
                }
            }
        }

        return e.isConsumed();
    }


    public GComponent focusOwner() {
        return mFocus;
    }


    public void transferFocus( GComponent source ) {
        if( mAwtRoot != null && !mAwtRoot.hasFocus() ) {
            mAwtRoot.requestFocus();
        }

        GComponent focus = mFocus;
        if( focus == source ) {
            return;
        }

        if( source == null || GToolkit.isChild( mRoot, source ) ) {
            updateFocus( source, source, false );
        }
    }


    public void transferFocusBackward( GComponent source ) {
        GComponent focus = mFocus;
        GComponent root  = mRoot;

        if( focus != source ) {
            return;
        }

        GComponent target = mPolicy.getComponentBefore( root, focus );
        if( target != focus ) {
            updateFocus( source, target, false );
        }
    }


    public void transferFocusForward( GComponent source ) {
        GComponent focus = mFocus;
        GComponent root  = mRoot;

        if( focus != source ) {
            return;
        }

        GComponent target = mPolicy.getComponentAfter( root, focus );
        if( target != focus ) {
            updateFocus( source, target, false );
        }
    }


    public void validate( GComponent optSource ) {
        GComponent root  = mRoot;
        GComponent focus = mFocus;

        if( focus == null || !GToolkit.isKeyboardFocusable( focus ) || !GToolkit.isChild( root, focus ) ) {
            GComponent target = mRoot;
            target = mPolicy.getDefaultComponent( target );
            updateFocus( optSource, target, false );
        }
    }


    public GComponent root() {
        return mRoot;
    }


    public void setRoot( GComponent root, GComponent initFocus ) {
        mRoot = root;
        validate( mRoot );
    }


    public void setAwtOwner( Component root ) {
        mAwtRoot = root;
    }


    public void addKeyEventDispatcher( GKeyEventDispatcher dispatcher ) {
        mDispatchers.add( dispatcher );
    }


    public void removeKeyEventDispatcher( GKeyEventDispatcher dispatcher ) {
        mDispatchers.remove( dispatcher );
    }


    public void addKeyEventPostProcessor( GKeyEventPostProcessor proc ) {
        mPostProcessors.add( proc );
    }


    public void removeKeyEventPostProcessor( GKeyEventPostProcessor proc ) {
        mPostProcessors.remove( proc );
    }


    private void updateFocus( GComponent optSource, GComponent target, boolean temporary ) {
        GComponent prev = mFocus;
        if( prev == target ) {
            return;
        }

        if( optSource == null ) {
            optSource = mRoot;
        }

        mFocus = target;

        if( prev != null ) {
            prev.processFocusEvent( new GFocusEvent( optSource, GFocusEvent.FOCUS_LOST, temporary, target ) );
        }

        if( target != null ) {
            target.processFocusEvent( new GFocusEvent( optSource, GFocusEvent.FOCUS_GAINED, temporary, target ) );
        }
    }

}
