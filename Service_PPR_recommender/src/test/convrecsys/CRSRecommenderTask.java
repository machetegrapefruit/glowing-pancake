package test.convrecsys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import dialog.PendingEvaluation;
import dialog.PendingEvaluation.PendingEvaluationType;
import dialog.Preference;
import dialog.SentimentAnalyzerConnector;
import dialog.SentimentObject;
import dialog.functions.AddPreferenceFunction;
import dialog.functions.AddPreferenceFunction.AddPreferenceResponse;
import entity.Pair;
import functions.EntityService;
import functions.ProfileService;
import functions.PropertyService;
import test.TestAddedElement;
import test.TestPagerank;
import utils.Alias;
import utils.FileUtils;
import utils.MatchedElement;

public class CRSRecommenderTask implements Runnable {
	private CRSConversation conversation;
	private ConcurrentLinkedQueue<CRSRecommendationConversation> recQueue;
	private ConcurrentLinkedQueue<String> userIDQueue;
	private EntityService es;
	private ProfileService ps;
	private PropertyService pr;
	private String userID;
	private String problemsPath;
	private boolean forcedEntities;
	private boolean forcedSentiment;
	
	public CRSRecommenderTask(CRSConversation conversation, ConcurrentLinkedQueue<CRSRecommendationConversation> recQueue,
			ConcurrentLinkedQueue<String> userIDQueue, String problemsPath, String type) {
		super();
		this.conversation = conversation;
		this.recQueue = recQueue;
		this.userIDQueue = userIDQueue;
		this.es = new EntityService();
		this.ps = new ProfileService();
		this.pr = new PropertyService();
		this.problemsPath = problemsPath;
		this.forcedEntities = false;
		this.forcedSentiment = false;
		if (type.equalsIgnoreCase("forcedEntities")) {
			this.forcedEntities = true;
		} else if (type.equalsIgnoreCase("forcedSentiment")) {
			this.forcedSentiment = true;
		}
	}
	
	private CRSRecommendationTurn analyzeTurn(CRSTurn turn) {
		try {
			List<CRSRecommendation> results = new ArrayList<>();
			System.out.print("Removing user preferences...");
			boolean deleted = es.deleteAllRatedEntities(this.userID);
			ps.deleteUserProfile(this.userID);
			pr.deleteAllRatedProperties(this.userID);
			if (deleted) {
				System.out.println("done");
			} else {
				System.out.println("Could not delete user preferences!");
			}
			
			List<CRSMessage> userPreferences = new ArrayList<>();
			CRSMessage recommendation = null;
			double feedback = -1;
			for (CRSMessage message: turn.getMessages()) {
				//Per ogni messaggio dell'utente
				//Valuto se è una preferenza
				if (message.getAgent().equals("user") && message.getEntities().length > 0) {
					userPreferences.add(message);
				}
				//Se è una raccomandazione
				if (message.getAgent().equals("bot") && message.getEntities().length > 0) {
					recommendation = message;
				}
				//Se è un messaggio con feedback non nullo
				if (message.getAgent().equals("user") && message.getFeedback() > -1) {
					feedback = message.getFeedback();
					String[] preferenceStrings = new String[userPreferences.size()];
					List<CRSEntity> entitiesInDataset = new ArrayList<>();
					for (int i = 0; i < userPreferences.size(); i++) {
						preferenceStrings[i] = userPreferences.get(i).getUtterance();
						entitiesInDataset.addAll(getEntitiesInDataset(userPreferences.get(i).getEntities()));
					}
					List<CRSEntity> recommendationsInDataset = getEntitiesInDataset(recommendation.getEntities());
					System.out.println("Preferences: " + Arrays.toString(preferenceStrings) + ", recommendation is " + recommendation.getUtterance());
					results.add(addPreferencesAndGetRecommendations(entitiesInDataset, 
							recommendation.getEntities(), 
							userPreferences.toArray(new CRSMessage[userPreferences.size()]), 
							preferenceStrings, 
							recommendation, 
							recommendationsInDataset, 
							feedback
							));
					userPreferences.clear();
				}
			}
			deleted = es.deleteAllRatedEntities(this.userID);
			ps.deleteUserProfile(this.userID);
			pr.deleteAllRatedProperties(this.userID);
			if (deleted) {
				System.out.println("done");
			} else {
				System.out.println("Could not delete user preferences!");
			}
			
			return new CRSRecommendationTurn(results.toArray(new CRSRecommendation[results.size()]));
		} catch (Exception e) {
			e.printStackTrace();
			boolean deleted = es.deleteAllRatedEntities(this.userID);
			ps.deleteUserProfile(this.userID);
			pr.deleteAllRatedProperties(this.userID);
			if (deleted) {
				System.out.println("done");
			} else {
				System.out.println("Could not delete user preferences!");
			}
		}	
		return null;
	}

	@Override
	public void run() {
		this.userID = userIDQueue.poll();
		List<CRSRecommendationTurn> recTurns = new ArrayList<>();
		for (CRSTurn turn: conversation.getTurns()) {
			recTurns.add(analyzeTurn(turn));
		}
		CRSRecommendationConversation resultConversation = new CRSRecommendationConversation(
				conversation.getId(),
				recTurns.toArray(new CRSRecommendationTurn[recTurns.size()])
			);
		recQueue.offer(resultConversation);
		userIDQueue.offer(this.userID);
	}
	
	private CRSRecommendation addPreferencesAndGetRecommendations(List<CRSEntity> actualEntities, 
			CRSEntity[] actualRecommendations,
			CRSMessage[] preferenceMessages,
			String[] preferenceStrings,
			CRSMessage recommendation,
			List<CRSEntity> recommendationsInDataset,
			double feedback) throws Exception {
		if (recommendationsInDataset.size() > 0 && actualEntities.size() > 0) {
			//Se c'è almeno un film raccomandato nel database, si inseriscono le preferenze e si esegue il pagerank
			if (this.forcedSentiment) {
				return addDialogPreferencesAndGetRecommendationsFS(
						actualEntities, actualRecommendations, 
						preferenceMessages, preferenceStrings, 
						recommendation, recommendationsInDataset, 
						feedback);
			} else if (this.forcedEntities) {
				return addDialogPreferencesAndGetRecommendationsFE(
						actualEntities, actualRecommendations, 
						preferenceMessages, preferenceStrings, 
						recommendation, recommendationsInDataset, 
						feedback);
			} else {
				//Upper bound
				List<String> pagerank = addPreferencesAndGetRecommendationsUB(actualEntities, recommendation.getEntities());
				return new CRSRecommendation(
								preferenceStrings, 
								recommendation.getUtterance(), 
								feedback,
								actualEntities.toArray(new CRSEntity[actualEntities.size()]),
								recommendationsInDataset.toArray(new CRSEntity[recommendationsInDataset.size()]),
								null,
								pagerank.toArray(new String[pagerank.size()]),
								false
							);
			}
		} else {
			//Se nessuno dei film raccomandati è nel database, si salta l'analisi
			return new CRSRecommendation(
							preferenceStrings, 
							recommendation.getUtterance(), 
							feedback,
							actualEntities.toArray(new CRSEntity[actualEntities.size()]),
							null,
							null,
							null,
							true
						);
		}
	}
	
	//Upper bound
	private List<String> addPreferencesAndGetRecommendationsUB(List<CRSEntity> entities, CRSEntity[] recommendations) throws Exception {
		EntityService es = new EntityService();
		PropertyService ps = new PropertyService();
		for (CRSEntity entity: entities) {
			if (es.isEntity(entity.getId())) {
				es.addEntityPreference(this.userID, entity.getId(), entity.getRating(), "user");
			} else if (ps.isPropertyObject(entity.getId())) {
				//Siccome ai fini della raccomandazione il tipo di proprietà non è rilevante, prendo il primo dalla lista
				List<String> propertyTypes = ps.getPropertyTypes(entity.getId()).get(entity.getId());
				if (propertyTypes != null && propertyTypes.size() > 0) {
					ps.addPropertyPreference(this.userID, entity.getId(), propertyTypes.get(0), entity.getRating(), "user");
				} else {
					//Inserisco un tipo di proprietà a caso per evitare di restituire null
					String log = "Property " + entity.getId() + " has no property types! Defaulting to P161";
					System.out.println(log);
					FileUtils.appendToFile(problemsPath, log);
					ps.addPropertyPreference(this.userID, entity.getId(), "P161", entity.getRating(), "user");
				}
			}
		}
		
		List<String> pagerankResults = TestPagerank.createGraphAndRunPageRankTest(this.userID);
		System.out.println("Adding preference for: " + entities + "\nPagerank results are " + pagerankResults);
		return pagerankResults;
	}
	
	//Modalità forced sentiment/test entity extractor
	private CRSRecommendation addDialogPreferencesAndGetRecommendationsFS(List<CRSEntity> actualEntities, 
			CRSEntity[] actualRecommendations,
			CRSMessage[] preferenceMessages,
			String[] preferenceStrings,
			CRSMessage recommendation,
			List<CRSEntity> recommendationsInDataset,
			double feedback) throws Exception {
		List<TestAddedElement> result = new ArrayList<>();
		AddPreferenceFunction apf = new AddPreferenceFunction();
		apf.setAutoAdd(false);
		int addedCount = 0;
		for (CRSMessage preference: preferenceMessages) {
			AddPreferenceResponse apr = apf.addPreferences(preference.getUtterance(), this.userID, null, null, null);
			List<MatchedElement> added = apr.getAddedEntities();
			for (MatchedElement a: added) {
				String uri = a.getElement().getURI();
				CRSEntity entity = getAsCRSEntity(uri, actualEntities);
				if (entity != null) {
					int rating = entity.getRating();
					es.addEntityPreference(userID, a.getElement().getURI(), rating, "user");
					addedCount++;
					result.add(new TestAddedElement(a.getElement(), rating, true));
				} else {
					//Segnalo comunque il riconoscimento
					result.add(new TestAddedElement(a.getElement(), -1, true));
				}
			}
			List<MatchedElement> addedProps = apr.getAddedProperties();
			for (MatchedElement a: addedProps) {
				String uri = a.getElement().getURI();
				CRSEntity entity = getAsCRSEntity(uri, actualEntities);
				if (entity != null) {
					int rating = entity.getRating();
					//Ricavo l'unico propertyType possibile per la proprietà
					String propertyType = pr.getPropertyTypes(entity.getId()).get(entity.getId()).get(0);
					pr.addPropertyPreference(userID, a.getElement().getURI(), propertyType, rating, "user");
					result.add(new TestAddedElement(a.getElement(), rating, true));
					addedCount++;
				} else {
					//Segnalo comunque il riconoscimento
					result.add(new TestAddedElement(a.getElement(), -1, true));
				}
			}
			
			Preference p = apr.getPreference();
			List<Pair<Integer, PendingEvaluation>> disambiguations = p.getPendingEvaluations();
			//Per ogni disambiguazione in corso, seleziona soltanto l'opzione contenuta tra le vere entità della frase
			for (Pair<Integer, PendingEvaluation> disambiguation: disambiguations) {
				if (disambiguation.value.getType() == PendingEvaluationType.NAME_DISAMBIGUATION) {
					for (CRSEntity entity: preference.getEntities()) {
						if (contains(actualEntities, entity) && isInAliasList(entity.getId(), disambiguation.value.getPossibleValues())) {
							System.out.println("Adding preference for entity " + entity);
							addPreference(entity.getId(), entity.getRating());
							result.add(new TestAddedElement(new Alias(entity.getId(), entity.getLabel()), entity.getRating(), false));
							addedCount++;
						}
					}
				} else if (disambiguation.value.getType() == PendingEvaluationType.PROPERTY_TYPE_DISAMBIGUATION) {
					for (CRSEntity entity: preference.getEntities()) {
						//Aggiungo la preferenza soltanto nel caso in cui la proprietà su cui si sta facendo riferimento è stata riconosciuta correttamente
						if (contains(actualEntities, entity) && disambiguation.value.getElementName().getURI().equals(entity.getId())) {
							addPreference(disambiguation.value.getElementName().getURI(), entity.getRating());
							result.add(new TestAddedElement(new Alias(entity.getId(), entity.getLabel()), entity.getRating(), false));
							addedCount++;
						}
					}
				}
			}
		}
		
		CRSRecommendation crsRecommendation = null;
		if (addedCount > 0) {
			List<String> pagerank = TestPagerank.createGraphAndRunPageRankTest(this.userID);
			crsRecommendation = new CRSRecommendation(
					preferenceStrings, 
					recommendation.getUtterance(), 
					feedback,
					actualEntities.toArray(new CRSEntity[actualEntities.size()]),
					recommendationsInDataset.toArray(new CRSEntity[recommendationsInDataset.size()]),
					result.toArray(new TestAddedElement[result.size()]),
					pagerank.toArray(new String[pagerank.size()]),
					false
				);
		} else {
			crsRecommendation = new CRSRecommendation(
					preferenceStrings, 
					recommendation.getUtterance(), 
					feedback,
					actualEntities.toArray(new CRSEntity[actualEntities.size()]),
					recommendationsInDataset.toArray(new CRSEntity[recommendationsInDataset.size()]),
					null,
					null,
					true
			);
		}
		
		return crsRecommendation;
	}
	
	private void addPreference(String id, int rating) {
		if (es.isEntity(id)) {
			es.addEntityPreference(this.userID, id, rating, "user");
		} else if (pr.isPropertyObject(id)) {
			//Ricavo l'unico propertyType possibile per la proprietà
			String propertyType = pr.getPropertyTypes(id).get(id).get(0);
			pr.addPropertyPreference(userID, id, propertyType, rating, "user");
		}
	}
	
	//Modalità forced entities/test sentiment extractor
	private CRSRecommendation addDialogPreferencesAndGetRecommendationsFE(List<CRSEntity> actualEntities, 
			CRSEntity[] actualRecommendations,
			CRSMessage[] preferenceMessages,
			String[] preferenceStrings,
			CRSMessage recommendation,
			List<CRSEntity> recommendationsInDataset,
			double feedback) throws Exception {
		List<TestAddedElement> result = new ArrayList<>();
		AddPreferenceFunction apf = new AddPreferenceFunction();
		apf.setAutoAdd(false);
		int addedCount = 0;
		SentimentAnalyzerConnector sac = new SentimentAnalyzerConnector(true, true, true);
		
		for (CRSMessage preference: preferenceMessages) {
			//Aggiungo in toAdd solo le entità contenute nel dataset
			List<CRSEntity> toAdd = new ArrayList<>();
			for (CRSEntity entity: preference.getEntities()) {
				if (contains(actualEntities, entity)) {
					toAdd.add(entity);
				}
			}
			List<SentimentObject> sentimentObjects = sac.getSentiment(preference.getUtterance());
			for (SentimentObject so: sentimentObjects) {
				List<Alias> aliases = so.getAliases();
				int i = 0;
				while (i < toAdd.size()) {
					CRSEntity current = toAdd.get(i);
					if (isInAliasList(current.getId(), aliases)) {
						int rating = getRatingFromSentiment(so.getSentiment());
						System.out.println("Adding " + current.getId() + " with rating " + rating);
						addPreference(current.getId(), rating);
						result.add(new TestAddedElement(new Alias(current.getId(), current.getLabel()), rating, true));
						addedCount++;
						toAdd.remove(i);
					} else {
						i++;
					}
				}
			}
			
			for (CRSEntity remaining: toAdd) {
				addPreference(remaining.getId(), remaining.getRating());
				System.out.println("Adding forced sentiment for entity " + remaining.getId());
				addedCount++;
				result.add(new TestAddedElement(new Alias(remaining.getId(), remaining.getLabel()), remaining.getRating(), false));
			}
			
		}
		
		CRSRecommendation crsRecommendation = null;
		if (addedCount > 0) {
			List<String> pagerank = TestPagerank.createGraphAndRunPageRankTest(this.userID);
			crsRecommendation = new CRSRecommendation(
					preferenceStrings, 
					recommendation.getUtterance(), 
					feedback,
					actualEntities.toArray(new CRSEntity[actualEntities.size()]),
					recommendationsInDataset.toArray(new CRSEntity[recommendationsInDataset.size()]),
					result.toArray(new TestAddedElement[result.size()]),
					pagerank.toArray(new String[pagerank.size()]),
					false
				);
		} else {
			crsRecommendation = new CRSRecommendation(
					preferenceStrings, 
					recommendation.getUtterance(), 
					feedback,
					actualEntities.toArray(new CRSEntity[actualEntities.size()]),
					null,
					null,
					null,
					true
			);
		}
		
		return crsRecommendation;
	}
	
	public int getNumEntitiesInDataset(CRSEntity[] entityList) {
		int count = 0;
		for (CRSEntity entity: entityList) {
			if (entity.isInDataset()) {
				count++;
			}
		}
		return count;
	}
	
	public List<CRSEntity> getEntitiesInDataset(CRSEntity[] entityList) {
		List<CRSEntity> result = new ArrayList<>();
		for (CRSEntity entity: entityList) {
			if (entity.isInDataset()) {
				result.add(entity);
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
	
	private boolean contains(List<CRSEntity> list, CRSEntity entity) {
		for (CRSEntity e: list) {
			if (e.getId().equals(entity.getId())) {
				return true;
			}
		}
		return false;
	}
	
	private CRSEntity getAsCRSEntity(String entity, List<CRSEntity> entitiesInDataset) {
		for (CRSEntity e: entitiesInDataset) {
			if (entity.equals(e.getId())) {
				return e;
			}
		}
		return null;
	}
	
	private int getRatingFromSentiment(int sentiment) {
		int rating = 0;
		if (sentiment > 1) {
			rating = 1;
		}
		return rating;
	}
	
}
