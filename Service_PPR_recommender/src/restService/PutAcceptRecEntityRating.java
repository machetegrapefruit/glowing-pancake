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

@Path("/recEntityRating")
public class PutAcceptRecEntityRating {
	
	//Tomcat non permette la put, ci sara' un modo per configurarlo attraverso web.xml
	//ma per il momento la camuffiamo in get
	@Context ServletContext servletContext;
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/putAcceptRecEntityRating")
	public String putAcceptRecEntityRating(  @QueryParam("userID") String userID,
							   	 			@QueryParam("entityURI") String entityURI,
							   	 			@QueryParam("rating") String rating) throws Exception 
	{
		Integer r = Integer.parseInt(rating);
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		entityURI = asController.getEntityUriFromEntities(entityURI); //Controllo l'esistenza del film tra tutti i film
		int numberRatedEntities;
		
		if (!entityURI.equalsIgnoreCase("null")) {
			System.out.println("/putAcceptRecEntityRating/Run...insertEntityRatedByUser userID: " + userID + " entityURI:" + entityURI);
			asController.insertAcceptRecEntityRatedByUser(userID, entityURI, r);
			//Per non far partire il page rank in caso di refine
			if (r.equals(1) || r.equals(2) ) {
				asController.putLastChange(userID, "entity_rating");
			}
			
		}
		else {
			System.err.println("/putAcceptRecEntityRating/Error - insertEntityRatedByUser userID: " + userID + " entityURI:" + entityURI);
		}		
		numberRatedEntities = asController.getNumberRatedEntities(userID);		
		Gson gson = new Gson();
		String json = gson.toJson(numberRatedEntities);
			
		System.out.print("/putAcceptRecEntityRating/");
		System.out.println(json);
		
		return json;		
	}	
}






