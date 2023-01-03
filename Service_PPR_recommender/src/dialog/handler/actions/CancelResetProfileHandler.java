package dialog.handler.actions;

import com.google.cloud.dialogflow.v2.QueryResult;

import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import functions.ResponseService;
import functions.ServiceSingleton;

/**
 * Handles the dialog when the user wants to abort the reset of the profile.
 * It is activated when the "reset_profile - cancel" intent is recognized.
 * It returns a message confirming that the profile has not been reset.
 * @author Andrea Iovine
 *
 */
public class CancelResetProfileHandler implements DialogHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("reset_profile - cancel");
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		ApiAiResponse response = new ApiAiResponse();
		ResponseService responseService = ServiceSingleton.getResponseService();
		response.addSpeech(result.getFulfillmentText());
		if (state.isNewUser()) {
			state.setNewUser(false);
			response.addSpeech(responseService.getIntroductionMessage());
		}
		return new DefaultHandlerResponse(response, true, false);
	}

}
