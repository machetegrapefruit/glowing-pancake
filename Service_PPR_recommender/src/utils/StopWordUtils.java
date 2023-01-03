package utils;

public class StopWordUtils {
	public static String removeStopWords(String message, String[] stopWords) {
		String result = message;
		for (String word: stopWords) {
			result = result.replaceAll("(?i)" + word, "");
		}
		return result;
	}
	
	public static void main(String[] args) {
		String[] stopWords = {"movie"};
		System.out.println(StopWordUtils.removeStopWords("I really like this MovIe", stopWords));
	}
}
