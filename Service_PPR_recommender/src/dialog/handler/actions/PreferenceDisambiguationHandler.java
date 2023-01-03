package dialog.handler.actions;

import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.cloud.dialogflow.v2.QueryResult;

import configuration.Configuration;
import dialog.ApiAiDialog;
import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.PendingEvaluation;
import dialog.Preference;
import dialog.functions.AddPreferenceFunction;
import dialog.functions.DeletePreferenceFunction;
import dialog.functions.DisambiguationFunction;
import dialog.functions.SetItemToRateFunction;
import dialog.functions.AddPreferenceFunction.AddPreferenceResponse;
import dialog.functions.DeletePreferenceFunction.DeletePreferenceResponse;
import dialog.functions.DisambiguationFunction.DisambiguationResponse;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import exception.NoItemsToSuggestException;
import functions.ProfileService;
import functions.ResponseService;
import functions.ServiceSingleton;
import utils.MatchedElement;

/**
 * Handles the conversation when the user answers to a disambiguation request.
 * It is activated when one of the disambiguation intents is activated.
 * It then attempts to find the option that was chosen by the user, and then
 * adds/removes the preference for the selected option.
 * If none of the selected options are found, it proceeds to add/remove the preferences
 * found in the current message, in case the user decided to ignore the disambiguation
 * request.
 * @author Andrea Iovine
 *
 */
public class PreferenceDisambiguationHandler implements DialogHandler {
	private final static Logger LOGGER = Logger.getLogger(PreferenceDisambiguationHandler.class.getName());

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		boolean isDisambiguationIntent = 
				intentName.equals("preference - disambiguation")
				|| intentName.equals("preference - disambiguation_fallback")
				|| intentName.equals("request_recommendation - critiquing - fallback")
				|| intentName.equals("show_profile - delete_preference - disambiguation")
				|| intentName.equals("request_recommendation - critiquing - disambiguation");
		Preference p = null;
		if (!state.getPendingPreferenceQueue().isEmpty()) {
			p = state.getPendingPreferenceQueue().getFirst();
		}
		//Force the disambiguation if no intent is recognized and a disambiguation is requested
		boolean isDisambiguationRequested = 
				intentName.equals("Default Fallback Intent") 
				&& p != null
				&& !p.allDisambiguated();
		return isDisambiguationIntent || isDisambiguationRequested;
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		LOGGER.log(Level.INFO, "Called doPreferenceDisambiguation");
		ResponseService responseService = ServiceSingleton.getResponseService();
		ApiAiResponse response = new ApiAiResponse();
		StringJoiner sj = new StringJoiner(", ");
		String query = result.getQueryText();
		//Get the current preference in the preference queue
		Preference currentPreference = state.getPendingPreferenceQueue().peek();
		
		if (currentPreference != null && currentPreference.getNextPendingEvaluation() != null) {
			//Get the next pending prompt from the list
			PendingEvaluation nextEv = currentPreference.getNextPendingEvaluation();
			DisambiguationResponse dResponse = new DisambiguationFunction().doPreferenceDisambiguation(query, userID, currentPreference);
			int numAdded = dResponse.getMatchedEntities().size() + dResponse.getMatchedProperties().size();
			if (dResponse.isSuccess() 
					&& (numAdded > 0 || dResponse.getNewEvaluations().size() > 0)) {
				//If no errors are found and at least one more element has been added or a new disambiguation is required
				//Remove the current prompt
				currentPreference.disambiguate(dResponse);
				if (numAdded > 0) {
					//If at least an item is added, we can notify the user and add the recognized elements in the response
					switch(nextEv.getType()) {
					case NAME_DISAMBIGUATION:
						addMatchedElementsInResponse(response, dResponse.getMatchedEntities(), dResponse.getMatchedProperties());
						response.addSpeech(responseService.getAddedPreferencesMessage(
								dResponse.getMatchedEntities(), 
								dResponse.getMatchedProperties()));
						break;
					case DELETE_NAME_DISAMBIGUATION:
						addMatchedElementsInResponse(response, dResponse.getMatchedEntities(), null);
						response.addSpeech(responseService.getDeletedPreferencesMessage(dResponse.getMatchedEntities()));
						break;
					case PROPERTY_TYPE_DISAMBIGUATION:
						response.addRecognizedObject(nextEv.getElementName().getURI() + nextEv.getRatingSymbol());
						response.addSpeech(responseService.getAddedPreferencesMessage(new MatchedElement(nextEv.getElementName(), nextEv.getRating())));
						break;
					}
				}
			} else {
				//If the disambiguation fails, we try to add the preferences in the user message
				response.setFailure(true);
				ApiAiResponse cdResponse = changeDisambiguation(userID, nextEv, query, state);
				response.merge(cdResponse);
			}
		} else {
			LOGGER.log(Level.INFO, "Received disambiguation input but there is no pending disambiguation!");
			response.addSpeech(responseService.getDefaultFailureMessage());
			response.setFailure(true);
			response.addContext("property_type_disambiguation", 0);
		}
		
		//Set the new item to rate
		try {
			new SetItemToRateFunction().setItemToRate(userID, state, true);
		} catch (NoItemsToSuggestException e) {
			//No more items to suggest, do nothing
		}
			
		return new DefaultHandlerResponse(response, true, true);
	}
	
	private ApiAiResponse changeDisambiguation(String userID, PendingEvaluation currentEv, String message, DialogState state) {
		String[] stopWords = Configuration.getDefaultConfiguration().getStopWordsForPreference();
		ApiAiResponse response = new ApiAiResponse();
		Preference p = state.getPendingPreferenceQueue().peek();
		switch(currentEv.getType()) {
			case PROPERTY_TYPE_DISAMBIGUATION:
			case NAME_DISAMBIGUATION:
				//When changing disambiguation, we force the method to ignore the recommended item mentions
				//This because we assume that the recommended item was rated in an earlier message
				AddPreferenceResponse addPreferenceResponse = new AddPreferenceFunction().addPreferences(message, userID, stopWords, null, null);				
				p.changeDisambiguation(addPreferenceResponse);			
				response.merge(handleAddPreferenceResponse(addPreferenceResponse));
				break;
			case DELETE_NAME_DISAMBIGUATION:
				DeletePreferenceResponse dpResponse = new DeletePreferenceFunction().deletePreferencesInMessage(message, userID);
				p.changeDisambiguation(dpResponse);
				response.merge(handleDeletePreferenceResponse(dpResponse, state));
				break;
		}
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
	
	private ApiAiResponse handleAddPreferenceResponse(AddPreferenceResponse addPreferenceResponse) {
		ResponseService responseService = ServiceSingleton.getResponseService();
		ApiAiResponse response = new ApiAiResponse();
		//Notify the addition of the recognized elements
		addMatchedElementsInResponse(response, addPreferenceResponse.getAddedEntities(), addPreferenceResponse.getAddedProperties());
		response.addSpeech(responseService.getAddPreferenceMessage(addPreferenceResponse));
		return response;
	}
	
	private ApiAiResponse handleDeletePreferenceResponse(DeletePreferenceResponse dpResponse, DialogState state) {
		ApiAiResponse response = new ApiAiResponse();
		ResponseService responseService = ServiceSingleton.getResponseService();

		if (dpResponse.getSuccess()  && 
				(dpResponse.getDeletedElements().size() > 0 
						|| dpResponse.getPreference().getNextPendingEvaluation() != null)) {
			state.getPendingPreferenceQueue().push(dpResponse.getPreference());
			//Notify the deletion of the recognized elements.
			response.addSpeech(responseService.getDeletedPreferencesMessage(dpResponse.getDeletedElements()));
		} else {
			response.addSpeech(responseService.getDefaultFailureMessage());
			response.setFailure(true);
		}
		return response;
	}

}
