package functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import configuration.Configuration;
import dialog.FilteredAlias;
import dialog.PendingEvaluation;
import dialog.PendingEvaluation.PendingEvaluationType;
import functions.response.GetNameDisambiguationResponse;
import functions.response.GetPropertyTypeDisambiguationResponse;
import utils.Alias;
import utils.DistanceMeasure;
import utils.LevenshteinDistanceCalculator;
import utils.Match;
import utils.MatchMap;

public class DisambiguationService {
	private final static Logger LOGGER = Logger.getLogger(DisambiguationService.class.getName());
	
	/**
	 * Restituisce una nuova disambiguazione sulla base del nome, data una frase e una lista di alias possibili.
	 * Restituisce un solo alias se riconosce un match perfetto, altrimenti restituisce un oggetto PendingEvaluation.
	 * Se il filtraggio non è superato da nessun alias allora restituisce success=false.
	 */
	
	/**
	 * Given a list of candidate items for a mention, checks whether a disambiguation on
	 * the name is needed. If there is more than one candidate item, it will return a 
	 * PendingEvaluation object containing the disambiguation request. If no disambiguation
	 * is needed, it will return the correct item.
	 * If one of the candidate items (Alias objects) has a perfect match with the user's sentence
	 * no disambiguation will be required, and instead the perfect match will be returned.
	 * @param rating The rating associated to the mention
	 * @param aliases A list of candidate items associated to the mention
	 * @param type Disambiguation type
	 * @return A GetNameDisambiguationResponse containing the outcome of the operation
	 */
	public GetNameDisambiguationResponse getNameDisambiguation(int rating, List<FilteredAlias> aliases, PendingEvaluationType type) {
		if (aliases.size() == 0) {
			//No aliases found, should not happen
			LOGGER.log(Level.INFO, "distances is empty! That means that no name has been found!");
			return new GetNameDisambiguationResponse(false);
		} else if (aliases.size() == 1) {
			//Only one alias found, no need to disambiguate
			LOGGER.log(Level.INFO, "distances.size() is 1, no name disambiguation needed");
			return new GetNameDisambiguationResponse(aliases.get(0).getAlias());
		} else {
			//More than one candidate item found
			LOGGER.log(Level.INFO, "Adding name disambiguation to queue");
			if (aliases.get(0).getDistance() > LevenshteinDistanceCalculator.PERFECT_THRESHOLD && !checkHomonyms(aliases)) {
				//If one of the candidate items has a perfect match with the sentence, no need to disambiguate
				return new GetNameDisambiguationResponse(aliases.get(0).getAlias());
			} else {
				//Need to disambiguate on the name
				List<Alias> filteredOptions = new ArrayList<Alias>();
				if (checkHomonyms(aliases)) {
					for (FilteredAlias a: getHomonyms(aliases)) {
						filteredOptions.add(new Alias(a.getAlias().getURI(), getLabelFromDb(a.getAlias())));
					}
				} else {
					for (FilteredAlias a: aliases) {
						filteredOptions.add(new Alias(a.getAlias().getURI(), getLabelFromDb(a.getAlias())));
					}
				}
				return new GetNameDisambiguationResponse(new PendingEvaluation(null, rating, filteredOptions, type));
			}

		}
	}
	
	/**
	 * Checks if there are no homonyms in the list of candidate objects. Useful when
	 * there are items with the same label but different URI
	 */
	private boolean checkHomonyms(List<FilteredAlias> aliases) {
		boolean found = aliases.size() > 1 
				&& aliases.get(0).getDistance() > LevenshteinDistanceCalculator.PERFECT_THRESHOLD 
				&& aliases.get(0).getAlias().getLabel().equals(aliases.get(1).getAlias().getLabel());
		if (found) {
			LOGGER.log(Level.INFO, "Found homonyms: " + aliases.get(0) + " " + aliases.get(1));
		}
		return found;
	}
	
	/**
	 * Returns all the aliases that obtained a perfect match with the user sentence 
	 * @param aliases The list of candidate items
	 * @return A filtered list of the aliases that have a perfect match
	 */
	private List<FilteredAlias> getHomonyms(List<FilteredAlias> aliases) {
		List<FilteredAlias> homonyms = new ArrayList<>();
		boolean search = true;
		int i = 0;
		while (i < aliases.size() && search) {
			FilteredAlias alias = aliases.get(i);
			if (alias.getDistance() < LevenshteinDistanceCalculator.PERFECT_THRESHOLD) {
				search = false;
			} else {
				homonyms.add(alias);
				i++;
			}
		}
		return homonyms;
	}
	
	/**
	 * Obtain the label for a given Alias object
	 * @param alias An alias object representing an item
	 * @return The label for the alias
	 */
	private String getLabelFromDb(Alias alias) {
		EntityService es = new EntityService();
		String label = es.getEntityLabel(alias.getURI());
		if (label == null) {
			label = new PropertyService().getPropertyLabel(alias.getURI());
		}
		if (label == null) {
			//Come fallback, si usa la label del property extractor
			LOGGER.log(Level.INFO, "No label found in DB for " + alias.getURI() + "!");
			label = alias.getLabel();
		}
		return label;
	}
	
	/**
	 * Given an item, check whether a disambiguation on the property type is needed.
	 * The Alias item must represent a property. Retrieves the list of property types
	 * associated to it, and if there is more than one property type, it returns a 
	 * disambiguation request on the property type. If only one property type is found,
	 * then no disambiguation will be requested.
	 * @param entity The property to be evaluated
	 * @param rating The user's rating
	 * @return A GetPropertyTypeDisambiguation object containing the outcome of the operation
	 */
	public GetPropertyTypeDisambiguationResponse getPropertyTypeDisambiguation(Alias entity, int rating) {
		PropertyService ps = new PropertyService();
		Configuration configuration = Configuration.getDefaultConfiguration();
		String uri = entity.getURI();
		//Get the property types for the item (e.g. "Tom Cruise"->"actor, director...")
		List<String> propertyTypes = new PropertyService().getPropertyTypes(uri).get(uri);
		if (propertyTypes != null && propertyTypes.size() > 0) {
			if (propertyTypes.size() == 1) {
				//Only one property type
				LOGGER.log(Level.INFO, "propertyTypes.size() is 1! No property type disambiguation needed.");
				return new GetPropertyTypeDisambiguationResponse(propertyTypes.get(0));
			} else {
				//More than one property type
				List<Alias> aliases = new ArrayList<Alias>();
				for (String propertyType: propertyTypes) {
					aliases.add(new Alias(propertyType, configuration.getPropertyTypesLabels().get(propertyType)));
				}
				LOGGER.log(Level.INFO, "Property types found. Adding property type disambiguation in the queue");
				return new GetPropertyTypeDisambiguationResponse(new PendingEvaluation(entity, 
								rating,
								aliases, 
								PendingEvaluationType.PROPERTY_TYPE_DISAMBIGUATION));
			}
		} else {
			LOGGER.log(Level.INFO, "Property type list is empty!");
			return new GetPropertyTypeDisambiguationResponse(false);
		}
	}
	
	/**
	 * Effettua la disambiguazione vera e propria. Data una frase, e una lista di opzioni possibili,
	 * il metodo cerca se all'interno della frase ci sono menzioni delle opzioni specificate. Restituisce
	 * una lista di interi, contenente gli indici dei possibleValues che sono stati riconosciuti all'interno
	 * della frase.
	 */
	
	/**
	 * Executes the disambiguation. Given the user's disambiguation sentence, checks if it contains
	 * one or more of the options specified in the possibleValues list
	 * @param sentence User's disambiguation message
	 * @param possibleValues List of possible options
	 * @return A list of integers, each integer is the index of one of the options that were chosen 
	 * by the user
	 */
	public List<Integer> disambiguate(String sentence, List<String> possibleValues) {
		List<Integer> result = new ArrayList<>();
		LOGGER.log(Level.INFO, "Called disambiguate");
		String cleanText = sentence.replaceAll("[,.!?\\-\\[\\]]", "");
		MatchMap matches = LevenshteinDistanceCalculator.findMatches(cleanText, possibleValues, 0.90);
		List<List<Match>> groups = matches.getGroupedMatches();
		LOGGER.log(Level.INFO, "Found " + groups.size() + " possible matches");
		for (List<Match> group: groups) {
			LOGGER.log(Level.INFO, "Processing group " + group);
			List<DistanceMeasure> distances = new ArrayList<>();
			for (Match m: group) {
				distances.addAll(matches.get(m));
			}
			Collections.sort(distances);
			Collections.reverse(distances);
			if (distances.size() > 0) {
				LOGGER.log(Level.INFO, "Index of the best value is " + distances.get(0).getIndex());
				result.add(distances.get(0).getIndex());
			} else {
				LOGGER.log(Level.INFO, "No index found");
			}
		}
		return result;
	}
}
