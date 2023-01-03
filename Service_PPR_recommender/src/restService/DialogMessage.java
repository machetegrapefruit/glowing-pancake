package restService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dialog.DialogSingleton;
import dialog.DialogState;
import functions.LogService;

@Path("/dialogMessage")
public class DialogMessage {
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//
//	public String receiveDialogMessage(DialogState state, LogService logService, String messageID, QueryResult input, String userID) {
//		System.out.println("Received message from Api.ai - Post");
//		System.out.println(input);
//		JsonObject responseObject = DialogSingleton.getDialogController().processMessage(state, logService, messageID, input, userID);
//		return responseObject.toString();
//	}
//	
//	@GET
//	@Produces(MediaType.TEXT_PLAIN)
//	public String receiveDialogMessageGet() {
//		System.out.println("Received message from Api.ai - Get");
//		return "";
//	}
	
}
