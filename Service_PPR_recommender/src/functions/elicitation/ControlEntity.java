package functions.elicitation;

import cern.colt.Arrays;
import exception.NoItemsToSuggestException;
import functions.EntityService;

public class ControlEntity {
	final int EOL=11;
	String user_id;
	int conloop=0;
	EntityService controller = new EntityService();
	public ControlEntity(String userID) {
		// TODO Auto-generated constructor stub
		user_id = userID;
	}
	/**
	 * Controls entity in user profile if present
	 * @param String uri of entity
	 * @param String userID
	 * @return	boolean result of control
	 */
	boolean SeekControlEntity(String uri, String user_id){
		String [] con= controller.getEntityToControl(user_id);
		String [] skip= controller.getEntitySkipped(user_id);
		for(int i = 0;uri != null && i < con.length;i++) {
			if(uri.equals(con[i].toString())) {
				System.out.println("Entità già valutata per questo utente!");
				return false;
			}
		}
		for(int i = 0;uri != null && i < skip.length;i++) {
			if(uri.equals(skip[i].toString())) {
				System.out.println("Entità già skippata per questo utente!");
				return false;
			} else {
				if(skip[i] == null) {
					System.out.println("Entità già skippata per questo utente!");
					return false;
				}
			}
		}
		return true;
	}
	
	boolean Ismlistance(String uri) {
		boolean check = false;
		String label = controller.getEntityMLLabel(uri);
		if(label != null) {
			label=label.replaceAll("^The\\s", "");
			label=label.replaceAll("\\s+[(]+.+[)]", "");
		}
		if(uri != null && label != null && controller.getEntityMovieLens(label) > 3 ) {
			System.out.println("Film Corretto");
			check = true;
			return check;
		}else {
			System.out.println("Film non Corretto");
			return check;
		}
	}
	
	boolean Controloop(){
		conloop++;
		System.out.println("Actual loop time: " + conloop);
		if(conloop >= EOL) {
			try {
				conloop = 0;
				throw new exception.NoItemsToSuggestException();
			} catch (NoItemsToSuggestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
}
