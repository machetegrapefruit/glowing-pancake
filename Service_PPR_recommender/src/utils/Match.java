package utils;

/*
 * Questa classe rappresenta un matching ottenuto dall'algoritmo di LevenshteinDistanceCalculator
 */
public class Match implements Comparable<Match> {
	private int start;     			//Indice del token inizio del matching
	private int end;       			//Indice del token finale del matching
	private String matchString;     //Stringa associata al matching
	
	public Match(int start, int end, String matchString) {
		this.start = start;
		this.end = end;
		this.matchString = matchString;
	}
	
	public int getStart() {
		return this.start;
	}
	
	public int getEnd() {
		return this.end;
	}
	
	public String getMatchString() {
		return this.matchString;
	}
	
	@Override
	public boolean equals(Object other) {
		Match otherObj = (Match) other;
		return (otherObj.end == this.end && otherObj.start == this.start);
	}
	
	public boolean contains(Match other) {
		return (this.start <= other.start && this.end >= other.end);
	}
	
	public boolean hasIntersection(Match other) {
		return ((this.start <= other.end && this.end > other.end)
				|| (this.start < other.end && this.end >= other.end)
				|| (other.start <= this.end && other.end > this.end)
				|| (other.start < this.end && other.end >= this.end));
	}
	
	@Override
	public String toString() {
		return "Start: " + start + ", End: " + end + ", match: " + matchString;
	}

	@Override
	public int compareTo(Match o) {
		int thisLength = this.end - this.start;
		int thatLength = o.end - o.start;
		return thisLength - thatLength;
	}

}
