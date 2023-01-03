package dialog;

import java.util.List;

import utils.Alias;

public class PendingConfirmation {
	private Alias entity;
	private Alias propertyType;
	private int rating;
	private List<Alias> properties;
	
	public PendingConfirmation(Alias entity, Alias propertyType, int rating, List<Alias> properties) {
		this.entity = entity;
		this.propertyType = propertyType;
		this.rating = rating;
		this.properties = properties;
	}

	public Alias getEntity() {
		return entity;
	}
	
	public int getRating() {
		return rating;
	}

	public Alias getPropertyType() {
		return propertyType;
	}
	
	public List<Alias> getProperties() {
		return this.properties;
	}
	
	public char getRatingSymbol() {
		if (rating == 1) {
			return '+';
		} else if (rating == 0) {
			return '-';
		} else {
			return '/';
		}
	}
	
	
}
