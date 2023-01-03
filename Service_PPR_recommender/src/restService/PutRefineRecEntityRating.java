package restService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletContext;
//import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
//import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import functions.EntityService;
import graph.AdaptiveSelectionController;

/**
 * 
 * @author Francesco Baccaro
 *
 */

@Path("/userRefineRecEntityRating")
public class PutRefineRecEntityRating {
	
	//Tomcat non permette la put, ci sara' un modo per configurarlo attraverso web.xml
	//ma per il momento la camuffiamo in get
	@Context ServletContext servletContext;
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/putRefineRecEntityRating")
	public String putRefineRecEntityRating(@QueryParam("userID") String userID,
								   	 @QueryParam("entityURI") String entityURI,
								   	 @QueryParam("numberRecommendationList") String numberRecommendationList,
								   	 @QueryParam("refine") String refine
								   	 ) throws Exception 
	{

		EntityService service = new EntityService();
		entityURI = entityURI.replace("http://dbpedia.org/resource/", "");
		entityURI = entityURI.replace("_", " ");
		String entityObjectUri = service.getEntityURI(entityURI);
		System.out.println("entityObjectUri: " + entityObjectUri);
		
		int number_recommendation_list = Integer.parseInt(numberRecommendationList);
		AdaptiveSelectionController asController = new AdaptiveSelectionController();

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		entityObjectUri = asController.getEntityUriFromEntities(entityObjectUri); //Controllo l'esistenza del film tra tutti i film
		int numberRatedEntities;
		
		if (!entityURI.equalsIgnoreCase("null")) {
			asController.insertRefineRecEntityRatingByUser(userID, entityObjectUri, number_recommendation_list, refine);
			//asController.putLastChange(userID, "pagerank");
		}
		else {
			System.err.println("/putRefineRecEntityRating/Error - insertRefineRecEntityRating userID: " + userID + " entityURI:" + entityURI);
		}		
		numberRatedEntities = asController.getNumberRatedEntities(userID);		
		Gson gson = new Gson();
		String json = gson.toJson(numberRatedEntities);
			
		System.out.print("/putRefineRecEntityRating/userID: " + userID + " entityURI:" + entityURI + " numberRecommendationList:" + numberRecommendationList + "/");
		System.out.println(json);
		
		return json;		
	}	
}







