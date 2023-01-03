package it.uniba.swap.mler.entityrecognizer;

import it.uniba.swap.mler.utils.IndexRange;

public class SentimentMentionMap{
	private MentionMap mention;
	private int sentiment;
	
	public SentimentMentionMap(MentionMap mention, int sentiment) {
		this.mention = mention;
		this.sentiment = sentiment;
	}

	public MentionMap getMention() {
		return mention;
	}

	public int getSentiment() {
		return sentiment;
	}
}
