package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TotalInteractionTime {
private MetricsAccessRecsysDB dbMetricAccess;
	
	private TotalInteractionTime(){
		dbMetricAccess = new MetricsAccessRecsysDB();
	}
	
	public Map<Integer, List<Integer>> totalInteractionTime (String botName) throws Exception{
		Map<Integer, List<Integer>> timeMap = new HashMap<Integer, List<Integer>>();
		Map<Integer, List<Integer>> userTimeMap = new HashMap<Integer, List<Integer>>();
		List<Integer> usersList = new ArrayList<Integer>();
		usersList = dbMetricAccess.selectAllUsers();
		
		Integer key = 0;
		for (Integer user_id : usersList) {
			userTimeMap = dbMetricAccess.selectTotalInteractionTime(user_id, botName);
			for (Integer userkey : userTimeMap.keySet()) {
				usersList = userTimeMap.get(userkey);
				timeMap.put(key, usersList);
				//System.out.println(usersList.get(0));
				System.out.println(usersList.get(1));
				key++;
			}			
		}
		
		System.out.println("timeMap Size: " + timeMap.size());
		System.out.println("timeMap: " + timeMap.toString());	
		
		return timeMap;
	}
	
	public void averageTotalInteractionTime (Map<Integer, List<Integer>>  totalInteractionTimeMap) throws Exception{
		List<Integer> usersList = new ArrayList<Integer>();
	     int second = 0;
	     float n = totalInteractionTimeMap.size();
		for (Integer userkey : totalInteractionTimeMap.keySet()) {
			usersList = totalInteractionTimeMap.get(userkey);
			second = second + usersList.get(1);
		}
		System.out.println("second: " + second);

		float averageTotalInteractionTime = (second)/n;
		
		System.out.println("averageTotalInteractionTime: " + averageTotalInteractionTime);
	}
	
	public void averageTotalInteractionTimeConf1() throws Exception{
		Map<Integer, List<Integer>> totalInteractionTimeMap = new HashMap<Integer, List<Integer>>();
		String conf1 = "conf1testrecsysbot";
		totalInteractionTimeMap = this.totalInteractionTime(conf1);
		this.averageTotalInteractionTime(totalInteractionTimeMap);
	}
	
	public void averageTotalInteractionTimeConf2() throws Exception{
		Map<Integer, List<Integer>> totalInteractionTimeMap = new HashMap<Integer, List<Integer>>();
		String conf1 = "conf2testrecsysbot";
		totalInteractionTimeMap = this.totalInteractionTime(conf1);
		this.averageTotalInteractionTime(totalInteractionTimeMap);
	}
	
	public void averageTotalInteractionTimeConf3() throws Exception{
		Map<Integer, List<Integer>> totalInteractionTimeMap = new HashMap<Integer, List<Integer>>();
		String conf1 = "conf3testrecsysbot";
		totalInteractionTimeMap = this.totalInteractionTime(conf1);
		this.averageTotalInteractionTime(totalInteractionTimeMap);
	}
	
	public void averageTotalInteractionTimeConf4() throws Exception{
		Map<Integer, List<Integer>> totalInteractionTimeMap = new HashMap<Integer, List<Integer>>();
		String conf1 = "conf4testrecsysbot";
		totalInteractionTimeMap = this.totalInteractionTime(conf1);
		this.averageTotalInteractionTime(totalInteractionTimeMap);
	}
	
	
	
    public static void main(String[] args) throws Exception {
    	TotalInteractionTime numberOfQuestions = new TotalInteractionTime();
    	
//    	System.out.println("\naverageTotalInteractionTimeConf1: ");
//    	numberOfQuestions.averageTotalInteractionTimeConf1();
    	
//    	System.out.println("\naverageTotalInteractionTimeConf2: ");
//    	numberOfQuestions.averageTotalInteractionTimeConf2();
//    	System.out.println("\naverageTotalInteractionTimeConf3: ");
//    	numberOfQuestions.averageTotalInteractionTimeConf3();
    	System.out.println("\naverageTotalInteractionTimeConf4: ");
    	numberOfQuestions.averageTotalInteractionTimeConf4();
    }

}
