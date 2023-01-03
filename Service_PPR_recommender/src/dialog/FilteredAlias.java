package dialog;

import utils.Alias;

public class FilteredAlias {
	private Alias alias;
	private double distance;
	
	public FilteredAlias(Alias alias, double distance) {
		this.alias = alias;
		this.distance = distance;
	}
	
	public Alias getAlias() {
		return this.alias;
	}
	
	public double getDistance() {
		return this.distance;
	}
	
	public boolean equals(Object other) {
		return this.alias.getURI().equals(((FilteredAlias) other).getAlias().getURI());
	}
}
