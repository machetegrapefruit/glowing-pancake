package keyboards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.text.WordUtils;

import com.google.gson.Gson;

import configuration.Configuration;
import entity.PropertyRatingManager;
import functions.EntityService;
import functions.PropertyService;
import functions.ResponseService;
import replies.ProfileReply;
import replies.ProfileReply.ProfileType;
import restService.GetRatings;
import utils.EmojiCodes;
import utils.TextUtils;

public class ProfileKeyboard implements Keyboard {

	private String[][] options;
	
	@SuppressWarnings("unchecked")
	public ProfileKeyboard(String userID, ProfileType context) throws Exception {

		GetRatings getter = new GetRatings();
		String ratings = getter.getAllEntityOrPropertyRatings(userID);
		
		List<String> options = new ArrayList<String>();
		
		Gson gson = new Gson();

		Map<String, Double> map = null;
		if (!ratings.equals("\"null\"")) {

			map = (Map<String, Double>) gson.fromJson(ratings, Map.class);

			System.out.println("map: " + map);
			
			for (Entry<String, Double> propertyRating : map.entrySet()) {
				String key = propertyRating.getKey();
				System.out.println("Key: " + key);

				Integer rating = propertyRating.getValue().intValue();
				String[] property = key.split(",");
				String entityOrProperty = property[0];
				String entityOrPropertyObjectUri = property[1];
				String label = null;
				
				EntityService eService = new EntityService();
				PropertyService pService = new PropertyService();
				String emoji = null;
								
				if (entityOrProperty.equals("entity")) {
					label = eService.getEntityLabel(entityOrPropertyObjectUri);
					emoji = Configuration.getDefaultConfiguration().getEntityEmoji();
				} else {
					label = pService.getPropertyLabel(entityOrPropertyObjectUri);
					emoji = TextUtils.getEmoji(entityOrProperty);
				}
				System.out.println(label + " : " + entityOrPropertyObjectUri + " : " + rating);

				PropertyRatingManager.putProposedValueURI(entityOrProperty, label);

				System.out.println("entityOrProperty: " + entityOrProperty);
				String option = composeOption(entityOrProperty, label, rating);

				System.out.println("option: " + option);
				if (emoji == null) { // se è un'entità
					emoji = Configuration.getDefaultConfiguration().getEntityEmoji();
				}
				option = emoji + " " + option;
				
				options.add(option);
			}
		}
		
		ResponseService rs = new ResponseService();

		String backarrowCode = EmojiCodes.hexHtmlSurrogatePairs.get("backarrow");
		String orangeBookCode = EmojiCodes.hexHtmlSurrogatePairs.get("orange_book");
		String xCode = EmojiCodes.hexHtmlSurrogatePairs.get("heavy_x");
		
		this.options = new String[options.size() + 2][1];
		for (int i = 0; i < options.size(); i++ ) {
			this.options[i][0] = options.get(i);
		}
		
		String[] firstRow = null;
		if (context.equals(ProfileType.REC)) {
			firstRow = new String[] {
					backarrowCode + " Home", 
					backarrowCode + " Back to " + rs.getEntityTypePluralMessage(false)
			};
		} else {
			firstRow = new String[] {
					backarrowCode + " Home"
			};
		}
		
		String[] secondRow = null;
		if (map != null) {
			secondRow = new String[] {
					orangeBookCode + " Help",
					xCode + " Reset"
			};
		} else {
			secondRow = new String[] {
					orangeBookCode + " Help"
			};
		}
		
		this.options[this.options.length - 2] = firstRow;
		this.options[this.options.length - 1] = secondRow;
}
	
	private String composeOption(String propertyType, String propertyValue, Integer rating) {
		
		String option = null;
		String thumbsupCode = EmojiCodes.hexHtmlSurrogatePairs.get("thumbsup");
		String thumbsdownCode = EmojiCodes.hexHtmlSurrogatePairs.get("thumbsdown");
		String expressionlessCode = EmojiCodes.hexHtmlSurrogatePairs.get("expressionless");
		String slightSmileCode = EmojiCodes.hexHtmlSurrogatePairs.get("slight_smile");

		if (TextUtils.isPropertyType(propertyType)) {
			if (rating.intValue() == 1) {
				option = WordUtils.capitalize(propertyValue) + " - " + slightSmileCode;
			} else if (rating.intValue() == 0) {
				option = WordUtils.capitalize(propertyValue) + " - " + expressionlessCode;
			}
		} else {
			if (rating.intValue() == 1) {
				option = WordUtils.capitalize(propertyValue) + " - " + thumbsupCode;
			} else if (rating.intValue() == 0) {
				option = WordUtils.capitalize(propertyValue) + " - " + thumbsdownCode;
			}
		}
		
		return option;
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}

}
