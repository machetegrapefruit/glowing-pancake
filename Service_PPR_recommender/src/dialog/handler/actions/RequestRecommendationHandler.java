package dialog.handler.actions;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.gson.JsonObject;

import configuration.Configuration;
import dialog.ApiAiDialog;
import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.FilterManager;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import functions.AuxiliaryRequestService;
import functions.EntityService;
import functions.ProfileService;
import functions.ResponseService;
import functions.ServiceSingleton;
import utils.PropertyFilter;

/**
 * Handles the dialog when the user requests a new recommendation (e.g. "What can I watch tonight?")
 * It is activated when the "request_recommendation" intent is recognized.
 * It checks if the user has provided the minimum number of preferences:
 * - If it does not, it returns a feedback message asking for more recommendations
 * - If it does, it starts the recommendation phase
 * When the recommendation phase is started, it invokes the Sentiment Analyzer component to check
 * for any filter on the recommendation (e.g. "Can you recommend a comedy movie?"). It then
 * returns a message containing a feedback for the start of the recommendation, as well as
 * an auxiiary API to the GetFirstRecommendation class, in which the interaction will continue.
 * @author Andrea Iovine
 *
 */
public class RequestRecommendationHandler implements DialogHandler {
	private final static Logger LOGGER = Logger.getLogger(RequestRecommendationHandler.class.getName());	
	
	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("request_recommendation");
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		LOGGER.log(Level.INFO, "Called requestRecommendation");
		ResponseService responseService = ServiceSingleton.getResponseService();
		ApiAiResponse response = new ApiAiResponse();
		JsonObject dataObject = new JsonObject();
		String query = result.getQueryText();
		String speech = "";
		boolean nextPendingEvaluation = false;
		boolean setNextRecommendedEntity = false;
		Configuration c = Configuration.getDefaultConfiguration();
		
		//Reset items to rate
		state.setEntityToRate(null);

		try {
			int numberOfPreferences = new ProfileService().getPreferencesCount(userID);
			
			//Check if the user has provided at least three preferences
			if (numberOfPreferences < state.getMinPreferences()) {
				//Remove all pending prompts from the queue
				clearPendingEvaluationQueue(state);
				response.addSpeech(responseService.getNotEnoughPreferencesMessage());
				//Remove the recommendation context
				response.addContext("request_recommendation-followup", 0);
			} else {
				//Remove all pending prompts from the queue
				clearPendingEvaluationQueue(state);
				List<PropertyFilter> filters = FilterManager.getFiltersFromSentence(query);
				//If the user is already in the recommendation phase
				//and there are no filters in the new request
				if (state.getCurrentRecommendedIndex() != -1 && (filters == null || filters.size() == 0)) {
					//We just skip to the next recommended entity
					EntityService es = new EntityService();
					es.skipRecommendedEntity(
							userID,
							es.getCachedRecommendedEntities(userID).get(state.getCurrentRecommendedIndex())
							);
					response.addSpeech(responseService.getSkipMessage(true));
					setNextRecommendedEntity = false;
					nextPendingEvaluation = false;
				} else {
					response.addSpeech(responseService.getRecommendationStartMessage(filters));
					LOGGER.log(Level.INFO, "Received from filter manager: " + filters);
					
					/*
					 * The execution of the recommendation algorithm is postponed by using an
					 * auxiliary API. This allows the user to receive a response before the
					 * recommendation is terminated.
					 * Refer to the restService.GetFirstRecommendation class to continue the flow
					 * of the execution of this code.
					 */
					response.setAuxAPI(new AuxiliaryRequestService().getAuxRequestForRequestRecommendation(userID, messageID, filters));
					LOGGER.log(Level.INFO, "auxAPI is " + dataObject.getAsJsonObject("auxAPI"));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		response.addEvent("recommendation");
		return new DefaultHandlerResponse(response, nextPendingEvaluation, setNextRecommendedEntity);
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
