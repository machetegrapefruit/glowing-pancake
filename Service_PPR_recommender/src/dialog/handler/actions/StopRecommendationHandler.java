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
 * Handles the dialog when the user asks to stop the recommendation phase.
 * Returns a feedback message.
 * @author Andrea Iovine
 *
 */
public class StopRecommendationHandler implements DialogHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("request_recommendation - stop");
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		ApiAiResponse response = new ApiAiResponse();
		ResponseService responseService = ServiceSingleton.getResponseService();
		boolean success = false;
		
		if (state.getCurrentRecommendedIndex() != -1) {
			success = true;
			clearPendingEvaluationQueue(state);
			state.setCurrentRecommendedIndex(-1);
		}
		response.addSpeech(responseService.getStopRecommendationsMessage(success));
		response.addEvent("recommendation");
		return new DefaultHandlerResponse(response, false, false);
	}
	
	/**
	 * Removes all the pending prompts from the preference queue
	 */
	private void clearPendingEvaluationQueue(DialogState state) {
		state.getPendingPreferenceQueue().clear();
		//Reset suggested item
		state.setEntityToRate(null);
	}

}
