package functions;

import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import configuration.Configuration;
import dialog.DialogState;
import entity.Entity;
import restService.GetNumberService;
import restService.PutDetailsEntityRequest;
import utils.TextUtils;

public class EntityRatingAcquisition {
	
	public class AllPopularEntitiesRatedException extends Exception {
		
	}

	private String userID;
	
	private String entityURI;
	
	private DialogState state;
	
	public EntityRatingAcquisition(String userID, DialogState state) {
		
		this.userID = userID;
		this.state = state;
	}
	
	public void obtainEntityToRate() throws Exception {

		EntityService service = new EntityService();
		String entityURI = service.getEntityToRate(userID);
		
		boolean alreadyRated = false;
		ProfileService profileService = new ProfileService();
		JsonArray profile = profileService.getUserProfileWithSkips(userID);
		System.out.println("Profile: " + profile);
		
		int infiniteLoopCheck = 0;
		do {
			alreadyRated = false;
			
			for (Iterator<JsonElement> iterator = profile.iterator(); iterator.hasNext();) {
				JsonElement element = iterator.next();
				String uri = element.getAsJsonObject().get("label").getAsString();
				System.out.println("entityURI: " + entityURI + "; uri: " + uri);
				if (entityURI.equals(uri)) {
					System.out.println("Already rated");
					alreadyRated = true;
					entityURI = service.getEntityToRate(userID);
					break;
				}
			}
			if (infiniteLoopCheck++ > 100) {
				throw new AllPopularEntitiesRatedException();
			}
		} while (alreadyRated);
						
		this.entityURI = entityURI;
	}
	
	public void setEntityToRate(String entityURI) {
		this.entityURI = entityURI;
	}
	
	public String getEntityToRate() {
		return this.entityURI;
	}
	
	public void putEntityToRate() throws Exception {
		
		state.setEntityToRate(entityURI);
		
		EntityService service = new EntityService();
		service.insertEntityToRate(userID, entityURI, 0);
	}
	
	/**
	 * 
	 * @return The NAME of the entity.
	 * @throws Exception
	 */
	public String getEntityToRateSelected() throws Exception {
		
		String entity = state.getEntityToRate();

		return entity;
	}
	
	/**
	 * 
	 * @param entityName
	 * @return new String[] {new String(title), new String(poster)}
	 * @throws Exception
	 */
	public String[] getTitleAndPoster(String entityName) throws UnknownTitleException, Exception {
	
		String[] titleAndPoster = null;
		
		String name = TextUtils.getNameFromURIKeepBrackets(entityName);
		try {
			titleAndPoster = getTitleAndPosterUtility(name);
		} catch (UnknownTitleException e) {
			name = TextUtils.getNameFromURIDeleteBrackets(entityName);
			try {
				titleAndPoster = getTitleAndPosterUtility(name);
			} catch (UnknownTitleException f) {
				titleAndPoster = new String[] {entityName, null};
			}
		}
		
		String title = titleAndPoster[0];
		String poster = titleAndPoster[1];
		
		System.out.println("Title: " + title);
		System.out.println("Poster: " + poster);
		
		// Elimino spazi bianchi dall'URL del poster per l'invio in JSON
		if (poster != null) {
			poster = poster.replace(" ", "%20");
		} else {
			poster = "null";
		}

		return new String[] {
				new String(title),
				new String(poster)
		};
	}
	
	private String[] getTitleAndPosterUtility(String entityName) throws UnknownTitleException {
		String title = null;
		String poster = null;
		
		EntityService entityService = new EntityService();
System.out.println("entityName: " + entityName);
		String entityURI = entityService.getEntityURI(entityName);
System.out.println("entityURI: " + entityURI);
		Entity properties = entityService.getEntityDetails(entityURI);
System.out.println("properties: " + properties);
		String propertyTypeName = Configuration.getDefaultConfiguration().getPropertyTypeName();
		String propertyTypeImage = Configuration.getDefaultConfiguration().getPropertyTypeImage();
		
		if (properties.get(propertyTypeName) != null) {
			title = properties.get(propertyTypeName).get(0);
		}
		if (properties.get(propertyTypeImage) != null) {
			poster = properties.get(propertyTypeImage).get(0);
		}
		
		if (title == null) {
			throw new UnknownTitleException();
		}
		
		return new String[] {
				title,
				poster
		};
	}
	
	public void putDetailsRequest() throws Exception {
		
		String entityURI = getEntityToRateSelected();
		
		GetNumberService numberService = new GetNumberService();
		String numberRecommendationList = numberService.getNumberRecommendationList(userID);
		
		String details = "details";
		
		PutDetailsEntityRequest putDetailsEntityRequest = new PutDetailsEntityRequest();
		putDetailsEntityRequest.putDetailsEntityRequest(userID, entityURI, numberRecommendationList, details);
	}
	
	public void putEntityRating(int rating, String lastChange) throws Exception {

		String entityURI = this.getEntityToRateSelected();

		System.out.println("[EntityRatingAcquisition.putEntityRating] Sto mettendo like a " + entityURI);

		EntityService entityService = new EntityService();
		entityURI = entityService.getEntityURI(TextUtils.getNameFromURI(entityURI));
		entityService.addEntityPreference(userID, entityURI, rating, lastChange);
		
	}
}
