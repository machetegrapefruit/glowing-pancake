package test.babi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.JsonObject;

import dialog.PendingEvaluation;
import dialog.PendingEvaluation.PendingEvaluationType;
import dialog.Preference;
import dialog.SentimentAnalyzerConnector;
import dialog.SentimentObject;
import dialog.functions.AddPreferenceFunction;
import dialog.functions.AddPreferenceFunction.AddPreferenceResponse;
import entity.Pair;
import entity.Rating;
import entity.RequestStruct;
import functions.EntityService;
import functions.ProfileService;
import functions.PropertyService;
import graph.AdaptiveSelectionUserItemPropertyDB;
import test.DialogFlowConnector;
import test.Entity;
import test.TestAddedElement;
import utils.Alias;
import utils.MatchedElement;

public class BabiDialogRecommenderTask implements Runnable {
	private BabiSentence sentence;
	private ConcurrentLinkedQueue<BabiDialogRecommendation> recQueue;
	private ConcurrentLinkedQueue<String> userIDQueue;
	private String userID;
	private boolean forceSentiment = false;
	private boolean forceEntities = false;
	
	public BabiDialogRecommenderTask(ConcurrentLinkedQueue<BabiDialogRecommendation> recQueue, 
			ConcurrentLinkedQueue<String> userIDQueue,
			BabiSentence sentence,
			boolean forceSentiment,
			boolean forceEntities) {
		this.sentence = sentence;
		this.recQueue = recQueue;
		this.userIDQueue = userIDQueue;
		this.forceSentiment = forceSentiment;
		this.forceEntities = forceEntities;
	}
	
	@Override
	public void run() {
		this.userID = userIDQueue.poll();
		System.out.println("Sentence is " + sentence);
		getRecommendation(this.userID, recQueue, sentence);
	}
	
	private void getRecommendation(String userID, 
			ConcurrentLinkedQueue<BabiDialogRecommendation> recQueue, 
			BabiSentence sentence) {
		EntityService es = new EntityService();
		ProfileService ps = new ProfileService();
		PropertyService pr = new PropertyService();
		System.out.print("Removing user preferences...");
		boolean deleted = es.deleteAllRatedEntities(this.userID);
		ps.deleteUserProfile(this.userID);
		pr.deleteAllRatedProperties(this.userID);
		if (deleted) {
			System.out.println("done");
		} else {
			System.out.println("Could not delete user preferences!");
		}
		
		try {
			JsonObject prefIntentJson = DialogFlowConnector.processMessage(sentence.getPreference(), this.userID);
			JsonObject recIntentJson = DialogFlowConnector.processMessage(this.sentence.getRequestRecommendation(), this.userID);
			String prefIntent = "";
			String recIntent = "";
			try {
				prefIntent = prefIntentJson.getAsJsonObject("result").getAsJsonObject("metadata").get("intentName").getAsString();
			} catch (Exception e) {
				prefIntent = prefIntentJson.getAsJsonObject("result").get("action").getAsString();
			}
			try {
				recIntent = recIntentJson.getAsJsonObject("result").getAsJsonObject("metadata").get("intentName").getAsString();
			} catch (ClassCastException e) {
				recIntent = recIntentJson.getAsJsonObject("result").get("action").getAsString();
			}
			List<TestAddedElement> added;
			if (!forceEntities) {
				added = addPreference(sentence.getPreference(), this.userID);
			} else {
				added = addPreferenceWithForcedEntities(sentence.getPreference(), this.userID, sentence.getEntities());
			}

			System.out.println("Recognized elements: " + added);
			
			List<String> recommended = new ArrayList<>();
			if (added.size() >= 1) {
				recommended = createGraphAndRunPageRankTest(this.userID);
			}
			
			BabiDialogRecommendation recommendation = new BabiDialogRecommendation(sentence.getId(),
					sentence.getPreference(), 
					sentence.getRecommendedMovie(), 
					sentence.getRecMovieUri(), 
					prefIntent,
					recIntent,
					sentence.getEntities(),
					added.toArray(new TestAddedElement[added.size()]),
					recommended.toArray(new String[recommended.size()]));
			recQueue.offer(recommendation);
			System.out.println("Recommended entities are: " + recommended);
			System.out.println("Is recommended entity in list? " + recommended.contains(sentence.getRecMovieUri()));
			System.out.print("Removing user preferences...");
			deleted = es.deleteAllRatedEntities(this.userID);
			ps.deleteUserProfile(this.userID);
			pr.deleteAllRatedProperties(this.userID);
			if (deleted) {
				System.out.println("done");
			} else {
				System.out.println("Could not delete user preferences!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userIDQueue.offer(this.userID);
	}
	
	private List<TestAddedElement> addPreferenceWithForcedEntities(String preference, String userID, Entity[] entities) {
		List<TestAddedElement> result = new ArrayList<>();
		EntityService es = new EntityService();
		PropertyService ps = new PropertyService();
		SentimentAnalyzerConnector sac = new SentimentAnalyzerConnector(true, true, true);
		List<Entity> toAdd = new ArrayList<>(Arrays.asList(entities));
		try {
			List<SentimentObject> sentimentObjects = sac.getSentiment(preference);
			for (SentimentObject so: sentimentObjects) {
				List<Alias> aliases = so.getAliases();
				int i = 0;
				while (i < toAdd.size()) {
					Entity current = toAdd.get(i);
					if (isInAliasList(current.getUri(), aliases)) {
						int rating = getRatingFromSentiment(so.getSentiment());
						System.out.println("Adding " + current.getUri() + " with rating " + rating);
						es.addEntityPreference(userID, current.getUri(), rating, "user");
						result.add(new TestAddedElement(new Alias(current.getUri(), current.getLabel()), rating, true));
						toAdd.remove(i);
					} else {
						i++;
					}
				}
			}
			
			for (Entity remaining: toAdd) {
				es.addEntityPreference(userID, remaining.getUri(), 1, "user");
				System.out.println("Adding forced sentiment 1 for entity " + remaining.getUri());
				result.add(new TestAddedElement(new Alias(remaining.getUri(), remaining.getLabel()), 1, false));
			}
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	private List<TestAddedElement> addPreference(String preference, String userID) {
		List<TestAddedElement> result = new ArrayList<>();
		EntityService es = new EntityService();
		PropertyService ps = new PropertyService();
		AddPreferenceFunction apf = new AddPreferenceFunction();
		apf.setAutoAdd(false);
		AddPreferenceResponse apr = apf.addPreferences(sentence.getPreference(), this.userID, null, null, null);
		List<MatchedElement> added = apr.getAddedEntities();
		for (MatchedElement a: added) {
			int rating = 1;
			if (!forceSentiment) {
				rating = a.getRating();
			}
			es.addEntityPreference(userID, a.getElement().getURI(), rating, "user");
			result.add(new TestAddedElement(a.getElement(), rating, true));
		}
		List<MatchedElement> addedProps = apr.getAddedProperties();
		for (MatchedElement a: addedProps) {
			int rating = 1;
			if (!forceSentiment) {
				rating = a.getRating();
			}
			//Ricavo l'unico propertyType possibile per la proprietà
			String propertyType = ps.getPropertyTypes(a.getElement().getURI()).get(a.getElement().getURI()).get(0);
			ps.addPropertyPreference(userID, a.getElement().getURI(), propertyType, rating, "user");
			result.add(new TestAddedElement(a.getElement(), rating, true));
		}
		Preference p = apr.getPreference();
		List<Pair<Integer, PendingEvaluation>> disambiguations = p.getPendingEvaluations();
		//Per ogni disambiguazione in corso, seleziona soltanto l'opzione contenuta tra le vere entità della frase
		for (Pair<Integer, PendingEvaluation> disambiguation: disambiguations) {
			int rating = 1;
			if (!forceSentiment) {
				rating = disambiguation.value.getRating();
			}
			if (disambiguation.value.getType() == PendingEvaluationType.NAME_DISAMBIGUATION) {
				for (Entity entity: sentence.getEntities()) {
					if (isInAliasList(entity.getUri(), disambiguation.value.getPossibleValues())) {
						System.out.println("Adding preference for entity " + entity);
						es.addEntityPreference(this.userID, entity.getUri(), rating, "user");
						result.add(new TestAddedElement(new Alias(entity.getUri(), entity.getLabel()), rating, false));
					}
				}
			}
		}
		
		return result;
	}
	
	private boolean isInAliasList(String entity, List<Alias> disambiguationOptions) {
		for (Alias a: disambiguationOptions) {
			if (a.getURI().equals(entity)) {
				return true;
			}
		}
		return false;
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
	
	private int getRatingFromSentiment(int sentiment) {
		int rating = 0;
		if (sentiment > 1) {
			rating = 1;
		}
		return rating;
	}
	
	public static void main(String[] args) {
		Entity[] entities = new Entity[3];
		entities[0] = new Entity("Q683663", "Twister");
		entities[1] = (new Entity("Q106428", "Apollo 13"));
		entities[2] = (new Entity("Q134773", "Forrest Gump"));
		BabiSentence sentence = new BabiSentence(0, "The Lion King, Twister, Apollo 13, Aladdin, Four Weddings and a Funeral, Forrest Gump, and Sabrina are movies I like", " Would you recommend a movie?", "It Could Happen to You", "Q222868", entities);
		ConcurrentLinkedQueue<BabiDialogRecommendation> queue = new  ConcurrentLinkedQueue<BabiDialogRecommendation>();
		ConcurrentLinkedQueue<String> user = new ConcurrentLinkedQueue<>();
		user.offer("1234567890");
		new BabiDialogRecommenderTask(queue, user, sentence, false, true).run();
		System.out.println(Arrays.toString(queue.poll().getPagerankResults()));
	}
}
