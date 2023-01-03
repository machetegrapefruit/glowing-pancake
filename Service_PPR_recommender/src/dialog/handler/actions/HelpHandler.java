package dialog.handler.actions;

import com.google.cloud.dialogflow.v2.QueryResult;

import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import functions.ResponseService;
import functions.ServiceSingleton;

/**
 * Handles the conversation when the user asks for help. It is activated when one of
 * the help intents is recognized.
 * Returns an help message that depends on the current state of the dialog.
 * @author Andrea Iovine
 *
 */
public class HelpHandler implements DialogHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("smalltalk.agent.can_you_help")
				|| intentName.equals("help")
				|| intentName.equals("preference - help")
				|| intentName.equals("request_recommendation - help")
				|| intentName.equals("request_recommendation - critiquing - help")
				|| intentName.equals("show_profile - delete_preference - help")
				|| intentName.equals("show_profile - help");
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		ResponseService responseService = ServiceSingleton.getResponseService();
		boolean showProfile = result.getIntent().getDisplayName().equals("show_profile - help");
		ApiAiResponse response = new ApiAiResponse();
		boolean disambiguation = state.getPendingPreferenceQueue().size() > 0 
				&& !state.getPendingPreferenceQueue().peek().allDisambiguated();
		boolean recommendation = state.getCurrentRecommendedIndex() != -1;
		response.addSpeech(responseService.getHelpMessage(disambiguation, recommendation, showProfile));
		return new DefaultHandlerResponse(response, false, false);
	}

}
