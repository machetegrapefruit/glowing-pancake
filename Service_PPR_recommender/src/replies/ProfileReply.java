package replies;

import dialog.DialogState;
import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import keyboards.Keyboard;
import keyboards.ProfileKeyboard;

public class ProfileReply implements Reply {

	public enum ProfileType {
		REC,
		RATE
	}
	
	Message[] messages;
	ReplyMarkup replyMarkup;
	
	public ProfileReply(String userID, ProfileType type, DialogState state) throws Exception {
		String text = "";
		
		state.setTraining(false);
		
		Keyboard  keyboard = new ProfileKeyboard(userID, type);
		if (keyboard.getOptions().length == 1) {
			text = "Your profile is empty";
		} else {
			text = "You have rated:";
		}
		this.messages = new Message[] {
				new Message(text)
		};
		this.replyMarkup = new ReplyMarkup(keyboard);
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
