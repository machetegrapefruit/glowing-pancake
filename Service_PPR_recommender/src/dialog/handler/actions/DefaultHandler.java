package dialog.handler.actions;

import com.google.cloud.dialogflow.v2.QueryResult;

import dialog.ApiAiResponse;
import dialog.DialogState;
import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;

/**
 * Handles the dialog when none of the other Handler classes is activated. When this happens, this 
 * class simply forwards the fulfillment message produced by Dialogflow.
 * @author Andrea Iovine
 *
 */
public class DefaultHandler implements DialogHandler {

	@Override
	public boolean check(String userID, QueryResult result, DialogState state, String messageID) {
		return true;
	}

	@Override
	public HandlerResponse handle(String userID, QueryResult result, DialogState state, String messageID) {
		//Reset suggested item
		state.setEntityToRate(null);
		ApiAiResponse response = new ApiAiResponse();
		response.addSpeech(result.getFulfillmentText());
		if (state.isNewUser()) {
			state.setNewUser(false);
		}
		return new DefaultHandlerResponse(response, true, false);
	}

}
