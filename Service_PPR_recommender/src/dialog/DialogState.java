package dialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import configuration.Configuration;
import datasetcreation.DatasetPopulationManager;
import entity.DeleteType;
import entity.Property;

public class DialogState {
	private Deque<Preference> pendingPreferenceQueue;
	private String clientID;
	private int currentRecommendedIndex;
	private JsonArray contexts;
	private boolean firstRecommendation;
	private boolean firstDisambiguation;
	private boolean firstProfile;
	private boolean newUser;
	private DatasetPopulationManager datasetPopulationManager;
	
	private String entityToRecommend;
	private ItemSuggestion itemToSuggest;
	private int suggestedItemsCount;			//How many items have been suggested
	private int ratedSuggestedItemsCount;		//How many suggested items have been rated by the user
	private int preferenceMessagesCount;
	private String[] top5Entities;
	private String entityToRate;
	private DeleteType deleteType;
	private Property propertyToRate;
	private List<String> messages;
	private int page;
	private boolean critiquing;
	private boolean training;
	private boolean genericProperties;
	
	/*
	 * Counts how many preferences were given in past recommendation cycles. This can be used
	 * to keep track of the number of preferences given in the current recommendation, e.g. to
	 * set a minimum number of new preferences that the user must give before starting a new
	 * session
	 */
	private int minPreferences; 		
	
	private int currentQuestionIndex;
	
	public DialogState(String clientID) {
		this.clientID = clientID;
		this.pendingPreferenceQueue = new LinkedList<Preference>();
		this.currentRecommendedIndex = -1;
		this.contexts = null;
		this.firstRecommendation = true;
		this.firstDisambiguation = true;
		this.firstProfile = true;
		this.newUser = true;
		this.datasetPopulationManager = new DatasetPopulationManager();
		
		this.entityToRecommend = null;
		this.top5Entities = new String[5];
		this.entityToRate = null;
		this.deleteType = DeleteType.NONE;
		this.propertyToRate = null;
		this.messages = new ArrayList<String>();
		this.page = 1;
		this.critiquing = false;
		this.training = false;
		this.genericProperties = true;
		
		this.currentQuestionIndex = -1;
		this.suggestedItemsCount = 0;
		this.ratedSuggestedItemsCount = 0;
		this.preferenceMessagesCount = 0;
		
		Configuration c = Configuration.getDefaultConfiguration();
		
		this.minPreferences = c.getMinPreferencesFirstSession();
	}
	
	
	public DialogState(Deque<Preference> pendingPreferenceQueue, String clientID, int currentRecommendedIndex,
			JsonArray contexts, boolean firstRecommendation, boolean firstDisambiguation, boolean firstProfile,
			boolean newUser, DatasetPopulationManager datasetPopulationManager,
			String entityToRecommend, String[] top5Entities, String entityToRate,
			DeleteType deleteType, Property propertyToRate, List<String> messages, int page,
			boolean critiquing, boolean training, boolean genericProperties, int currentQuestionIndex, ItemSuggestion itemToSuggest,
			int minPreferences) {
		this.pendingPreferenceQueue = pendingPreferenceQueue;
		this.clientID = clientID;
		this.currentRecommendedIndex = currentRecommendedIndex;
		this.contexts = contexts;
		this.firstRecommendation = firstRecommendation;
		this.firstDisambiguation = firstDisambiguation;
		this.firstProfile = firstProfile;
		this.newUser = newUser;
		this.datasetPopulationManager = datasetPopulationManager;
		
		this.entityToRecommend = entityToRecommend;
		this.top5Entities = top5Entities;
		this.entityToRate = entityToRate;
		this.deleteType = deleteType;
		this.propertyToRate = propertyToRate;
		this.messages = messages;
		this.page = page;
		this.critiquing = critiquing;
		this.training = training;
		this.genericProperties = genericProperties;
		
		this.currentQuestionIndex = currentQuestionIndex;
		this.itemToSuggest = itemToSuggest;
		this.minPreferences = minPreferences;
	}
	
	public void resetCurrentSuggestion() {
		this.suggestedItemsCount = 0;
		this.ratedSuggestedItemsCount = 0;
		this.itemToSuggest = null;
	}
	
	public int getPreferenceMessagesCount() {
		return this.preferenceMessagesCount;
	}
	
	public void setPreferenceMessagesCount(int count) {
		this.preferenceMessagesCount = count;
	}
	
	public Deque<Preference> getPendingPreferenceQueue() {
		return pendingPreferenceQueue;
	}
	public String getClientID() {
		return clientID;
	}
	public int getCurrentRecommendedIndex() {
		return currentRecommendedIndex;
	}
	public JsonArray getContexts() {
		return contexts;
	}
	public boolean isFirstRecommendation() {
		return firstRecommendation;
	}
	public boolean isFirstDisambiguation() {
		return firstDisambiguation;
	}
	public boolean isFirstProfile() {
		return firstProfile;
	}
	public boolean isNewUser() {
		return newUser;
	}
	public DatasetPopulationManager getDatasetPopulationManager() {
		return this.datasetPopulationManager;
	}
	public String getEntityToRecommend() {
		return this.entityToRecommend;
	}
	public String[] getTop5Entities() {
		return this.top5Entities;
	}
	public String getEntityToRate() {
		return this.entityToRate;
	}
	public DeleteType getDeleteType() {
		return this.deleteType;
	}
	public Property getPropertyToRate() {
		return this.propertyToRate;
	}
	public List<String> getMessages() {
		return this.messages;
	}
	public boolean hasMessage(String messageID) {
		return this.messages.contains(messageID);
	}
	public int getPage() {
		return this.page;
	}
	public void setCurrentRecommendedIndex(int index) {
		this.currentRecommendedIndex = index;
	}
	public void setFirstRecommendation(boolean value) {
		this.firstRecommendation = value;
	}
	public void setFirstDisambiguation(boolean value) {
		this.firstDisambiguation = value;
	}
	public void setFirstProfile(boolean value) {
		this.firstProfile = value;
	}
	public void setNewUser(boolean value) {
		this.newUser = value;
	}
	public void setContexts(JsonArray contexts) {
		this.contexts = contexts;
	}
	public void setEntityToRecommend(String entity) {
		this.entityToRecommend = entity;
	}
	public void setTop5Entities(String[] entities) {
		this.top5Entities = entities.clone();
	}
	public void setEntityToRate(String entity) {
		this.entityToRate = entity;
	}
	public void setDeleteType(DeleteType deleteType) {
		this.deleteType = deleteType;
	}
	public void setPropertyToRate(Property property) {
		this.propertyToRate = property;
	}
	public void addMessage(String messageID) {
		this.messages.add(messageID);
	}
	public void setPage(int page) {
		this.page = page;
	}
	public void setCritiquing(boolean critiquing) {
		this.critiquing = critiquing;
	}
	public boolean isCritiquing() {
		return this.critiquing;
	}
	public void setCurrentQuestionIndex(int index) {
		this.currentQuestionIndex = index;
	}
	public int getCurrentQuestionIndex() {
		return this.currentQuestionIndex;
	}
	public void setTraining(boolean training) {
		this.training = training;
	}
	public boolean isTraining() {
		return training;
	}
	public void setGenericProperties(boolean genericProperties) {
		this.genericProperties = genericProperties;
	}
	public boolean isGenericProperties() {
		return genericProperties;
	}
	public ItemSuggestion getCurrentSuggestion() {
		return this.itemToSuggest;
	}
	public void setSuggestion(String itemToSuggest, boolean advance) {
		if (itemToSuggest != null) {
			this.suggestedItemsCount++;
			if (advance) {
				this.ratedSuggestedItemsCount++;	//Increase the suggestion counter
			}
			this.itemToSuggest = new ItemSuggestion(itemToSuggest, this.ratedSuggestedItemsCount, this.suggestedItemsCount);
		} else {
			this.itemToSuggest = null;
		}
	}
	
	public int getRatedSuggestedItemsCount() {
		return this.ratedSuggestedItemsCount;
	}
	
	public int getMinPreferences() {
		return this.minPreferences;
	}
	
	public void setMinPreferences(int minPreferences) {
		this.minPreferences = minPreferences;
	}
	
	public JsonElement toJson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DialogState.class, new DialogStateAdapter());
        return gsonBuilder.create().toJsonTree(this);
	}
	
	public static DialogState fromJson(JsonElement json) {
		GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DialogState.class, new DialogStateAdapter());
        return gsonBuilder.create().fromJson(json.toString(), DialogState.class);
	}
	
	
//	public static void main(String[] args) {
//		List<FilteredSentimentObject> entities = new ArrayList<FilteredSentimentObject>();
//		entities.add(new FilteredSentimentObject(
//				new SentimentObject(0, 0, "a", "aa", 1, new ArrayList<Alias>(), SentimentObjectType.ENTITY), 
//				new ArrayList<FilteredAlias>()));
//		Preference p = new Preference(entities, new ArrayList<FilteredSentimentObject>());
//		Deque<Preference> queue = new LinkedList<Preference>();
//		p.setPropertyTypeAssignationPolicy(new AssignToEntityPolicy(new Candidate(new Alias("a", "asd"), 0, 1, 1)));
//		queue.addLast(p);
//		DialogState d = new DialogState(queue, 
//				123, 
//				2, 
//				null, 
//				false, 
//				false, 
//				false, 
//				false,
//				new DatasetPopulationManager(),
//	            -1);
//		
//		System.out.println(d.toJson().toString());
//		DialogState d2 = DialogState.fromJson(d.toJson());
//		System.out.println(d2.toJson().toString());
//	}
	
	public static class DialogStateAdapter implements JsonSerializer<DialogState>, JsonDeserializer<DialogState> {
		@Override
		public JsonElement serialize(DialogState src, Type typeOfSrc, JsonSerializationContext context) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Preference.class, new dialog.Preference.PreferenceAdapter());
			gsonBuilder.registerTypeAdapter(DatasetPopulationManager.class, new DatasetPopulationManager.DatasetPopulationManagerAdapter());
			return gsonBuilder.create().toJsonTree(src);
		}
		
		@Override
		public DialogState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Preference.class, new dialog.Preference.PreferenceAdapter());
			gsonBuilder.registerTypeAdapter(DatasetPopulationManager.class, new DatasetPopulationManager.DatasetPopulationManagerAdapter());
			Gson gson = gsonBuilder.create();
			DialogState state = gson.fromJson(json, DialogState.class);
			return state;
		}
	}
}
