package recommendationService;

import java.util.List;

import functions.EntityService;
import graph.AdaptiveSelectionController;
import utils.PropertyFilter;

public final class RecommendationUtilities {

	private RecommendationUtilities() {
		// Cannot instantiate
	}
	
	protected static final List<String> getRecommendations(
			String user, List<PropertyFilter> filters, boolean forcePagerank) {

		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		EntityService entityService = new EntityService();
		
		String userID = user;
		
		List<String> recommended = null;

		String lastChange;
		try {
			lastChange = asController.getLastChange(userID);
			//controllo per eseguire il pagerank solo se c'è stato un cambiamento
			boolean check = true;
			if (!forcePagerank && (lastChange.equals("pagerank") || lastChange.equals("refine"))) {
				check = false;
			}
			
			//con propertyType = entity parte il pagerank per aggiornare la lista dei film raccomandati
			//if (propertyType.equals("entity") && (!lastChange.equals("pagerank") || !lastChange.equals("refine"))) {
			if (check == true) {
				asController.getUserRaccomandations(userID, filters);
				
				//Incremento di pagerankCicle e numberRecommendationList
				int oldPagerankCicle = asController.getNumberPagerankCicle(userID);
				int oldNumberRecommendationList = asController.getNumberRecommendationList(userID);
				asController.putNumberPagerankCicleByUser(userID, oldPagerankCicle + 1);
				asController.putNumberRecommendationListByUser(userID, oldNumberRecommendationList + 1);
				recommended = entityService.getCachedRecommendedEntities(userID);
				//recommended = new ArrayList<String>(asController.getPropertyValueListMapFromPropertyType(userID, "entity").values());
			} else {
				recommended = entityService.getCachedRecommendedEntities(userID);
				//recommended = new ArrayList<String>(asController.getPropertyValueListMapFromPropertyType(userID, "entity").values());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return recommended;
	}
}
