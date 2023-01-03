package restService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
/**
 * Tra i film raccomandabili per l'utente (userID)
 * Seleziona i valori delle proprieta' di un certo tipo, es. regista
 * Oppure selezione tutti i film raccomandabili in base allo score
 */

import graph.AdaptiveSelectionController;

/**
 * Tra i film raccomandabili per l'utente (userID)
 * Seleziona i valori delle proprieta' di un certo tipo, es. regista
 * Oppure selezione tutti i film raccomandabili in base allo score
 */
@Path("/recEntityTopropertyValueAndScoreList")
public class GetPropertyValueFromPropertyTypeAndEntity {

	@Deprecated
	@Context ServletContext servletContext;
	@GET
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	@Path("/getPropertyValueAndScoreListByRecEntityFromUserAndPropertyType")
	public String getPropertyValueAndScoreListByRecEntityFromUserAndPropertyType(@QueryParam("userID") String userID,
						   							@QueryParam("propertyType") String propertyType) throws Exception 
	{
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		Map<String, List<String>> propertyMap = new HashMap<String, List<String>>();

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		String lastChange = asController.getLastChange(userID);
		
		if (propertyType.equals("entity") &&  (!lastChange.equals("pagerank")|| !lastChange.equals("refine"))) {	
			asController.getUserRaccomandations(userID, null);
			propertyMap = asController.getPropertyValueAndScoreListByRecEntityFromUserAndPropertyType(userID, propertyType);
      		
		} else {
			propertyMap = asController.getPropertyValueAndScoreListByRecEntityFromUserAndPropertyType(userID, propertyType);
		}	
		
		Gson gson = new Gson();
		String json = gson.toJson("null");
		
		if (propertyMap != null && !propertyMap.isEmpty()) {			
			json = gson.toJson(propertyMap);
		}
		 
		System.out.print("/getPropertyValueFromPropertyTypeAndEntity?userID=" + userID + "&propertyType=" + propertyType +"/");
		if (json.equals("null")) {
			System.out.println(json);
		}
		else {
			System.out.println("ok");
		}
			 
		return json;
	}

}