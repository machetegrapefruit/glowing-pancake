package dialog.functions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import configuration.Configuration;
import dialog.FilteredAlias;
import dialog.FilteredSentimentObject;
import dialog.PendingEvaluation;
import dialog.PendingEvaluation.PendingEvaluationType;
import dialog.Preference;
import dialog.SentimentAnalyzerConnector;
import dialog.SentimentObject;
import dialog.SentimentObject.SentimentObjectType;
import functions.DisambiguationService;
import functions.EntityService;
import functions.PropertyService;
import functions.ServiceSingleton;
import functions.response.GetNameDisambiguationResponse;
import functions.response.GetPropertyTypeDisambiguationResponse;
import graph.AdaptiveSelectionController;
import utils.Alias;
import utils.DistanceMeasure;
import utils.MatchedElement;

/**
 * Handles the addition of all the preferences mentioned in a user sentence during the 
 * profile acquisition phase, handling eventual disambiguation or confirmation requests 
 * needed.
 */
public class AddPreferenceFunction2 {
	private boolean autoAdd = true;		//Aggiunto a fini di test
	private static final Logger LOGGER = Logger.getLogger(AddPreferenceFunction2.class.getName());
	
	public void setAutoAdd(boolean autoAdd) {
		this.autoAdd = autoAdd;
	}
	
	/**
	 * Adds the ratings contained in the user message.
	 * @param message User's message
	 * @param userID ID of the user
	 * @param stopWords An array containing words that will be ignored in the recognition process
	 * @return an AddPreferenceResponse object containing all the feedback from the preference addition process
	 */
	public AddPreferenceResponse addPreferences(String message, String userID, String[] stopWords) {
		AddPreferenceResponse response = new AddPreferenceResponse();
		DisambiguationService ds = new DisambiguationService();
		SentimentAnalyzerConnector saConnector = new SentimentAnalyzerConnector(true, true, true);
		try {
			//Invoke the sentiment analyzer component to get all the ratings mentioned in the message
			List<FilteredSentimentObject> sentimentArray = saConnector.getFilteredSentiment(message, stopWords, 5);
						List<FilteredSentimentObject> sentimentEntities = new ArrayList<>();
			List<FilteredSentimentObject> sentimentPropertyTypes = new ArrayList<>();
			
			//Sort entity and property type objects
			for (int i = 0; i < sentimentArray.size(); i++) {
				FilteredSentimentObject so = sentimentArray.get(i);
				if (so.getType() == SentimentObjectType.ENTITY) {
					sentimentEntities.add(so);
				} else if (so.getType() == SentimentObjectType.PROPERTY_TYPE) {
					LOGGER.log(Level.INFO, "Found a property type: " + so.getLabel());
					sentimentPropertyTypes.add(so);
				}
			}
			
			Preference preference = new Preference(sentimentEntities, sentimentPropertyTypes);
		
			//For each entity and property
			for (int i = 0; i < sentimentEntities.size(); i++) {
				LOGGER.log(Level.INFO, "Processing element no. " + i);
				FilteredSentimentObject sentimentObject = sentimentArray.get(i);
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
									PendingEvaluation pe = aspResponse.getQueued();
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
			response.addPreference(preference);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setSuccess(false);
		}
		return response;
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
			AdaptiveSelectionController asController = new AdaptiveSelectionController();
			PropertyService ps = new PropertyService();
			EntityService es = ServiceSingleton.getEntityService();
			List<List<String>> propertyList = asController.getAllEntityProperties(uri, Configuration.getDefaultConfiguration().getPropertyTypes());

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
