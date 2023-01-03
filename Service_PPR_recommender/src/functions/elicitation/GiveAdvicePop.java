package functions.elicitation;
import functions.EntityService;

public class GiveAdvicePop implements ActLearnInterface{
	EntityService ef=new EntityService();
	String userID;
	GiveAdvicePop(String userID){
		this.userID = userID;
	}
	ControlEntity ce=new ControlEntity(userID);
	/**
	 * Returns Randomic entity to recommend
	 * @param String userID user identifier
	 * @return	String ID entity to recommend
	 */
	@Override
	public String GetEntityToRate(String userID) {
		// TODO Auto-generated method stub
		userID=this.userID;
		String uri=null;
		String label;
		boolean loop=false;
		do{
			if(loop==false) {
				do {
					if(ce.Controloop()!=true) {
						label=ef.getEntityToRate(userID);
						uri=ef.getEntityURI(label);
						System.out.println("movieURIpopular: " + uri);
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
