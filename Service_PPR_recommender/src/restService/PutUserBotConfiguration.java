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

@Path("/botConfiguration")
public class PutUserBotConfiguration {
	
	@Context ServletContext servletContext;
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/putUserBotConfiguration")
	public String putUserBotConfiguration(	@QueryParam("userID") String userID,
											@QueryParam("botName") String botName,
											@QueryParam("botTimestamp")String botTimestap) throws Exception 
	{
		int bot_timestamp = Integer.parseInt(botTimestap);
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		if (botName != null && !botName.isEmpty()) {
			asController.putBotConfigurationByUser(userID, botName, bot_timestamp);
		}
		else {
			System.err.println("Error - putUserBotConfiguration userID: " + userID + " - botName:" + botName);
		}
			
		Gson gson = new Gson();
		String json = gson.toJson("null");
		json = gson.toJson(botName);			

		System.out.print("/putUserBotConfiguration?userID=" + userID + "&botName=" + botName + "/");
		System.out.println(json);
		
		return json;		
	}	
}