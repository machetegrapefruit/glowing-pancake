package keyboards;

import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import utils.EmojiCodes;

public class PropertyValueRatingKeyboard implements Keyboard {

	private String[][] options;

	public PropertyValueRatingKeyboard(String propertyValue) {
		
		Map<String, String> emojis = EmojiCodes.getEmojis();
		
		options = new String[][] {
			new String[] {
				emojis.get(EmojiCodes.SLIGHT_SMILE) + " I like \"" + WordUtils.capitalize(propertyValue + "\"")	
			},
			new String[] {
				emojis.get(EmojiCodes.EXPRESSIONLESS) + " I dislike \"" + WordUtils.capitalize(propertyValue + "\"")
			},
			new String[] {
				emojis.get(EmojiCodes.THINKING) + " It's indifferent to me"
			}
		};
	}

	@Override
	public String[][] getOptions() {
		return options;
	}
	
}
