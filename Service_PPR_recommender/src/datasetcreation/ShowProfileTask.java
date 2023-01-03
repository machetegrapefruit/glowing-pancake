package datasetcreation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import dialog.DialogState;

public class ShowProfileTask implements DatasetPopulationTask {
	private static final Logger LOGGER = Logger.getLogger(ShowProfileTask.class.getName());
	private List<DatasetInstance> instances;
	private boolean taskFailed;
	private boolean taskCompleted;
	
	public ShowProfileTask() {
		this.instances = new ArrayList<DatasetInstance>();
		this.taskFailed = false;
		this.taskCompleted = false;
	}
	
	@Override
	public void processMessage(DialogState state, JsonObject response, String userID, String messageID, String message,
			String intent, String contexts) {
		// TODO Auto-generated method stub
		LOGGER.log(Level.INFO, "Entered processMessage, intent is " + intent);
		if (response.get("failure").getAsBoolean()) {
			this.taskFailed = true;
		}
		switch(intent) {
		case "show_profile":
			this.instances.add(new DatasetInstance(userID, messageID, message, intent, contexts, false));
			this.taskCompleted = true;
			break;
		default:
			this.taskFailed = true;
			this.taskCompleted = true;
			break;
		}
	}

	@Override
	public List<DatasetInstance> getTrainingInstances() {
		return this.instances;
	}

	@Override
	public boolean isTaskFailed() {
		return this.taskFailed;
	}

	@Override
	public boolean isTaskCompleted() {
		return this.taskCompleted;
	}

	@Override
	public void setTaskFailed(boolean taskFailed) {
		this.taskFailed = taskFailed;
	}
	
}
