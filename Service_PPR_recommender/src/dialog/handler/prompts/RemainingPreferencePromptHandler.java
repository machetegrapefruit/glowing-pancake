package dialog.handler.prompts;

import com.google.cloud.dialogflow.v2.QueryResult;

import configuration.Configuration;
import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.Preference;
import dialog.handler.HandlerResponse;
import functions.ProfileService;
import functions.ResponseService;
import functions.ServiceSingleton;

/**
 * Handles the prompt message for when there are no pending preferences or recommendations.
 * It can show the number of remaining preferences needed to activate the recommendation,
 * or notifies the user that the recommendation is now available.
 * @author isz_d
 *
 */
public class RemainingPreferencePromptHandler implements DialogPromptHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID,
			HandlerResponse actionResponse) {
		Preference p = state.getPendingPreferenceQueue().peek();
		boolean addReminder = actionResponse.appendNextTaskReminder();
		return actionResponse.appendNextTaskReminder()
				&& state.getCurrentRecommendedIndex() == -1 
				&& state.getCurrentSuggestion() == null
				&& state.getCurrentQuestionIndex() == -1
				&& (p == null || (p.allConfirmed() && p.allDisambiguated()))
				&& addReminder;
	}

	/*
	 * Returns a message containing the number of preferences needed before the recommendation function
	 * can be activated, or tells the user that the recommendation function is now available.
	 */
	@Override
	public ApiAiResponse handle(String userID, QueryResult result, DialogState state, String messageID,
			HandlerResponse actionResponse) {
		ApiAiResponse response = actionResponse.getResponse();
		ResponseService responseService = ServiceSingleton.getResponseService();
		ProfileService profileService = ServiceSingleton.getProfileService();
		int numPreferences = profileService.getPreferencesCount(userID);
		int minPreferences = state.getMinPreferences();
		//int numPreferences = state.getPreferenceMessagesCount();
		response.addSpeech(responseService.getRemainingPreferencesMessage(numPreferences, minPreferences));
		//Add hint
		if (numPreferences < Configuration.getDefaultConfiguration().getNumFreeTextPreferences()) {
			response.addSpeech(responseService.getPreferenceHintMessage());
		}
		return response;
	}
}
