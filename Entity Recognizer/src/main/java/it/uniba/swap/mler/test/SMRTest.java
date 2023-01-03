package it.uniba.swap.mler.test;

import java.util.List;
import java.util.Scanner;

import it.uniba.swap.mler.entityrecognizer.EntityFinder;
import it.uniba.swap.mler.entityrecognizer.EntityFinderFactory;
import it.uniba.swap.mler.entityrecognizer.IMentionFinder;
import it.uniba.swap.mler.entityrecognizer.Match;
import it.uniba.swap.mler.entityrecognizer.MatchMap;
import it.uniba.swap.mler.entityrecognizer.MentionFinderFactory;
import it.uniba.swap.mler.entityrecognizer.MentionMap;
import it.uniba.swap.mler.entityrecognizer.SentimentMentionFinder;
import it.uniba.swap.mler.entityrecognizer.SentimentMentionMap;

public class SMRTest {
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		SentimentMentionFinder ef = new SentimentMentionFinder();
		while (true) {
			System.out.print(">");
			String input = s.nextLine();
			List<SentimentMentionMap> mentions = ef.findEntities(input, false, false);
			for (SentimentMentionMap results: mentions) {
				System.out.println("Mention " + results.getMention().getMatchedString() + " " + results.getSentiment());
				for (String key: results.getMention().getKeys()) {
					List<Match> entities = results.getMention().getBest(key, 5);
					System.out.println(entities);
					for (Match m: entities) {
						System.out.println(m.getMatchedName() + " " + m.getMatch());
					}
				}
			}
		}
	}
}
