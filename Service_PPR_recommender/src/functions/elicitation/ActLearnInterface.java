package functions.elicitation;

public interface ActLearnInterface {
	/**
	 * Returns the ID of an entity or a property the user should give a rating to.
	 * Details on how the entity or property is chosen depend on the 
	 * specific implementation of the service
	 * @param userID ID of the user 
	 * @return The ID of an entity or a property
	 */
	public String GetEntityToRate(String userID);
}
