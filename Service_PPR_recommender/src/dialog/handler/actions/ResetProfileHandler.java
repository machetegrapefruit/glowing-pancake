package dialog.handler.actions;

import com.google.cloud.dialogflow.v2.QueryResult;

import configuration.Configuration;
import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import functions.EntityService;
import functions.ProfileService;
import functions.PropertyService;
import functions.ResponseService;
import functions.ServiceSingleton;

/**
 * Handles the dialog when the user asks for his/her profile to be deleted.
 * It is activated when one of the reset_profile intents is recognized.
 * When this happens, the system asks if it should delete the entities, the properties, or both.
 * After one of those options is deleted, it will then proceed to ask for confirmation.
 * After the confirmation, it will then proceed to delete the specified data.
 * @author Andrea Iovine
 *
 */
public class ResetProfileHandler implements DialogHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("reset_profile")
				|| intentName.equals("reset - reset_movies")
				|| intentName.equals("reset - reset_properties")
				|| intentName.equals("reset - reset_everything")
				|| intentName.equals("reset - reset_movies - no")
				|| intentName.equals("reset - reset_properties - no")
				|| intentName.equals("reset - reset_everything - no")
				|| intentName.equals("reset - reset_movies - yes")
				|| intentName.equals("reset - reset_properties - yes")
				|| intentName.equals("reset - reset_everything - yes");
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		this.clearPendingEvaluationQueue(state);
		ResponseService responseService = ServiceSingleton.getResponseService();
		//Reset the recommendation list
		state.setCurrentRecommendedIndex(-1);
		Configuration c = Configuration.getDefaultConfiguration();
		
		ApiAiResponse response = new ApiAiResponse();
		String speech = "";
		String[] resetSplit = result.getIntent().getDisplayName().split("-");
		
		if (resetSplit.length == 1 || resetSplit.length == 2) {
			//User has asked to reset the profile - the agent asks what should be reset, or asks for confirmation
			speech = result.getFulfillmentText();
			response.addSpeech(speech);
			response.addEvent("question");
			return new DefaultHandlerResponse(response, false, false);
		} else {
			//The user said yes or no
			String resetType = resetSplit[1].trim();
			String confirm = resetSplit[2].trim();
			
			boolean done = false;
			if (resetType.equals("reset_movies") && confirm.equals("yes") ) {
				//Reset item suggestion state
				state.resetCurrentSuggestion();
				state.setPreferenceMessagesCount(0);
				EntityService es = new EntityService();
				done = es.deleteAllRatedEntities(userID);
				if (done) {
					response.addSpeech(responseService.getDeletedEntityPreferencesMessage());
				}
			} else if (resetType.equals("reset_properties") && confirm.equals("yes") ) {
				//Reset item suggestion state
				state.resetCurrentSuggestion();
				state.setPreferenceMessagesCount(0);
				PropertyService ps = new PropertyService();
				done = ps.deleteAllRatedProperties(userID);
				if (done) {
					response.addSpeech(responseService.getDeletedPropertyPreferencesMessage());
				}
			} else if (resetType.equals("reset_everything") && confirm.equals("yes") ) {
				//Reset item suggestion state
				state.resetCurrentSuggestion();
				state.setPreferenceMessagesCount(0);
				ProfileService ps = new ProfileService();
				done = ps.deleteUserProfile(userID); 
				if (done) {
					response.addSpeech(responseService.getDeletedProfileMessage());
				}
				state.setMinPreferences(c.getMinPreferencesFirstSession());
			} else if (confirm.equals("no")) {
				done = true;
			}
			
			if (!done) {
				response.addSpeech(responseService.getDeleteProfileFailureMessage());
			}
//			ApiAiResponse pendingResponse = getNextPendingTask(true, state);
//			response.merge(pendingResponse);
//			response.setData(pendingResponse.getData());
//			return response;
			return new DefaultHandlerResponse(response, true, false);
		}
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
