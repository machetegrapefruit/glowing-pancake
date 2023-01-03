package test.convrecsys;

public class CRSConversation {
	private String id;
	private CRSTurn[] turns;
	public CRSConversation(String id, CRSTurn[] turns) {
		super();
		this.id = id;
		this.turns = turns;
	}
	public String getId() {
		return id;
	}
	public CRSTurn[] getTurns() {
		return turns;
	}
	
}
