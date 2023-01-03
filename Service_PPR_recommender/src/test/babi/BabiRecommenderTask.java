package test.babi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import entity.Rating;
import entity.RequestStruct;
import functions.EntityService;
import graph.AdaptiveSelectionUserItemPropertyDB;
import test.Entity;

public class BabiRecommenderTask implements Runnable {
	private BabiSentence sentence;
	private ConcurrentLinkedQueue<BabiRecommendation> recQueue;
	private ConcurrentLinkedQueue<String> userIDQueue;
	private String userID;

	public BabiRecommenderTask(ConcurrentLinkedQueue<BabiRecommendation> recQueue, 
			ConcurrentLinkedQueue<String> userIDQueue,
			BabiSentence sentence) {
		this.sentence = sentence;
		this.recQueue = recQueue;
		this.userIDQueue = userIDQueue;
	}
	
	@Override
	public void run() {
		this.userID = userIDQueue.poll();
		System.out.println("Sentence is " + sentence);
		try {
			EntityService es = new EntityService();
			System.out.print("Removing user preferences...");
			boolean deleted = es.deleteAllRatedEntities(this.userID);
			if (deleted) {
				System.out.println("done");
			} else {
				System.out.println("Could not delete user preferences!");
			}
			for (Entity entity: sentence.getEntities()) {
				System.out.println("Adding preference for entity " + entity.getUri());
				es.addEntityPreference(this.userID, entity.getUri(), 1, "user");
			}
			
			List<String> recommended = createGraphAndRunPageRankTest(this.userID);
			BabiRecommendation recommendation = new BabiRecommendation(sentence.getId(), sentence.getPreference(), 
					sentence.getRecommendedMovie(), 
					sentence.getRecMovieUri(), 
					recommended.toArray(new String[recommended.size()]));
			recQueue.offer(recommendation);
			System.out.println("Recommended entities are: " + recommended);
			System.out.println("Is recommended entity in list? " + recommended.contains(sentence.getRecMovieUri()));
			System.out.print("Removing user preferences...");
			deleted = es.deleteAllRatedEntities(this.userID);
			if (deleted) {
				System.out.println("done");
			} else {
				System.out.println("Could not delete user preferences!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		userIDQueue.offer(this.userID);
	}
	
	private List<String> createGraphAndRunPageRankTest(String user_id) throws Exception {
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
