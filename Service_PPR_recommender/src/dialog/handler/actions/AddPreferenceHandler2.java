//package dialog.handler.actions;
//
//import java.util.List;
//
//import com.google.cloud.dialogflow.v2.QueryResult;
//
//import configuration.Configuration;
//import dialog.ApiAiResponse;
//import dialog.DialogState;
//import dialog.functions.AddPreferenceFunction;
//import dialog.functions.AddPreferenceFunction.AddPreferenceResponse;
//import dialog.functions.AddPreferenceFunction2;
//import dialog.handler.DefaultHandlerResponse;
//import dialog.handler.DialogHandler;
//import dialog.handler.HandlerResponse;
//import functions.ServiceSingleton;
//import utils.MatchedElement;
//
///**
// * Handles the dialog for when the user wants to add a new preference to the system
// * (e.g. with the sentence "I like The Matrix, but I hate Keanu Reeves").
// * This class is activated when the "preference" intent has been recognized.
// * Upon activation, this class invokes the Sentiment Analyzer component to retrieve
// * all the recognized ratings, and attempts to add them to the user profile.
// * It will then return a message containing a feedback for each added element. 
// * 
// * @author Andrea Iovine
// *
// */
//public class AddPreferenceHandler2 implements DialogHandler {
//
//	@Override
//	public HandlerResponse handle(int userID, QueryResult result, DialogState state, String messageID) {
//		this.clearPendingEvaluationQueue(state);
//		state.setCurrentRecommendedIndex(-1);
//		ApiAiResponse response = new ApiAiResponse();
//		String query = result.getQueryText();
//		
//		String[] stopWords = Configuration.getDefaultConfiguration().getStopWordsForPreference();
//		dialog.functions.AddPreferenceFunction2.AddPreferenceResponse addPreferenceResponse = new AddPreferenceFunction2().addPreferences(query, userID, stopWords);
//		if (addPreferenceResponse.isSuccess() 
//				&& (addPreferenceResponse.getAddedEntities().size() > 0
//						|| addPreferenceResponse.getAddedProperties().size() > 0
//						|| addPreferenceResponse.getPreference().getNextPendingEvaluation() != null)) {
//			state.getPendingPreferenceQueue().push(addPreferenceResponse.getPreference());
//		} else if (!addPreferenceResponse.isSuccess()) {
//			response.setFailure(true);
//		}
//		response.merge(handleAddPreferenceResponse(addPreferenceResponse));
//		response.addEvent("preference");
//		return new DefaultHandlerResponse(response, true, false);
//	}
//
//	@Override
//	public boolean check(int userID, QueryResult result, DialogState state, String messageID) {
//		String intentName = result.getIntent().getDisplayName();
//		return intentName.equals("preference") 
//				|| intentName.equals("smalltalk.user.loves_agent")
//				|| intentName.equals("smalltalk.user.likes_agent");
//	}
//	
//	/**
//	 * Removes all the pending prompts from the preference queue
//	 */
//	private void clearPendingEvaluationQueue(DialogState state) {
//		state.getPendingPreferenceQueue().clear();
//	}
//	
//	private ApiAiResponse handleAddPreferenceResponse(dialog.functions.AddPreferenceFunction2.AddPreferenceResponse addPreferenceResponse) {
//		ApiAiResponse response = new ApiAiResponse();
//		//Notify all the added items in the response
//		addMatchedElementsInResponse(response, addPreferenceResponse.getAddedEntities(), addPreferenceResponse.getAddedProperties());
//		response.addSpeech(ServiceSingleton.getResponseService().getAddPreferenceMessage(addPreferenceResponse));
//		return response;
//	}
//	
//	private void addMatchedElementsInResponse(ApiAiResponse response, List<MatchedElement> addedEntities, List<MatchedElement> addedProperties) {
//		for (MatchedElement added: addedEntities) {
//			response.addRecognizedObject(added.getElement().getURI() + added.getRatingSymbol());
//		}
//		if (addedProperties != null) {
//			for (MatchedElement added: addedProperties) {
//				response.addRecognizedObject(added.getElement().getURI() + added.getRatingSymbol());
//			}
//		}
//	}
//
//}
