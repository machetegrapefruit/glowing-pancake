package recommendationService;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import utils.PropertyFilter;

@Path("/recommendation")
public class Recommendation {

	/**
	 * REST endpoint.
	 * Starts the recommendation algorithm and produces a recommended item
	 * for the specified user.
	 * @param userID The ID of the user
	 * @return The URI of the recommended entity
	 */
	@Path("single")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getRecommendation(
			@QueryParam("userID") String userID) {
		
		List<String> list = RecommendationUtilities.getRecommendations(userID, null, false);
		String entity = list.get(0);
		
		Gson gson = new Gson();
		String json = gson.toJson(entity, String.class);
		
		return json;
	}
	
	/**
	 * REST endpoint
	 * Starts the recommendation algorithm and produces a list of recommended items.
	 * @param userID the ID of the user
	 * @return A list of recommended item, each item is represented by its URI
	 */
	@Path("list")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getRecommendationList(
			@QueryParam("userID") String userID) {
		
		List<String> list = RecommendationUtilities.getRecommendations(userID, null, false);
		
		Gson gson = new Gson();
		String json = gson.toJson(list, List.class);
		
		return json;
	}
	
	public List<String> getRecommendationList(
			String userID,
			List<PropertyFilter> filters,
			boolean forcePagerank) {
		
		List<String> list = RecommendationUtilities.getRecommendations(userID, filters, forcePagerank);
		
		return list;
	}
}
