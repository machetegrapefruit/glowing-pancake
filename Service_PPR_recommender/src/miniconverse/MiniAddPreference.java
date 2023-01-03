package miniconverse;

import java.util.List;

import functions.EntityService;
import functions.PropertyService;

public class MiniAddPreference {
	public boolean addPreferences(String userID, List<MiniPreference> preferences) {
		boolean success = true;
		EntityService es = new EntityService();
		PropertyService ps = new PropertyService();
		
		for (MiniPreference p: preferences) {
			String uri = p.getItem();
			if (es.isEntity(uri)) {
				es.addEntityPreference(userID, uri, p.getRating(), "user");
			} else if (ps.isPropertyObject(uri)) {
				List<String> propertyTypes = ps.getPropertyTypes(uri).get(uri);
				for (String pt: propertyTypes) {
					ps.addPropertyPreference(
							userID,
							uri, 
							pt, 
							p.getRating(), 
							"user"
							);
				}
			} else {
				success = false;
			}
		}
		
		return success;
	}
}
