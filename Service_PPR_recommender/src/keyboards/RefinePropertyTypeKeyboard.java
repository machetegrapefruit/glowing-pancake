package keyboards;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.text.WordUtils;

import configuration.Configuration;
import entity.Entity;
import functions.EntityService;
import functions.PropertyService;
import utils.EmojiCodes;
import utils.TextUtils;

public class RefinePropertyTypeKeyboard implements Keyboard {

	private String[][] options;
	
	public RefinePropertyTypeKeyboard(String entityName, String propertyType) throws Exception {
		
		EntityService entityService = new EntityService();
		Entity properties = entityService.getEntityDetails(entityService.getEntityURI(entityName));
		
		String propertyCode = null;
		Map<String, String> propertyTypesLabels = Configuration.getDefaultConfiguration().getPropertyTypesLabels();
		for (Entry<String, String> entry : propertyTypesLabels.entrySet()) {
			System.out.println("entry.getValue(): " + entry.getValue() + ", propertyType: " + propertyType);
			if (entry.getValue().equals(propertyType)) {
				propertyCode = entry.getKey();
			}
		}
		
		System.out.println(propertyCode);
		List<String> propertyValues = null;
		if (propertyCode != null) {
			propertyValues = properties.get(propertyCode);
			System.out.println(properties);
		}
		
		PropertyService propertyService = new PropertyService();
	
		if (propertyValues != null) {
			for (int i = 0; i < propertyValues.size(); i++) {
				if (propertyService.isPropertyObject(propertyValues.get(i))) {
					System.out.println("isPropertyObject");
					String entityLabel = propertyService.getPropertyLabel(propertyValues.get(i));
					System.out.println("label: " + entityLabel);
					entityLabel = TextUtils.addEmoji(entityLabel, propertyType);
					propertyValues.set(i, WordUtils.capitalize(entityLabel));
				}
			}
			
			options = new String[propertyValues.size() + 1][1];
			for (int i = 0; i < propertyValues.size(); i++) {
				options[i][0] = propertyValues.get(i);
			}
		} else {
			options = new String[1][1];
		}
		
		options[options.length - 1][0] = EmojiCodes.getEmojis().get(EmojiCodes.BACKARROW)
				+ " Return to the list of \"property\" of \"" + entityName + "\"";
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}

}
