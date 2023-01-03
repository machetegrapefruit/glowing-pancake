package functions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import configuration.Configuration;
import dialog.PendingConfirmation;
import dialog.PendingEvaluation;
import dialog.PendingEvaluation.PendingEvaluationType;
import dialog.functions.AddPreferenceFunction.AddPreferenceResponse;
import utils.Alias;
import utils.MatchedElement;
import utils.PropertyFilter;

/**
 * Handles the generation of responses from the resource file.
 * @author Andrea Iovine
 *
 */
public class ResponseService {
	private ResourceBundle messages; 
	private MessageFormat formatter;
	
	public ResponseService() {
		this.messages = ResourceBundle.getBundle("ApplicationResources", Locale.US);
		this.formatter = new MessageFormat("");
		this.formatter.setLocale(Locale.US);
		System.out.println(this.messages.getString("Test"));
	}
	
	public String getAbleToRecommendMessage() {
		return this.messages.getString("AbleToRecommend");
	}
	
	public String getLetMeRecommendMessage() {
		return this.messages.getString("LetMeRecommend");
	}
	
	public String getEndRecommendationMessage() {
		return this.messages.getString("EndRecommendation");
	}
	
	public String getStartSuggestionMessage() {
		return this.messages.getString("ItemSuggestionStart");
	}
	
	public String getGreetingMessage(String firstname) {
		return format("Greeting", new String[] {firstname});
	}
	
	public String getDefaultFailureMessage() {
		return this.messages.getString("DefaultFailure");
	}
	
	public String getMultipleMessage(String messageName) {
		String message = null;
		ArrayList<String> possibleMessages = new ArrayList<>();
		int i = 1;
		while (this.messages.containsKey(messageName + "." + i)) {
			possibleMessages.add(this.messages.getString(messageName + "." + i));
			i++;
		}
		if (i > 1) {
			int randomIndex = new Random().nextInt(i - 1);
			message = possibleMessages.get(randomIndex);
		}
		
		return message;
	}
	
	public String getAddedPreferencesMessage(MatchedElement addedEntity) {
		String[] array = {addedEntity.getElement().getLabel()};
		String response = this.messages.getString("AddedStart");
		if (addedEntity.getRating() == 1) {
			response += this.format("AddedPositive", array);
		} else {
			response += this.format("AddedNegative", array);
		}
		return response;
	}
	
	public String getInfoMessage() {
		return messages.getString("Info");
	}
	
	public String getStartMessage(String name, String neededRatings) {
		return format("Start", new String[] {
				name, 
				this.getEntityTypePluralMessage(true), 
				neededRatings,
				this.getEntityTypeSingularMessage(true)});
	}
	
	public String getAddRecommendationPreferencesMessage(String label, int rating, List<MatchedElement> addedProperties) {
		return getAddRecommendationPreferencesMessage(label, rating, new ArrayList<MatchedElement>(), addedProperties);
	}
	
	public String getAddRecommendationPreferencesMessage(String label, int rating, List<MatchedElement> addedEntities, List<MatchedElement> addedProperties) {
		addedEntities.add(0, new MatchedElement(new Alias("", label), rating));
		return getAddedPreferencesMessage(addedEntities, addedProperties);
	}
	
	public String getAddedPreferencesMessage(List<MatchedElement> addedEntities, List<MatchedElement> addedProperties) {
		StringJoiner positive = new StringJoiner(", ");
		StringJoiner negative = new StringJoiner(", ");
		String response = "";
		
		int addedElements = 0;
		if (addedEntities != null) {
			addedElements += addedEntities.size();
		}
		if (addedProperties != null) {
			addedElements += addedProperties.size();
		}
		if (addedElements > 0) {  
			//Notifico l'utente di tutte le entitÃ  o proprietÃ  per cui Ã¨ stata aggiunta la valutazione
			if (addedEntities != null) {
				for (MatchedElement a: addedEntities) {
					if (a.getRating() == 1) {
						positive.add(a.getElement().getLabel());
					} else {
						negative.add(a.getElement().getLabel());
					}
				}
			}
			if (addedProperties != null) {
				for (MatchedElement a: addedProperties) {
					if (a.getRating() == 1) {
						positive.add(a.getElement().getLabel());
					} else {
						negative.add(a.getElement().getLabel());
					}
				}
			}
			String[] positiveArray = {positive.toString()};
			String[] negativeArray = {negative.toString()};
			response = this.messages.getString("AddedStart");
			if (positive.length() > 0) {
				response += this.format("AddedPositive", positiveArray) + "\n";
			}
			if (negative.length() > 0) {
				response += this.format("AddedNegative", negativeArray);
			}
		} else {
			response = this.messages.getString("DefaultFailure");
		}
		return response;
	}
	
	public String getDeletedPreferencesMessage(List<MatchedElement> deletedEntities) {
		StringJoiner sj = new StringJoiner(", ");
		String response = "";
		
		if (deletedEntities.size() > 0) {
			for (MatchedElement alias: deletedEntities) {
				sj.add(alias.getElement().getLabel());
			}
			String[] params = {sj.toString()};
			response = this.format("Deleted", params);
		}
		return response;
	}
	
	public String getAddPreferenceMessage(AddPreferenceResponse addPreferenceResponse) {
		StringJoiner positive = new StringJoiner(", ");
		StringJoiner negative = new StringJoiner(", ");
		String response = "";
		
		if (addPreferenceResponse.isSuccess() 
				&& (addPreferenceResponse.getAddedEntities().size() > 0
						|| addPreferenceResponse.getAddedProperties().size() > 0
						|| addPreferenceResponse.getPreference().getNextPendingEvaluation() != null
						|| addPreferenceResponse.getPreference().getNextConfirmation() != null)) {
			int addedElements = addPreferenceResponse.getAddedEntities().size()
					+ addPreferenceResponse.getAddedProperties().size();
			if (addedElements > 0) {  
				//Notifico l'utente di tutte le entitÃ  o proprietÃ  per cui Ã¨ stata aggiunta la valutazione
				for (MatchedElement a: addPreferenceResponse.getAddedEntities()) {
					if (a.getRating() == 1) {
						positive.add(a.getElement().getLabel());
					} else {
						negative.add(a.getElement().getLabel());
					}
				}
				for (MatchedElement a: addPreferenceResponse.getAddedProperties()) {
					if (a.getRating() == 1) {
						positive.add(a.getElement().getLabel());
					} else {
						negative.add(a.getElement().getLabel());
					}
				}
				String[] positiveArray = {positive.toString()};
				String[] negativeArray = {negative.toString()};
				response = this.messages.getString("AddedStart");
				if (positive.length() > 0) {
					response += this.format("AddedPositive", positiveArray) + "\n";
				}
				if (negative.length() > 0) {
					response += this.format("AddedNegative", negativeArray);
				}
			}		
		} else if (addPreferenceResponse.getAddedEntities().size() == 0
				&& addPreferenceResponse.getAddedProperties().size() == 0){
			response = this.messages.getString("NoneFound");
		} else {
			response = this.messages.getString("DefaultFailure");
		}
		return response;
	}
	
	public String getRecommendationStartMessage(List<PropertyFilter> filters) {
		PropertyService ps = new PropertyService();
		if (filters.size() > 0) {
			StringJoiner sj = new StringJoiner(", ");
			for (PropertyFilter filter: filters) {
				sj.add(ps.getPropertyLabel(filter.getPropertyValue()));
			}
			String[] array = {sj.toString()};
			return this.format("RequestRecommendationFilteredStart", array);
		}
		return this.messages.getString("RequestRecommendationStart");
	}
	
	public String getRecommendationReminderMessage(String currentEntity) {
		String[] array = {currentEntity};
		return this.format("RecommendationReminder", array);
	}
	
	public String getPreferenceHintMessage() {
		String[] params = {this.getMultipleMessage("Example.preference")};
		return this.format("Hint.preference", params);
	}
	
	public String getSuggestionHintMessage() {
		String[] params = {this.getMultipleMessage("Example.suggestion.preference")};
		return this.format("Hint.suggestion", params);
	}
	
	public String getShowProfileMessage(JsonArray profile, boolean showPreface, boolean first, boolean showHint) {
		StringBuilder sb = new StringBuilder();
		if (profile.size() > 0) {
			if (showPreface) {
				sb.append(this.messages.getString("ShowProfilePreface"));
			}
			for (JsonElement p: profile) {
				String ratingStr = "";
				JsonObject ratingJson = p.getAsJsonObject();
				int rating = Integer.parseInt(ratingJson.get("rating").getAsString());
				String type = ratingJson.get("type").getAsString();
				if (rating == 0) {
					ratingStr = this.messages.getString("ShowProfilePreferenceDislike");
				} else {
					ratingStr = this.messages.getString("ShowProfilePreferenceLike");
				}
				String name = ratingJson.get("label").getAsString();
				if (!type.equalsIgnoreCase("entity")) {
					System.out.println("***\n" + ratingJson + "\n***");
					String propertyType = null;
					try {
						propertyType = ratingJson.get("typeLabel").getAsString();
					} catch (UnsupportedOperationException e) {
						propertyType = "error";
					}
					String[] parameters = {ratingStr, name, propertyType};
					sb.append(this.format("ShowProfilePreferenceProperty", parameters));
				} else {
					String[] parameters = {ratingStr, name};
					sb.append(this.format("ShowProfilePreferenceEntity", parameters));
				}
			}
			sb.append("\n");
			if (first) {
				sb.append(this.messages.getString("HelpProfile"));
			} else if (showHint) {
				sb.append(this.messages.getString("ShowProfileEnd"));
			}
		} else {
			sb.append(this.messages.getString("ShowProfileEmpty"));
		}
		return sb.toString();
	}
	
	public String getSkipMessage(boolean success) {
		if (success) {
			return this.messages.getString("SkipSuccess");
		}
		return this.messages.getString("SkipFailure");
	}
	
	public String getStopRecommendationsMessage(boolean success) {
		if (success) {
			return this.messages.getString("StopRecommendationSuccess");
		}
		return this.messages.getString("StopRecommendationFailure");
	}
	
	public String getShowTrailerMessage(List<String> trailer) {
		if (trailer != null) {
			String[] parameters = {""};
			return this.format("ShowTrailerSuccess", parameters);
		} else {
			return this.messages.getString("ShowTrailerFailure");
		}
	}
	
	public String getRemainingPreferencesMessage(int numPreferences, int minPreferences) {
		String speech = "";
		if (numPreferences >= minPreferences) {
			speech = this.messages.getString("UserCanStartRecommendation");
		} else if (numPreferences >= 0) {
			String[] parameters = {minPreferences - numPreferences + ""};
			speech = this.format("PreferencesRemaining", parameters);
		}
		return speech;
	}
	
	public String getPendingEvaluationMessage(PendingEvaluation nextEv) {
		String rating = "";
		if (nextEv.getRating() == 0) {
			rating = this.messages.getString("Dislike");
		} else {
			rating = this.messages.getString("Like");
		}
		
		String speech = "";

		if (nextEv.getType() == PendingEvaluationType.PROPERTY_TYPE_DISAMBIGUATION) {
			StringJoiner sj = new StringJoiner(", ");
			for (Alias value: nextEv.getPossibleValues()) {
				sj.add(value.getLabel());
			}
			String[] parameters = {rating, nextEv.getElementName().getLabel(), sj.toString()};
			speech = this.format("PendingPropertyTypeDisambiguation", parameters);
		} else if (nextEv.getType() == PendingEvaluationType.NAME_DISAMBIGUATION) {
			StringJoiner sj = new StringJoiner("\n");
			for (Alias value: nextEv.getPossibleValues()) {
				sj.add(value.getLabel());
			}
			String[] parameters = {sj.toString(), rating};
			speech = this.format("PendingNameDisambiguation", parameters);
		} else if (nextEv.getType() == PendingEvaluationType.DELETE_NAME_DISAMBIGUATION) {
			StringJoiner sj = new StringJoiner("\n");
			for (Alias value: nextEv.getPossibleValues()) {
				sj.add(value.getLabel());
			}
			String[] parameters = {sj.toString()};
			speech = this.format("PendingDeleteNameDisambiguation", parameters);
		}
		return speech;
	}
	
	public String getEntityRatingPromptMessage(String entityToRateLabel) {
		String[] parameters = {entityToRateLabel};
		return this.format("RateProposedEntity", parameters);
	}
	
	public String getEntityRatingReminderMessage(String entityToRateLabel) {
		String[] parameters = {entityToRateLabel};
		return this.format("ReminderProposedEntity", parameters);
	}
	
	public String getNextConfirmationMessage(PendingConfirmation pc) {
		StringJoiner sj = new StringJoiner(", ");
		String rating = "";
		if (pc.getRating() == 0) {
			rating = this.messages.getString("Dislike");
		} else {
			rating = this.messages.getString("Like");
		}

		List<Alias> properties = pc.getProperties();
		if (properties != null && properties.size() > 0) {
			for (Alias property: properties) {
				sj.add(property.getLabel());
			}
			String[] parameters = {rating, pc.getPropertyType().getLabel(), pc.getEntity().getLabel(), sj.toString()};
			return this.format("PendingConfirmation", parameters);
		} else {
			String[] parameters = {rating, pc.getPropertyType().getLabel(), pc.getEntity().getLabel()};
			return this.format("PendingConfirmationNoneFound", parameters);
		}
	}
	
	public String getExplanationKey(String predicate, String object) {
		String[] params = {predicate, object};
		String template;
		if (predicate.equals("review")) {
			template = "ExplanationKeyReview";
		}
		else {
			template = "ExplanationKey";
		}
		return this.format(template, params);
	}
	
	public String getEntityExplanationByUserMovieMessage(String recEntity, Map<String, String> itemCommonPropertyMap) {
		String speech = "";
		
		if (!itemCommonPropertyMap.isEmpty()) {
			String[] params = {recEntity};
			speech = this.format("ExplanationUserEntity", params);
			for (Entry<String, String> entry : itemCommonPropertyMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				String[] params2 = {key, value};
				String template;
				if (key.startsWith("Reviewers")){
					template = "ExplanationUserEntityRowReview";
				}
				else {
					template = "ExplanationUserEntityRow";
				}
				speech += "\n" + this.format(template, params2);
			}
		}
		
		return speech;
	}
	
	public String getEntityExplanationByLikeUserPropertyMessage(String recEntity, Map<String, String> itemCommonPropertyMap) {
		String speech = "";
		if (!itemCommonPropertyMap.isEmpty()) {
			String[] params = {recEntity};
			speech = this.format("ExplanationLikeUserProperty", params);
			for (Entry<String, String> entry : itemCommonPropertyMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				String[] params2 = {key, value};
				String template;
				if (key.startsWith("Reviewers")){
					template = "ExplanationLikeUserPropertyRowReview";
				}
				else {
					template = "ExplanationLikeUserPropertyRow";
				}
				speech += "\n" + this.format(template, params2);
			}
		}
		return speech;
	}
	
	public String getEntityExplanationByDislikeUserPropertyMessage(String recEntity, Map<String, String> itemCommonPropertyMap) {
		String speech = "";
		if (!itemCommonPropertyMap.isEmpty()) {
			String key = null;
			String value = null;
			String[] params = {recEntity};
			speech = this.format("ExplanationDislikeUserProperty", params);
			String propertyTypeNew = "null";
			String propertyTypeOld = "null";
			String propertyValueOld = "null";
			// System.out.println("itemCommonPropertyMap:" + itemCommonPropertyMap);
			int k = 0;
			TreeMap<String, String> itemCommonPropertySet = new TreeMap<String, String>(Collections.reverseOrder());
			itemCommonPropertySet.putAll(itemCommonPropertyMap);
			// itemCommonPropertySet.descendingKeySet();
			for (Entry<String, String> entry : itemCommonPropertySet.entrySet()) {
				key = entry.getKey();
				value = entry.getValue();
				propertyTypeNew = key.split("\\s+")[0];
				// System.out.println("" + propertyTypeNew);
				if (!propertyTypeNew.equals(propertyTypeOld) && (!propertyTypeOld.equals("null"))) {
					String[] p = {propertyValueOld};
					speech += "\n" + this.format("ExplanationDislikeUserPropertyRow", p);
					propertyTypeOld = propertyTypeNew;
					k = 0;
				} else {
					propertyValueOld = value;
					propertyTypeOld = propertyTypeNew;
				}
				if (k < 3) {
					String[] p = {key};
					speech += "\n" + this.format("ExplanationDislikeUserPropertyRow2", p);
					k++;
				}

			}

			String[] params2 = {value};
			speech += "\n" + this.format("ExplanationDislikeUserPropertyRow3", params2);
		}
		return speech;
	}
	
	public String getHelpMessage(boolean disambiguation, boolean recommendation, boolean showProfile) {
		String speech = "";
		if (disambiguation) {
			speech = this.messages.getString("HelpDisambiguation");
		} else if (recommendation) {
			speech = this.messages.getString("HelpRecommendation");
		} else if (showProfile) {
			speech = this.messages.getString("HelpProfile");
		} else {
			speech = this.messages.getString("HelpDefault");
		}
		return speech;
	}
	
	public String getDeniedConfirmationMessage() {
		return this.messages.getString("ConfirmFalse");
	}
	
	public String getDeletedEntityPreferencesMessage() {
		return this.messages.getString("DeleteMoviePreferencesSuccess");
	}
	
	public String getDeletedPropertyPreferencesMessage() {
		return this.messages.getString("DeletePropertyPreferencesSuccess");
	}
	
	public String getDeletedProfileMessage() {
		return this.messages.getString("DeleteEverythingSuccess");
	}
	
	public String getDeleteProfileFailureMessage() {
		return this.messages.getString("DeleteFailure");
	}
	
	public String getNotEnoughPreferencesMessage() {
		return this.messages.getString("NotEnoughPreferencesMessage");
	}
	
	public String getNoneFoundMessage() {
		return this.messages.getString("NoneFound");
	}
	
	public String getRequestExplanationFailureMessage() {
		return this.messages.getString("RequestExplanationFailure");
	}
	
	public String getRequestDetailsFailureMessage() {
		return this.messages.getString("RequestDetailsFailure");
	}
	
	public String getRecommendedMoviePrefaceMessage() {
		return this.messages.getString("RecommendedMoviePreface");
	}
	
	public String getIntroductionMessage() {
		return this.messages.getString("Introduction");
	}
	
	public String getEmptyConfirmationMessage() {
		return this.messages.getString("EmptyConfirmResponse");
	}
	
	public String getRecommendedMovieAskPreferenceMessage(boolean firstRecommendation) {
		if (firstRecommendation) {
			return this.messages.getString("HelpRecommendation");
		} else {
			this.messages.getString("RecommendedMovieAskPreference");
		}
		return this.messages.getString("RecommendedMovieAskPreference");
	}
	
	public String getRecommendationEndedMessage(boolean refocus) {
		String speech = this.messages.getString("RecommendationEnded");
		if (refocus) {
			speech += this.messages.getString("RecommendationEndedRefocus");
		} else {
			speech += this.messages.getString("RecommendationEndedDefault");
		}
		return speech;
	}
	
	public String getShowIDMessage(String userID) {
		String[] args = {userID + ""};
		return this.format("Show.id", args);
	}
	
	public String getEntityTypeSingularMessage(boolean forceLowerCase) {
		String speech = this.messages.getString("EntityTypeSingular");;
		if (forceLowerCase) {
			speech = speech.toLowerCase();
		}
		return speech;
	}
	
	public String getEntityTypePluralMessage(boolean forceLowerCase) {
		String speech = this.messages.getString("EntityTypePlural");;
		if (forceLowerCase) {
			speech = speech.toLowerCase();
		}
		return speech;
	}
	
	private String format(String messageName, String[] params) {
		String message = this.messages.getString(messageName);
		//Eseguo l'escape degli apostrofi
		String fixMessage = message.replace("'", "''");
		this.formatter.applyPattern(fixMessage);
		return this.formatter.format(params);
	}
	
	public static void main(String[] args) {
		ResponseService rs = new ResponseService();
		String[] params = {"Test"};
		System.out.println(rs.format("RequestRecommendationFilteredStart", params));
		System.out.println(rs.format("AddedNegative", params));
	}

	public String getLinkLabel() {
		return messages.getString("LinkLabel");
	}
}