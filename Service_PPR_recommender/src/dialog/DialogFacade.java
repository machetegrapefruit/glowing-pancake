package dialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.WordUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import configuration.Configuration;
import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import functions.LogService;
import keyboards.CustomKeyboard;
import keyboards.Keyboard;
import replies.CustomReply;
import replies.Reply;
import restService.DialogMessage;
import restService.GetContexts;
import restService.GetFirstRecommendation;
import utils.Alias;
import utils.FormatUtils;
import utils.PropertyFilter;

public class DialogFacade {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public static Reply getReply(String userID, String messageID, String message, DialogState state, LogService logService) {
		Reply reply = null;
		
		JsonObject contexts = getContexts(userID, state);
		System.out.println("contexts: " + contexts);
		
		QueryResult data = null;
		try {
			data = sendIntentRequest(userID, contexts, message);
			System.out.println("data: " + data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Reply webServiceResponse = null;
		try {
			webServiceResponse = sendRequestToWebService(state, logService, messageID, data, userID);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//reply = handleResponse(state, webServiceResponse);
		
		return webServiceResponse;
	}
	
	private static boolean isDisambiguation(DialogState state) {
		
		boolean isDisambiguation = false;
		
		Deque<Preference> preferences = state.getPendingPreferenceQueue();
		Preference preference = null;
		if (!preferences.isEmpty()) {
			preference = preferences.peek();
			if (!preference.allDisambiguated()) {
				isDisambiguation = true;
			}
		}
		return isDisambiguation;
	}
	
	private static String[] getOptions(Preference preference) {
		
		String[] options = null;

		PendingEvaluation pe = preference.getNextPendingEvaluation();
		List<Alias> possibleValues = pe.getPossibleValues();
		
		options = new String[possibleValues.size()];
		for (int i = 0; i < options.length; i++) {
			String label = possibleValues.get(i).getLabel();
			options[i] = WordUtils.capitalize(label);
		}
		
		return options;
	}
	
	private static JsonObject getContexts(String userID, DialogState state) {
		GetContexts service = new GetContexts();
		String response = service.getContexts(userID, state);
		JsonParser parser = new JsonParser();
		JsonObject json = null;
		json = (JsonObject) parser.parse(response);

		return json;
	}
	
	@SuppressWarnings("unchecked")
	private static QueryResult sendIntentRequest(String userID, JsonObject contexts, String text) throws Exception {
		DialogflowConnector dc = new DialogflowConnector();
		JsonArray contextObj = new JsonArray();
		if (contexts.get("responseDone").getAsBoolean() == true) {
			contextObj = contexts.get("contexts").getAsJsonArray();
		}
		return dc.getResponse(userID, text, contextObj);


		/*JsonObject obj = new JsonObject();
		obj.add("contexts", contextObj);
		obj.addProperty("query", text);
		obj.addProperty("sessionId", userID);
		obj.addProperty("lang", "en");
		
		String url = "https://api.dialogflow.com/v1/query?v=20150910";
		URL urlObj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

		// Add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Authorization", "Bearer " + getAccessToken());
		con.setRequestProperty("Content-Type", "application/json; charset=utf-8");

		// Send post request
		con.setDoOutput(true);
		BufferedWriter wr = new BufferedWriter( new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
		wr.write(obj.toString());
		wr.flush();
		wr.close();
		con.connect();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + obj);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream(), "UTF-8"));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(response.toString());
		
		return json;*/
	}
	
	private static Reply sendRequestToWebService(DialogState state, LogService logService, String messageID, QueryResult data, String userID) throws ParseException {
		//DialogMessage dialog = new DialogMessage();
		//String response = dialog.receiveDialogMessage(state, logService, messageID, data, userID);
		//System.out.println("Web Service Response: " + response);
		return DialogSingleton.getDialogController().processMessage(state, logService, messageID, data, userID).getReply();
		
//		JsonParser parser = new JsonParser();
//		JsonObject json = (JsonObject) parser.parse(response);
//		
//		return json;
	}
	
//	private static Reply handleResponse(DialogState state, JsonObject webServiceResponse) {
//		
//		List<Message> messagesList = new ArrayList<Message>();
//		
//		String displayText = webServiceResponse.get("displayText").getAsString();
//		System.out.println("Display text tutto attaccato: " + displayText);
//	
//		Message[] messages = null;
//		Keyboard keyboard = null;
//		
//		String interactionType = Configuration.getDefaultConfiguration().getInteractionType();
//
//		// se è una disambiguazione
//		if (isDisambiguation(state) &&
//				interactionType.equals("mixed")) {
//			
//			System.out.println("Restituisco elementi disambiguazione come pulsanti");
//			
//			Deque<Preference> preferences = state.getPendingPreferenceQueue();
//			Preference preference = preferences.peek();
//			
//			String[] options = getOptions(preference);
//			
//			//ResponseService rs = new ResponseService();
//			//String message = rs.getPendingEvaluationMessage(preference.getNextPendingEvaluation());
//			String[] texts = displayText.split("\n\n");
//			for (int i = 0; i < texts.length; i++) {
//				messagesList.add(new Message(texts[i]));
//			}
//			
//			keyboard = new CustomKeyboard(options);
//			
//		} else {
//			System.out.println("Non rilevata disambiguazione, o interazione NL");
//			
//			String[] texts = displayText.split("\n\n");
//			for (int i = 0; i < texts.length; i++) {
//				messagesList.add(new Message(texts[i]));
//			}
//		}
//		
//		JsonObject data = (JsonObject) webServiceResponse.get("data");
//		System.out.println("datadebug: " + data);
//		
//		if (data.has("image")) {
//			String imageUrl = data.get("image").getAsString();
//			imageUrl = FormatUtils.correctPhotoRes(imageUrl);
//			messagesList.add((new Message(data.get("imageCaption").getAsString(), imageUrl)));
//			messagesList.add(new Message(data.get("postImageSpeech").getAsString()));
//		}
//		if (data.has("link")) {
//			String link = data.get("link").getAsString();
//			String linkLabel = data.get("linkLabel").getAsString();
//			messagesList.add(new Message(linkLabel, null, link));
//			if (data.has("postLinkText")) {
//				String postLinkText = data.get("postLinkText").getAsString();
//				messagesList.add(new Message(postLinkText, null, null));
//			}
//		}
//		
//		Reply reply = null;
//		
//		if (data.has("auxAPI")) {
//			JsonObject auxData = data.getAsJsonObject("auxAPI");
//			String apiURL = auxData.get("apiURL").getAsString() + "?userID=" + state.getClientID();
//			JsonObject parameters = auxData.getAsJsonObject("parameters");
//			Message[] m = new Message[] {
//					new Message(displayText)
//			};
//			AuxAPI auxAPI = new AuxAPI(apiURL, null, parameters);
//			reply = new CustomReply(m, null, auxAPI);
//			
//		} else {
//		
//			List<Message> temp = new ArrayList<Message>();
//			for (Message m : messagesList) {
//				m.setText(m.getText().trim());
//				if (!m.getText().isEmpty()) {
//					temp.add(m);
//				}
//			}
//			messages = new Message[temp.size()];
//			System.out.println("Messages:");
//			for (int i = 0; i < messages.length; i++) {
//				messages[i] = temp.get(i);
//				System.out.println(temp.get(i).getText());
//			}
//			
//			ReplyMarkup replyMarkup = null;
//			if (keyboard != null) {
//			    replyMarkup = new ReplyMarkup(keyboard, true, true);
//			}
//			reply = new CustomReply(messages, replyMarkup);
//		}
//		
//		return reply;
//	}
	
	private static JSONObject sendAuxPostRequest(JSONObject data) {

		System.out.println("Il servizio ausiliario ha ricevuto: " + data.toJSONString());
		List<PropertyFilter> filters = (List<PropertyFilter>) ((JSONObject) data.get("parameters")).get("filters");
		Long userID = (Long) ((JSONObject) data.get("parameters")).get("userID");
		JSONObject parameters = (JSONObject) data.get("parameters");
		GetFirstRecommendation service = new GetFirstRecommendation();
		System.out.println("Richiesta a getRecommendations; userID: " + userID + ", filters: " + filters);
		String response = service.getFirstRecommendationPost(parameters.toJSONString());
		JSONParser parser = new JSONParser();
		JSONObject ret = null;
		try {
			ret = (JSONObject) parser.parse(response);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private static String getAccessToken() {
		return Configuration.getDefaultConfiguration().getDialogFlowToken();
	}
	
	public static void main(String[] args) throws Exception {
		JsonObject contexts = new JsonObject();
		contexts.add("contexts", new JsonArray());
		contexts.addProperty("responseDone", true);
		System.out.println(sendIntentRequest("123456", contexts, "I like it, but I don’t like the genre"));
	}
}
