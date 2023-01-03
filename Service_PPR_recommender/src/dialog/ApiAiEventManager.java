package dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.JsonObject;

public class ApiAiEventManager {
	/**
	 * Invia un evento ad api.ai con i parametri specificati
	 * @throws IOException 
	 */
	public void sendEvent(String eventName, JsonObject parameters) throws IOException {
		JsonObject json = new JsonObject();
		JsonObject eventObject = new JsonObject();
		eventObject.addProperty("name", eventName);
		eventObject.add("data", parameters);
		json.add("event", eventObject);    
		json.addProperty("sessionId", "c0f7f5c9-f5cb-4600-ada6-f355596aee51"); //TODO: Sostituire con la chat id
		System.out.println("Post body is " + json.toString());

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		try {
		    HttpPost request = new HttpPost("https://api.api.ai/v1/query?v=20150910");
		    StringEntity params = new StringEntity(json.toString());
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
			
			System.out.println("Result from api.ai is:" + result.toString());

		} catch (Exception ex) {
		    ex.printStackTrace();
		} finally {
		    httpClient.close();
		}
	}
}
