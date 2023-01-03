package it.uniba.swap.mler.entityrecognizer;

import java.util.ArrayList;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class FuzzyTest {
	public static void main(String[] args) {
		List<String> choices = new ArrayList<String>();
		choices.add("test sentence");
		choices.add("asdad");
		choices.add("adadada");
		choices.add("test");
		choices.add("Alice Smith");
		choices.add("Alice Jones");
		
		String[] sentences = {
				"Send 500 GBP to Alice",
				"Send 500 GBP to Alice Jones"
		};
		for (String s: sentences) {
			System.out.println("Sentence is " + s);
			for (String c: choices) {
				System.out.println(c + "=" + FuzzySearch.partialRatio(c, s));
			}
		}

	}
}
