package dialog.handler.actions;

import java.util.List;

import com.google.cloud.dialogflow.v2.QueryResult;

import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.functions.AddRecommendationPreferenceFunction;
import dialog.functions.AddRecommendationPreferenceFunction.AddRecommendationPreferenceResponse;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import functions.EntityService;
import functions.ResponseService;
import functions.ServiceSingleton;
import utils.MatchedElement;

/**
 * Handles the dialog when the user adds a preference to a recommended item, e.g. when the user
 * writes "I like this movie", or "I like it for the director".
 * It is activated when the "request_recommendation - preference" intent is recognized. It then
 * proceeds to add the rating to the recommended item, and to each additional item found in the
 * message.
 * @author Andrea Iovine
 *
 */
public class AddRecommendationPreferenceHandler implements DialogHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("request_recommendation - preference");
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		ResponseService responseService = ServiceSingleton.getResponseService();
		this.clearPendingEvaluationQueue(state);
		String query = result.getQueryText();
		ApiAiResponse response = new ApiAiResponse();
		if (state.getCurrentRecommendedIndex() != -1) {
			AddRecommendationPreferenceResponse arpResponse = 
					new AddRecommendationPreferenceFunction()
					.addRecommendationPreference(query, state.getCurrentRecommendedIndex(), userID);
			if (arpResponse.isSuccess()) {
				String currentEntityURI = new EntityService().getCachedRecommendedEntities(userID).get(state.getCurrentRecommendedIndex());
				response.addRecognizedObject(currentEntityURI + arpResponse.getRatingSymbol());
				List<MatchedElement> addedProperties = arpResponse.getAddedProperties();
				for (MatchedElement property: addedProperties) {
					response.addRecognizedObject(property.getElement().getURI() + property.getRatingSymbol());
				}
				response.addSpeech(responseService.getAddRecommendationPreferencesMessage(
						arpResponse.getLabel(), arpResponse.getRating(), arpResponse.getAddedProperties()));
				//state.setCurrentRecommendedIndex(state.getCurrentRecommendedIndex() + 1);
			} else {
				response.addSpeech(responseService.getDefaultFailureMessage());
			}
		} else {
			response.addSpeech(responseService.getDefaultFailureMessage());
		}

//		this.checkAndSetNextRecommendedEntity(state, response);
//		ApiAiResponse pendingResponse = getNextPendingTask(true, state);
//		response.merge(pendingResponse);
//		response.setData(pendingResponse.getData());
		response.addEvent("preference");
		response.addEvent("recommendation");

		return new DefaultHandlerResponse(response, true, true);
	}
	
	/**
	 * Removes all the pending prompts from the preference queue
	 */
	private void clearPendingEvaluationQueue(DialogState state) {
		state.getPendingPreferenceQueue().clear();
	}

}
