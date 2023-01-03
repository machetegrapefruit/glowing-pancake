package keyboards;

import java.util.Map;

import functions.ResponseService;
import utils.EmojiCodes;

public class RatedEntityNewUserKeyboard implements Keyboard {

	private String[][] keyboard;
	
	public RatedEntityNewUserKeyboard() {
		
		ResponseService responseService = new ResponseService();
		
		Map<String, String> emojis = EmojiCodes.getEmojis();
		
		String clipboardCode = emojis.get(EmojiCodes.CLIPBOARD);
		String thumbsupCode = emojis.get(EmojiCodes.THUMBSUP);
		String thumbsdownCode = emojis.get(EmojiCodes.THUMBSDOWN);
		String arrowCode = emojis.get(EmojiCodes.ARROW_RIGHT);
		
		String redCircleCode = emojis.get(EmojiCodes.RED_CIRCLE);
		String blueBookCode = emojis.get(EmojiCodes.BLUE_BOOK);
		String gearCode = emojis.get(EmojiCodes.GEAR);
		String backArrowCode = emojis.get(EmojiCodes.BACKARROW);
		
		keyboard = new String[][] {
			new String[] {clipboardCode + " Details", thumbsupCode, thumbsdownCode, arrowCode + " Skip"},
			new String[] {redCircleCode + " " + responseService.getEntityTypeSingularMessage(false) + " Properties", blueBookCode + " Help"},
			new String[] {gearCode + " Profile", backArrowCode + " Home"}
		};
	}
	
	@Override
	public String[][] getOptions() {
		return keyboard;
	}

}
