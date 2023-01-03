package keyboards;

import functions.ResponseService;
import utils.EmojiCodes;

/**
 * Keyboard utilizzata se l'utente ha già votato il numero minimo di film e quindi
 * può ricevere raccomandazioni.
 * @author Altieri
 *
 */
public class UserPropertyValueKeyboard implements Keyboard {

	private String[][] keyboard;
	
	public UserPropertyValueKeyboard() {
		
		ResponseService rs = new ResponseService();
		
		String globeCode = EmojiCodes.hexHtmlSurrogatePairs.get("globe_with_meridians");
		String redCircleCode = EmojiCodes.hexHtmlSurrogatePairs.get("red_circle");
		String blueCircleCode = EmojiCodes.hexHtmlSurrogatePairs.get("blue_circle");
		String gearCode = EmojiCodes.hexHtmlSurrogatePairs.get("gear");
		
		keyboard = new String[][] {
			new String[] {globeCode + " Recommend " + rs.getEntityTypePluralMessage(false)},
			new String[] {redCircleCode + " Rate properties"},
			new String[] {blueCircleCode + " Rate " + rs.getEntityTypePluralMessage(true)},
			new String[] {gearCode + " Profile"}
		};
	}

	@Override
	public String[][] getOptions() {
		return keyboard;
	}
}
