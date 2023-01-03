package replies;

import org.apache.commons.lang.WordUtils;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import keyboards.PropertyValueRatingKeyboard;

public class PropertyValueRatingReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
	
	public PropertyValueRatingReply(String propertyValue) {
		
		String text = "Do you like \"" + WordUtils.capitalize(propertyValue) + "\"?";
		messages = new Message[] {new Message(text)};
		replyMarkup = new ReplyMarkup(new PropertyValueRatingKeyboard(propertyValue));
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
