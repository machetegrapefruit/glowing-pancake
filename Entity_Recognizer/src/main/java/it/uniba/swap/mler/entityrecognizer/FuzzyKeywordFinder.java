package it.uniba.swap.mler.entityrecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import it.uniba.swap.mler.utils.FileUtils;

public class FuzzyKeywordFinder implements EntityLinker {
	private static Map<String, List<Entity>> entities;
	private static final boolean CHECK_FIRST_CHAR = false;
	
	public FuzzyKeywordFinder() {
		if (entities == null) {
			readEntities();
		}
	}
	
	/*public List<Entity> findEntities(String text) {
		List<Entity> found = new ArrayList<>();
		String[] split = text.split(" ");
		for (Entity e: entities) {
			int len = e.getValue().split(" ").length;
			int start = 0;
			int end = start + len;
			while (end <= split.length) {
				int j = start;
				StringJoiner sj = new StringJoiner(" ");
				while (j < end) {
					sj.add(split[j]);
					j++;
				}
				String ngram = sj.toString().toLowerCase();
				int match = FuzzySearch.ratio(ngram, e.getValue());
				if (match > 90) {
					found.add(e);
				}
				start++; end++;
			}
		}
		return found;
	}*/
	
	/**
	 * Finds entities in text.
	 */
	public List<Match> findEntities(String text, double minLengthRatio) {
		List<Match> found = new ArrayList<>();
		for (List<Entity> list: entities.values()) {
			//For each entity type
			for (Entity e: list) {
				//For each entity of this type
				double bestMatch = -1;
				double bestLengthRatio = -1;
				String bestAlias = "";
				for (int i = 0; i < e.getAliases().length; i++) {
					//For each alias of this entity
					String alias = e.getAliases()[i];
					int minMatch = e.getMinMatches()[i];
					double lengthRatio = getLengthRatio(text, alias);
					if (lengthRatio > minLengthRatio) {
						//If at least the minimum ratio of the entity has been mentioned
						double match = FuzzySearch.ratio(alias, text.toLowerCase());
						if (match >= minMatch) {
							//Check if the minimum similarity is found
							//Find the best alias for this entity
							if (match > bestMatch) {
								bestMatch = match;
								bestAlias = alias;
								bestLengthRatio = lengthRatio;
							} else if (match == bestMatch) {
								//In case multiple aliases have a 100 match, the best alias is the shorter one
								if (lengthRatio > bestLengthRatio) {
									bestMatch = match;
									bestAlias = alias;
									bestLengthRatio = lengthRatio;
								}
							}
						}	
					}
				}
				if (bestMatch > -1) {
					//Add a Match object, if a match has been found
					found.add(new Match(e.getID(), e.getType(), bestAlias, bestMatch, bestLengthRatio));
				}
			}
		}
		return found;
	}
	
	private double getLengthRatio(String text, String alias) {
		return Math.min((double) text.length() / alias.length(), (double) alias.length() / text.length());
	}
	
	public List<Match> findEntities(String text, String[] entityTypes, double minLengthRatio) {
		List<Match> found = new ArrayList<>();
		for (String type: entityTypes) {
			//For each entity type specified in entityTypes
			List<Entity> list = entities.get(type);
			if (list != null) {
				for (Entity e: list) {
					//For each entity of this type
					double bestMatch = -1;
					String bestAlias = "";
					double bestLengthRatio = -1;
					for (int i = 0; i < e.getAliases().length; i++) {
						//For each alias of this entity
						String alias = e.getAliases()[i];
						int minMatch = e.getMinMatches()[i];
						double lengthRatio = getLengthRatio(text, alias);
						if (lengthRatio > minLengthRatio) {
							//If at least the minimum ratio of the entity has been mentioned
							double match = FuzzySearch.ratio(alias, text.toLowerCase());
							if (match >= minMatch) {
								//Check if the minimum similarity is found
								//Find the best alias for this entity
								if (match > bestMatch) {
									bestMatch = match;
									bestAlias = alias;
									bestLengthRatio = lengthRatio;
								} else if (match == bestMatch) {
									//In case multiple aliases have a 100 match, the best alias is the shorter one
									if (lengthRatio > bestLengthRatio) {
										bestMatch = match;
										bestAlias = alias;
										bestLengthRatio = lengthRatio;
									}
								}
							}	
						}
					}
					if (bestMatch > -1) {
						found.add(new Match(e.getID(), e.getType(), bestAlias, bestMatch, bestLengthRatio));
					}
				}
			}
		}
		return found;
	}
	
	/*public List<Entity> findEntities3(String text) {
		List<Entity> found = new ArrayList<>();
		for (Entity e: entities) {
			int match = FuzzySearch.tokenSetPartialRatio(e.getValue(), text.toLowerCase());
			if (match > 90) {
				found.add(e);
			}
		}
		return found;
	}*/
	

	
	private void readEntities() {
		entities = new HashMap<String, List<Entity>>();
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			List<String> entStr = FileUtils.readFileAsList(classLoader.getResource("entitiesfuzzy.train").getPath());
			for (String s: entStr) {
				String[] split = s.split(",");
				String id = split[0];
				String[] aliases = split[1].split("\\|");
				for (int i = 0; i < aliases.length; i++) {
					aliases[i] = aliases[i].toLowerCase();
				}
				String[] mmStr = split[2].split("\\|");
				int[] minMatches = new int[mmStr.length];
				for (int i = 0; i < minMatches.length; i++) {
					minMatches[i] = Integer.parseInt(mmStr[i]);
				}
				String type = split[3];
				Entity e = new Entity(id, type, aliases, minMatches);
				if (!entities.containsKey(type)) {
					List<Entity> l = new ArrayList<>();
					l.add(e);
					entities.put(type, l);
				} else {
					entities.get(type).add(e);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		FuzzyKeywordFinder sc = new FuzzyKeywordFinder();
		List<Match> res = sc.findEntities("bayer", 0);
		for (Match m: res) {
			System.out.println(m.getEntityID() + " " + m.getMatchedName() + " " + m.getMatch());
		}
		//System.out.println(sc.findEntities("horizon finance", 0));
		//System.out.println(FuzzySearch.tokenSetRatio("trust vident international", "ETF Series Solutions Trust Vident International Equity Fund"));
		/*String[] testSentences = {
				"Invest in funds",
				"Send 500 GBP to Alice",
				"international",
				"internatonal",
				"Send 1200 EUR to Bob Jones at bank C",
				"What is the value of my trading account?",
				"barclay"
		};
		FuzzyKeywordFinder fkf = new FuzzyKeywordFinder();
		long start = System.currentTimeMillis();
		for (String s: testSentences) {
			System.out.println(fkf.findEntities(s));
		}
		long end = System.currentTimeMillis();
		System.out.println("Done in " + ((end - start) / 1000.0) + " seconds");*/
		/*start = System.currentTimeMillis();
		for (String s: testSentences) {
			System.out.println(fkf.findEntities2(s));
		}
		end = System.currentTimeMillis();
		System.out.println("Done in " + ((end - start) / 1000.0) + " seconds");
		start = System.currentTimeMillis();
		for (String s: testSentences) {
			System.out.println(fkf.findEntities3(s));
		}
		end = System.currentTimeMillis();
		System.out.println("Done in " + ((end - start) / 1000.0) + " seconds");*/
	}
}
