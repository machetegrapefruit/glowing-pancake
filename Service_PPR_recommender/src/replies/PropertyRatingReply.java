package replies;

import dialog.DialogState;
import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import keyboards.PropertyRatingKeyboard;

public class PropertyRatingReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
		
	public PropertyRatingReply(String userID, DialogState state) throws Exception {
		if (state.isGenericProperties()) {
			String text = "Please, choose among the most popular properties";
			messages = new Message[] {
					new Message(text)
			};
		} else {
			String text = "Please, choose among the properties of " + state.getEntityToRate();
			messages = new Message[] {
					new Message(text)
			};
		}
		
		replyMarkup = new ReplyMarkup(new PropertyRatingKeyboard(userID, state));
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
