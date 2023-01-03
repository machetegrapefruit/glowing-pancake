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

@Path("/levDistance")
public class GetLevDistanceFromAllVertexUriByName {
	
	@Context ServletContext servletContext;
	@GET
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	@Path("/getLevDistanceFromAllVertexUriByName")
	public String getAllPropertyListFromEntity (@QueryParam("name") String name) throws Exception 
	{
		
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		Map<String, Double> levdistanceMap = new HashMap<String, Double>();
		levdistanceMap = asController.getLevDistanceFromAllVertexUriByName(name);
		
		Gson gson = new Gson();
		String json = gson.toJson("null");
		
		if (levdistanceMap != null && !levdistanceMap.isEmpty()) {			
	  		json = gson.toJson(levdistanceMap);
		}
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
  		System.out.print("/levDistance/getLevDistanceFromAllVertexUriByName/" + name + "/");
  		System.out.println(json);
  		
  		return json;
	}

}
