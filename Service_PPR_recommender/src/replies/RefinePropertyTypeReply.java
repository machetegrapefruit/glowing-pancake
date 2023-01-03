package replies;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import keyboards.RefinePropertyTypeKeyboard;

public class RefinePropertyTypeReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
	
	public RefinePropertyTypeReply(String entityName, String propertyType) throws Exception {
		
		messages = new Message[] {
				new Message("Please, choose the " + propertyType + 
						" you want to rate or type the name")
		};
		replyMarkup = new ReplyMarkup(new RefinePropertyTypeKeyboard(entityName, propertyType));
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
