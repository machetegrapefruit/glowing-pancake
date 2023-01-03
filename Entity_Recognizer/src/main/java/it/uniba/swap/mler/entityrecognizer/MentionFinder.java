package it.uniba.swap.mler.entityrecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;
import org.joda.time.Partial;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasOffset;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.BeginIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.EndIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.MentionsAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreNLPProtos.Sentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.QuoteAttributionAnnotator.MentionAnnotation;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.time.SUTime.Temporal;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import it.uniba.swap.mler.exception.TimexParsingException;
import it.uniba.swap.mler.utils.Configuration;
import it.uniba.swap.mler.utils.FileUtils;
import it.uniba.swap.mler.utils.IndexRange;

/**
 * This class implements some Entity Recognition and Linking functions by combining
 * CoreNLP and fuzzy string matching.
 * Entity mentions are recognized by a combination of three models:
 * - A RegexNER that matches exact mentions
 * - A custom CRF model that can match non-exact mentions
 * - The standard CRF models provided by CoreNLP, which can extract numbers, person and organization names
 * The entities that require linking are then passed to the FuzzyKeywordFinder class,
 * which will try to link each mention to a particular entity.
 * Since CRF models do not work well without a full sentence, the entity recognizer can work in two modes:
 * - Sentence mode: the previously described behavior is applied.
 * - Answer mode: Fuzzy string matching is directly applied. CoreNLP may or may not be used depending
 *   on the entity types that need to be recognized.
 * The EntityFinder class is configurable, information about the entity types must be entered. 
 * 
 * This class differs from the EntityFinder class because it organizes the results by mention.
 * Candidate entities belonging to the same mention will be added to the same MentionMap, while
 * different mentions will have different MentionMaps. E.g. if the findEntities method returns a list of
 * two MentionMap objects, that means that the system has recognized two separate mentions.
 * @author Andrea Iovine
 *
 */
public class MentionFinder implements IMentionFinder {
	private static StanfordCoreNLP pipeline;
	private static HashMap<String, ERProperty> properties;
	private double minMatchRatio = 0.3;
	private boolean useParser;
	private Set<String> constituents;
	
	/*public EntityFinder() {
		if (pipeline == null) {
			Properties props = new Properties();
			props.put("annotators", "tokenize, ssplit, pos, lemma, ner, regexner, entitymentions");
			props.put("regexner.mapping", "D:\\entitiesregex.train");
			pipeline = new StanfordCoreNLP(props);
		}
	}
	
	public List<Entity> findEntities(String text) {
		List<Entity> entities = new ArrayList<>();
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		System.out.println(document);
		//List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<CoreLabel> tokens = document.get(TokensAnnotation.class);
		for (CoreLabel token: tokens) {
			String type = token.get(NamedEntityTagAnnotation.class);
			String value = token.toString();
			System.out.println(type + " " + value);
		}
		
		List<CoreMap> mentions = document.get(MentionsAnnotation.class);
		for (CoreMap mention: mentions) {
			String type = mention.get(NamedEntityTagAnnotation.class);
			String value = mention.toString();
			if (type.equals("PERSON")) {
				type = "sys-person";
			} else if (type.equals("NUMBER")) {
				type = "sys-number";
			} else if (type.equals("DATE")) {
				//Check if this entity is a number
				try {
					double numberValue = Double.parseDouble(mention.toString());
					//If it is a number, add another entity
					Entity numberEntity = new Entity("sys-number", value);
					entities.add(numberEntity);
				} catch (NumberFormatException e) {
					//Do nothing
				}
			}
			entities.add(new Entity(type, value));
		}
	
		return entities;
	}*/
	
	public MentionFinder(StanfordCoreNLP pipeline) {
		this.pipeline = pipeline;
		if (properties == null) {
			readEntityProperties();
		}
	}
	
	public MentionFinder() {
		if (pipeline == null) {
			
			ClassLoader classLoader = getClass().getClassLoader();
			String model = classLoader.getResource("model.txt").getPath();
			String regex = classLoader.getResource("entitiesregex.train").getPath();
			String sutime = "edu/stanford/nlp/models/sutime/defs.sutime.txt,edu/stanford/nlp/models/sutime/english.sutime.txt,edu/stanford/nlp/models/sutime/english.holidays.sutime.txt," + 
					classLoader.getResource("sutime.custom.txt").getPath();
			
			Properties props = new Properties();
			props.put("annotators", "tokenize, ssplit, pos, lemma, parse, ner, regexner, entitymentions");
			props.put("ner.combinationMode", "HIGH_RECALL");
			props.put("regexner.mapping", regex);
			props.put("regexner.ignorecase", "true");
			props.setProperty("sutime.includeRange", "true");
			props.setProperty("sutime.rules", sutime);
//			props.put("ner.model", model + ","
//					+ "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz,"
//					+ "edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz,"
//					+ "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz");
			props.put("ner.model", model);
			pipeline = new StanfordCoreNLP(props);
			pipeline.addAnnotator(new TimeAnnotator("sutime", props));
		}
		if (properties == null) {
			readEntityProperties();
		}
	}
	
	public void addEntitiesFromCoreNLP(String text, String[] types, boolean findDates, boolean fullTextMode, Annotation document, EntityLinker fkf, List<MentionMap> entities, List<IndexRange> foundRanges) {
		List<CoreLabel> tokens = document.get(TokensAnnotation.class);
		for (CoreLabel token: tokens) {
			String type = token.get(NamedEntityTagAnnotation.class);
			String value = token.toString();
			System.out.println(type + " " + value);
		}

		List<CoreMap> mentions = document.get(MentionsAnnotation.class);
		for (CoreMap mention: mentions) {
			String type = mention.get(NamedEntityTagAnnotation.class);
			String value = mention.toString();
			boolean found = false;
			int start = mention.get(CharacterOffsetBeginAnnotation.class);
			int end = mention.get(CharacterOffsetEndAnnotation.class);
			IndexRange range = new IndexRange(start, end);
			List<CoreLabel> mentionTokens = mention.get(TokensAnnotation.class);
			CoreLabel firstToken = mentionTokens.get(0);
			CoreLabel lastToken = mentionTokens.get(mentionTokens.size() - 1);
			IndexRange tokenRange = new IndexRange(firstToken.index() - 1, lastToken.index() - 1);
			MentionMap currentMention = new MentionMap(range, tokenRange, value);

			for (ERProperty p: properties.values()) {
				if (p.getCorenlpName().equals(type)) {
					found = true;
					type = p.getPropertyName();
					if (p.requiresLinking()) {
						String[] typeArray = {p.getPropertyName()};
						List<Match> res = fkf.findEntities(value, typeArray, minMatchRatio);
						if (res != null && res.size() > 0) {
							foundRanges.add(new IndexRange(start, end));
							add(currentMention, res, types, range);
						}
					} else {
						foundRanges.add(new IndexRange(start, end));
						add(currentMention, new Match(null, type, value, 100, 1.0), types, range);
					}
				}
			}
			if (!found) {
				if (type.equals("DATE")) {
					//Check if this entity is a number
					try {
						double numberValue = Double.parseDouble(mention.toString());
						//If it is a number, add another entity
						Match numberEntity = new Match(null, "sys-number", value, 100, 1.0);
						add(currentMention, numberEntity, types, range);
					} catch (NumberFormatException e) {
						//Do nothing
					}
					
					add(currentMention, new Match(null, type, value, 100, 1.0), types, range);
				} else {
					List<Match> res = fkf.findEntities(value, minMatchRatio);
					if (res != null && res.size() > 0) {
						foundRanges.add(range);
						add(currentMention, fkf.findEntities(value, minMatchRatio), types, range);
					}
				}
			}
			entities.add(currentMention);
		}
		
		if (useParser) {
			List<Tree> nps = this.getNPs(document);
			if (nps.size() > 0) {
				for (Tree np: nps) {
					List<Tree> leaves = np.getLeaves();
					CoreLabel firstLeaf = (CoreLabel) leaves.get(0).label();
					CoreLabel lastLeaf = (CoreLabel) leaves.get(leaves.size() - 1).label();
					int start = ((HasOffset) firstLeaf).beginPosition();
					int end = ((HasOffset) lastLeaf).endPosition();
					IndexRange tokenRange = new IndexRange(firstLeaf.index() - 1, lastLeaf.index() - 1);
					IndexRange range = new IndexRange(start, end);
					String txt = SentenceUtils.listToString(np.yield());
					MentionMap currentMention = new MentionMap(range, tokenRange, txt);
					System.out.print("Found " + txt);
					if (!hasIntersection(range, foundRanges)) {
						List<Match> res = fkf.findEntities(txt, minMatchRatio);
						if (res != null && res.size() > 0) {
							foundRanges.add(new IndexRange(start, end));
							add(currentMention, res, types, range);
							entities.add(currentMention);
						}
						System.out.println();
					} else {
						System.out.println(", has intersection");
					}
				}
			}
		}
	}
	
	/**
	 * Find the entity mentions in the text.
	 * @param text Text from which entities should be extracted.
	 * @param types Array of entity types that should be searched.
	 * 				When it is set to null "sentence mode" is activated.
	 * 				Otherwise "answer mode" is enabled.
	 * @param findDates When true, this method will also try to find references to dates.
	 * @return A list of entity mentions
	 */
	public List<MentionMap> findEntities(String text, String[] types, boolean findDates, boolean fullTextMode) {
		boolean useCorenlp = !fullTextMode;
		EntityLinker fkf = EntityLinkerSingleton.getLinker();

		List<MentionMap> entities = new ArrayList<MentionMap>();
		if (!useCorenlp) {
			//Answer mode
			MentionMap onlyMention = new MentionMap(new IndexRange(0, text.length() - 1), new IndexRange(0, text.length() - 1) , text);	//TODO: token index range should be (0, numTokens - 1)
			for (int i = 0; !useCorenlp && types != null  && i < types.length; i++) {
				if (properties.containsKey(types[i]) && properties.get(types[i]).requiresCorenlp()) {
					useCorenlp = true;
				}
	 		}
			if (types == null) {
				add(onlyMention, fkf.findEntities(text, minMatchRatio), types, new IndexRange(0, text.length() - 1));
			} else {
				add(onlyMention, fkf.findEntities(text, types, minMatchRatio), types, new IndexRange(0, text.length() - 1));
			}
			entities.add(onlyMention);
		}
		
		List<IndexRange> foundRanges = new ArrayList<>();
		if (useCorenlp) {
			Annotation document = new Annotation(text);
			DateTime d = DateTime.now();
			//document.set(CoreAnnotations.DocDateAnnotation.class, d.toString());
			pipeline.annotate(document);
			System.out.println(document);
			//List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			this.addEntitiesFromCoreNLP(text, types, findDates, fullTextMode, document, fkf, entities, foundRanges);
			
			// TODO: Implement time expressions
//			if (findDates) {
//				try {
//					entities.setTimeExpression(findDates(text, document));
//				} catch (TimexParsingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
		}	
		return entities;
	}
	
	private boolean hasIntersection(IndexRange r, List<IndexRange> l) {
		for (IndexRange r2: l) {
			if (r.hasIntersection(r2)) {
				return true;
			}
		}
		return false;
	}
	
	public it.uniba.swap.mler.entityrecognizer.TimeExpression findDates(String text, Annotation document) throws TimexParsingException {
		List<CoreMap> timexAnnsAll = document.get(TimeAnnotations.TimexAnnotations.class);
		DateTime now = DateTime.now().withSecondOfMinute(0);
		return TimeExpressionFinder.fromAnnotation(timexAnnsAll, now);
	}
	
//	private void findDates(String text) {
//		MentionMap mm = findEntities(text, null, true, false);
//		System.out.println(mm.getTimeExpression());
//	}
//	
	private void add(MentionMap found, List<Match> matches, String[] types, IndexRange range) {
		for (Match m: matches) {
			add(found, m, types, range);
		}
	}
	
	private void add(MentionMap found, Match m, String[] types, IndexRange range) {
		if (types == null || isInTypeList(m.getEntityType(), types)) {
			m.setIndexes(range);
			found.add(m);
		}
	}
	
	private boolean isInTypeList(String type, String[] typeList) {
		for (String s: typeList) {
			if (s != null && s.equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}
	
	private void readEntityProperties() {
		properties = new HashMap<String, ERProperty>();
		try {
			Gson gson = new GsonBuilder().create();
			JsonObject configuration = Configuration.getConfiguration();
			JsonObject erProperties = configuration.get("erProperties").getAsJsonObject();
			for (Entry<String, JsonElement> e: erProperties.entrySet()) {
				ERProperty ep = gson.fromJson(e.getValue().toString(), ERProperty.class);
				properties.put(e.getKey(), ep);
			}
			JsonObject mentionFinderProperties = configuration.get("mentionFinderProperties").getAsJsonObject();
			useParser = mentionFinderProperties.get("useParser").getAsBoolean();
			if (useParser) {
				constituents = new HashSet<String>();
				JsonArray constituentsArray = mentionFinderProperties.get("constituents").getAsJsonArray();
				for (JsonElement constituent: constituentsArray) {
					constituents.add(constituent.getAsString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns entities found in a previously already annotated text
	 * @param text Text message
	 * @param findDates unused
	 * @param fullTextMode unused
	 * @param document CoreNLP annotated document
	 * @return
	 */
	public List<MentionMap> findEntities(String text, boolean findDates, boolean fullTextMode, Annotation document) {
		EntityLinker fkf = EntityLinkerSingleton.getLinker();
		List<MentionMap> entities = new ArrayList<MentionMap>();
		List<IndexRange> foundRanges = new ArrayList<IndexRange>();
		
		this.addEntitiesFromCoreNLP(text, null, findDates, fullTextMode, document, fkf, entities, foundRanges);
		return entities;
	}
	
	public List<MentionMap> findEntities(String text, boolean findDates, boolean fullTextMode) {
		return this.findEntities(text, null, findDates, fullTextMode);
	}
	
	public static void main(String[] args) {
		System.out.println(new MentionFinder().findEntities("Invest 120 EUR in blackrock", false, false));
		/*new EntityFinder().findDates("What is my portfolio performance in the last two weeks?");
		new EntityFinder().findDates("What is my portfolio performance this year?");
		new EntityFinder().findDates("What is my portfolio performance last year?");
		new EntityFinder().findDates("What is my portfolio performance from Jan 1st 2018 to April 3rd?");
		new EntityFinder().findDates("What is my portfolio performance since 10 days ago?");
		new EntityFinder().findDates("What is my portfolio performance since the start of the contract");
		new EntityFinder().findDates("What is my portfolio performance since contract");
		EntityFinder ef = new EntityFinder();
		List<Tree> nps = ef.getNPs("I want to buy 250 apple inc");
		FuzzyKeywordFinder fkf = new FuzzyKeywordFinder();
		for(Tree np: nps) {
			String text = SentenceUtils.listToString(np.yield());
			System.out.println(text);
			List<Match> matches = fkf.findEntities(text);
			for (Match m: matches) {
				System.out.println(m.getMatchedName() + " " + m.getMatch());
			}
		}
		System.out.println(nps);*/
	}
	
	private List<Tree> getNPs(Annotation document) {
		List<Tree> nps = new ArrayList<>();
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence: sentences) {
			Tree t = sentence.get(TreeAnnotation.class);
			System.out.println(t);
			List<Tree> visitList = t.getChildrenAsList();
			while (!visitList.isEmpty()) {
				Tree child = visitList.remove(0);
				Label label = child.label();
				if (constituents.contains(label.toString())) {
					System.out.println("Found " + label.toString() + " " + SentenceUtils.listToString(child.yield()));
					nps.add(child);
				}
				visitList.addAll(child.getChildrenAsList());
			}
		}
		return nps;
	}
			
}
