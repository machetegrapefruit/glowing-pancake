package restService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import dialog.ApiAiResponse;
import dialog.DialogSingleton;
import dialog.DialogState;
import functions.DialogStateService;
import functions.LogService;
import functions.ProfileService;
import functions.ResponseService;
import functions.ServiceSingleton;
import recommendationService.Recommendation;
import replies.JsonReply;
import replies.Reply;
import utils.PropertyFilter;

/**
 * Questa API viene chiamata direttamente dalla chatbot attraverso il meccanismo delle API ausiliarie.
 * Esegue l'algoritmo di pagerank e restituisce un messaggio testuale contenente il primo dei film consigliati
 *
 */

@Path("/getFirstRecommendation")
public class GetFirstRecommendation {
	/*
	@Context ServletContext servletContext;
	@GET
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	public String getFirstRecommendation(@QueryParam("userID") String userID
			) {
		//TODO: impongo il genere a comedy solo per testare
		List<PropertyFilter> propertyFilters = new ArrayList<PropertyFilter>();
		propertyFilters.add(new PropertyFilter("genre", "http://dbpedia.org/resource/Comedy"));
		return getRecommendations(Integer.parseInt(userID), propertyFilters).toString();
	}*/
	
	/*
	@GET
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	public String getFirstRecommendation(@QueryParam("userID") String userID,
			@QueryParam("filterPropertyType") String filterPropertyType,
			@QueryParam("filterPropertyValue") String filterPropertyValue
			) {
		JsonObject res = null;
		JsonParser parser = new JsonParser();

		if (filterPropertyType != null && filterPropertyValue != null) {
			List<PropertyFilter> propertyFilters = new ArrayList<PropertyFilter>();
			propertyFilters.add(new PropertyFilter(filterPropertyType, filterPropertyValue));			
			res = parser.parse(getRecommendations(Integer.parseInt(userID), propertyFilters).toString()).getAsJsonObject();
		} else {
			res = parser.parse(getRecommendations(Integer.parseInt(userID), null).toString()).getAsJsonObject();
		}
		
		JsonReply reply = new JsonReply(res);
		return reply.toJson();
	}*/
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	public String getFirstRecommendationPost(String input) {
		System.out.println("input is " + input);
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(PropertyFilter.class, new PropertyFilter.PropertyFilterDeserializer());
		Gson gson = builder.create();
		JsonParser parser = new JsonParser();
		JsonObject parameters = parser.parse(input).getAsJsonObject();
		System.out.println("Parameters: " + parameters);
		String userID = parameters.get("userID").getAsString();
		String messageID = parameters.get("messageID").getAsString();
		Type listType = new TypeToken<ArrayList<PropertyFilter>>(){}.getType();
		List<PropertyFilter> filters = gson.fromJson(parameters.get("filters"), listType);
		
		Reply recommendations = null;
		if (filters == null || filters.size() == 0) {
			recommendations = getRecommendations(userID, messageID, null);
		} else {
			recommendations = getRecommendations(userID, messageID, filters);
		}

		//Aggiorno timestamp_end, pagerank_cicle e number_recommendation_list del messaggio di richiesta raccomandazione a questo istante
		ProfileService ps = new ProfileService();
		int newPRCycle = ps.getPagerankCycle(userID);
		int newNumRecList = ps.getNumberRecommendationList(userID);
		List<String> newEvents = new  ArrayList<String>();
		if (new DialogStateService().getDialogState(userID).getCurrentRecommendedIndex() != -1) {
			newEvents.add("question");
		}
		new LogService().updateLogMessage(userID + "", messageID, System.currentTimeMillis(), newPRCycle, newNumRecList, newEvents);	
		
		return new JsonReply(recommendations.getMessages(), null, null).toJson();
	}
	
	private Reply getRecommendations(String userID, String messageID, List<PropertyFilter> propertyFilters) {
		ApiAiResponse responseObject;
		List<String> recommendations = new Recommendation().getRecommendationList(userID, propertyFilters, true);
		//new EntityService().insertEntityToRate(userID, recommendations.get(0), 0);
		DialogStateService dss = new DialogStateService();
		DialogState state = dss.getDialogState(userID);
		if (recommendations.size() > 0) {
			//state.setCurrentRecommendedIndex(0);
			responseObject = DialogSingleton.getDialogController().getDialog(userID + "").getNextPendingTask(state);
		} else {
			responseObject = new ApiAiResponse();
			responseObject.addSpeech(ServiceSingleton.getResponseService().getDefaultFailureMessage());
		}
		dss.saveDialogState(userID, state);

		return responseObject.getReply();
	}
	
	private String findProperty(List<List<String>> properties, String propertyToFind) {
		for (List<String> l : properties) {
			if (l.get(1).equalsIgnoreCase(propertyToFind)) {
				return l.get(2);
			}
		}
		return null;
	}
}
