package configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import questionnaire.Question;

/**
 * Contains all the data that is registered in the JSON configuration file.
 * @author Andrea Iovine
 *
 */
public class Configuration {
	private static Configuration defaultConfiguration;
	
	private String[] propertyTypes;
	private String[] propertyTypesForExplanation;
	private String[] propertyTypesForDetails;
	String[] filterablePropertyTypes;
	private Map<String, String> propertyTypesLabels;
	private Map<String, String> propertyTypesEmojis;
	private String propertyTypeName;
	private String propertyTypeImage;
	private String propertyTypeReleaseYear;
	private String propertyTypeTrailer;
	private String propertyTypeRuntimeMinutes;
	private String[] stopWordsForPreference;
	private String[] stopWordsForCritiquing;
	private String[] stopWordsForRecommendationFilters;
	private String entityEmoji;
	private String[] popularEntities;
	private PropertyMapper propertyMapper;
	private String dbUrl;
	private String dbName;
	private String dbUser;
	private String dbPass;
	private String sentimentExtractorBasePath;
	private String entityExtractorBasePath;
	private String interactionType;
	private String recSysServiceBasePath;
	private String dialogFlowToken;
	private boolean doQuestionnaire;
	private Question[] questionnaire;
	private String dialogflowV2CredentialsPath;
	private String dialogflowV2AgentName;
	private boolean isPropertyTypeDisambiguationEnabled;
	private int numFreeTextPreferences;
	private int numSuggestedItems;
	private String actLearnFunction;
	private int recListSize;
	private int maxCycles;
	private int minPreferencesFirstSession;
	private int minPreferencesSecondSession;
	private String recsysAlgorithm;
	private String recsysUrl;
	private int recsysIndex;
	
	public String getDialogflowV2AgentName() {
		return dialogflowV2AgentName;
	}

	public Configuration(String[] propertyTypes, String[] propertyTypesForExplanation, String[] propertyTypesForDetails,
			String[] filterablePropertyTypes, Map<String, String> propertyTypesLabels, Map<String, String> propertyTypesEmojis, String propertyTypeName, 
			String propertyTypeReleaseYear, String propertyTypeImage, String propertyTypeTrailer, String propertyTypeRuntimeMinutes, String[] stopWordsForPreference, String[] stopWordsForCritiquing, 
			String[] stopWordsForRecommendationFilters, String entityEmoji, String[] popularEntities, PropertyMapper propertyMapper,
			String dbUrl, String dbName, String dbUser, String dbPass, String sentimentExtractorBasePath,
			String entityExtractorBasePath, String interactionType, String recSysServiceBasePath, String dialogFlowToken,
			String dialogflowV2CredentialsPath, String dialogflowV2AgentName,
			boolean doQuestionnaire, Question[] questionnaire, boolean isPropertyTypeDisambiguationEnabled, int numFreeTextPreferences,
			int numSuggestedItems, String actLearnFunction, int recListSize, int maxCycles,
			int minPreferencesFirstSession, int minPreferencesSecondSession, String recsysAlgorithm,
			String recsysUrl, int recsysIndex) {

		this.propertyTypes = propertyTypes;
		this.propertyTypesForExplanation = propertyTypesForExplanation;
		this.propertyTypesForDetails = propertyTypesForDetails;
		this.filterablePropertyTypes = filterablePropertyTypes;
		this.propertyTypesLabels = propertyTypesLabels;
		this.propertyTypesEmojis = propertyTypesEmojis;
		this.propertyTypeName = propertyTypeName;
		this.propertyTypeReleaseYear = propertyTypeReleaseYear;
		this.propertyTypeImage = propertyTypeImage;
		this.propertyTypeTrailer = propertyTypeTrailer;
		this.propertyTypeRuntimeMinutes = propertyTypeRuntimeMinutes;
		this.stopWordsForPreference = stopWordsForPreference;
		this.stopWordsForCritiquing = stopWordsForCritiquing;
		this.stopWordsForRecommendationFilters = stopWordsForRecommendationFilters;
		this.entityEmoji = entityEmoji;
		this.popularEntities = popularEntities;
		this.propertyMapper = propertyMapper;
		this.dbUrl = dbUrl;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPass = dbPass;
		this.sentimentExtractorBasePath = sentimentExtractorBasePath;
		this.entityExtractorBasePath = entityExtractorBasePath;
		this.interactionType = interactionType;
		this.recSysServiceBasePath = recSysServiceBasePath;
		this.dialogFlowToken = dialogFlowToken;
		this.doQuestionnaire = doQuestionnaire;
		this.questionnaire = questionnaire;
		this.dialogflowV2CredentialsPath = dialogflowV2CredentialsPath;
		this.dialogflowV2AgentName = dialogflowV2AgentName;
		this.isPropertyTypeDisambiguationEnabled = isPropertyTypeDisambiguationEnabled;
		this.numFreeTextPreferences = numFreeTextPreferences;
		this.numSuggestedItems = numSuggestedItems;
		this.actLearnFunction = actLearnFunction;
		this.recListSize = recListSize;
		this.maxCycles = maxCycles;
		this.minPreferencesFirstSession = minPreferencesFirstSession;
		this.minPreferencesSecondSession = minPreferencesSecondSession;
		this.recsysAlgorithm = recsysAlgorithm;
		this.recsysUrl = recsysUrl;
		this.recsysIndex = recsysIndex;
	}
	
	public String getActLearnFunction() {
		return actLearnFunction;
	}
	
	public int getNumFreeTextPreferences() {
		return numFreeTextPreferences;
	}

	public int getNumSuggestedItems() {
		return numSuggestedItems;
	}

	public String getEntityEmoji() {
		return entityEmoji;
	}

	public String getPropertyTypeName() {
		return propertyTypeName;
	}
	
	public String getPropertyTypeRuntimeMinutes() {
		return propertyTypeRuntimeMinutes;
	}
	
	public String getPropertyTypeReleaseYear() {
		return propertyTypeReleaseYear;
	}

	public String getPropertyTypeImage() {
		return propertyTypeImage;
	}
	
	public String getPropertyTypeTrailer() {
		return propertyTypeTrailer;
	}
	
	public String[] getPopularEntities() {
		return this.popularEntities;
	}

	public String[] getPropertyTypesForExplanation() {
		return this.propertyTypesForExplanation;
	}
	
	public String[] getPropertyTypes() {
		return this.propertyTypes;
	}
	
	public String[] getPropertyTypesDetails() {
		return this.propertyTypesForDetails;
	}
	
	public String[] getFilterablePropertyTypes() {
		return this.filterablePropertyTypes;
	}
	
	public Map<String, String> getPropertyTypesLabels() {
		return this.propertyTypesLabels;
	}
	
	public Map<String, String> getPropertyTypeEmojis() {
		return this.propertyTypesEmojis;
	}
	
	public String[] getStopWordsForPreference() {
		return this.stopWordsForPreference;
	}
	
	public String[] getStopWordsForCritiquing() {
		return this.stopWordsForCritiquing;
	}
	
	public String[] getStopWordsForRecommendationFilters() {
		return this.stopWordsForRecommendationFilters;
	}
	
	public PropertyMapper getPropertyMapper() {
		return this.propertyMapper;
	}
	
	public String getDbName() {
		return this.dbName;
	}
	
	public String getDbUser() {
		return this.dbUser;
	}
	
	public String getDbPass() {
		return this.dbPass;
	}
	
	public String getDbUrl() {
		System.out.println("dbUrl: " + dbUrl);
		return this.dbUrl;
	}
	
	public String getSentimentExtractorBasePath() {
		return this.sentimentExtractorBasePath;
	}
	
	public String getEntityExtractorBasePath() {
		return this.entityExtractorBasePath;
	}
	
	public String getInteractionType() {
		return this.interactionType;
	}
	
	public String getRecSysServiceBasePath() {
		return this.recSysServiceBasePath;
	}
	
	public String getDialogFlowToken() {
		return this.dialogFlowToken;
	}
		
	public String getDialogflowV2CredentialsPath() {
		return dialogflowV2CredentialsPath;
	}

	public boolean isQuestionnaireEnabled() {
		return this.doQuestionnaire;
	}
	
	public boolean isPropertyTypeDisambiguationEnabled() {
		return isPropertyTypeDisambiguationEnabled;
	}
	
	public Question[] getQuestionnaire() {
		return this.questionnaire;
	}
	
	public int getRecListSize() {
		return this.recListSize;
	}
	
	public int getMaxCycles() {
		return this.maxCycles;
	}
	
	public int getMinPreferencesFirstSession() {
		return this.minPreferencesFirstSession;
	}

	public int getMinPreferencesSecondSession() {
		return this.minPreferencesSecondSession;
	}
	
	public String getRecsysAlgorithm() {
		return this.recsysAlgorithm;
	}

	public String getRecsysUrl() { return this.recsysUrl; }

	public int getRecsysIndex() {
		return this.recsysIndex;
	}
	
	/**
	 * Reads the JSON configuration file
	 * @return the Configuration object that contains the data in the configuration file
	 */
	public static Configuration getDefaultConfiguration() {

		if (defaultConfiguration == null) {
			JsonObject confJson = readJsonFromFile("/configuration.json");
	    	defaultConfiguration = readConfigurationFromJson(confJson);
		}    	

    	return defaultConfiguration;
	}
	
	private static JsonObject readJsonFromFile(String path) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			InputStream is = Configuration.class.getResourceAsStream(path);
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = br.readLine();
	    	while (line != null) {
	    		sb.append(line);
	    		line = br.readLine();
	    	}    	
	    	br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Unable to load configuration file!");
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}

    	JsonObject confJson = (JsonObject) new JsonParser().parse(sb.toString());
    	System.out.println(confJson);
    	return confJson;
	}
	
	private static Configuration readConfigurationFromJson(JsonObject confJson) {
		List<String> propertyTypes = new ArrayList<>();
		List<String> propertyTypesDetails = new ArrayList<>();
		List<String> propertyTypesExplanation = new ArrayList<>();
		Map<String, String> propertyTypesLabels = new HashMap<>();
		Map<String, String> propertyTypesEmojis = new HashMap<>();
		String propertyTypeName = confJson.get("propertyTypeName").getAsString();
		String propertyTypeReleaseYear = confJson.get("propertyTypeReleaseYear").getAsString();
		String propertyTypeImage = confJson.get("propertyTypeImage").getAsString();
		String propertyTypeTrailer = confJson.get("propertyTypeTrailer").getAsString();
		String propertyTypeRuntimeMinutes = null;
		if (confJson.get("propertyTypeRuntimeMinutes") != null) {
			propertyTypeRuntimeMinutes = confJson.get("propertyTypeRuntimeMinutes").getAsString();
		}
		List<String> stopWordsForPreference = new ArrayList<>();
		List<String> stopWordsForCritiquing = new ArrayList<>();
		List<String> stopWordsForRecommendationFilters = new ArrayList<>();
		List<String> popularEntities = new ArrayList<>();
		List<String> filterablePropertyTypes = new ArrayList<>();
		Map<String, PropertyMapping> map = new HashMap<>();
		String sentimentExtractorBasePath = confJson.get("sentimentExtractorBasePath").getAsString();
		String entityExtractorBasePath = confJson.get("entityExtractorBasePath").getAsString();
		String interactionType = confJson.get("interactionType").getAsString();
		String recSysServiceBasePath = confJson.get("recSysServiceBasePath").getAsString();
		String dialogFlowToken = confJson.get("dialogFlowToken").getAsString();
		String dialogflowV2CredentialsPath = confJson.get("dialogflowV2CredentialsPath").getAsString();
		String dialogflowV2AgentName = confJson.get("dialogflowV2AgentName").getAsString();
		
		boolean doQuestionnaire = confJson.get("doQuestionnaire").getAsBoolean();
		boolean isPropertyTypeDisambiguationEnabled = confJson.get("isPropertyTypeDisambiguationEnabled").getAsBoolean();
		Question[] questionnaire = null;
		if (doQuestionnaire) {
			questionnaire = readQuestionnaireFromJson(confJson);
		}
		
		JsonArray propertyTypesJson = confJson.getAsJsonArray("propertyTypes");
		for (int i = 0; i < propertyTypesJson.size(); i++) {
			JsonObject propType = propertyTypesJson.get(i).getAsJsonObject();
			String id = propType.get("id").getAsString();
			String label = propType.get("label").getAsString();
			JsonElement emoji = propType.get("emoji");
			propertyTypes.add(id);
			propertyTypesLabels.put(id, label);
			if (emoji != null) {
				propertyTypesEmojis.put(id, emoji.getAsString());
			}
			if (propType.get("showInExplanation").getAsBoolean()) {
				propertyTypesExplanation.add(id);
			}
			if (propType.get("filterable").getAsBoolean()) {
				filterablePropertyTypes.add(id);
			}
			String table = propType.get("dbMapping").getAsJsonObject().get("tableName").getAsString();
			String column = propType.get("dbMapping").getAsJsonObject().get("columnName").getAsString();
			map.put(id, new PropertyMapping(id, table, column));
		}
		
		JsonArray propertyTypesDetailsLayout = confJson.getAsJsonArray("propertyTypesDetailsLayout");
		
		for (int i = 0; i < propertyTypesDetailsLayout.size(); i++) {
			propertyTypesDetails.add(propertyTypesDetailsLayout.get(i).getAsString());
		}
		
		JsonArray swPreference = confJson.getAsJsonArray("stopWordsForPreference");
		for (int i = 0; i < swPreference.size(); i++) {
			stopWordsForPreference.add(swPreference.get(i).getAsString());
		}
		
		String entityEmoji = confJson.get("entityEmoji").getAsString();
		
		JsonArray swCritiquing = confJson.getAsJsonArray("stopWordsForCritiquing");
		for (int i = 0; i < swCritiquing.size(); i++) {
			stopWordsForCritiquing.add(swCritiquing.get(i).getAsString());
		}
		
		JsonArray swFilter = confJson.getAsJsonArray("stopWordsForRecommendationFilters");
		for (int i = 0; i < swFilter.size(); i++) {
			stopWordsForRecommendationFilters.add(swFilter.get(i).getAsString());
		}
		
		JsonArray popEntitiesJson = confJson.getAsJsonArray("popularEntities");
		for (int i = 0; i < popEntitiesJson.size(); i++) {
			popularEntities.add(popEntitiesJson.get(i).getAsString());
		}
		
		String dbUrl = confJson.get("databaseConf").getAsJsonObject().get("dbUrl").getAsString();
		String dbName = confJson.get("databaseConf").getAsJsonObject().get("dbName").getAsString();
		String dbUser = confJson.get("databaseConf").getAsJsonObject().get("dbUser").getAsString();
		String dbPass = confJson.get("databaseConf").getAsJsonObject().get("dbPass").getAsString();
		
		int numFreeTextEntities = confJson.get("numFreeTextPreferences").getAsInt();
		int numSuggestedItems = confJson.get("numSuggestedItems").getAsInt();
		String actLearnFunction = confJson.get("actLearnFunction").getAsString();
		int recListSize = confJson.get("recListSize").getAsInt();
		int maxCycles = confJson.get("maxCycles").getAsInt();
		int minPreferencesFirstSession = confJson.get("minPreferencesFirstSession").getAsInt();
		int minPreferencesSecondSession = confJson.get("minPreferencesSecondSession").getAsInt();
		
		String recsysAlgorithm = confJson.get("recsysConf").getAsJsonObject().get("recsysAlgorithm").getAsString();
		String recsysUrl = confJson.get("recsysConf").getAsJsonObject().get("recsysUrl").getAsString();
		int recsysIndex = confJson.get("recsysConf").getAsJsonObject().get("recsysIndex").getAsInt();
		
		return new Configuration(propertyTypes.toArray(new String[propertyTypes.size()]), 
				propertyTypesExplanation.toArray(new String[propertyTypesExplanation.size()]), 
				propertyTypesDetails.toArray(new String[propertyTypesDetails.size()]), 
				filterablePropertyTypes.toArray(new String[filterablePropertyTypes.size()]),
				propertyTypesLabels,
				propertyTypesEmojis,
				propertyTypeName,
				propertyTypeReleaseYear,
				propertyTypeImage,
				propertyTypeTrailer,
				propertyTypeRuntimeMinutes,
				stopWordsForPreference.toArray(new String[stopWordsForPreference.size()]), 
				stopWordsForCritiquing.toArray(new String[stopWordsForCritiquing.size()]), 
				stopWordsForRecommendationFilters.toArray(new String[stopWordsForRecommendationFilters.size()]), 
				entityEmoji,
				popularEntities.toArray(new String[popularEntities.size()]),
				new PropertyMapper(map),
				dbUrl,
				dbName,
				dbUser,
				dbPass,
				sentimentExtractorBasePath,
				entityExtractorBasePath,
				interactionType,
				recSysServiceBasePath,
				dialogFlowToken,
				dialogflowV2CredentialsPath,
				dialogflowV2AgentName,
				doQuestionnaire,
				questionnaire,
				isPropertyTypeDisambiguationEnabled,
				numFreeTextEntities,
				numSuggestedItems,
				actLearnFunction,
				recListSize,
				maxCycles,
				minPreferencesFirstSession,
				minPreferencesSecondSession,
				recsysAlgorithm,
				recsysUrl,
				recsysIndex);
	}
	
	public static Question[] readQuestionnaireFromJson(JsonObject confJson) {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(confJson.get("questionnaire"), Question[].class);
	}
	
	public static void main(String[] args) {
		Configuration c = Configuration.getDefaultConfiguration();
		System.out.println("propertyTypes: " + Arrays.toString(c.getPropertyTypes()));
		System.out.println("propertyTypesDetails: " + Arrays.toString(c.getPropertyTypesDetails()));
		System.out.println("propertyTypesExplanation: " + Arrays.toString(c.getPropertyTypesForExplanation()));
		System.out.println("filterablePropertyTypes: " + Arrays.toString(c.getFilterablePropertyTypes()));
		System.out.println("propertyTypesLabels: " + c.getPropertyTypesLabels());
		System.out.println("propertyTypeName: " + c.getPropertyTypeName());
		System.out.println("propertyTypeImage: " + c.getPropertyTypeImage());
		System.out.println("stopWordsForPreference: " + Arrays.toString(c.getStopWordsForPreference()));
		System.out.println("stopWordsForCritiquing: " + Arrays.toString(c.getStopWordsForCritiquing()));
	}

}
