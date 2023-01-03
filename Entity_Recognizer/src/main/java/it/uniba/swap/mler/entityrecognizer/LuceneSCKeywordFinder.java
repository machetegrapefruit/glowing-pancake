package it.uniba.swap.mler.entityrecognizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import it.uniba.swap.mler.utils.FileUtils;

public class LuceneSCKeywordFinder implements EntityLinker {
	private static Map<String, Entity> entities;
	private static SpellChecker checker;

	public LuceneSCKeywordFinder() {
		if (entities == null) {
			readEntities();

			IndexWriter writer = null;
			try {
				StringBuilder sb = new StringBuilder();
				WhitespaceAnalyzer standardAnalyzer = new WhitespaceAnalyzer();
				Directory dir = new RAMDirectory();
				Set<String> tokens = new HashSet<>();
				for (Entry<String, Entity> entry : entities.entrySet()) {
					for (String alias: entry.getValue().getAliases()) {
						String[] split = alias.split("\\s");
						for (String s: split) {
							String sl = s.toLowerCase();
							if (!tokens.contains(sl)) {
								tokens.add(sl);
								sb.append(sl);
								sb.append("\n");
							}
						}
					}
					/*
					 * String[] aliases = entry.getValue().getAliases(); for (int i = 0; i <
					 * aliases.length; i++) { String alias = aliases[i]; Document document = new
					 * Document(); document.add(new TextField("content", alias, Field.Store.YES));
					 * document.add(new TextField("id", entry.getValue().getID(), Field.Store.YES));
					 * document.add(new TextField("alias_index", i + "", Field.Store.YES));
					 * writer.addDocument(document); }
					 */
				}


				checker = new SpellChecker(dir);
				StringReader reader = new StringReader(sb.toString());
				IndexWriterConfig config2 = new IndexWriterConfig(standardAnalyzer);
		        PlainTextDictionary words = new PlainTextDictionary(reader);
				checker.indexDictionary(words, config2, false);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public List<Match> findEntities(String text, double minLengthRatio) {
		return findEntities(text, null, minLengthRatio);
	}

	@Override
	public List<Match> findEntities(String text, String[] entityTypes, double minLengthRatio) {
		List<Match> found = new ArrayList<>();
		String[] res;
		StringJoiner sj = new StringJoiner(" ");
		try {
			String[] split = text.split("\\s+");
			for (String s : split) {
				String sl = s.toLowerCase();
				if (!sl.equals("")) {
					if (checker.exist(sl)) {
						sj.add(s);
					} else {
						res = checker.suggestSimilar(sl, 10);
						if (res.length > 0) {
							System.out.println("corrected " + s + " to " + res[0]);
							sj.add(res[0]);
						} else {
							sj.add(s);
						}
					}
				}
			}
			LuceneKeywordFinder kf = new LuceneKeywordFinder();
			return kf.findEntities(sj.toString(), entityTypes, minLengthRatio);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return found;
	}

	private static boolean isInEntityTypeList(String entityType, String[] list) {
		for (int i = 0; i < list.length; i++) {
			if (list[i].equals(entityType)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) throws IOException, ParseException {
		LuceneSCKeywordFinder sc = new LuceneSCKeywordFinder();
		System.out.println(sc.findEntities("i want to buy allena", 0));
	}

	private static void readEntities() {
		entities = new HashMap<String, Entity>();
		try {
			ClassLoader classLoader = LuceneSCKeywordFinder.class.getClassLoader();
			List<String> entStr = FileUtils.readFileAsList(classLoader.getResource("entitiesfuzzy.train").getPath());
			for (String s : entStr) {
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
				entities.put(e.getID(), e);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
