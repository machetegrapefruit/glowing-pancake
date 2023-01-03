package utils;

public abstract class URIUtils {
	
	/**
	 * 
	 * @param name
	 * @return The URI of the provided entity name, or null if the name is not valid,
	 */
	public static String entityNameToURI(String name) {
		
		String uri = "";
		String movieCamera = EmojiCodes.getEmojis().get("movie_camera");
		if (name == null || name.isEmpty() || name.equals("null")) {
			uri = null;
		} else {
			uri = name.replace(" ", "_")
					.replace(movieCamera + "_", "")
					.replace(movieCamera, "");
			uri = "http://dbpedia.org/resource/" + uri;
		}
		
		return uri;
	}
	
	public static String getNameFromURI(String uri) {
		String[] uriSplit = uri.split("/");
		String name = uriSplit[uriSplit.length - 1].replace("_", " ");
		return name;
	}
}

