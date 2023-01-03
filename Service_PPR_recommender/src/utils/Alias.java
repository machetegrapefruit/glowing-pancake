package utils;

public class Alias {
	private String uri;
	private String label;
	
	public Alias(String uri, String label) {
		this.uri = uri;
		this.label = label;
	}
	
	public String getURI() {
		return this.uri;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public String toString() {
		return "uri: " + uri + ", label: " + label;
	}
	
	@Override
	public boolean equals(Object o) {
		return this.uri.equals(((Alias) o).getURI()); 
	}
}
