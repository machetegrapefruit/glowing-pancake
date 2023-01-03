package miniconverse;

public class RecEntity {
	private String uri;
	private String label;
	private String image;
	private String trailer;
	public RecEntity(String uri, String label, String image, String trailer) {
		super();
		this.uri = uri;
		this.label = label;
		this.image = image;
		this.trailer = trailer;
	}
	public String getUri() {
		return uri;
	}
	public String getLabel() {
		return label;
	}
	public String getImage() {
		return image;
	}
	public String getTrailer() {
		return trailer;
	}
	
	
}
