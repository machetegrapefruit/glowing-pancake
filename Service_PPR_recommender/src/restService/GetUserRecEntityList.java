package restService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import graph.AdaptiveSelectionController;
/**
 * Selezione i 5 film raccomandabili in base allo score
 */

@Path("/recEntityList")
public class GetUserRecEntityList {

	@Context ServletContext servletContext;
	@GET
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	@Path("/getUserRecEntityList")
	public String getUserRecEntityList( @QueryParam("userID") String userID) throws Exception 
	{
		Map<Double, String> recEntityListMap = new HashMap<Double, String>();
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		recEntityListMap = asController.getRecEntityListByUser(userID);
		
		Gson gson = new Gson();
		String json = gson.toJson("null");
		
		if (recEntityListMap != null && !recEntityListMap.isEmpty()) {			
			json = gson.toJson(recEntityListMap);
		}
		 
		System.out.print("/getUserRecEntityList?userID=" + userID + "/");
		System.out.println(json);
			 
		return json;
	}

}