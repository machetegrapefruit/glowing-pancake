package test.grouplens;

import java.util.List;

public class GrouplensInstance {
	private String firstQuery;
	private String followupQuery;
	private List<String> items;
	private String firstQueryIntent;
	private String followupQueryIntent;
	private List<String> recognizedItems;
	
	public GrouplensInstance() {
		
	}
	
	public void setFirstQueryIntent(String firstQueryIntent) {
		this.firstQueryIntent = firstQueryIntent;
	}

	public void setFollowupQueryIntent(String followupQueryIntent) {
		this.followupQueryIntent = followupQueryIntent;
	}

	public void setrecognizedItems(List<String> recognizedItems) {
		this.recognizedItems = recognizedItems;
	}
	public String getFirstQueryIntent() {
		return firstQueryIntent;
	}

	public String getFollowupQueryIntent() {
		return followupQueryIntent;
	}

	public List<String> getRecognizedItems() {
		return recognizedItems;
	}

	public String getFirstQuery() {
		return firstQuery;
	}
	public String getFollowupQuery() {
		return followupQuery;
	}
	public List<String> getItems() {
		return items;
	}
	public void setFirstQuery(String firstQuery) {
		this.firstQuery = firstQuery;
	}
	public void setFollowupQuery(String followupQuery) {
		this.followupQuery = followupQuery;
	}
	public void setItems(List<String> items) {
		this.items = items;
	}	
}
