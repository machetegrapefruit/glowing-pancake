package test.babi;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class BabiResultAnalyzer {
   
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
		
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		while (jReader.hasNext()) {
			BabiRecommendation recommendation = gson.fromJson(jReader, BabiRecommendation.class);
			numResults++;
			String recMovieUri = recommendation.getRecMovieUri();
			String[] recommendations = recommendation.getPagerankResults();
			int index = getIndex(recommendations, recMovieUri);
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
		jReader.endArray();
		jReader.close();
		System.out.println("Number of correct recommendations in top-5: " + foundRecommendations.get(5) + " (" + ((double) foundRecommendations.get(5) / numResults) + ")");
		System.out.println("Number of correct recommendations in top-10: " + foundRecommendations.get(10) + " (" + ((double) foundRecommendations.get(10) / numResults) + ")");
		System.out.println("Number of correct recommendations in top-20: " + foundRecommendations.get(20) + " (" + ((double) foundRecommendations.get(20) / numResults) + ")");
		System.out.println("Number of correct recommendations in top-50: " + foundRecommendations.get(50) + " (" + ((double) foundRecommendations.get(50) / numResults) + ")");
		System.out.println("Number of correct recommendations in top-100: " + foundRecommendations.get(100) + " (" + ((double) foundRecommendations.get(100) / numResults) + ")");
    }
    
    private static void updateCount(Map<Integer, Integer> map, int limit) {
    	int oldCount = map.get(limit);
    	map.remove(limit);
    	map.put(limit, oldCount + 1);
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
