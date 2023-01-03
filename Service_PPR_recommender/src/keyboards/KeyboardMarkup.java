package keyboards;

public class KeyboardMarkup {
	
	String[][] keyboard;
	boolean resize_keyboard;
	boolean one_time_keyboard;
	
	public KeyboardMarkup(String[]... options) {
		this(true, false, options);
	}
	
	public KeyboardMarkup(boolean resizeKeyboard, boolean oneTimeKeyboard, String[]... options) {
		keyboard = new String[options.length][];
		this.resize_keyboard = resizeKeyboard;
		this.one_time_keyboard = oneTimeKeyboard;
		for (int i = 0; i < options.length; i++) {
			keyboard[i] = new String[options[i].length];
			for (int j = 0; j < options[i].length; j++) {
				keyboard[i][j] = options[i][j];
			}
		}
	}
}