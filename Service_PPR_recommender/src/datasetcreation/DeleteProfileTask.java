package datasetcreation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import dialog.DialogState;

public class DeleteProfileTask implements DatasetPopulationTask {
	private static final Logger LOGGER = Logger.getLogger(DeleteProfileTask.class.getName());
	private List<DatasetInstance> instances;
	private boolean taskFailed;
	private boolean taskCompleted;
	
	public DeleteProfileTask() {
		this.instances = new ArrayList<DatasetInstance>();
		this.taskFailed = false;
		this.taskCompleted = false;
	}
	
	@Override
	public void processMessage(DialogState state, JsonObject response, String userID, String messageID, String message,
			String intent, String contexts) {
		LOGGER.log(Level.INFO, "Entered processMessage, intent is " + intent);
		switch(intent) {
		case "reset_profile":
		case "reset - reset_movies":
		case "reset - reset_properties":
		case "reset - reset_everything":
			this.instances.add(new DatasetInstance(userID, messageID, message, intent, contexts, true));
			break;
		case "reset - reset_movies - yes":
		case "reset - reset_properties - yes":
		case "reset - reset_everything - yes":
			this.instances.add(new DatasetInstance(userID, messageID, message, intent, contexts, true));
			this.taskCompleted = true;
			break;
		default:
			this.taskFailed = true;
			this.instances.add(new DatasetInstance(userID, messageID, message, intent, contexts, true));
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
