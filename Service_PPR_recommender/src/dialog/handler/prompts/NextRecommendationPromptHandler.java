package dialog.handler.prompts;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.gson.JsonObject;

import configuration.Configuration;
import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.Preference;
import dialog.handler.HandlerResponse;
import entity.Entity;
import functions.EntityService;
import functions.ProfileService;
import functions.ResponseService;
import functions.ServiceSingleton;


/**
 * Handles the prompt during the recommendation phase. There are multiple situations:
 * 1. A new recommended item is shown. The title and image of the recommended entity is shown.
 * 2. We are still waiting for the evaluation of the recommended item. In this case, we
 *    show a short reminder (e.g. "So, what do you think of <x>?")
 * 3. The recommended items are finished, in this case, there are multiple sub-cases:
 * 3.1. Normal case, a feedback message is returned, and if active, the questionnaire is started.
 * 3.2. The user skipped all the recommended entities, the refocus message is shown
 * 
 * @author Andrea Iovine
 *
 */
public class NextRecommendationPromptHandler implements DialogPromptHandler {
	private final static Logger LOGGER = Logger.getLogger(NextRecommendationPromptHandler.class.getName());

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID,
			HandlerResponse actionResponse) {
		Preference p = state.getPendingPreferenceQueue().peek();
		return state.getCurrentRecommendedIndex() != -1 
				&& (p == null || (p.allConfirmed() && p.allDisambiguated()));
	}

	@Override
	public ApiAiResponse handle(String userID, QueryResult result, DialogState state, String messageID,
			HandlerResponse actionResponse) {
		EntityService es = ServiceSingleton.getEntityService();
		ResponseService responseService = ServiceSingleton.getResponseService();
		ApiAiResponse response = actionResponse.getResponse();
		List<String> recommended = es.getCachedRecommendedEntities(userID);
		LOGGER.log(Level.INFO, "currentRecommendedIndex is " + state.getCurrentRecommendedIndex() 
				+ ", recommended.size() is " + recommended.size());
		
		if (actionResponse.setNextRecommendedEntity()) {
			state.setCurrentRecommendedIndex(state.getCurrentRecommendedIndex() + 1);
		}
		
		if (state.getCurrentRecommendedIndex() < recommended.size()) {
			//If not all entities have been rated
			if (actionResponse.setNextRecommendedEntity()) {
				//Get the next one
				ApiAiResponse nextMovie = getNextRecommendedEntity(userID, recommended, state);
				response.merge(nextMovie);
				//response.setData(nextMovie.getData());
				response.addEvent("recommendation"); response.addEvent("question");
			} else if (actionResponse.appendNextTaskReminder()) {
				//Get a reminder for the current recommended entity
				response.merge(getRecommendationReminder(state, userID));
				response.addEvent("recommendation"); response.addEvent("question");
			}
			//Add a hint
			this.addHint(response);
		} else {
			ProfileService ps = new ProfileService();
			int numRated = ps.getNumRatedRecommendedEntities(userID);
			boolean doRefocus = false;			// Disabled refocus for the experiment
			if (doRefocus) {
				LOGGER.log(Level.INFO, "User has skipped all recommended movies! Starting refocus...");
				ps.doRefocus(userID);
			} else {
				//Check if the questionnaire should be submitted
				response.addEvent("finished_recommendation");
				boolean doQuestionnaire = Configuration.getDefaultConfiguration().isQuestionnaireEnabled();
				if (doQuestionnaire && ps.checkQuestionnaireConditions(userID)) {
					//Start the questionnaire
					state.setCurrentQuestionIndex(0);
				} else {
					//Notify the end of the recommendation session
					response.addSpeech(responseService.getRecommendationEndedMessage(false));
				}
			}
			//Delete the recommendation context
			//response.addSpeech(responseService.getRecommendationEndedMessage(doRefocus));
			state.setCurrentRecommendedIndex(-1);
			//Ask the user to give more entities
			Configuration c = Configuration.getDefaultConfiguration();
			state.setMinPreferences(state.getMinPreferences() + c.getMinPreferencesSecondSession());
		}
		return response;
	}
	
	public ApiAiResponse getNextRecommendedEntity(String userID, List<String> recommended, DialogState state) {
		ApiAiResponse response = new ApiAiResponse();
		ResponseService responseService = ServiceSingleton.getResponseService();
		
		response.setChangedRecommendedEntity(true);
		LOGGER.log(Level.INFO, "Next recommended entity is " + recommended.get(state.getCurrentRecommendedIndex()));
		EntityService es = ServiceSingleton.getEntityService();
		es.insertEntityToRate(userID, recommended.get(state.getCurrentRecommendedIndex()), state.getCurrentRecommendedIndex());
		Entity e = es.getEntityDetails(recommended.get(state.getCurrentRecommendedIndex()));
		response.addContext("request_recommendation-followup");
		response.addSpeech(responseService.getRecommendedMoviePrefaceMessage());
		//String postImageSpeech = responseService.getRecommendedMovieAskPreferenceMessage(state.isFirstRecommendation());
		String postImageSpeech = responseService.getRecommendedMovieAskPreferenceMessage(false);
		state.setFirstRecommendation(false);
		JsonObject dataObj = new JsonObject();
		Configuration c = Configuration.getDefaultConfiguration();
		List<String> images = e.get(c.getPropertyTypeImage());
		String imageURL;
		String title;
		if (images != null && images.size() > 0) {
			imageURL = images.get(0);
		} else {
			imageURL = "none";
		}
		List<String> titles = e.get(c.getPropertyTypeName());
		if (titles != null && titles.size() > 0) {
			title = titles.get(0);
		} else {
			title = es.getEntityLabel(recommended.get(state.getCurrentRecommendedIndex()));
		}
		response.addImage(imageURL, title);
		response.addSpeech(postImageSpeech);
//		dataObj.addProperty("postImageSpeech", postImageSpeech);
//		response.setData(dataObj);
		return response;
	}
	
	private void addHint(ApiAiResponse response) {
		ResponseService responseService = ServiceSingleton.getResponseService();
		///50% chance of adding a hint
		int random = new Random().nextInt(2);
		if (random == 1) {
			response.addSpeech(responseService.getMultipleMessage("Hint.recommendation"));
		}
	}
	
	private ApiAiResponse getRecommendationReminder(DialogState state, String userID) {
		ApiAiResponse reminder = new ApiAiResponse();
		ResponseService responseService = ServiceSingleton.getResponseService();
		if (state.getCurrentRecommendedIndex() != -1) {
			reminder.addContext("request_recommendation-followup", 1);
			EntityService es = new EntityService();
			List<String> recommendations = es.getCachedRecommendedEntities(userID);
			String currentEntityURI = recommendations.get(state.getCurrentRecommendedIndex());
			reminder.addSpeech(responseService.getRecommendationReminderMessage(es.getEntityLabel(currentEntityURI)));
		}
		reminder.addEvent("recommendation"); reminder.addEvent("question");
		return reminder;
	}

}
