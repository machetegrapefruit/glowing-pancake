package replies;

import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import dialog.DialogState;
import entity.AuxAPI;
import entity.Message;
import entity.Property;
import entity.ReplyMarkup;
import functions.LogService;
import functions.ProfileService;
import functions.PropertyService;
import functions.ResponseService;
import keyboards.StartProfileAcquisitionKeyboard;
import keyboards.UserPropertyValueKeyboard;
import restService.GetRatings;
import utils.EmojiCodes;
import utils.TextUtils;
import utils.UserRating;

public class UserPropertyValueRatingReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
	
	public UserPropertyValueRatingReply(String userID, UserRating rating,
			DialogState state, LogService logService) throws Exception {
		
		Property propertyToRate = state.getPropertyToRate();
		PropertyService propertyService = new PropertyService();
		
		String symbol = null;
		switch (rating) {
		case LIKE: symbol = "+"; break;
		case DISLIKE: symbol = "-"; break;
		case INDIFFERENT: symbol = "/"; break;
		}
		logService.addRecognizedObject(propertyService.getPropertyURI(propertyToRate.getValue()) + symbol);

		String propertyValue = propertyToRate.getValue();
		String propertyType = propertyToRate.getType();
		
		System.out.println("propertyType: " + propertyType + "\n" + "propertyValue: " + propertyValue);
		
		String replyMessage = null;
		int serviceCompatibleRating = -1;

		switch (rating) {
		case LIKE:
			serviceCompatibleRating = 1;
			replyMessage = "You have rated \"" + WordUtils.capitalize(TextUtils.getPropertyValueFromURI(propertyValue)) + "\"";
			break;
		case DISLIKE:
			serviceCompatibleRating = 0;
			replyMessage = "You have rated \"" + WordUtils.capitalize(TextUtils.getPropertyValueFromURI(propertyValue)) + "\"";
			break;
		case INDIFFERENT:
			serviceCompatibleRating = 2;
			replyMessage = "You have rated Indifferent \"" + WordUtils.capitalize(TextUtils.getPropertyValueFromURI(propertyValue)) + "\"";
			break;
		}
		
		String propertyValueURI = propertyService.getPropertyURI(propertyValue);
		String propertyTypeURI = state.getPropertyToRate().getType();
		propertyService.addPropertyPreference(userID, propertyValueURI, propertyTypeURI, serviceCompatibleRating, "user");
		state.setPropertyToRate(null);
		
		GetRatings getter = new GetRatings();
		int ratingsNeeded = 3 - Integer.parseInt(getter.getNumberRatedEntities(userID)) - Integer.parseInt(getter.getNumberRatedProperties(userID));

		boolean hasPositiveRating = (new ProfileService()).hasPositiveRating(userID);
		
		ResponseService responseService = new ResponseService();
		Map<String, String> emojis = EmojiCodes.getEmojis();

		if (ratingsNeeded > 0) {
			replyMessage += "\nI need " + ratingsNeeded + " more ratings " + emojis.get(EmojiCodes.SLIGHT_SMILE);
		} else if (!hasPositiveRating) {
			replyMessage += "\nI need at least a positive preference to recommend you " + responseService.getEntityTypePluralMessage(true);
		} else {
			replyMessage += "\nProfile updated with " + getter.getNumberRatedProperties(userID) + " rated properties";
		}

		ReplyMarkup markup = null;
		
		String otherMessage = null;
		if (ratingsNeeded > 0 || !hasPositiveRating) {
			otherMessage = "Do you want to tell me something else about you?";
			markup = new ReplyMarkup(new StartProfileAcquisitionKeyboard());
		} else {
			// aggiungere la possibilità che l'utente si trovi in refine di una entity
			// quindi if (state.getEntityToRefine != null) ....
			otherMessage = responseService.getLetMeRecommendMessage();
			markup = new ReplyMarkup(new UserPropertyValueKeyboard());
		}

		messages = new Message[] {
				new Message(replyMessage),
				new Message(otherMessage)
		};
		
		replyMarkup = markup;
	}
	
	@Override
	public Message[] getMessages() {
		return messages;
	}

	@Override
	public ReplyMarkup getReplyMarkup() {
		return replyMarkup;
	}

	@Override
	public AuxAPI getAuxAPI() {
		return null;
	}

}
