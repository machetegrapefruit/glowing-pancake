package replies;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import functions.ResponseService;
import keyboards.Keyboard;
import keyboards.StartProfileAcquisitionKeyboard;
import restService.GetRatings;
import utils.EmojiCodes;

public class StartProfileAcquisitionReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
	
	public StartProfileAcquisitionReply(String userID, String firstname) throws Exception {
		String smileCode = EmojiCodes.hexHtmlSurrogatePairs.get("smiley");
		String winkCode = EmojiCodes.hexHtmlSurrogatePairs.get("wink");
		String slightSmileCode = EmojiCodes.hexHtmlSurrogatePairs.get("slight_smile");
		String clapperBoardCode = EmojiCodes.hexHtmlSurrogatePairs.get("clapper");
		
		GetRatings service = new GetRatings();
		String ratedEntities = service.getNumberRatedEntities(userID);
		String ratedProperties = service.getNumberRatedProperties(userID);
		final int TOTAL_NEEDED_RATINGS = 3;
		int neededRatings = TOTAL_NEEDED_RATINGS - (Integer.parseInt(ratedEntities) + Integer.parseInt(ratedProperties));
		
		ResponseService rs = new ResponseService();
		String text = rs.getStartMessage(firstname, Integer.toString(neededRatings));
		Message message = new Message(text);
		
		Keyboard keyboard = new StartProfileAcquisitionKeyboard();
		
		this.messages = new Message[] {
			message,
		};
		this.replyMarkup = new ReplyMarkup(keyboard, true, false);
	}
	
	@Override
	public Message[] getMessages() {
		return messages;
	}

	@Override
	public ReplyMarkup getReplyMarkup() {
		return replyMarkup;
	}

	@Override
	public AuxAPI getAuxAPI() {
		return null;
	}
}
