package test;

public class RecUserData {
	private String userID;
	private int numHits;
	private double hitRate;
	private int numRecLists;
	private int numLikes;
	private int numRecEntities;
	private int numRecEntitiesAlt;
	private double accuracy;
	private double accuracyAlt;
	private double meanAveragePrecision;
	private double meanAveragePrecisionAlt;
	private double nDCG;
	public RecUserData(String userID, int numHits, double hitRate, int numRecLists, int numLikes, int numRecEntities,
			int numRecEntitiesAlt, double accuracy, double accuracyAlt, double meanAveragePrecision,
			double meanAveragePrecisionAlt, double nDCG) {
		super();
		this.userID = userID;
		this.numHits = numHits;
		this.hitRate = hitRate;
		this.numRecLists = numRecLists;
		this.numLikes = numLikes;
		this.numRecEntities = numRecEntities;
		this.numRecEntitiesAlt = numRecEntitiesAlt;
		this.accuracy = accuracy;
		this.accuracyAlt = accuracyAlt;
		this.meanAveragePrecision = meanAveragePrecision;
		this.meanAveragePrecisionAlt = meanAveragePrecisionAlt;
		this.nDCG = nDCG;
	}
	public String getUserID() {
		return userID;
	}
	public int getNumHits() {
		return numHits;
	}
	public double getHitRate() {
		return hitRate;
	}
	public int getNumRecLists() {
		return numRecLists;
	}
	public int getNumLikes() {
		return numLikes;
	}
	public int getNumRecEntities() {
		return numRecEntities;
	}
	public int getNumRecEntitiesAlt() {
		return numRecEntitiesAlt;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public double getAccuracyAlt() {
		return accuracyAlt;
	}
	public double getMeanAveragePrecision() {
		return meanAveragePrecision;
	}
	public double getMeanAveragePrecisionAlt() {
		return meanAveragePrecisionAlt;
	}
	
	public static String getCSVHeader() {
		return "userID, numHits, hitRate, numRecLists, numLikes, numRecEntities, numRecEntities (alt), accuracy, accuracy (alt), meanAveragePrecision, meanAveragePrecision (alt), nDCG";
	}
	
	public String toCSVRow() {
		return userID + ","
				+ numHits + ","
				+ hitRate + ","
				+ numRecLists + ","
				+ numLikes + ","
				+ numRecEntities + ","
				+ numRecEntitiesAlt + ","
				+ accuracy  + ","
				+ accuracyAlt  + ","
				+ meanAveragePrecision  + ","
				+ meanAveragePrecisionAlt + ","
				+ nDCG;
	}
}
