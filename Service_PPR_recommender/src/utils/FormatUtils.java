package utils;

public class FormatUtils {

	public static String truncateID(String userID) {
		if (userID.length() > 10) {
			userID = userID.substring(userID.length() - 10);
		}
		return userID;
	}
	
	public static String correctPhotoRes(String photoURL) {
		return photoURL.replaceFirst("w[0-9]+", "w500");
	}
}
