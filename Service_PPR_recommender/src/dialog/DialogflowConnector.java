package dialog;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.Context;
import com.google.cloud.dialogflow.v2.ContextName;
import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryParameters;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.cloud.dialogflow.v2.TextInput.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import configuration.Configuration;

/**
 * This class handles the communication with the DialogFlow agent
 * @author Andrea Iovine
 *
 */
public class DialogflowConnector {

	/**
	 * Forwards the message to the DialogFlow agent
	 * @param userID The user that sent the message
	 * @param text Contents of the text message
	 * @return A QueryResult object that contains all data returned by DialogFlow, such as the recognized intent.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public QueryResult getResponse(String userID, String text, JsonArray contexts) throws FileNotFoundException, IOException {
		Configuration conf = Configuration.getDefaultConfiguration();
		String credentialsPath = conf.getDialogflowV2CredentialsPath();
		String agentName = conf.getDialogflowV2AgentName();
		
		GoogleCredentials cred = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
		SessionsSettings settings = SessionsSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(cred)).build();
		try (SessionsClient sessionsClient = SessionsClient.create(settings)) {
			//String agentName = Configuration.getConfiguration().get("dialogFlowInfo").getAsJsonObject().get("agentName").getAsString();
			SessionName session = SessionName.of(agentName, userID);
			System.out.println("Session Path: " + session.toString());

			Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode("en");
			QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
			QueryParameters.Builder parameters = QueryParameters.newBuilder();
			for (JsonElement context: contexts) {
				JsonObject contextObj = context.getAsJsonObject();
				ContextName contextName = ContextName.newBuilder()
				          .setProject(agentName)
				          .setSession(userID)
				          .setContext(contextObj.get("name").getAsString())
				          .build();
				Context c = Context.newBuilder()
						.setName(contextName.toString())
						.setLifespanCount(contextObj.get("lifespan").getAsInt())
						.build();
				parameters.addContexts(c);
			}
			QueryParameters param = parameters.build();
			DetectIntentRequest request = DetectIntentRequest.newBuilder()
					.setSession(session.toString())
					.setQueryInput(queryInput).setQueryParams(param)
					.build();
		    DetectIntentResponse response = sessionsClient.detectIntent(request);
		    QueryResult queryResult = response.getQueryResult();

	      	return queryResult;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		DialogflowConnector dc = new DialogflowConnector();
		try {
			QueryResult res = dc.getResponse("1234", "I like The Matrix", new JsonArray());
			System.out.println(res.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
