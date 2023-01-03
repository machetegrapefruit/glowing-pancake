package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.search.spell.StringDistance;

public class LevenshteinDistanceCalculator {
	public static final double EPSILON = 0.005;
	
	//Soglia minima usata per considerare una similarità
	public static final double MIN_THRESHOLD = 0.5;
	public static final double PERFECT_THRESHOLD = 0.99;
	
	/**
	 * Restituisce le similarità tra una frase e una lista di possibili valori
	 * La frase è divisa in più parti, e ciascuna di queste parti è confrontata con
	 * tutti i valori possibili calcolando la distanza di Levenshtein.
	 * Gli elementi sono nello stesso ordine della lista possibleValues
	 */
	public static List<DistanceMeasure> getSimilarity(String sentence, List<String> possibleValues) {
		//Si divide la frase in token
		String[] sentenceSplit = sentence.split("\\s+");
		List<DistanceMeasure> distances = new ArrayList<DistanceMeasure>();
		StringDistance dist = new LevensteinDistance();
		
		for (int i = 0; i < possibleValues.size(); i++) {
			String s = possibleValues.get(i);
			//Per ogni valore possibile
			float maxSimilarity = 0;
			int bestStart = -1;
			int bestEnd = -1;
			String bestMatch = "";
			//Si calcola il numero di parole di cui è composto
			int possibleValueLength = s.split("\\s+").length;
			if (possibleValueLength > sentenceSplit.length) {
				/*Se il valore possibile contiene più parole della frase
				  Si calcola direttamente la distanza tra frase e valore possibile*/
				distances.add(new DistanceMeasure(s, 
						dist.getDistance(sentence, s), 
						i));
			} else {
				for (int j = 0; j <= sentenceSplit.length - possibleValueLength; j++) {
					/*Altrimenti, data n la lunghezza del valore possibile
					  Si analizza la frase un n-gramma alla volta*/
					String nGram = getNgram(sentenceSplit, possibleValueLength, j).toLowerCase();
					float distance = dist.getDistance(nGram, s.toLowerCase());
					if (distance > maxSimilarity) {
						maxSimilarity = distance;
						bestStart = j;
						bestEnd = j + possibleValueLength;
						bestMatch = nGram;
					}
				}
				/*La similarità tra frase e valore possibile è data dalla similarità massima ottenuta
				  tra il valore possibile e un n-gramma della frase*/
				distances.add(new DistanceMeasure(s, maxSimilarity, i));
			}
		}
		
		return distances;
	}
	
	/**
	 * Data una frase in input e una lista di valori possibili, restituisce i valori con similarità più alta.
	 * In caso di parità, si sceglie il valore composto da più parole
	 */
	public static List<DistanceMeasure> getMostSimilar(String sentence, List<String> possibleValues, int number, double minSimilarity) {
		List<DistanceMeasure> distancesList = getSimilarity(sentence, possibleValues);
		System.out.println("Calculated distances: " + distancesList.toString());
		
		Collections.sort(distancesList);
		Collections.reverse(distancesList);
		
		List<DistanceMeasure> filteredList = new ArrayList<DistanceMeasure>();
		
		/*
		 * Se c'è un elemento con similarità quasi 1, aggiungi soltanto quello
		 * (non c'è bisogno di disambiguare) - DEPRECATO
		 */
		/*double maxSimilarity = distancesList.get(0).getDistance();
		if (maxSimilarity > PERFECT_THRESHOLD) {
			System.out.println("Reached perfect threshold");
			filteredList.add(distancesList.get(0));
		} else {
			int i = 0;
			while (i < distancesList.size() && distancesList.get(i).getDistance() > minSimilarity) {
				filteredList.add(distancesList.get(i));
				i++;
			}
		}*/
		int i = 0;
		while (i < distancesList.size() && distancesList.get(i).getDistance() > minSimilarity) {
			filteredList.add(distancesList.get(i));
			i++;
		}
		System.out.println("filteredList contains " + filteredList.toString());
				
		return filteredList;
	}
	
	public static MatchMap findMatches(String sentence, List<String> possibleValues, double minSimilarity) {
		MatchMap matches = new MatchMap();
		String[] sentenceSplit = sentence.split("\\s+");
		StringDistance dist = new LevensteinDistance();
		
		for (int i = 0; i < possibleValues.size(); i++) {
			//Per ogni valore possibile
			String s = possibleValues.get(i);
			//Si calcola il numero di parole di cui è composto
			int possibleValueLength = s.split("\\s+").length;
			if (possibleValueLength > sentenceSplit.length) {
				/*Se il valore possibile contiene più parole della frase
				  Si calcola direttamente la distanza tra frase e valore possibile*/
				float distance = dist.getDistance(sentence, s);
				if (distance > minSimilarity) {
					matches.add(0, sentenceSplit.length - 1, sentence, new DistanceMeasure(s, distance, i));
				}
			} else {
				for (int j = 0; j <= sentenceSplit.length - possibleValueLength; j++) {
					/*Altrimenti, data n la lunghezza del valore possibile
					  Si analizza la frase un n-gramma alla volta*/
					String nGram = getNgram(sentenceSplit, possibleValueLength, j).toLowerCase();
					float distance = dist.getDistance(nGram, s.toLowerCase());
					if (distance > minSimilarity) {
						matches.add(j, j + possibleValueLength - 1, nGram, new DistanceMeasure(s, distance, i));
					}
				}
			}
		}
		
		return matches;
	}
	
	private static String getNgram(String[] splitText, int size, int start) {
		int end = start + size;
		if (end <= splitText.length) {
			StringJoiner sj = new StringJoiner(" ");
			for (int i = start; i < end; i++) {
				sj.add(splitText[i]);
			}
			return sj.toString();
		} else {
			throw new ArrayIndexOutOfBoundsException("getNgram() failed! This should not happen");
		}
	}
	
	public static void main(String[] args) {
		List<String> possibleValues = new ArrayList<String>();
		Collections.addAll(possibleValues, "starring", "producer", "director", "genre", "category");
		printDistances("staring", possibleValues);
		
		possibleValues = new ArrayList<String>();
		Collections.addAll(possibleValues, 
				"Ghostbusters", 
				"Ghostbusters II", 
				"Forrest Gump", 
				"Saving Private Ryan", 
				"The Blues Brothers",
				"Quentin Tarantino",
				"producer",
				"director",
				"starring");
		printDistances("I really like ghostbusters", possibleValues);
		printDistances("I really like ghostbusters II", possibleValues);
		printDistances("I really like ghostbusters ii", possibleValues);
		printDistances("I really like ghostbusters 2", possibleValues);
		printDistances("blues broters", possibleValues);
		printMatches("I really like ghostbusters", possibleValues);
		printMatches("I really like ghostbusters II", possibleValues);
		printMatches("I really like ghostbusters ii", possibleValues);
		printMatches("I really like ghostbusters 2", possibleValues);
		printMatches("I really like ghostbusters and ghostbusters II", possibleValues);
		printMatches("I really like ghostbusters II and ghostbusters", possibleValues);
		printMatches("I really like The Blues Brothers ghostbusters II and ghostbusters", possibleValues);
		printMatches("I really like Tarantino", possibleValues);
		printMatches("blues broters", possibleValues);
		printMatches("I mean both as a producer and starring", possibleValues);
		printMatches("as director, and starring", possibleValues);
		printMatches("I really like ghostbusters II The Blues Brothers and ghostbusters", possibleValues);
	}
	
	private static void printDistances(String sentence, List<String> possibleValues) {
		System.out.println("\nDistances for " + sentence);
		List<DistanceMeasure> distances = getSimilarity(sentence, possibleValues);
		for (DistanceMeasure m: distances) {
			System.out.println(m.getValue() + ": " + m.getDistance());
		}
		System.out.println("Most similar is " + getMostSimilar(sentence, possibleValues, 1, LevenshteinDistanceCalculator.MIN_THRESHOLD).get(0).getValue());
	}
	
	private static void printMatches(String sentence, List<String> possibleValues) {
		System.out.println("\nMatches for " + sentence);
		MatchMap matches = findMatches(sentence, possibleValues, 0.9);
		for (Entry<Match, List<DistanceMeasure>> entry: matches.getMatches().entrySet()) {
			System.out.println("Match is " + entry.getKey().getMatchString() + ": " + entry.getValue());
		}
		List<List<Match>> groups = matches.getGroupedMatches();
		System.out.println("Groups: " + groups);
	}
}
