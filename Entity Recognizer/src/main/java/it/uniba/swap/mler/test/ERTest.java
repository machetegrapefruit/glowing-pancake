package it.uniba.swap.mler.test;

import java.util.List;
import java.util.Scanner;

import it.uniba.swap.mler.entityrecognizer.EntityFinder;
import it.uniba.swap.mler.entityrecognizer.EntityFinderFactory;
import it.uniba.swap.mler.entityrecognizer.IEntityFinder;
import it.uniba.swap.mler.entityrecognizer.Match;
import it.uniba.swap.mler.entityrecognizer.MatchMap;

public class ERTest {
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		IEntityFinder ef = EntityFinderFactory.getEntityFinder();
		while (true) {
			System.out.print(">");
			String input = s.nextLine();
			MatchMap results = ef.findEntities(input, false, false);
			for (String key: results.getKeys()) {
				List<Match> entities = results.get(key);
				System.out.println(entities);
				for (Match m: entities) {
					System.out.println(m.getMatchedName() + " " + m.getMatch());
				}
			}
		}
	}
}
