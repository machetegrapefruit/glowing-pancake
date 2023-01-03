package explanationService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

@Path("/explanation")
public class Explanation {
	
	/**
	 * Produces an explanation for the specified entity and for the specified user
	 * @param userID The user ID.
	 * @param entityURI The URI of the entity
	 * @return A JSON desribing why the system would recommend entityName to userID.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getExplanation(
			@QueryParam("userID") String userID,
			@QueryParam("entityURI") String entityURI) throws Exception {
		
		String explanationString = ExplanationUtilities.getExplanation(userID, entityURI);

		Gson gson = new Gson();
		String json = gson.toJson("Sorry, this recommendation is serendipitous also for me 🙂.\nI’m not able to provide an explanation 🤔");
		
		if (explanationString != null && !explanationString.isEmpty()) {			
	  		//json = gson.toJson(explanationString + ".");
			json = explanationString;
		}
		
  		System.out.println("/getEntityExplanation/" + json);
  		
  		return json;
	}
}
