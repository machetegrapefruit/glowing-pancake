package keyboards;

import functions.ResponseService;
import utils.EmojiCodes;

public class ResetKeyboard implements Keyboard {

	String[][] options;
	
	public ResetKeyboard() {
		
		ResponseService rs = new ResponseService();
		
		String blackSquareButtonCode = EmojiCodes.hexHtmlSurrogatePairs.get("black_square_button");
		String whiteSquareButtonCode = EmojiCodes.hexHtmlSurrogatePairs.get("white_square_button");
		String wasteBasketCode = EmojiCodes.hexHtmlSurrogatePairs.get("waste_basket");
		
		options = new String[][] {
			new String[] {
					blackSquareButtonCode + " All Properties",
					whiteSquareButtonCode + " All " + rs.getEntityTypePluralMessage(false)
					},
			new String[] {
					wasteBasketCode + " Delete all preferences"
			}
		};
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}

}

	