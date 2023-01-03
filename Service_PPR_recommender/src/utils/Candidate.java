package utils;

public class Candidate {
	private Alias alias;
	private int start;
	private int end;
	private int rating;
	
	public Candidate(Alias alias, int start, int end, int rating) {
		this.alias = alias;
		this.start = start;
		this.end = end;
		this.rating = rating;
	}
	
	public Alias getAlias() {
		return alias;
	}
	public int getStart() {
		return start;
	}
	public int getEnd() {
		return end;
	}
	public int getRating() {
		return rating;
	}
}
