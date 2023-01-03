package dialog.functions;

import java.util.logging.Logger;

import configuration.Configuration;
import dialog.DialogState;
import dialog.Preference;
import dialog.handler.prompts.ItemToRatePrompt;
import exception.NoItemsToSuggestException;
import functions.ProfileService;
import functions.ServiceSingleton;
import functions.elicitation.ActLearnFactory;
import functions.elicitation.ActLearnInterface;

public class SetItemToRateFunction {
	
	private static final Logger LOGGER = Logger.getLogger(SetItemToRateFunction.class.getName());

	/**
	 * Modifies the dialog state by setting an item to be rated. The item suggestion phase
	 * starts after the free text preference phase. In particular, after the user has provided
	 * a certain number of free text ratings (specified in the configuration file).
	 * One after another, a certain number of items will be suggested to the user (also specified
	 * in the configuration file).
	 * @param userID
	 * @param state
	 * @param advance True if the suggestion counter must be increased (the suggested item was rated by the user)
	 * @throws NoItemsToSuggestException 
	 */
	public void setItemToRate(String userID, DialogState state, boolean advance) throws NoItemsToSuggestException {
		//If we're not in the recommendation phase
		Preference p = state.getPendingPreferenceQueue().peek();
		if (state.getCurrentRecommendedIndex() == -1 && (p == null || (p.allConfirmed() && p.allDisambiguated()))) {
			//If all disambiguations have been resolved
			//Reset the item to be rated
			state.setSuggestion(null, false);
			//int numPreferences = state.getPreferenceMessagesCount();
			ProfileService ps = ServiceSingleton.getProfileService();
			int numPreferences = ps.getPreferencesCount(userID);
			//If we have collected at least three preferences from the initial cold-start phase
			//We can start asking the user to rate some items
			if (numPreferences >= Configuration.getDefaultConfiguration().getNumFreeTextPreferences()) {
				getItemToRate(userID, state, advance);
			}
		}
	}
	
	private void getItemToRate(String userID, DialogState state, boolean advance) throws NoItemsToSuggestException {
		int ratedSuggestedItemsCount = state.getRatedSuggestedItemsCount();
		LOGGER.info("state.getRatedSuggestedItemsCount is " + state.getRatedSuggestedItemsCount());
		
		if (ratedSuggestedItemsCount < Configuration.getDefaultConfiguration().getNumSuggestedItems()) {
			ActLearnInterface actLearn = ActLearnFactory.getFunction(userID);
			String newSuggestion = actLearn.GetEntityToRate(userID);
			if (newSuggestion != null) {
				state.setSuggestion(actLearn.GetEntityToRate(userID), advance);
			} else {
				//We cannot suggest any more items
				state.setSuggestion(null, false);
				throw new NoItemsToSuggestException();
			}
			LOGGER.info("state.getRatedSuggestedItemsCount is " + state.getRatedSuggestedItemsCount());
		} else {
			//Enough suggestions have been made
			//Set the suggestion to null without resetting the suggestion counter
			state.setSuggestion(null, false);
		}
	}
}
