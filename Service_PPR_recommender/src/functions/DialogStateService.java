package functions;

import com.google.gson.JsonParser;

import dialog.DialogState;
import graph.AdaptiveSelectionController;

public class DialogStateService {
	public DialogState getDialogState(String userID) {
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		String state = asController.getDialogState(userID);
		JsonParser parser = new JsonParser();
		if (state != null) {
			return DialogState.fromJson(parser.parse(state));
		}
		return null;
	}
	
	public void saveDialogState(String userID, DialogState state) {
		String stateStr = state.toJson().toString();
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		asController.setDialogState(userID, stateStr);
	}
}
