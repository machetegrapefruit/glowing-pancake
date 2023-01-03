package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HitRate {
	
	private MetricsAccessRecsysDB dbMetricAccess;
	public static Map<Integer, List<Integer>> userNListNLikeMap;
	
	private HitRate(){
		dbMetricAccess = new MetricsAccessRecsysDB();
		userNListNLikeMap = new HashMap<Integer, List<Integer>>();
	}
	
	public double getTotalAvaregeNumberOfLike(String botName) throws Exception{
		double totalAvaregeNumberOfLike = calculateTotalAvaregeNumberOfLike(botName);
				
		return totalAvaregeNumberOfLike;
	}
	
	public double getSelectionAvaregeNumberOfLike(String botName,int typeSelection) throws Exception{
		double selectionAvaregeNumberOfLike = calculateSelectionAvaregeNumberOfLike(botName,typeSelection);
				
		return selectionAvaregeNumberOfLike;
	}
	
	public double calculateSelectionAvaregeNumberOfLike (String botName,int typeSelection) throws Exception{
		Map<Integer, Integer> listLikeMap = new HashMap<Integer, Integer>();
		
		List<Integer> usersList = dbMetricAccess.selectAllUsers();		
		
		Integer key = 0;
		for (Integer user_id : usersList) {
			Map<Integer, List<Integer>> userListAndTypeMap = new HashMap<Integer, List<Integer>>();
			Map<Integer, List<Integer>> userElementOfListMap = new HashMap<Integer, List<Integer>>();
			userListAndTypeMap = dbMetricAccess.selectNumberRecommendationListFromUserAndBotName(user_id, botName);
			userListAndTypeMap = dbMetricAccess.selectTypeOfNumberRecommendationListFromUserAndBotName(user_id, botName, userListAndTypeMap);
			//di ogni utente abbiamo in userListAndTypeMap: utente|nLista|typo
			
			//di ogni lista di queste liste utente vediamo se c'è almeno un like
			for (Integer userkey : userListAndTypeMap.keySet()) {
				List<Integer> usersListAndType = new ArrayList<Integer>();
				usersListAndType = userListAndTypeMap.get(userkey);
				int user = usersListAndType.get(0); 
				int list = usersListAndType.get(1);
				int type = usersListAndType.get(2); 
				//System.out.println("userListAndTypeMap: " + userListAndTypeMap.toString());	
				
				//user_id|number_recommendation_list|like|dislike|position);
				//controlla il tipo di richesta
				if (typeSelection == type) {
					userElementOfListMap = dbMetricAccess.selectLikeAndDislikeFromRecommendationListByUserBotNameAndList(user, botName, list);
					//System.out.println("userElementOfListMap: " + userElementOfListMap.toString());	
					if (userElementOfListMap != null && !userElementOfListMap.isEmpty()){
						int countLike = countLikeOfList(userElementOfListMap);
						//crea la mappa dei like
						listLikeMap.put(key, countLike);
						key++;
					}
				}
			}
		}
		double sum = 0;
		int size = listLikeMap.size();
		System.out.println("listLikeMap: " + listLikeMap.toString());	
			
		for (Integer avkey : listLikeMap.keySet()) {
			double value = listLikeMap.get(avkey);
			if (value > 0) {
				sum++;
			}
		}
		
		Double totalLikeList = sum/size;		
		System.out.println("sum: " + sum);
		System.out.println("size: " + size);
		System.out.println("TotalAvaregeNumberOfLike: " + totalLikeList);
		
    	//System.out.println("\nuserNListNLikeMap size: " + userNListNLikeMap.size());
    	//System.out.println("userNListNLikeMap: " + userNListNLikeMap);
    	
    	double userAv = 0;
		for (Integer user : userNListNLikeMap.keySet()) {
			List<Integer> usersNLike = userNListNLikeMap.get(user);
			double listSize = usersNLike.size();
			int listSum = 0;
			for (Integer like : usersNLike) {
				listSum = listSum + like;
			}
			Double userAverage = listSum/listSize;
			//System.out.println(user);
			//System.out.println("listSum: " + listSum);
			//System.out.println("listSize: " + listSize);			
			System.out.println(userAverage);
			userAv = userAv + userAverage;
		}
		double sizeMap = userNListNLikeMap.size();
		Double totaluserList = userAv/sizeMap;		
		System.out.println("sum: " + userAv);
		System.out.println("size: " + sizeMap);
		System.out.println("User HitRate: " + totaluserList);
		
    	//System.out.println("\nuserNListNLikeMap size: " + userNListNLikeMap.size());
    	//System.out.println("userNListNLikeMap: " + userNListNLikeMap);
		

		return totalLikeList;
	}
	
	
	public double calculateTotalAvaregeNumberOfLike (String botName) throws Exception{
		Map<Integer, List<Integer>> likeRecMoviePostRefineMap = new HashMap<Integer, List<Integer>>();
		Map<Integer, List<Integer>> userLikeRecMoviePostRefineMap = new HashMap<Integer, List<Integer>>();
		Map<Integer, Integer> listLikeMap = new HashMap<Integer, Integer>();
		
		List<Integer> usersList = dbMetricAccess.selectAllUsers();		
		
		Integer key = 0;
		for (Integer user_id : usersList) {
			Map<Integer, List<Integer>> userListAndTypeMap = new HashMap<Integer, List<Integer>>();
			Map<Integer, List<Integer>> userElementOfListMap = new HashMap<Integer, List<Integer>>();
			Map<Integer, Double> userHitrateMap = new HashMap<Integer, Double>();
			userListAndTypeMap = dbMetricAccess.selectNumberRecommendationListFromUserAndBotName(user_id, botName);
			userListAndTypeMap = dbMetricAccess.selectTypeOfNumberRecommendationListFromUserAndBotName(user_id, botName, userListAndTypeMap);
			//di ogni utente abbiamo in userListAndTypeMap: utente|nLista|typo
			
			//di ogni lista di queste liste utente vediamo se c'è almeno un like
			for (Integer userkey : userListAndTypeMap.keySet()) {
				List<Integer> usersListAndType = new ArrayList<Integer>();
				usersListAndType = userListAndTypeMap.get(userkey);
				int user = usersListAndType.get(0); 
				int list = usersListAndType.get(1);
				int nType = usersListAndType.get(2); 
				
				//user_id|number_recommendation_list|like|dislike|position);
				userElementOfListMap = dbMetricAccess.selectLikeAndDislikeFromRecommendationListByUserBotNameAndList(user, botName, list);
				
				
				
				if (userElementOfListMap != null && !userElementOfListMap.isEmpty()){
					int countLike = countLikeOfList(userElementOfListMap);
					//crea la mappa dei like
					listLikeMap.put(key, countLike);
					key++;
				}
			}
		}
		double sum = 0;
		int size = listLikeMap.size();
		System.out.println("listLikeMap: " + listLikeMap.toString());		
			
		for (Integer avkey : listLikeMap.keySet()) {
			double value = listLikeMap.get(avkey);
			if (value > 0) {
				sum++;
			}
		}
		
		
		Double totalLikeList = sum/size;		
		System.out.println("sum: " + sum);
		System.out.println("size: " + size);
		System.out.println("TotalAvaregeNumberOfLike: " + totalLikeList);	
		
//		System.out.println("\nuserNListNLikeMap size: " + userNListNLikeMap.size());
//    	System.out.println("userNListNLikeMap: " + userNListNLikeMap);
    	
    	double userAv = 0;
		for (Integer user : userNListNLikeMap.keySet()) {
			List<Integer> usersNLike = userNListNLikeMap.get(user);
			double listSize = usersNLike.size();
			int listSum = 0;
			for (Integer like : usersNLike) {
				listSum = listSum + like;
			}
			Double userAverage = listSum/listSize;
			System.out.println(user);
			//System.out.println("listSum: " + listSum);
			//System.out.println("listSize: " + listSize);			
			//System.out.println(userAverage);
			userAv = userAv + userAverage;
		}
		double sizeMap = userNListNLikeMap.size();
		Double totaluserList = userAv/sizeMap;		
		System.out.println("sum: " + userAv);
		System.out.println("size: " + sizeMap);
		System.out.println("User HitRate: " + totaluserList);
		
//    	System.out.println("\nuserNListNLikeMap size: " + userNListNLikeMap.size());
//    	System.out.println("userNListNLikeMap: " + userNListNLikeMap);
		
		return totalLikeList;
	}
	
	public Integer countLikeOfList(Map<Integer, List<Integer>> userListLike){
		List<Integer> userLikeDislikePositionList = new ArrayList<Integer>();
		List<Integer> userListNLike = new ArrayList<Integer>();
		
		int nLike = 0;
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
			}
			
		}
		
		if (nLike > 0){
			//userListNLike.add(list);
			if (userNListNLikeMap.containsKey(user)) {
				List<Integer> userListNLikeOld = new ArrayList<Integer>();
				userListNLikeOld = userNListNLikeMap.get(user);
				userListNLike.addAll(userListNLikeOld);
				userListNLike.add(1);
				userNListNLikeMap.put(user,userListNLike);
			}else {
				userListNLike.add(1);
				userNListNLikeMap.put(user, userListNLike);
			}
			
		}else {
			//userListNLike.add(list);
			
			if (userNListNLikeMap.containsKey(user)) {
				List<Integer> userListNLikeOld = new ArrayList<Integer>();
				userListNLikeOld = userNListNLikeMap.get(user);
				userListNLike.addAll(userListNLikeOld);
				userListNLike.add(0);
				userNListNLikeMap.put(user,userListNLike);
			}else {
				userListNLike.add(0);
				userNListNLikeMap.put(user, userListNLike);
			}
		}
		
//		System.out.println(user);
//		if (nLike > 0)
//			System.out.println(1);
//	
//		else
//		  System.out.println(nLike);
		
		//System.out.println("nLike: " + nLike);
		return nLike;
	}

	
    public static void main(String[] args) throws Exception {
    	HitRate hitRate = new HitRate();
    	
    	String conf = "conf1testrecsysbot";
    	
//    	double totalAveragePrecision = hitRate.getTotalAvaregeNumberOfLike(conf);
//    	System.out.println("\nHit Rate: " + totalAveragePrecision);
//    	//before = 0 - refine = 1 - refocus = 2
    	double beforeAvaregeNumberOfLike = hitRate.getSelectionAvaregeNumberOfLike(conf, 1);
    	System.out.println("\nHit Rate: " + beforeAvaregeNumberOfLike);
    	
  	
    	

    }

}
