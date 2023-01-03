package keyboards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;

import org.apache.commons.lang.WordUtils;

import com.google.gson.Gson;

import configuration.Configuration;
import dialog.DialogState;
import entity.Entity;
import entity.PropertyRatingManager;
import functions.EntityService;
import functions.PropertyService;
import functions.ResponseService;
import restService.GetPropertyValueListFromPropertyType;
import utils.EmojiCodes;
import utils.SortableMap;
import utils.SortableMap.SortType;
import utils.TextUtils;

public class MoreKeyboard implements Keyboard {

	private String[][] options;
	
	public MoreKeyboard(String userID, DialogState state) throws Exception {
		
		
		Map<String, String> emojis = EmojiCodes.getEmojis();
		
		List<String> optionsList = new ArrayList<String>();
		
		Map<String, List<String>> properties = new HashMap<String, List<String>>();
		
		PropertyService propertyService = new PropertyService();
		EntityService entityService = new EntityService();
		ResponseService rs = new ResponseService();

		List<String> types = Arrays.asList(Configuration.getDefaultConfiguration().getPropertyTypesForExplanation());
		for (String type : types) {
			properties.put(type, propertyService.getPropertyValues(type));
		}
		
		SortableMap<String, Integer> propertyTypePopularity = new SortableMap<String, Integer>();
		propertyTypePopularity.setSortType(SortType.INCREASING);
		for (Entry<String, List<String>> entry : properties.entrySet()) {
			propertyTypePopularity.put(WordUtils.capitalize(entry.getKey()), entry.getValue().size());
		}
		
		for (Iterator<Entry<String, Integer>> iterator = propertyTypePopularity.iterator(); iterator.hasNext();) {
			Entry<String, Integer> entry = iterator.next();
			String type = entry.getKey();
			System.out.println("Type: " + type + ", popularity: " + entry.getValue());

			Entity entity = entityService.getEntityDetails(entityService.getEntityURI(state.getEntityToRate()));
			System.out.println("entity: " + entity);
			List<String> values = null;
			if (state.isGenericProperties()) {
				values = propertyService.getPropertyValues(type);
			} else {
				values = entity.get(type);
			}
			System.out.println("type: " + type);
			System.out.println("values: " + values);
			if (values != null) {
				optionsList.add(type);
			}
			
			PropertyRatingManager.putProposedValueURI(WordUtils.capitalize(TextUtils.getNormalizedPropertyType(type)), TextUtils.getURIFromType(type));
		}
		
		System.out.println(optionsList);
		optionsList.replaceAll(new UnaryOperator<String>() {

			@Override
			public String apply(String t) {
				Map<String, String> labels = Configuration.getDefaultConfiguration().getPropertyTypesLabels();
				return WordUtils.capitalize(labels.get(t));
			}
			
		});
		
		optionsList = optionsList.subList(4, optionsList.size());
		
		options = new String[optionsList.size() + 2][];
		for (int i = 0; i < optionsList.size(); i++) {
			options[i] = new String[1];
			options[i][0] = optionsList.get(i);
		}
		options[options.length - 2] = new String[] {
				emojis.get(EmojiCodes.BACKARROW) + " Return to the short list"
		};
		options[options.length - 1] = new String[] {
			emojis.get(EmojiCodes.BLUE_CIRCLE) + " " + rs.getEntityTypePluralMessage(false),
			emojis.get(EmojiCodes.GEAR) + " Profile"
		};
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}

}
