package it.uniba.swap.mler.entityrecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

import it.uniba.swap.mler.utils.FileUtils;
import it.uniba.swap.mler.utils.Pair;

public class EntityFinderTester {
	private static int NUM_REMOVED = 2;
	private static boolean REPLACE = false;
	private static String prepend = "";
	private static final String CSV_HEADER = "strategy, precision, recall, f1score, average time";
	
	public static void main(String[] args) {
		String path = null;
		if (args.length > 0) {
			NUM_REMOVED = Integer.parseInt(args[0]);
			REPLACE = Boolean.parseBoolean(args[1]);
			if (args.length == 3) {
				path = args[2];
			} else if (args.length > 3) {
				prepend = args[2];
				path = args[3];
			}
		}
		StringJoiner sj = new StringJoiner("\n");
		sj.add(CSV_HEADER);
		HashMap<String, List<Entity>> entities = readEntities();
		IEntityFinder ef = EntityFinderFactory.getEntityFinder();
		
		List<Entity> products = entities.get("product");
		int total = 0;
		long[] totalTimes = new long[9];
		String[] settings = {"np fw", "full fw", "np lucene", "full lucene", "np luceneSC", "full luceneSC", "np luceneComb", "full luceneComb", "her"};
		List<String> names = new ArrayList<>();
		List<String> ids = new ArrayList<>();
		List<boolean[]> res = new ArrayList<>();
		List<int[]> numRes = new ArrayList<>();
		for (int i = 0; i < products.size(); i++) {
			total++;
			Entity e = products.get(i);
			System.out.println("Entity " + e.getValue() + " (" + e.getID() + ")");
			String[] aliases = e.getAliases();
			String name = aliases[aliases.length - 1];
			names.add(prepend + getWrongName(name));
			ids.add(e.getID());
		}
		
		findEntity("test", "test", false, settings[0]);
		
		for (int i = 0; i < names.size(); i++) {
			boolean[] p = new boolean[settings.length];
			int[] np = new int[settings.length];
			for (int j = 0; j < settings.length; j++) {
				boolean full = setMode(settings[j]);
				Pair<Pair<Boolean, Integer>, Long> fer = findEntity(ids.get(i), names.get(i), full, settings[j]);
				p[j] = fer.getFirst().getFirst();
				np[j] = fer.getFirst().getSecond();
				totalTimes[j] += fer.getSecond();
			}
			res.add(p);
			numRes.add(np);
		}
		int[] foundCounts = new int[settings.length];
		int[] resultCounts = new int[settings.length];
		for (int j = 0; j < res.size(); j++) {
			boolean[] r = res.get(j);
			for (int i = 0; i < foundCounts.length; i++) {
				if (r[i]) {
					foundCounts[i]++;
				}
				resultCounts[i] += numRes.get(j)[i];
			}
		}
		for (int i = 0; i < settings.length; i++) {
			double avgTime = ((double) totalTimes[i] / total) / 1000.0;
			double precision = (double) foundCounts[i] / resultCounts[i];
			double recall = (double) foundCounts[i] / total;
			double f1score = 2 * (precision * recall) / (precision + recall);
			System.out.println("Average time " + settings[i] + ": " + avgTime);
			System.out.println("Recall " + settings[i] + ": " + recall);
			System.out.println("Precision " + settings[i] + ": " + precision);
			System.out.println("f1score " + settings[i] + ": " + f1score);
			sj.add(printCSVLine(settings[i], precision, recall, f1score, avgTime));
		}
		
		FileUtils.writeToFile(path, sj.toString());

	}
	
	private static String printCSVLine(String strategy, double precision, double recall, double f1score, double time) {
		return strategy + "," + precision + "," + recall + "," + f1score + "," + time;
	}
	
	private static String getWrongName(String name) {
		String wrong = name + "";
		for (int k = 0; wrong.length() > 2 && k < NUM_REMOVED; k++) {
			int randomIndex = (int) (Math.random() * (wrong.length() - 1));
			while (wrong.charAt(randomIndex) == ' ') {
				randomIndex = (int) (Math.random() * (wrong.length() - 1));
			}
			
			System.out.println("randomIndex is " + randomIndex);
			if (!REPLACE) {
				wrong = wrong.substring(0, randomIndex) + wrong.substring(randomIndex + 1, wrong.length());
			} else {
				Random r = new Random();
				char c = (char)(r.nextInt(26) + 'a');
				while (c == wrong.charAt(randomIndex)) {
					c = (char)(r.nextInt(26) + 'a');
				}
				wrong = wrong.substring(0, randomIndex) + c + wrong.substring(randomIndex + 1, wrong.length());
			}
		}
		return wrong;
	}
	
	private static boolean setMode(String setting) {
		if (setting.contains("her")) {
			//EntityFinderFactory.setEntityFinder(new HEREntityFinder());
		} else {
			EntityFinderFactory.setEntityFinder(new EntityFinder());
			if (setting.contains("fw")) {
				EntityLinkerSingleton.setLinker(new FuzzyKeywordFinder());
			} else if (setting.contains("luceneSC")) {
				EntityLinkerSingleton.setLinker(new LuceneSCKeywordFinder());
			} else if (setting.contains("luceneComb")) {
				EntityLinkerSingleton.setLinker(new CombinedKeywordFinder());
			} else {
				EntityLinkerSingleton.setLinker(new LuceneKeywordFinder());
			}
		}
		return setting.contains("full");
	}
	
	private static Pair<Pair<Boolean, Integer>, Long> findEntity(String entityID, String name, boolean fullText, String setting) {
		IEntityFinder ef = EntityFinderFactory.getEntityFinder();
		long start = System.currentTimeMillis();
		MatchMap results = ef.findEntities(name, false, fullText);
		long end = System.currentTimeMillis();
		List<Match> resultsProd = results.get("product");
		boolean found = false;
		int size = 0;
		if (resultsProd != null) {
			for (Match m: resultsProd) {
				if (!found && m.getEntityID().equalsIgnoreCase(entityID)) {
					found = true;
					System.out.println("Found with " + setting);
				}
			}
			size = resultsProd.size();
		}
		if (!found) {
			System.out.println("Not found with " + setting);
		}
		return new Pair<>(new Pair<>(found, size), end - start);
	}
	
	private static HashMap<String, List<Entity>> readEntities() {
		HashMap<String, List<Entity>> entities = new HashMap<String, List<Entity>>();
		try {
			ClassLoader classLoader = EntityFinderTester.class.getClassLoader();
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
		return entities;
	}
}
