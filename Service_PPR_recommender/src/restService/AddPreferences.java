package restService;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import miniconverse.MiniAddPreference;
import miniconverse.MiniPreference;

@Path("/addPreferences")
public class AddPreferences {
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	public String addPreference(String input) {
		JsonObject response = new JsonObject();
		JsonObject inputJson = new JsonParser().parse(input).getAsJsonObject();
		String userID = inputJson.get("userID").getAsString();
		JsonArray preferences = inputJson.get("preferences").getAsJsonArray();
		List<MiniPreference> mps = new ArrayList<MiniPreference>();
		for (JsonElement p: preferences) {
			JsonObject po = p.getAsJsonObject();
			mps.add(new MiniPreference(po.get("uri").getAsString(), po.get("rating").getAsInt()));
		}
		
		boolean success = new MiniAddPreference().addPreferences(userID, mps);
		response.addProperty("success", success);
		 
		return response.toString();
	}

}
