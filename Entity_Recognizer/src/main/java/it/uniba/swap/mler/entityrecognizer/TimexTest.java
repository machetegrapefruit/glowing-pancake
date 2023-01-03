package it.uniba.swap.mler.entityrecognizer;

import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.util.CoreMap;
import it.uniba.swap.mler.exception.TimexParsingException;

public class TimexTest {
	
	private static String[] weekDays = {
			"Monday",
			"Tuesday",
			"Wednesday",
			"Thursday",
			"Friday",
			"Saturday",
			"Sunday"
	};
	
	private static String[] months = {
			"January",
			"February",
			"March",
			"April",
			"May",
			"June",
			"July",
			"August",
			"September",
			"October",
			"November",
			"December"
	};
	
	private static it.uniba.swap.mler.entityrecognizer.TimeExpression findTimex(StanfordCoreNLP pipeline, String text, DateTime reference) throws TimexParsingException {
		Annotation document = new Annotation(text);
		pipeline.annotate(document);

		List<CoreMap> timexAnnsAll = document.get(TimeAnnotations.TimexAnnotations.class);
		return TimeExpressionFinder.fromAnnotation(timexAnnsAll, reference);
	}
	
	public static void main(String[] args) throws TimexParsingException {
		ClassLoader classLoader = TimeExpressionFinder.class.getClassLoader();
		String model = classLoader.getResource("model.txt").getPath();
		String regex = classLoader.getResource("entitiesregex.train").getPath();
		String sutime = "edu/stanford/nlp/models/sutime/defs.sutime.txt,edu/stanford/nlp/models/sutime/english.sutime.txt,edu/stanford/nlp/models/sutime/english.holidays.sutime.txt," + 
				classLoader.getResource("sutime.custom.txt").getPath();
		
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, parse, ner, regexner, entitymentions");
		//props.setProperty("sutime.includeRange", "true");
		props.setProperty("sutime.rules", sutime);
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		pipeline.addAnnotator(new TimeAnnotator("sutime", props));
		//pipeline.addAnnotator(new TokensRegexNERAnnotator("D:\\uwmp\\entitiesregex.train", true));
		//pipeline.addAnnotator(new EntityMentionsAnnotator());
		
		DateTime now = DateTime.now();
		int nextWeekDayIndex = now.plusDays(1).getDayOfWeek();
		String nextWeekDayString = weekDays[nextWeekDayIndex - 1];
		int nextMonthIndex = now.plusMonths(1).getMonthOfYear();
		String nextMonthString = months[nextMonthIndex - 1];
		String[] sentences = {
				"What was my portfolio performance since Jan 1st 2018",
				"What was my portfolio performance since Jan 1st",
				"What was my portfolio performance since January",
				"What was my portfolio performance since Last january",
				"What was my portfolio performance since " + nextWeekDayString,
				"What was my portfolio performance since last " + nextWeekDayString,
				"What was my portfolio performance since " + nextMonthString,
				"What was my portfolio performance since last " + nextMonthString,
				"What was my portfolio performance since Christmas",
				"What was my portfolio performance since 2010",
				"What was my portfolio performance this year",
				"What was my portfolio performance this week",
				"What was my portfolio performance last year",
				"What was my portfolio performance last week",
				"What was my portfolio performance from Jan 1st to May 30th",
				"What was my portfolio performance since 12 months ago",
				"What was my portfolio performance in the last three months",
				"What was my portfolio performance in the next three months",
				"What was my portfolio performance since the start of the contract"
		};
		
		for (String sentence: sentences) {
			System.out.println("Sentence is " + sentence);
			it.uniba.swap.mler.entityrecognizer.TimeExpression te = findTimex(pipeline, sentence, now);
			System.out.println("Processed dates are: ");
			System.out.println(te.getStartDT());
			System.out.println(te.getEndDT());
		}
		
		DateTime lastDay = DateTime.parse("2018-12-31");
		it.uniba.swap.mler.entityrecognizer.TimeExpression te = findTimex(pipeline, "Tuesday", lastDay);
		System.out.println(te.getStartDT());
		te = findTimex(pipeline, "January", lastDay);
		System.out.println(te.getStartDT());
	}
}
