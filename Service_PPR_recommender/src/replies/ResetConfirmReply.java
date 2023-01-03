package replies;

import org.apache.commons.lang.WordUtils;

import entity.AuxAPI;
import entity.DeleteType;
import entity.Message;
import entity.ReplyMarkup;
import functions.ResponseService;
import restService.Delete;

public class ResetConfirmReply implements Reply {

	Message[] messages;
	ReplyMarkup replyMarkup;
	
	public ResetConfirmReply(String userID, String firstname, DeleteType deleteType, String confirm) throws Exception {
		
		String sorryText = "Sorry " + firstname + ", there was a problem to reset your preferences";

		String replyText = "";
		
		Delete deleteService = new Delete();
		ResponseService rs = new ResponseService();

		if (confirm.equals("yes")) {
			String result = "";
			switch (deleteType) {
			case PROPERTIES:
				result = deleteService.deleteAllPropertyRated(userID);
				if (result.equals("0") || result.equals("\"null\"")) {
					replyText = "All right " + firstname + ", I deleted all your preferences";
				} else {
					replyText = "Sorry " + firstname + ", there is a problem to reset all your preferences";
				}
				break;
			case ENTITIES:
				result = deleteService.deleteAllEntityRated(userID);
				if (result.equals("0") || result.equals("\"null\"")) {
					replyText = "All right " + firstname + ", I deleted all your " + rs.getEntityTypeSingularMessage(true) + " preferences";
				} else {
					replyText = "Sorry " + firstname + ", there is a problem to reset all your " + rs.getEntityTypeSingularMessage(true) + " preferences";
				}
				break;
			case ALL:
				result = deleteService.deleteAllProfile(userID);
				if (result.equals("0") || result.equals("\"null\"")) {
					replyText = "All right " + firstname + ", I deleted all your preferences";
				} else {
					replyText = "Sorry " + firstname + ", there is a problem to reset all preferences that you have evaluated";
				}
				break;
			case NONE:
				replyText = sorryText;
			}
			System.out.println("Result: " + result);
			System.out.println("ReplyText: " + replyText);
		} else  {
			replyText = WordUtils.capitalize(firstname) + ", your profile has not been changed";
		}
		
		this.messages = new Message[] {
				new Message(replyText)
		};
		this.replyMarkup = null;
	}

	@Override
	public Message[] getMessages() {
		return messages;
	}

	@Override
	public ReplyMarkup getReplyMarkup() {
		return replyMarkup;
	}
	
	public void addReplyMessages(Reply other) {
		Message[] otherMessages = other.getMessages();
		Message[] totalMessages = new Message[this.messages.length + otherMessages.length];
		for (int i = 0; i < this.messages.length; i++) {
			totalMessages[i] = this.messages[i];
		}
		for (int i = 0; i < otherMessages.length; i++) {
			totalMessages[i + this.messages.length] = otherMessages[i];
		}
		this.messages = totalMessages;
	}
	
	public void setReplyMarkup(ReplyMarkup replyMarkup) {
		this.replyMarkup = replyMarkup;
	}

	@Override
	public AuxAPI getAuxAPI() {
		return null;
	}

}
