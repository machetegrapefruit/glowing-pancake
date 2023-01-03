package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NumberOfQuestions {
	
	private MetricsAccessRecsysDB dbMetricAccess;
	
	private NumberOfQuestions(){
		dbMetricAccess = new MetricsAccessRecsysDB();
	}
	
	public Map<Integer, List<Integer>> numberOfQuestions (String botName) throws Exception{
		Map<Integer, List<Integer>> beforeKeyAfterValueMap = new HashMap<Integer, List<Integer>>();
		Map<Integer, List<Integer>> userBeforeKeyAfterValueMap = new HashMap<Integer, List<Integer>>();
		List<Integer> usersList = new ArrayList<Integer>();
		usersList = dbMetricAccess.selectAllUsers();
		
		Integer key = 0;
		for (Integer user_id : usersList) {
			userBeforeKeyAfterValueMap = dbMetricAccess.selectNumberOfQuestions(user_id, botName);
			for (Integer userkey : userBeforeKeyAfterValueMap.keySet()) {
				usersList = userBeforeKeyAfterValueMap.get(userkey);
				//System.out.println(usersList.get(0));
				System.out.println(usersList.get(1)); //prima
				//System.out.println(usersList.get(2)); //dopo
				beforeKeyAfterValueMap.put(key, usersList);
				key++;
			}
			
		}
		
		System.out.println("beforeKeyAfterValueMap Size: " + beforeKeyAfterValueMap.size());
		System.out.println("beforeKeyAfterValueMap: " + beforeKeyAfterValueMap.toString());	
		
		return beforeKeyAfterValueMap;
	}
	
	public void averageNumberOfQuestions (Map<Integer, List<Integer>>  beforeKeyAfterValueMap) throws Exception{
		List<Integer> usersList = new ArrayList<Integer>();
	     int countBefore = 0;
	     int countAfter = 0;
	     float n = beforeKeyAfterValueMap.size();
		for (Integer userkey : beforeKeyAfterValueMap.keySet()) {
			usersList = beforeKeyAfterValueMap.get(userkey);
			countBefore = countBefore + usersList.get(1);
		    countAfter = countAfter + usersList.get(2);

		}
		System.out.println("countBefore: " + countBefore);
		System.out.println("countAfter: " + countAfter);
		float averageNumberOfQuestionsBefore = (countBefore)/n;
		float averageNumberOfQuestionsAfter = (countAfter)/n;
		
		System.out.println("averageNumberOfQuestionsBefore: " + averageNumberOfQuestionsBefore);
		System.out.println("averageNumberOfQuestionsAfter: " + averageNumberOfQuestionsAfter);
	}
	
	public void averageNumberOfQuestionsConf1() throws Exception{
		Map<Integer, List<Integer>> beforeKeyAfterValueMap = new HashMap<Integer, List<Integer>>();
		String conf1 = "conf1testrecsysbot";
		beforeKeyAfterValueMap = this.numberOfQuestions(conf1);
		this.averageNumberOfQuestions(beforeKeyAfterValueMap);
	}
	
	public void averageNumberOfQuestionsConf2() throws Exception{
		Map<Integer, List<Integer>> beforeKeyAfterValueMap = new HashMap<Integer, List<Integer>>();
		String conf1 = "conf2testrecsysbot";
		beforeKeyAfterValueMap = this.numberOfQuestions(conf1);
		this.averageNumberOfQuestions(beforeKeyAfterValueMap);
	}
	
	public void averageNumberOfQuestionsConf3() throws Exception{
		Map<Integer, List<Integer>> beforeKeyAfterValueMap = new HashMap<Integer, List<Integer>>();
		String conf1 = "conf3testrecsysbot";
		beforeKeyAfterValueMap = this.numberOfQuestions(conf1);
		this.averageNumberOfQuestions(beforeKeyAfterValueMap);
	}
	
	public void averageNumberOfQuestionsConf4() throws Exception{
		Map<Integer, List<Integer>> beforeKeyAfterValueMap = new HashMap<Integer, List<Integer>>();
		String conf1 = "conf4testrecsysbot";
		beforeKeyAfterValueMap = this.numberOfQuestions(conf1);
		this.averageNumberOfQuestions(beforeKeyAfterValueMap);
	}
	
	
	
	
    public static void main(String[] args) throws Exception {
    	NumberOfQuestions numberOfQuestions = new NumberOfQuestions();
//    	System.out.println("\naverageNumberOfQuestionsConf1: ");
//    	numberOfQuestions.averageNumberOfQuestionsConf1();
//    	System.out.println("\naverageNumberOfQuestionsConf2: ");
//    	numberOfQuestions.averageNumberOfQuestionsConf2();
//    	System.out.println("\naverageNumberOfQuestionsConf3: ");
//    	numberOfQuestions.averageNumberOfQuestionsConf3();
    	System.out.println("\naverageNumberOfQuestionsConf4: ");
    	numberOfQuestions.averageNumberOfQuestionsConf4();
    }

}
