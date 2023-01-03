package dialog.handler.prompts;

import com.google.cloud.dialogflow.v2.QueryResult;

import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.handler.HandlerResponse;

/**
 * Interface for all classes that will implement a Prompt Handler. Prompt handlers manage 
 * additional messages that can be appended after an action has been taken, e.g. to 
 * prompt the user for the next action. Example of prompts are:
 * 1. Signaling the user the remaining number of preference needed
 *    "I need 2 preferences before you can receive a recommendation"
 * 2. Notifying the user that the recommendation is now available
 * 3. Prompting the user for a disambiguation request
 *    "Which one of these do you like/dislike?"
 * 4. Prompting the user for the confirmation of the association between an entity and a
 *    property type
 *    "You said you like the director of The Matrix, is that correct?"
 * Each prompt handler will be checked using the check method, and if it is activated, it
 * will append its own message to the final response.
 * @author Andrea Iovine
 *
 */
public interface DialogPromptHandler {
	/**
	 * Checks whether the prompt handler should be activated, based on the current state of the dialog
	 * @param userID ID of the user
	 * @param result response from Dialogflow
	 * @param state current dialog state
	 * @param messageID ID of the message
	 * @param actionResponse response obtained from the Dialog Handler classes
	 * @return true if the prompt handler should be activated
	 */
	public boolean check(String userID, QueryResult result, DialogState state, String messageID, HandlerResponse actionResponse);
	
	/**
	 * This method is called when the check method returns true. It appends its own messages to the actionResponse parameter,
	 * and also returns the response (redundant).
	 * @param userID ID of the user
	 * @param result response from Dialogflow
	 * @param state current dialog state
	 * @param messageID ID of the message
	 * @param actionResponse response obtained from the Dialog Handler classes
	 * @return The updated response object
	 */
	public ApiAiResponse handle(String userID, QueryResult result, DialogState state, String messageID, HandlerResponse actionResponse);
}
