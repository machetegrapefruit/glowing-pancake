package replies;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import keyboards.ResetKeyboard;

public class ResetReply implements Reply {

	Message[] messages;
	ReplyMarkup replyMarkup;
	
	public ResetReply() {
		messages = new Message[] {
				new Message("Please, select what you desire to delete:")
		};
		replyMarkup = new ReplyMarkup(new ResetKeyboard());
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
