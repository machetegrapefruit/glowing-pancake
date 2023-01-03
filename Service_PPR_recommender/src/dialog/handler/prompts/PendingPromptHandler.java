package dialog.handler.prompts;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.WordUtils;

import com.google.cloud.dialogflow.v2.QueryResult;

import configuration.Configuration;
import dialog.ApiAiDialog;
import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.PendingConfirmation;
import dialog.PendingEvaluation;
import dialog.Preference;
import dialog.handler.HandlerResponse;
import entity.ReplyMarkup;
import functions.ResponseService;
import functions.ServiceSingleton;
import keyboards.CustomKeyboard;
import keyboards.KeyboardMarkup;
import utils.Alias;

/**
 * Handles the prompt message to be shown when there is a preference pending. This happens when:
 * 1. The system has to request a disambiguation (e.g. "Which one of these do you like?")
 * 2. A confirmation is required for an entity-property type association
 * @author Andrea Iovine
 *
 */
public class PendingPromptHandler implements DialogPromptHandler {
	private final static Logger LOGGER = Logger.getLogger(PendingPromptHandler.class.getName());

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID,
			HandlerResponse actionResponse) {
		Preference p = state.getPendingPreferenceQueue().peek();
		return p != null 
				&& (!p.allDisambiguated() || !p.allConfirmed());
	}

	@Override
	public ApiAiResponse handle(String userID, QueryResult result, DialogState state, String messageID,
			HandlerResponse actionResponse) {
		Preference p = state.getPendingPreferenceQueue().peek();
		ApiAiResponse response = actionResponse.getResponse();
		if (!p.allDisambiguated()) {
			LOGGER.log(Level.INFO, "pendingEvaluationsQueue is not empty, getting next evaluation");
			//Some items still need to be disambiguated
			response.addEvent("question"); 
			response.addEvent("preference"); 
			response.addEvent("disambiguation");
			response.merge(getNextPendingEvaluationMessage(state));
		} else if (!p.allConfirmed()) {
			LOGGER.log(Level.INFO, "pendingConfirmationsQueue is not empty, getting next confirmation request");
			response.addEvent("question"); response.addEvent("preference"); response.addEvent("disambiguation");
			response.addSpeech(getNextPendingConfirmationMessage(state));
		}
		return response;

	}
	
	/**
	 * Returns a text message prompting the user to perform a disambiguation
	 * @return
	 */
	private ApiAiResponse getNextPendingEvaluationMessage(DialogState state) {
		ApiAiResponse response = new ApiAiResponse();
		String interactionMode = Configuration.getDefaultConfiguration().getInteractionType();
		ResponseService responseService = ServiceSingleton.getResponseService();
		PendingEvaluation nextEv = state.getPendingPreferenceQueue().peek().getNextPendingEvaluation();
		response.addSpeech(responseService.getPendingEvaluationMessage(nextEv));

		//If the interactionMode is set to "mixed"
		if (interactionMode.equals("mixed")) {
			String[] buttons = new String[nextEv.getPossibleValues().size()];
			int i = 0;
			for (Alias elem: nextEv.getPossibleValues()) {
				String label = elem.getLabel();
				buttons[i] = WordUtils.capitalize(label);
				i++;
			}
			response.setReplyMarkup(new ReplyMarkup(new CustomKeyboard(buttons)));
		}
		
		if (state.isFirstDisambiguation()) {
			//Show the help message if this is the first time disambiguating
			state.setFirstDisambiguation(false);
			response.addSpeech(responseService.getHelpMessage(true, false, false));
		}
		
		return response;
	}
	
	private String getNextPendingConfirmationMessage(DialogState state) {
		ResponseService responseService = ServiceSingleton.getResponseService();
		PendingConfirmation pc = state.getPendingPreferenceQueue().peek().getNextConfirmation();
		return responseService.getNextConfirmationMessage(pc);
	}
}
