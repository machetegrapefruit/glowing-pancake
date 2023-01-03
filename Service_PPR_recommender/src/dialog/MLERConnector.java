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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import configuration.Configuration;
import dialog.SentimentObject.SentimentObjectType;
import dialog.entityrecognizer.Match;
import dialog.entityrecognizer.SentimentMentionMap;
import utils.Alias;


/**
 * This class handles the connection with the Sentiment Analyzer component
 * @author Andrea Iovine
 *
 */
public class MLERConnector {
	//private static final String default_url = "http://localhost:8081/SentimentExtractorWebService/sentiment";
	private boolean findEntities;		//If true, entities will be recognized in the sentence
	private boolean findPropertyTypes;	//If true, property types (e.g. "director") will be recognized in the sentence
	private boolean retryAndCapitalize;	//If true, the first letter of each word will be capitalized if no entity was found
	private String url;					//URL of the Sentiment Analyzer component
	
	public MLERConnector(boolean findEntities, boolean findPropertyTypes, boolean retryAndCapitalize) {
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
		List<SentimentMentionMap> mentions = sendGetEntities(url, message);
		List<FilteredSentimentObject> result = new ArrayList<>();
		for (SentimentMentionMap mention: mentions) {
			//For each mention found
			if (mention.getMention().containsKey("item") && mention.getMention().get("item").size() > 0) {
				//At least one entity has been linked to this mention
				int sentiment = mention.getSentiment();
				List<Match> allMatches = mention.getMention().getBest("item", numAliases);
				Match firstMatch = allMatches.get(0);
				String uri = firstMatch.getEntityID();
				String label = firstMatch.getMatchedName();
				int start = mention.getMention().getTokenRange().getStart();
				int end = mention.getMention().getTokenRange().getEnd();
				List<FilteredAlias> aliases = new ArrayList<FilteredAlias>();
				SentimentObjectType type = getSentimentObjectType(allMatches);
				for (Match m: allMatches) {
					//Only add aliases that match the current object type
					String id = m.getEntityID();
					boolean addThis = (id.startsWith("P") 
							&& type == SentimentObjectType.PROPERTY_TYPE
							&& this.findPropertyTypes)							//It's a property type
							|| (id.startsWith("K") 
									&& type == SentimentObjectType.KEYWORD)		//It's a keyword
							|| (!id.startsWith("P") 
									&& !id.startsWith("K") 
									&& type == SentimentObjectType.ENTITY);		//It's an entity
					if (addThis) {
						Alias a = new Alias(m.getEntityID(), m.getMatchedName());
						FilteredAlias fa = new FilteredAlias(a, m.getMatch());
						aliases.add(fa);
					}
				}
				FilteredSentimentObject fso = new FilteredSentimentObject(label, uri, sentiment, start, end, type, aliases);
				result.add(fso);
			}
		}
		return result;
	}
	
	public int getSentiment(String message) throws IOException {
		return sendGetSentiment(url, message);
	}
	
	//Decide the type of a mention based on the entities that are linked to it
	private SentimentObjectType getSentimentObjectType(List<Match> allMatches) {
		SentimentObjectType type = SentimentObjectType.ENTITY;
		for (Match m: allMatches) {
			if (m.getEntityID().startsWith("P") && m.getMatch() > 99.9) {
				return SentimentObjectType.PROPERTY_TYPE;
			} else if (m.getEntityID().startsWith("K") && m.getMatch() > 99.9) {
				return SentimentObjectType.KEYWORD;
			}
		}
		return type;
	}
	
	private int sendGetSentiment(String url, String message) throws IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url + "/getSentiment?" 
				+ getParameterString("text", URLEncoder.encode(message, "UTF-8")));

		
		HttpResponse response = client.execute(request);
		
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		System.out.println("Result from Sentiment Analyzer is:" + result.toString());
		JsonObject sentimentObject = new JsonParser().parse(result.toString()).getAsJsonObject();
		return sentimentObject.get("sentiment").getAsInt();
	}
	
	private List<SentimentMentionMap> sendGetEntities(String url, String message) throws UnsupportedOperationException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url + "/getEntities?" 
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
		List<SentimentMentionMap> mentions = new ArrayList<SentimentMentionMap>();
		Gson gson = new Gson();
		for (JsonElement json: sentimentArray) {
			SentimentMentionMap mention = gson.fromJson(json, SentimentMentionMap.class);
			mentions.add(mention);
		}
		
		return mentions;
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
		new MLERConnector(true, false, true).getFilteredSentiment("I like \"Weird Al\" Yankovic", null, 10);
	}
}
