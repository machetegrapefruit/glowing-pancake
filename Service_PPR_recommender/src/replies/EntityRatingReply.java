package replies;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import functions.ProfileService;
import functions.ResponseService;
import keyboards.Keyboard;
import keyboards.RatedEntityNewUserKeyboard;
import keyboards.RatedEntityOldUserKeyboard;
import restService.GetRatings;
import utils.EmojiCodes;

public class EntityRatingReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
		
	private GetRatings service;

	public EntityRatingReply(String entity, String poster, String userID) throws Exception {
		
		System.out.println("[EntityRatingReply] entity: " + entity);
		System.out.println("[EntityRatingReply] poster: " + poster);
			
		service = new GetRatings();
		ResponseService rs = new ResponseService();
		
		String confusedCode = EmojiCodes.hexHtmlSurrogatePairs.get("confused");
		String thinkingCode = EmojiCodes.hexHtmlSurrogatePairs.get("thinking");
		String globeCode = EmojiCodes.hexHtmlSurrogatePairs.get("globe_with_meridians");
		String smileCode = EmojiCodes.hexHtmlSurrogatePairs.get("smiley");
		String thumbsupCode = EmojiCodes.hexHtmlSurrogatePairs.get("thumbsup");
		String thumbsdownCode = EmojiCodes.hexHtmlSurrogatePairs.get("thumbsdown");
		String rightArrowCode = EmojiCodes.hexHtmlSurrogatePairs.get("arrow_right");
		String slightSmileCode = EmojiCodes.hexHtmlSurrogatePairs.get("slight_smile");
		
		boolean hasPositiveRating = (new ProfileService()).hasPositiveRating(userID);
		List<Message> messages = new ArrayList<Message>();
		Keyboard keyboard = null;
		
		String ratedEntities = service.getNumberRatedEntities(userID);
		String ratedProperties = service.getNumberRatedProperties(userID);
		int neededRatings = 3 - Integer.parseInt(ratedEntities) - Integer.parseInt(ratedProperties);
		
		if (entity != null) {
			
			if (poster != "" && poster != "N/A") {
				messages.add(new Message(WordUtils.capitalize(entity), poster));
			} else {
				messages.add(new Message(WordUtils.capitalize(entity)));
			}
			
			String text = "Do you " + thumbsupCode + " like or " + thumbsdownCode + " dislike this " + rs.getEntityTypeSingularMessage(true) + "?\n";
			text += "Otherwise, tap " + rightArrowCode + " Skip";
			messages.add(new Message(text));
			
			if (neededRatings == 0 && hasPositiveRating) {
				String text2 = "I am now able to recommend you some " + rs.getEntityTypePluralMessage(true) + smileCode;
				text2 += "\nTap on \"" + globeCode + " Recommend " + rs.getEntityTypePluralMessage(false) + "\" button, ";
				text2 += "otherwise you can enrich your profile by rating this " + rs.getEntityTypeSingularMessage(true) + slightSmileCode;
				messages.add(new Message(text2));
			}
			
		} else {
			String text = "Sorry..." + confusedCode + "\n";
			text += "I'm not able to find other " + rs.getEntityTypePluralMessage(true) + " right now" + thinkingCode + "\n\n";
			text += "Tap on \"" + globeCode + " Recommend " + rs.getEntityTypePluralMessage(false) + "\" button" + smileCode;
			messages.add(new Message(text));
		}
		
		this.messages = new Message[messages.size()];
		for (int i = 0; i < this.messages.length; i++) {
			this.messages[i] = messages.get(i);
		}
		
		if (neededRatings > 0 || !hasPositiveRating) {
			keyboard = new RatedEntityNewUserKeyboard();
		} else {
			keyboard = new RatedEntityOldUserKeyboard();
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
