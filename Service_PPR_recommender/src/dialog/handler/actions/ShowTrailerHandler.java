package dialog.handler.actions;

import java.util.List;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.gson.JsonObject;

import configuration.Configuration;
import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.Preference;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import entity.Entity;
import functions.EntityService;
import functions.ResponseService;
import functions.ServiceSingleton;

/**
 * Handles the dialog when the user requests a preview/trailer of the currently recommended item.
 * Returns the message containing the preview/trailer of the currently recommended item.
 * @author Andrea Iovine
 *
 */
public class ShowTrailerHandler implements DialogHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("request_recommendation - trailer");
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		ResponseService responseService = ServiceSingleton.getResponseService();
		Configuration configuration = Configuration.getDefaultConfiguration();
		ApiAiResponse response = new ApiAiResponse();
		//Check if there is a pending prompt
		Preference p = state.getPendingPreferenceQueue().peek();
		if (p != null && (!p.allDisambiguated() || !p.allConfirmed())) {
			//Can't show the trailer when there is a prompt waiting!
			response.addSpeech(responseService.getDefaultFailureMessage());
		} else {
			//We show the trailer message
			EntityService es = new EntityService();
			//Get the current recommended entity
			String currentEntityURI = es.getCachedRecommendedEntities(userID).get(state.getCurrentRecommendedIndex());
			Entity currentEntity = es.getEntityDetails(currentEntityURI);
			//Get the trailer
			List<String> trailer = currentEntity.get(configuration.getPropertyTypeTrailer());
			response.addSpeech(responseService.getShowTrailerMessage(trailer));		
			//Add the link to the trailer
			if (trailer != null) {
				String linkLabel = es.getEntityLabel(currentEntityURI);
				response.addLink(trailer.get(0), linkLabel);
				//response.addSpeech(responseService.getRecommendationReminderMessage(linkLabel));
			}
			response.addContext("request_recommendation-followup", 1);
		}
		response.addEvent("question");
		response.addEvent("recommendation");
		return new DefaultHandlerResponse(response, true, false);
	}

}
