package test.convrecsys;

import test.TestAddedElement;

public class CRSRecommendation {
	private String[] preferences;
	private String recommendation;
	private double recommendationFeedback;
	private CRSEntity[] actualPreferenceEntities;
	private CRSEntity[] actualRecommendedEntities;
	private boolean skipped;
	private TestAddedElement[] recognizedEntities;
	private String[] pagerankResults;
	private String[] intents;
	public CRSRecommendation(String[] preferences, String recommendation, double recommendationFeedback,
			CRSEntity[] actualPreferenceEntities, CRSEntity[] actualRecommendedEntities,
			TestAddedElement[] recognizedEntities, String[] pagerankResults, boolean skipped) {
		super();
		this.preferences = preferences;
		this.recommendation = recommendation;
		this.recommendationFeedback = recommendationFeedback;
		this.actualPreferenceEntities = actualPreferenceEntities;
		this.actualRecommendedEntities = actualRecommendedEntities;
		this.recognizedEntities = recognizedEntities;
		this.pagerankResults = pagerankResults;
		this.skipped = skipped;
	}
	public String[] getPreferences() {
		return preferences;
	}
	public String getRecommendation() {
		return recommendation;
	}
	public double getRecommendationFeedback() {
		return recommendationFeedback;
	}
	public CRSEntity[] getActualPreferenceEntities() {
		return actualPreferenceEntities;
	}
	public CRSEntity[] getActualRecommendedEntities() {
		return actualRecommendedEntities;
	}
	public TestAddedElement[] getRecognizedEntities() {
		return recognizedEntities;
	}
	public String[] getPagerankResults() {
		return pagerankResults;
	}
	public String[] getIntents() {
		return intents;
	}
	public void setIntents(String[] intents) {
		this.intents = intents;
	}
	public boolean isSkipped() {
		return skipped;
	}

}
