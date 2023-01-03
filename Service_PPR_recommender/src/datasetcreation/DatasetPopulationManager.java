package datasetcreation;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import dialog.DialogState;
import functions.LogService;

/**
 * Handles the population of the message log. 
 * This class manages a set of tasks, which attempt to understand if the flow of the
 * conversation is correct or if there are some anomalies. This is useful for checking
 * if the recognized intent can be trusted as accurate or not. This is signaled in the
 * log using the to_check flag.
 *
 */
public class DatasetPopulationManager {
	private static final Logger LOGGER = Logger.getLogger(DatasetPopulationManager.class.getName());
	private DatasetPopulationTask currentTask;
	
	public void processMessage(DialogState state, JsonObject response, String userID, String messageID, String message, String intent, String contexts, boolean saveOnDatabase) {
		//Controllo se l'intent riconosciuto fa partire un nuovo task
		checkTaskStartingConditions(state, response, userID, message, intent, contexts);
		
		if (currentTask != null) {
			//Altrimenti, il messaggio è processato dal task corrente
			currentTask.processMessage(state, response, userID, messageID, message, intent, contexts);
			
			if (currentTask.isTaskCompleted()) {
				//Se il task è stato completato, si possono inserire le istanze nel dataset
				LOGGER.log(Level.INFO, "Task completed, instances are: " + currentTask.getTrainingInstances());
				if (saveOnDatabase) {
					setTrainingInstances(currentTask.getTrainingInstances());
				}
				currentTask = null;
				//Controllo se un evento ha prodotto l'avvio di un nuovo task (es. il passaggio ad un'altra entità raccomandata
				//attiva un nuovo RecommendationTask)
				checkTaskStartingConditionsFromEvent(state, response, userID, message, intent, contexts);
			}
		}
	}
	
	/**
	 * Controlla se bisogna attivare un nuovo task
	 */
	public void checkTaskStartingConditions(DialogState state, JsonObject response, String userID, String message, String intent, String contexts) {
		DatasetPopulationTask newTask = null;
		//Controllo gli intent che possono attivare un nuovo task
		switch (intent) {
		case "preference":
		case "show_profile - delete_preference":
			newTask = new PreferenceTask();
			break;
		case "request_recommendation":
			newTask = new RecommendationTask(state.getCurrentRecommendedIndex());
			break;				
		case "show_profile":
			newTask = new ShowProfileTask();
			break;
		case "reset_profile":
			newTask = new DeleteProfileTask();
			break;
		}
		
		if (newTask != null) {
			//Se c'è un nuovo task, controllo se è stato interrotto uno precedente
			if (currentTask != null) {
				//Se un task è stato interrotto, tutti i messaggi sono da controllare
				LOGGER.log(Level.INFO, "Task interrupted, instances are: " + currentTask.getTrainingInstances());
			}
			currentTask = newTask;
		}
	}
	
	/**
	 * Controlla se deve essere attivato un nuovo task dagli eventi generati 
	 */
	public void checkTaskStartingConditionsFromEvent(DialogState state, JsonObject response, String userID, String message, String intent, String contexts) {
		boolean isChangedRecommendedEntity = response.get("changedRecommendedEntity").getAsBoolean();
		if (isChangedRecommendedEntity) {
			if (state.getCurrentRecommendedIndex() != -1) {
				LOGGER.log(Level.INFO, "Task started from event");
				currentTask = new RecommendationTask(state.getCurrentRecommendedIndex());
			}
		}
	}
	
	private void setTrainingInstances(List<DatasetInstance> instances) {
		LogService ls = new LogService();
		for (DatasetInstance instance: instances) {
			ls.setLogMessageToCheck(instance.getUserID() + "", instance.getMessageID(), instance.toCheck());
		}
	}
	
	public static class DatasetPopulationManagerAdapter implements JsonSerializer<DatasetPopulationManager>, JsonDeserializer<DatasetPopulationManager> {

		@Override
		public DatasetPopulationManager deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(DatasetPopulationTask.class, new DatasetPopulationTaskAdapter());
			return gsonBuilder.create().fromJson(json.toString(), DatasetPopulationManager.class);
		}

		@Override
		public JsonElement serialize(DatasetPopulationManager src, Type typeOfSrc, JsonSerializationContext context) {
			// TODO Auto-generated method stub
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(DatasetPopulationTask.class, new DatasetPopulationTaskAdapter());
			return gsonBuilder.create().toJsonTree(src);
		}
		
	}
}
