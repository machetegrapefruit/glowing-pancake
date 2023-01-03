package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import entity.Rating;
import entity.RequestStruct;
import graph.AdaptiveSelectionUserItemPropertyDB;

public class TestPagerank {
	public static List<String> createGraphAndRunPageRankTest(String user_id) throws Exception {
		List<String> recommended = new ArrayList<String>();
		long meanTimeElapsed = 0, startTime;
		startTime = System.nanoTime();

		AdaptiveSelectionUserItemPropertyDB graph = new AdaptiveSelectionUserItemPropertyDB(user_id);

		meanTimeElapsed += (System.nanoTime() - startTime);
		double second = (double) meanTimeElapsed / 1000000000.0;
		System.out.println("Graph create in: " + second + "''");
		meanTimeElapsed = 0;
		startTime = System.nanoTime();
		Map<String, Set<Rating>> ratings = graph.runPageRankForSingleUser(user_id, new RequestStruct(0.85));
		meanTimeElapsed += (System.nanoTime() - startTime);
		second = (double) meanTimeElapsed / 1000000000.0;
		// currLogger.info("\nRuntime PageRank: " + second + "''");

		System.out.println("PageRank done in " + second + "'' - Stored scores into database in progress..");
		Set<Rating> rating = ratings.get(user_id);
		Iterator<Rating> setIterator = rating.iterator();
		
		int i = 0;
		while (i < 100 && setIterator.hasNext()) {
			recommended.add(setIterator.next().getItemID());
			i++;
		}
		
		return recommended;
	}
}
