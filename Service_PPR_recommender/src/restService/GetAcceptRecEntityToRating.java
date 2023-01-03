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

@Path("/recEntityToRating")
public class GetAcceptRecEntityToRating {
	
	@Context ServletContext servletContext;
	@GET
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	@Path("/getAcceptRecEntityToRating")
	public String getAcceptRecEntityToRating (@QueryParam("userID") String userID) throws Exception 
	{
		String entityURI = "null";
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		entityURI = asController.getLastAcceptRecEntityToRating(userID);
		
		Gson gson = new Gson();
		String json = gson.toJson("null");
		if (!entityURI.equals("null")) {			
			json = gson.toJson(entityURI);
		}
		 		
  		System.out.print("/getEntityToRating/");
  		System.out.println(json);
	 
  		return json;
	}	

}
