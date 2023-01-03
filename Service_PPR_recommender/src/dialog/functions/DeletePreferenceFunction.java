package dialog.functions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dialog.ApiAiResponse;
import dialog.FilteredAlias;
import dialog.FilteredSentimentObject;
import dialog.MLERConnector;
import dialog.PendingEvaluation.PendingEvaluationType;
import dialog.Preference;
import dialog.SentimentAnalyzerConnector;
import functions.DisambiguationService;
import functions.ProfileService;
import functions.ServiceSingleton;
import functions.response.GetNameDisambiguationResponse;
import utils.Alias;
import utils.MatchedElement;

/**
 * Handles the addition of all the preferences mentioned in a user sentence during the 
 * delete_preference intent, handling eventual disambiguation or confirmation requests 
 * needed.
 */
public class DeletePreferenceFunction {
	
	private static final Logger LOGGER = Logger.getLogger(DeletePreferenceFunction.class.getName());

	public DeletePreferenceResponse deletePreferencesInMessage(String message, String userID) {
		DeletePreferenceResponse response = new DeletePreferenceResponse();
		ProfileService ps = ServiceSingleton.getProfileService();
		
		MLERConnector saConnector = new MLERConnector(true, false, true);
		try {
			//Invoke the sentiment analyzer component to get all the ratings mentioned in the message
			List<FilteredSentimentObject> sentimentArray = saConnector.getFilteredSentiment(message, null, 5);
			Preference p = new Preference(sentimentArray, new ArrayList<FilteredSentimentObject>());
			DisambiguationService ds = new DisambiguationService();
			for (int i = 0; i < sentimentArray.size(); i++) {
				List<FilteredAlias> aliases = sentimentArray.get(i).getFilteredAliases();
				List<FilteredAlias> filteredAliases = ps.filterAliasesByUserProfile(userID, aliases);
				LOGGER.log(Level.INFO, "filteredAliases contains " + filteredAliases);
				if (filteredAliases.size() > 0) {
					GetNameDisambiguationResponse gndr = ds.getNameDisambiguation(0, filteredAliases, PendingEvaluationType.DELETE_NAME_DISAMBIGUATION);
					if (gndr.success()) {
						if (gndr.getPerfectMatch() != null) {
							//We can directly remove the item
							Alias perfectMatch = gndr.getPerfectMatch();
							deleteSinglePreference(userID, perfectMatch.getURI(), perfectMatch.getLabel());
							response.addDeletedElement(new MatchedElement(perfectMatch, -1));
							p.addDisambiguatedEntity(i, perfectMatch);
						} else {
							//Add the disambiguation request
							p.addDisambiguation(i, gndr.getEvaluation());
						}
					}
				}
			}
			if (response.deleted.size() == 0) {
				response.setSuccess(false);
			}
			response.setPreference(p);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setSuccess(false);
		}
		
		return response;
	}
	
	public void deleteSinglePreference(String userID, String uri, String name) {
		ApiAiResponse response = new ApiAiResponse();
		response.addSpeech("Ok, I removed " + name + ".");
		new ProfileService().deletePreference(userID, uri);
	}
	
	public class DeletePreferenceResponse {
		/**
		 * True if no error occurred
		 */
		private boolean success;
		/**
		 * List of deleted items
		 */
		private List<MatchedElement> deleted;
		/**
		 * Preference object containing all the information needed to complete the request
		 */
		private Preference preference;
		
		public DeletePreferenceResponse() {
			deleted = new ArrayList<MatchedElement>();
			success = true;
		}
		
		private void addDeletedElement(MatchedElement a) {
			this.deleted.add(a);
		}
		
		private void setSuccess(boolean success) {
			this.success = success;
		}
		
		private void setPreference(Preference p) {
			this.preference = p;
		}
		
		public List<MatchedElement> getDeletedElements() {
			return this.deleted;
		}
		
		public boolean getSuccess() {
			return this.success;
		}

		public Preference getPreference() {
			return this.preference;
		}
		
		
	}
}
