package entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;

import restService.GetAllPropertyListFromEntity;

public class PropertyRatingManager {

	/**
	 * Key: Testo come viene mostrato nel button
	 * Value: URI grezzo
	 */
	private static Map<String, String> proposedValueURIs;
	
	static {
		proposedValueURIs = new HashMap<String, String>();
	}
	
	public static boolean contains(String key) {
		return proposedValueURIs.containsKey(key);
	}
	
	public static void putProposedValueURI(String text, String uri) {
		proposedValueURIs.put(text, uri);
	}
	
	public static String getProposedValueURI(String text) {
		String value = proposedValueURIs.get(text);
		if (value == null) {
			value = proposedValueURIs.get(text.toLowerCase());
		}
		if (value == null) {
			value = proposedValueURIs.get(WordUtils.capitalize(text));
		}
		return value;
	}
	
	public static void __debug() {
		System.out.println(proposedValueURIs);
		
		GetAllPropertyListFromEntity get = new GetAllPropertyListFromEntity();
		try {
			String data = get.getAllPropertyListFromEntity("Pulp fiction");
			System.out.println(data);
			String data2 = get.getAllPropertyListFromEntity("pulp fiction");
			System.out.println(data2);

			String data4 = get.getAllPropertyListFromEntity("Pulp Fiction");
			System.out.println(data4);

			String data5 = get.getAllPropertyListFromEntity("Pulp_Fiction");
			System.out.println(data5);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
