package replies;

import org.apache.commons.lang3.text.WordUtils;

import dialog.DialogState;
import entity.AuxAPI;
import entity.Message;
import entity.Property;
import entity.ReplyMarkup;
import keyboards.Keyboard;
import keyboards.PropertyValueKeyboard;

public class PropertyValueReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
	
	public PropertyValueReply(String userID, String propertyType, DialogState state) throws Exception {
		
		Reply reply = null;

		Property propertyToRate = null;
		if (state.getPropertyToRate() != null) {
			System.out.println("propertyToRate != null");
			propertyToRate = state.getPropertyToRate();

			if (propertyToRate.getType() == null &&
					propertyToRate.getValue() != null) {
				// C'era una valutazione in sospeso, non si sapeva il type
				
				propertyToRate.setType(propertyType);
				state.setPropertyToRate(propertyToRate);
				reply = new PropertyValueRatingReply(WordUtils.capitalize(propertyToRate.getValue()));
				messages = reply.getMessages();
				replyMarkup = reply.getReplyMarkup();
			} 
		} else {
			// Valutazione di una proprietà generica
			
			Keyboard keyboard = null;
			
			keyboard = new PropertyValueKeyboard(userID, propertyType, state);
			
			String replyText = "Please, choose the " + propertyType + " you want to rate\n";
			replyText += "or type the name";
			
			messages = new Message[] {
					new Message(replyText)
			};
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
