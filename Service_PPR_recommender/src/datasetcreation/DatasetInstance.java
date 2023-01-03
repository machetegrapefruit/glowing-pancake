package datasetcreation;

public class DatasetInstance {
	private String userID;
	private String messageID;
	private String message;
	private String intent;
	private String contexts;
	private boolean toCheck;
	
	public DatasetInstance(String userID, String messageID, String message, String intent, String contexts, boolean toCheck) {
		this.userID = userID;
		this.messageID = messageID;
		this.message = message;
		this.intent = intent;
		this.contexts = contexts;
		this.toCheck = toCheck;
	}

	public String getUserID() {
		return userID;
	}
	
	public String getMessageID() {
		return messageID;
	}

	public String getMessage() {
		return message;
	}

	public String getIntent() {
		return intent;
	}

	public String getContexts() {
		return contexts;
	}
	
	public boolean toCheck() {
		return this.toCheck;
	}
	
	public void setToCheck(boolean toCheck) {
		this.toCheck = toCheck;
	}
	
	public String toString() {
		return "userID: " + userID + ", message: " + message + ", intent: "+ intent + ", contexts: "+ contexts + ", toCheck: " + toCheck; 
	}
	
}
