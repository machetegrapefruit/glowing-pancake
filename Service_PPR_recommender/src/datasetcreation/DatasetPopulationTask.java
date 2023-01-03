package datasetcreation;

import java.util.List;

import com.google.gson.JsonObject;

import dialog.DialogState;

public interface DatasetPopulationTask {
	public void processMessage(DialogState state, JsonObject response, String userID, String messageID, String message, String intent, String contexts);
	public List<DatasetInstance> getTrainingInstances();
	public boolean isTaskFailed();
	public boolean isTaskCompleted();
	public void setTaskFailed(boolean taskFailed);
}
