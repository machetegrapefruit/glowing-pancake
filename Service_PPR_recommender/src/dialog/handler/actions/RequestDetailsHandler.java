package dialog.handler.actions;

import java.util.List;

import com.google.cloud.dialogflow.v2.QueryResult;

import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.Preference;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import functions.EntityService;
import functions.ResponseService;
import functions.ServiceSingleton;

/**
 * Handles the dialog when the user requests the details of the currently recommended item.
 * Returns the message containing the details of the currently recommended item.
 * @author Andrea Iovine
 *
 */
public class RequestDetailsHandler implements DialogHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("request_recommendation - details"); 
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		ResponseService responseService = ServiceSingleton.getResponseService();
		ApiAiResponse response = new ApiAiResponse();
		EntityService es = new EntityService();
		List<String> recommendations = es.getCachedRecommendedEntities(userID);

		if (state.getCurrentRecommendedIndex() > recommendations.size() 
				|| state.getCurrentRecommendedIndex() == -1) {
			response.addSpeech(responseService.getRequestDetailsFailureMessage());
		} else {
			//If there is a pending prompt it means that the entity has already been rated
			Preference p = state.getPendingPreferenceQueue().peek();
			if (p != null && (!p.allDisambiguated() || !p.allConfirmed())) {
				response.addSpeech(responseService.getDefaultFailureMessage());
			} else {
				String currentEntityURI = recommendations.get(state.getCurrentRecommendedIndex());
				es.setDetails(userID, currentEntityURI);
				response.addSpeech(es.getEntityDetails(currentEntityURI).toString());
			}
		}
		response.addEvent("recommendation");
		return new DefaultHandlerResponse(response, true, false);
	}

}
