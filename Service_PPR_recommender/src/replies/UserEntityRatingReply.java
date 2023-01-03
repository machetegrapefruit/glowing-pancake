package replies;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import functions.EntityService;
import functions.LogService;
import functions.ProfileService;
import functions.ResponseService;
import keyboards.ErrorKeyboard;
import keyboards.Keyboard;
import keyboards.RatedEntityNewUserKeyboard;
import keyboards.RatedEntityOldUserKeyboard;
import restService.GetRatings;
import utils.EmojiCodes;
import utils.TextUtils;

public class UserEntityRatingReply implements Reply {

	Message[] messages;
	ReplyMarkup replyMarkup;

	public void addReplyMessages(Reply reply) {
		Message[] replyMessages = reply.getMessages();
		Message[] newMessages = new Message[this.messages.length + replyMessages.length];
		for (int i = 0; i < this.messages.length; i++) {
			newMessages[i] = this.messages[i];
		}
		for (int i = 0; i < replyMessages.length; i++) {
			newMessages[this.messages.length + i] = replyMessages[i];
		}
		
		this.messages = newMessages;
	}
	
	private void setMessage(String text) {
		messages = new Message[] {new Message(text)};
	}
	public UserEntityRatingReply(String entity, String userID, int rating, LogService logService) throws Exception {
		
		
		EntityService entityService = new EntityService();
		ResponseService rs = new ResponseService();
		System.out.println("[UserEntityRatingReply] Inizio creazione reply");
		
		String symbol = null;
		switch (rating) {
		case 1: symbol = "+"; break;
		case 0: symbol = "-"; break;
		case 2: symbol = "/"; break;
		}
		logService.addRecognizedObject(entityService.getEntityURI(TextUtils.getNameFromURI(entity)) + symbol);
		
		String confusedCode = EmojiCodes.hexHtmlSurrogatePairs.get("confused");
		String thinkingCode = EmojiCodes.hexHtmlSurrogatePairs.get("thinking");
		String smileCode = EmojiCodes.hexHtmlSurrogatePairs.get("smiley");
		
		GetRatings service = new GetRatings();
		String ratedEntities = service.getNumberRatedEntities(userID);
		String ratedProperties = service.getNumberRatedProperties(userID);
		int neededRatings = 3 - Integer.parseInt(ratedEntities) - Integer.parseInt(ratedProperties);
		String text = "";
		
		System.out.println("[UserEntityRatingReply] Creo reply");
		if (entity != null) {
						
			switch (rating) {
			case 2:
				text = "You skipped \"" + entity + "\" " + rs.getEntityTypeSingularMessage(true) + "\n";
				break;
			case 1:
				text = "You have rated \"" + entity + "\" " + rs.getEntityTypeSingularMessage(true) + "\n";
				break;
			case 0:
				text = "You have rated \"" + entity + "\" " + rs.getEntityTypeSingularMessage(true) + "\n";
				break;
			}
			
			boolean hasPositiveRating = (new ProfileService()).hasPositiveRating(userID);
			if (neededRatings > 0) {
				text += "I need " + neededRatings + " more ratings " + smileCode;
				Keyboard keyboard = new RatedEntityNewUserKeyboard();
				replyMarkup = new ReplyMarkup(keyboard, true, false);
			} else if (!hasPositiveRating) {
				text += "\nI need at least a positive preference to recommend you " + (new ResponseService()).getEntityTypePluralMessage(true);

			} else {
				Keyboard keyboard = new RatedEntityOldUserKeyboard();
				replyMarkup = new ReplyMarkup(keyboard, true, false);
			}
			
		} else {
			text = "Sorry..." + confusedCode + "\nI'm not able to find " + rs.getEntityTypePluralMessage(true) + " right now" + thinkingCode;
			Keyboard keyboard = new ErrorKeyboard();
			
			replyMarkup = new ReplyMarkup(keyboard, true, false);
		}
		
		setMessage(text);		
		System.out.println("[UserEntityRatingReply] Reply creata");

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
