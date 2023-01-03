package functions;

import java.util.ArrayList;
import java.util.List;

import graph.AdaptiveSelectionController;

public class LogService {

	public String userID;
	private String messageID;
	private String message;
	private long timestampStart;
	private long timestampEnd;
	private String intent;
	private String contexts;
	private List<String> recognized;
	private List<String> events;
	private int pagerankCycle;
	private int numberRecommendationList;
	private String interactionType;
	private String recommendedEntity;
	
	public String getRecommendedEntity() {
		return recommendedEntity;
	}

	public void setRecommendedEntity(String recommendedEntity) {
		this.recommendedEntity = recommendedEntity;
	}

	public enum EventType {
		PREFERENCE,
		RECOMMENDATION,
		DISAMBIGUATION,
		NEW_RECOMMENDATION_CYCLE,
		QUESTION,
		FINISHED_RECOMMENDATION;
		
		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	}
	
	public LogService(String userID) {
		this.userID = userID;
		this.recognized = new ArrayList<String>();
		this.events = new ArrayList<String>();
	}
//		
//	public void insertMessageInLog(String userID, String messageID, String message, long timestampStart, long timestampEnd, 
//			 String intent, String contexts, List<String> recognized, List<String> events, String interactionType) {
//		AdaptiveSelectionController asController = new AdaptiveSelectionController();
//		int pagerankCicle = -1;
//		int numRecommendationList = -1;
//		try {
//			pagerankCicle = asController.getNumberPagerankCicle(userID);
//			numRecommendationList = asController.getNumberRecommendationList(userID);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		asController.insertMessageIntoLog(messageID, userID, message, timestampStart, timestampEnd, 
//				intent, contexts, recognized, events,
//				pagerankCicle, numRecommendationList, interactionType);
//	}
//	
	public LogService() {
		this("undefined");
	}

	public void insertMessageInLog() {
		new AdaptiveSelectionController().insertMessageIntoLog(messageID, userID, message, timestampStart, timestampEnd, intent, contexts, recognized, events, pagerankCycle, numberRecommendationList, interactionType, recommendedEntity);
	}
	
	public void addRecognizedObject(String object) {
		this.recognized.add(object);
	}
	
	/**
	 * 
	 * @return Il <b>riferimento</b> alla lista.
	 */
	public List<String> getRecognizedObjects() {
		return recognized;
	}
	
	public void addEvent(EventType event) {
		this.events.add(event.toString());
	}
	
	public void addEvent(String event) {
		this.events.add(event);
	}
	
	/**
	 * 
	 * @return Il <b>riferimento</b> alla lista.
	 */
	public List<String> getEvents() {
		return events;
	}

	public void setLogMessageToCheck(String userID, String messageID, boolean toCheck) {
		new AdaptiveSelectionController().setLogMessageToCheck(userID, messageID, toCheck);
	}
	public void updateLogMessage(long timestampEnd) {
		new AdaptiveSelectionController().updateMessageLog(this.userID, this.messageID, timestampEnd);
	}
	
	public void updateLogMessage(String userID, String messageID, long timestampEnd, int pagerankCycle, int numberRecommendationList) {
		new AdaptiveSelectionController().updateMessageLog(userID, messageID, timestampEnd, pagerankCycle, numberRecommendationList);
	}
	
	public void updateLogMessage(String userID, String messageID, long timestampEnd) {
		new AdaptiveSelectionController().updateMessageLog(userID, messageID, timestampEnd);
	}

	public void updateLogMessage(String userID, String messageID, long timestampEnd, int pagerankCycle, int numberRecommendationList, List<String> events) {
		new AdaptiveSelectionController().updateMessageLog(userID, messageID, timestampEnd, pagerankCycle, numberRecommendationList, events);

	}
	
	public void setTimestampStart(long timestamp) {
		this.timestampStart = timestamp;
	}
	
	public long getTimestampStart() {
		return timestampStart;
	}
	
	public void setTimestampEnd(long timestamp) {
		this.timestampEnd = timestamp;
	}
	
	public void setPagerankCycle(int pagerankCycle) {
		this.pagerankCycle = pagerankCycle;
	}
	
	public void setNumberRecommendationList(int numberRecommendationList) {
		this.numberRecommendationList = numberRecommendationList;
	}
	
	public int getPagerankCycle() {
		return this.pagerankCycle;
	}
	
	public int getNumberRecommendationList() {
		return this.numberRecommendationList;
	}
	
	public long getTimestampEnd() {
		return timestampEnd;
	}
	
	public void setMessageID (String messageID) {
		this.messageID = messageID;
	}
	
	public String getMessageID() {
		return messageID;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setIntent(String intent) {
		this.intent = intent;
	}
	
	public String getIntent() {
		return intent;
	}
	
	public void setContexts(String contexts) {
		this.contexts = contexts;
	}
	
	public String getContexts() {
		return contexts;
	}
	
	public void setInteractionType(String interactionType) {
		this.interactionType = interactionType;
	}
	
	public String getInteractionType() {
		return interactionType;
	}
	
	public boolean hasMessage(String messageID) {
		return new AdaptiveSelectionController().hasMessage(messageID);
	}
	
	public String getLastTestMessageID() {
		return new AdaptiveSelectionController().getLastTestMessageID();
	}
}
