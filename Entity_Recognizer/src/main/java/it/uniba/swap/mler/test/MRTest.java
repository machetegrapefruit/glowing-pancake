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

public class MRTest {
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		IMentionFinder ef = MentionFinderFactory.getEntityFinder();
		while (true) {
			System.out.print(">");
			String input = s.nextLine();
			List<MentionMap> mentions = ef.findEntities(input, false, false);
			for (MentionMap results: mentions) {
				System.out.println("Mention " + results.getMatchedString());
				for (String key: results.getKeys()) {
					List<Match> entities = results.getBest(key, 5);
					System.out.println(entities);
					for (Match m: entities) {
						System.out.println(m.getMatchedName() + " " + m.getMatch());
					}
				}
			}
		}
	}
}
