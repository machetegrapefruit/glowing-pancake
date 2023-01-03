package datasetcreation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import dialog.DialogState;

public class PreferenceTask implements DatasetPopulationTask {
	private static final Logger LOGGER = Logger.getLogger(PreferenceTask.class.getName());
	private List<DatasetInstance> instances;
	private boolean taskFailed;
	private boolean taskCompleted;
	
	public PreferenceTask() {
		this.instances = new ArrayList<DatasetInstance>();
		this.taskFailed = false;
		this.taskCompleted = false;
	}

	@Override
	public void processMessage(DialogState state, JsonObject response, String userID, String messageID, String message, String intent, String contexts) {
		LOGGER.log(Level.INFO, "Entered processMessage, intent is " + intent);
		if (response.get("failure").getAsBoolean()) {
			this.taskFailed = true;
		}
		switch(intent) {
		case "preference":
		case "request_recommendation - yes_but":
		case "request_recommendation - no_but":
		case "show_profile - delete_preference":
		case "preference - disambiguation":
		case "preference - yes":
		case "preference - no":
		case "request_recommendation - yes_but - disambiguation":
		case "request_recommendation - yes":
		case "request_recommendation - no":
		case "show_profile - delete_preference - disambiguation":
			this.instances.add(new DatasetInstance(userID, messageID, message, intent, contexts, true));
			break;
		default:
			LOGGER.log(Level.INFO, "Task failed!");
			this.taskFailed = true;
			break;
		}
		checkTaskCompleted(state);
	}
	
	private void checkTaskCompleted(DialogState state) {
		if (state.getPendingPreferenceQueue().size() == 0 ||
				(
						state.getPendingPreferenceQueue().size() > 0
						&& state.getPendingPreferenceQueue().getFirst().allDisambiguated()
						&& state.getPendingPreferenceQueue().getFirst().allConfirmed()
						)
				) {
			LOGGER.log(Level.INFO, "Task completed!");
			this.taskCompleted = true;
			if (!this.taskFailed) {
				this.setAllMessagesAsValid();
			}
		}
	}

	@Override
	public List<DatasetInstance> getTrainingInstances() {
		return instances;
	}
	
	public void setTaskFailed(boolean taskFailed) {
		this.taskFailed = taskFailed;
	}
	
	public boolean isTaskFailed() {
		return this.taskFailed;
	}
	
	public boolean isTaskCompleted() {
		return this.taskCompleted;
	}
	
	private void setAllMessagesAsValid() {
		for (DatasetInstance instance: instances) {
			instance.setToCheck(false);
		}
	}

}
