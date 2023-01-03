package keyboards;

import functions.ResponseService;
import utils.EmojiCodes;

public class RefineEndKeyboard implements Keyboard {

	String[][] options;
	
	public RefineEndKeyboard(String entityName) {
		
		ResponseService responseService = new ResponseService();
		
		String magnifyingGlass = EmojiCodes.getEmojis().get(EmojiCodes.MAGNIFYING_GLASS);
		String backArrow = EmojiCodes.getEmojis().get(EmojiCodes.BACKARROW);
		
		options = new String[][] {
			new String[] {
					magnifyingGlass + " Rate other properties of \"" + entityName + "\""
			},
			new String[] {
					backArrow + " Back to " + responseService.getEntityTypePluralMessage(true)
			}
		};
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}

}
