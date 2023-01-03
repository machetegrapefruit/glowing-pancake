package dialog;

import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.standard.MediaSize.Other;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import replies.CustomReply;
import replies.Reply;
import utils.FormatUtils;

public class ApiAiResponse implements Response {
	private String speech;
	private JsonArray context;
	private JsonObject data;
	private boolean failure;
	private boolean changedRecommendedEntity;
	private boolean skippedDisambiguation;
	private List<String> recognizedObjects;
	private List<String> events;
	
	private List<Message> messages;
	private ReplyMarkup replyMarkup;
	private AuxAPI auxAPI;
	
	public ApiAiResponse() {
		this.context = new JsonArray();
		this.failure = false;
		this.changedRecommendedEntity = false;
		this.skippedDisambiguation = false;
		this.recognizedObjects = new ArrayList<>();
		this.events = new ArrayList<>();	
		
		this.messages = new ArrayList<Message>();
	}
	
	public void addSpeech(String speech) {
		String[] speechSplit = speech.split("\n\n");
		for (String s: speechSplit) {
			if (!s.trim().equals("")) {
				this.messages.add(new Message(s.trim()));
			}
		}
	}
	
	public void addContext(String contextName) {
		JsonObject contextObject = new JsonObject();
		contextObject.addProperty("name", contextName);
		contextObject.addProperty("lifespan", 2);
		this.context.add(contextObject);
	}
	
	public void addContext(String contextName, int duration) {
		JsonObject contextObject = new JsonObject();
		contextObject.addProperty("name", contextName);
		contextObject.addProperty("lifespan", duration);
		this.context.add(contextObject);
	}
	
	public void addEvent(String event) {
		if (!events.contains(event)) {
			this.events.add(event);
		}
	}
	
	public void addRecognizedObject(String id) {
		this.recognizedObjects.add(id);
	}
	
	public List<String> getEvents() {
		return this.events;
	}
	
	public List<String> getRecognizedObjects() {
		return this.recognizedObjects;
	}
	
	public JsonArray getContexts() {
		return this.context;
	}
	
	public void addImage(String imageURL, String imageCaption) {
		imageURL = FormatUtils.correctPhotoRes(imageURL);
		this.messages.add(new Message(imageCaption, imageURL));
	}
	
	public void addLink(String linkURL, String linkLabel) {
		this.messages.add(new Message(linkLabel, null, linkURL));
	}
	
	public void setReplyMarkup(ReplyMarkup markup) {
		this.replyMarkup = markup;
	}
	
	public void setAuxAPI(AuxAPI auxAPI) {
		this.auxAPI = auxAPI;
	}
	
	public Reply getReply() {
		return new CustomReply(messages.toArray(new Message[messages.size()]), replyMarkup, auxAPI);
	}
	
	public JsonObject getData() {
		return this.data;
	}
	
	public void merge(ApiAiResponse other) {
		for (String event: other.getEvents()) {
			addEvent(event);
		}
		this.recognizedObjects.addAll(other.getRecognizedObjects());
		for (JsonElement otherContext: other.context) {
			this.context.add(otherContext);
		}
		if (other.failure) {
			this.failure = other.failure;
		}
		if (other.changedRecommendedEntity) {
			this.changedRecommendedEntity = other.changedRecommendedEntity;
		} if (other.skippedDisambiguation) {
			this.skippedDisambiguation = other.skippedDisambiguation;
		}
		for (Message m: other.messages) {
			this.messages.add(m);
		}
		if (other.auxAPI != null) {
			this.auxAPI = other.auxAPI;
		}
		if (other.replyMarkup != null) {
			this.replyMarkup = other.replyMarkup;
		}
	} 
	
	public String getSpeech() {
		return this.speech;
	}

	@Override
	public boolean isFailure() {
		return this.failure;
	}
	
	public boolean isChangedRecommendedEntity() {
		return changedRecommendedEntity;
	}
	
	public boolean isSkippedDisambiguation() {
		return this.skippedDisambiguation;
	}
	
	public void setChangedRecommendedEntity(boolean changedRecommendedEntity) {
		this.changedRecommendedEntity = changedRecommendedEntity;
	}

	@Override
	public void setFailure(boolean failure) {
		this.failure = failure;
	}
	
	public void setSkippedDisambiguation(boolean skippedDisambiguation) {
		this.skippedDisambiguation = skippedDisambiguation;
	}

	@Override
	public JsonObject toJson() {
		return new Gson().toJsonTree(this).getAsJsonObject();
	}
}
