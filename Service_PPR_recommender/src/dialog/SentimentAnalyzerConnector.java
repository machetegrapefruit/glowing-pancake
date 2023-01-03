package dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import configuration.Configuration;
import utils.Alias;
import utils.DistanceMeasure;
import utils.LevenshteinDistanceCalculator;
import utils.StopWordUtils;

/**
 * This class handles the connection with the Sentiment Analyzer component
 * @author Andrea Iovine
 *
 */
public class SentimentAnalyzerConnector {
	//private static final String default_url = "http://localhost:8081/SentimentExtractorWebService/sentiment";
	private boolean findEntities;		//If true, entities will be recognized in the sentence
	private boolean findPropertyTypes;	//If true, property types (e.g. "director") will be recognized in the sentence
	private boolean retryAndCapitalize;	//If true, the first letter of each word will be capitalized if no entity was found
	private String url;					//URL of the Sentiment Analyzer component
	
	public SentimentAnalyzerConnector(boolean findEntities, boolean findPropertyTypes, boolean retryAndCapitalize) {
		this.findEntities = findEntities;
		this.findPropertyTypes = findPropertyTypes;
		this.retryAndCapitalize = retryAndCapitalize;
		this.url = Configuration.getDefaultConfiguration().getSentimentExtractorBasePath();
	}
	
	/**
	 * Retrieves the entities and keywords mentioned in the provided sentence, filtering and ranking them based on
	 * a distance measure (e.g. Levenshtein distance). For each mention, it also retrieves the user's sentiment,
	 * on a scale from 0 to 4 (0-1 is negative, 2 is neutral, 3-4 is positive).
	 * @param message Text that contains mentions to entities
	 * @param stopWords Words to ignore. This is useful to avoid that some domain keywords (such as "movie", "song") are
	 *  mistaken as entity mentions  
	 * @param numAliases Number of possible candidates to consider for each entity mention
	 * @return A list of objects, each containing a mention, and for each mention, all possible candidate entities.
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public List<FilteredSentimentObject> getFilteredSentiment(String message, String[] stopWords, int numAliases) throws IllegalStateException, IOException {
		List<FilteredSentimentObject> sentiment = getSentimentAndFilter(message, stopWords, numAliases);
		//If no entities are found, and retryAndCapitalize is true, retry with the capitalizewd sentence
		if (sentiment.size() == 0 && retryAndCapitalize) {
			sentiment = getSentimentAndFilter(capitalizeString(message), stopWords, numAliases);
		}
		
		return sentiment;
	}
	
	private List<FilteredSentimentObject> getSentimentAndFilter(String message, String[] stopWords, int numAliases) throws IllegalStateException, IOException {
		String filteredText = message;
		if (stopWords != null) {
			//Remove the stopwords from the sentence
			filteredText = StopWordUtils.removeStopWords(message, stopWords);
		}
		List<SentimentObject> allObjects = getSentiment(this.url, filteredText, false);
		List<FilteredSentimentObject> filtered = new ArrayList<>();
		
		//Filter the mentions
		for (SentimentObject entity: allObjects) {
			List<Alias> aliases = entity.getAliases();		//List of candidate entities for a mention
			List<String> labels = new ArrayList<String>();
			if (aliases.size() > 0) {
				for (Alias a: aliases) {
					//Get the labels for each candidate entity
					labels.add(a.getLabel().replaceAll("[,.!?\\-\\[\\]]", "").trim());
				}
				//Get the labels that are most similar to the ones in the message
				List<DistanceMeasure> distances = LevenshteinDistanceCalculator.getMostSimilar(message.replaceAll("[,.!?\\-\\[\\]]", "").trim(), labels, numAliases, LevenshteinDistanceCalculator.MIN_THRESHOLD);
				if (distances.size() > 0) {
					List<FilteredAlias> filteredOptions = new ArrayList<FilteredAlias>();
					for (DistanceMeasure m: distances) {
						FilteredAlias fa = new FilteredAlias(aliases.get(m.getIndex()), m.getDistance());
						filteredOptions.add(fa);
					}
					filtered.add(new FilteredSentimentObject(entity, filteredOptions));
				}
			}
		}
		
		return filtered;
	}
	
	/**
	 * Returns the entities and keywords mentioned in the message. For each mention, it also retrieves the user's sentiment,
	 * on a scale from 0 to 4 (0-1 is negative, 2 is neutral, 3-4 is positive).
	 * @param message Text that contains mentions to entities
	 * @return A list of objects, each containing a mention, and for each mention, all possible candidate entities.
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public List<SentimentObject> getSentiment(String message) throws IllegalStateException, IOException {
		return getSentiment(this.url, message, this.retryAndCapitalize);
	}
	
	private List<SentimentObject> getSentiment(String url, String message, boolean retry) throws IllegalStateException, IOException {
		JsonArray sentimentArray = sendRequest(url, message);
		List<SentimentObject> sentimentObjects = new ArrayList<>();
		if (isEmpty(sentimentArray) && retry) {
			sentimentArray = sendRequest(url, capitalizeString(message));
		}
		
		for (int i = 0; i < sentimentArray.size(); i++) {
			sentimentObjects.add(new SentimentObject(sentimentArray.get(i).getAsJsonObject()));
		}
		
		return sentimentObjects;
	}
	
	private JsonArray sendRequest(String url, String message) throws UnsupportedOperationException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url + "?" 
				+ getParameterString("text", URLEncoder.encode(message, "UTF-8"))
				+ "&" + getParameterString("findEntities", findEntities)
				+ "&" + getParameterString("findPropertyTypes", findPropertyTypes));

		
		HttpResponse response = client.execute(request);
		
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		System.out.println("Result from Sentiment Analyzer is:" + result.toString());
		JsonArray sentimentArray = new JsonParser().parse(result.toString()).getAsJsonArray();
		return sentimentArray;
	}
	
	private boolean isEmpty(JsonArray sentimentArray) {
		boolean isEmpty = false;
		if (sentimentArray.size() == 1) {
			JsonObject sentiment = sentimentArray.get(0).getAsJsonObject();
			if (sentiment.get("label").getAsString().equals("")) {
				isEmpty = true;
			}
		}
		return isEmpty;
	}
	
	private String capitalizeString(String message) {
		StringJoiner sj = new StringJoiner(" ");
		String[] split = message.split(" ");
		for (int i = 0; i < split.length; i++) {
			sj.add(StringUtils.capitalize(split[i]));
		}
		return sj.toString();
	}
	
	private String getParameterString(String parameterName, String parameterValue) {
		return parameterName + "=" + parameterValue.replace(" ", "%20");
	}
	
	private String getParameterString(String parameterName, boolean parameterValue) {
		if (parameterValue) {
			return parameterName + "=" + "true";
		} else {
			return parameterName + "=" + "false";
		}
	}
	
	public static void main(String[] args) throws IllegalStateException, IOException {
		new SentimentAnalyzerConnector(true, false, true).getSentimentAndFilter("I like \"Weird Al\" Yankovic", null, 10);
	}
}
