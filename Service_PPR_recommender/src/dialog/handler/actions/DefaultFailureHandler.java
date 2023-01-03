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
 * Handles the conversation in case of a generic error situation, i.e. when the intent was not
 * recognized correctly. Returns a feedback for the error.
 * @author Andrea Iovine
 *
 */
public class DefaultFailureHandler implements DialogHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("request_recommendation - fallback");
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		//Reset suggested item
		state.setEntityToRate(null);
		ApiAiResponse response = new ApiAiResponse();
		ResponseService responseService = ServiceSingleton.getResponseService();
		response.addSpeech(responseService.getDefaultFailureMessage());
		return new DefaultHandlerResponse(response, true, false);
	}

}
