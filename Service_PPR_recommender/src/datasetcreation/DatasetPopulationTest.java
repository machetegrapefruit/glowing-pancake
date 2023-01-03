package datasetcreation;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import dialog.DialogState;
import dialog.FilteredSentimentObject;
import dialog.PendingEvaluation;
import dialog.PendingEvaluation.PendingEvaluationType;
import dialog.Preference;
import dialog.SentimentObject;
import utils.Alias;

public class DatasetPopulationTest {
	public static void main(String[] args) {
		JsonObject defaultResponse = new JsonObject();
		defaultResponse.addProperty("failure", false);
		defaultResponse.addProperty("changedRecommendedEntity", false);
		DialogState state = new DialogState("0");
		DatasetPopulationManager manager = new DatasetPopulationManager();
	
		//Simulo la raccomandazione di un film
		state.setCurrentRecommendedIndex(0);
		defaultResponse.addProperty("changedRecommendedEntity", false);
		manager.processMessage(state, defaultResponse, "0", "0", "recommend me a movie", "request_recommendation", "", false);
		
		//Preferenza film
		state.setCurrentRecommendedIndex(1);
		defaultResponse.addProperty("changedRecommendedEntity", true);
		manager.processMessage(state, defaultResponse, "0", "0", "i love it", "request_recommendation - preference", "", false);
		
		state.setCurrentRecommendedIndex(2);
		defaultResponse.addProperty("changedRecommendedEntity", true);
		manager.processMessage(state, defaultResponse, "0", "0", "i hate it", "request_recommendation - preference", "", false);
		
		//Dettagli
		defaultResponse.addProperty("changedRecommendedEntity", false);
		manager.processMessage(state, defaultResponse, "0", "0", "what are the details", "request_recommendation - details", "", false);
		
		//Critiquing
		List<FilteredSentimentObject> entities = new ArrayList<FilteredSentimentObject>();
		List<FilteredSentimentObject> properties = new ArrayList<FilteredSentimentObject>();
		entities.add(new FilteredSentimentObject(new SentimentObject(0, 0, "test", "test", 0, null, null), null));
		Preference p = new Preference(entities, properties);
		p.addDisambiguation(0, new PendingEvaluation(new Alias("test", "test"), 0, null, PendingEvaluationType.NAME_DISAMBIGUATION));
		state.getPendingPreferenceQueue().add(p);
		manager.processMessage(state, defaultResponse, "0", "0", "I love it, but I hate Tom Cruise", "request_recommendation - yes_but", "", false);
		
		//Disambiguazione
		p.addDisambiguatedEntity(0, new Alias("test", "test"));
		p.skipCurrentDisambiguation();
		state.setCurrentRecommendedIndex(3);
		state.getPendingPreferenceQueue().clear();
		defaultResponse.addProperty("changedRecommendedEntity", true);
		manager.processMessage(state, defaultResponse, "0", "0", "starring", "request_recommendation - critiquing - disambiguation", "", false);
		
		//Critiquing
		entities = new ArrayList<FilteredSentimentObject>();
		properties = new ArrayList<FilteredSentimentObject>();
		entities.add(new FilteredSentimentObject(new SentimentObject(0, 0, "test", "test", 0, null, null), null));
		p = new Preference(entities, properties);
		p.addDisambiguation(0, new PendingEvaluation(new Alias("test", "test"), 0, null, PendingEvaluationType.NAME_DISAMBIGUATION));
		state.getPendingPreferenceQueue().add(p);
		defaultResponse.addProperty("changedRecommendedEntity", false);
		manager.processMessage(state, defaultResponse, "0", "0", "I love it, but I hate Tom Cruise", "request_recommendation - yes_but", "", false);
		
		//Skip
		p.skipCurrentDisambiguation();
		state.setCurrentRecommendedIndex(4);
		state.getPendingPreferenceQueue().clear();
		defaultResponse.addProperty("changedRecommendedEntity", true);
		defaultResponse.addProperty("skippedDisambiguation", false);
		manager.processMessage(state, defaultResponse, "0", "0", "skip this", "request_recommendation - skip", "", false);
		
		//Critiquing
		entities = new ArrayList<FilteredSentimentObject>();
		properties = new ArrayList<FilteredSentimentObject>();
		entities.add(new FilteredSentimentObject(new SentimentObject(0, 0, "test", "test", 0, null, null), null));
		p = new Preference(entities, properties);
		p.addDisambiguation(0, new PendingEvaluation(new Alias("test", "test"), 0, null, PendingEvaluationType.NAME_DISAMBIGUATION));
		state.getPendingPreferenceQueue().add(p);
		defaultResponse.addProperty("changedRecommendedEntity", false);
		manager.processMessage(state, defaultResponse, "0", "0", "I love it, but I hate Tom Hanks", "request_recommendation - yes_but", "", false);
		
		//Task fallito
		defaultResponse.addProperty("changedRecommendedEntity", false);
		manager.processMessage(state, defaultResponse, "0", "0", "test", "fallback", "", false);
		
		//Skip
		p.skipCurrentDisambiguation();
		state.setCurrentRecommendedIndex(5);
		state.getPendingPreferenceQueue().clear();
		defaultResponse.addProperty("changedRecommendedEntity", true);
		defaultResponse.addProperty("skippedDisambiguation", false);
		manager.processMessage(state, defaultResponse, "0", "0", "skip this", "request_recommendation - skip", "", false);
		
		//Critiquing
		entities = new ArrayList<FilteredSentimentObject>();
		properties = new ArrayList<FilteredSentimentObject>();
		entities.add(new FilteredSentimentObject(new SentimentObject(0, 0, "test", "test", 0, null, null), null));
		p = new Preference(entities, properties);
		p.addDisambiguation(0, new PendingEvaluation(new Alias("test", "test"), 0, null, PendingEvaluationType.NAME_DISAMBIGUATION));
		state.getPendingPreferenceQueue().add(p);
		defaultResponse.addProperty("changedRecommendedEntity", false);
		manager.processMessage(state, defaultResponse, "0", "0", "I love it, but I hate Dan Aykroid", "request_recommendation - yes_but", "", false);
		
		//Intent sbagliato
		state.getPendingPreferenceQueue().clear();
		defaultResponse.addProperty("changedRecommendedEntity", false);
		manager.processMessage(state, defaultResponse, "0", "0", "I like Ghostbusters", "preference", "", false);
		
		//Stop raccomandazione
		state.setCurrentRecommendedIndex(-1);
		manager.processMessage(state, defaultResponse, "0", "0", "stop", "request_recommendation - stop", "", false);
		
		//Inserimento preferenza
		entities = new ArrayList<FilteredSentimentObject>();
		properties = new ArrayList<FilteredSentimentObject>();
		entities.add(new FilteredSentimentObject(new SentimentObject(0, 0, "tom cruise", "tom cruise", 0, null, null), null));
		entities.add(new FilteredSentimentObject(new SentimentObject(0, 0, "ghostbusters", "ghostbusters", 0, null, null), null));
		//properties.add(new FilteredSentimentObject(new SentimentObject(0, 0, "director", "director", 0, null, null), null));
		p = new Preference(entities, properties);
		p.addDisambiguation(0, new PendingEvaluation(new Alias("tom cruise", "tom cruise"), 0, null, PendingEvaluationType.NAME_DISAMBIGUATION));
		p.addDisambiguatedEntity(1, new Alias("ghostbusters", "ghostbusters"));
		state.getPendingPreferenceQueue().add(p);
		manager.processMessage(state, defaultResponse, "0", "0", "I love Tom Cruise, and Ghostbusters for the director", "preference", "", false);
		
		//Disambiguazione sul nome
		p.skipCurrentDisambiguation();
		p.addDisambiguation(0, new PendingEvaluation(new Alias("tom cruise", "tom cruise"), 0, null, PendingEvaluationType.PROPERTY_TYPE_DISAMBIGUATION));
		manager.processMessage(state, defaultResponse, "0", "0", "I mean Tom Cruise", "preference - disambiguation", "", false);
		
		//Disambiguazione sul propertyType
		p.skipCurrentDisambiguation();
		p.addDisambiguatedEntity(0, new Alias("tom cruise", "tom cruise"));
		manager.processMessage(state, defaultResponse, "0", "0", "actor", "preference - disambiguation", "", false);
		
		//Conferma associazione
		/*p.confirmAssignment();
		manager.processMessage(state, defaultResponse, 0, "yes", "preference - yes", "");*/
	}
}
