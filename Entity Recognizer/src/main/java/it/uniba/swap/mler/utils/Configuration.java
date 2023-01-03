package it.uniba.swap.mler.utils;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Configuration {
	private static JsonObject configuration;
	
	public static JsonObject getConfiguration() throws IOException {
		if (configuration == null) {
			//Get default configuration file
			String json = FileUtils.readFile(Configuration.class.getClassLoader().getResource("configuration.json").getPath());
			JsonParser parser = new JsonParser();
			configuration = parser.parse(json).getAsJsonObject();
		}
		return configuration;
	}
	
	public static void setConfiguration(JsonObject aConf) {
		configuration = aConf;
	}
}
