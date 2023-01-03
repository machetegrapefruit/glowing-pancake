package entity;

import keyboards.Keyboard;

public class ReplyMarkup {

	private Keyboard keyboard;
	private boolean resizeKeyboard;
	private boolean oneTimeKeyboard;
	
	public ReplyMarkup(Keyboard keyboard, boolean resizeKeyboard, boolean oneTimeKeyboard) {
		this.keyboard = keyboard;
		this.resizeKeyboard = resizeKeyboard;
		this.oneTimeKeyboard = oneTimeKeyboard;
	}
	
	public ReplyMarkup(Keyboard keyboard) {
		this(keyboard, true, false);
	}

	public Keyboard getKeyboard() {
		return keyboard;
	}
	
	public boolean getResizeKeyboard() {
		return resizeKeyboard;
	}
	
	public boolean getOneTimeKeyboard() {
		return oneTimeKeyboard;
	}
}
