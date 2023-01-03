package test.grouplens;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import dialog.SentimentAnalyzerConnector;
import dialog.SentimentObject;
import test.DialogFlowConnector;
import utils.Alias;

public class GrouplensTest {
	public static void main(String[] args) throws IOException {
		String datasetPath = args[0];
		String itemsPath = args[1];
		String outputPath = args[2];
		FileWriter writer = new FileWriter(outputPath);
		JsonWriter jWriter = new JsonWriter(writer);
		jWriter.beginArray();
		jWriter.setIndent("  ");
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		Map<Integer, GrouplensInstance> instances = new HashMap<>();
		BufferedReader datasetReader = Files.newBufferedReader(Paths.get(datasetPath), StandardCharsets.ISO_8859_1);
		CSVParser datasetParser = new CSVParser(datasetReader, 
				CSVFormat.DEFAULT
				.withHeader("firstQuery", 
						"followupQuery", 
						"modality", 
						"assignmentCategory", 
						"firstQueryTime", 
						"howOftenVoiceAssistant", 
						"otherVoiceAssistant", 
						"shouldIncludeRated")
				.withIgnoreSurroundingSpaces(true).withSkipHeaderRecord(true)
				);
		int i = 2;
		for (CSVRecord record: datasetParser) {
			System.out.println(i + " " + record.get("firstQuery"));
			GrouplensInstance instance = new GrouplensInstance();
			instance.setFirstQuery(record.get("firstQuery"));
			instance.setFollowupQuery(record.get("followupQuery"));
			instances.put(i, instance);
			i++;
		}
		
		i = 2;
		BufferedReader itemsReader = Files.newBufferedReader(Paths.get(itemsPath), StandardCharsets.ISO_8859_1);
		CSVParser itemsParser = new CSVParser(itemsReader, 
				CSVFormat.DEFAULT
				.withHeader("Attori", 
						"Registi", 
						"Generi", 
						"Film", 
						"Canzoni", 
						"Produttori / Case produttrici", 
						"Appunti", 
						"Legenda")
				.withIgnoreSurroundingSpaces(true).withSkipHeaderRecord(true)
				);
		for (CSVRecord record: itemsParser) {
			GrouplensInstance instance = instances.get(i);
			String actorsString = record.get("Attori");
			String directorsString = record.get("Registi");
			String genresString = record.get("Generi");
			String moviesString = record.get("Film");
			String producersString = record.get("Produttori / Case produttrici");
			List<String> items = new ArrayList<>();
			String[] actorsSplit = actorsString.split("/");
			for (String a: actorsSplit) {
				String[] s = a.split(":");
				if (s.length > 1) {
					items.add(s[1].trim());
				}
			}
			String[] directorsSplit = directorsString.split("/");
			for (String a: directorsSplit) {
				String[] s = a.split(":");
				if (s.length > 1) {
					items.add(s[1].trim());
				}
			}
			String[] genresSplit = genresString.split("/");
			for (String a: genresSplit) {
				String[] s = a.split(":");
				if (s.length > 1) {
					items.add(s[1].trim());
				}
			}
			String[] moviesSplit = moviesString.split("/");
			for (String a: moviesSplit) {
				String[] s = a.split(":");
				if (s.length > 1) {
					items.add(s[1].trim());
				}
			}
			String[] producersSplit = producersString.split("/");
			for (String a: producersSplit) {
				String[] s = a.split(":");
				if (s.length > 1) {
					items.add(s[1].trim());
				}
			}
			instance.setItems(items);
			i++;
		}
		
		for (Entry<Integer, GrouplensInstance> entry: instances.entrySet()) {
			GrouplensInstance instance = entry.getValue();
			System.out.println("Instance n. " + entry.getKey());
			JsonObject firstQueryIntentJson = DialogFlowConnector.processMessage(instance.getFirstQuery(), "1234567890");

			String firstQueryIntent = "";
			try {
				firstQueryIntent = firstQueryIntentJson.getAsJsonObject("result").getAsJsonObject("metadata").get("intentName").getAsString();
			} catch (Exception e) {
				firstQueryIntent = firstQueryIntentJson.getAsJsonObject("result").get("action").getAsString();
			}
			
			if (instance.getFollowupQuery() != null && !instance.getFollowupQuery().trim().equals("")) {
				JsonObject followupQueryIntentJson = DialogFlowConnector.processMessage(instance.getFollowupQuery(), "1234567890");
				String followupQueryIntent = "";
				try {
					followupQueryIntent = followupQueryIntentJson.getAsJsonObject("result").getAsJsonObject("metadata").get("intentName").getAsString();
				} catch (Exception e) {
					followupQueryIntent = followupQueryIntentJson.getAsJsonObject("result").get("action").getAsString();
				}
				System.out.println("followupQueryIntent is " + followupQueryIntent);
				instance.setFollowupQueryIntent(followupQueryIntent);
			}

			System.out.println("firstQueryIntent is " + firstQueryIntent);
			instance.setFirstQueryIntent(firstQueryIntent);
			
			SentimentAnalyzerConnector sac = new SentimentAnalyzerConnector(true, true, true);
			List<SentimentObject> sentimentObjectsFirst = sac.getSentiment(instance.getFirstQuery());
			List<SentimentObject> sentimentObjectsFollowup = sac.getSentiment(instance.getFollowupQuery());
			sentimentObjectsFirst.addAll(sentimentObjectsFollowup);
			List<String> recognized = new ArrayList<>();
			for (SentimentObject so: sentimentObjectsFirst) {
				List<Alias> aliases = so.getAliases();
				for (String item: instance.getItems()) {
					if (isInAliasList(item, aliases)) {
						System.out.println("Recognized " + item);
						recognized.add(item);
					}
				}
			}
			instance.setrecognizedItems(recognized);
			gson.toJson(instance, GrouplensInstance.class, jWriter);
		}
		jWriter.endArray();
		jWriter.close();
	}
	
	private static boolean isInAliasList(String entity, List<Alias> disambiguationOptions) {
		for (Alias a: disambiguationOptions) {
			if (a.getURI().equals(entity)) {
				return true;
			}
		}
		return false;
	}
}
