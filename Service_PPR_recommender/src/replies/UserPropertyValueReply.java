package replies;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import functions.ResponseService;
import keyboards.Keyboard;
import keyboards.UserPropertyValueKeyboard;
import utils.EmojiCodes;

public class UserPropertyValueReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
	
	public UserPropertyValueReply(String userID, String firstname) {
		
		String smileCode = EmojiCodes.hexHtmlSurrogatePairs.get("smiley");
		String winkCode = EmojiCodes.hexHtmlSurrogatePairs.get("wink");
		String globeCode = EmojiCodes.hexHtmlSurrogatePairs.get("globe_with_meridians");
		
		ResponseService responseService = new ResponseService();
		
		String text = responseService.getAbleToRecommendMessage();
		Message message = new Message(text);
		
		this.messages = new Message[] {
				message
		};

		Keyboard keyboard = new UserPropertyValueKeyboard();
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
