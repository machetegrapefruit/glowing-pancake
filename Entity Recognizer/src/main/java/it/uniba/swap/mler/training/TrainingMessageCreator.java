package it.uniba.swap.mler.training;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.ValueAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import it.uniba.swap.mler.utils.FileUtils;

public class TrainingMessageCreator {
	
	private static StanfordCoreNLP pipeline;

	
	private static String getRandomItem(List<String> entities, List<String> properties) {
		Random r = new Random();
		int type = r.nextInt(2);
		if (type == 0) {
			//Choose a movie
			int movieIndex = r.nextInt(entities.size());
			String line = entities.get(movieIndex);
			String[] split = line.split("\\|");
			String label = split[1];
			label = label.replaceAll(",", "");
			String filteredLabel = label.replaceAll("\\+", "");
			filteredLabel = filteredLabel.replaceAll("\\((.*?)\\)", "").replaceAll("\\)", "").trim();
			return filteredLabel;
		} else {
			//Choose a property
			int movieIndex = r.nextInt(properties.size());
			String line = properties.get(movieIndex);
			String[] split = line.split("\\|");
			String label = split[1];
			label = label.replaceAll(",", "");
			String filteredLabel = label.replaceAll("\\+", "");
			filteredLabel = filteredLabel.replaceAll("\\((.*?)\\)", "").replaceAll("\\)", "").trim();
			return filteredLabel;
		}
	}
	
	public static int numSentences = 10000;
	public static void main(String[] args) throws IOException {
		
		ClassLoader classLoader = TrainingMessageCreator.class.getClassLoader();	
		Properties props = new Properties();

		props.put("annotators", "tokenize, ssplit, pos, lemma, parse");
		pipeline = new StanfordCoreNLP(props);
		
		
		String entitiesPath = args[0];
		String propertiesPath = args[1];
		String templatesPath = args[2];
		String outputPath = args[3];
		
		List<String> entities = FileUtils.readFileAsList(entitiesPath);
		List<String> properties = FileUtils.readFileAsList(propertiesPath);
		List<String> templates = FileUtils.readFileAsList(templatesPath);
		
		for (int i = 0; i < numSentences; i++) {
			int templateIndex = new Random().nextInt(templates.size());
			String template = templates.get(templateIndex) + ""; 
//			template.replace("#1", item1);
//			template.replace("#2", item2);
			
			Annotation document = new Annotation(template);
			pipeline.annotate(document);
			for (CoreLabel token : document.get(TokensAnnotation.class)) {
				String value = token.getString(ValueAnnotation.class);
				if (value.equals("#")) {
					String randomItem = getRandomItem(entities, properties);
					String[] randomItemTokens = randomItem.split("\\s");
					for (String s: randomItemTokens) {
						System.out.println(s + "\titem");
						FileUtils.appendToFile(outputPath, s + "\titem\n");
					}
				} else {
					System.out.println(value + "\tO");
					FileUtils.appendToFile(outputPath, value + "\tO\n");
				}
			}
			System.out.println();
			FileUtils.appendToFile(outputPath, "\n");
		}
	}
}
