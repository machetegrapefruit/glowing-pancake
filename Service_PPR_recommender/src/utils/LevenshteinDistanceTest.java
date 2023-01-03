package utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import database.AccessRecsysDB;

public class LevenshteinDistanceTest {
	private static AccessRecsysDB dbAccess = new AccessRecsysDB();
	private static String[] sentences = {
			"I really like ghostbusters",
			"I really like ghostbusters II",
			"I really like ghostbusters ii",
			"I really like ghostbusters 2",
			"I really like ghostbusters and ghostbusters II",
			"I really like ghostbusters II and ghostbusters",
			"I really like The Blues Brothers ghostbusters II and ghostbusters",
			"I really like Tarantino",
			"blues broters",
			"I mean both as a producer and starring",
			"I really like ghostbusters II The Blues Brothers and ghostbusters",
	};
	
    public static void main(String[] args) throws Exception {
    	TreeMap<String, String> vertexUriAndNameSet = dbAccess.selectUriAndNameFromVertexTrailerSelection();
    	
    	List<String> uris = new ArrayList<>();
    	List<String> labels = new ArrayList<>();
    	for (Entry<String, String> entry: vertexUriAndNameSet.entrySet()) {
    		uris.add(entry.getKey());
    		labels.add(entry.getValue());
    	}
    	
    	for (String sentence: sentences) {
    		printMatches(sentence, labels);
    	}
    }
    
	private static void printMatches(String sentence, List<String> possibleValues) {
		long start = System.currentTimeMillis();
		System.out.println("\nMatches for " + sentence);
		MatchMap matches = LevenshteinDistanceCalculator.findMatches(sentence, possibleValues, 0.75);
		for (Entry<Match, List<DistanceMeasure>> entry: matches.getMatches().entrySet()) {
			System.out.println("Match is " + entry.getKey().getMatchString() + ": " + entry.getValue());
		}
		List<List<Match>> groups = matches.getGroupedMatches();
		long stop = System.currentTimeMillis();
		String time = new SimpleDateFormat("mm:ss:SSS").format(new Date(stop - start));
		System.out.println("Groups: " + groups);
		System.out.println("Time is " + time);
	}
}
