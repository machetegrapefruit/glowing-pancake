package functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graph.AdaptiveSelectionController;

public class PropertyService {

	public Map<String, List<String>> getPropertyTypes(String propertyURI) {
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		Map<String, List<String>> propertyTypes = new HashMap<String, List<String>>();
		try {
			propertyTypes = asController.getPropertyTypeFromPropertyValue(propertyURI);
			
			//Tratta i valori di proprietà che non hanno l'uri
			/*if (propertyTypes.isEmpty()) {			
				propertyTypes = asController.getPropertyTypeFromPropertyValue(propertyName);
			}*/		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return propertyTypes;
	}
	
	public boolean addPropertyPreference(
			String userID, 
			String propertyURI, 
			String propertyTypeURI,
			int rating,
			String lastChange
			) {
		boolean done = false;
		try {
			AdaptiveSelectionController asController = new AdaptiveSelectionController();

			/*if (!propertyTypeURI.equals("runtimeRange") || !propertyTypeURI.equals("releaseYear") ) {
				propertyTypeURI = asController.getResourceUriFromDbpediaMoviesSelection(propertyTypeURI);		//controllo l'esistenza delle property
				propertyURI = asController.getResourceUriFromDbpediaMoviesSelection(propertyURI);				//e risolvo il problema del case_sensitive
			}*/	
			
			if (!propertyTypeURI.equalsIgnoreCase("null") && !propertyURI.equalsIgnoreCase("null")) {
				asController.putPropertyRatedByUser(userID,propertyTypeURI, propertyURI, rating, lastChange);
				if (rating == 1 || rating == 2 ) {
					asController.putLastChange(userID, "property_rating");
				}
				done = true;
			}
			else {
				System.err.println("Error - addPropertyPreference userID: " + userID + " propertyURI:" + propertyURI);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return done;
	}
	
	/**
	 * Elimina tutte le preferenze delle proprietà dell'utente specificato
	 */
	public boolean deleteAllRatedProperties(String userID) {
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		int oldNumberRatedProperties;
		try {
			oldNumberRatedProperties = asController.getNumberRatedProperties(userID);
			
			asController.deleteAllPropertyRatedByUser(userID);
			int numberRatedProperties = asController.getNumberRatedProperties(userID);
			
			if (numberRatedProperties == 0 || numberRatedProperties < oldNumberRatedProperties ) {			
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public String getPropertyLabel(String uri) {
		return new AdaptiveSelectionController().getPropertyLabel(uri);
	}
	
	public String getPropertyFromMovie(String movie_uri, String userID){
		return new AdaptiveSelectionController().getPropertyFromMovie(movie_uri, userID);
	}
	
	public String getPropertyFromMovies(String movie_uri, String userID){
		return new AdaptiveSelectionController().getPropertyFromMovies(movie_uri, userID);
	}
	
	public boolean isPropertyObject(String uri) {
		return new AdaptiveSelectionController().getPropertyLabel(uri) != null;
	}
	
	public String getPropertyURI(String label) {
		return new AdaptiveSelectionController().getPropertyURI(label);
	}

	public List<String> getPropertyValues(String propertyTypeURI) {
		return new AdaptiveSelectionController().getPropertyValues(propertyTypeURI);
	}
	
	public List<String> getPropertyValuesSortedByPopularity(String propertyTypeURI) {
		return new AdaptiveSelectionController().getPropertyValuesSortedByPopularity(propertyTypeURI);
	}

	
}
