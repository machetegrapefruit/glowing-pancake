package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.text.WordUtils;

import configuration.Configuration;
import functions.EntityService;

public class TextUtils {
	
	private static Map<String, String> typeEmoji;
	private static Map<String, String> emojis = EmojiCodes.getEmojis();
	
	static {
		typeEmoji = new HashMap<String, String>();
		loadTypeEmojiMap();
	}
	
	private static void loadTypeEmojiMap() {
		
		typeEmoji.put("director", emojis.get(EmojiCodes.CLAPPER));
		typeEmoji.put("actor", emojis.get(EmojiCodes.MAN_LEVITATING));
		typeEmoji.put("performer", emojis.get(EmojiCodes.MAN_LEVITATING));
		typeEmoji.put("category", emojis.get(EmojiCodes.VHS));
		typeEmoji.put("album", emojis.get(EmojiCodes.VHS));
		typeEmoji.put("record label", emojis.get(EmojiCodes.VHS));
		typeEmoji.put("genre", emojis.get(EmojiCodes.FILM_FRAME));
		typeEmoji.put("writer", emojis.get(EmojiCodes.PEN));
		typeEmoji.put("author", emojis.get(EmojiCodes.PEN));
		typeEmoji.put("producer", emojis.get(EmojiCodes.MONEY_BAG));
		typeEmoji.put("musicComposer", emojis.get(EmojiCodes.MUSICAL_SCORE));
		typeEmoji.put("composer", emojis.get(EmojiCodes.MUSICAL_SCORE));
		typeEmoji.put("music composer", emojis.get(EmojiCodes.MUSICAL_SCORE));
		typeEmoji.put("cinematography", emojis.get(EmojiCodes.CAMERA));
		typeEmoji.put("basedOn", emojis.get(EmojiCodes.NOTEBOOK_WITH_DECORATIVE_COVER));
		typeEmoji.put("based on", emojis.get(EmojiCodes.NOTEBOOK_WITH_DECORATIVE_COVER));
		typeEmoji.put("editing", emojis.get(EmojiCodes.BRIEFCASE));
		typeEmoji.put("distributor", emojis.get(EmojiCodes.OFFICE));	
		typeEmoji.put("releaseYear", emojis.get(EmojiCodes.CALENDAR));
		typeEmoji.put("release year", emojis.get(EmojiCodes.CALENDAR));
		typeEmoji.put("runtimeRange", emojis.get(EmojiCodes.CLOCKFLAT));
		typeEmoji.put("runtime range", emojis.get(EmojiCodes.CLOCKFLAT));
		typeEmoji.put("runtime minutes", emojis.get(EmojiCodes.CLOCKFLAT));
		typeEmoji.put("photography", emojis.get(EmojiCodes.CAMERA));
		typeEmoji.put("release date", emojis.get(EmojiCodes.CLOCKFLAT));
		typeEmoji.put("releaseDate", emojis.get(EmojiCodes.CLOCKFLAT));		
		typeEmoji.put("plot", emojis.get(EmojiCodes.CLIPBOARD));
		typeEmoji.put("description", emojis.get(EmojiCodes.CLIPBOARD));
		typeEmoji.put("country", emojis.get(EmojiCodes.BRIEFCASE));
		typeEmoji.put("MusicBrainz ID", emojis.get(EmojiCodes.STAR));
		typeEmoji.put("awards", emojis.get(EmojiCodes.TROPHY));
		typeEmoji.put("language", emojis.get(EmojiCodes.NOTEBOOK_WITH_DECORATIVE_COVER));
		typeEmoji.put("publisher", emojis.get(EmojiCodes.OFFICE));
		typeEmoji.put("ISBN-13", emojis.get(EmojiCodes.PEN));
		typeEmoji.put("ISBN-10", emojis.get(EmojiCodes.PEN));
		typeEmoji.put("subject", emojis.get(EmojiCodes.VHS));
	}
	
	public static String getPropertyTypeFromEmoji(String emoji) {
		Map<String, String> emojis = Configuration.getDefaultConfiguration().getPropertyTypeEmojis();
		String property = null;
		for (Entry<String, String> entry : emojis.entrySet()) {
			if (entry.getValue().equals(emoji)) {
				property = entry.getKey();
			}
		}
		System.out.println("getPropertyTypeFromEmoji: " + property);
		return property;
	}

	public static boolean isPropertyType(String plainPropertyType) {

		plainPropertyType = plainPropertyType.toLowerCase();
		boolean result = false;
		
		Map<String, String> propertyTypesLabels = Configuration.getDefaultConfiguration().getPropertyTypesLabels();
		for (Entry<String, String> entry : propertyTypesLabels.entrySet()) {
			if (entry.getValue().contains(plainPropertyType) ||
					plainPropertyType.contains(entry.getValue())) {
				result = true;
			}
		}
		// Se text contiene of, è una proprietà di
		// un'entità specifica!
		result &= !plainPropertyType.contains("of");
		
		return result;
	}

	/**
	 * Estrae il tipo di una proprietà. <br/>
	 * <i>Esempio:
	 * text = "Actor" restituisce "starring"</i>
	 * @param text testo deve rappresentare il tipo di una proprietà, altrimenti questo metodo
	 * può restituire risultati imprevisti
	 * @return
	 */
	public static String getNormalizedPropertyType(String plainPropertyType) {
		
		plainPropertyType = plainPropertyType.toLowerCase();
		String normalizedPropertyType = null;
		
		Map<String, String> propertyTypesLabels = Configuration.getDefaultConfiguration().getPropertyTypesLabels();
		for (Entry<String, String> entry : propertyTypesLabels.entrySet()) {
			if (plainPropertyType.contains(entry.getValue()) ||
					entry.getValue().contains(plainPropertyType)) {
				normalizedPropertyType = entry.getValue();
			}
		}
		
		return normalizedPropertyType;
	}

	public static boolean isPropertyValue(String text) {
		text = text.toLowerCase();
		boolean result = false;
		Map<String, String> emojis = Configuration.getDefaultConfiguration().getPropertyTypeEmojis();
		for (Entry<String, String> entry : emojis.entrySet()) {
			if (text.startsWith(entry.getValue())) {
				result = true;
			}
		}
		
		System.out.println("result: " + result);
		return result;
	}
	
	/**
	 * Estrae il valore di una proprietà dal testo di un pulsante.
	 * @param text testo deve rappresentare il valore di una proprietà, altrimenti questo metodo
	 * può restituire risultati imprevisti.
	 * @return
	 */
	public static String getPropertyValue(String text) {

		String result = null;
		if (text.matches(". .+")) {
			result = text.substring(text.indexOf(" ") + 1);
		} else {
			result = text.substring(text.indexOf(":") + 1);

		}
		result = result.trim();
		
		return result;
	}
	
	public static String addEmoji(String text, String propertyType) {
		
		String result = null;
		
		String emoji = typeEmoji.get(propertyType);
		result = emoji + " " + text;
		
		return result;
	}
	
	public static String getEmoji(String propertyType) {
		
		return Configuration.getDefaultConfiguration().getPropertyTypeEmojis().get(propertyType);
	}
	
	public static String getNameFromURI(String uri) {
		
		EntityService entityService = new EntityService();
		
		String result = getNameFromURIKeepBrackets(uri);
		String qURI = entityService.getEntityURI(result);
		if (qURI == null || qURI.isEmpty() || qURI.equals("null")) {
			result = getNameFromURIDeleteBrackets(uri);
		}
		return result;
	}
	
	public static String getNameFromURIKeepBrackets(String uri) {
		
		String result = uri.replace("http://dbpedia.org/resource/", "")
				.replace("_", " ");
		
		if (result.contains(":")) {
			String prefix = result.substring(0, result.indexOf(":"));
			if (isPropertyType(prefix)) {
				result = result.substring(result.indexOf(":") + 1);
			}
		}

		System.out.println("Result getNameKeepBrackets: " + result);
		return result;
	}
	
	public static String getNameFromURIDeleteBrackets(String uri) {
		
		String result = getNameFromURIKeepBrackets(uri);
		
		if (result.contains("(")) {
			result = result.substring(0, result.indexOf("(") - 1);
		}

		System.out.println("ResultgetNameDeleteBrackets: " + result);
		return result;
	}
	
	public static String getURIFromType(String type) {
		
		String result = null;
		switch (type.toLowerCase()) {
		case "category":
			result = "http://purl.org/dc/terms/subject";
			break;
		case "genre":
			result = "genre";
			break;
		case "title":
			result = "title";
			break;
		case "runtime":
			result = "runtimeMinutes";
			break;
		case "plot":
			result = "plot";
			break;
		default:
			result = "http://dbpedia.org/ontology/" + getNormalizedPropertyType(type.toLowerCase());
		}
		return result;
	}
	
	public static String getURIFromResource(String resource) {
		
		return getURIFromResourceCapitalize(resource).toLowerCase();
	}
	
	public static String getURIFromResourceCapitalize(String resource) {
		
		return "http://dbpedia.org/resource/" + resource.replace(" ", "_");
	}

	public static String getPropertyValueFromURI(String propertyValueURI) {
		
		String result = null;
		propertyValueURI = propertyValueURI.toLowerCase();
		result = propertyValueURI.replace("http://purl.org/dc/terms/subject", "")
				.replace("http://dbpedia.org/ontology/", "")
				.replace("http://dbpedia.org/resource/category:", "")
				.replace("http://dbpedia.org/resource/", "")
				.replace("_", " ");
		result = WordUtils.capitalize(result);
		return result;
	}
	
	@Deprecated
	public static String getPropertyTypeFromURI(String uri) {
		String result = null;
		
		uri = uri.toLowerCase()
				.replace("http://dbpedia.org/ontology/", "");
		if (uri.equals("http://purl.org/dc/terms/subject")) {
			result = "category";
		} else {
			result = uri;
		}
		return result;
	}

	public static boolean isEntityPropertyType(String plainPropertyType) {
		
		boolean result = false;
		
		plainPropertyType = plainPropertyType.toLowerCase();
		
		Map<String, String> propertyTypesLabels = Configuration.getDefaultConfiguration().getPropertyTypesLabels();
		for (Entry<String, String> entry : propertyTypesLabels.entrySet()) {
			if (plainPropertyType.contains(entry.getValue())) {
				result = true;
			}
		}
		
		result &= plainPropertyType.contains("of");
				
		return result;
	}

	public static String getEntityPropertyType(String text) {
		text = text.toLowerCase();
		
		String propertyType = null;
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("director", "director");
		map.put("star", "actor");
		map.put("actor", "actor");
		map.put("categor", "category");
		map.put("genre", "genre");
		map.put("writer", "writer");
		map.put("producer", "producer");
		map.put("music", "music composer");
		map.put("cinematograph", "cinematograph");
		map.put("photograph", "photography");
		map.put("distributor", "distributor");
		map.put("release", "release year");
		map.put("runtime", "runtime minutes");
		map.put("perform", "performer");
		map.put("sing", "performer");
		map.put("artist", "performer");
		map.put("album", "album");		
		map.put("composer", "composer");		
		
		for (Entry<String, String> entry : map.entrySet()) {
			if (text.contains(entry.getKey())) {
				propertyType = entry.getValue();
			}
		}
		
		if (propertyType == null) {
			// Cerca nei tipi del file configuration
			for (String label : Configuration.getDefaultConfiguration().getPropertyTypesLabels().values()) {
				if (text.contains(label) ||
						label.contains(text)) {
					propertyType = label;
				}
			}
		}
		
		return propertyType;
	}
	
	public static String getPropertyTypeURI(String propertyType) {
		String propertyTypeURI = null;
		for (Entry<String, String> entry : Configuration.getDefaultConfiguration().getPropertyTypesLabels().entrySet()) {
			if (propertyType.equals(entry.getValue())) {
				propertyTypeURI = entry.getKey();
			}
		}
		return propertyTypeURI;
	}
}
