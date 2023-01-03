package keyboards;

import functions.ResponseService;
import utils.EmojiCodes;

public class HelpRateEntitiesKeyboard implements Keyboard {

	private String[][] options;
	
	public HelpRateEntitiesKeyboard() {
		
		ResponseService rs = new ResponseService();
		
		String blueCircleCode = EmojiCodes.hexHtmlSurrogatePairs.get("blue_circle");
		
		options = new String[][] {
			new String[] {blueCircleCode + " Rate " + rs.getEntityTypePluralMessage(false)}
		};
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}

}
