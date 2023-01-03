package dialog.handler;

import dialog.ApiAiResponse;

/**
 * The HandlerResponse classes contain the information produced by a Dialog Handler.
 * @author Andrea Iovine
 *
 */
public interface HandlerResponse {
	/**
	 * Returns the messages that will be shown to the user, along with other information
	 * that can be used for logging purposes
	 * @return An ApiAiResponse object
	 */
	public ApiAiResponse getResponse();
	
	/**
	 * Checks whether the next recommended entity should be presented after the action
	 * has been performed (if possible).
	 * @return true if the dialog manager should proceed to the next recommended entity
	 */
	public boolean setNextRecommendedEntity();
	
	/**
	 * Checks whether a reminder for the next task should be appended in the final response.
	 * This will be used by the prompt handler classes.
	 * @return true if a reminder for the next task should be appended in the final response.
	 */
	public boolean appendNextTaskReminder();
}
