package it.uniba.swap.mler.entityrecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import it.uniba.swap.mler.utils.FileUtils;

public class LuceneKeywordFinder implements EntityLinker {
	private static Map<String, Entity> entities;
	private static Directory directory;
	
	/*
	 * The constants ALPHA and BETA are used as weights when calculating the final score.
	 * ALPHA is the weight for the similarity ratio, BETA is the weight for Lucene's score.
	 */
	private static final double ALPHA = 1.0;
	private static final double BETA = 1.0;
	
	public LuceneKeywordFinder() {
		if (entities == null) {
			readEntities();
			
			IndexWriter writer = null;
			try {
				WhitespaceAnalyzer standardAnalyzer = new WhitespaceAnalyzer();
				Directory dir = new RAMDirectory();
				IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
				writer = new IndexWriter(dir, config);
				for (Entry<String, Entity> entry : entities.entrySet()) {
					String[] aliases = entry.getValue().getAliases();
					for (int i = 0; i < aliases.length; i++) {
						String alias = aliases[i];
						Document document = new Document();
						document.add(new TextField("content", alias, Field.Store.YES));
						document.add(new TextField("id", entry.getValue().getID(), Field.Store.YES));
						document.add(new TextField("alias_index", i + "", Field.Store.YES));
						writer.addDocument(document);
					}
				}
				directory = dir;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				try {
					if (writer != null) {
						writer.commit();
						writer.close();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public List<Match> findEntities(String text, double minLengthRatio) {
		return findEntities(text, null, minLengthRatio);
	}

	private double getLengthRatio(String text, String alias) {
		return Math.min((double) text.length() / alias.length(), (double) alias.length() / text.length());
	}

	@Override
	public List<Match> findEntities(String text, String[] entityTypes, double minLengthRatio) {
		List<Match> found = new ArrayList<>();
		Set<String> foundIDs = new HashSet<String>();
		Query query = getQuery(text);
		IndexReader reader;
		try {
			reader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopDocs docs = searcher.search(query, 10);
			ScoreDoc[] hits = docs.scoreDocs;
			for(int i = 0; i < hits.length; ++i) {
			    int docId = hits[i].doc;
			    Document d = searcher.doc(docId);
			    int aliasIndex = Integer.parseInt(d.get("alias_index"));
			    String entityID = d.get("id");
			    Entity e = entities.get(entityID);
			    double lengthRatio = getLengthRatio(text, d.get("content"));
			    if (!foundIDs.contains(entityID) 
			    		&&	(entityTypes == null || isInEntityTypeList(e.getType(), entityTypes))
			    		&& lengthRatio >= minLengthRatio) {
				    String alias = e.getAliases()[aliasIndex];
				    int minMatch = e.getMinMatches()[aliasIndex];
					float maxScore = hits[0].score;
					int score = getScore(maxScore, hits[i].score);
					int simRatio = FuzzySearch.tokenSortPartialRatio(text, d.get("content"));
					/*
					 * The final match value is a weighted average of the Lucene score and
					 * the similarity ratio between query and entity name. If ALPHA is higher than 
					 * BETA, more results will be returned, increasing recall but decreasing precision,
					 * and vice versa. The similarity ratio is useful because Lucene tends to 
					 * return a low score when searching a long entity with few exact keywords
					 */
					int match = (int) ((double) (BETA * score + ALPHA * simRatio) / (BETA + ALPHA));
					if (match >= minMatch) {
					    Match m = new Match(e.getID(), e.getType(), alias, match, lengthRatio);
					    found.add(m);
					    foundIDs.add(e.getID());
					}
			    }
			}
		} catch (IOException e) {
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
		LuceneKeywordFinder sc = new LuceneKeywordFinder();
		List<Match> res = sc.findEntities("EUR", 0);
		for (Match m: res) {
			System.out.println(m.getEntityID() + " " + m.getMatchedName() + " " + m.getMatch());
		}
	}
	
	/*
	 * Calculates a score that is derived from Lucene's own scoring system.
	 * This score is the fraction of the maximum score achieved. This means
	 * that the result is an integer that is comprised between 0 and 100.
	 */
	private static int getScore(float max, float value) {
		return (int) ((value / max) * 100);
	}
	
	private static Query getQuery(String str) {
		String[] split = str.split("\\s");
		if (split.length > 1) {
			SpanQuery[] clauses = new SpanQuery[split.length];
			for (int i = 0; i < clauses.length; i++) {
			    clauses[i] = new SpanMultiTermQueryWrapper<FuzzyQuery>(new FuzzyQuery(new Term("content", split[i].toLowerCase()), 2));
			}
		    return new SpanNearQuery(clauses, 3, true);
		} else {
			return new FuzzyQuery(new Term("content", str.toLowerCase()));
		}
	}

	private static void readEntities() {
		entities = new HashMap<String, Entity>();
		try {
			ClassLoader classLoader = LuceneKeywordFinder.class.getClassLoader();
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
