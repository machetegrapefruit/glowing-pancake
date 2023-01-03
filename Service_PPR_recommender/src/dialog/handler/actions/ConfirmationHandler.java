package dialog.handler.actions;

import java.util.List;

import com.google.cloud.dialogflow.v2.QueryResult;

import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.PendingConfirmation;
import dialog.Preference;
import dialog.functions.PropertyTypePreferenceFunction;
import dialog.functions.SetItemToRateFunction;
import dialog.functions.PropertyTypePreferenceFunction.AddPreferenceFromConfirmationResponse;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import exception.NoItemsToSuggestException;
import functions.ResponseService;
import functions.ServiceSingleton;
import utils.MatchedElement;

/**
 * Handles the dialog when the user responds to a confirmation request (e.g. "You said that you
 * like the director of The Matrix, is that correct?").
 * It is activated when one of the confirmation intents is recognized.
 * If the user said yes, it proceeds to add the preference to the recognized items.
 * If the user said no, it removes the association between the entity and the property type.
 * Returns a message containing a feedback of what has happened.
 * @author Andrea Iovine
 *
 */
public class ConfirmationHandler implements DialogHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		String intentName = result.getIntent().getDisplayName();
		return intentName.equals("smalltalk.confirmation.yes")
				|| intentName.equals("preference - yes")
				|| intentName.equals("request_recommendation - critiquing - yes")
				|| intentName.equals("smalltalk.confirmation.no")
				|| intentName.equals("preference - no")
				|| intentName.equals("request_recommendation - critiquing - no");				
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		ResponseService responseService = ServiceSingleton.getResponseService();
		boolean confirm = result.getIntent().getDisplayName().contains("yes");
		ApiAiResponse response = new ApiAiResponse();
		Preference currentPreference = state.getPendingPreferenceQueue().peek();
		PendingConfirmation currentPc = null;
		if (currentPreference != null) {
			currentPc = currentPreference.getNextConfirmation();
		}
		if (currentPc != null) {
			if (confirm) {
				//Remove the confirmation from the queue
				currentPreference.confirmAssignment();
				AddPreferenceFromConfirmationResponse apfcr = 
						new PropertyTypePreferenceFunction()
						.addPreferenceFromConfirmation(userID, currentPc);
				if (apfcr.isSuccess()) {
					response.addSpeech(responseService.getAddedPreferencesMessage(apfcr.getAdded(), null));
					addMatchedElementsInResponse(response, apfcr.getAdded(), null);
				} else {
					response.addSpeech(responseService.getEmptyConfirmationMessage());
				}
			} else {
				response.addSpeech(responseService.getDeniedConfirmationMessage());
				currentPreference.changeConfirmation();
			}
		} else {
			response.addSpeech(result.getFulfillmentText());
		}
		response.addEvent("preference");
		
		//Set the new item to rate
		try {
			new SetItemToRateFunction().setItemToRate(userID, state, true);
		} catch (NoItemsToSuggestException e) {
			//No more items to suggest, do nothing
		}
		
		return new DefaultHandlerResponse(response, true, true);
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
