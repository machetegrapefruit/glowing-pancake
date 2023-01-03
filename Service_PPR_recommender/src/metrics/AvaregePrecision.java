package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AvaregePrecision {

	private MetricsAccessRecsysDB dbMetricAccess;
	
	private AvaregePrecision(){
		dbMetricAccess = new MetricsAccessRecsysDB();
	}
	
	public double getTotalAvaregePrecisionByBotName(String botName) throws Exception{
		double totalAveragePrecision = calculateTotalAvaregePrecisionByBotName(botName);
				
		return totalAveragePrecision;
	}
	
	public double getSelectionAvaregePrecisionByBotName(String botName,int typeSelection) throws Exception{
		double beforeAveragePrecision = calculateSelectionAvaregePrecisionByBotName(botName,typeSelection);
				
		return beforeAveragePrecision;
	}
	
	private Double calculateSelectionAvaregePrecisionByBotName(String botName,int typeSelection) throws Exception{

		List<Integer> usersList = dbMetricAccess.selectAllUsers();
		
		Map<Integer, Double> listAveragePrecisionMap = new HashMap<Integer, Double>();
		
		Integer key = 0;
		for (Integer user_id : usersList) {
			Map<Integer, List<Integer>> userListAndTypeMap = new HashMap<Integer, List<Integer>>();
			Map<Integer, List<Integer>> userElementOfListMap = new HashMap<Integer, List<Integer>>();
			userListAndTypeMap = dbMetricAccess.selectNumberRecommendationListFromUserAndBotName(user_id, botName);
			userListAndTypeMap = dbMetricAccess.selectTypeOfNumberRecommendationListFromUserAndBotName(user_id, botName, userListAndTypeMap);
			//di ogni utente abbiamo in userListAndTypeMap: utente|nLista|tipo
			
			//di ogni lista di questo utente calcoliamo avPrecision
			for (Integer userkey : userListAndTypeMap.keySet()) {
				List<Integer> usersListAndType = new ArrayList<Integer>();
				usersListAndType = userListAndTypeMap.get(userkey);
				int user = usersListAndType.get(0); 
				int list = usersListAndType.get(1);
				int type = usersListAndType.get(2); 
				
				//controlla il tipo di richesta
				if (typeSelection == type) {
					userElementOfListMap = dbMetricAccess.selectLikeAndDislikeFromRecommendationListByUserBotNameAndList(user, botName, list);
					
					if (userElementOfListMap != null && !userElementOfListMap.isEmpty()){
						//System.out.println("\nuserElementOfListMap: " + userElementOfListMap);
						Double avaregePrecisionOfList = calculateAVList(userElementOfListMap);
						//crea la mappa dei valori
						listAveragePrecisionMap.put(key, avaregePrecisionOfList);
						key++;
					}
					
				}

			}	
		}
		//somma i valori
		double sum = 0;
		int size = listAveragePrecisionMap.size();
		System.out.println("listAveragePrecisionMap: " + listAveragePrecisionMap);
		
		for (Integer avkey : listAveragePrecisionMap.keySet()) {
			double value = listAveragePrecisionMap.get(avkey);
			sum = sum + value;
		}
		Double totalavaregePrecisionOfList = sum/size;		
		System.out.println("sum: " + sum);
		System.out.println("size: " + size);
		System.out.println("avaregePrecisionOfList: " + totalavaregePrecisionOfList);
		
		
		
		
		return totalavaregePrecisionOfList;
			
	}
	
	
	private Double calculateTotalAvaregePrecisionByBotName(String botName) throws Exception{
		Map<Integer, List<Integer>> likeRecMoviePostRefocusMap = new HashMap<Integer, List<Integer>>();
		Map<Integer, List<Integer>> userLikeRecMoviePostRefocusMap = new HashMap<Integer, List<Integer>>();
		List<Float> userListAvPrecisionList = new ArrayList<Float>();
		List<Integer> usersList = dbMetricAccess.selectAllUsers();
		
		Map<Integer, Double> listAveragePrecisionMap = new HashMap<Integer, Double>();
		
		Integer key = 0;
		int user = 0;
		for (Integer user_id : usersList) {
			Map<Integer, List<Integer>> userListAndTypeMap = new HashMap<Integer, List<Integer>>();
			Map<Integer, List<Integer>> userElementOfListMap = new HashMap<Integer, List<Integer>>();
			userListAndTypeMap = dbMetricAccess.selectNumberRecommendationListFromUserAndBotName(user_id, botName);
			userListAndTypeMap = dbMetricAccess.selectTypeOfNumberRecommendationListFromUserAndBotName(user_id, botName, userListAndTypeMap);
			//di ogni utente abbiamo in userListAndTypeMap: utente|nLista|typo
			
			//di ogni lista di questo utente calcoliamo avPrecision
			for (Integer userkey : userListAndTypeMap.keySet()) {
				List<Integer> usersListAndType = new ArrayList<Integer>();
				usersListAndType = userListAndTypeMap.get(userkey);
				user = usersListAndType.get(0); 
				int list = usersListAndType.get(1);
				int type = usersListAndType.get(2); 
				
				
	
				//user_id|number_recommendation_list|like|dislike|position);
				userElementOfListMap = dbMetricAccess.selectLikeAndDislikeFromRecommendationListByUserBotNameAndList(user, botName, list);
				
				if (userElementOfListMap != null && !userElementOfListMap.isEmpty()){
					Double avaregePrecisionOfList = calculateAVList(userElementOfListMap);
					//crea la mappa dei valori
					listAveragePrecisionMap.put(key, avaregePrecisionOfList);
					key++;
				}
			}	
		}
		//somma i valori
		double sum = 0;
		int size = listAveragePrecisionMap.size();
//		System.out.println("listAveragePrecisionMap: " + listAveragePrecisionMap);
		
		for (Integer avkey : listAveragePrecisionMap.keySet()) {
			double value = listAveragePrecisionMap.get(avkey);
			sum = sum + value;
		}
		Double totalavaregePrecisionOfList = sum/size;		
//		System.out.println("sum: " + sum);
//		System.out.println("size: " + size);
//		System.out.println("avaregePrecisionOfList: " + totalavaregePrecisionOfList);		
		
		return totalavaregePrecisionOfList;
			
	}
	
	public Double calculateAVList(Map<Integer, List<Integer>> userListLike){
		List<Integer> userLikeDislikePositionList = new ArrayList<Integer>();
		
		double nLike = 0;
		int user = 0;
		List<Double> avList = new ArrayList<Double>();
		for (Integer key : userListLike.keySet()) {
			userLikeDislikePositionList = userListLike.get(key);
			
			user = userLikeDislikePositionList.get(0); 
			int list = userLikeDislikePositionList.get(1);
			int like = userLikeDislikePositionList.get(2); 
			int dislike = userLikeDislikePositionList.get(3); 
			int position = userLikeDislikePositionList.get(4); 
			
			if (like == 1) {
				nLike++;
				Double av = (Double) ((nLike/position) * 1);
				avList.add(av);
//				System.out.println("nLike: " + nLike);
//				System.out.println(position + "position: " + av);
			}
			else if (dislike == 1) {
				Double av = (Double) ((nLike/position) * 0);
				avList.add(av);
//				System.out.println("nLike: " + nLike);
//				System.out.println(position + "position: " + av);
			}
		}
		
		double sum = 0;
		Double size = (double) avList.size();
		
		for(Double p : avList){
		    sum = (sum + p);
		}

		Double avaregePrecisionOfList = sum/size;		
//		System.out.println("sum: " + sum);
//		System.out.println("size: " + size);		
//		System.out.println("avaregePrecisionOfList: " + avaregePrecisionOfList);
		
		System.out.println(user);
//		System.out.println(avaregePrecisionOfList);
		return avaregePrecisionOfList;
	}
	

	

	public static void main(String[] args) throws Exception {
		AvaregePrecision avaregePrecision = new AvaregePrecision();
		
		String conf1 = "conf4testrecsysbot"; 
		
//    	double totalAveragePrecision = avaregePrecision.getTotalAvaregePrecisionByBotName(conf1);
//    	System.out.println("\nAvarege Precision: " + totalAveragePrecision);
//    	//before = 0 - refine = 1 - refocus = 2
    	double beforeAveragePrecision = avaregePrecision.getSelectionAvaregePrecisionByBotName(conf1, 2);
    	System.out.println("\nbeforeAveragePrecision: " + beforeAveragePrecision);


	}

}
