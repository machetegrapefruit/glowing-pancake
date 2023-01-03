package keyboards;

import functions.ResponseService;
import utils.EmojiCodes;

/**
 * Keyboard utilizzata se l'utente non ha ancora votato il numero minimo di film e quindi non può ancora
 * ottenere raccomandazioni.
 * @author Altieri
 *
 */
public class StartProfileAcquisitionKeyboard implements Keyboard {

	private String[][] keyboard;
	
	public StartProfileAcquisitionKeyboard() {
		
		ResponseService rs = new ResponseService();
		
		String redCircleCode = EmojiCodes.hexHtmlSurrogatePairs.get("red_circle");
		String blueCircleCode = EmojiCodes.hexHtmlSurrogatePairs.get("blue_circle");
		String gearCode = EmojiCodes.hexHtmlSurrogatePairs.get("gear");;

		keyboard = new String[][] {
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