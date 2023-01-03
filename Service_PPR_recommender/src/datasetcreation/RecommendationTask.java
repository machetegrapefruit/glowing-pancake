package datasetcreation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import dialog.DialogState;

public class RecommendationTask implements DatasetPopulationTask{
	private static final Logger LOGGER = Logger.getLogger(RecommendationTask.class.getName());
	private List<DatasetInstance> instances;
	private boolean taskFailed;
	private boolean taskCompleted;
	private int recommendedIndex;
	
	public RecommendationTask(int recommendedIndex) {
		this.instances = new ArrayList<DatasetInstance>();
		this.taskFailed = false;
		this.taskCompleted = false;
		this.recommendedIndex = recommendedIndex;
	}
	
	@Override
	public void processMessage(DialogState state, JsonObject response, String userID, String messageID, String message, String intent,
			String contexts) {
		LOGGER.log(Level.INFO, "Entered processMessage, intent is " + intent);
		if (response.get("failure").getAsBoolean()) {
			this.taskFailed = true;
		}
		
		switch(intent) {
		case "request_recommendation":
		case "request_recommendation - why":
		case "request_recommendation - preference":
		case "request_recommendation - yes_but":
		case "request_recommendation - no_but":
		case "request_recommendation - details":
		case "request_recommendation - critiquing - disambiguation":
		case "request_recommendation - critiquing - yes":
		case "request_recommendation - critiquing - no":
		case "request_recommendation - trailer":
			this.instances.add(new DatasetInstance(userID, messageID, message, intent, contexts, true));
			break;
		case "request_recommendation - skip":
			this.instances.add(new DatasetInstance(userID, messageID, message, intent, contexts, true));
			//Se è stato richiesto uno skip della disambiguazione, fallisci il task
			if (response.get("skippedDisambiguation").getAsBoolean()) {
				LOGGER.log(Level.INFO, "Task failed!");
				this.taskFailed = true;
			}
			break;
		default:
			LOGGER.log(Level.INFO, "Task failed!");
			this.instances.add(new DatasetInstance(userID, messageID, message, intent, contexts, true));
			this.taskFailed = true;
			break;

		}
		
		checkTaskCompleted(state, response);
		
	}
	
	private void checkTaskCompleted(DialogState state, JsonObject response) {
		if (response.get("changedRecommendedEntity").getAsBoolean()) {
			//Il task è completo se si è passati ad un'altra entità raccomandata
			LOGGER.log(Level.INFO, "Task completed!");
			this.taskCompleted = true;
			if (!this.taskFailed) {
				this.setAllMessagesAsValid();
			}
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
	
	private void setAllMessagesAsValid() {
		for (DatasetInstance instance: instances) {
			instance.setToCheck(false);
		}
	}

}
