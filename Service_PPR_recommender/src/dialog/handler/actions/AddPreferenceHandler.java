package dialog.handler.actions;

import java.util.List;

import com.google.cloud.dialogflow.v2.QueryResult;

import configuration.Configuration;
import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.Preference;
import dialog.functions.AddPreferenceFunction;
import dialog.functions.AddPreferenceFunction.AddPreferenceResponse;
import dialog.functions.SetItemToRateFunction;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import exception.NoItemsToSuggestException;
import functions.ActLearnService;
import functions.ActLearnService.MakinAct;
import functions.EntityService;
import functions.ProfileService;
import functions.ServiceSingleton;
import functions.elicitation.ActLearnFactory;
import functions.elicitation.ActLearnInterface;
import utils.MatchedElement;

/**
 * Handles the dialog for when the user wants to add a new preference to the system
 * (e.g. with the sentence "I like The Matrix, but I hate Keanu Reeves").
 * This class is activated when the "preference" intent has been recognized.
 * Upon activation, this class invokes the Sentiment Analyzer component to retrieve
 * all the recognized ratings, and attempts to add them to the user profile.
 * It will then return a message containing a feedback for each added element. 
 * 
 * @author Andrea Iovine
 *
 */
public class AddPreferenceHandler implements DialogHandler {

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		ApiAiResponse response = new ApiAiResponse();
		String query = result.getQueryText();
		EntityService es = new EntityService();
		String currentRecommendedEntity = null;
		
		//Get the correct stop words depending on whether we are in the recommendation phase or not
		String[] stopWords = Configuration.getDefaultConfiguration().getStopWordsForPreference();
		if (state.getCurrentRecommendedIndex() != -1) {
			stopWords = Configuration.getDefaultConfiguration().getStopWordsForCritiquing();
			currentRecommendedEntity = es.getCachedRecommendedEntities(userID).get(state.getCurrentRecommendedIndex());
		} else if (state.getCurrentSuggestion() != null) {
			stopWords = Configuration.getDefaultConfiguration().getStopWordsForCritiquing();
		}
		
		boolean nextRating = true;		//True if after the preference we can go to the next recommended item or item to rate
		AddPreferenceResponse addPreferenceResponse = new AddPreferenceFunction().addPreferences(query, userID, stopWords, currentRecommendedEntity, state.getCurrentSuggestion());
		if (addPreferenceResponse.isSuccess() 
				&& (addPreferenceResponse.getAddedEntities().size() > 0
						|| addPreferenceResponse.getAddedProperties().size() > 0
						|| addPreferenceResponse.getPreference().getNextPendingEvaluation() != null
						|| addPreferenceResponse.getPreference().getNextConfirmation() != null)) {
			state.getPendingPreferenceQueue().push(addPreferenceResponse.getPreference());
			//Increase the preference messages counter
			state.setPreferenceMessagesCount(state.getPreferenceMessagesCount() + 1);
		} else {
			//No ratings were added and no prompts are generated
			//Signal that we do not want to get the next item to rate
			nextRating = false;
			if (!addPreferenceResponse.isSuccess()) {
				response.setFailure(true);
			}
		}
		
		//Set the new item to rate
		try {
			new SetItemToRateFunction().setItemToRate(userID, state, true);
		} catch (NoItemsToSuggestException e) {
			//No more items to suggest, do nothing
		}
		
		response.merge(handleAddPreferenceResponse(addPreferenceResponse));
		response.addEvent("preference");
		return new DefaultHandlerResponse(response, true, nextRating);
	}
	
	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("preference") 
				|| intentName.equals("smalltalk.user.loves_agent")
				|| intentName.equals("smalltalk.user.likes_agent")
				|| intentName.equals("request_recommendation - preference")
				|| intentName.equals("request_recommendation - yes_but")
				|| intentName.equals("request_recommendation - no_but");
	}
	
	/**
	 * Removes all the pending prompts from the preference queue
	 */
	private void clearPendingEvaluationQueue(DialogState state) {
		state.getPendingPreferenceQueue().clear();
	}
	
	private ApiAiResponse handleAddPreferenceResponse(AddPreferenceResponse addPreferenceResponse) {
		ApiAiResponse response = new ApiAiResponse();
		//Notify all the added items in the response
		addMatchedElementsInResponse(response, addPreferenceResponse.getAddedEntities(), addPreferenceResponse.getAddedProperties());
		response.addSpeech(ServiceSingleton.getResponseService().getAddPreferenceMessage(addPreferenceResponse));
		return response;
	}
	
	private void addMatchedElementsInResponse(ApiAiResponse response, List<MatchedElement> addedEntities, List<MatchedElement> addedProperties) {
		for (MatchedElement added: addedEntities) {
			response.addRecognizedObject(added.getElement().getURI() + added.getRatingSymbol());
		}
		if (addedProperties != null) {
			for (MatchedElement added: addedProperties) {
				response.addRecognizedObject(added.getElement().getURI() + added.getRatingSymbol());
			}
		}
	}

}
