package test.convrecsys;

public class CRSRecommendationConversation {
	private String id;
	private CRSRecommendationTurn[] turns;
	public CRSRecommendationConversation(String id, CRSRecommendationTurn[] turns) {
		super();
		this.id = id;
		this.turns = turns;
	}
	public String getId() {
		return id;
	}
	public CRSRecommendationTurn[] getTurns() {
		return turns;
	}
}
