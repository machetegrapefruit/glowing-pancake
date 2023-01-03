package dialog.handler.actions;

import com.google.cloud.dialogflow.v2.QueryResult;

import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.functions.DeletePreferenceFunction;
import dialog.functions.DeletePreferenceFunction.DeletePreferenceResponse;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import functions.ResponseService;
import functions.ServiceSingleton;

/**
 * Handles the dialog when the user asks to delete a preference from the profile.
 * It is activated when the "show_profile - delete_preference" intent is recognized.
 * It then proceeds to invoke the Sentiment Analyzer component to find all the items
 * mentioned in the message, and then deletes them from the profile.
 * Returns a feedback message containing the items that were deleted.
 * @author Andrea Iovine
 *
 */
public class DeletePreferenceHandler implements DialogHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("show_profile - delete_preference");
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		this.clearPendingEvaluationQueue(state);
		String query = result.getQueryText();
		ApiAiResponse response = new ApiAiResponse();
		DeletePreferenceResponse dpResponse = new DeletePreferenceFunction().deletePreferencesInMessage(query, userID);
		if (dpResponse.getSuccess()  && 
				(dpResponse.getDeletedElements().size() > 0 
						|| dpResponse.getPreference().getNextPendingEvaluation() != null)) {
			state.getPendingPreferenceQueue().push(dpResponse.getPreference());
		}
		
		response.merge(handleDeletePreferenceResponse(dpResponse, state));
		response.addEvent("preference");
		return new DefaultHandlerResponse(response, true, false);
	}
	
	private ApiAiResponse handleDeletePreferenceResponse(DeletePreferenceResponse dpResponse, DialogState state) {
		ResponseService responseService = ServiceSingleton.getResponseService();
		ApiAiResponse response = new ApiAiResponse();
		if (dpResponse.getSuccess()  && 
				(dpResponse.getDeletedElements().size() > 0 
						|| dpResponse.getPreference().getNextPendingEvaluation() != null)) {
			state.getPendingPreferenceQueue().push(dpResponse.getPreference());
			//Notify the user of the deletion
			response.addSpeech(responseService.getDeletedPreferencesMessage(dpResponse.getDeletedElements()));
		} else {
			response.addSpeech(responseService.getDefaultFailureMessage());
			response.setFailure(true);
		}
		return response;
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
