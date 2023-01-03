package dialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import dialog.SentimentObject.SentimentObjectType;
import dialog.functions.AddPreferenceFunction.AddPreferenceResponse;
import dialog.functions.DeletePreferenceFunction.DeletePreferenceResponse;
import dialog.functions.DisambiguationFunction.DisambiguationResponse;
import entity.Pair;
import utils.Alias;
import utils.Candidate;
import utils.MatchedElement;

/**
 * Represents a user sentence containing some ratings (e.g. "I like The Matrix but I hate Keanu Reeves").
 * Each rating can refer to an entity (items that can be recommended e.g. "The Matrix"), a property
 * (e.g. "Keanu Reeves", "Steven Spielberg"), or a property type (e.g. "director", "actor").
 * For each entity or property it tracks wheter it needs to be disambiguated.
 * For each property type it associates to a particular entity (e.g. in the case of the sentence
 * "I like The Matrix for the director".
 *
 */
public class Preference {
	
	private static final Logger LOGGER = Logger.getLogger(Preference.class.getName());
	
	/**
	 * List of entities (and properties) that are recognized in the preference
	 */
	private List<FilteredSentimentObject> entities;
	/**
	 * List of property types recognized in the preference
	 */
	private List<FilteredSentimentObject> propertyTypes;
	/**
	 * List of disambiguations that are required before the preference can be considered
	 * fully understood
	 */
	private List<Pair<Integer, PendingEvaluation>> toDisambiguate;
	/**
	 * List of entities that have been correctly disambiguated and recognized
	 */
	private Map<Integer, List<Alias>> disambiguatedEntities;
	/**
	 * List of properties that have been correctly disambiguated and recognized
	 */
	private Map<Integer, List<Alias>> disambiguatedProperties;
	private int toAssign;

	/**
	 * Entity - property type assignation policy currently in use for this preference.
	 */
	private PropertyTypeAssociationPolicy assignationPolicy;
	/**
	 * List of pending entity - property type assignation confirmations that are required
	 * before the preference can be considered fully understood
	 */
	private List<PendingConfirmation> currentConfirmation;
	
	public Preference(List<FilteredSentimentObject> entities, List<FilteredSentimentObject> propertyTypes) {
		this.entities = entities;
		this.propertyTypes = propertyTypes;
		this.toDisambiguate = new ArrayList<Pair<Integer, PendingEvaluation>>();
		this.disambiguatedEntities = new HashMap<Integer, List<Alias>>();
		this.disambiguatedProperties = new HashMap<Integer, List<Alias>>();
		this.toAssign = 0;
		//Default association policy is AssignToNearestPolicy
		this.assignationPolicy = new AssignToNearestPolicy();
	}
	
	/**
	 * Sets the assignation policy that will be used to determine how to assign an entity
	 * to a mentioned property type
	 * @param policy the PropertyTypeAssignationPolicy object
	 */
	public void setPropertyTypeAssignationPolicy(PropertyTypeAssociationPolicy policy) {
		this.assignationPolicy = policy;
	}
	
	/**
	 * Returns the disambiguations stille needed
	 * @return a list containing all the pending evaluations
	 */
	public List<Pair<Integer, PendingEvaluation>> getPendingEvaluations() {
		return this.toDisambiguate;
	}
	
	/**
	 * Adds a new disambiguation to the queue of pending disambiguations. It will be
	 * considered only after the previous disambiguation requests.
	 * @param index Index of the entity in the entities list
	 * @param pe Object representing the disambiguation request
	 */
	public void addDisambiguation(int index, PendingEvaluation pe) {
		this.toDisambiguate.add(new Pair<Integer, PendingEvaluation>(index, pe));
		LOGGER.log(Level.INFO, "Preference state: " + this.toJson());
	}
	
	/**
	 * Adds a new disambiguation to the top of the pending diwsambiguations queue.
	 * It will be evaluated before the previous disambiguation requests
	 * @param index Index of the entity in the entities list
	 * @param pe Object representing the disambiguation request
	 */
	public void pushDisambiguation(int index, PendingEvaluation pe) {
		this.toDisambiguate.add(0, new Pair<Integer, PendingEvaluation>(index, pe));
		LOGGER.log(Level.INFO, "Preference state: " + this.toJson());
	}
	
	/**
	 * 
	 * @return The object representing the next disambiguation request.
	 */
	public PendingEvaluation getNextPendingEvaluation() {
		if (this.toDisambiguate.size() > 0) {
			return this.toDisambiguate.get(0).value;
		}
		return null;
	}
	
	public int getNextEvaluationIndex() {
		if (this.toDisambiguate.size() > 0) {
			return this.toDisambiguate.get(0).key;
		}
		return -1;
	}
		
	/**
	 * Performs a disambiguation to the current disambiguation request.
	 * @param dResponse The DisambiguationResponse object obtained from the DisambiguationFunction class
	 */
	public void disambiguate(DisambiguationResponse dResponse) {
		int currentIndex = this.toDisambiguate.get(0).key;
		PendingEvaluation currentEv = this.toDisambiguate.get(0).value;
		if (dResponse.isSuccess() 
				&& (dResponse.getMatchedEntities().size() > 0 
						|| dResponse.getMatchedProperties().size() > 0
						|| dResponse.getNewEvaluations().size() > 0)) {
			//If no errors are found and at least one more item has been added
			//We remove the current disambiguation request
			skipCurrentDisambiguation();
			switch(currentEv.getType()) {
				case NAME_DISAMBIGUATION:
					//Add each of the disambiguated items
					for (MatchedElement alias: dResponse.getMatchedEntities()) {
						addDisambiguatedEntity(currentIndex, alias.getElement());
					}
					for (MatchedElement alias: dResponse.getMatchedProperties()) {
						addDisambiguatedProperty(currentIndex, alias.getElement());
					}
					break;
				case PROPERTY_TYPE_DISAMBIGUATION:
					//Add the disambiguated property
					addDisambiguatedProperty(currentIndex, currentEv.getElementName());
					break;
				case DELETE_NAME_DISAMBIGUATION:
					break;
			}
			//Add new disambiguation requests at the top of the list
			if (dResponse.getNewEvaluations().size() > 0) {
				for (PendingEvaluation pe: dResponse.getNewEvaluations()) {
					pushDisambiguation(currentIndex, pe);
				}
			}
		}
	}
	
	/**
	 * Attempts to change the current disambiguation when the user decides to ignore
	 * the pending disambiguation request by providing instead a different preference
	 * message
	 * @param apResponse The output obtained from the AddPreferenceFunction class
	 */
	public void changeDisambiguation(AddPreferenceResponse apResponse) {
		int currentIndex = this.toDisambiguate.get(0).key;
		if (apResponse.isSuccess()) {
			if (apResponse.getAddedEntities().size() > 0 || apResponse.getAddedProperties().size() > 0) {
				//If we added at least one entity
				//We can remove the current disambiguation request
				skipCurrentDisambiguation();
				for (MatchedElement added: apResponse.getAddedEntities()) {
					addDisambiguatedEntity(currentIndex, added.getElement());
				}
				for (MatchedElement added: apResponse.getAddedProperties()) {
					addDisambiguatedProperty(currentIndex, added.getElement());
				}
			} else if (apResponse.getPreference().getNextPendingEvaluation() != null) {
				//If the new preference requires new disambiguation requests
				//We need to replace the old disambiguation requests
				changeCurrentDisambiguation(apResponse.getPreference().getNextPendingEvaluation());
			}
		}
	}
	
	/**
	 * Attempts to change the current disambiguation when the user decides to ignore
	 * the pending disambiguation request by providing instead a different delete
	 * preference message
	 * @param apResponse The output obtained from the AddPreferenceFunction class
	 */
	public void changeDisambiguation(DeletePreferenceResponse dpResponse) {
		int currentIndex = this.toDisambiguate.get(0).key;
		if (dpResponse.getSuccess()) {
			if (dpResponse.getDeletedElements().size() > 0) {
				skipCurrentDisambiguation();
				//Non c'è bisogno di distinguere tra entità e proprietà in questo caso
				for (MatchedElement added: dpResponse.getDeletedElements()) {
					addDisambiguatedEntity(currentIndex, added.getElement());
				}
			} else if (dpResponse.getPreference().getNextPendingEvaluation() != null) {
				changeCurrentDisambiguation(dpResponse.getPreference().getNextPendingEvaluation());
			}
		}
	}
	
	/**
	 * Signals that the specified entity has been disambiguated
	 * @param index index in the entities list
	 * @param match Disambiguation option chosen by the user
	 */
	public void addDisambiguatedEntity(int index, Alias match) {
		if (!this.disambiguatedEntities.containsKey(index)) {
			this.disambiguatedEntities.put(index, new ArrayList<Alias>());
		}
		this.disambiguatedEntities.get(index).add(match);
		LOGGER.log(Level.INFO, "Preference state: " + this.toJson());
	}
	
	/**
	 * Signals that the specified property has been disambiguated
	 * @param index index in the entities list
	 * @param match Disambiguation option chosen by the user
	 */
	public void addDisambiguatedProperty(int index, Alias match) {
		if (!this.disambiguatedProperties.containsKey(index)) {
			this.disambiguatedProperties.put(index, new ArrayList<Alias>());
		}
		this.disambiguatedProperties.get(index).add(match);
		LOGGER.log(Level.INFO, "Preference state: " + this.toJson());
	}
	
	private void changeCurrentDisambiguation(PendingEvaluation pe) {
		if (this.toDisambiguate.size() > 0) {
			Pair<Integer, PendingEvaluation> current = this.toDisambiguate.get(0);
			current.value = pe;
		}
		LOGGER.log(Level.INFO, "Preference state: " + this.toJson());
	}
	
	/**
	 * Skips the current disambiguation request
	 */
	public void skipCurrentDisambiguation() {
		this.toDisambiguate.remove(0);
		LOGGER.log(Level.INFO, "Preference state: " + this.toJson());
	}
	
	/**
	 * Skips the current entity - property type association confirmation request.
	 */
	public void skipCurrentConfirmation() {
		this.toAssign++;
		this.currentConfirmation = null;
		LOGGER.log(Level.INFO, "Preference state: " + this.toJson());
	}
	
	/**
	 * 
	 * @return true if all entities and properties have been correctly disambiguated
	 */
	public boolean allDisambiguated() {
		return this.toDisambiguate.size() == 0;
	}
	
	/**
	 * 
	 * @return true if all the entity-property types have been correctly associated
	 */
	public boolean allConfirmed() {
		return this.getNextConfirmation() == null;
	}
	
	/**
	 * Finds the next entity-property type association to ask the user for confirmation
	 * @return a PendingConfirmation object containing the details of the confirmation request
	 */
	public PendingConfirmation getNextConfirmation() {
		if (this.allDisambiguated() && this.toAssign < this.propertyTypes.size()) {
			//If no more disambiguation requests are available, and there are still some
			//non assigned property types
			if (this.currentConfirmation == null || this.currentConfirmation.isEmpty()) {
				//If there are no currently available confirmations
				FilteredSentimentObject currentPropertyType = propertyTypes.get(this.toAssign);
				Candidate propTypeCandidate = 
						new Candidate(
								new Alias(currentPropertyType.getUri(), currentPropertyType.getLabel()),
								currentPropertyType.getStart(), 
								currentPropertyType.getEnd(), 
								getRatingFromSentiment(currentPropertyType.getSentiment()));
				List<Candidate> candidates = this.getDisambiguatedEntitiesAsList();
				//Find possible assignments for the current property type
				this.currentConfirmation = this.assignationPolicy.assignPropertyType(propTypeCandidate, candidates);
			}
			if (this.currentConfirmation != null && !this.currentConfirmation.isEmpty()) {
				//Return the first possible entity-property type association
				return this.currentConfirmation.get(0);
			}
		}
		return null;
	}
	
	/**
	 * Confirms the current entity-property type association
	 */
	public void confirmAssignment() {
		this.toAssign++;
		this.currentConfirmation = null;
	}
	
	/**
	 * Ignores the current entity - property type association and tries a different
	 * one, if there are other associations available. If there aren't, the current
	 * property type will be ignored.
	 */
	public void changeConfirmation() {
		//Remove the current confirmation request
		//We can try to ask the user for a different
		this.currentConfirmation.remove(0);
		if (this.currentConfirmation.size() == 0) {
			//If there are no more associations available, the current property type
			//will remain non-associated
			this.toAssign++;
			this.currentConfirmation = null;
		}
	}
	
	private List<Candidate> getDisambiguatedEntitiesAsList() {
		List<Candidate> result = new ArrayList<>();
		for(int d: this.disambiguatedEntities.keySet()) {
			FilteredSentimentObject so = this.entities.get(d);
			Candidate candidate = new Candidate(this.disambiguatedEntities.get(d).get(0), 
					so.getStart(), so.getEnd(), getRatingFromSentiment(so.getSentiment()));
			result.add(candidate);
		}
		return result;
	}
	
	/**
	 * Gets the rating evaluation from the sentiment returned by the Sentiment Analyzer component
	 */
	private int getRatingFromSentiment(int sentiment) {
		int rating = 0;
		if (sentiment > 1) {
			rating = 1;
		} else if (sentiment == -1) {
			rating = -1;
		}
		return rating;
	}
	
	public String toJson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Preference.class, new PreferenceAdapter());
		return gsonBuilder.create().toJson(this);
	}
	
	public static Preference fromJson(String obj) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Preference.class, new PreferenceAdapter());
		return gsonBuilder.create().fromJson(obj, Preference.class);
	}
	
	public static void main(String[] args) {
		List<FilteredSentimentObject> entities = new ArrayList<FilteredSentimentObject>();
		entities.add(new FilteredSentimentObject(
				new SentimentObject(0, 0, "a", "aa", 1, new ArrayList<Alias>(), SentimentObjectType.ENTITY), 
				new ArrayList<FilteredAlias>()));
		Preference p = new Preference(entities, new ArrayList<FilteredSentimentObject>());
		
		System.out.println(p.toJson().toString());
		Preference p2 = Preference.fromJson(p.toJson());
		System.out.println(p2.toJson());
	}
	
	public static class PreferenceAdapter implements JsonSerializer<Preference>, JsonDeserializer<Preference> {
		@Override
		public Preference deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(PropertyTypeAssociationPolicy.class, new PropertyTypeAssociationPolicyAdapter());
			return gsonBuilder.create().fromJson(json.toString(), Preference.class);
		}

		@Override
		public JsonElement serialize(Preference src, Type typeOfSrc, JsonSerializationContext context) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(PropertyTypeAssociationPolicy.class, new PropertyTypeAssociationPolicyAdapter());
			return gsonBuilder.create().toJsonTree(src);
		}
		
	}
}
