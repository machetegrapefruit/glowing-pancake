package keyboards;

import functions.ResponseService;
import utils.EmojiCodes;

public class HelpRecommendEntitiesKeyboard implements Keyboard {

	private String[][] options;
	
	public HelpRecommendEntitiesKeyboard() {
		
		ResponseService rs = new ResponseService();
		
		String backarrowCode = EmojiCodes.hexHtmlSurrogatePairs.get("backarrow");
		
		options = new String[][] {
			new String[] {backarrowCode + " Back to " + rs.getEntityTypePluralMessage(false)}
		};
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}

}
