package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DialogFlowConnector {
	public static JsonObject processMessage(String message, String userID) throws IOException {
		JsonObject reqObject = new JsonObject();
		JsonArray contextArray = new JsonArray();
		JsonObject prefFollowup = new JsonObject();
		prefFollowup.addProperty("name", "preference-followup");
		prefFollowup.addProperty("lifespan", 0);
		JsonObject reqFollowup = new JsonObject();
		reqFollowup.addProperty("name", "request_recommendation-followup");
		reqFollowup.addProperty("lifespan", 0);
		contextArray.add(prefFollowup);
		contextArray.add(reqFollowup);
		reqObject.add("contexts", contextArray);
		reqObject.addProperty("lang", "en");
		reqObject.addProperty("query", message);
		reqObject.addProperty("sessionId", userID);
		
		return doRequest("https://api.dialogflow.com/v1/query?v=20150910", reqObject);
	}
	
	private static JsonObject doRequest(String url, JsonObject postParams) throws IOException {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost(url);
	    StringEntity params = new StringEntity(postParams.toString());
	    request.addHeader("content-type", "application/json");
	    request.addHeader("Authorization", "Bearer 4ef6680948dc45ed8097591046388029");
	    request.setEntity(params);
	    HttpResponse response = httpClient.execute(request);
	    
	    BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		return new JsonParser().parse(result.toString()).getAsJsonObject();
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(processMessage("I like The Matrix", "1234567890"));
		System.out.println(processMessage("Can you recommend me a movie", "1234567890"));
	}
}
