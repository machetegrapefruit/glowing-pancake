package dialog.handler.actions;

import java.util.List;

import com.google.cloud.dialogflow.v2.QueryResult;

import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.Preference;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import explanationService.Explanation;
import functions.EntityService;
import functions.ResponseService;
import functions.ServiceSingleton;

/**
 * Handles the dialog when the user request an explanation for the currently recommended item
 * Returns a message containing the explanation.
 * @author Andrea Iovine
 *
 */
public class RequestExplanationHandler implements DialogHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("request_recommendation - why");
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		ResponseService responseService = ServiceSingleton.getResponseService();
		ApiAiResponse response = new ApiAiResponse();
		Explanation ex = new Explanation();
		EntityService es = new EntityService();
		List<String> recommendations = es.getCachedRecommendedEntities(userID);
		
		if (state.getCurrentRecommendedIndex() > recommendations.size() || state.getCurrentRecommendedIndex() == -1) {
			response.addSpeech(responseService.getRequestExplanationFailureMessage());
		} else {
			//Cannot request an explanation while there is a prompt pending
			Preference p = state.getPendingPreferenceQueue().peek();
			if (p != null && (!p.allDisambiguated() || !p.allConfirmed())) {
				response.addSpeech(responseService.getDefaultFailureMessage());
			} else {
				String currentEntityURI = recommendations.get(state.getCurrentRecommendedIndex());
				es.setWhy(userID, currentEntityURI);
				try {
					response.addSpeech(ex.getExplanation(String.valueOf(userID), currentEntityURI));
				} catch (Exception e) {
					System.out.println("Cannot obtain an explanation");
					e.printStackTrace();
				} 
			}
		}
		
		response.addEvent("recommendation");
		return new DefaultHandlerResponse(response, true, false);
	}

}
