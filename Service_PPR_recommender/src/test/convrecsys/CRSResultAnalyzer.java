package test.convrecsys;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.plaf.synth.SynthSpinnerUI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import test.TestAddedElement;
import utils.MatchedElement;

public class CRSResultAnalyzer {
	private static Gson gson;
	
	public static void main(String[] args) throws IOException {
		String path = args[0];
		String ubPath = args[1];
		FileReader reader = new FileReader(path);
		JsonReader jReader = new JsonReader(reader);
		jReader.beginArray();
		Map<Integer, Integer> foundRecommendations = new HashMap<Integer, Integer>();
		foundRecommendations.put(5, 0);
		foundRecommendations.put(10, 0);
		foundRecommendations.put(20, 0);
		foundRecommendations.put(50, 0);
		foundRecommendations.put(100, 0);
		int maxNumRecEntities = 0;
		int numResults = 0;
		int numSentences = 0;
		
		int entitiesCount = 0;
		int correctEntitiesCount = 0;
		int correctPreferenceIntents = 0;
		int correctSentimentsCount = 0;
		int autoAddedCount = 0;
		Set<String> distinctEntities = new HashSet<String>();
		Set<String> distinctCorrectEntities = new HashSet<String>();
		
		gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		Map<String, CRSRecommendationConversation> ubMap = getUpperbound(ubPath);
		//int x = 0;
		while (jReader.hasNext()) {
			//System.out.println(x);
			//x++;
			CRSRecommendationConversation conv = gson.fromJson(jReader, CRSRecommendationConversation.class);
			CRSRecommendationConversation uConv = ubMap.get(conv.getId());
			for (int i = 0; i < conv.getTurns().length; i++) {
				CRSRecommendationTurn turn = conv.getTurns()[i];
				for (int j = 0; j < turn.getRecommendations().length; j++) {
					//System.out.println("Conv " + conv.getId() + " turn " + i + " rec " + j);
					CRSRecommendation rec = turn.getRecommendations()[j];
					//if (rec.getActualRecommendedEntities() != null) {
					CRSRecommendation ubRec = uConv.getTurns()[i].getRecommendations()[j];
					if (!ubRec.isSkipped()) {
						List<CRSEntity> actualEntities = getEntitiesInDataset(rec.getActualPreferenceEntities());
						List<CRSEntity> ubActualEntities = getEntitiesInDataset(ubRec.getActualPreferenceEntities());
						entitiesCount = entitiesCount + ubActualEntities.size();
						List<CRSEntity> actualRecommendedEntities = null;
						actualRecommendedEntities = getEntitiesInDataset(ubRec.getActualRecommendedEntities());
						

						if (actualEntities.size() > ubActualEntities.size()) {
							System.out.println("actualEntities.size() > ubActualEntities.size()!");
						} else if (actualEntities.size() < ubActualEntities.size()) {
							System.out.println("actualEntities.size() < ubActualEntities.size()!");
						}
						/*if (rec.getActualRecommendedEntities() == null) {
							actualRecommendedEntities = getEntitiesInDataset(ubRec.getActualRecommendedEntities());
						} else {
							actualRecommendedEntities = getEntitiesInDataset(rec.getActualRecommendedEntities());
						}*/
						TestAddedElement[] recognized = rec.getRecognizedEntities();
						String[] pagerank = rec.getPagerankResults();
						maxNumRecEntities = Math.max(maxNumRecEntities, actualRecommendedEntities.size());

						if (recognized != null) {
							int correct = 0;
							int autoAdd = 0;
							Set<String> found = new HashSet<>();
							for (CRSEntity e: ubActualEntities) {
								if (!distinctEntities.contains(e.getId())) {
									distinctEntities.add(e.getId());
								}
								int index = find(recognized, e.getId());
								if (index > -1 && !found.contains(e.getId())) {
									correctEntitiesCount++;
									found.add(e.getId());
									if (!distinctCorrectEntities.contains(e.getId())) {
										distinctCorrectEntities.add(e.getId());
									}
									if (recognized[index].isAutoAdded() && recognized[index].getRating() == e.getRating()) {
										correctSentimentsCount++;
										correct++;
									}
								}
							}
							found = new HashSet<>();
							for (TestAddedElement entity: recognized) {
								if (!found.contains(entity.getElement().getURI()) && entity.isAutoAdded() && entity.getRating() > -1 && find(actualEntities, entity) > -1) {
									autoAddedCount++;
									autoAdd++;
									found.add(entity.getElement().getURI());
								}
							}
							/*if (correct != autoAdd) {
								System.out.println();
							}*/
						}
						if (rec.getIntents() != null) {
							for (String intent: rec.getIntents()) {
								if (intent.equals("preference")) {
									correctPreferenceIntents++;
								}
							}
						}
						numResults += actualRecommendedEntities.size();
						numSentences += rec.getPreferences().length;
						//Prima di aggiungere le raccomandazioni corrette controllo se ci sono gli intent
						//Se ci sono, allora aggiungo soltanto se almeno un intent è stato riconosciuto correttamente
						//Necessario perchè nell'intent test non avevo skippato la raccomandazione se gli intent non erano riconosciuti
						if (pagerank != null && (rec.getIntents() == null || getCorrectIntents(rec.getIntents()) > 0)) {
							for (CRSEntity recEntity : actualRecommendedEntities) {
								int index = getIndex(pagerank, recEntity.getId());
								addNewFoundRecommendation(foundRecommendations, index);
							}
						} else {
							System.out.println();
						}
					}
				}
			}
		}
		jReader.endArray();
		jReader.close();
		System.out.println("Max number of recommended entities: " + maxNumRecEntities);
		System.out.println("Total number of recommended entities: " + numResults);
		System.out.println("Total number of sentences: " + numSentences);
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
		System.out.println("Number of correctly recognized preference intents: " + correctPreferenceIntents + "(" + ((double) correctPreferenceIntents / numSentences) + ")");
		System.out.println("Number of auto-added entities: " + autoAddedCount + " (ratio " + ((double) autoAddedCount / entitiesCount ) + ")");
		System.out.println("Number of correctly recognized sentiments (only for auto-added entities): " + correctSentimentsCount + " (ratio " + ((double) correctSentimentsCount / autoAddedCount ) + ")");
		//System.out.println("distinct entities " + distinctEntities);
	}
	
	private static void addNewFoundRecommendation(Map<Integer, Integer> foundRecommendations, int index) {
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
	
	private static int getCorrectIntents(String[] intents) {
		int count = 0;
		for (String intent: intents) {
			if (intent.equals("preference")) {
				count++;
			}
		}
		return count;
	}
	
    private static void updateCount(Map<Integer, Integer> map, int limit) {
    	int oldCount = map.get(limit);
    	map.remove(limit);
    	map.put(limit, oldCount + 1);
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
	
    public static int find(MatchedElement[] elements, String item)  {
    	for (int i = 0; i < elements.length; i++) {
    		if (elements[i].getElement().getURI().equals(item)) {
    			return i;
    		}
    	}
    	return -1;
    }
    
    public static int find(List<CRSEntity> elements, MatchedElement item)  {
    	for (int i = 0; i < elements.size(); i++) {
    		if (elements.get(i).getId().equals(item.getElement().getURI())) {
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
    
    public static Map<String, CRSRecommendationConversation> getUpperbound(String path) throws IOException {
    	FileReader reader = new FileReader(path);
		JsonReader jReader = new JsonReader(reader);
		jReader.beginArray();
		Map<String, CRSRecommendationConversation> map = new HashMap<>();
		while (jReader.hasNext()) {
			CRSRecommendationConversation conv = gson.fromJson(jReader, CRSRecommendationConversation.class);
			map.put(conv.getId(), conv);
		}
		jReader.endArray();
		jReader.close();
		return map;
    }
}
