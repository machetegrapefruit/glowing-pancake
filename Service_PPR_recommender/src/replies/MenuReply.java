package replies;

import java.util.ArrayList;
import java.util.List;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import functions.ProfileService;
import functions.ResponseService;
import keyboards.Keyboard;
import keyboards.StartProfileAcquisitionKeyboard;
import keyboards.UserPropertyValueKeyboard;
import restService.GetRatings;
import utils.EmojiCodes;

public class MenuReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
		
	public MenuReply(String userID) throws Exception {
				
		ResponseService rs = new ResponseService();
		
		String slightSmileCode = EmojiCodes.hexHtmlSurrogatePairs.get("slight_smile");
		String smileCode = EmojiCodes.hexHtmlSurrogatePairs.get("smiley");
		String globeCode = EmojiCodes.hexHtmlSurrogatePairs.get("globe_with_meridians");
		String winkCode = EmojiCodes.hexHtmlSurrogatePairs.get("wink");
		
		GetRatings service = new GetRatings();
		String ratedEntities = service.getNumberRatedEntities(userID);
		String ratedProperties = service.getNumberRatedProperties(userID);
		final int TOTAL_NEEDED_RATINGS = 3;
		int neededRatings = TOTAL_NEEDED_RATINGS - (Integer.parseInt(ratedEntities) + Integer.parseInt(ratedProperties));
		
		List<Message> messages = new ArrayList<Message>();
		Keyboard keyboard = null;
		
		if (neededRatings > 0) {
			String text = "I need at least " + neededRatings + " preferences ";
			text += "for generating recommendations.";
			messages.add(new Message(text));
			String text2 = "Please, tell me something about you\n";
			text2 += "or type your preference " + slightSmileCode;
			messages.add(new Message(text2));
			keyboard = new StartProfileAcquisitionKeyboard();
		} else if (!(new ProfileService()).hasPositiveRating(userID)) {
			String text = "\nI need at least a positive preference to recommend you " + (new ResponseService()).getEntityTypePluralMessage(true);
			messages.add(new Message(text));
			String text2 = "Please, tell me something about you\n";
			text2 += "or type your preference " + slightSmileCode;
			messages.add(new Message(text2));
			keyboard = new StartProfileAcquisitionKeyboard();
		} else {
			String text = "Let me recommend a " + rs.getEntityTypeSingularMessage(true) + smileCode + "\n";
			text += "Tap on \"" + globeCode + " Recommend " + rs.getEntityTypePluralMessage(false) + "\" button, ";
			text += "otherwise you can enrich your profile by providing ";
			text += "further ratings " + winkCode;
			messages.add(new Message(text));
			keyboard = new UserPropertyValueKeyboard();
		}
		
		this.messages = new Message[messages.size()];
		for (int i = 0; i < messages.size(); i++) {
			this.messages[i] = messages.get(i);
		}
		
		this.replyMarkup = new ReplyMarkup(keyboard);
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
