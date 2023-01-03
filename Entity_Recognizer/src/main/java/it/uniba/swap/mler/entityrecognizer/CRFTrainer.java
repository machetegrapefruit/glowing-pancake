package it.uniba.swap.mler.entityrecognizer;

import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.StringUtils;

public class CRFTrainer {
	public static void main(String[] args) {
		trainAndWrite("src/main/resources/model.txt", "properties.txt", "training.txt");
	}
	
	public static void trainAndWrite(String modelOutPath, String prop, String trainingFilepath) {
		   ClassLoader cLoader = CRFTrainer.class.getClassLoader();
		   String propPath = cLoader.getResource(prop).getPath().toString();
		   trainingFilepath = cLoader.getResource(trainingFilepath).getPath().toString();
		   Properties props = StringUtils.propFileToProperties(propPath);
		   props.setProperty("serializeTo", modelOutPath);

		   //if input use that, else use from properties file.
		   if (trainingFilepath != null) {
			   String gazettePath = cLoader.getResource("gazette.txt").getPath();
		       props.setProperty("trainFile", trainingFilepath);
//		       props.setProperty("gazette", gazettePath);
		   }

		   SeqClassifierFlags flags = new SeqClassifierFlags(props);
		   CRFClassifier<CoreLabel> crf = new CRFClassifier<>(flags);
		   crf.train();

		   crf.serializeClassifier(modelOutPath);
		}
}
