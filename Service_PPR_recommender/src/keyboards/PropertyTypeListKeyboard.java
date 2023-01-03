package keyboards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.text.WordUtils;

import com.google.gson.Gson;

import utils.EmojiCodes;

@Deprecated
public class PropertyTypeListKeyboard implements Keyboard {

	String[][] options;
	
	public PropertyTypeListKeyboard(String propertyTypeListJson) {
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, ArrayList<String>> properties = gson.fromJson(propertyTypeListJson, Map.class);
		
		Map<String, String> propertyArray = new HashMap<String, String>();
		
		System.out.println("[PropertyTypeListKeyboard] properties: \n" + properties);
		for (Entry<String, ArrayList<String>> entry : properties.entrySet()) {
			String property = entry.getKey();
			String name = replaceURIWithName(property);
			ArrayList<String> propertyTypeArray = entry.getValue();
			for (String propertyTypeArrayEntry : propertyTypeArray) {
				String propertyTypeName = replaceURIWithName(propertyTypeArrayEntry);
				propertyArray.put(propertyTypeName, name);
			}
		}
		
		String backarrowCode = EmojiCodes.hexHtmlSurrogatePairs.get("backarrow");
		
		List<String> options = new ArrayList<String>();
		options.addAll(getOptionsFromPropertyArrayAsPropertyTypeToPropertyValue(propertyArray));
		options.add(backarrowCode + " Go to the list of Properties");
		
		this.options = new String[options.size()][1];
		for (int i = 0; i < this.options.length; i++) {
			this.options[i][0] = options.get(i);
		}
	}
	
	private String capitalize(String input) {
	   return WordUtils.capitalize(input);
	}
	
	private List<String> getOptionsFromPropertyArrayAsPropertyTypeToPropertyValue(Map<String, String> properties) {
		
		List<String> result = new ArrayList<String>();
		
		String clapperCode = EmojiCodes.hexHtmlSurrogatePairs.get("clapper");
		String manLevitatingCode = EmojiCodes.hexHtmlSurrogatePairs.get("man_levitating");
		String vhsCode = EmojiCodes.hexHtmlSurrogatePairs.get("vhs");
		String filmframeCode = EmojiCodes.hexHtmlSurrogatePairs.get("film_frame");
		String penCode = EmojiCodes.hexHtmlSurrogatePairs.get("pen");
		String moneyBagCode = EmojiCodes.hexHtmlSurrogatePairs.get("money_bag");
		String musicalScoreCode = EmojiCodes.hexHtmlSurrogatePairs.get("musical_score");
		String cameraCode = EmojiCodes.hexHtmlSurrogatePairs.get("camera");
		String notebookCode = EmojiCodes.hexHtmlSurrogatePairs.get("notebook_with_decorative_cover");
		String briefcaseCode = EmojiCodes.hexHtmlSurrogatePairs.get("briefcase");
		String officeCode = EmojiCodes.hexHtmlSurrogatePairs.get("office");
		
		for (Entry<String, String> entry : properties.entrySet()) {
			String propertyType = entry.getKey();
			String propertyValue = entry.getValue();
			
			switch (propertyType) {
			case "/directors":
			case "directors":
			case "director":
				result.add(clapperCode + " " + capitalize(propertyValue) + " - Director");
				break;
			case "/starring":
			case "starring":
				result.add(manLevitatingCode + " " + capitalize(propertyValue) + " - Actor");
				break;
			case "/categories":
			case "categories":
			case "category":
			case "http://purl.org/dc/terms/subject":
				result.add(vhsCode + " " + capitalize(propertyValue) + " - Category");
				break;
			case "/genres":
			case "genres":
			case "genre":
				result.add(filmframeCode + " " + capitalize(propertyValue) + " - Genre");
				break;
			case "/writers":
			case "writers":
			case "writer":
				result.add(penCode + " " + capitalize(propertyValue) + " - Writer");
				break;
			case "/producers":
			case "producers":
			case "producer":
				result.add(moneyBagCode + " " + capitalize(propertyValue) + " - Producer");
				break;
			case "/music composers":
			case "music composers":
			case "music composer":
			case "musicComposer":
			case "music":
				result.add(musicalScoreCode + " " + capitalize(propertyValue) + " - Music composer");
				break;
			case "/cinematographies":
			case "cinematographies":
			case "cinematography":
				result.add(cameraCode + " " + capitalize(propertyValue) + " - Cinematography"); 
				break;
			case "/based on":
			case "based on":
			case "basedOn":
				result.add(notebookCode + " " + capitalize(propertyValue) + " - Based on");
				break;
			case "/editings":
			case "editings":
			case "editing":
				result.add(briefcaseCode + " " + capitalize(propertyValue) + " - Editor");
				break;
			case "/distributors":
			case "distributors":
			case "distributor":
				result.add(officeCode + " " + capitalize(propertyValue) + " - Distributor");
				break;
			default:
				break;
			}
		}
		return result;
	}
	
	private String replaceURIWithName(String uri) {
		String name = "";
		
		if (uri.startsWith("http://dbpedia.org/resource/")) {
			name = uri.replace("http://dbpedia.org/resource/", "");
		} else if (uri.startsWith("http://dbpedia.org/ontology/")) {
			name = uri.replace("http://dbpedia.org/ontology/", "");
		}
		name = name.replace("_", " ");
		
		return name;
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}
}
