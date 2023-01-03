package test.grouplens;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import utils.FileUtils;

public class GrouplensAnalyzer {
	public static void main(String[] args) throws IOException {
		String path = args[0];
		String entitiesPath = args[1];
		String propertiesPath = args[2];
		
		Set<String> entities = new HashSet<>();
		Set<String> properties = new HashSet<>();
		List<String> entityLines = FileUtils.readFileAsList(entitiesPath);
		List<String> propertyLines = FileUtils.readFileAsList(propertiesPath);
		
		for (String entity: entityLines) {
			String[] split = entity.split("\\|");
			entities.add(split[0]);
		}
		
		for (String property: propertyLines) {
			String[] split = property.split("\\|");
			properties.add(split[0]);
		}
		
		FileReader reader = new FileReader(path);
		JsonReader jReader = new JsonReader(reader);
		jReader.beginArray();
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		int numItems = 0;
		int correctlyRecognizedItems = 0;
		int numSentences = 0;
		int correctIntents = 0;
		while (jReader.hasNext()) {
			GrouplensInstance instance = gson.fromJson(jReader, GrouplensInstance.class);
			List<String> items = instance.getItems();
			List<String> recognized = instance.getRecognizedItems();
			for (String item: items) {
				if (entities.contains(item) || properties.contains(item)) {
					numItems++;
					if (recognized.contains(item)) {
						correctlyRecognizedItems++;
					}
				}
			}
			numSentences++;
			if (instance.getFollowupQuery() != null) {
				numSentences++;
			}
			if (instance.getFirstQueryIntent().equals("request_recommendation")) {
				correctIntents++;
			}
			if (instance.getFollowupQuery() != null 
					&& !instance.getFollowupQuery().equals("") 
					&& instance.getFollowupQueryIntent().equals("request_recommendation")) {
				correctIntents++;
			}
		}
		System.out.println("Number of sentences: " + numSentences);
		System.out.println("Number of items: " + numItems);
		System.out.println("Number of correct intents: " + correctIntents + " ratio (" + ((double) correctIntents / numSentences) + ")");
		System.out.println("Number of correct entities: " + correctlyRecognizedItems + " ratio (" + ((double) correctlyRecognizedItems / numItems) + ")");
	}
}
