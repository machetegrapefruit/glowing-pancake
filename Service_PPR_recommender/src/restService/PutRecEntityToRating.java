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
import utils.TextUtils;

/**
 * Inserisce il film raccomandato nella tabella
 * @author Francesco
 *
 */

@Path("/userRecEntityToRating")
public class PutRecEntityToRating {
	
	//Tomcat non permette la put, ci sara' un modo per configurarlo attraverso web.xml
	//ma per il momento la camuffiamo in get
	@Context ServletContext servletContext;
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/putRecEntityToRating")
	public String putRecEntityToRating(@QueryParam("userID") String userID,
								   	 @QueryParam("entityURI") String entityURI,
								   	 @QueryParam("numberRecommendationList") String numberRecommendationList,
								   	 @QueryParam("position") String position,
								   	 @QueryParam("pagerankCicle") String pagerankCicle,
								   	 @QueryParam("botName") String botName,
								   	 @QueryParam("botTimestamp") String botTimestamp
								   	 ) throws Exception 
	{
		int pos = Integer.parseInt(position);
		int pagerank_cicle = Integer.parseInt(pagerankCicle);
		int bot_timestamp = Integer.parseInt(botTimestamp);
		int number_recommendation_list = Integer.parseInt(numberRecommendationList);
		AdaptiveSelectionController asController = new AdaptiveSelectionController();

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		System.out.println("/.putRecEntityToRating/userID: " + userID + " entityURI:" + entityURI + " numberRecommendationList:" + numberRecommendationList + "/");
		
		EntityService es = new EntityService();
		entityURI = es.getEntityURI(TextUtils.getNameFromURIKeepBrackets(entityURI));
		
		System.out.println("/..putRecEntityToRating/userID: " + userID + " entityURI:" + entityURI + " numberRecommendationList:" + numberRecommendationList + "/");
		
		int numberRatedEntities;
		
		if(entityURI != null && !entityURI.isEmpty()){
			asController.insertRecEntityToRatingByUser(userID, entityURI,number_recommendation_list, pos, pagerank_cicle, botName, "0", bot_timestamp);
		}
		else {
			System.err.println("/putRecEntityToRating/Error - insertRecEntityToRatingByUser userID: " + userID + " entityURI:" + entityURI);
		}		
		numberRatedEntities = asController.getNumberRatedEntities(userID);		
		Gson gson = new Gson();
		String json = gson.toJson(numberRatedEntities);
			
		System.out.print("/putRecEntityToRating/userID: " + userID + " entityURI:" + entityURI + " numberRecommendationList:" + numberRecommendationList + "/");
		System.out.println(json);
		
		return json;		
	}	
}






