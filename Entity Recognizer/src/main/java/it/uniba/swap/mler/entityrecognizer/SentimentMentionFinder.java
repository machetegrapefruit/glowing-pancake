package it.uniba.swap.mler.entityrecognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentMentionFinder {
	private static StanfordCoreNLP pipeline;
	private static MentionFinder mentionFinder;
	private ArrayList<String> currentTokens;
	private ArrayList<Integer> currentSentimentTokens;
	private ArrayList<MentionMap> entities;
	private ArrayList<CoreLabel> currentLabels;
	
	public SentimentMentionFinder() {
		if (pipeline == null) {
			
			ClassLoader classLoader = getClass().getClassLoader();
			String model = classLoader.getResource("model.txt").getPath();
			String regex = classLoader.getResource("entitiesregex.train").getPath();
			String sutime = "edu/stanford/nlp/models/sutime/defs.sutime.txt,edu/stanford/nlp/models/sutime/english.sutime.txt,edu/stanford/nlp/models/sutime/english.holidays.sutime.txt," + 
					classLoader.getResource("sutime.custom.txt").getPath();
			
			Properties props = new Properties();

			props.put("annotators", "tokenize, ssplit, pos, lemma, parse, ner, regexner, entitymentions, sentiment");
			props.put("ner.combinationMode", "NORMAL");
			props.put("regexner.mapping", regex);
			props.put("regexner.ignorecase", "true");
			props.setProperty("sutime.includeRange", "true");
			props.setProperty("sutime.rules", sutime);
			props.put("ner.model", model);
//			props.put("ner.model", model + ","
//					+ "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz,"
//					+ "edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz,"
//					+ "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz");
			pipeline = new StanfordCoreNLP(props);
			pipeline.addAnnotator(new TimeAnnotator("sutime", props));
		}
		
		currentTokens = new ArrayList<String>();
		currentSentimentTokens = new ArrayList<Integer>();
		currentLabels = new ArrayList<CoreLabel>();
		
		mentionFinder = new MentionFinder(pipeline);
	}
	
	/**
	 * Find the entity mentions in the text, and the sentiment associated to each mention.
	 * @param text Text from which entities should be extracted.
	 * @param types Array of entity types that should be searched.
	 * 				When it is set to null "sentence mode" is activated.
	 * 				Otherwise "answer mode" is enabled.
	 * @param findDates When true, this method will also try to find references to dates.
	 * @return A list of entity mentions, tagged with the 
	 */
	public List<SentimentMentionMap> findEntities(String text, boolean findDates, boolean fullTextMode) {
		Annotation document = new Annotation(text);
		
		pipeline.annotate(document);
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<SentimentMentionMap> sentimentList = new ArrayList<>();
		
		for(CoreMap sentence : sentences) {
			currentTokens = new ArrayList<String>();
			currentSentimentTokens = new ArrayList<Integer>();
			currentLabels = new ArrayList<CoreLabel>();
			
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				currentTokens.add(token.getString(TextAnnotation.class));
				currentLabels.add(token);
			}
			entities = new ArrayList<>();
			entities.addAll(mentionFinder.findEntities(text, findDates, fullTextMode, document));
			
			if (entities != null && !entities.isEmpty()) {
				Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
				
				TreeNAry<Data> tree = new TreeNAry<Data>();
				createSentimentTree(sentimentTree, tree);
				
				getSentimentToken(tree);
				
				List<Tree> leaves = sentimentTree.getLeaves();
				int i = 0;
				for (Tree leaf: leaves) {
					System.out.println("leaf " + i + " " + leaf.label().toString());
					i++;
				}

				SemanticGraph dependencies = sentence.get(BasicDependenciesAnnotation.class);	
				for (MentionMap e : entities) {
					int numToken = e.getTokenRange().getEnd();
					int maxDepth = 3;
					int currentDepth = 0;
					int timeToDepthIncrease;
					boolean pendingDepthIncrease = false;
					//e.setMention(getMention(e.getStart(), e.getEnd(), document));					
					
					Stack<IndexedWord> s = new Stack<IndexedWord>();

					s.push(new IndexedWord (currentLabels.get(numToken)));
					timeToDepthIncrease = s.size();

					ArrayList<IndexedWord> visited = new ArrayList<IndexedWord> ();
					ArrayList<IndexedWord> candidatedSentimentTokens = new ArrayList<IndexedWord>();
					
					while (currentDepth < maxDepth  && !s.isEmpty()) {
						IndexedWord currentIw = s.pop();
						visited.add(currentIw);
						
						timeToDepthIncrease--;
						if (timeToDepthIncrease == 0) {
							currentDepth++;
							pendingDepthIncrease = true;
						}
						
						int currentIndex = currentIw.index() - 1;
						int sentiment = currentSentimentTokens.get(currentIndex);
						if (sentiment != 2 && sentiment != -1 && ((currentIndex < e.getTokenRange().getStart() && currentIndex < e.getTokenRange().getEnd()) || (currentIndex > e.getTokenRange().getStart() && currentIndex > e.getTokenRange().getEnd()))) {
							candidatedSentimentTokens.add(currentIw);	
						}
					
						List<IndexedWord> parentList = dependencies.getParentList(currentIw);
						List<IndexedWord> childList = dependencies.getChildList(currentIw);
						
						
						if (pendingDepthIncrease) {
							timeToDepthIncrease = s.size();
							pendingDepthIncrease = false;
						}
						for (IndexedWord iw : parentList) {
							if (!visited.contains(iw))
								s.push(iw);
						}
						for (IndexedWord iw : childList) {
							if (!visited.contains(iw))
								s.push(iw);
						}
					}

					
					int currentMinDistance = currentTokens.size() + 1;
					int currentSentiment = -1;
					int currentTokenIndex = -1;
					for (IndexedWord currentIw : candidatedSentimentTokens) {
						List<IndexedWord> childList = dependencies.getChildList(currentIw);
						boolean negated = false;
						for (IndexedWord iw : childList) {
							String relation = dependencies.getEdge(currentIw, iw).getRelation().toString();
							if (relation.equals("neg")) {
								negated = true;
								break;
							}
						}
						int distance = Math.abs(e.getTokenRange().getEnd() - (currentIw.index() - 1));
						if (distance < currentMinDistance) {
							currentMinDistance = distance;
							if (negated) {
								currentSentiment = 4 - currentSentimentTokens.get(currentIw.index() - 1);
							} else {
								currentSentiment = currentSentimentTokens.get(currentIw.index() - 1);
							}
						}
						
						
					}
					SentimentMentionMap sm = new SentimentMentionMap(e, currentSentiment);
					sentimentList.add(sm);	
				}
			}
		}

		return sentimentList;
	}
	
	/**
	 * Returns the overall sentiment of the sentence, without entities
	 * @param text Text to be analyzed
	 * @return A number representing the sentiment of the sentence.
	 */
	public int getSentiment(String text) {
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<SentimentMentionMap> sentimentList = new ArrayList<>();
		
		for(CoreMap sentence : sentences) {
			currentTokens = new ArrayList<String>();
			currentSentimentTokens = new ArrayList<Integer>();
			currentLabels = new ArrayList<CoreLabel>();
			
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				currentTokens.add(token.getString(TextAnnotation.class));
				currentLabels.add(token);
			}
			entities = new ArrayList<>();
			Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
			SemanticGraph dependencies = sentence.get(BasicDependenciesAnnotation.class);	
			TreeNAry<Data> tree = new TreeNAry<Data>();
			createSentimentTree(sentimentTree, tree);
			
			getSentimentToken(tree);
			
			
			int numToken = currentTokens.size() - 1;
			int maxDepth = 3;
			int currentDepth = 0;
			int timeToDepthIncrease;
			boolean pendingDepthIncrease = false;
			
			Stack<IndexedWord> s = new Stack<IndexedWord>();
			
			s.push(new IndexedWord (currentLabels.get(numToken)));
			timeToDepthIncrease = s.size();

			ArrayList<IndexedWord> visited = new ArrayList<IndexedWord> ();
			ArrayList<IndexedWord> candidatedSentimentTokens = new ArrayList<IndexedWord>();
			
			while (currentDepth < maxDepth  && !s.isEmpty()) {
				IndexedWord currentIw = s.pop();
				visited.add(currentIw);
				
				timeToDepthIncrease--;
				if (timeToDepthIncrease == 0) {
					currentDepth++;
					pendingDepthIncrease = true;
				}
				
				int currentIndex = currentIw.index() - 1;
				int sentiment = currentSentimentTokens.get(currentIndex);
				if (sentiment != 2 && sentiment != -1 ) {
					candidatedSentimentTokens.add(currentIw);
				}
			
				List<IndexedWord> parentList = dependencies.getParentList(currentIw);
				List<IndexedWord> childList = dependencies.getChildList(currentIw);
				
				
				if (pendingDepthIncrease) {
					timeToDepthIncrease = s.size();
					pendingDepthIncrease = false;
				}
				for (IndexedWord iw : parentList) {
					if (!visited.contains(iw))
						s.push(iw);
				}
				for (IndexedWord iw : childList) {
					if (!visited.contains(iw))
						s.push(iw);
				}
			}

			
			int currentMinDistance = currentTokens.size() + 1;
			int currentSentiment = -1;
			int currentTokenIndex = -1;
			for (IndexedWord currentIw : candidatedSentimentTokens) {
				List<IndexedWord> childList = dependencies.getChildList(currentIw);
				boolean negated = false;
				for (IndexedWord iw : childList) {
					String relation = dependencies.getEdge(currentIw, iw).getRelation().toString();
					if (relation.equals("neg")) {
						negated = true;
						break;
					}
				}
				if (negated) {
					currentSentiment = 4 - currentSentimentTokens.get(currentIw.index() - 1);
				} else {
					currentSentiment = currentSentimentTokens.get(currentIw.index() - 1);
				}
				
				currentSentimentTokens.set(currentIw.index() - 1, currentSentiment);
			}
			
			for (int sentiment: currentSentimentTokens) {
				if (sentiment != -1 && sentiment != 2) {
					return sentiment;
				}
			}
			return 2;
		}
		return 2;
	}
	
	private void getSentimentToken (TreeNAry<Data> tree) {
		List<Node<Data>> leaves = tree.getLeaves();
		for (Node<Data> l : leaves) {
			if (l.getData().getLabel().equals("like")) {
				currentSentimentTokens.add(3);
			} else if (l.getData().getLabel().equals("dislike")) {
				currentSentimentTokens.add(0);
			} else {
				currentSentimentTokens.add(l.getParent().getData().getSentiment());
			}
		}
	}
	
	private void createSentimentTree (Tree tSrc, TreeNAry<Data> tDst) {
		
		if (tSrc == null)
			return;
		
		Stack<Tree> s = new Stack<Tree>();
		Stack<Node<Data>> sDst = new Stack<Node<Data>>();

		Node<Data> root = new Node<Data>();
		tDst.setRoot(root);
		
		s.push(tSrc);
		sDst.push(root);
		
		int numCurrentToken = 0;
		
		while (!s.isEmpty()) {
			Tree currentNode = s.pop();
			Node<Data> currentNodeDst = sDst.pop();
			
			int sentiment = -1;
			
			if (currentNode.label().toString().equals("like")) {
				sentiment = 3;
			} else if (currentNode.label().toString().equals("dislike")) {
				sentiment = 0;
			} else {
				sentiment = RNNCoreAnnotations.getPredictedClass(currentNode);
			}
			
			
			if (currentNode.isLeaf()) {
				EntityTag tag = getEntityTag(numCurrentToken);
				currentNodeDst.setData(new Data(currentNode.label().toString(), sentiment, tag, numCurrentToken));
				numCurrentToken++;
			} else {
				currentNodeDst.setData(new Data(currentNode.label().toString(), sentiment, EntityTag.NA, -1));
			}
			
			
			
			List<Tree> children = currentNode.getChildrenAsList();
			Collections.reverse(children);
			
			for (Tree c : children) {
				s.push(c);
				Node<Data> n = new Node<Data>();
				currentNodeDst.addChild(n);
				sDst.push(n);
			}
		}
	}
	
	private EntityTag getEntityTag (int index) {
		
		for (MentionMap e : entities) {
			if (index >= e.getTokenRange().getStart() && index <= e.getTokenRange().getEnd()) {
				return EntityTag.ENTITY;
			}
		}
		
		return EntityTag.NA;
	}
	
	public static void main(String[] args) {
		System.out.println(new SentimentMentionFinder().getSentiment("I hate"));
	}
}
