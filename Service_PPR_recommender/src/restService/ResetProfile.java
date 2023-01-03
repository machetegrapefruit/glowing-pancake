package restService;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import configuration.Configuration;
import entity.Entity;
import functions.EntityService;
import functions.ProfileService;
import miniconverse.RecEntity;
import recommendationService.Recommendation;

@Path("/resetProfile")
public class ResetProfile {
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	public String resetProfile(@QueryParam("userID") String userID) {
		ProfileService ps = new ProfileService();
		boolean success = ps.deleteUserProfile(userID);
		JsonObject result = new JsonObject();
		result.addProperty("success", success);
		return result.toString();
	}
}
