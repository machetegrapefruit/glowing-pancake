package database;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AccessRecsysDBTest {
	static 	String[] prop = {			
			"http://dbpedia.org/ontology/director",
			"http://dbpedia.org/ontology/producer",
			"http://dbpedia.org/ontology/writer",
			"http://dbpedia.org/ontology/starring",
			"http://dbpedia.org/ontology/musicComposer",
			"http://dbpedia.org/ontology/cinematography",
			"http://dbpedia.org/ontology/basedOn",
			"http://purl.org/dc/terms/subject", 
			"genre"
			};
	static String[] prop2 = {"http://dbpedia.org/ontology/director",
			"http://dbpedia.org/ontology/producer",
			"http://dbpedia.org/ontology/writer",
			"http://dbpedia.org/ontology/starring",
			"http://dbpedia.org/ontology/musicComposer",
			"http://dbpedia.org/ontology/cinematography",
			"http://dbpedia.org/ontology/editing",
			"http://dbpedia.org/ontology/distributor",
			"http://dbpedia.org/ontology/basedOn",
			"http://purl.org/dc/terms/subject", 
			"genre",
			"title",
			"release_year",
			"reference_period",
			"release_date",
			"runtime_minutes",
			"runtime_range",
			"runtime_uri",
			"plot",
			"language",
			"country",
			"awards",
			"poster",
			"trailer",
			"score",
			"metascore",
			"imdb_rating",
			"imdb_id",
			"imdb_votes"};		
	
	private static AccessRecsysDB db = new AccessRecsysDB();
	
	public static void testSelectPropertyByMovieForClient() throws Exception {
		//System.out.println("Original: " + db.selectPropertyByMovieForClient("http://dbpedia.org/resource/Midnight_in_Paris"));
		System.out.println("New: " + db.selectEntityProperties("http://dbpedia.org/resource/Midnight_in_Paris", prop2));
	}
	
	public static void testSelectMoviesAndPropertyByUser() throws Exception {
		//System.out.println("Original: " + db.selectMoviesAndPropertyByUser(19371450, "like"));
		System.out.println("New: " + db.selectMoviesAndPropertyByUser("19371450", "like", prop));
	}
	
	public static void testselectPropertyByMovieForExplanation() throws Exception {

		//System.out.println("Original: " + db.selectPropertyByMovieForExplanation(""));
		System.out.println("New: " + db.selectEntityProperties("http://dbpedia.org/resource/Midnight_in_Paris", prop));

	}
	
	public static void testSelectPropertyValueListMapFromPropertyType() throws Exception {
		//System.out.println("Original: " + db.selectPropertyValueListMapFromPropertyType(165209895, "movie"));
		System.out.println("New: " + db.getEntitiesToRecommend("165209895"));
	}
	
	public static void testSelectAllResourceAndPropertyFromDbpediaMoviesSelection() throws Exception {
		Set<String> ids = new HashSet<String>();
		ids.add("http://dbpedia.org/resource/Having_Wonderful_Time");
		Map<String,Set<List<String>>> original = db.selectAllResourceAndPropertyFromDbpediaMoviesSelection(ids);
		Map<String,Set<List<String>>> mod = db.selectAllResourceAndPropertyFromDbpediaMoviesSelection(ids);
		System.out.println("Original: " + original);
		System.out.println("New: " + mod);
	}
	
	public static void testSelectTestSetForUserFromMoviesForPageRank() throws Exception {
		Map<String, Set<String>> original = db.selectTestSetForUserFromMoviesForPageRank("19371450");
		Map<String, Set<String>> mod = db.selectTestSetForUserFromMoviesForPageRank("19371450");
		System.out.println("Original: " + original.get("19371450").size());
		System.out.println("New: " + mod.get("19371450").size());
		mod.get("19371450").removeAll(original.get("19371450"));
		System.out.println(mod.get("19371450"));
	}
	
	public static void testSelectMovie() throws Exception {
		String[] uris = {
				"none",
				"http://dbpedia.org/resource/'It's_Alive!'",
				"http://dbpedia.org/resource/Having_Wonderful_Time",
				"http://dbpedia.org/resource/No%C3%ABlle_(film)"
		};
		
		for (String uri: uris) {
			System.out.println("Original: " + db.selectMovie(uri) + ", new: " + db.selectMovie(uri));
		}
	}
	
	public static void main(String[] args) throws Exception {
		testSelectMovie();
		testSelectTestSetForUserFromMoviesForPageRank();
		testSelectAllResourceAndPropertyFromDbpediaMoviesSelection();
		testSelectPropertyValueListMapFromPropertyType();
		testselectPropertyByMovieForExplanation();
		//testSelectMoviesAndPropertyByUser();
		testSelectPropertyByMovieForClient();
	}

}
