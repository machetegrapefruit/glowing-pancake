package it.uniba.swap.mler.entityrecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Queue;

import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;
import org.joda.time.Partial;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasOffset;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
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
 * @author Andrea Iovine
 *
 */
public class EntityFinder implements IEntityFinder {
	private static StanfordCoreNLP pipeline;
	private static HashMap<String, ERProperty> properties;
	
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
	
	public EntityFinder() {
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
			props.put("ner.model", model + ","
					+ "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz,"
					+ "edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz,"
					+ "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz");
			pipeline = new StanfordCoreNLP(props);
			pipeline.addAnnotator(new TimeAnnotator("sutime", props));
			//pipeline.addAnnotator(new TokensRegexNERAnnotator("D:\\uwmp\\entitiesregex.train", true));
			//pipeline.addAnnotator(new EntityMentionsAnnotator());
		}
		if (properties == null) {
			readEntityProperties();
			/*properties = new HashMap<String, ERProperty>();
			properties.put("sys-person", new ERProperty("sys-person", "PERSON", true, false));
			properties.put("sys-number", new ERProperty("sys-number", "NUMBER", true, false));
			properties.put("currency", new ERProperty("currency", "currency", false, true));
			properties.put("Bank", new ERProperty("bank", "bank", false, true));
			properties.put("yes-no", new ERProperty("yes-no", "yes-no", false, true));
			properties.put("payment_type", new ERProperty("payment_type", "payment_type", false, true));
			properties.put("investment_type", new ERProperty("investment_type", "investment_type", false, true));
			properties.put("inv_keyword", new ERProperty("inv_keyword", "inv_keyword", false, true));
			properties.put("product", new ERProperty("product", "product", false, true));
			properties.put("Account", new ERProperty("Account", "Account", false, true));
			properties.put("Portfolio", new ERProperty("Portfolio", "Portfolio", false, true));
			properties.put("question_key", new ERProperty("question_key", "question_key", false, true));
			properties.put("help_topic", new ERProperty("help_topic", "help_topic", false, true));
			properties.put("limit_price", new ERProperty("limit_price", "limit_price", false, true));*/
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
	public MatchMap findEntities(String text, String[] types, boolean findDates, boolean fullTextMode) {
		boolean useCorenlp = !fullTextMode;
		EntityLinker fkf = EntityLinkerSingleton.getLinker();

		MatchMap entities = new MatchMap();
		if (!useCorenlp) {
			//Answer mode
			for (int i = 0; !useCorenlp && types != null  && i < types.length; i++) {
				if (properties.containsKey(types[i]) && properties.get(types[i]).requiresCorenlp()) {
					useCorenlp = true;
				}
	 		}
			if (types == null) {
				add(entities, fkf.findEntities(text, 0), types, new IndexRange(0, text.length() - 1));
			} else {
				add(entities, fkf.findEntities(text, types, 0), types, new IndexRange(0, text.length() - 1));
			}
		}
		
		List<IndexRange> foundRanges = new ArrayList<>();
		if (useCorenlp) {
			Annotation document = new Annotation(text);
			DateTime d = DateTime.now();
			//document.set(CoreAnnotations.DocDateAnnotation.class, d.toString());
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
				boolean found = false;
				int start = mention.get(CharacterOffsetBeginAnnotation.class);
				int end = mention.get(CharacterOffsetEndAnnotation.class);
				IndexRange range = new IndexRange(start, end);

				for (ERProperty p: properties.values()) {
					if (p.getCorenlpName().equals(type)) {
						found = true;
						type = p.getPropertyName();
						if (p.requiresLinking()) {
							String[] typeArray = {p.getPropertyName()};
							List<Match> res = fkf.findEntities(value, typeArray, 0);
							if (res != null && res.size() > 0) {
								foundRanges.add(new IndexRange(start, end));
								add(entities, res, types, range);
							}
						} else {
							foundRanges.add(new IndexRange(start, end));
							add(entities, new Match(null, type, value, 100, 1.0), types, range);
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
							add(entities, numberEntity, types, range);
						} catch (NumberFormatException e) {
							//Do nothing
						}
						
						add(entities, new Match(null, type, value, 100, 1.0), types, range);
					} else {
						List<Match> res = fkf.findEntities(value, 0);
						if (res != null && res.size() > 0) {
							foundRanges.add(range);
							add(entities, fkf.findEntities(value, 0), types, range);
						}
					}
				}
			}
			
			if (findDates) {
				try {
					entities.setTimeExpression(findDates(text, document));
				} catch (TimexParsingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			List<Tree> nps = this.getNPs(document);
			if (!fullTextMode && nps.size() > 0) {
				for (Tree np: nps) {
					List<Tree> leaves = np.getLeaves();
					Label firstLeaf = leaves.get(0).label();
					Label lastLeaf = leaves.get(leaves.size() - 1).label();
					int start = ((HasOffset) firstLeaf).beginPosition();
					int end = ((HasOffset) lastLeaf).endPosition();
					IndexRange range = new IndexRange(start, end);
					String txt = SentenceUtils.listToString(np.yield());
					System.out.print("Found np " + txt + range);
					if (!hasIntersection(range, foundRanges)) {

						add(entities, fkf.findEntities(txt, 0.2), types, range);
						foundRanges.add(range);
						System.out.println();
					} else {
						System.out.println(", has intersection");
					}
				}
			} else if (!fullTextMode && nps.size() == 0) {
				add(entities, fkf.findEntities(text, 0.2), types, new IndexRange(0, text.length() - 1));
			}

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
	
	private void findDates(String text) {
		MatchMap mm = findEntities(text, null, true, false);
		System.out.println(mm.getTimeExpression());
	}
	
	private void add(MatchMap found, List<Match> matches, String[] types, IndexRange range) {
		for (Match m: matches) {
			add(found, m, types, range);
		}
	}
	
	private void add(MatchMap found, Match m, String[] types, IndexRange range) {
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
			JsonObject erProperties = Configuration.getConfiguration().get("erProperties").getAsJsonObject();
			for (Entry<String, JsonElement> e: erProperties.entrySet()) {
				ERProperty ep = gson.fromJson(e.getValue().toString(), ERProperty.class);
				properties.put(e.getKey(), ep);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public MatchMap findEntities(String text, boolean findDates, boolean fullTextMode) {
		return findEntities(text, null, findDates, fullTextMode);
	}
	
	public static void main(String[] args) {
		System.out.println(new EntityFinder().findEntities("Invest 120 EUR in blackrock", false, false));
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
				if (label.toString().equals("NP") || label.toString().equals("VP") || label.toString().equals("NNP") || label.toString().equals("NN")) {
					nps.add(child);
				}
				visitList.addAll(child.getChildrenAsList());
			}
		}
		return nps;
	}
			
}
