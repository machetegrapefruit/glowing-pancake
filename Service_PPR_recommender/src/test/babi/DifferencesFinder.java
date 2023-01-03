package test.babi;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import test.Entity;
import test.TestAddedElement;
import utils.MatchedElement;

public class DifferencesFinder {
	public static void findDifferences(String path1, String path2) throws IOException {
    	FileReader reader = new FileReader(path1);
		JsonReader jReader = new JsonReader(reader);
		jReader.beginArray();
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		int count = 0;
		while (jReader.hasNext()) {
			BabiDialogRecommendation recommendation = gson.fromJson(jReader, BabiDialogRecommendation.class);
			BabiDialogRecommendation recommendation2 = findRecommendation(recommendation.getId(), path2);
			int correct1 = getNumCorrectEntities(recommendation);
			int correct2 = getNumCorrectAutoAddedEntities(recommendation2);
			if (correct1 != correct2) {
				System.out.println("Found difference in sentence " + recommendation.getId());
			}

			count++;
			if (count % 100 == 0) {
				System.out.println("Analyzed " + count + " sentences");
			}
		}
		
		jReader.endArray();
		jReader.close();

	}
	
	public static int getNumCorrectEntities(BabiDialogRecommendation recommendation) {
		int correct = 0;
		Entity[] actualEntities = recommendation.getActualEntities();
		TestAddedElement[] recognizedEntities = recommendation.getRecognizedEntities();
		for (Entity e: actualEntities) {
			if (find(recognizedEntities, e.getUri()) > -1) {
				correct++;
			}
		}
		return correct;
	}
	
	public static int getNumCorrectAutoAddedEntities(BabiDialogRecommendation recommendation) {
		int correct = 0;
		Entity[] actualEntities = recommendation.getActualEntities();
		TestAddedElement[] recognizedEntities = recommendation.getRecognizedEntities();
		for (Entity e: actualEntities) {
			int index = find(recognizedEntities, e.getUri());
			if (index > -1 && recognizedEntities[index].isAutoAdded()) {
				correct++;
			}
		}
		return correct;
	}
	
    public static int find(MatchedElement[] elements, String item)  {
    	for (int i = 0; i < elements.length; i++) {
    		if (elements[i].getElement().getURI().equals(item)) {
    			return i;
    		}
    	}
    	return -1;
    }
	
	public static BabiDialogRecommendation findRecommendation(int id, String path) throws IOException {
		BabiDialogRecommendation result = null;
    	FileReader reader = new FileReader(path);
		JsonReader jReader = new JsonReader(reader);
		jReader.beginArray();
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		while (result == null && jReader.hasNext()) {
			BabiDialogRecommendation recommendation = gson.fromJson(jReader, BabiDialogRecommendation.class);
			if (recommendation.getId() == id) {
				result = recommendation;
			}
		}
		if (result == null) {
			jReader.endArray();
		}
		jReader.close();
		return result;
	}
	
	
	public static void main(String[] args) throws IOException {
		findDifferences(args[0], args[1]);
	}
}
