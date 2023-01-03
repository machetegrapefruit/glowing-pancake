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

import graph.AdaptiveSelectionController;

@Path("/propertyTypeList")
public class GetPropertyTypeListFromPropertyValue {
	
	@Context ServletContext servletContext;
	@GET
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	@Path("/getPropertyTypeListFromPropertyValue")
	public String getAllPropertyListFromEntity (@QueryParam("name") String propertyValueName) throws Exception 
	{
		String propertyValueUri = "http://dbpedia.org/resource/" + propertyValueName;
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		Map<String, List<String>> propertyValueToPropertyTypeListMap = new HashMap<String, List<String>>();
		propertyValueToPropertyTypeListMap = asController.getPropertyTypeFromPropertyValue(propertyValueUri);
		
		//Tratta i valori di proprietà che non hanno l'uri
		if (propertyValueToPropertyTypeListMap.isEmpty()) {			
			propertyValueToPropertyTypeListMap = asController.getPropertyTypeFromPropertyValue(propertyValueName);
		}		
		
		Gson gson = new Gson();
		String json = gson.toJson("null");
		
		if (propertyValueToPropertyTypeListMap != null && !propertyValueToPropertyTypeListMap.isEmpty()) {			
	  		json = gson.toJson(propertyValueToPropertyTypeListMap);
		}
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
  		System.out.print("/propertyTypeList/getPropertyTypeListFromPropertyValue/" + propertyValueName + "/");
		if (json.equals("null")) {
			System.out.println(json);
		}
		else {
			System.out.println("ok");
		}
  		
  		return json;
	}

}
