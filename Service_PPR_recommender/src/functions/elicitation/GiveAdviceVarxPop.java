package functions.elicitation;

import functions.EntityService;

public class GiveAdviceVarxPop implements ActLearnInterface{
	EntityService ef=new EntityService();;
	String userID;
	GiveAdviceVarxPop(String userID){
		this.userID = userID;
	}
	ControlEntity ce=new ControlEntity(userID);
	/**
	 * Returns Popular-Variable entity to recommend
	 * @param String userID user identifier
	 * @return	String ID entity to recommend
	 */
	@Override
	public String GetEntityToRate(String userID) {
		// TODO Auto-generated method stub
		userID=this.userID;
		String uri=null;
		boolean loop=false;
		do {
			if(loop==false){
				do {
					if(ce.Controloop()!=true) {
						uri = ef.getEntityToRatePopxVar(userID);
					}else {
						uri = null;
						loop = true;
						break;
					}
				}while(ce.Ismlistance(uri)!=true);
			}else {
				uri=null;
				loop=false;
				break;
			}
		}while(ce.SeekControlEntity(uri, userID)!=true);
		return uri;
	}
}