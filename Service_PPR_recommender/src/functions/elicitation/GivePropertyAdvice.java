package functions.elicitation;

import functions.PropertyService;

public class GivePropertyAdvice implements ActLearnInterface{
	PropertyService prf=new PropertyService();
	Object actalg;
	String userID;
	GivePropertyAdvice(String userID){
		this.userID = userID;
	}
	/**
	 * Randomly-Chosen Algorithm for recommending 
	 * @return	Uri generated from Chosen
	 */
	private String RrandBuildAlg() {
		double rand = (Math.random()*((3-1)+1))+1;
		String uri = "";
		System.out.println(rand);
		if(rand <= 1) {
				actalg = new GiveAdviceVarxPop(userID);
				uri = ((GiveAdviceVarxPop) actalg).GetEntityToRate(userID);
		} else {
		if(rand <= 2) {
				actalg = new GiveAdviceRandPop(userID);
				uri = ((GiveAdviceRandPop) actalg).GetEntityToRate(userID);
		} else {
				actalg = new GiveAdvicePop(userID);
				uri = ((GiveAdvicePop) actalg).GetEntityToRate(userID);
		}}
		return uri;
	}
	/**
	 * Returns property to recommend
	 * @param String userID user identifier
	 * @return	String ID property to recommend
	 */
	@Override
	public String GetEntityToRate(String userID) {
		// TODO Auto-generated method stub
		userID = this.userID;
		String uri = prf.getPropertyFromMovies(RrandBuildAlg(),userID);
		return uri;
	}
}
