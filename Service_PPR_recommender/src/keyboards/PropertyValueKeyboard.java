package keyboards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;

import org.apache.commons.lang3.text.WordUtils;

import configuration.Configuration;
import dialog.DialogState;
import entity.Entity;
import entity.PropertyRatingManager;
import functions.EntityService;
import functions.PropertyService;
import utils.EmojiCodes;

public class PropertyValueKeyboard implements Keyboard {

	String[][] options;
	
	public PropertyValueKeyboard(String userID, String propertyType, DialogState state) throws Exception {

		final PropertyService ps = new PropertyService();
		final EntityService entityService = new EntityService();
		String propertyTypeURI = null;
		
		Map<String, String> propertyTypesLabels = Configuration.getDefaultConfiguration().getPropertyTypesLabels();
		for (Entry<String, String> entry : propertyTypesLabels.entrySet()) {
			if (entry.getValue().equals(propertyType)) {
				propertyTypeURI = entry.getKey();
			}
		}
		
		List<String> properties = null;
		
		if (propertyTypeURI != null) {
			
			if (state.isGenericProperties()) {
				properties = ps.getPropertyValuesSortedByPopularity(propertyTypeURI);

			} else {
				Entity details = entityService.getEntityDetails(entityService.getEntityURI(state.getEntityToRate()));
				properties = details.get(propertyTypeURI);
				properties.replaceAll(new UnaryOperator<String>() {

					@Override
					public String apply(String t) {
						return ps.getPropertyLabel(t);
					}
					
				});
			}
			
		} else {	
			properties = new ArrayList<String>();
		}
		
		List<String> optionsList = new ArrayList<String>();

		Map<String, String> typeEmojis = Configuration.getDefaultConfiguration().getPropertyTypeEmojis();
		for (String property : properties) {
			PropertyRatingManager.putProposedValueURI(propertyType, property);
			optionsList.add(typeEmojis.get(propertyTypeURI) + " " + WordUtils.capitalize(property));
		}
		
		options = new String[optionsList.size() + 1][1];
		for (int i = 0; i < optionsList.size(); i++) {
			options[i] = new String[] {optionsList.get(i)};
		}
		Map<String, String> emojis = EmojiCodes.getEmojis();
		options[options.length - 1][0] = emojis.get(EmojiCodes.BACKARROW) + " Property types";
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}

}
