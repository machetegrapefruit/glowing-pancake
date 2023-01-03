package functions;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import configuration.Configuration;
import entity.AuxAPI;
import utils.PropertyFilter;

public class AuxiliaryRequestService {
	/**
	 * Returns an AuxAPI object that contains all the data required to invoke
	 * the GetFirstRecommendation auxiliary API.
	 */
	public AuxAPI getAuxRequestForRequestRecommendation(String userID, String messageID, List<PropertyFilter> filters) {
		Configuration configuration = Configuration.getDefaultConfiguration();
		String apiURL = configuration.getRecSysServiceBasePath() + "/getFirstRecommendation";
		JsonObject parameters = new JsonObject();
		parameters.addProperty("messageID", messageID);
		parameters.add("filters", new Gson().toJsonTree(filters));
		parameters.addProperty("userID", userID);
		return new AuxAPI(apiURL, null, parameters);
	}
}
