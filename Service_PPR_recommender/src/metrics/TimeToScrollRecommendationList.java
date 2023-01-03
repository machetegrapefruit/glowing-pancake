package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeToScrollRecommendationList {
	
	private MetricsAccessRecsysDB dbMetricAccess;

	private TimeToScrollRecommendationList(){
		dbMetricAccess = new MetricsAccessRecsysDB();
	}
	
	public double getTimeToScrollRecommendationList(String botName) throws Exception{
		double timeToScrollRecommendationList = calculateTimeToScrollRecommendationList(botName);
				
		return timeToScrollRecommendationList;
	}
	
	private Double calculateTimeToScrollRecommendationList(String botName) throws Exception{
		Map<Integer, Integer> timeToScrollMap = new HashMap<Integer, Integer>();
		
		List<Integer> usersList = dbMetricAccess.selectAllUsers();
		Integer key = 0;
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
				int user = usersListAndType.get(0); 
				int list = usersListAndType.get(1);
				int type = usersListAndType.get(2); 
				
				
	
				//user_id|number_recommendation_list|like|dislike|position|responseTime);
				userElementOfListMap = dbMetricAccess.selectLikeAndDislikeFromRecommendationListByUserBotNameAndList(user, botName, list);
				
				if (userElementOfListMap != null && !userElementOfListMap.isEmpty()){
					int avaregePrecisionOfList = calculateTimeToScroll(userElementOfListMap);
					//crea la mappa dei valori
					timeToScrollMap.put(key, avaregePrecisionOfList);
					key++;
				}
			}	
		}
		//somma i valori
		double sum = 0;
		int size = timeToScrollMap.size();
		System.out.println("timeToScrollMap: " + timeToScrollMap);
		
		for (Integer avkey : timeToScrollMap.keySet()) {
			double value = timeToScrollMap.get(avkey);
			sum = sum + value;
		}
		Double totalAvaregeTimeToScrollMap= sum/size;		
		System.out.println("sum: " + sum);
		System.out.println("size: " + size);
		System.out.println("totalAvaregeTimeToScrollMap: " + totalAvaregeTimeToScrollMap);		
		
		return totalAvaregeTimeToScrollMap;
			
	}
	
	public Integer calculateTimeToScroll(Map<Integer, List<Integer>> userListLike){
		List<Integer> userLikeDislikePositionList = new ArrayList<Integer>();
		
		int timeToScroll = 0;
		List<Double> avList = new ArrayList<Double>();
		for (Integer key : userListLike.keySet()) {
			userLikeDislikePositionList = userListLike.get(key);
			
			int user = userLikeDislikePositionList.get(0); 
			int list = userLikeDislikePositionList.get(1);
			int like = userLikeDislikePositionList.get(2); 
			int dislike = userLikeDislikePositionList.get(3); 
			int position = userLikeDislikePositionList.get(4);
			int responseTime = userLikeDislikePositionList.get(5); 
			
			if (responseTime > 0 && responseTime <= 300) {
				timeToScroll = timeToScroll + responseTime;
			}
			

		}
		
		System.out.println("timeToScroll: " + timeToScroll);
		return timeToScroll;
	}
	
    public static void main(String[] args) throws Exception {
    	TimeToScrollRecommendationList TimeToScrollRecommendationList = new TimeToScrollRecommendationList();
    	
    	String conf = "conf4testrecsysbot";
    	
    	double totalAverageTimeToScrollRecommendationList = TimeToScrollRecommendationList.getTimeToScrollRecommendationList(conf);
    	System.out.println("\nConf1 Avarege Precision: " + totalAverageTimeToScrollRecommendationList);
    }

}
