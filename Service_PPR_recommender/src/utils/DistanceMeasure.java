package utils;


public class DistanceMeasure implements Comparable<DistanceMeasure> {
	private String value;
	private double distance;
	private int index;
	public static final double EPSILON = 0.0005;
	
	public DistanceMeasure(String value, double distance, int index) {
		this.value = value;
		this.distance = distance;
		this.index = index;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public String getValue() {
		return value;
	}
	
	public int getIndex() {
		return index;
	}


	@Override
	public int compareTo(DistanceMeasure o) {
		if (o.distance < this.distance) {
			return 1;
		} else if (o.distance - this.distance < 0.0005) {
			int oLength = o.value.split("\\s+").length;
			int tLength = this.value.split("\\s+").length;
			if (oLength < tLength) {
				return 1; 
			} else if (oLength > tLength) {
				return -1;
			} else {
				return 0;
			}
		} else {
			return -1;
		}
	}
	
	public String toString() {
		return "Value: " + this.value + ", Distance: " + this.distance;
	}
}
