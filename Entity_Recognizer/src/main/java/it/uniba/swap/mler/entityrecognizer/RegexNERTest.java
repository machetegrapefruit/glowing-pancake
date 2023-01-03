package it.uniba.swap.mler.entityrecognizer;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.RegexNERAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.TokensRegexNERAnnotator;
import edu.stanford.nlp.util.CoreMap;

public class RegexNERTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, regexner");
		props.put("regexner.mapping", "D:\\entitiesregex.train");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation document = new Annotation("Send 500 GBP to Alice");
		
		pipeline.annotate(document);
		System.out.println(document);
		//List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreLabel token: document.get(TokensAnnotation.class)) {
		    // this is the NER label of the token
		    String ne = token.get(NamedEntityTagAnnotation.class);
		    System.out.println(ne);
		}
	}

}
