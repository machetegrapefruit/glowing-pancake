package restService;

import java.net.URLDecoder;
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

@Path("/filters")
public class Filters {

	@PUT
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/runtimeRangeFilter")
	public String putRuntimeRangeFilter(	@QueryParam("userID") String userID,
												@QueryParam("propertyType") String propertyType,
							   				@QueryParam("propertyValue") String propertyValue) throws Exception 
	{
		String runtimeRangeFilter = null;
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		if (propertyType.equals("runtimeRange")) {
			runtimeRangeFilter = asController.changeRuntimeRangeStringToRuntimeRangeValue(propertyValue);				
		}			
		
		if (!runtimeRangeFilter.equalsIgnoreCase(null)) {
			//System.out.println("Run...insertPropertyRatedByUser userID: " + userID + " propertyURI:" + propertyURI);
			asController.putRuntimeRangeFilterByUser(userID, propertyType, runtimeRangeFilter);
			asController.putLastChange(userID, "property_rating");
		}
		else {
			System.err.println("Error - putRuntimeRangeFilter userID: " + userID + " - propertyValue:" + propertyValue + " - runtimeRangeFilter:" + runtimeRangeFilter);
		}
			
		Gson gson = new Gson();
		String json = gson.toJson("null");
		json = gson.toJson(runtimeRangeFilter);			

		System.out.print("/putRuntimeRangeFilter?userID=" + userID + "&propertyType=" + propertyType + "&propertyValue=" + propertyValue + "/");

		System.out.println(json);
		
		return json;		
	}	
	
	@PUT
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/putReleaseYearFilter")
	public String putReleaseYearFilter(@QueryParam("userID") String userID,
									@QueryParam("propertyType") String propertyType,
							   		@QueryParam("propertyValue") String propertyValue) throws Exception 
	{
		String releaseYearFilter = null;
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		if (propertyType.equals("releaseYear")) {
			releaseYearFilter = asController.changeReleaseYearStringToReleaseYearValue(URLDecoder.decode(propertyValue, "UTF-8"));				
		}			
		
		if (releaseYearFilter != null && !releaseYearFilter.isEmpty()) {
			asController.putReleaseYearFilterByUser(userID, propertyType, releaseYearFilter);
			asController.putLastChange(userID, "property_rating");
		}
		else {
			System.err.println("Error - putReleaseYearFilter userID: " + userID + " - propertyValue:" + propertyValue + " - releaseYearFilter:" + releaseYearFilter);
		}
			
		Gson gson = new Gson();
		String json = gson.toJson("null");
		json = gson.toJson(releaseYearFilter);			

		System.out.print("/putReleaseYearFilter?userID=" + userID + "&propertyType=" + propertyType + "&propertyValue=" + propertyValue + "/");

		System.out.println(json);
		
		return json;		
	}	
}
