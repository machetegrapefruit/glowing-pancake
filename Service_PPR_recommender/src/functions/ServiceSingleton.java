package functions;

public class ServiceSingleton {
	private static ResponseService responseService;
	private static EntityService entityService;
	private static ProfileService profileService;
	private static LogService logService;
	private static PropertyService propertyService;
	
	public static ResponseService getResponseService() {
		if (responseService == null) {
			responseService = new ResponseService();
		}
		return responseService;
	}
	
	public static EntityService getEntityService() {
		if (entityService == null) {
			entityService = new EntityService();
		}
		return entityService;
	}
	
	public static ProfileService getProfileService() {
		if (profileService == null) {
			profileService = new ProfileService();
		}
		return profileService;
	}
	
	public static PropertyService getPropertyService() {
		if (propertyService == null) {
			propertyService = new PropertyService();
		}
		return propertyService;
	}
}
