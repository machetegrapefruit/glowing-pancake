package test.convrecsys;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import functions.EntityService;
import functions.ProfileService;
import functions.PropertyService;
import test.DialogFlowConnector;
import test.TestPagerank;

public class CRSIntentTest {
	private static PropertyService pr = new PropertyService();
	private static ProfileService ps = new ProfileService();
	private static EntityService es = new EntityService();
	private static String userID = "1234567890";
	private static Gson gson;
	private static String upperboundPath;
	public static void main(String[] args) throws IOException {
		String datasetPath = args[0];
		upperboundPath = args[1];
		String outputPath = args[2];
		
		FileReader reader = new FileReader(datasetPath);
		JsonReader jReader = new JsonReader(reader);
		jReader.beginArray();
		
		FileWriter writer = new FileWriter(outputPath);
		JsonWriter jWriter = new JsonWriter(writer);
		jWriter.setIndent("  ");
		jWriter.beginArray();
		
		gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		while (jReader.hasNext()) {
			CRSConversation conversation = gson.fromJson(jReader, CRSConversation.class);
			int i = 0;
			List<CRSRecommendationTurn> recTurns = new ArrayList<>();
			for (CRSTurn turn: conversation.getTurns()) {
				CRSRecommendationTurn crt = analyzeTurn(conversation.getId(), i, turn);
				recTurns.add(crt);
				i++;
			}
			CRSRecommendationConversation resultConversation = new CRSRecommendationConversation(
					conversation.getId(),
					recTurns.toArray(new CRSRecommendationTurn[recTurns.size()])
				);
			gson.toJson(resultConversation, CRSRecommendationConversation.class, jWriter);
		}
		jWriter.endArray();
		jWriter.close();
	}
	
	private static CRSRecommendationTurn analyzeTurn(String convId, int turnIndex, CRSTurn turn) {
		try {
			List<CRSRecommendation> results = new ArrayList<>();
			System.out.print("Removing user preferences...");
			boolean deleted = es.deleteAllRatedEntities(userID);
			ps.deleteUserProfile(userID);
			pr.deleteAllRatedProperties(userID);
			if (deleted) {
				System.out.println("done");
			} else {
				System.out.println("Could not delete user preferences!");
			}
			
			List<CRSMessage> userPreferences = new ArrayList<>();
			CRSMessage recommendation = null;
			double feedback = -1;
			int recIndex = 0;
			for (CRSMessage message: turn.getMessages()) {
				//Per ogni messaggio dell'utente
				//Valuto se è una preferenza
				if (message.getAgent().equals("user") && message.getEntities().length > 0) {
					userPreferences.add(message);
				}
				//Se è una raccomandazione
				if (message.getAgent().equals("bot") && message.getEntities().length > 0) {
					recommendation = message;
				}
				//Se è un messaggio con feedback non nullo
				if (message.getAgent().equals("user") && message.getFeedback() > -1) {
					feedback = message.getFeedback();
					String[] preferenceStrings = new String[userPreferences.size()];
					List<CRSEntity> entitiesInDataset = new ArrayList<>();
					for (int i = 0; i < userPreferences.size(); i++) {
						preferenceStrings[i] = userPreferences.get(i).getUtterance();
						entitiesInDataset.addAll(getEntitiesInDataset(userPreferences.get(i).getEntities()));
					}
					List<CRSEntity> recommendationsInDataset = getEntitiesInDataset(recommendation.getEntities());
					System.out.println("Preferences: " + Arrays.toString(preferenceStrings) + ", recommendation is " + recommendation.getUtterance());
					results.add(addPreferencesAndGetRecommendations(
							convId,
							turnIndex,
							recIndex,
							entitiesInDataset, 
							recommendation.getEntities(), 
							userPreferences.toArray(new CRSMessage[userPreferences.size()]), 
							preferenceStrings, 
							recommendation, 
							recommendationsInDataset, 
							feedback
							));
					recIndex++;
					userPreferences.clear();
				}
			}
			deleted = es.deleteAllRatedEntities(userID);
			ps.deleteUserProfile(userID);
			pr.deleteAllRatedProperties(userID);
			if (deleted) {
				System.out.println("done");
			} else {
				System.out.println("Could not delete user preferences!");
			}
			
			return new CRSRecommendationTurn(results.toArray(new CRSRecommendation[results.size()]));
		} catch (Exception e) {
			e.printStackTrace();
			boolean deleted = es.deleteAllRatedEntities(userID);
			ps.deleteUserProfile(userID);
			pr.deleteAllRatedProperties(userID);
			if (deleted) {
				System.out.println("done");
			} else {
				System.out.println("Could not delete user preferences!");
			}
		}	
		return null;
	}
	
	private static CRSRecommendation addPreferencesAndGetRecommendations(
			String conversationID,
			int turnIndex,
			int recIndex,
			List<CRSEntity> actualEntities, 
			CRSEntity[] actualRecommendations,
			CRSMessage[] preferenceMessages,
			String[] preferenceStrings,
			CRSMessage recommendation,
			List<CRSEntity> recommendationsInDataset,
			double feedback) throws Exception {
		if (recommendationsInDataset.size() > 0 && actualEntities.size() > 0) {
			CRSRecommendation result = getIntentsAndPagerank(conversationID, turnIndex, recIndex, preferenceStrings, recommendation, feedback, actualEntities, preferenceMessages, recommendationsInDataset);
			return result;
		} else {
			//Se nessuno dei film raccomandati è nel database, si salta l'analisi
			return new CRSRecommendation(
							preferenceStrings, 
							recommendation.getUtterance(), 
							feedback,
							actualEntities.toArray(new CRSEntity[actualEntities.size()]),
							null,
							null,
							null,
							true
						);
		}
	}
	
	private static CRSRecommendation getIntentsAndPagerank(
			String convId, int turnIndex, int recIndex, 
			String[] preferenceStrings,
			CRSMessage recommendation,
			double feedback,
			List<CRSEntity> actualEntities,
			CRSMessage[] preferenceMessages, 
			List<CRSEntity> recommendationsInDataset
			) throws Exception {
		int numCorrectIntents = 0;
		String[] intents = new String[preferenceMessages.length];
		List<CRSEntity> entitiesInDataset = new ArrayList<>();
		for (int i = 0; i < preferenceMessages.length; i++) {
			CRSMessage message = preferenceMessages[i];
			JsonObject intentJson = DialogFlowConnector.processMessage(message.getUtterance(), "1234567890");
			String intent = "";
			try {
				intent = intentJson.getAsJsonObject("result").getAsJsonObject("metadata").get("intentName").getAsString();
			} catch (Exception e) {
				intent = intentJson.getAsJsonObject("result").get("action").getAsString();
			}
			if (intent.equals("preference")) {
				numCorrectIntents++;
			}
			intents[i] = intent;
			entitiesInDataset.addAll(getEntitiesInDataset(message.getEntities()));
		}
		
		String[] pagerank;
		if (numCorrectIntents == preferenceMessages.length) {
			System.out.println("All intents recognized correctly. No need to redo pagerank");
			pagerank = getPagerankFromUpperBound(convId, turnIndex, recIndex);
		} else {
			for (CRSEntity entity: entitiesInDataset) {
				if (es.isEntity(entity.getId())) {
					es.addEntityPreference(userID, entity.getId(), entity.getRating(), "user");
				} else if (pr.isPropertyObject(entity.getId())) {
					//Siccome ai fini della raccomandazione il tipo di proprietà non è rilevante, prendo il primo dalla lista
					List<String> propertyTypes = pr.getPropertyTypes(entity.getId()).get(entity.getId());
					if (propertyTypes != null && propertyTypes.size() > 0) {
						pr.addPropertyPreference(userID, entity.getId(), propertyTypes.get(0), entity.getRating(), "user");
					} else {
						//Inserisco un tipo di proprietà a caso per evitare di restituire null
						String log = "Property " + entity.getId() + " has no property types! Defaulting to P161";
						System.out.println(log);
						pr.addPropertyPreference(userID, entity.getId(), "P161", entity.getRating(), "user");
					}
				}
			}
			
			List<String> pagerankResults = TestPagerank.createGraphAndRunPageRankTest(userID);
			pagerank = pagerankResults.toArray(new String[pagerankResults.size()]);
			System.out.println("Adding preference for: " + entitiesInDataset + "\nPagerank results are " + pagerankResults);
		}
		
		CRSRecommendation rec = new CRSRecommendation(
				preferenceStrings, 
				recommendation.getUtterance(), 
				feedback,
				actualEntities.toArray(new CRSEntity[actualEntities.size()]),
				recommendationsInDataset.toArray(new CRSEntity[recommendationsInDataset.size()]),
				null,
				pagerank,
				false
			);
		rec.setIntents(intents);
		return rec;
	}
	
	private static String[] getPagerankFromUpperBound(String convId, int turnIndex, int recIndex) throws IOException {
		FileReader reader = new FileReader(upperboundPath);
		JsonReader jReader = new JsonReader(reader);
		jReader.beginArray();
		
		while (jReader.hasNext()) {
			CRSRecommendationConversation rConv = gson.fromJson(jReader, CRSRecommendationConversation.class);
			if (rConv.getId().equals(convId)) {
				return rConv.getTurns()[turnIndex].getRecommendations()[recIndex].getPagerankResults();
			}
		}
		return null;
	}
	
	public static List<CRSEntity> getEntitiesInDataset(CRSEntity[] entityList) {
		List<CRSEntity> result = new ArrayList<>();
		for (CRSEntity entity: entityList) {
			if (entity.isInDataset()) {
				result.add(entity);
			}
		}
		return result;
	}
}
