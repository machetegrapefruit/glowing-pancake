package dialog;

import java.util.ArrayList;
import java.util.List;

import dialog.SentimentObject.SentimentObjectType;
import utils.Alias;

public class FilteredSentimentObject {
	private int start;
	private int end;
	private String label;
	private String uri;
	private int sentiment;
	private List<FilteredAlias> filteredAliases;
	private List<Alias> aliases;
	private SentimentObjectType type;
	
	public FilteredSentimentObject(String label, String uri, int sentiment, int start, int end, SentimentObjectType type, List<FilteredAlias> aliases) {
		this.start = start;
		this.end = end;
		this.label = label;
		this.uri = uri;
		this.sentiment = sentiment;
		this.filteredAliases = aliases;
		this.type = type;
		this.aliases = new ArrayList<Alias>();
		if (filteredAliases != null) {
			for (FilteredAlias a: filteredAliases) {
				this.aliases.add(a.getAlias());
			}
		}
	}
	
	public FilteredSentimentObject(SentimentObject original, List<FilteredAlias> aliases) {
		this.start = original.getStart();
		this.end = original.getEnd();
		this.label = original.getLabel();
		this.uri = original.getUri();
		this.sentiment = original.getSentiment();
		this.filteredAliases = aliases;
		this.type = original.getType();
		this.aliases = new ArrayList<Alias>();
		if (filteredAliases != null) {
			for (FilteredAlias a: filteredAliases) {
				this.aliases.add(a.getAlias());
			}
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

	public List<FilteredAlias> getFilteredAliases() {
		return filteredAliases;
	}
	
	public List<Alias> getAliases() {
		return this.aliases;
	}

	public SentimentObjectType getType() {
		return type;
	}
}
