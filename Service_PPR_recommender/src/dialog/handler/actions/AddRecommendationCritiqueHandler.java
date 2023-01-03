package dialog.handler.actions;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.cloud.dialogflow.v2.QueryResult;

import configuration.Configuration;
import dialog.ApiAiResponse;
import dialog.AssignToEntityPolicy;
import dialog.DialogState;
import dialog.Preference;
import dialog.functions.AddPreferenceFunction2;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import functions.EntityService;
import functions.ResponseService;
import functions.ServiceSingleton;
import utils.Alias;
import utils.Candidate;
import utils.MatchedElement;

/**
 * Handles the dialog when the user adds a preference to a recommended entity of the form
 * "I like it, but I don't like <x>", or "I don't like it, but I like <x>".
 * It is activated when the "request_recommendation - yes/no_but" is recognized, and then
 * proceeds to add the rating of the recommended item, and to each of the additional
 * items that are recognized in the message. It then returns a feedback for each of the
 * added ratings.
 * @author Andrea Iovine
 *
 */
public class AddRecommendationCritiqueHandler implements DialogHandler {
	private static final Logger LOGGER = Logger.getLogger(AddRecommendationCritiqueHandler.class.getName());

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("request_recommendation - yes_but")
				|| intentName.equals("request_recommendation - no_but");
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		ResponseService responseService = ServiceSingleton.getResponseService();
		int rating = 0;
		if (result.getIntent().getDisplayName().equals("request_recommendation - yes_but")) {
			rating = 1;
		}
		ApiAiResponse response = new ApiAiResponse();
		if (state.getCurrentRecommendedIndex() != -1) {
			this.clearPendingEvaluationQueue(state);
			LOGGER.log(Level.INFO, "Called addRecommendationCritique, rating is " + rating);
			EntityService es = new EntityService();
			String recommended = es.getCachedRecommendedEntities(userID).get(state.getCurrentRecommendedIndex());
			boolean success = es.addRecommendedEntityPreference(userID, recommended, rating, "user");
			LOGGER.log(Level.INFO, "addRecommendationPreference returned " + success);
			es.setRefine(userID, recommended);
			char ratingSymbol = (rating == 1) ? '+' : '-';
			response.addRecognizedObject(recommended + ratingSymbol);
			String query = result.getQueryText();
			
			String[] stopWords = Configuration.getDefaultConfiguration().getStopWordsForCritiquing();
			dialog.functions.AddPreferenceFunction2.AddPreferenceResponse addPreferenceResponse = new AddPreferenceFunction2().addPreferences(query, userID, stopWords);
			Preference p = addPreferenceResponse.getPreference();
			Alias recMovieAlias = new Alias(recommended, es.getEntityLabel(recommended));
			//Imposto la policy in modo che tutte le propertyType riconosciute siano assegnate all'entità raccomandata
			p.setPropertyTypeAssignationPolicy(new AssignToEntityPolicy(new Candidate(recMovieAlias, 0, 0, rating)));
			
			if (addPreferenceResponse.isSuccess() 
					&& (addPreferenceResponse.getAddedEntities().size() > 0
							|| addPreferenceResponse.getAddedProperties().size() > 0
							|| p.getNextPendingEvaluation() != null)
					        || p.getNextConfirmation() != null) {
				addMatchedElementsInResponse(response, addPreferenceResponse.getAddedEntities(), addPreferenceResponse.getAddedProperties());
				state.getPendingPreferenceQueue().push(addPreferenceResponse.getPreference());
			}
			response.addSpeech(
					responseService.getAddRecommendationPreferencesMessage(
							es.getEntityLabel(recommended), 
							rating,
							addPreferenceResponse.getAddedEntities(), 
							addPreferenceResponse.getAddedProperties())
					);
			response.addEvent("preference");
			response.addEvent("recommendation");
		} else {
			response.addSpeech(responseService.getDefaultFailureMessage());
		}

//		this.checkAndSetNextRecommendedEntity(state, response);
//		ApiAiResponse pendingResponse = getNextPendingTask(true, state);
//		response.merge(pendingResponse);
//		response.setData(pendingResponse.getData());
//		return response;
		return new DefaultHandlerResponse(response, true, true);
	}
	
	/**
	 * Removes all the pending prompts from the preference queue
	 */
	private void clearPendingEvaluationQueue(DialogState state) {
		state.getPendingPreferenceQueue().clear();
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
