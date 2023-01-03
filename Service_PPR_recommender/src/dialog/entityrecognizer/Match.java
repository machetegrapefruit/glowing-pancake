package dialog.entityrecognizer;

import dialog.entityrecognizer.IndexRange;

/**
 * The Match class describes an entity mention inside the user's message.
 * @author Andrea Iovine
 *
 */
public class Match implements Comparable<Match>{
	/**
	 * ID of the corresponding entity. May be null or the same as matchedName if
	 * this object represents a mention to a non-linkable entity (such as a person's name)
	 */
	private String entityID;
	
	/**
	 * The alias of the entity with which the mention has been matched. In particular, it's
	 * the alias that is the most similar to the mention.
	 */
	private String matchedName;
	
	/**
	 * Entity type of the mention
	 */
	private String entityType;
	
	/**
	 * Value that measures the similarity between the mention and the entity. The higher this
	 * measure, the higher the similarity. This value is used to compare different Match
	 * objects. Sorting a list of Match objects means that the objects will be sorted by 
	 * match value.
	 */
	private double match;
	private double lengthRatio;
	private IndexRange indexes;
	
	public Match(String entityID, String entityType, String matchedName, double match, double lengthRatio) {
		super();
		this.entityID = entityID;
		this.matchedName = matchedName;
		this.match = match;
		this.entityType = entityType;
		this.lengthRatio = lengthRatio;
	}
	public void setIndexes(IndexRange range) {
		this.indexes = range;
	}
	public IndexRange getIndexes() {
		return indexes;
	}
	public String getEntityID() {
		return entityID;
	}
	public String getEntityType() {
		return entityType;
	}
	public String getMatchedName() {
		return matchedName;
	}
	public double getMatch() {
		return match;
	}
	public double getLengthRatio() {
		return lengthRatio;
	}
	public String toString() {
		return matchedName + "(" + entityID + ")";
	}
	@Override
	public int compareTo(Match arg0) {
		if (this.match == 100 && arg0.match == 100) {
			if (this.lengthRatio > arg0.lengthRatio) {
				return 1;
			} else if (this.lengthRatio < arg0.lengthRatio) {
				return -1;
			} else {
				return 0;
			}
		} else {
			return (int) (this.match - arg0.match);
		}
	}
	
}
