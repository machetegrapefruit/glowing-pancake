package replies;

import java.util.ArrayList;
import java.util.List;

import dialog.DialogState;
import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import functions.ProfileService;
import functions.ResponseService;
import keyboards.RefineEndKeyboard;

public class RefineEndReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
	private AuxAPI auxAPI;
	
	public RefineEndReply(String userID, DialogState state) {
		
		ResponseService responseService = new ResponseService();
		
		int count = (new ProfileService()).getPreferencesCount(userID);
		
		messages = new Message[] {
				new Message("Profile updated with " + count + " properties rated."),
				new Message("Do you prefer to rate other properties of \"" + state.getEntityToRecommend() + "\""
						+ " or back to " + responseService.getEntityTypePluralMessage(true) + "?")
		};
		
		replyMarkup = new ReplyMarkup(new RefineEndKeyboard(state.getEntityToRecommend()));
		
		auxAPI = null;
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
		return auxAPI;
	}

}
