package dialog.functions;

import java.util.ArrayList;
import java.util.List;

import configuration.Configuration;
import dialog.SentimentAnalyzerConnector;
import dialog.SentimentObject;
import entity.Entity;
import functions.EntityService;
import functions.PropertyService;
import functions.ServiceSingleton;
import utils.Alias;
import utils.MatchedElement;

/**
 * Handles the addition of the preferences during the recommendation phase, handling eventual
 * disambiguation or confirmation requests needed.
 */
public class AddRecommendationPreferenceFunction {
	
	/**
	 * Adds the rating to the currently recommended item, and to any other item mentioned
	 * in the sentence.
	 * @param message Message of the user
	 * @param currentRecommendedIndex index in the list of recommended items representing the currently recommended item
	 * @param userID ID of the user
	 * @return an AddRecommendationPreferenceResponseObject containing all the feedback from the preference addition process
	 */
	public AddRecommendationPreferenceResponse addRecommendationPreference(String message, int currentRecommendedIndex, String userID) {
		SentimentAnalyzerConnector sa = new SentimentAnalyzerConnector(false, true, false);
		EntityService es = ServiceSingleton.getEntityService();
		PropertyService ps = ServiceSingleton.getPropertyService();
		Configuration c = Configuration.getDefaultConfiguration();
		try {
			//Invoke the Sentiment Analyzer component
			List<SentimentObject> sentimentArray = sa.getSentiment(message);
			System.out.println("Got result from sentiment analysis service:");
			System.out.println(sentimentArray.toString());
			
			if (sentimentArray.size() > 0) {
				int sentiment = sentimentArray.get(0).getSentiment();
				String currentRecommended = es.getCachedRecommendedEntities(userID).get(currentRecommendedIndex);
				int rating = getRatingFromSentiment(sentiment);
				Entity currentEntity = es.getEntityDetails(currentRecommended);				
				//We add the preference to the recommended item
				es.addRecommendedEntityPreference(userID, currentRecommended, rating, "user");
				AddRecommendationPreferenceResponse response = 
						new AddRecommendationPreferenceResponse(true, es.getEntityLabel(currentRecommended), rating);
				
				//For each recognized property type, we add a preference
				//Handles sentences of the form "I like The Matrix for the director"
				for (SentimentObject propType: sentimentArray) {
					List<String> propertyURIs = currentEntity.get(propType.getUri());
					if (propertyURIs != null) {
						for (String property: propertyURIs) {
							ps.addPropertyPreference(userID, property, propType.getUri(), rating, "user");
							response.addProperty(new MatchedElement(new Alias(property, ps.getPropertyLabel(property)), rating));
						}
					}
				}
				
				return response;
			} else {
				return new AddRecommendationPreferenceResponse(false, "", -1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new AddRecommendationPreferenceResponse(false, "", -1);
		}
	}
	
	/**
	 * Returns the rating from the sentiment returned by the Sentiment Analyzer
	 */
	private int getRatingFromSentiment(int sentiment) {
		int rating = 0;
		if (sentiment > 1) {
			rating = 1;
		}
		return rating;
	}
	
	public class AddRecommendationPreferenceResponse {
		/**
		 * True if the preference was added successfully
		 */
		private boolean success;
		/**
		 * Label of the recommended item
		 */
		private String label;
		/**
		 * Rating assigned to the recommended item
		 */
		private int rating;
		/**
		 * List of properties that were added along with the recommended item
		 */
		private List<MatchedElement> addedProperties;
		
		public AddRecommendationPreferenceResponse(boolean success, String label, int rating) {
			this.success = success;
			this.label = label;
			this.rating = rating;
			this.addedProperties = new ArrayList<MatchedElement>();
		}

		public boolean isSuccess() {
			return success;
		}
		
		public String getLabel() {
			return this.label;
		}
		
		public int getRating() {
			return this.rating;
		}
		
		public char getRatingSymbol() {
			return (this.rating == 1) ? '+' : '-';
		}
		
		public void addProperty(MatchedElement a) {
			this.addedProperties.add(a);
		}
		
		public List<MatchedElement> getAddedProperties() {
			return this.addedProperties;
		}
	}
}
