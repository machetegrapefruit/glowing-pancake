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

public class CombinedKeywordFinder implements EntityLinker {
	private static Map<String, Entity> entities;
	private static Directory directory;
	
	public CombinedKeywordFinder() {
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
		String[] split = text.split("\\s+");
		List<Query> queries = new ArrayList<Query>();
		for (String s: split) {
			String sl = s.toLowerCase();
			Query q = new FuzzyQuery(new Term("content", sl));
			queries.add(q);
		}
		IndexReader reader;
		try {
			reader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			for (Query q: queries) {
				TopDocs docs = searcher.search(q, 50);
				ScoreDoc[] hits = docs.scoreDocs;
				for (int i = 0; i < hits.length; i++) {
				    int docId = hits[i].doc;
				    Document d = searcher.doc(docId);
					String entityID = d.get("id");
					if (!foundIDs.contains(entityID)) {
						foundIDs.add(entityID);
					}
				}
			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String id: foundIDs) {
			int bestMatch = -1;
			String bestAlias = "";
			double bestRatio = -1;
			Entity e = entities.get(id);
			String[] aliases = e.getAliases();
			for (int j = 0; j < aliases.length; j++) {
				int minMatch = e.getMinMatches()[j];
				int match = FuzzySearch.tokenSetRatio(aliases[j], text.toLowerCase());
				double lengthRatio = getLengthRatio(text, aliases[j]);
				if (match >= minMatch && match > bestMatch) {
					bestMatch = match;
					bestAlias = aliases[j];
					bestRatio = lengthRatio;
				}	
			}
			if (bestMatch > -1) {
				found.add(new Match(e.getID(), e.getType(), bestAlias, bestMatch, bestRatio));
			}
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
		CombinedKeywordFinder sc = new CombinedKeywordFinder();
		List<Match> res = sc.findEntities("blackrock", 0);
		for (Match m: res) {
			System.out.println(m.getEntityID() + " " + m.getMatchedName() + " " + m.getMatch());
		}
		//System.out.println(sc.findEntities("demar pharmaceuticals", 0));
	}
	
	private static void readEntities() {
		entities = new HashMap<String, Entity>();
		try {
			ClassLoader classLoader = CombinedKeywordFinder.class.getClassLoader();
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
