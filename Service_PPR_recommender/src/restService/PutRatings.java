package restService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import functions.EntityService;
import functions.PropertyService;
import graph.AdaptiveSelectionController;

@Path("/putRatings")
public class PutRatings {

	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/putEntityRating")
	public String putEntityRating(@QueryParam("userID") String userID,
							   	 @QueryParam("entityURI") String entityURI,
							   	 @QueryParam("rating") String rating,
							   	 @QueryParam("lastChange") String lastChange) throws Exception 
	{
		System.out.println("Rating: " + rating);
		
		Integer r = Integer.parseInt(rating);
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		EntityService service = new EntityService();
		
		String entityName = entityURI.replace("http://dbpedia.org/resource/", "")
				.replace("_", " ");
		if (entityName.contains("(")) {
			entityName = entityName.substring(0, entityName.indexOf("("));
		}
		
		String entityObjectUri = service.getEntityURI(entityName);

		entityObjectUri = asController.getEntityUriFromEntities(entityObjectUri); //Controllo l'esistenza del film tra tutti i film
		System.out.println("entityObjectUri: " + entityObjectUri);
		int numberRatedEntities;
		
		if (!entityObjectUri.equalsIgnoreCase("null")) {
			asController.putEntityRatedByUser(userID, entityObjectUri, r, lastChange);
			if (r.equals(1) || r.equals(2) ) {
				service.addEntityPreference(userID, entityObjectUri, r, lastChange);
				
				asController.putLastChange(userID, lastChange);
			}
		}
		else {
			System.err.println("Error - insertEntityRatedByUser userID: " + userID + " entityURI:" + entityURI);
		}		
		numberRatedEntities = asController.getNumberRatedEntities(userID);		
		Gson gson = new Gson();
		String json = gson.toJson(numberRatedEntities);
		
		System.out.print("/putEntityRating/");
		System.out.println(json);
		
		return json;		
	}	
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/putPropertyRating")
	public String putPropertyRating(@QueryParam("userID") String userID,
									@QueryParam("propertyTypeURI") String propertyTypeURI,
							   		@QueryParam("propertyURI") String propertyURI,
							   		@QueryParam("rating") String rating,
							   		@QueryParam("lastChange") String lastChange) throws Exception 
	{
		Integer r = Integer.parseInt(rating);

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		PropertyService service = new PropertyService();

		AdaptiveSelectionController asController = new AdaptiveSelectionController();

		int numberRatedProperties;

		if (!propertyTypeURI.equalsIgnoreCase("null") && !propertyURI.equalsIgnoreCase("null")) {
System.out.println("debug propertyType: " + propertyTypeURI);
			String propertyType = propertyTypeURI.replace("http://dbpedia.org/ontology/", "");
			propertyType = propertyType.replace("http://purl.org/dc/terms/subject", "category");
			System.out.println("debug propertyType: " + propertyType);

			String property = propertyURI.replace("http://dbpedia.org/resource/", "");
			property = property.replace("_", " ");
			property = service.getPropertyURI(property);
			System.out.println("Property type: " + propertyType);
			System.out.println("Property value: " + property);
			
            service.addPropertyPreference(userID, property, propertyType, r, lastChange);
			asController.putPropertyRatedByUser(userID, propertyType, property, r, lastChange);

			if (r.equals(1) || r.equals(2) ) {

				asController.putLastChange(userID, "property_rating");
			}
		}
		else {
			System.err.println("Error - putPropertyRating userID: " + userID + " propertyURI:" + propertyURI);
		}
		numberRatedProperties = asController.getNumberRatedProperties(userID);				
		
		Gson gson = new Gson();
		String json = gson.toJson("null");
		json = gson.toJson(numberRatedProperties);		
		
		System.out.print("/putPropertyRating?userID=" + userID  + "&propertyTypeURI=" + propertyTypeURI + "&propertyURI=" + propertyURI + "&rating=" + rating +"/");
		System.out.println(json);
		
		return json;		
	}	
}
