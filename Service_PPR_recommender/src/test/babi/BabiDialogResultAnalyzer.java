package test.babi;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import test.Entity;
import test.TestAddedElement;
import utils.MatchedElement;

public class BabiDialogResultAnalyzer {
	
	private static final boolean checkIntent = true;
   
    public static void analyzeDataset(String datasetPath) throws IOException {
    	FileReader reader = new FileReader(datasetPath);
		JsonReader jReader = new JsonReader(reader);
		jReader.beginArray();
		Map<Integer, Integer> foundRecommendations = new HashMap<Integer, Integer>();
		foundRecommendations.put(5, 0);
		foundRecommendations.put(10, 0);
		foundRecommendations.put(20, 0);
		foundRecommendations.put(50, 0);
		foundRecommendations.put(100, 0);
		int numResults = 0;
		
		int entitiesCount = 0;
		int correctEntitiesCount = 0;
		int correctPreferenceIntents = 0;
		int correctReqRecommIntents = 0;
		int correctSentimentsCount = 0;
		int autoAddedCount = 0;
		Set<String> distinctEntities = new HashSet<String>();
		Set<String> distinctCorrectEntities = new HashSet<String>();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		while (jReader.hasNext()) {
			BabiDialogRecommendation recommendation = gson.fromJson(jReader, BabiDialogRecommendation.class);
			Entity[] actualEntities = recommendation.getActualEntities();
			entitiesCount = entitiesCount + actualEntities.length;
			TestAddedElement[] recognizedEntities = recommendation.getRecognizedEntities();
			for (Entity e: actualEntities) {
				if (!distinctEntities.contains(e.getUri())) {
					distinctEntities.add(e.getUri());
				}
				if (find(recognizedEntities, e.getUri()) > -1) {
					correctEntitiesCount++;
					if (!distinctCorrectEntities.contains(e.getUri())) {
						distinctCorrectEntities.add(e.getUri());
					}
				}
			}
			for (TestAddedElement entity: recognizedEntities) {
				if (entity.isAutoAdded()) {
					autoAddedCount++;
					if (entity.getRating() == 1) {
						correctSentimentsCount++;
					}
				}
			}
			if (recommendation.getPreferenceIntent().equals("preference")) {
				correctPreferenceIntents++;
			}
			if (recommendation.getRequestRecommendationIntent().equals("request_recommendation")) {
				correctReqRecommIntents++;
			}
			numResults++;
			String recMovieUri = recommendation.getRecMovieUri();
			String[] recommendations = recommendation.getPagerankResults();
			int index = getIndex(recommendations, recMovieUri);
			if (!checkIntent || (
					recommendation.getPreferenceIntent().equals("preference")
					&& recommendation.getRequestRecommendationIntent().equals("request_recommendation")
					)
				) {
				if (index > -1 && index < 5) {
					updateCount(foundRecommendations, 5);
				}
				if (index > -1 && index < 10) {
					updateCount(foundRecommendations, 10);
				}
				if (index > -1 && index < 20) {
					updateCount(foundRecommendations, 20);
				}
				if (index > -1 && index < 50) {
					updateCount(foundRecommendations, 50);
				}
				if (index > -1 && index < 100) {
					updateCount(foundRecommendations, 100);
				}
			}

		}
		jReader.endArray();
		jReader.close();
		System.out.println("Number of correct recommendations in top-5: " + foundRecommendations.get(5) + " (" + ((double) foundRecommendations.get(5) / numResults) + ")");
		System.out.println("Number of correct recommendations in top-10: " + foundRecommendations.get(10) + " (" + ((double) foundRecommendations.get(10) / numResults) + ")");
		System.out.println("Number of correct recommendations in top-20: " + foundRecommendations.get(20) + " (" + ((double) foundRecommendations.get(20) / numResults) + ")");
		System.out.println("Number of correct recommendations in top-50: " + foundRecommendations.get(50) + " (" + ((double) foundRecommendations.get(50) / numResults) + ")");
		System.out.println("Number of correct recommendations in top-100: " + foundRecommendations.get(100) + " (" + ((double) foundRecommendations.get(100) / numResults) + ")");
		System.out.println("Total number of entities: " + entitiesCount
				+ ", number of correctly recognized entities: " + correctEntitiesCount
				+ " (ratio" + ((double) correctEntitiesCount / entitiesCount) + ")");
		System.out.println("Number of distinct entities: " + distinctEntities.size());
		System.out.println("Number of correctly recognized distinct entities: " + distinctCorrectEntities.size());
		System.out.println("Number of correctly recognized preference intents: " + correctPreferenceIntents + "(" + ((double) correctPreferenceIntents / numResults) + ")");
		System.out.println("Number of correctly recognized request_recommendation intents: " + correctReqRecommIntents + "(" + ((double) correctReqRecommIntents / numResults) + ")");
		System.out.println("Number of auto-added entities: " + autoAddedCount + " (ratio " + ((double) autoAddedCount / entitiesCount ) + ")");
		System.out.println("Number of correctly recognized sentiments (only for auto-added entities): " + correctSentimentsCount + " (ratio " + ((double) correctSentimentsCount / autoAddedCount ) + ")");
    }
    
    private static void updateCount(Map<Integer, Integer> map, int limit) {
    	int oldCount = map.get(limit);
    	map.remove(limit);
    	map.put(limit, oldCount + 1);
    }
    
    public static int find(MatchedElement[] elements, String item)  {
    	for (int i = 0; i < elements.length; i++) {
    		if (elements[i].getElement().getURI().equals(item)) {
    			return i;
    		}
    	}
    	return -1;
    }
    
    public static int getIndex(String[] array, String item) {
    	for (int i = 0; i < array.length; i++) {
    		if (array[i].equals(item)) {
    			return i;
    		}
    	}
    	return -1;
    }
    
	public static void main(String[] args) throws IOException {
		analyzeDataset(args[0]);
	}
}
