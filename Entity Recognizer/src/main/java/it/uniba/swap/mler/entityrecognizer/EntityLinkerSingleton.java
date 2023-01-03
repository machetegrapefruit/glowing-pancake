package it.uniba.swap.mler.entityrecognizer;

import java.io.IOException;

import com.google.gson.JsonObject;

import it.uniba.swap.mler.utils.Configuration;

public class EntityLinkerSingleton {
	private static EntityLinker linker;
	
	public static EntityLinker getLinker() {
		if (linker == null) {
			JsonObject configuration;
			try {
				configuration = Configuration.getConfiguration();
				String linkerType = configuration.get("linkerConfig").getAsString();
				linker = getLinkerFromString(linkerType);
			} catch (IOException e) {
				System.err.println("Cannot read the configuration file");
				e.printStackTrace();
			}
		}
		return linker;
	}
	
	public static void setLinker(EntityLinker aLinker) {
		linker = aLinker;
	}
	
	private static EntityLinker getLinkerFromString(String linkerType) {
		switch(linkerType) {
		case "lucene": 
			return new LuceneKeywordFinder();
		case "lucene-sc":
			return new LuceneSCKeywordFinder();
		case "combined":
			return new CombinedKeywordFinder();
		case "fuzzy":
		default:
			return new FuzzyKeywordFinder();
		}
	}
}
