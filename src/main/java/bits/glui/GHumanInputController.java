/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.glui;


/**
 * Interface for feeding human input into windowing system.
 * Methods return true iff event was accepted and "consumed".
 *
 * @author Philip DeCamp
 */
public interface GHumanInputController {
    public boolean keyPressed( int keyCode, char keyChar, int keyLoc );
    public boolean keyPressed( long micros, int mods, int keyCode, char keyChar, int keyLoc );
    public boolean keyReleased( int keyCode, char keyChar, int keyLoc );
    public boolean keyReleased( long micros, int mods, int keyCode, char keyChar, int keyLoc );
    public boolean keyTyped( int keyCode, char keyChar, int keyLoc );
    public boolean keyTyped( long micros, int mods, int keyCode, char keyChar, int keyLoc );

    public boolean mousePressed( int button );
    public boolean mousePressed( long micros, int mods, int button, boolean triggerPopup );
    public boolean mouseReleased( int button, boolean genClick );
    public boolean mouseReleased( long micros, int mods, int button, boolean triggerPopup, boolean genClick );

    public boolean mouseEntered( int x, int y );
    public boolean mouseEntered( long micros, int mods, int x, int y );
    public boolean mouseExited();
    public boolean mouseExited( long micros, int mods );
    public boolean mouseMoved( int x, int y );
    public boolean mouseMoved( long micros, int mods, int x, int y );
    public boolean mouseWheelMoved( int scrollType, int scrollAmount, int wheelRotation );
    public boolean mouseWheelMoved( long micros, int mods, int scrollType, int scrollAmount, int wheelRotation );
}
