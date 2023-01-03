package dialog;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.gson.JsonObject;

import functions.LogService;

public interface Dialog {
	public ApiAiResponse processMessage(DialogState state, LogService logService, String messageID, QueryResult message);
	public ApiAiResponse getNextPendingTask(DialogState state);
}
