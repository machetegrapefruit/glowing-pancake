package dialog.functions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import configuration.Configuration;
import dialog.AssignToEntityPolicy;
import dialog.AssignToNearestPolicy;
import dialog.FilteredAlias;
import dialog.FilteredSentimentObject;
import dialog.ItemSuggestion;
import dialog.MLERConnector;
import dialog.PendingEvaluation;
import dialog.PendingEvaluation.PendingEvaluationType;
import dialog.Preference;
import dialog.PropertyTypeAssociationPolicy;
import dialog.SentimentAnalyzerConnector;
import dialog.SentimentObject;
import dialog.SentimentObject.SentimentObjectType;
import entity.Entity;
import functions.DisambiguationService;
import functions.EntityService;
import functions.PropertyService;
import functions.ServiceSingleton;
import functions.response.GetNameDisambiguationResponse;
import functions.response.GetPropertyTypeDisambiguationResponse;
import graph.AdaptiveSelectionController;
import utils.Alias;
import utils.Candidate;
import utils.DistanceMeasure;
import utils.MatchedElement;

/**
 * Handles the addition of all the preferences mentioned in a user sentence during the 
 * profile acquisition phase, handling eventual disambiguation or confirmation requests 
 * needed.
 */
public class AddPreferenceFunction {
	private boolean autoAdd = true;
	private static final Logger LOGGER = Logger.getLogger(AddPreferenceFunction.class.getName());
	
	public void setAutoAdd(boolean autoAdd) {
		this.autoAdd = autoAdd;
	}
	
	/**
	 * Adds the ratings contained in the user message. If necessary, it will also handle the 
	 * rating to the currently recommended item.
	 * @param message User's message
	 * @param userID ID of the user
	 * @param stopWords An array containing words that will be ignored in the recognition process
	 * @param currentRecommendedIndex index of the current recommended item in the user profile.
	 *        It is set to -1 if we are not in the recommendation phase. If it is different from -1,
	 *        the method looks for mentions of type recommended_entity
	 * @return an AddPreferenceResponse object containing all the feedback from the preference addition process
	 */
	public AddPreferenceResponse addPreferences(String message, String userID, String[] stopWords, String currentRecommendedEntity, ItemSuggestion currentEntityToRate) {
		AddPreferenceResponse response = new AddPreferenceResponse();
		DisambiguationService ds = new DisambiguationService();
		//SentimentAnalyzerConnector saConnector = new SentimentAnalyzerConnector(true, true, true);
		MLERConnector saConnector = new MLERConnector(true, true, true);
		EntityService es = ServiceSingleton.getEntityService();
		PropertyService ps = ServiceSingleton.getPropertyService();
		try {
			//Invoke the sentiment analyzer component to get all the ratings mentioned in the message
			List<FilteredSentimentObject> sentimentArray = saConnector.getFilteredSentiment(message, stopWords, 5);
						List<FilteredSentimentObject> sentimentEntities = new ArrayList<>();
			List<FilteredSentimentObject> sentimentPropertyTypes = new ArrayList<>();
			List<FilteredSentimentObject> sentimentKeywords = new ArrayList<>();
			
			//Sort entity and property type objects
			for (int i = 0; i < sentimentArray.size(); i++) {
				FilteredSentimentObject so = sentimentArray.get(i);
				if (so.getType() == SentimentObjectType.ENTITY) {
					sentimentEntities.add(so);
				} else if (so.getType() == SentimentObjectType.PROPERTY_TYPE) {
					LOGGER.log(Level.INFO, "Found a property type: " + so.getLabel());
					sentimentPropertyTypes.add(so);
				} else if (so.getType() == SentimentObjectType.KEYWORD) {
					LOGGER.log(Level.INFO, "Found a keyword:" + so.getLabel());
					sentimentKeywords.add(so);
				}
			}
			
			Preference preference = new Preference(sentimentEntities, sentimentPropertyTypes);
			if (currentRecommendedEntity != null) {
				//Handle the preference to the recommended item
				//String currentRecommended = es.getCachedRecommendedEntities(userID).get(currentRecommendedIndex);
				handleRecommendedEntityPreferences(
						userID, 
						es, 
						currentRecommendedEntity, 
						preference, 
						sentimentEntities, sentimentPropertyTypes, 
						sentimentKeywords, 
						response, 
						message);
			} else if (currentEntityToRate != null) {
				//Handle the preference to an entity that the system asked to rate
				handleSuggestedEntityPreferences(
						userID, 
						es, 
						ps,
						currentEntityToRate, 
						preference, 
						sentimentEntities, sentimentPropertyTypes, 
						sentimentKeywords, 
						response, 
						message);
			}
			
			//Handle all the preferences that were explicitly mentioned in the message
			handleMentionedPreferences(userID, sentimentEntities, preference, response);
			response.addPreference(preference);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setSuccess(false);
		}
		return response;
	}
	
	private void handleSuggestedEntityPreferences(
			String userID, 
			EntityService es, 
			PropertyService ps,
			ItemSuggestion currentEntityToRate,
			Preference preference,
			List<FilteredSentimentObject> sentimentEntities,
			List<FilteredSentimentObject> sentimentPropertyTypes,
			List<FilteredSentimentObject> sentimentKeywords,
			AddPreferenceResponse response,
			String message
			) throws IllegalStateException, IOException {
		LOGGER.log(Level.INFO, "Handling suggested entity preferences");
		LOGGER.log(Level.INFO, "Current suggested item is " + currentEntityToRate);
		boolean foundSuggEntityMention = false;
		Alias suggEntityAlias = getAlias(currentEntityToRate);
		//Set the policy so that all the property types will be assigned to the suggested item
		preference.setPropertyTypeAssignationPolicy(new AssignToEntityPolicy(new Candidate(suggEntityAlias, 0, 0, 0)));
		for (int i = 0; !foundSuggEntityMention && i < sentimentKeywords.size(); i++) {
			FilteredSentimentObject fso = sentimentKeywords.get(i);
			if (fso.getAliases().get(0).getURI().equals("recommended_entity")) {
				//We found a mention to the suggested entity (e.g. the user has written "I like it")
				foundSuggEntityMention = true;
				LOGGER.log(Level.INFO, "Found a mention to the suggested entity in the message");
				int sentiment = fso.getSentiment();
				//We add the preference to the suggested item
				addSuggestedItemPreference(preference, suggEntityAlias, userID, sentiment, es, ps, response);
			}
		}

		if (!foundSuggEntityMention) {
			LOGGER.log(Level.INFO, "No mention to the suggested entity found in the message");
			//The user didn't mention the recommended item in the message
			int entityCount = sentimentEntities.size() + sentimentPropertyTypes.size();
			if (entityCount == 0) {
				LOGGER.log(Level.INFO, "No items found in the message, attempt to add a preference to the suggested item");
				//No entities have been found
				//We're in the "I like" or "I dislike" cases
				//We try to add the preference to the suggested item
//				SentimentAnalyzerConnector saConnector = new SentimentAnalyzerConnector(false, false, false);
//				List<SentimentObject> fullSentence = saConnector.getSentiment(message);
//				SentimentObject so = fullSentence.get(0);
//				int sentiment = so.getSentiment();
				MLERConnector saConnector = new MLERConnector(false, false, false);
				int sentiment = saConnector.getSentiment(message);
				//We add the preference to the recommended item
				addSuggestedItemPreference(preference, suggEntityAlias, userID, sentiment, es, ps, response);
			} else {
				LOGGER.log(Level.INFO, "The user has mentioned other entities, we can skip the suggested item");
				//At least one entity has been found
				//We're in the "I like the director" or "I like Titanic" case
				//We assume that the user did not rate the recommended item
				//We skip it
			}

		}
	}
	
	private void handleRecommendedEntityPreferences(
			String userID, 
			EntityService es, 
			String currentRecommended,
			Preference preference,
			List<FilteredSentimentObject> sentimentEntities,
			List<FilteredSentimentObject> sentimentPropertyTypes,
			List<FilteredSentimentObject> sentimentKeywords,
			AddPreferenceResponse response,
			String message
			) throws IllegalStateException, IOException {
		LOGGER.log(Level.INFO, "Handling recommended entity preferences");
		LOGGER.log(Level.INFO, "Current recommended item is " + currentRecommended);
		boolean foundRecEntityMention = false;
		Alias recEntityAlias = new Alias(currentRecommended, es.getEntityLabel(currentRecommended));
		//Set the policy so that all the property types will be assigned to the recommended item
		preference.setPropertyTypeAssignationPolicy(new AssignToEntityPolicy(new Candidate(recEntityAlias, 0, 0, 0)));
		for (int i = 0; !foundRecEntityMention && i < sentimentKeywords.size(); i++) {
			FilteredSentimentObject fso = sentimentKeywords.get(i);
			if (fso.getAliases().get(0).getURI().equals("K1")) {
				//We found a mention to the recommended entity (e.g. the user has written "I like it")
				foundRecEntityMention = true;
				LOGGER.log(Level.INFO, "Found a mention to the recommended entity in the message");
				int sentiment = fso.getSentiment();
				//We add the preference to the recommended item
				boolean setCritiquing = sentimentEntities.size() + sentimentPropertyTypes.size() > 0;
				addRecommendedEntityPreference(recEntityAlias, userID, sentiment, es, response, setCritiquing);
			}
		}

		if (!foundRecEntityMention) {
			LOGGER.log(Level.INFO, "No mention to the recommended entity found in the message");
			//The user didn't mention the recommended item in the message
			int entityCount = sentimentEntities.size() + sentimentPropertyTypes.size();
			if (entityCount == 0) {
				LOGGER.log(Level.INFO, "No items found in the message, attempt to add a preference to the recommended item");
				//No entities have been found
				//We're in the "I like" or "I dislike" cases
				//We try to add the preference to the recommended item
//				SentimentAnalyzerConnector saConnector = new SentimentAnalyzerConnector(false, false, false);
//				List<SentimentObject> fullSentence = saConnector.getSentiment(message);
				MLERConnector saConnector = new MLERConnector(false, false, false);
//				SentimentObject so = fullSentence.get(0);
//				int sentiment = so.getSentiment();
				int sentiment = saConnector.getSentiment(message);
				//We add the preference to the recommended item
				addRecommendedEntityPreference(recEntityAlias, userID, sentiment, es, response, false);
			} else {
				LOGGER.log(Level.INFO, "The user has mentioned other entities, we can skip the recommended item");
				//At least one entity has been found
				//We're in the "I like the director" or "I like Titanic" case
				//We assume that the user did not rate the recommended item
				//We skip it
//				es.skipRecommendedEntity(userID, currentRecommended);
				
				//We try to add the preference to the recommended item
				MLERConnector saConnector = new MLERConnector(false, false, false);
//				SentimentObject so = fullSentence.get(0);
//				int sentiment = so.getSentiment();
				int sentiment = saConnector.getSentiment(message);
				//We add the preference to the recommended item
				addRecommendedEntityPreference(recEntityAlias, userID, sentiment, es, response, false);
			}

		}
	}
	
	private void handleMentionedPreferences(
			String userID,
			List<FilteredSentimentObject> sentimentEntities,
			Preference preference,
			AddPreferenceResponse response
			) {
		DisambiguationService ds = new DisambiguationService();
		//For each entity and property
		for (int i = 0; i < sentimentEntities.size(); i++) {
			LOGGER.log(Level.INFO, "Processing element no. " + i);
			FilteredSentimentObject sentimentObject = sentimentEntities.get(i);
			String label = sentimentObject.getLabel();
			int sentiment = sentimentObject.getSentiment();
			int rating = getRatingFromSentiment(sentiment);
			
			if (!label.equals("")) {
				LOGGER.log(Level.INFO, "Label is " + label);
				List<FilteredAlias> aliases = sentimentObject.getFilteredAliases();
				//Check if there is an ambiguity on the name of the item
				GetNameDisambiguationResponse gndr = ds.getNameDisambiguation(rating, aliases, PendingEvaluationType.NAME_DISAMBIGUATION);
				if (gndr.success()) {
					if (gndr.getPerfectMatch() != null) {
						//Only one item matches the name, no disambiguation required
						Alias match = gndr.getPerfectMatch();
						AddSinglePreferenceResponse aspResponse = addSinglePreference(match.getURI(), match.getLabel(), rating, userID);
						if (aspResponse.success) {
							if (aspResponse.added) {
								//Check if it is an entity or a property
								if (aspResponse.isEntity()) {
									preference.addDisambiguatedEntity(i, match);
									response.addEntity(new MatchedElement(match, rating));
								} else {
									preference.addDisambiguatedProperty(i, match);
									response.addProperty(new MatchedElement(match, rating));
								}
							} else {
								//This is a property, which needs a disambiguation on the property type
								//E.g. "You like Tom Cruise as actor or producer?"
								preference.addDisambiguation(i, aspResponse.getQueued());
							}
						}
					} else if (gndr.getEvaluation() != null) {
						//Need to disambiguate the name
						//E.g. "Do you mean Tom Cruise or Tom Hanks?"
						PendingEvaluation pe = gndr.getEvaluation();
						preference.addDisambiguation(i, pe);
					}
				}
			}
		}
	}
	
	private Alias getAlias(ItemSuggestion suggestedItem) {
		return new Alias(suggestedItem.getID(), suggestedItem.getLabel());
	}
	
	private void addRecommendedEntityPreference(Alias recEntityAlias, String userID, int sentiment, EntityService es, AddPreferenceResponse response, boolean setCritiquing) {
		int rating = getRatingFromSentiment(sentiment);
		//We add the preference to the recommended item
		LOGGER.log(Level.INFO, "Rating is " + rating);
		es.addRecommendedEntityPreference(userID, recEntityAlias.getURI(), rating, "user");
		MatchedElement elem = new MatchedElement(recEntityAlias, rating);
		response.addEntity(elem);
		if (setCritiquing) {
			//Signal the intention of the user to criticize the recommended entity
			es.setRefine(userID, recEntityAlias.getURI());
		}
	}
	
	private void addSuggestedItemPreference(Preference preference, Alias suggEntityAlias, String userID, int sentiment, EntityService es, PropertyService ps, AddPreferenceResponse response) {
		int rating = getRatingFromSentiment(sentiment);
		//We add the preference to the suggested item
		LOGGER.log(Level.INFO, "Rating is " + rating);
		AddSinglePreferenceResponse aspr = addSinglePreference(suggEntityAlias.getURI(), suggEntityAlias.getLabel(), rating, userID);
		if (aspr.added) {
			MatchedElement elem = new MatchedElement(suggEntityAlias, rating);
			response.addEntity(elem);
		} else if (aspr.getQueued() != null) {
			//We set the index to -1 because it is not referring to any of the items recognized in the sentence
			preference.addDisambiguation(-1, aspr.getQueued());
		}
	}
	
	/**
	 * Adds a preference to the specified entity or property
	 * @param uri URI of the entity or property to be rated
	 * @param name Label of the entity or property to be rated
	 * @param rating Rating (1= positive, 0=negative)
	 * @param userID ID of the user
	 * @return an AddSinglePreferenceResponse object containing the feedback of the operation
	 */
	public AddSinglePreferenceResponse addSinglePreference(String uri, String name, int rating, String userID) {
		LOGGER.log(Level.INFO, "Called addSinglePreference for " + uri);
		try {
			PropertyService ps = new PropertyService();
			EntityService es = ServiceSingleton.getEntityService();
			//List<List<String>> propertyList = asController.getAllEntityProperties(uri, Configuration.getDefaultConfiguration().getPropertyTypes());

			if (es.isEntity(uri)) {
				LOGGER.log(Level.INFO, "Assuming it is an entity");
				//Add a preference to the entity
				if (this.autoAdd) {
					es.addEntityPreference(userID, uri, rating, "user");
				}
				return new AddSinglePreferenceResponse(true, true, true, null);
			} else if (ps.isPropertyObject(uri)) {
				//It's a property
				LOGGER.log(Level.INFO, "Assuming it is a property");
				//Check if property type disambiguation is enabled
				Configuration c = Configuration.getDefaultConfiguration();
				
				if (c.isPropertyTypeDisambiguationEnabled()) {
					DisambiguationService ds = new DisambiguationService();
					GetPropertyTypeDisambiguationResponse gptdr = ds.getPropertyTypeDisambiguation(new Alias(uri, name), rating);				
					if (gptdr.success()) {
						if (gptdr.getPerfectMatch() != null) {
							//No need to disambiguate by property type
							//We can directly add the preference
							if (this.autoAdd) {
								ps.addPropertyPreference(
										userID,
										uri, 
										gptdr.getPerfectMatch(), 
										rating, "user"
										);
							}
							return new AddSinglePreferenceResponse(true, true, false, null);
						} else if (gptdr.getEvaluation() != null) {
							//Need to disambiguate by property type
							//E.g. "You like Tom Cruise as actor or producer?"
							return new AddSinglePreferenceResponse(false, true, false, gptdr.getEvaluation());
						}
					} else {
						//No property types found for the property. Should not happen.
						LOGGER.log(Level.INFO, "Property type list is empty!");
						return new AddSinglePreferenceResponse(false, false, false, null);
					}
				} else {
					//Property type disambiguation is disabled
					//We add a preference to the property for each property type it appears in the database
					List<String> propertyTypes = new PropertyService().getPropertyTypes(uri).get(uri);
					for (String propertyType : propertyTypes) {
						ps.addPropertyPreference(userID, uri, propertyType, rating, "user");		
					}
					LOGGER.log(Level.INFO, "Disambiguation is disabled: setting same rating for all properties.");
					
					return new AddSinglePreferenceResponse(true, true, false, null);
				}
			} else {
				//There is no object for this property type. Should not happen.
				LOGGER.log(Level.INFO, "Trying to add a preference to a non-object property type!");
				return new AddSinglePreferenceResponse(false, false, false, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new AddSinglePreferenceResponse(false, false, false, null);
		}
		return new AddSinglePreferenceResponse(false, false, false, null);
	}
	
	
	/**
	 * Trasforma il rating del servizio di Sentiment Extraction nel rating del recommender system
	 */
	private int getRatingFromSentiment(int sentiment) {
		int rating = 0;
		if (sentiment > 1) {
			rating = 1;
		}
		return rating;
	}
		
	public class AddPreferenceResponse {
		/**
		 * True if there are no errors in the process
		 */
		private boolean success;
		/**
		 * List containing all the entities that were added successfully
		 */
		private List<MatchedElement> addedEntities;
		/**
		 * List containing all the properties that were added successfully
		 */
		private List<MatchedElement> addedProperties;
		//private List<PendingEvaluation> toQueue;
		//private List<PendingConfirmation> confirmations;
		/**
		 * Preference object containing all the information regarding the
		 * current preference.
		 */
		private Preference preference;
		
		public AddPreferenceResponse() {
			addedEntities = new ArrayList<MatchedElement>();
			addedProperties = new ArrayList<MatchedElement>();
			//toQueue = new ArrayList<PendingEvaluation>();
			//confirmations = new ArrayList<PendingConfirmation>();
			success = true;
		}
		
		private void addEntity(MatchedElement a) {
			this.addedEntities.add(a);
		}
		private void addProperty(MatchedElement a) {
			this.addedProperties.add(a);
		}
		/*
		private void addEvaluation(PendingEvaluation p) {
			this.toQueue.add(p);
		}
		
		private void addConfirmation(PendingConfirmation p) {
			this.confirmations.add(p);
		}*/
		
		public Preference getPreference() {
			return this.preference;
		}
		
		private void addPreference(Preference p) {
			this.preference = p;
		}
		
		private void setSuccess(boolean success) {
			this.success = success;
		}
		
		public List<MatchedElement> getAddedEntities() {
			return this.addedEntities;
		}
		public List<MatchedElement> getAddedProperties() {
			return this.addedProperties;
		}
		
		public boolean isSuccess() {
			return this.success;
		}
		
		/*public List<PendingEvaluation> getNewEvaluations() {
			return this.toQueue;
		}
		
		public List<PendingConfirmation> getNewConfirmations() {
			return this.confirmations;
		}*/
	}
	
	public class AddSinglePreferenceResponse {
		/**
		 * True if the preference was added without any disambiguation required
		 */
		private boolean added;
		/**
		 * True if no error occurred
		 */
		private boolean success;
		/**
		 * True if the added item is an entity
		 */
		private boolean isEntity;
		/**
		 * New prompt to be asked
		 */
		private PendingEvaluation queued;
		
		public AddSinglePreferenceResponse(boolean added, boolean success, boolean isEntity, PendingEvaluation queued) {
			this.added = added;
			this.success = success;
			this.queued = queued;
			this.isEntity = isEntity;
		}

		public boolean isAdded() {
			return added;
		}

		public boolean isSuccess() {
			return success;
		}
		
		public boolean isEntity() {
			return this.isEntity;
		}

		public PendingEvaluation getQueued() {
			return queued;
		}
	}
}
