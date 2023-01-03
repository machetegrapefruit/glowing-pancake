package dialog.handler.prompts;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.google.cloud.dialogflow.v2.QueryResult;

import configuration.Configuration;
import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.ItemSuggestion;
import dialog.Preference;
import dialog.handler.HandlerResponse;
import entity.Entity;
import functions.EntityService;
import functions.ProfileService;
import functions.PropertyService;
import functions.ResponseService;
import functions.ServiceSingleton;

public class ItemToRatePrompt implements DialogPromptHandler {
	
	private static final Logger LOGGER = Logger.getLogger(ItemToRatePrompt.class.getName());

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID,
			HandlerResponse actionResponse) {
		String intentName = result.getIntent().getDisplayName();
		Preference p = state.getPendingPreferenceQueue().peek();
	
		//Do not show the prompt when these intents have been called
		boolean isIgnoredIntent = intentName.contains("reset")
				|| intentName.contains("request_recommendation");
		return  !isIgnoredIntent
				&& state.getCurrentRecommendedIndex() == -1 
				&& state.getCurrentSuggestion() != null
				&& (p == null || (p.allConfirmed() && p.allDisambiguated()));
	}

	@Override
	public ApiAiResponse handle(String userID, QueryResult result, DialogState state, String messageID,
			HandlerResponse actionResponse) {
		ApiAiResponse response = actionResponse.getResponse();
		ResponseService responseService = ServiceSingleton.getResponseService();
		ItemSuggestion suggestion = state.getCurrentSuggestion();
		LOGGER.info("suggestion.getSuggestedCount is " + suggestion.getRatedCount());
		if (suggestion.isAsked()) {
			LOGGER.info("suggestion.isAsked is true");
			//We remind the user to evaluate the suggested item
			response.addSpeech(responseService.getEntityRatingReminderMessage(suggestion.getLabel()));
		} else {
			LOGGER.info("suggestion.isAsked is false");
			if (suggestion.getSuggestedCount() == 1) {
				//This is the first suggested item, and we didn't ask it already
				//Show the "start suggestion phase" message
				response.addSpeech(responseService.getStartSuggestionMessage());
			}
			
			//We propose a new suggested item
			EntityService es = ServiceSingleton.getEntityService();
			Entity e = es.getEntityDetails(suggestion.getID());
			String caption = responseService.getEntityRatingPromptMessage(suggestion.getLabel());
			Configuration c = Configuration.getDefaultConfiguration();
			List<String> images = e.get(c.getPropertyTypeImage());
			String imageURL = null;
			if (images != null && images.size() > 0) {
				imageURL = images.get(0);
			} else {
				imageURL = "none";
			}
			
			//Set the appropriate event
			response.addEvent("question");
			response.addEvent("suggestion");
			response.addImage(imageURL, caption);
		}
	
		//Add a hint
		addHint(response);
		
		//Signal that the item was suggested to the user
		suggestion.setAsked(true);
		return response;
	}
	
	private void addHint(ApiAiResponse response) {
		ResponseService responseService = ServiceSingleton.getResponseService();
		//50% chance of adding a hint
		int random = new Random().nextInt(2);
		if (random == 1) {
			response.addSpeech(responseService.getSuggestionHintMessage());
		}
	}

}
