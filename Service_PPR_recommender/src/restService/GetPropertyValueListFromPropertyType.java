package restService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import configuration.Configuration;
import functions.PropertyService;
import graph.AdaptiveSelectionController;
/**
 * Tra i film raccomandabili per l'utente (userID)
 * Seleziona i valori delle proprieta' di un certo tipo, es. regista
 * Oppure selezione tutti i film raccomandabili in base allo score
 */

@Path("/propertyValueList")
public class GetPropertyValueListFromPropertyType {

	@Context ServletContext servletContext;
	@GET
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	@Path("/getPropertyValueListFromPropertyType")
	public String getPropertyValueListFromPropertyType( @QueryParam("userID") String userID,
						   								@QueryParam("propertyType") String propertyType) throws Exception 
	{
		Map<String, List<String>> propertyMap = null;
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		String lastChange = asController.getLastChange(userID);
		
		//controllo per esegue il pagerank solo se c'è stato un cambiamento
		boolean check = true;
		if (lastChange.equals("pagerank") || lastChange.equals("refine")) {
			check = false;
		}
		
		String propertyTypeUri = Configuration.getDefaultConfiguration().getPropertyTypesLabels().get(propertyType);

		if ( (propertyType.equals("entity") && check == true) ) {
			asController.getUserRaccomandations(userID, null);
			propertyMap = asController.getItemListFromProperty(userID, propertyType, propertyTypeUri);
		}
		else {
			propertyMap = asController.getItemListFromProperty(userID, propertyType, propertyTypeUri);
		}	
		
		// Debug
		PropertyService service = new PropertyService();
		
		propertyMap.clear();
		Map<String, String> propertyTypesLabels = Configuration.getDefaultConfiguration().getPropertyTypesLabels();
		String[] propertyTypes = Configuration.getDefaultConfiguration().getPropertyTypes();
		for (String type : propertyTypes) {
			List<String> values = service.getPropertyValues(type);
			propertyMap.put(propertyTypesLabels.get(type), values);
		}
		
		Gson gson = new Gson();
		String json = gson.toJson("null");
		
		if (propertyMap != null && !propertyMap.isEmpty()) {			
			json = gson.toJson(propertyMap);
		}
		 
		System.out.print("/getPropertyValueListFromPropertyType?userID=" + userID + "&propertyType=" + propertyType + " - lastChange: " + lastChange + "/");
		if (json.equals("null")) {
			System.out.println(json);
		}
		else {
			System.out.println("ok");
		}
		
			 
		return json;
	}

}