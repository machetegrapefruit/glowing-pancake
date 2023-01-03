package it.uniba.swap.mler.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.uniba.swap.mler.entityrecognizer.EntityFinder;
import it.uniba.swap.mler.entityrecognizer.MatchMap;

public class ERTest2 {
	public static List<String> frasi(String path){
		File file = new File(path);
		FileReader fr;
		ArrayList<String> array = null;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			StringBuffer sb = new StringBuffer();
			String line;
			array = new ArrayList<String>();
			while ((line=br.readLine()) != null){
				array.add(line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return array;
		
	}
	
	public static void main(String[] args) {
		// System.out.println(new EntityFinder().findEntities("I like Brade pit ",
		// false, false));
//		new EntityFinder().findDates("What is my portfolio performance in the last two weeks?");
//		new EntityFinder().findDates("What is my portfolio performance this year?");
//		new EntityFinder().findDates("What is my portfolio performance last year?");
//		new EntityFinder().findDates("What is my portfolio performance from Jan 1st 2018 to April 3rd?");
//		new EntityFinder().findDates("What is my portfolio performance since 10 days ago?");
//		new EntityFinder().findDates("What is my portfolio performance since the start of the contract");
//		new EntityFinder().findDates("What is my portfolio performance since contract");
		EntityFinder ef = new EntityFinder();
//		System.out.println(ef.findEntities("My favourite film saga is star wars", false, false));
//		System.out.println(ef.findEntities("My favourite film saga is Star Wars", false, false));
		
//		
		ArrayList<String> frasi = (ArrayList<String>) frasi("E:\\ER\\ER\\template\\test\\test1.txt");
		for (String s: frasi) {
			System.out.println("Frase: "+ s);
			MatchMap entities = ef.findEntities(s, false, false);
			if (entities.getKeys().size() == 0)
				System.out.println("NO CORRISPONDENZA");
			else {
				System.out.println("Corrispondenze: ");
				System.out.println(entities);
			}
			System.out.println("-----------------------------------------");
		}
			
			
//		
//		
//		System.out.println("Frase: i hate marvel because i don't like superheroes");
//		System.out.println(ef.findEntities("i hate marvel because i don't like superheroes", false, false));
//		System.out.println("Frase: I like clooney");
//		System.out.println(ef.findEntities("I like clooney", false, false));
//		System.out.println("Frase: i like clooney");
//		System.out.println(ef.findEntities("i like clooney", false, false));
//		System.out.println("------");
//		if (ef.findEntities("I like cacalala", false, false) == null)
//			System.out.println("NO CORRISPONDENZA");
//
//		System.out.println("------");
//		System.out.println("Frase: I like leonardo di caprio");
//		System.out.println(ef.findEntities("I like leonardo di caprio", false, false));

//		List<Tree> nps = ef.getNPs("I want to buy 250 apple inc");
//		FuzzyKeywordFinder fkf = new FuzzyKeywordFinder();
//		for(Tree np: nps) {
//			String text = SentenceUtils.listToString(np.yield());
//			System.out.println(text);
//			List<Match> matches = fkf.findEntities(text);
//			for (Match m: matches) {
//				System.out.println(m.getMatchedName() + " " + m.getMatch());
//			}
//		}
//		System.out.println(nps);
	}
}
