package it.uniba.swap.mler.entityrecognizer;

import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class CRFTester {
	public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException {
		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier("resources/model.txt");
		System.out.println(classifier.classifyToString("Pay 500 CAD to Alice at wells frago"));
		//List<List<CoreLabel>> entities = classifier.classify("Pay 500 CAD to Alice at wells frago");
	}
}
