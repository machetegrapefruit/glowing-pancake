package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Questa classe implementa un dizionario di matching
 * Questo dizionario usa come chiavi oggetti di classe Match (che rappresenta una porzione della
 * frase dell'utente), e i valori sono liste di oggetti DistanceMeasure, ognuno contenente
 * il valore della distanza di Levenshtein della porzione di frase con un possibile valore
 *
 */
public class MatchMap {
	private Map<Match, List<DistanceMeasure>> matches;
	
	public MatchMap() {
		matches = new HashMap<>();
	}
	
	public void add(int start, int end, String matchString, DistanceMeasure distance) {
		Match m = new Match(start, end, matchString);
		if (matches.containsKey(m)) {
			matches.get(m).add(distance);
		} else {
			List<DistanceMeasure> newList = new ArrayList<>();
			newList.add(distance);
			matches.put(m, newList);
		}
	}
	
	public Map<Match, List<DistanceMeasure>> getMatches() {
		return matches;
	}
	
	public List<DistanceMeasure> get(Match m) {
		return matches.get(m);
	}
	
	/**
	 * Raggruppa tutti gli oggetti Match in più gruppi. Ogni gruppo rappresenta una singola entità 
	 * riconosciuta all'interno della frase. Questo è necessario in quanto a fronte di una singola 
	 * entità espressa dall'utente possono essere associate più porzioni di frase adiacenti.
	 */
	public List<List<Match>> getGroupedMatches() {
		List<List<Match>> grouped = new ArrayList<>();
		List<MatchMaxSimilarity> sortedMatches = getSortedMatches();
		while (!sortedMatches.isEmpty()) {
			List<Match> group = new ArrayList<>();
			//Si prende il primo elemento, si rimuove dalla lista e lo si aggiunge al nuovo gruppo
			Match m = sortedMatches.get(0).getMatch();
			sortedMatches.remove(0);
			group.add(m);
			boolean inserted = false;
			do {
				inserted = false;
				int i = 0;
				while (i < sortedMatches.size()) {
					//Per ogni match
					//Se appartiene al gruppo, lo si aggiunge al gruppo e lo si rimuove dalla lista
					Match n = sortedMatches.get(i).getMatch();
					if (isInGroup(group, n)) {
						group.add(n);
						sortedMatches.remove(i);
						inserted = true;
					} else {
						i++;
					}
				}
				//Si continuano ad aggiungere match nel gruppo fino a quando questo non è più possibile
			} while (inserted);

			grouped.add(group);
		}
		
		return grouped;
	}
	
	private List<MatchMaxSimilarity> getSortedMatches() {
		List<Match> allMatches = new ArrayList<Match>(matches.keySet());
		List<MatchMaxSimilarity> mmsList = new ArrayList<>();
		for (Match m: allMatches) {
			List<DistanceMeasure> distances = matches.get(m);
			double maxSimilarity = Collections.max(distances).getDistance();
			mmsList.add(new MatchMaxSimilarity(m, maxSimilarity));
		}
		Collections.sort(mmsList);
		Collections.reverse(mmsList);
		return mmsList;
	}
	
	private boolean isInGroup(List<Match> group, Match m) {
		for (Match g: group) {
			if (g.contains(m) 
					|| m.contains(g)) {
				return true;
			}
		}
		return false;
	}
	
	private class MatchMaxSimilarity implements Comparable<MatchMaxSimilarity> {
		private Match match;
		private double maxSimilarity;
		
		public MatchMaxSimilarity(Match match, double maxSimilarity) {
			this.match = match;
			this.maxSimilarity = maxSimilarity;
		}
		
		public Match getMatch() {
			return this.match;
		}
		public double getMaxSimilarity() {
			return this.maxSimilarity;
		}
		
		public String toString() {
			return match + " Max similarity: " + maxSimilarity;
		}
		
		@Override
		public int compareTo(MatchMaxSimilarity other) {
			if (maxSimilarity > other.maxSimilarity) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
