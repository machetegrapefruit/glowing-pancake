package test.convrecsys;

public class CRSMessage {
	private String utterance;
	private String agent;
	private String goal;
	private double feedback;
	private CRSEntity[] entities;
	public CRSMessage(String utterance, String agent, String goal, double feedback, CRSEntity[] entities) {
		super();
		this.utterance = utterance;
		this.agent = agent;
		this.goal = goal;
		this.feedback = feedback;
		this.entities = entities;
	}
	public String getUtterance() {
		return utterance;
	}
	public String getAgent() {
		return agent;
	}
	public String getGoal() {
		return goal;
	}
	public double getFeedback() {
		return feedback;
	}
	public CRSEntity[] getEntities() {
		return entities;
	}
	
}
