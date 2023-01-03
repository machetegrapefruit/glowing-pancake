package dialog;

import functions.EntityService;
import functions.PropertyService;
import functions.ServiceSingleton;

public class ItemSuggestion {
	private int ratedCount;
	private int suggestedCount;
	private String itemID;
	private String itemLabel;
	private boolean isEntity;
	private boolean asked;
	
	public ItemSuggestion(String itemID, int ratedCount, int suggestedCount) {
		this.itemID = itemID;
		this.ratedCount = ratedCount;
		this.suggestedCount = suggestedCount;
		EntityService es = ServiceSingleton.getEntityService();
		PropertyService ps = ServiceSingleton.getPropertyService();
		if (es.isEntity(itemID)) {
			this.isEntity = true;
			this.itemLabel = es.getEntityLabel(itemID);
		} else if (ps.isPropertyObject(itemID)) {
			this.isEntity = false;
			this.itemLabel = ps.getPropertyLabel(itemID);
		} else {
			throw new RuntimeException("Item is not an entity nor a property");
		}
	}
	
	public int getRatedCount() {
		return this.ratedCount;
	}
	
	public int getSuggestedCount() {
		return this.suggestedCount;
	}
	
	public String getID() {
		return this.itemID;
	}
	
	public String getLabel() {
		return this.itemLabel;
	}
	
	public boolean isEntity() {
		return this.isEntity;
	}
	
	public boolean isAsked() {
		return this.asked;
	}
	
	public void setAsked(boolean asked) {
		this.asked = asked;
	}
}
