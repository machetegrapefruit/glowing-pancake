package functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import configuration.Configuration;
import dialog.FilteredAlias;
import graph.AdaptiveSelectionController;
import test.RecEntityRating;
import utils.Alias;

public class ProfileService {
	
	protected static Logger LOGGER = Logger.getLogger(ProfileService.class.getName());
	
	/**
	 * Elimina l'intero profilo dell'utente specificato
	 */
	public boolean deleteUserProfile(String userID) {
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		int oldNumberPagerankCicle;
		try {
			oldNumberPagerankCicle = asController.getNumberPagerankCicle(userID);
			asController.deleteAllProfileByUser(userID);
			int numberPagerankCicle = asController.getNumberPagerankCicle(userID);
			
			if (numberPagerankCicle == 0 || numberPagerankCicle < oldNumberPagerankCicle ) {			
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks the conditions that need to be activated before the questionnaire can be submitted. The test is 
	 * considered complete when the user has rated the recommended item positively.
	 * @param userID ID of the user to check
	 * @return True if the test is over and the questionnaire can be submitted to the user
	 */
	public boolean checkQuestionnaireConditions(String userID) {
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		int maxCycles = Configuration.getDefaultConfiguration().getMaxCycles();
		try {
			int numRecList = asController.getNumberPagerankCicle(userID);
			boolean likedRecommendation = false;
			List<RecEntityRating> currentRatings = asController.getRecEntityRatings(userID);
			// Sort ratings by the numberRecommendationList (descending order)
//			Collections.sort(currentRatings);
//			Collections.reverse(currentRatings);
			int firstRecList = currentRatings.get(0).getNumberRecommendationList();
			int i = 0;
			boolean found = false;
			// Get the first recommendation list associated to the current user session 
			while (!found && i < currentRatings.size()) {
				int recList = currentRatings.get(i).getNumberRecommendationList();
				int pCycle = currentRatings.get(i).getPagerankCycle();
				LOGGER.log(Level.INFO, "Looking at rating " + recList + " pagerankCycle " + pCycle);
				if (pCycle == 1) {
					firstRecList = recList;
					found = true;
					LOGGER.log(Level.INFO, "Stopping search");
				}
				i++;
			}
			
			LOGGER.log(Level.INFO, "First recommendation list is " + firstRecList);
			i = 0;
			
			// Search for a liked recommendation, but ignoring the recommendations received before a profile reset
			while (i < currentRatings.size() && !likedRecommendation && currentRatings.get(i).getNumberRecommendationList() >= firstRecList) {
				RecEntityRating rating = currentRatings.get(i);
				if (rating.isLike()) {
					LOGGER.log(Level.INFO, "Found liked recommendation in recommendation list " + rating.getNumberRecommendationList());
					// The user has found a good recommendation
					likedRecommendation = true;
				}
				i++;
			}		
			return (numRecList >= maxCycles) || likedRecommendation;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean hasPositiveRating(String userID) {
		boolean hasPositiveRating = false;
		JsonArray profile = this.getUserProfile(userID);
		for (Iterator<JsonElement> iterator = profile.iterator(); iterator.hasNext();) {
			JsonElement element = iterator.next();
			if (element.getAsJsonObject().get("rating").getAsInt() == 1) {
				hasPositiveRating = true;
			}
		}
		return hasPositiveRating;
	}
	
	public JsonArray getUserProfileWithSkips(String userID) {
		Configuration c = Configuration.getDefaultConfiguration();
		EntityService es = new EntityService();
		PropertyService ps = new PropertyService();
		JsonArray result = new JsonArray();
		try {
			AdaptiveSelectionController asController = new AdaptiveSelectionController();
			Map<String, Integer> entityOrPropertyToRatingMap = new HashMap<String, Integer>();
			entityOrPropertyToRatingMap = asController.getRatingsForUserFromRatings(userID);
			for (String key: entityOrPropertyToRatingMap.keySet()) {
				String type;
				JsonObject ratingJson = new JsonObject();
				if (key.startsWith("entity")) {
					type = "entity";
					ratingJson.addProperty("name", key.split(",")[1]);
					ratingJson.addProperty("label", es.getEntityLabel( key.split(",")[1]));
				} else {
					String[] keySplit = key.split(",");
					type = keySplit[0];
					ratingJson.addProperty("name", keySplit[1]);
					ratingJson.addProperty("label", ps.getPropertyLabel(keySplit[1]));
					ratingJson.addProperty("typeLabel", c.getPropertyTypesLabels().get(type));
				}
				ratingJson.addProperty("type", type);
				ratingJson.addProperty("rating", entityOrPropertyToRatingMap.get(key));
				result.add(ratingJson);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public JsonArray getUserProfile(String userID) {
		Configuration c = Configuration.getDefaultConfiguration();
		EntityService es = new EntityService();
		PropertyService ps = new PropertyService();
		JsonArray result = new JsonArray();
		try {
			AdaptiveSelectionController asController = new AdaptiveSelectionController();
			Map<String, Integer> entityOrPropertyToRatingMap = new HashMap<String, Integer>();
			entityOrPropertyToRatingMap = asController.getPosNegRatingForUserFromRatings(userID);
			for (String key: entityOrPropertyToRatingMap.keySet()) {
				String type;
				JsonObject ratingJson = new JsonObject();
				if (key.startsWith("entity")) {
					type = "entity";
					ratingJson.addProperty("name", key.split(",")[1]);
					ratingJson.addProperty("label", es.getEntityLabel( key.split(",")[1]));
				} else {
					String[] keySplit = key.split(",");
					type = keySplit[0];
					ratingJson.addProperty("name", keySplit[1]);
					ratingJson.addProperty("label", ps.getPropertyLabel(keySplit[1]));
					ratingJson.addProperty("typeLabel", c.getPropertyTypesLabels().get(type));
				}
				ratingJson.addProperty("type", type);
				ratingJson.addProperty("rating", entityOrPropertyToRatingMap.get(key));
				result.add(ratingJson);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public void deletePreference(String userID, String uri) {
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		try {
			asController.deletePropertyRatedByUser(userID, uri);
			asController.deleteEntityRatedByUser(userID, uri);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Filtra la lista degli alias selezionando solo quelli che si riferiscono a entità
	 * o proprietà valutate dall'utente
	 * @param aliases
	 * @return
	 */
	public List<FilteredAlias> filterAliasesByUserProfile(String userID, List<FilteredAlias> aliases) {
		List<FilteredAlias> filteredAliases = new ArrayList<>();
		JsonArray userProfile = getUserProfile(userID);
		
		//Prendi tutte le preferenze dell'utente
		List<Alias> profileElems = new ArrayList<>();
		for (int i = 0; i < userProfile.size(); i++) {
			JsonObject elem = userProfile.get(i).getAsJsonObject();
			String uri = elem.get("name").getAsString();
			String label = elem.get("label").getAsString();
			profileElems.add(new Alias(uri, label));
		}
		
		//Aggiungi solo gli alias che sono contenuti anche nel profilo utente
		for (FilteredAlias a: aliases) {
			if (profileElems.contains(a.getAlias())) {
				filteredAliases.add(a);
			}
		}
		
		return filteredAliases;
	}
	
	public int getPreferencesCount(String userID) {
		Configuration c = Configuration.getDefaultConfiguration();
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		try {
			if (c.isPropertyTypeDisambiguationEnabled()) {
				return asController.getNumberRatedEntities(userID) + asController.getNumberRatedProperties(userID);
			} else {
				return asController.getNumberRatedEntities(userID) + asController.getNumberRatedPropertiesWithoutDuplicates(userID);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public int getNumRatedRecommendedEntities(String userID) {
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		try {
			return asController.getNumberRatedRecEntityByUserAndRecList(userID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public void doRefocus(String userID) {
		AdaptiveSelectionController asController = new AdaptiveSelectionController();

		//se il numero di film raccomandati valutati è zero puoi avviare il refocus
		try {
			asController.setRefocusRecListByUser(userID);
			asController.putLastChange(userID, "entity_rating");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getPagerankCycle(String userID) {
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		try {
			return asController.getNumberPagerankCicle(userID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public int getNumberRecommendationList(String userID) {
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		try {
			return asController.getNumberRecommendationList(userID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public static void main(String[] args) {
//		System.out.println(new ProfileService().getUserProfile("19371450"));
		
//		List<RecEntityRating> currentRatings = new ArrayList<RecEntityRating>();
//		currentRatings.add(new RecEntityRating("1", "1", 2, false, true, 0, 0, 2, false, false, false, false, null, null, 0, 0));
//		currentRatings.add(new RecEntityRating("1", "1", 1, false, true, 0, 0, 1, false, false, false, false, null, null, 0, 0));
//		int maxCycles = Configuration.getDefaultConfiguration().getMaxCycles();
//		boolean res = false;
//		try {
//			int numRecList = 2;
//			boolean likedRecommendation = false;
//			// Sort ratings by the numberRecommendationList (descending order)
//			Collections.sort(currentRatings);
//			Collections.reverse(currentRatings);
//			int firstRecList = currentRatings.get(0).getNumberRecommendationList();
//			int i = 0;
//			boolean found = false;
//			// Get the first recommendation list associated to the current user session 
//			while (!found && i < currentRatings.size()) {
//				int recList = currentRatings.get(i).getNumberRecommendationList();
//				int pCycle = currentRatings.get(i).getPagerankCycle();
//				if (pCycle == 1) {
//					firstRecList = recList;
//					found = true;
//				}
//				i++;
//			}
//			
//			LOGGER.log(Level.INFO, "First recommendation list is " + firstRecList);
//			i = 0;
//			
//			// Search for a liked recommendation, but ignoring the recommendations received before a profile reset
//			while (i < currentRatings.size() && !likedRecommendation && currentRatings.get(i).getNumberRecommendationList() >= firstRecList) {
//				RecEntityRating rating = currentRatings.get(i);
//				if (rating.isLike()) {
//					LOGGER.log(Level.INFO, "Found liked recommendation in recommendation list " + rating.getNumberRecommendationList());
//					// The user has found a good recommendation
//					likedRecommendation = true;
//				}
//				i++;
//			}		
//			res = (numRecList >= maxCycles) || likedRecommendation;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(res);
	}
}
