package test;

public class UserData {
	private String userID;
	private int numTrainQuestions;
	private int numRecQuestions;
	private double avgTimePerQuestion;
	private long totalInteractionTime;
	private double queryDensity;
	private double queryDensityPreference;
	private double queryEfficiency;
	private double queryEfficiencyTraining;
	private double avgSessionTime;
	public UserData(String userID, int numTrainQuestions, int numRecQuestions, double avgTimePerQuestion, long totalInteractionTime, double queryDensity,
			double queryDensityPreference, double queryEfficiency, double queryEfficiencyTraining, double avgSessionTime) {
		super();
		this.userID = userID;
		this.numTrainQuestions = numTrainQuestions;
		this.numRecQuestions = numRecQuestions;
		this.avgTimePerQuestion = avgTimePerQuestion;
		this.totalInteractionTime = totalInteractionTime;
		this.queryDensity = queryDensity;
		this.queryDensityPreference = queryDensityPreference;
		this.queryEfficiency = queryEfficiency;
		this.queryEfficiencyTraining = queryEfficiencyTraining;
		this.avgSessionTime = avgSessionTime;
	}
	public String getUserID() {
		return userID;
	}
	public int getNumTrainQuestions() {
		return numTrainQuestions;
	}
	public int getNumRecQuestions() {
		return numRecQuestions;
	}
	public double getAvgTimePerQuestion() {
		return avgTimePerQuestion;
	}
	public long getTotalInteractionTime() {
		return totalInteractionTime;
	}
	public double getQueryDensity() {
		return queryDensity;
	}
	public double getQueryDensityPreference() {
		return queryDensityPreference;
	}
	public double getQueryEfficiency() {
		return queryEfficiency;
	}
	public double getQueryEfficiencyTraining() {
		return queryEfficiencyTraining;
	}
	public double getAvgSessionTime() {
		return avgSessionTime;
	}
	public String toCSVRow() {
		return userID + "," + numTrainQuestions + "," + numRecQuestions + "," + avgTimePerQuestion + "," + totalInteractionTime + "," + queryDensity + "," + queryDensityPreference +  "," + queryEfficiency + "," + queryEfficiencyTraining + "," + avgSessionTime;
	}
	public static String getCSVHeader() {
		return "userID, numTrainQuestions, numRecQuestions, avgTimePerQuestion, totalInteractionTime, queryDensity, queryDensity (per preference messages), queryEfficiency, queryEfficiency (training), avgSessionTime";
	}
}
