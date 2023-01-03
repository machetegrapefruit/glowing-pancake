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
 * Handles the dialog when the user first opens the chatbot.
 * It is activated when the "start" intent is recognized.
 * Returns an introduction message
 * @author Andrea Iovine
 *
 */
public class StartHandler implements DialogHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("start");
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		ApiAiResponse response = new ApiAiResponse();
		ResponseService responseService = ServiceSingleton.getResponseService();
		response.addSpeech(responseService.getIntroductionMessage());
		//Add example
		response.addSpeech(responseService.getPreferenceHintMessage());
		// Show the user's ID number
		response.addSpeech(responseService.getShowIDMessage(userID));
		return new DefaultHandlerResponse(response, false, false);
	}

}
