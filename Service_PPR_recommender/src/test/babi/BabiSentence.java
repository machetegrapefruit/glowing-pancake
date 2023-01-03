package test.babi;

import java.util.Arrays;

import test.Entity;

public class BabiSentence {
	private int id;
	private String preference;
	private String requestRecommendation;
	private String recommendedMovie;
	private String recMovieUri;
	private Entity[] entities;
	public BabiSentence(int id, String preference, String requestRecommendation, String recommendedMovie, String recMovieUri,
			Entity[] entities) {
		super();
		this.id = id;
		this.preference = preference;
		this.requestRecommendation = requestRecommendation;
		this.recommendedMovie = recommendedMovie;
		this.recMovieUri = recMovieUri;
		this.entities = entities;
	}
	public int getId() {
		return this.id;
	}
	public String getPreference() {
		return preference;
	}
	public String getRequestRecommendation() {
		return requestRecommendation;
	}
	public String getRecommendedMovie() {
		return recommendedMovie;
	}
	public String getRecMovieUri() {
		return recMovieUri;
	}
	public Entity[] getEntities() {
		return entities;
	}
	
	public String toString() {
		return "preference: " + preference 
				+ ", requestRecommendation: " + requestRecommendation 
				+ ", recommendedMovie: " + recommendedMovie + " (" + recMovieUri + ")"
				+ ", entities: [" + Arrays.toString(entities) + "]";
	}
}
