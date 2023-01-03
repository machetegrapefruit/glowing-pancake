package entity;

import com.google.gson.JsonObject;

public class AuxAPI {

	private String apiURL;
	private String messageID;
	private JsonObject parameters;
	
	public AuxAPI(String apiURL, String messageID, JsonObject parameters) {
		this.apiURL = apiURL;
		this.messageID = messageID;
		this.parameters = parameters;
	}
	
	public AuxAPI(String apiURL) {
		this.apiURL = apiURL;
	}

	public String getApiURL() {
		return apiURL;
	}

	public void setApiURL(String apiURL) {
		this.apiURL = apiURL;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public JsonObject getParameters() {
		return parameters;
	}

	public void setParameters(JsonObject parameters) {
		this.parameters = parameters;
	}
}
