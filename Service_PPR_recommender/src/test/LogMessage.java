package test;

public class LogMessage {
	private String messageID;
	private String userID;
	private String message;
	private long timestampStart;
	private long timestampEnd;
	private String intent;
	private String contexts;
	private boolean toCheck;
	private String recognizedObjects;
	private String events;
	private int pagerankCycle;
	private int numberRecommendationList;
	private String interactionType;
	public String getMessageID() {
		return messageID;
	}
	public String getUserID() {
		return userID;
	}
	public String getMessage() {
		return message;
	}
	public long getTimestampStart() {
		return timestampStart;
	}
	public long getTimestampEnd() {
		return timestampEnd;
	}
	public String getIntent() {
		return intent;
	}
	public String getContexts() {
		return contexts;
	}
	public boolean isToCheck() {
		return toCheck;
	}
	public String getRecognizedObjects() {
		return recognizedObjects;
	}
	public String getEvents() {
		return events;
	}
	public int getPagerankCycle() {
		return pagerankCycle;
	}
	public int getNumberRecommendationList() {
		return numberRecommendationList;
	}
	public String getInteractionType() {
		return interactionType;
	}
	public LogMessage(String messageID, String userID, String message, long timestampStart, long timestampEnd,
			String intent, String contexts, boolean toCheck, String recognizedObjects, String events, int pagerankCycle,
			int numberRecommendationList, String interactionType) {
		super();
		this.messageID = messageID;
		this.userID = userID;
		this.message = message;
		this.timestampStart = timestampStart;
		this.timestampEnd = timestampEnd;
		this.intent = intent;
		this.contexts = contexts;
		this.toCheck = toCheck;
		this.recognizedObjects = recognizedObjects;
		this.events = events;
		this.pagerankCycle = pagerankCycle;
		this.numberRecommendationList = numberRecommendationList;
		this.interactionType = interactionType;
	}
	
}
