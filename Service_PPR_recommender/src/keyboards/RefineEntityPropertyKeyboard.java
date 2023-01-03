package keyboards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import configuration.Configuration;
import entity.Entity;
import functions.EntityService;
import functions.ResponseService;
import utils.EmojiCodes;

public class RefineEntityPropertyKeyboard implements Keyboard {

	private String[][] options;
	
	private ArrayList<String> optionsList;
	
	private Map<String, List<String>> properties;
	
	/**
	 * 
	 * @param userID
	 * @param entity Plain name
	 * @throws Exception
	 */
	public RefineEntityPropertyKeyboard(String userID, String entity) throws Exception {
		
		ResponseService rs = new ResponseService();
		
		properties = new HashMap<String, List<String>>();
		
		EntityService entityService = new EntityService();
		Entity details = entityService.getEntityDetails(entityService.getEntityURI(entity));
		List<String> refinableTypes = Arrays.asList(Configuration.getDefaultConfiguration().getPropertyTypesForExplanation());
		
		for (String propertyType : refinableTypes) {
			List<String> values = details.get(propertyType);
			properties.put(propertyType, values);
		}
				
		optionsList = new ArrayList<String>();
		
		for (String type : properties.keySet()) {
			String plainType = Configuration.getDefaultConfiguration().getPropertyTypesLabels().get(type);
			if (properties.get(type) != null) {
				optionsList.add(WordUtils.capitalize(plainType) + " of \"" + entity + "\"");
			}
		}
	
		options = new String[optionsList.size() + 1][];
		for (int i = 0; i < optionsList.size(); i++) {
			options[i] = new String[] {
					optionsList.get(i)
			};
		}
		options[options.length - 1] = new String[] {
				EmojiCodes.getEmojis().get("backarrow") + " Back to " + rs.getEntityTypePluralMessage(false)
		};

	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}
}
