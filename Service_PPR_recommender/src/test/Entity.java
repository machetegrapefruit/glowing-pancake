package test;

public class Entity {
	private String uri;
	private String label;
	public Entity(String uri, String label) {
		super();
		this.uri = uri;
		this.label = label;
	}
	public String getUri() {
		return uri;
	}
	public String getLabel() {
		return label;
	}
	
	public String toString() {
		return label + " (" + uri + ")";
	}
}
