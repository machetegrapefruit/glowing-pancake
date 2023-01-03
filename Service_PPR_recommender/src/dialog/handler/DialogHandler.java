package dialog.handler;

import com.google.cloud.dialogflow.v2.QueryResult;

import dialog.ApiAiResponse;
import dialog.DialogState;

/**
 * The DialogHandler classes manage each a particular action requested by the user
 * (usually, one intent -> one handler). When a DialogHandler is activated, it 
 * performs the requested operation, and then returns a response describing what
 * has happened.
 * @author Andrea Iovine
 *
 */
public interface DialogHandler {
	/**
	 * Checks whether the dialog handler should be activated, based on the current state of the dialog
	 * @param userID ID of the user
	 * @param result response from Dialogflow
	 * @param state current dialog state
	 * @param messageID ID of the message
	 * @return true if the handler should be activated
	 */
	public boolean check(String userID, QueryResult result, DialogState state, String messageID);
	
	/**
	 * This method is called when the check method returns true. It returns a feedback message
	 * that describes what has happened.
	 * @param userID ID of the user
	 * @param result response from Dialogflow
	 * @param state current dialog state
	 * @param messageID ID of the message
	 * @return An HandlerResponse object, that contains a feedback for the performed action
	 */
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID);
}
