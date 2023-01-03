package functions;

import com.google.gson.JsonArray;

public class ActLearnService {
	interface ActLearnInterface{
		public String GiveAdvice(ProfileService pf, EntityService ef);
		public String GiveAdviceJaccard(ProfileService pf, EntityService ef);
		public String GiveAdviceRandPopularity(EntityService ef);
		public String GiveAdvicePopxVar(EntityService ef);
		public String GivePropertyAdvice(String entity_ID, PropertyService prf);
	}
	
	public static class MakinAct implements ActLearnInterface{
		String userID;
		public MakinAct(String userID){
			this.userID = userID;
		}
		/**
		 * Restituisce un' entità randomica da consigliare
		 * @param ProfileService Utility per il profilo utente
		 * @param EntityService Utility delle entità
		 * @return	String ID entità da raccomandare
		 */
		@Override
		public String GiveAdvice(ProfileService pf, EntityService ef) {
			// TODO Auto-generated method stub
			pf.getUserProfile(userID);
			return ef.getEntityToRate(userID);
		}
		/**
		 * Restituisce un' entità randomica o una simile ad altre piaciute all' utente
		 * @param ProfileService Utility per il profilo utente
		 * @param EntityService Utility delle entità
		 * @return	String ID entità da raccomandare
		 */
		@Override
		public String GiveAdviceJaccard(ProfileService pf, EntityService ef) {
			// TODO Auto-generated method stub
				pf.getUserProfile(userID);
				return ef.getEntityToRateJaccard(userID);
		}
		/**
		 * Restituisce un' entità randomica tra quelle già famose da consigliare
		 * @param EntityService Utility delle entità
		 * @return	String ID entità da raccomandare
		 */
		@Override
		public String GiveAdviceRandPopularity(EntityService ef) {
			// TODO Auto-generated method stub
			return ef.getEntityToRateRandPopular(userID);
		}
		/**
		 * Restituisce un' entità tra le più valutate e famose da consigliare
		 * @param EntityService Utility delle entità
		 * @return	String ID entità da raccomandare
		 */
		@Override
		public String GiveAdvicePopxVar(EntityService ef) {
			// TODO Auto-generated method stub
			return ef.getEntityToRatePopxVar(userID);
		}
		/**
		 * Restituisce una proprietà del film da consigliare
		 * @param String ID entità da raccomandare
		 * @param PropertyService Utility per le proprietà
		 * @param String ID della proprietà
		 */
		@Override
		public String GivePropertyAdvice(String entity_ID, PropertyService prf) {
			// TODO Auto-generated method stub
			return prf.getPropertyLabel(entity_ID);
		}}
}
