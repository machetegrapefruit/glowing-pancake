package keyboards;

import java.util.Map;

import functions.ProfileService;
import utils.EmojiCodes;

public class EntityRecommendationKeyboard implements Keyboard {
	
	private String[][] options;

	public EntityRecommendationKeyboard(String userID) {
		this(userID, 1);
	}
	
	public EntityRecommendationKeyboard(String userID, int page) {
	
		Map<String, String> emojis = EmojiCodes.getEmojis();
		String smileCode = emojis.get(EmojiCodes.SMILEY);
		String sadCode = emojis.get(EmojiCodes.SLIGHTLY_SAD);
		String cycloneCode = emojis.get(EmojiCodes.CYCLONE);
		String bookmarkCode = emojis.get(EmojiCodes.BOOKMARK_TAGS);
		String megaphoneCode = emojis.get(EmojiCodes.MEGAPHONE);
		String pointLeftCode = emojis.get(EmojiCodes.POINT_LEFT);
		String pointRightCode = emojis.get(EmojiCodes.POINT_RIGHT);
		String backarrowCode = emojis.get(EmojiCodes.BACKARROW);
		String greenBookCode = emojis.get(EmojiCodes.GREEN_BOOK);
		String silhouetteCode = emojis.get(EmojiCodes.SILHOUETTE);
		String angerCode = EmojiCodes.hexHtmlSurrogatePairs.get(EmojiCodes.ANGER);
		
		ProfileService profileService = new ProfileService();
		
		options = new String[4][];
		options[0] = 
			new String[] {
					smileCode + " Like",
					sadCode + " Dislike",
					cycloneCode + " Like, but..."
			};
			
		options[1] = 
			new String[] {
					bookmarkCode + " Details",
					megaphoneCode + " Why?"
			};
		
		options[3] = 
			new String[] {
					backarrowCode + " Home",
					greenBookCode + " Help",
					silhouetteCode + " Profile"
			};
		
		switch (page) {
		case 1:
			options[2] = new String[] {
					"Next " + (page + 1) + " " + pointRightCode
			};
			break;
		case 2:
		case 3:
		case 4:
			options[2] = new String[] {
					pointLeftCode + " Back " + (page - 1),
					"Next " + (page + 1) + " " + pointRightCode
			};
			break;
		case 5:
			if (profileService.getNumRatedRecommendedEntities(userID) == 0) {
				options[2] = new String[] {
						pointLeftCode + " Back " + (page - 1),
						angerCode + " Change"
				};
			} else {
				options[2] = new String[] {
						pointLeftCode + " Back " + (page - 1),
						"Next " + pointRightCode
				};
			}
			break;
		}
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}
	
}
