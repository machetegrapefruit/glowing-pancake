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
 * Inserisce il film raccomandato nella tabella e aggiorna il rating, il refine e il refocus se la chiave esiste già
 * @author Francesco
 *
 */

@Path("/ratingsRecEntity")
public class PutRecEntityRating {
	
	//Tomcat non permette la put, ci sara' un modo per configurarlo attraverso web.xml
	//ma per il momento la camuffiamo in get
	@Context ServletContext servletContext;
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/putRecEntityRating")
	public String putRecEntityRating(@QueryParam("userID") String userID,
								   	 @QueryParam("entityURI") String entityURI,
								   	 @QueryParam("numberRecommendationList") String numberRecommendationList,
								   	 @QueryParam("rating") String rating,
								   	 @QueryParam("position") String position,
								   	 @QueryParam("pagerankCicle") String pagerankCicle,
								   	 @QueryParam("refine") String refine,
								   	 @QueryParam("refocus") String refocus,
								   	 @QueryParam("botName") String botName,
								   	 @QueryParam("messageID") String messageID,
								   	 @QueryParam("botTimestamp") String botTimestamp,
								   	 @QueryParam("recommendatinsList") String recommendatinsList,
								   	 @QueryParam("ratingsList") String ratingsList
								   	 ) throws Exception 
	{
		int r = Integer.parseInt(rating);
		int pos = Integer.parseInt(position);
		int pagerank_cicle = Integer.parseInt(pagerankCicle);
		int bot_timestamp = Integer.parseInt(botTimestamp);
		int number_recommendation_list = Integer.parseInt(numberRecommendationList);
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
				
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		entityURI = asController.getEntityUriFromEntities(entityURI); //Controllo l'esistenza del film tra tutti i film
		int numberRatedEntities;
		
		if (!entityURI.equalsIgnoreCase("null")) {
			asController.insertRecEntityRatedByUser(userID, entityURI,number_recommendation_list, r, pos, pagerank_cicle, refine, refocus, botName, messageID, bot_timestamp, recommendatinsList, ratingsList);
			asController.putLastChange(userID, "entity_rating");
		}
		else {
			System.err.println("/putRecEntityRating/Error - insertRecEntityRatingByUser userID: " + userID + " entityURI:" + entityURI);
		}		
		numberRatedEntities = asController.getNumberRatedEntities(userID);		
		Gson gson = new Gson();
		String json = gson.toJson(numberRatedEntities);
			
		System.out.print("/putRecEntityRating/userID: " + userID + " entityURI:" + entityURI + " numberRecommendationList:" + numberRecommendationList + " refine:" + refine + " refocus:" + refocus + "/");
		System.out.println(json);
		
		return json;		
	}	
}






