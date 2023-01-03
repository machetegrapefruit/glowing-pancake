package dialog.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dialog.PendingConfirmation;
import functions.PropertyService;
import functions.ServiceSingleton;
import utils.Alias;
import utils.MatchedElement;

/**
 * Handles the addition of preferences when the user answers an entity-property type association
 * confirmation request.
 * @author Andrea Iovine
 *
 */
public class PropertyTypePreferenceFunction {
	private static final Logger LOGGER = Logger.getLogger(PropertyTypePreferenceFunction.class.getName());
	
	/**
	 * Adds the preferences from the confirmation request
	 * @param userID ID of the user
	 * @param pc Object containing the data of the current confirmation request
	 * @return and AddPreferenceFromConfirmationResponse object containing the feedback of the operation
	 */
	public AddPreferenceFromConfirmationResponse addPreferenceFromConfirmation(String userID, PendingConfirmation pc) {
		LOGGER.log(Level.INFO, "Called addPrefrenceFromConfirmation");
		AddPreferenceFromConfirmationResponse response = new AddPreferenceFromConfirmationResponse();
		Alias entity = pc.getEntity();
		Alias propertyType = pc.getPropertyType();
		List<Alias> propertyList = pc.getProperties();
		LOGGER.log(Level.INFO, "Entity is " + entity.getLabel() + ", propertyType is " + propertyType.getLabel());
		PropertyService ps = ServiceSingleton.getPropertyService();
		
		if (propertyList != null && !propertyList.isEmpty()) {
			//Add a preference for each of the properties found
			for (Alias property: propertyList) {
				if (ps.addPropertyPreference(userID, property.getURI(), propertyType.getURI(), pc.getRating(), "user")) {
					response.add(new MatchedElement(property, pc.getRating()));
				}
			}
		} else {
			response.setSuccess(false);
		}
		
		return response;
	}
	
	public static class AddPreferenceFromConfirmationResponse {
		/**
		 * True if the preferences were added correctly
		 */
		private boolean success;
		/**
		 * List of all the properties that were added
		 */
		private List<MatchedElement> added;
		
		public AddPreferenceFromConfirmationResponse() {
			this.success = true;
			this.added = new ArrayList<MatchedElement>();
		}
		
		public void add(MatchedElement a) {
			this.added.add(a);
		}
		
		public void setSuccess(boolean success) {
			this.success = success;
		}

		public boolean isSuccess() {
			return success;
		}

		public List<MatchedElement> getAdded() {
			return added;
		}
		
	}
}
