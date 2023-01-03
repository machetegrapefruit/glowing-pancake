package dialog.handler.actions;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.gson.JsonArray;

import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import functions.ProfileService;
import functions.ResponseService;
import functions.ServiceSingleton;

/**
 * Handles the dialog when the user requests his/her profile.
 * Returns a message containing all the ratings of the user.
 * @author Andrea Iovine
 *
 */
public class ShowProfileHandler implements DialogHandler{

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("show_profile");
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		ResponseService responseService = ServiceSingleton.getResponseService();
		this.clearPendingEvaluationQueue(state);
		state.setCurrentRecommendedIndex(-1);
		ApiAiResponse response = new ApiAiResponse();
		JsonArray profile = new ProfileService().getUserProfile(userID);
		response.addSpeech(responseService.getShowProfileMessage(profile, true, state.isFirstProfile(), true));
		state.setFirstProfile(false);
		return new DefaultHandlerResponse(response, true, false);
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
