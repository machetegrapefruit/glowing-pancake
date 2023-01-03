package test.convrecsys;

public class CRSEntity {
	private String id;
	private String label;
	private int rating;
	private boolean inDataset;
	public CRSEntity(String id, String label, int rating, boolean inDataset) {
		super();
		this.id = id;
		this.label = label;
		this.rating = rating;
		this.inDataset = inDataset;
	}
	public String getId() {
		return id;
	}
	public String getLabel() {
		return label;
	}
	public int getRating() {
		return rating;
	}
	public boolean isInDataset() {
		return inDataset;
	}
	@Override
	public String toString() {
		return "CRSEntity [id=" + id + ", rating=" + rating + "]";
	}
	
	
	
}
