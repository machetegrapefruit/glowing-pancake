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

import configuration.Configuration;
import dialog.DialogState;
import entity.Entity;
import entity.PropertyRatingManager;
import functions.EntityService;
import functions.ProfileService;
import functions.PropertyService;
import functions.ResponseService;
import restService.GetRatings;
import utils.EmojiCodes;
import utils.SortableMap;
import utils.SortableMap.SortType;
import utils.TextUtils;

public class PropertyRatingKeyboard implements Keyboard {
	
	private Map<String, List<String>> properties;

	private String[][] options;
		
	public PropertyRatingKeyboard(String userID, DialogState state) throws Exception {
		
		properties = new HashMap<String, List<String>>();
		
		PropertyService propertyService = new PropertyService();
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
		
		Map<String, String> emojis = EmojiCodes.getEmojis();
		String recommendButton = emojis.get("globe_with_meridians") + " Recommend " + rs.getEntityTypePluralMessage(false);
		String entitiesButton = emojis.get("blue_circle") + " " + rs.getEntityTypePluralMessage(false);
		String profileButton = emojis.get("gear") + " Profile";
		String moreButton = "More " + emojis.get("point_right");
		String homeButton = emojis.get(EmojiCodes.BACKARROW) + " Home";
		
		EntityService entityService = new EntityService();
		
		List<String> topPropertyTypes = new ArrayList<String>();
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
				topPropertyTypes.add(type);
			}
			
			PropertyRatingManager.putProposedValueURI(WordUtils.capitalize(TextUtils.getNormalizedPropertyType(type)), TextUtils.getURIFromType(type));
		}
		
		System.out.println(topPropertyTypes);
		topPropertyTypes.replaceAll(new UnaryOperator<String>() {

			@Override
			public String apply(String t) {
				Map<String, String> labels = Configuration.getDefaultConfiguration().getPropertyTypesLabels();
				return WordUtils.capitalize(labels.get(t));
			}
			
		});
		
		GetRatings service = new GetRatings();
		String ratedEntities = service.getNumberRatedEntities(userID);
		String ratedProperties = service.getNumberRatedProperties(userID);
		int ratingsNeeded = 3 - Integer.parseInt(ratedEntities) - Integer.parseInt(ratedProperties);
		
		List<String> firstRowList = new ArrayList<String>();
		List<String> secondRowList = new ArrayList<String>();
		for (int i = 0; i < topPropertyTypes.size() && i < 2; i++) {
			firstRowList.add(topPropertyTypes.get(i));
		}
		for (int i = 2; i < topPropertyTypes.size() && i < 4; i++) {
			secondRowList.add(topPropertyTypes.get(i));
		}
		
		String[] firstRow = new String[firstRowList.size()];
		String[] secondRow = new String[secondRowList.size()];
		for (int i = 0; i < firstRow.length; i++) {
			firstRow[i] = firstRowList.get(i);
		}
		for (int i = 0; i < secondRow.length; i++) {
			secondRow[i] = secondRowList.get(i);
		}
		
		List<String> thirdRow = new ArrayList<String>();
		thirdRow.add(entitiesButton);
		thirdRow.add(profileButton);
		if (topPropertyTypes.size() > 4) {
			thirdRow.add(moreButton);
		}
		if (!state.isGenericProperties()) {
			thirdRow.add(homeButton);
		}

		if (ratingsNeeded > 0 || !(new ProfileService()).hasPositiveRating(userID)) {
	
			options = new String[][] {
				firstRow,
				secondRow,
				thirdRow.toArray(new String[thirdRow.size()])
			};
			
		} else {
			
			options = new String[][] {
				new String[] {recommendButton},
				firstRow,
				secondRow,
				thirdRow.toArray(new String[thirdRow.size()])
			};
		}
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}

}
