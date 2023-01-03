package functions.elicitation;

import functions.EntityService;

public class GiveAdviceJaccard implements ActLearnInterface{
	EntityService ef=new EntityService();
	String userID;
	GiveAdviceJaccard(String userID){
		this.userID = userID;
	}
	ControlEntity ce=new ControlEntity(userID);
	/**
	 * Returns Jaccard_mapped entity to recommend
	 * @param String userID user identifier
	 * @return	String ID entity to recommend
	 */
	@Override
	public String GetEntityToRate(String userID) {
		// TODO Auto-generated method stub
		userID=this.userID;
		int i=0;
		String uri=null;
		boolean loop=false;
		do {
			if(loop==false) {
				do {
					if(ce.Controloop()!=true) {
						uri = ef.getEntityToRateJaccard(userID);
						if(uri==null || i>5) {
							uri = ef.getEntityToRatePopxVar(userID);
						}
						i++;
					}else {
						uri=null;
						loop=true;
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