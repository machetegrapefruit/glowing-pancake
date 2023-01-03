package restService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import graph.AdaptiveSelectionController;

@Path("/users")
public class Users {

	@GET
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	@Path("/userDetail")
	public String getUserDetail (@QueryParam("userID") String userID) throws Exception 
	{

		Map<String,String> userDetailMap = new HashMap<String, String>();
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		userDetailMap = asController.getUserDetail(userID);
		
		Gson gson = new Gson();
		String json = gson.toJson("null");
		
		if (userDetailMap != null && !userDetailMap.isEmpty()) {			
	  		json = gson.toJson(userDetailMap);
		}
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		 		
  		System.out.print("/users/getUserDetail/userID=" + userID);
  		System.out.println(json);
	 
  		return json;
	}
	
	@PUT
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/userDetail")
	public String putUserDetail(@QueryParam("userID") String userID,
							   	 @QueryParam("firstname") String firstname,
							   	 @QueryParam("lastname") String lastname,
							   	 @QueryParam("username") String username) throws Exception 
	{
		
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		asController.putUserDetailByUser(userID, firstname, lastname, username);
		
		Gson gson = new Gson();
		String json = gson.toJson(userID);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		System.out.print("/user/putUserDetail/userID=");
		System.out.println(json);
		
		return json;
		
	}
	
	@PUT
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/userAge")
	public String putUserAge(	@QueryParam("userID") String userID,
								@QueryParam("age") String ageRange) throws Exception 
	{
		
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		if (ageRange != null && !ageRange.isEmpty()) {
			asController.putAgeRangeByUser(userID, ageRange);
		}
		else {
			System.err.println("Error - putUserAge userID: " + userID + " - ageRange:" + ageRange);
		}
			
		Gson gson = new Gson();
		String json = gson.toJson("null");
		json = gson.toJson(ageRange);			

		System.out.print("/putUserAge?userID=" + userID + "&ageRange=" + ageRange + "/");
		System.out.println(json);
		
		return json;		
	}	
	
	@PUT
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/userGender")
	public String putUserGender(	@QueryParam("userID") String userID,
									@QueryParam("gender") String gender) throws Exception 
	{
	
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		if (gender != null && !gender.isEmpty()) {
			asController.putGenderByUser(userID, gender);
		}
		else {
			System.err.println("Error - putUserGender userID: " + userID + " - gender:" + gender);
		}
			
		Gson gson = new Gson();
		String json = gson.toJson("null");
		json = gson.toJson(gender);			

		System.out.print("/putUserGender?userID=" + userID + "&gender=" + gender + "/");
		System.out.println(json);
		
		return json;		
	}	
	
	@PUT
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/userEducation")
	public String putUserEducation(@QueryParam("userID") String userID,
									@QueryParam("education") String education) throws Exception 
	{
	
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		if (education != null && !education.isEmpty()) {
			asController.putEducationByUser(userID, education);
		}
		else {
			System.err.println("Error - putUserEducation userID: " + userID + " - education:" + education);
		}
			
		Gson gson = new Gson();
		String json = gson.toJson("null");
		json = gson.toJson(education);			

		System.out.print("/putUserEducation?userID=" + userID + "&education=" + education + "/");
		System.out.println(json);
		
		return json;		
	}	
	
	@PUT
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/userInterestInEntities")
	public String putUserInterestInEntities(	@QueryParam("userID") String userID,
											@QueryParam("interestInEntities") String interestInEntities) throws Exception 
	{
	
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		if (interestInEntities != null && !interestInEntities.isEmpty()) {
			asController.putInterestInEntitiesByUser(userID, interestInEntities);
		}
		else {
			System.err.println("Error - putInterestInEntitiesByUser userID: " + userID + " - interestInEntities:" + interestInEntities);
		}
			
		Gson gson = new Gson();
		String json = gson.toJson("null");
		json = gson.toJson(interestInEntities);			

		System.out.print("/putInterestInEntitiesByUser?userID=" + userID + "&interestInEntities=" + interestInEntities + "/");
		System.out.println(json);
		
		return json;		
	}	
	
	@PUT
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/userUsedRecommenderSystems")
	public String putUserUsedRecommenderSystems(@QueryParam("userID") String userID,
												@QueryParam("usedRecSys") String usedRecSys) throws Exception 
	{
		
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		if (usedRecSys != null && !usedRecSys.isEmpty()) {
			asController.putUsedRecSysByUser(userID, usedRecSys);
		}
		else {
			System.err.println("Error - putUserUsedRecommenderSystems userID: " + userID + " - usedRecSys:" + usedRecSys);
		}
			
		Gson gson = new Gson();
		String json = gson.toJson("null");
		json = gson.toJson(usedRecSys);			

		System.out.print("/putUserUsedRecommenderSystems?userID=" + userID + "&usedRecSys=" + usedRecSys + "/");
		System.out.println(json);
		
		return json;		
	}	
}
