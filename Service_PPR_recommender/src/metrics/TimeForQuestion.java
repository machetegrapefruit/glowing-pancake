package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeForQuestion {
	
	private MetricsAccessRecsysDB dbMetricAccess;
	
	private TimeForQuestion(){
		dbMetricAccess = new MetricsAccessRecsysDB();
	}
	
	public Map<Integer, List<Integer>> timeforQuestions (String botName) throws Exception{
		Map<Integer, List<Integer>> timeMap = new HashMap<Integer, List<Integer>>();
		Map<Integer, List<Integer>> userTimeMap = new HashMap<Integer, List<Integer>>();
		List<Integer> usersList = new ArrayList<Integer>();
		usersList = dbMetricAccess.selectAllUsers();
		
		Integer key = 0;
		for (Integer user_id : usersList) {
			userTimeMap = dbMetricAccess.timeNumberOfQuestions(user_id, botName);
			//userTimeMap = dbMetricAccess.timeNumberOfQuestionTraining(user_id, botName);
			for (Integer userkey : userTimeMap.keySet()) {
				usersList = userTimeMap.get(userkey);
				System.out.println(usersList.get(0));
				//System.out.println(usersList.get(1));
				timeMap.put(key, usersList);
				key++;
			}			
		}
		
		System.out.println("timeMap Size: " + timeMap.size());
		System.out.println("timeMap: " + timeMap.toString());	
		
		return timeMap;
	}
	
	public void averageTimeForQuestions (Map<Integer, List<Integer>>  timeForQuestionsMap) throws Exception{
		List<Integer> usersList = new ArrayList<Integer>();
	     int second = 0;
	     float n = timeForQuestionsMap.size();
		for (Integer userkey : timeForQuestionsMap.keySet()) {
			usersList = timeForQuestionsMap.get(userkey);
			second = second + usersList.get(1);
		}
		System.out.println("second: " + second);

		float averageTimeForQuestions = (second)/n;
		
		System.out.println("averageTimeForQuestions: " + averageTimeForQuestions);
	}
	
	public void averageTimeForQuestionsConf1() throws Exception{
		Map<Integer, List<Integer>> beforeKeyAfterValueMap = new HashMap<Integer, List<Integer>>();
		String conf1 = "conf1testrecsysbot";
		beforeKeyAfterValueMap = this.timeforQuestions(conf1);
		this.averageTimeForQuestions(beforeKeyAfterValueMap);
	}
	
	public void averageTimeForQuestionsConf2() throws Exception{
		Map<Integer, List<Integer>> beforeKeyAfterValueMap = new HashMap<Integer, List<Integer>>();
		String conf1 = "conf2testrecsysbot";
		beforeKeyAfterValueMap = this.timeforQuestions(conf1);
		this.averageTimeForQuestions(beforeKeyAfterValueMap);
	}
	
	public void averageTimeForQuestionsConf3() throws Exception{
		Map<Integer, List<Integer>> beforeKeyAfterValueMap = new HashMap<Integer, List<Integer>>();
		String conf1 = "conf3testrecsysbot";
		beforeKeyAfterValueMap = this.timeforQuestions(conf1);
		this.averageTimeForQuestions(beforeKeyAfterValueMap);
	}
	
	public void averageTimeForQuestionsConf4() throws Exception{
		Map<Integer, List<Integer>> beforeKeyAfterValueMap = new HashMap<Integer, List<Integer>>();
		String conf1 = "conf4testrecsysbot";
		beforeKeyAfterValueMap = this.timeforQuestions(conf1);
		this.averageTimeForQuestions(beforeKeyAfterValueMap);
	}
	
	
	
    public static void main(String[] args) throws Exception {
    	TimeForQuestion numberOfQuestions = new TimeForQuestion();
    	System.out.println("\naverageNumberOfQuestionsConf1: ");
    	numberOfQuestions.averageTimeForQuestionsConf1();
//    	System.out.println("\naverageNumberOfQuestionsConf2: ");
//    	numberOfQuestions.averageTimeForQuestionsConf2();
//    	System.out.println("\naverageNumberOfQuestionsConf3: ");
//    	numberOfQuestions.averageTimeForQuestionsConf3();
//    	System.out.println("\naverageNumberOfQuestionsConf4: ");
//    	numberOfQuestions.averageTimeForQuestionsConf4();
    }

}
