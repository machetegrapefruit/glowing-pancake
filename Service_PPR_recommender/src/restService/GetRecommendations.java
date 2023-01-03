package restService;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import configuration.Configuration;
import entity.Entity;
import functions.EntityService;
import miniconverse.RecEntity;
import recommendationService.Recommendation;

@Path("/getRecommendations")
public class GetRecommendations {
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	public String getRecommendations(@QueryParam("userID") String userID) {
		List<String> recommendations = new Recommendation().getRecommendationList(userID, null, true);
		EntityService es = new EntityService();
		List<RecEntity> result = new ArrayList<RecEntity>();
		Configuration conf = Configuration.getDefaultConfiguration();
		for(String uri: recommendations) {
			Entity e = es.getEntityDetails(uri);
			String label = es.getEntityLabel(uri);
			List<String> images = e.get(conf.getPropertyTypeImage());
			List<String> trailers = e.get(conf.getPropertyTypeTrailer());
			String image = null;
			String trailer = null;
			
			if (images != null && images.size() > 0) {
				image = images.get(0);
			}
			if (trailers != null && trailers.size() > 0) {
				trailer = trailers.get(0);
			}
			
			result.add(new RecEntity(uri, label, image, trailer));
		}
		Gson gson = new Gson();
		return gson.toJson(result);
	}
}
