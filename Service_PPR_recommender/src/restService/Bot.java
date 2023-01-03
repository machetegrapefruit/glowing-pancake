package restService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import graph.AdaptiveSelectionController;

@Path("/bot")
public class Bot {

	@PUT
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/botName")
	public String putUserBotName(	@QueryParam("userID") String userID,
									@QueryParam("botName") String botName) throws Exception 
	{	
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		if (botName != null && !botName.isEmpty()) {
			asController.putBotNameByUser(userID, botName);
		}
		else {
			System.err.println("Error - putUserBotName userID: " + userID + " - botName:" + botName);
		}
			
		Gson gson = new Gson();
		String json = gson.toJson("null");
		json = gson.toJson(botName);			

		System.out.print("/putUserBotName?userID=" + userID + "&botName=" + botName + "/");
		System.out.println(json);
		
		return json;		
	}	
}
