package replies;

import dialog.DialogState;
import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import keyboards.Keyboard;
import keyboards.MoreKeyboard;

public class MoreReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
	
	public MoreReply(String userID, DialogState state) throws Exception {
		
		messages = new Message[] {
				new Message("Please, choose among the most popular properties or type the name")
		};
		Keyboard keyboard = new MoreKeyboard(userID, state);
		replyMarkup = new ReplyMarkup(keyboard);
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
