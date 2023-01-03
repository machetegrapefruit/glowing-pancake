package test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import utils.FileUtils;

public class RecEntityAnalyzer {
	public static void main(String[] args) throws JsonIOException, JsonSyntaxException, IOException {
		String logPath = args[0];
		String output = args[1];
		List<RecUserData> userDataList = new ArrayList<>();
		Map<String, List<RecEntityRating>> userMessages = new HashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		
    	FileReader reader = new FileReader(logPath);
		JsonReader jReader = new JsonReader(reader);
		jReader.beginArray();
		//Suddivido i messaggi per utente (nota: i messaggi sono già ordinati per timestamp crescente)
		while (jReader.hasNext()) {
			RecEntityRating rating = gson.fromJson(jReader, RecEntityRating.class);
			if (userMessages.containsKey(rating.getUserID() + "")) {
				userMessages.get(rating.getUserID() + "").add(rating);
			} else {
				List<RecEntityRating> list = new ArrayList<>();
				list.add(rating);
				userMessages.put(rating.getUserID() + "", list);
			}
		}
		
		//Per ogni utente
		for (Entry<String, List<RecEntityRating>> entry: userMessages.entrySet()) {
			int hits = 0;
			int likedCount = 0;
			int recsCount = 0;
			int recsCount2 = 0;
			int ratingsCount = 0;
			int recListCount = 0;
			boolean hasHit = false;
			int currentRecList = 0;
			List<Double> averagePrecisions = new ArrayList<Double>();
			List<Double> averagePrecisionsAlt = new ArrayList<Double>();
			List<Double> nDCGList = new ArrayList<Double>();
			int likedInCurrentList = 0;
			Map<Integer, Boolean> likedMap = new HashMap<>();
			int ratingsInCurrentList = 0;
			String[] currentRecEntities = null;
			for (RecEntityRating rating: entry.getValue()) {
				if (rating.getNumberRecommendationList() != currentRecList) {
					if (rating.getNumberRecommendationList() < currentRecList) {
						throw new RuntimeException("This should not happen");
					}
					if (ratingsInCurrentList > 0) {
						boolean[] likedArray = getArray(likedMap);
						recsCount2 += Math.max(currentRecEntities.length, likedArray.length);
						averagePrecisions.add(getAveragePrecision(likedArray, likedArray.length));
						averagePrecisionsAlt.add(getAveragePrecision(likedArray, ratingsInCurrentList));
						nDCGList.add(getnDCG(likedArray));
					}
					currentRecList = rating.getNumberRecommendationList();
					hasHit = false;
					recsCount += rating.getRecommendationsList().length;
					recListCount++;
					likedInCurrentList = 0;
					ratingsInCurrentList = 0;
					currentRecEntities = rating.getRecommendationsList();
					likedMap = new HashMap<>();
				}
				ratingsCount++;
				ratingsInCurrentList++;
				if (rating.isLike()) {
					if (!hasHit) {
						hits++;
						hasHit = true;
					}
					likedCount++;
					likedInCurrentList++;
					likedMap.put(rating.getPosition(), true);
				} else {
					likedMap.put(rating.getPosition(), false);
				}
			}
			if (ratingsInCurrentList > 0) {
				boolean[] likedArray = getArray(likedMap);
				int l = Math.max(likedArray.length, currentRecEntities.length);
				recsCount2 += l;
				averagePrecisions.add(getAveragePrecision(likedArray, l));
				averagePrecisionsAlt.add(getAveragePrecision(likedArray, ratingsInCurrentList));
				nDCGList.add(getnDCG(likedArray));
			}
			
			double hitRate = ((double) hits / recListCount);
			double accuracy = ((double) likedCount / recsCount2);
			double accuracy2 = ((double) likedCount / ratingsCount);
			double averagePrecision = getAverage(averagePrecisions);
			double averagePrecisionAlt = getAverage(averagePrecisionsAlt);
			double averagendcg = getAverage(nDCGList);
			RecUserData user = new RecUserData(entry.getKey(), hits, hitRate, recListCount, likedCount, recsCount2, ratingsCount, accuracy, accuracy2, averagePrecision, averagePrecisionAlt, averagendcg);
			userDataList.add(user);
			System.out.println("Results for user " + entry.getKey());
			System.out.println("Hit count: " + hits);
			System.out.println("Number of recommendation lists: " + recListCount);
			System.out.println("Hit rate: " + hitRate);
			System.out.println("Number of likes: " + likedCount);
			System.out.println("Number of recommended entities: " + recsCount2);
			System.out.println("Accuracy: " + accuracy);
			System.out.println("Accuracy (alt): " + accuracy2);
			System.out.println("Average precisions (for each session): " + averagePrecisions);
			System.out.println("Average precision: " + averagePrecision);
			System.out.println("Average precisions (alt for each session): " + averagePrecisionsAlt);
			System.out.println("Average precision (alt): " + averagePrecisionAlt);
			System.out.println("Average nDCG: " + averagendcg);
			System.out.println("---------------------------------");
			
		}
		
		String csv = RecUserData.getCSVHeader() + "\n";
		for (RecUserData u: userDataList) {
			csv += u.toCSVRow() + "\n";
		}
		FileUtils.writeToFile(output, csv);
	}
	
	private static boolean[] getArray(Map<Integer, Boolean> map) {
		int maxIndex = 0;
		for (Entry<Integer, Boolean> entry: map.entrySet()) {
			maxIndex = Math.max(maxIndex, entry.getKey());
		}
		boolean[] array = new boolean[maxIndex + 1];
		for (Entry<Integer, Boolean> entry: map.entrySet()) {
			array[entry.getKey()] = entry.getValue();
		}
		return array;
	}
	
	private static double getAveragePrecision(boolean[] likedVector, int size) {
		double total = 0.0;
		int likedCount = 0;
		for (int i = 0; i < likedVector.length; i++) {
			if (likedVector[i]) {
				likedCount++;
				total += (double) likedCount / (i + 1);
			}
		}
		if (likedCount > 0) {
			return (double) total / size;		
		} else {
			return 0;
		}
	}
	
	private static double getAverage(List<Double> list) {
		double total = 0;
		for (double ap: list) {
			total += ap;
		}
		return total / list.size();
	}
	
	private static double getnDCG(boolean[] gainVector) {
		double dcg = getDCG(gainVector);
		boolean[] idealVector = new boolean[gainVector.length];
		int count = 0;
		for (boolean gain: gainVector) {
			if (gain) {
				count++;
			}
		}
		for (int i = 0; i < count; i++) {
			idealVector[i] = true;
		}
		double idcg = getDCG(idealVector);
		if (idcg > 0) {
			return dcg/idcg;
		} else {
			return 0;
		}
	}
	
	private static double getDCG(boolean[] gainVector) {
		double total = 0;
		for (int i = 0; i < gainVector.length; i++) {
			boolean gain = gainVector[i];
			if (gain) {
				if (i == 0) {
					total += 1;
				} else {
					total += 1/(Math.log(i + 1)/Math.log(2));
				}
			}
		}
		return total;
	}
}
