package dialog;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import utils.Alias;

public class SentimentObject {
	
	public static enum SentimentObjectType {
		ENTITY,
		PROPERTY_TYPE,
		KEYWORD
	}
	
	private int start;
	private int end;
	private String label;
	private String uri;
	private int sentiment;
	private List<Alias> aliases;
	private SentimentObjectType type;
	
	public SentimentObject(int start, 
			int end, 
			String label, 
			String uri, 
			int sentiment, 
			List<Alias> aliases, 
			SentimentObjectType type) {
		this.start = start;
		this.end = end;
		this.label = label;
		this.uri = uri;
		this.sentiment = sentiment;
		this.aliases = aliases;
		this.type = type;
	}
	
	public SentimentObject(JsonObject obj) {
		this.start = obj.get("start").getAsInt();
		this.end = obj.get("end").getAsInt();
		this.sentiment = obj.get("sentiment").getAsInt();
		try {
			this.label = obj.get("label").getAsString();
			this.uri = obj.get("uriDBpedia").getAsString();
			String typeStr = obj.get("type").getAsString();
			if (typeStr.equals("entity")) {
				this.type = SentimentObjectType.ENTITY;
			} else if (typeStr.equals("propertyType")) {
				this.type = SentimentObjectType.PROPERTY_TYPE;
			} else if (typeStr.equals("keyword")) {
				this.type = SentimentObjectType.KEYWORD;
			}
		} catch (Exception e) {
			this.type = SentimentObjectType.ENTITY;
		}

		aliases = new ArrayList<Alias>();
		JsonArray aliasesJson = obj.getAsJsonArray("aliases");
		for (int i = 0; i < aliasesJson.size(); i++) {
			JsonObject aliasJson = aliasesJson.get(i).getAsJsonObject();
			Alias alias = new Alias(aliasJson.get("dbpediaURI").getAsString(), aliasJson.get("label").getAsString());
			this.aliases.add(alias);
		}
	}
	
	public int getStart() {
		return start;
	}
	public int getEnd() {
		return end;
	}
	public String getLabel() {
		return label;
	}
	public String getUri() {
		return uri;
	}
	public int getSentiment() {
		return sentiment;
	}
	public List<Alias> getAliases() {
		return aliases;
	}
	public SentimentObjectType getType() {
		return type;
	}
	
}
