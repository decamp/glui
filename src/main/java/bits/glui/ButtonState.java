package bits.glui;

public enum ButtonState {
    NORMAL,
    NORMAL_SELECTED,
    MOUSEOVER,
    MOUSEOVER_SELECTED,
    DEPRESSED,
    DEPRESSED_SELECTED;
    
    public static final int length = DEPRESSED_SELECTED.ordinal() + 1;
    
}
