package it.uniba.swap.mler.utils;

public class IndexRange {
	private int start;
	private int end;
	public IndexRange(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}
	public int getStart() {
		return start;
	}
	public int getEnd() {
		return end;
	}
	
	public boolean hasIntersection(IndexRange other) {
		return ((this.start <= other.end && this.end > other.end)
				|| (this.start < other.end && this.end >= other.end)
				|| (other.start <= this.end && other.end > this.end)
				|| (other.start < this.end && other.end >= this.end));
	}
	
	public String toString() {
		return "(" + start + ", " + end + ")";
	}
	
	public static void main(String[] args) {
		IndexRange r1 = new IndexRange(0,2);
		IndexRange r2 = new IndexRange(3,5);
		IndexRange r3 = new IndexRange(0,6);
		IndexRange r4 = new IndexRange(2,4);
		System.out.println(r1.hasIntersection(r2));
		System.out.println(r2.hasIntersection(r1));
		System.out.println(r3.hasIntersection(r4));
		System.out.println(r4.hasIntersection(r3));
		System.out.println(r1.hasIntersection(r1));
	}
}
