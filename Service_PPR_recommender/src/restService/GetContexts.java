package restService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dialog.DialogState;

@Path("/getContext")
public class GetContexts {
	@GET
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	public String getContexts(@QueryParam("userID") String userID,
			DialogState state
			) {
		boolean responseDone = false;
		JsonObject response = new JsonObject();
//		DialogState state = new DialogStateService().getDialogState(Integer.parseInt(userID));
		if (state != null) {
			JsonArray contexts = state.getContexts();
			if (contexts != null) {
				responseDone = true;
				response.add("contexts", contexts);
			}
		}

		response.addProperty("responseDone", responseDone);
		return response.toString();
	}
}
