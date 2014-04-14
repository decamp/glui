package bits.glui.event;

import java.awt.event.KeyEvent;

import bits.glui.GComponent;


/**
 * @author decamp
 */
public class GKeyEvent extends GInputEvent {
    
    public static final char CHAR_UNDEFINED                 = KeyEvent.CHAR_UNDEFINED;
    public static final int  KEY_FIRST                      = KeyEvent.KEY_FIRST;
    public static final int  KEY_LAST                       = KeyEvent.KEY_LAST;
    public static final int  KEY_LOCATION_LEFT              = KeyEvent.KEY_LOCATION_LEFT;
    public static final int  KEY_LOCATION_NUMPAD            = KeyEvent.KEY_LOCATION_NUMPAD;
    public static final int  KEY_LOCATION_RIGHT             = KeyEvent.KEY_LOCATION_RIGHT;
    public static final int  KEY_LOCATION_STANDARD          = KeyEvent.KEY_LOCATION_STANDARD;
    public static final int  KEY_LOCATION_UNKNOWN           = KeyEvent.KEY_LOCATION_UNKNOWN;
    public static final int  KEY_PRESSED                    = KeyEvent.KEY_PRESSED;
    public static final int  KEY_RELEASED                   = KeyEvent.KEY_RELEASED;
    public static final int  KEY_TYPED                      = KeyEvent.KEY_TYPED;
    public static final int  VK_0                           = KeyEvent.VK_0;
    public static final int  VK_1                           = KeyEvent.VK_1;
    public static final int  VK_2                           = KeyEvent.VK_2;
    public static final int  VK_3                           = KeyEvent.VK_3;
    public static final int  VK_4                           = KeyEvent.VK_4;
    public static final int  VK_5                           = KeyEvent.VK_5;
    public static final int  VK_6                           = KeyEvent.VK_6;
    public static final int  VK_7                           = KeyEvent.VK_7;
    public static final int  VK_8                           = KeyEvent.VK_8;
    public static final int  VK_9                           = KeyEvent.VK_9;
    public static final int  VK_A                           = KeyEvent.VK_A;
    public static final int  VK_ACCEPT                      = KeyEvent.VK_ACCEPT;
    public static final int  VK_ADD                         = KeyEvent.VK_ADD;
    public static final int  VK_AGAIN                       = KeyEvent.VK_AGAIN;
    public static final int  VK_ALL_CANDIDATES              = KeyEvent.VK_ALL_CANDIDATES;
    public static final int  VK_ALPHANUMERIC                = KeyEvent.VK_ALPHANUMERIC;
    public static final int  VK_ALT                         = KeyEvent.VK_ALT;
    public static final int  VK_ALT_GRAPH                   = KeyEvent.VK_ALT_GRAPH;
    public static final int  VK_AMPERSAND                   = KeyEvent.VK_AMPERSAND;
    public static final int  VK_ASTERISK                    = KeyEvent.VK_ASTERISK;
    public static final int  VK_AT                          = KeyEvent.VK_AT;
    public static final int  VK_B                           = KeyEvent.VK_B;
    public static final int  VK_BACK_QUOTE                  = KeyEvent.VK_BACK_QUOTE;
    public static final int  VK_BACK_SLASH                  = KeyEvent.VK_BACK_SLASH;
    public static final int  VK_BACK_SPACE                  = KeyEvent.VK_BACK_SPACE;
    public static final int  VK_BEGIN                       = KeyEvent.VK_BEGIN;
    public static final int  VK_BRACELEFT                   = KeyEvent.VK_BRACELEFT;
    public static final int  VK_BRACERIGHT                  = KeyEvent.VK_BRACERIGHT;
    public static final int  VK_C                           = KeyEvent.VK_C;
    public static final int  VK_CANCEL                      = KeyEvent.VK_CANCEL;
    public static final int  VK_CAPS_LOCK                   = KeyEvent.VK_CAPS_LOCK;
    public static final int  VK_CIRCUMFLEX                  = KeyEvent.VK_CIRCUMFLEX;
    public static final int  VK_CLEAR                       = KeyEvent.VK_CLEAR;
    public static final int  VK_CLOSE_BRACKET               = KeyEvent.VK_CLOSE_BRACKET;
    public static final int  VK_CODE_INPUT                  = KeyEvent.VK_CODE_INPUT;
    public static final int  VK_COLON                       = KeyEvent.VK_COLON;
    public static final int  VK_COMMA                       = KeyEvent.VK_COMMA;
    public static final int  VK_COMPOSE                     = KeyEvent.VK_COMPOSE;
    public static final int  VK_CONTEXT_MENU                = KeyEvent.VK_CONTEXT_MENU;
    public static final int  VK_CONTROL                     = KeyEvent.VK_CONTROL;
    public static final int  VK_CONVERT                     = KeyEvent.VK_CONVERT;
    public static final int  VK_COPY                        = KeyEvent.VK_COPY;
    public static final int  VK_CUT                         = KeyEvent.VK_CUT;
    public static final int  VK_D                           = KeyEvent.VK_D;
    public static final int  VK_DEAD_ABOVEDOT               = KeyEvent.VK_DEAD_ABOVEDOT;
    public static final int  VK_DEAD_ABOVERING              = KeyEvent.VK_DEAD_ABOVERING;
    public static final int  VK_DEAD_ACUTE                  = KeyEvent.VK_DEAD_ACUTE;
    public static final int  VK_DEAD_BREVE                  = KeyEvent.VK_DEAD_BREVE;
    public static final int  VK_DEAD_CARON                  = KeyEvent.VK_DEAD_CARON;
    public static final int  VK_DEAD_CEDILLA                = KeyEvent.VK_DEAD_CEDILLA;
    public static final int  VK_DEAD_CIRCUMFLEX             = KeyEvent.VK_DEAD_CIRCUMFLEX;
    public static final int  VK_DEAD_DIAERESIS              = KeyEvent.VK_DEAD_DIAERESIS;
    public static final int  VK_DEAD_DOUBLEACUTE            = KeyEvent.VK_DEAD_DOUBLEACUTE;
    public static final int  VK_DEAD_GRAVE                  = KeyEvent.VK_DEAD_GRAVE;
    public static final int  VK_DEAD_IOTA                   = KeyEvent.VK_DEAD_IOTA;
    public static final int  VK_DEAD_MACRON                 = KeyEvent.VK_DEAD_MACRON;
    public static final int  VK_DEAD_OGONEK                 = KeyEvent.VK_DEAD_OGONEK;
    public static final int  VK_DEAD_SEMIVOICED_SOUND       = KeyEvent.VK_DEAD_SEMIVOICED_SOUND;
    public static final int  VK_DEAD_TILDE                  = KeyEvent.VK_DEAD_TILDE;
    public static final int  VK_DEAD_VOICED_SOUND           = KeyEvent.VK_DEAD_VOICED_SOUND;
    public static final int  VK_DECIMAL                     = KeyEvent.VK_DECIMAL;
    public static final int  VK_DELETE                      = KeyEvent.VK_DELETE;
    public static final int  VK_DIVIDE                      = KeyEvent.VK_DIVIDE;
    public static final int  VK_DOLLAR                      = KeyEvent.VK_DOLLAR;
    public static final int  VK_DOWN                        = KeyEvent.VK_DOWN;
    public static final int  VK_E                           = KeyEvent.VK_E;
    public static final int  VK_END                         = KeyEvent.VK_END;
    public static final int  VK_ENTER                       = KeyEvent.VK_ENTER;
    public static final int  VK_EQUALS                      = KeyEvent.VK_EQUALS;
    public static final int  VK_ESCAPE                      = KeyEvent.VK_ESCAPE;
    public static final int  VK_EURO_SIGN                   = KeyEvent.VK_EURO_SIGN;
    public static final int  VK_EXCLAMATION_MARK            = KeyEvent.VK_EXCLAMATION_MARK;
    public static final int  VK_F                           = KeyEvent.VK_F;
    public static final int  VK_F1                          = KeyEvent.VK_F1;
    public static final int  VK_F10                         = KeyEvent.VK_F10;
    public static final int  VK_F11                         = KeyEvent.VK_F11;
    public static final int  VK_F12                         = KeyEvent.VK_F12;
    public static final int  VK_F13                         = KeyEvent.VK_F13;
    public static final int  VK_F14                         = KeyEvent.VK_F14;
    public static final int  VK_F15                         = KeyEvent.VK_F15;
    public static final int  VK_F16                         = KeyEvent.VK_F16;
    public static final int  VK_F17                         = KeyEvent.VK_F17;
    public static final int  VK_F18                         = KeyEvent.VK_F18;
    public static final int  VK_F19                         = KeyEvent.VK_F19;
    public static final int  VK_F2                          = KeyEvent.VK_F2;
    public static final int  VK_F20                         = KeyEvent.VK_F20;
    public static final int  VK_F21                         = KeyEvent.VK_F21;
    public static final int  VK_F22                         = KeyEvent.VK_F22;
    public static final int  VK_F23                         = KeyEvent.VK_F23;
    public static final int  VK_F24                         = KeyEvent.VK_F24;
    public static final int  VK_F3                          = KeyEvent.VK_F3;
    public static final int  VK_F4                          = KeyEvent.VK_F4;
    public static final int  VK_F5                          = KeyEvent.VK_F5;
    public static final int  VK_F6                          = KeyEvent.VK_F6;
    public static final int  VK_F7                          = KeyEvent.VK_F7;
    public static final int  VK_F8                          = KeyEvent.VK_F8;
    public static final int  VK_F9                          = KeyEvent.VK_F9;
    public static final int  VK_FINAL                       = KeyEvent.VK_FINAL;
    public static final int  VK_FIND                        = KeyEvent.VK_FIND;
    public static final int  VK_FULL_WIDTH                  = KeyEvent.VK_FULL_WIDTH;
    public static final int  VK_G                           = KeyEvent.VK_G;
    public static final int  VK_GREATER                     = KeyEvent.VK_GREATER;
    public static final int  VK_H                           = KeyEvent.VK_H;
    public static final int  VK_HALF_WIDTH                  = KeyEvent.VK_HALF_WIDTH;
    public static final int  VK_HELP                        = KeyEvent.VK_HELP;
    public static final int  VK_HIRAGANA                    = KeyEvent.VK_HIRAGANA;
    public static final int  VK_HOME                        = KeyEvent.VK_HOME;
    public static final int  VK_I                           = KeyEvent.VK_I;
    public static final int  VK_INPUT_METHOD_ON_OFF         = KeyEvent.VK_INPUT_METHOD_ON_OFF;
    public static final int  VK_INSERT                      = KeyEvent.VK_INSERT;
    public static final int  VK_INVERTED_EXCLAMATION_MARK   = KeyEvent.VK_INVERTED_EXCLAMATION_MARK;
    public static final int  VK_J                           = KeyEvent.VK_J;
    public static final int  VK_JAPANESE_HIRAGANA           = KeyEvent.VK_JAPANESE_HIRAGANA;
    public static final int  VK_JAPANESE_KATAKANA           = KeyEvent.VK_JAPANESE_KATAKANA;
    public static final int  VK_JAPANESE_ROMAN              = KeyEvent.VK_JAPANESE_ROMAN;
    public static final int  VK_K                           = KeyEvent.VK_K;
    public static final int  VK_KANA                        = KeyEvent.VK_KANA;
    public static final int  VK_KANA_LOCK                   = KeyEvent.VK_KANA_LOCK;
    public static final int  VK_KANJI                       = KeyEvent.VK_KANJI;
    public static final int  VK_KATAKANA                    = KeyEvent.VK_KATAKANA;
    public static final int  VK_KP_DOWN                     = KeyEvent.VK_KP_DOWN;
    public static final int  VK_KP_LEFT                     = KeyEvent.VK_KP_LEFT;
    public static final int  VK_KP_RIGHT                    = KeyEvent.VK_KP_RIGHT;
    public static final int  VK_KP_UP                       = KeyEvent.VK_KP_UP;
    public static final int  VK_L                           = KeyEvent.VK_L;
    public static final int  VK_LEFT                        = KeyEvent.VK_LEFT;
    public static final int  VK_LEFT_PARENTHESIS            = KeyEvent.VK_LEFT_PARENTHESIS;
    public static final int  VK_LESS                        = KeyEvent.VK_LESS;
    public static final int  VK_M                           = KeyEvent.VK_M;
    public static final int  VK_META                        = KeyEvent.VK_META;
    public static final int  VK_MINUS                       = KeyEvent.VK_MINUS;
    public static final int  VK_MODECHANGE                  = KeyEvent.VK_MODECHANGE;
    public static final int  VK_MULTIPLY                    = KeyEvent.VK_MULTIPLY;
    public static final int  VK_N                           = KeyEvent.VK_N;
    public static final int  VK_NONCONVERT                  = KeyEvent.VK_NONCONVERT;
    public static final int  VK_NUMBER_SIGN                 = KeyEvent.VK_NUMBER_SIGN;
    public static final int  VK_NUMPAD0                     = KeyEvent.VK_NUMPAD0;
    public static final int  VK_NUMPAD1                     = KeyEvent.VK_NUMPAD1;
    public static final int  VK_NUMPAD2                     = KeyEvent.VK_NUMPAD2;
    public static final int  VK_NUMPAD3                     = KeyEvent.VK_NUMPAD3;
    public static final int  VK_NUMPAD4                     = KeyEvent.VK_NUMPAD4;
    public static final int  VK_NUMPAD5                     = KeyEvent.VK_NUMPAD5;
    public static final int  VK_NUMPAD6                     = KeyEvent.VK_NUMPAD6;
    public static final int  VK_NUMPAD7                     = KeyEvent.VK_NUMPAD7;
    public static final int  VK_NUMPAD8                     = KeyEvent.VK_NUMPAD8;
    public static final int  VK_NUMPAD9                     = KeyEvent.VK_NUMPAD9;
    public static final int  VK_NUM_LOCK                    = KeyEvent.VK_NUM_LOCK;
    public static final int  VK_O                           = KeyEvent.VK_O;
    public static final int  VK_OPEN_BRACKET                = KeyEvent.VK_OPEN_BRACKET;
    public static final int  VK_P                           = KeyEvent.VK_P;
    public static final int  VK_PAGE_DOWN                   = KeyEvent.VK_PAGE_DOWN;
    public static final int  VK_PAGE_UP                     = KeyEvent.VK_PAGE_UP;
    public static final int  VK_PASTE                       = KeyEvent.VK_PASTE;
    public static final int  VK_PAUSE                       = KeyEvent.VK_PAUSE;
    public static final int  VK_PERIOD                      = KeyEvent.VK_PERIOD;
    public static final int  VK_PLUS                        = KeyEvent.VK_PLUS;
    public static final int  VK_PREVIOUS_CANDIDATE          = KeyEvent.VK_PREVIOUS_CANDIDATE;
    public static final int  VK_PRINTSCREEN                 = KeyEvent.VK_PRINTSCREEN;
    public static final int  VK_PROPS                       = KeyEvent.VK_PROPS;
    public static final int  VK_Q                           = KeyEvent.VK_Q;
    public static final int  VK_QUOTE                       = KeyEvent.VK_QUOTE;
    public static final int  VK_QUOTEDBL                    = KeyEvent.VK_QUOTEDBL;
    public static final int  VK_R                           = KeyEvent.VK_R;
    public static final int  VK_RIGHT                       = KeyEvent.VK_RIGHT;
    public static final int  VK_RIGHT_PARENTHESIS           = KeyEvent.VK_RIGHT_PARENTHESIS;
    public static final int  VK_ROMAN_CHARACTERS            = KeyEvent.VK_ROMAN_CHARACTERS;
    public static final int  VK_S                           = KeyEvent.VK_S;
    public static final int  VK_SCROLL_LOCK                 = KeyEvent.VK_SCROLL_LOCK;
    public static final int  VK_SEMICOLON                   = KeyEvent.VK_SEMICOLON;
    public static final int  VK_SEPARATER                   = KeyEvent.VK_SEPARATER;
    public static final int  VK_SEPARATOR                   = KeyEvent.VK_SEPARATOR;
    public static final int  VK_SHIFT                       = KeyEvent.VK_SHIFT;
    public static final int  VK_SLASH                       = KeyEvent.VK_SLASH;
    public static final int  VK_SPACE                       = KeyEvent.VK_SPACE;
    public static final int  VK_STOP                        = KeyEvent.VK_STOP;
    public static final int  VK_SUBTRACT                    = KeyEvent.VK_SUBTRACT;
    public static final int  VK_T                           = KeyEvent.VK_T;
    public static final int  VK_TAB                         = KeyEvent.VK_TAB;
    public static final int  VK_U                           = KeyEvent.VK_U;
    public static final int  VK_UNDEFINED                   = KeyEvent.VK_UNDEFINED;
    public static final int  VK_UNDERSCORE                  = KeyEvent.VK_UNDERSCORE;
    public static final int  VK_UNDO                        = KeyEvent.VK_UNDO;
    public static final int  VK_UP                          = KeyEvent.VK_UP;
    public static final int  VK_V                           = KeyEvent.VK_V;
    public static final int  VK_W                           = KeyEvent.VK_W;
    public static final int  VK_WINDOWS                     = KeyEvent.VK_WINDOWS;
    public static final int  VK_X                           = KeyEvent.VK_X;
    public static final int  VK_Y                           = KeyEvent.VK_Y;
    public static final int  VK_Z                           = KeyEvent.VK_Z;

    
    private final int  mKeyCode;
    private final char mKeyChar;
    private final int  mKeyLocation;
    
    
    public GKeyEvent( GComponent source, 
                      int id, 
                      long timestampMicros, 
                      int modifiers, 
                      int keyCode, 
                      char keyChar )
    {
        this( source, id, timestampMicros, modifiers, keyCode, keyChar, KEY_LOCATION_UNKNOWN );
    }

    
    public GKeyEvent( GComponent source, 
                      int id, 
                      long timestampMicros, 
                      int modifiers, 
                      int keyCode, 
                      char keyChar, 
                      int keyLocation )
    {
        super( source, id, timestampMicros, modifiers );
        mKeyCode     = keyCode;
        mKeyChar     = keyChar;
        mKeyLocation = keyLocation;
    }
    
    
    
    public char getKeyChar() {
        return mKeyChar;
    }

    
    public int getKeyCode() {
        return mKeyCode;
    }
    
    
    public int getKeyLocation() {
        return mKeyLocation;
    }

    
    public boolean isActionKey() {
        switch ( mKeyCode ) {
        case VK_HOME:
        case VK_END:
        case VK_PAGE_UP:
        case VK_PAGE_DOWN:
        case VK_UP:
        case VK_DOWN:
        case VK_LEFT:
        case VK_RIGHT:
        case VK_BEGIN:

        case VK_KP_LEFT: 
        case VK_KP_UP: 
        case VK_KP_RIGHT: 
        case VK_KP_DOWN: 

        case VK_F1:
        case VK_F2:
        case VK_F3:
        case VK_F4:
        case VK_F5:
        case VK_F6:
        case VK_F7:
        case VK_F8:
        case VK_F9:
        case VK_F10:
        case VK_F11:
        case VK_F12:
        case VK_F13:
        case VK_F14:
        case VK_F15:
        case VK_F16:
        case VK_F17:
        case VK_F18:
        case VK_F19:
        case VK_F20:
        case VK_F21:
        case VK_F22:
        case VK_F23:
        case VK_F24:
        case VK_PRINTSCREEN:
        case VK_SCROLL_LOCK:
        case VK_CAPS_LOCK:
        case VK_NUM_LOCK:
        case VK_PAUSE:
        case VK_INSERT:

        case VK_FINAL:
        case VK_CONVERT:
        case VK_NONCONVERT:
        case VK_ACCEPT:
        case VK_MODECHANGE:
        case VK_KANA:
        case VK_KANJI:
        case VK_ALPHANUMERIC:
        case VK_KATAKANA:
        case VK_HIRAGANA:
        case VK_FULL_WIDTH:
        case VK_HALF_WIDTH:
        case VK_ROMAN_CHARACTERS:
        case VK_ALL_CANDIDATES:
        case VK_PREVIOUS_CANDIDATE:
        case VK_CODE_INPUT:
        case VK_JAPANESE_KATAKANA:
        case VK_JAPANESE_HIRAGANA:
        case VK_JAPANESE_ROMAN:
        case VK_KANA_LOCK:
        case VK_INPUT_METHOD_ON_OFF:

        case VK_AGAIN:
        case VK_UNDO:
        case VK_COPY:
        case VK_PASTE:
        case VK_CUT:
        case VK_FIND:
        case VK_PROPS:
        case VK_STOP:

        case VK_HELP:
        case VK_WINDOWS:
        case VK_CONTEXT_MENU:
            return true;
        }
        
        return false;
    }


    public static String getKeyModifiersText( int modifiers ) {
        return KeyEvent.getKeyModifiersText( modifiers );
    }


    public static String getKeyText( int keyCode ) {
        return KeyEvent.getKeyText( keyCode );
    }


}
