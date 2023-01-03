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

import graph.AdaptiveSelectionController;

/**
 * Inserisce il film raccomandato nella tabella
 * @author Francesco
 */

@Path("/userDislikeRecEntityRating")
public class PutDislikeRecEntityRating {
	
	//Tomcat non permette la put, ci sara' un modo per configurarlo attraverso web.xml
	//ma per il momento la camuffiamo in get
	@Context ServletContext servletContext;
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/putDislikeRecEntityRating")
	public String putDislikeRecEntityRating(@QueryParam("userID") String userID,
								   	 @QueryParam("entityURI") String entityURI,
								   	 @QueryParam("numberRecommendationList") String numberRecommendationList,
								   	 @QueryParam("dislike") String dislike
								   	 ) throws Exception 
	{
		int number_recommendation_list = Integer.parseInt(numberRecommendationList);
		int r = Integer.parseInt(dislike);
		AdaptiveSelectionController asController = new AdaptiveSelectionController();

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		entityURI = asController.getEntityUriFromEntities(entityURI); //Controllo l'esistenza del film tra tutti i film
		int numberRatedEntities;
		
		if (!entityURI.equalsIgnoreCase("null")) {
			asController.insertDislikeRecEntityRatingByUser(userID, entityURI, number_recommendation_list, r);
			asController.putLastChange(userID, "pagerank");
		}
		else {
			System.err.println("/putDislikeRecEntityRating/Error - insertDislikeRecEntityRating userID: " + userID + " entityURI:" + entityURI);
		}		
		numberRatedEntities = asController.getNumberRatedEntities(userID);		
		Gson gson = new Gson();
		String json = gson.toJson(numberRatedEntities);
			
		System.out.print("/putDislikeRecEntityRating/userID: " + userID + " entityURI:" + entityURI + " numberRecommendationList:" + numberRecommendationList + "/");
		System.out.println(json);
		
		return json;		
	}	
}







