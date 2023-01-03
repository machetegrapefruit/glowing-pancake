package test.babi;

import test.Entity;
import test.TestAddedElement;

public class BabiDialogRecommendation {	
	private int id;
	private String sentence;
	private String recommendedMovie;
	private String recMovieUri;
	private String preferenceIntent;
	private String requestRecommendationIntent;
	private Entity[] actualEntities;
	private TestAddedElement[] recognizedEntities;
	private String[] pagerankResults;
	
	public BabiDialogRecommendation(int id,
			String sentence, 
			String recommendedMovie, 
			String recMovieUri, 
			String preferenceIntent,
			String requestRecommendationIntent,
			Entity[] actualEntities,
			TestAddedElement[] recognizedEntities, 
			String[] pagerankResults) {
		super();
		this.id = id;
		this.sentence = sentence;
		this.recommendedMovie = recommendedMovie;
		this.recMovieUri = recMovieUri;
		this.preferenceIntent = preferenceIntent;
		this.requestRecommendationIntent = requestRecommendationIntent;
		this.actualEntities = actualEntities;
		this.recognizedEntities = recognizedEntities;
		this.pagerankResults = pagerankResults;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getPreferenceIntent() {
		return preferenceIntent;
	}

	public String getRequestRecommendationIntent() {
		return requestRecommendationIntent;
	}

	public String getSentence() {
		return sentence;
	}
	public String getRecommendedMovie() {
		return recommendedMovie;
	}
	public String getRecMovieUri() {
		return recMovieUri;
	}
	public Entity[] getActualEntities() {
		return this.actualEntities;
	}
	public TestAddedElement[] getRecognizedEntities() {
		return recognizedEntities;
	}
	public String[] getPagerankResults() {
		return pagerankResults;
	}
	
}
