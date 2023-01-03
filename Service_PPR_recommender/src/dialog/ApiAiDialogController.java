package dialog;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.gson.JsonObject;

import functions.LogService;

public class ApiAiDialogController implements DialogController {
	//private Map<String, Dialog> dialogs;
	
	public ApiAiDialogController() {
		//this.dialogs = new HashMap<String, Dialog>();
	}
	
	public ApiAiResponse processMessage(DialogState state, LogService logService, String messageID, QueryResult message, String sessionID) {
		System.out.println("Called ApiAiDialogController.processMessage");
		//String sessionPath = message.get("sessionId").getAsString();
		/* sessionId può avere due forme: può essere solamente l'id della componente both, oppure un path
		 * con formato <session id di df>/<id del bot>.
		 * In ogni caso, serve l'id del bot, che sta sempre in ultima posizione nel path
		*/
		//String[] sessionPathSplit = sessionPath.split("/");
		//String sessionId = sessionPathSplit[sessionPathSplit.length - 1];
		/*if (!dialogs.containsKey(sessionId)) {
			dialogs.put(sessionId, new ApiAiDialog(Integer.parseInt(sessionId)));
		}
		Dialog d = dialogs.get(sessionId);*/
		Dialog d = new ApiAiDialog(sessionID);
		ApiAiResponse response = d.processMessage(state, logService, messageID, message);		
		return response;
	}
	
	/*public int getCurrentRecommendedIndex(String userID) {
		return dialogs.get(userID).getCurrentRecommendedIndex();
	}*/
	
	public Dialog getDialog(String sessionId) {
		return new ApiAiDialog(sessionId);
	}
	
	/*public void setCurrentRecommendedIndex(String userID, int index) {
		dialogs.get(userID).setCurrentRecommendedIndex(index);
	}*/
}
