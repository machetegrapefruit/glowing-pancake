package restService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import graph.AdaptiveSelectionController;

@Path("/ratingsExperimentalSession")
public class PutExperimentalSessionRating {
	
	//Tomcat non permette la put, ci sara' un modo per configurarlo attraverso web.xml
	//ma per il momento la camuffiamo in get
	@Context ServletContext servletContext;
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/putExperimentalSessionRating")
	public String putExperimentalSessionRating(@QueryParam("userID") String userID,
								   	 @QueryParam("numberRecommendationList") String numberRecommendationList,
								   	 @QueryParam("rating") String rating
								   	 ) throws Exception 
	{
		int number_recommendation_list = Integer.parseInt(numberRecommendationList);
		int r = Integer.parseInt(rating);
		
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		asController.putExperimentalSessionRatingByUser(userID, number_recommendation_list, r);
	
		Gson gson = new Gson();
		String json = gson.toJson(r);
			
		System.out.print("/putExperimentalSessionRating/userID: " + userID + " numberRecommendationList:" + numberRecommendationList + " rating:" + r + "/");
		System.out.println(json);
		
		return json;		
	}	
}
