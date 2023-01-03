package replies;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import functions.ResponseService;
import keyboards.HelpRateEntitiesKeyboard;
import keyboards.HelpRecommendEntitiesKeyboard;
import keyboards.Keyboard;
import utils.EmojiCodes;

public class HelpReply implements Reply {

	Message[] messages;
	ReplyMarkup replyMarkup;
	
	public HelpReply(String helpType) {
		
		ResponseService rs = new ResponseService();
		
		String text = "";
		Keyboard keyboard = null;
		
		String clipboardCode = EmojiCodes.hexHtmlSurrogatePairs.get("clipboard");
		String thumbsupCode = EmojiCodes.hexHtmlSurrogatePairs.get("thumbsup");
		String thumbsdownCode = EmojiCodes.hexHtmlSurrogatePairs.get("thumbsdown");
		String arrowRightCode = EmojiCodes.hexHtmlSurrogatePairs.get("arrow_right");
		String silhouetteCode = EmojiCodes.hexHtmlSurrogatePairs.get("silhouette");
		
		String smileCode = EmojiCodes.hexHtmlSurrogatePairs.get("smiley");
		String sadCode = EmojiCodes.hexHtmlSurrogatePairs.get("slightly_sad");
		String cycloneCode = EmojiCodes.hexHtmlSurrogatePairs.get("cyclone");
		String bookmarkCode = EmojiCodes.hexHtmlSurrogatePairs.get("bookmark_tags");
		String megaphoneCode = EmojiCodes.hexHtmlSurrogatePairs.get("megaphone");
		String angerCode = EmojiCodes.hexHtmlSurrogatePairs.get("anger");
		
		switch (helpType) {
		case "rateEntitySelected":
			text += clipboardCode + " Details: tap if you want to view the " + rs.getEntityTypeSingularMessage(true) + " details\n";
			text += thumbsupCode + ": tap if you like the " + rs.getEntityTypeSingularMessage(true) + "\n";
			text += thumbsdownCode + ": tap if you don't like the " + rs.getEntityTypeSingularMessage(true) + "\n";
			text += arrowRightCode + " Skip: tap for skipping to the next " + rs.getEntityTypeSingularMessage(true) + "\n";
			text += silhouetteCode + " Profile: tap to view your preferences and change them";
			keyboard = new HelpRateEntitiesKeyboard();
			break;
		case "recEntitySelected":
			text += smileCode + " Like: tap if you like the " + rs.getEntityTypeSingularMessage(true) + "\n";
			text += sadCode + " Dislike: tap if you don't like the " + rs.getEntityTypeSingularMessage(true) + "\n";
			text += cycloneCode + " Like, but...: tap if you like the " + rs.getEntityTypeSingularMessage(true) + ", ";
			text += "but you don't like some of its properties\n";
			text += bookmarkCode + " Details:  tap if you want to view the " + rs.getEntityTypeSingularMessage(true) + " details\n";
			text += megaphoneCode + " Why?: tap for viewing the motivations behind the recommendations\n";
			text += silhouetteCode + " Profile: by tapping this button you can view preferences and change them\n";
			text += angerCode + " Change: tap for receiving a new set of recommendations";
			keyboard = new HelpRecommendEntitiesKeyboard();
			break;
		case "profileSelected":
			text += "Here you can view and change your preferences by tapping on it.\n";
			text += "You rated:";
			break;
		}
		
		this.messages = new Message[] {
				new Message(text)
		};
		if (keyboard != null) {
			replyMarkup = new ReplyMarkup(keyboard);
		}
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
