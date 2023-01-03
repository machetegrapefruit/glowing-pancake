package dialog.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import dialog.PendingConfirmation;
import dialog.PendingEvaluation;
import dialog.PendingEvaluation.PendingEvaluationType;
import dialog.Preference;
import dialog.functions.AddPreferenceFunction.AddSinglePreferenceResponse;
import functions.DisambiguationService;
import functions.PropertyService;
import functions.ServiceSingleton;
import utils.Alias;
import utils.MatchedElement;

/**
 * Handles the disambiguation of entities and properties.
 * When an entity or property is not fully understood, the system proposes a set of possible
 * alternative, from which the user should choose one option.
 * Currently there are two types of disambiguation allowed:
 * 1. Disambiguation on the property type, when the user writes a preference on a property
 *    E.g. "I like Tom Cruise", "Do you mean Tom Cruise as an actor or as a producer?"
 * 2. Disambiguation on the name, when multiple items match the name mentioned by the user
 *    E.g. "I like Ghostbusters", "Do you mean Ghostbusters (1984) or Ghostbusters (2016)?"
 * 
 * @author Andrea Iovine
 *
 */
public class DisambiguationFunction {
	private static final Logger LOGGER = Logger.getLogger(DisambiguationFunction.class.getName());
	
	/**
	 * Executes a disambiguation, and adds the recognized item.
	 * @param message User message
	 * @param userID ID of the current user
	 * @param preference Object containing the data from the current preference
	 * @return
	 */
	public DisambiguationResponse doPreferenceDisambiguation(String message, String userID, Preference preference) {
		LOGGER.log(Level.INFO, "Called doPreferenceDisambiguation");
		DisambiguationResponse response = new DisambiguationResponse();
		PendingEvaluation evaluation = preference.getNextPendingEvaluation();
		
		if (evaluation.getType() == PendingEvaluationType.PROPERTY_TYPE_DISAMBIGUATION) {
			response = doPropertyTypeDisambiguation(evaluation, message, userID);
		} else if (evaluation.getType() == PendingEvaluationType.NAME_DISAMBIGUATION) {
			response = doNameDisambiguation(evaluation, message, userID);
		} else if (evaluation.getType() == PendingEvaluationType.DELETE_NAME_DISAMBIGUATION) {
			response = doNameDisambiguation(evaluation, message, userID);
		}
		return response;
	}
	
	/**
	 * Executes a disambiguation on the property type. It finds the option chosen by the user
	 * and adds the appropriate preference if possible.
	 * @param evaluation Current disambiguation request
	 * @param message Message of the user
	 * @param userID ID of the user
	 * @return a DisambiguationResponse object containing the outcome of the disambiguation
	 */
	private DisambiguationResponse doPropertyTypeDisambiguation(PendingEvaluation evaluation, String message, String userID) {
		LOGGER.log(Level.INFO, "Called doPropertyTypeDisambiguation");
		DisambiguationResponse response = new DisambiguationResponse();
		DisambiguationService ds = new DisambiguationService();
		
		List<String> possibleNames = new ArrayList<String>();
		for (Alias value: evaluation.getPossibleValues()) {
			possibleNames.add(value.getLabel());
		}
		
		List<Integer> indexes = ds.disambiguate(message, possibleNames);
		if (indexes.size() > 0) {
			for (int i: indexes) {
				String foundURI = evaluation.getPossibleValues().get(i).getURI();
				LOGGER.log(Level.INFO, "Found a property type: " + foundURI);
				Alias matched = evaluation.getPossibleValues().get(i);
				
				PropertyService ps = ServiceSingleton.getPropertyService();
				ps.addPropertyPreference(
						userID,
						evaluation.getElementName().getURI(), 
						matched.getURI(), 
						evaluation.getRating(), "user"
						);
				response.addMatchedProperty(new MatchedElement(matched, evaluation.getRating()));
			}
		} else {
			//If none of the options was found
			LOGGER.log(Level.INFO, "No property type found!");
			response.setSuccess(false);
		}
						
		return response;
	}
	
	/**
	 * Executes a disambiguation on the name. It finds the option chosen by the user and 
	 * adds the appropriate preference if possible, or adds a new disambiguation if necessary.
	 * @param evaluation Current disambiguation request
	 * @param message Message of the user
	 * @param userID ID of the user
	 * @return a DisambiguationResponse object containing the outcome of the disambiguation
	 */
	private DisambiguationResponse doNameDisambiguation(PendingEvaluation evaluation, String message, String userID) {
		LOGGER.log(Level.INFO, "Called doNameDisambiguation");
		StringJoiner sj = new StringJoiner(", ");
		DisambiguationResponse response = new DisambiguationResponse();
		DisambiguationService ds = new DisambiguationService();
		
		List<String> possibleNames = new ArrayList<String>();
		for (Alias value: evaluation.getPossibleValues()) {
			//Extract the name of each option
			possibleNames.add(value.getLabel());
		}
		
		List<Integer> indexes = ds.disambiguate(message, possibleNames);
		if (indexes.size() > 0) {		
			for (int i: indexes) {
				Alias matched = evaluation.getPossibleValues().get(i);
				String foundURI = matched.getURI();
				String foundName = matched.getLabel();
				LOGGER.log(Level.INFO, "Found entity or property: " + foundURI);
				try {
					if (evaluation.getType() == PendingEvaluationType.DELETE_NAME_DISAMBIGUATION) {
						//We can delete the chosen preference
						new DeletePreferenceFunction().deleteSinglePreference(userID, foundURI, foundName);
						response.addMatchedEntity(new MatchedElement(matched, -1));
					} else if (evaluation.getType() == PendingEvaluationType.NAME_DISAMBIGUATION) {
						//We can add the preference
						AddSinglePreferenceResponse aspResponse = 
								new AddPreferenceFunction()
								.addSinglePreference(foundURI, foundName, evaluation.getRating(), userID);
						if (aspResponse.isSuccess()) {
							if (aspResponse.isAdded()) {
								if (aspResponse.isEntity()) {
									response.addMatchedEntity(new MatchedElement(matched, evaluation.getRating()));
								} else {
									response.addMatchedProperty(new MatchedElement(matched, evaluation.getRating()));
								}
							} else {
								//A further disambiguation is required if it is a property
								PendingEvaluation pe = aspResponse.getQueued();
								response.addNewEvaluation(pe);
							}
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			//If none of the options was found
			LOGGER.log(Level.INFO, "No matching name found!");
			response.setSuccess(false);
		}
		
		return response;
	}
	
	public class DisambiguationResponse {
		
		/**
		 * True if no errors occurred
		 */
		private boolean success;
		/**
		 * Contains the entities that were disambiguated
		 */
		private List<MatchedElement> matchedEntities;
		/**
		 * Contains the properties that were disambiguated
		 */
		private List<MatchedElement> matchedProperties;
		/**
		 * Contains additional disambiguation requests needed
		 */
		private List<PendingEvaluation> newEvaluations;
		/**
		 * Contains additional entity-property type association confirmations needed
		 */
		private List<PendingConfirmation> confirmations;
		
		public DisambiguationResponse() {
			this.matchedEntities = new ArrayList<>();
			this.matchedProperties = new ArrayList<>();
			this.newEvaluations = new ArrayList<>();
			this.confirmations = new ArrayList<>();
			this.success = true;
		}
		
		private void setSuccess(boolean success) {
			this.success = success;
		}
		
		private void addMatchedEntity(MatchedElement a) {
			this.matchedEntities.add(a);
		}
		
		private void addMatchedProperty(MatchedElement a) {
			this.matchedProperties.add(a);
		}
		
		public boolean isSuccess() {
			return success;
		}

		public List<MatchedElement> getMatchedEntities() {
			return matchedEntities;
		}
		
		public List<MatchedElement> getMatchedProperties() {
			return matchedProperties;
		}

		public List<PendingEvaluation> getNewEvaluations() {
			return newEvaluations;
		}
		
		public List<PendingConfirmation> getConfirmations() {
			return confirmations;
		}

		private void addNewEvaluation(PendingEvaluation p) {
			this.newEvaluations.add(p);
		}
		
		private void addNewConfirmation(PendingConfirmation c) {
			this.confirmations.add(c);
		}
		
	}
}
