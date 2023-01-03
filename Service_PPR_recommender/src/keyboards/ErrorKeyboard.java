package keyboards;

import utils.EmojiCodes;

public class ErrorKeyboard implements Keyboard {

	String[][] options;
	
	public ErrorKeyboard() {
		
		String backarrowCode = EmojiCodes.hexHtmlSurrogatePairs.get("backarrow");
		
		options = new String[][] {
			new String[] {backarrowCode + " Home"}
		};
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}
}
