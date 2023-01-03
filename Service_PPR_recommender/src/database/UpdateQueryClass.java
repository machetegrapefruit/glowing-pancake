package database;

public class UpdateQueryClass {
	
	public static String SQLinsertDirector(String directorURI, String name) throws Exception {
		String SQLdirector = 	"INSERT INTO directors (`uri`,`name`)" +
								" VALUE (\"" + directorURI + "\",\"" + name + "\")" +
								" ON DUPLICATE KEY UPDATE `name` = \"" + name + "\";";
		return SQLdirector;
	}
	
	public static String SQLinsertProducer(String producerURI, String name) throws Exception {
		String SQLProducer = 	"INSERT INTO producers (`uri`,`name`)" +
								" VALUE (\"" + producerURI + "\",\"" + name + "\")" +
								" ON DUPLICATE KEY UPDATE `name` = \"" + name + "\";";
		return SQLProducer;
	}
	
	public static String SQLinsertWriter(String writerURI, String name) throws Exception {
		String SQLWriter = 	"INSERT INTO writers (`uri`,`name`)" +
								" VALUE (\"" + writerURI + "\",\"" + name + "\")" +
								" ON DUPLICATE KEY UPDATE `name` = \"" + name + "\";";
		return SQLWriter;
	}
	
	public static String SQLinsertStarring(String starringURI, String name) throws Exception {
		String SQLstarring = "INSERT INTO starring (`uri`,`name`)" +
							 " VALUE (\"" + starringURI + "\",\"" + name + "\")" +
							 " ON DUPLICATE KEY UPDATE `name` = \"" + name + "\";";
		return SQLstarring;
	}
	
	public static String SQLinsertMusicComposer(String musicComposerURI, String name) throws Exception {
		String SQLmusicComposer = 	"INSERT INTO music_composers (`uri`,`name`)" +
									" VALUE (\"" + musicComposerURI + "\",\"" + name + "\")" +
									" ON DUPLICATE KEY UPDATE `name` = \"" + name + "\";";
		return SQLmusicComposer;
	}
	
	public static String SQLinsertCinematography(String cinematographyURI, String name) throws Exception {
		String SQLcinematography = 	"INSERT INTO cinematographers (`uri`,`name`)" +
									" VALUE (\"" + cinematographyURI + "\",\"" + name + "\")" +
									" ON DUPLICATE KEY UPDATE `name` = \"" + name + "\";";
		return SQLcinematography;
	}
	
	public static String SQLinsertEditing(String editingURI, String name) throws Exception {
		String SQLediting = "INSERT INTO editings (`uri`,`name`)" +
							" VALUE (\"" + editingURI + "\",\"" + name + "\")" +
							" ON DUPLICATE KEY UPDATE `name` = \"" + name + "\";";
		return SQLediting;
	}
	
	public static String SQLinsertDistributor(String distributorURI, String name) throws Exception {
		String SQLdistributor = "INSERT INTO distributors (`uri`,`name`)" +
								" VALUE (\"" + distributorURI + "\",\"" + name + "\")" +
								" ON DUPLICATE KEY UPDATE `name` = \"" + name + "\";";
		return SQLdistributor;
	}
	
	public static String SQLinsertBasedOn(String basedOnURI, String name) throws Exception {
		String SQLbasedOn = 	"INSERT INTO based_on (`uri`,`name`)" +
								" VALUE (\"" + basedOnURI + "\",\"" + name + "\")" +
								" ON DUPLICATE KEY UPDATE `name` = \"" + name + "\";";
		return SQLbasedOn;
	}
	
	public static String SQLinsertCategory(String categoryURI, String name) throws Exception {
		String SQLcategory = 	"INSERT INTO categories (`uri`,`name`)" +
								" VALUE (\"" + categoryURI + "\",\"" + name + "\")" +
								" ON DUPLICATE KEY UPDATE `name` = \"" + name + "\";";
		return SQLcategory;
	}
	
	public static String SQLinsertGenre(String genre) throws Exception {
		String SQL = 	"INSERT INTO genres (`genre`)" +
						" VALUE (\"" + genre + "\")" +
						" ON DUPLICATE KEY UPDATE `genre` = `genre`;";
		
		return SQL;
	}
	
	public static String SQLinsertDirectorAndMovie(String directorURI, String movieURI) throws Exception {
		String SQLdirectorAndMovie = 	"INSERT INTO directors_movies (`director_uri`, `movie_uri`)" +
										" VALUES (\"" + directorURI + "\",\"" + movieURI + "\")" +
										" ON DUPLICATE KEY UPDATE `director_uri` = `director_uri`,`movie_uri` = `movie_uri`;";
		return SQLdirectorAndMovie;
	}
	
	public static String SQLinsertProducerAndMovie(String producerURI, String movieURI) throws Exception {
		String SQLProducerAndMovie = 	"INSERT INTO producers_movies (`producer_uri`, `movie_uri`)" +
										" VALUES (\"" + producerURI + "\",\"" + movieURI + "\")" +
										" ON DUPLICATE KEY UPDATE `producer_uri` = `producer_uri`,`movie_uri` = `movie_uri`;";
		return SQLProducerAndMovie;
	}
	
	public static String SQLinsertWriterAndMovie(String WriterURI, String movieURI) throws Exception {
		String SQLWriterAndMovie = 	"INSERT INTO writers_movies (`writer_uri`, `movie_uri`)" +
									" VALUES (\"" + WriterURI + "\",\"" + movieURI + "\")" +
									" ON DUPLICATE KEY UPDATE `writer_uri` = `writer_uri`,`movie_uri` = `movie_uri`;";
		return SQLWriterAndMovie;
	}	
	
	public static String SQLinsertStarringAndMovie(String starringURI, String movieURI) throws Exception {
		String SQLstarringAndMovie = 	"INSERT INTO starring_movies (`starring_uri`, `movie_uri`)" +
										" VALUES (\"" + starringURI + "\",\"" + movieURI + "\")" +
										" ON DUPLICATE KEY UPDATE `starring_uri` = `starring_uri`,`movie_uri` = `movie_uri`;";
		return SQLstarringAndMovie;
	}
	
	public static String SQLinsertMusicComposerAndMovie(String musicComposerURI, String movieURI) throws Exception {
		String SQLmusicComposerAndMovie = 	"INSERT INTO music_composers_movies (`music_composer_uri`, `movie_uri`)" +
											" VALUES (\"" + musicComposerURI + "\",\"" + movieURI + "\")" +
											" ON DUPLICATE KEY UPDATE `music_composer_uri` = `music_composer_uri`,`movie_uri` = `movie_uri`;";
		return SQLmusicComposerAndMovie;
	}
	
	public static String SQLinsertCinematographyAndMovie(String cinematographyURI, String movieURI) throws Exception {
		String SQLcinematographyAndMovie = 	"INSERT INTO cinematographers_movies (`cinematography_uri`, `movie_uri`)" +
											" VALUES (\"" + cinematographyURI + "\",\"" + movieURI + "\")" +
											" ON DUPLICATE KEY UPDATE `cinematography_uri` = `cinematography_uri`,`movie_uri` = `movie_uri`;";
		return SQLcinematographyAndMovie;
	}
	
	public static String SQLinsertEditingAndMovie(String editingURI, String movieURI) throws Exception {
		String SQLeditingAndMovie = 	"INSERT INTO editings_movies (`editing_uri`, `movie_uri`)" +
										" VALUES (\"" + editingURI + "\",\"" + movieURI + "\")" +
										" ON DUPLICATE KEY UPDATE `editing_uri` = `editing_uri`,`movie_uri` = `movie_uri`;";
		return SQLeditingAndMovie;
	}
	
	public static String SQLinsertDistributorAndMovie(String distributorURI, String movieURI) throws Exception {
		String SQLdistributorAndMovie = 	"INSERT INTO distributors_movies (`distributor_uri`, `movie_uri`)" +
											" VALUES (\"" + distributorURI + "\",\"" + movieURI + "\")" +
											" ON DUPLICATE KEY UPDATE `distributor_uri` = `distributor_uri`,`movie_uri` = `movie_uri`;";
		return SQLdistributorAndMovie;
	}	
	
	public static String SQLinsertBasedOnAndMovie(String basedOnURI, String movieURI) throws Exception {
		String SQLbasedOnAndMovie = 	"INSERT INTO based_on_movies (`based_on_uri`, `movie_uri`)" +
										" VALUES (\"" + basedOnURI + "\",\"" + movieURI + "\")" +
										" ON DUPLICATE KEY UPDATE `based_on_uri` = `based_on_uri`,`movie_uri` = `movie_uri`;";
		return SQLbasedOnAndMovie;
	}
	
	public static String SQLinsertCategoryAndMovie(String categoryURI, String movieURI) throws Exception {
		String SQLcategoryAndMovie = 	"INSERT INTO categories_movies (`category_uri`, `movie_uri`)" +
										" VALUES (\"" + categoryURI + "\",\"" + movieURI + "\")" +
										" ON DUPLICATE KEY UPDATE `category_uri` = `category_uri`,`movie_uri` = `movie_uri`;";
		return SQLcategoryAndMovie;
	}
	
	public static String SQLinsertGenreAndMovie(String genreName, String movieURI) throws Exception {
		String SQL = 	"INSERT INTO genres_movies (`genre_name`, `movie_uri`)" +
						" VALUES (\"" + genreName + "\",\"" + movieURI + "\")" +
						//" ON DUPLICATE KEY UPDATE `genre_name` = `genre_name`,`movie_uri` = `movie_uri`;";
						" ON DUPLICATE KEY UPDATE `genre_name` = \"" + genreName + "\", `movie_uri` = \"" + movieURI + "\";";
		return SQL;
	}
	
	//	public static String SQLinsertProperty(String subjectURI, String predicateURI, String objectURI){
	//	String SQL = "INSERT INTO mapping_dbpedia_movies_complete_review(`subject`,`predicate`,`object`)"
	//				+ " VALUE (\"" + subjectURI + "\",\"" + predicateURI + "\",\"" + objectURI + "\")"
	//				+ " ON DUPLICATE KEY UPDATE `subject` = `subject`,`predicate` = `predicate`,`object` = `object`;";
	//	return SQL;
	//}
	
	public static String SQLinsertDatasetTripleToMoviesPropertiesDbpedia(String subjectURI, String predicateURI, String objectURI){
		String SQL = "INSERT INTO mapping_dbpedia_movies_complete(`subject`,`predicate`,`object`)"
					+ " VALUE (\"" + subjectURI + "\",\"" + predicateURI + "\",\"" + objectURI + "\")"
					+ " ON DUPLICATE KEY UPDATE `subject` = `subject`,`predicate` = `predicate`,`object` = `object`;";
		return SQL;
	}
	
	public static String SQLinsertMovie(String movieURI) throws Exception {
		String SQLmovie = 	"INSERT INTO movies (`uri`)" +
							" VALUE (\"" + movieURI + "\")" +
							" ON DUPLICATE KEY UPDATE `uri` = `uri`;";
		return SQLmovie;
	}
	
	public static String SQLinsertReleaseDateAndMovie(String movieURI, String releaseDate) throws Exception {
		String releaseYear = releaseDate.substring(0, 4);
		//System.out.println(releaseYear);
		String SQLruntimeAndMovie = 	"INSERT INTO movies (`uri`, `release_date`,`release_year`)" +
										" VALUES (\"" + movieURI + "\",'" + releaseDate + "',\"" + releaseYear + "\")" +
										" ON DUPLICATE KEY UPDATE `release_date` = '" + releaseDate + "',`release_year` = \"" + releaseYear + "\" ;";
		return SQLruntimeAndMovie;
	}
	
	public static String SQLinsertRuntimeAndMovie(String movieURI, String runtimeURI, int runtimeMinutes, int runtimeRange) throws Exception {
		String SQLruntimeAndMovie = 	"INSERT INTO movies (`uri`, `runtime_uri`, `runtime_minutes`, `runtime_range`)" +
										" VALUES (\"" + movieURI + "\",\"" + runtimeURI + "\",\"" + runtimeMinutes + "\",\"" + runtimeRange + "\")" +
										" ON DUPLICATE KEY UPDATE `runtime_uri` = \"" + runtimeURI + "\", `runtime_minutes` = \"" + runtimeMinutes + "\",`runtime_range` = \"" + runtimeRange + "\" ;";
		return SQLruntimeAndMovie;
	}	

	public static String SQLupdateFiledIMDbOfMovie(String movieURI, String metascore,String imdbRating,String imdbVotes,String imdbID) {
		String SQL = "UPDATE movies" +
					 " SET `metascore` = \"" + metascore + "\","+
					 "`imdb_rating` = \"" + imdbRating + "\"," +
					 "`imdb_votes` = \"" + imdbVotes + "\"," +
					 "`imdb_id` = \"" + imdbID + "\" " +
					 " WHERE `uri` = \"" + movieURI + "\";";
		return SQL;
	}
	
	
	public static String SQLupdateAllInfoOfMovie(String uri, String release_date,int runtime_minutes,String plot,String language,String country,String awards,String poster, String metascore, String imdb_rating, String imdb_votes, String imdb_id) {
		String SQL = "UPDATE movies" +
					 " SET`release_date` = \"" + release_date + "\"," +
					 "`runtime_minutes` = \"" + runtime_minutes + "\"," +
					 "`plot` = \"" + plot + "\"," +
					 "`language` = \"" + language + "\"," +
					 "`country` = \"" + country + "\"," +
					 "`awards` = \"" + awards + "\"," +
					 "`poster` = \"" + poster + "\"," +
					 "`metascore` = \"" + metascore + "\","+
					 "`imdb_rating` = \"" + imdb_rating + "\"," +
					 "`imdb_votes` = \"" + imdb_votes + "\"," +
					 "`imdb_id` = \"" + imdb_id + "\" " +
					 " WHERE `uri` = \"" + uri + "\";";
		return SQL;
	}
	
	public static String SQLupdateInfoOfMovie(String movieURI, String title,String year,String released,int runtime,String plot,String language,String country,String awards,String poster) {
		String SQL = "UPDATE movies" +
					 " SET `title` = \"" + title + "\","+
					 "`release_year` = \"" + year + "\"," +
					 "`release_date` = \"" + released + "\"," +
					 "`runtime_minutes` = \"" + runtime + "\"," +
					 "`plot` = \"" + plot + "\"," +
					 "`language` = \"" + language + "\"," +
					 "`country` = \"" + country + "\"," +
					 "`awards` = \"" + awards + "\"," +
					 "`poster` = \"" + poster + "\"" +
					 " WHERE `uri` = \"" + movieURI + "\";";
		return SQL;
	}
	
	public static String SQLinsertGenreAndMovieToProperties() throws Exception {
		String SQL = 	"INSERT INTO mapping_dbpedia_movies_selection(subject, predicate, object)"
						+ " SELECT movie_uri, \"movie\", genre_name"
						+ " FROM genres_movies;";	
		return SQL; 
	}

	public static String SQLupdateTitleOfMovie(String movieURI, String title) {
		String SQL = 	"UPDATE movies" +
					    " SET `title` = \"" + title + "\"" +
						" WHERE `uri` = \"" + movieURI + "\";";
		return SQL;
	}
	
	//aggiorna il runtimeRange di uno specifico film
	public static String SQLupdateRuntimeRangeOfMovie(String movieURI, int runtimeRange ){
		String SQL = "UPDATE movies" +
			    " SET `runtime_range` = " + runtimeRange +
				" WHERE `uri` = \"" + movieURI + "\";";
		
		return SQL;
	}
	
	public static String SQLupdateReleaseYearOfMovie(String movieURI, int releaseYear){
		String SQL = "UPDATE movies" +
			    " SET `release_year` = " + releaseYear +
				" WHERE `uri` = \"" + movieURI + "\";";
		
		return SQL;
	}

	//Aggiorna il campo name
	public static String SQLupdateNameOfCategoryInVertexTable(String uri, String name){
		String SQL = "UPDATE vertices_trailer_selection" +
			    " SET `name` = \"" + name + "\"" +
				" WHERE `uri` = \"" + uri + "\";";
		
		return SQL;
	}
	
	
	//aggiorna il referencePeriod di uno spedifico film
	public static String SQLupdateReferencePeriodOfMovie(String movieURI, String referencePeriod ){
		String SQL = "UPDATE movies "
					+ "SET `reference_period` = \"" + referencePeriod + "\""
		    		+ " WHERE `uri` = \"" + movieURI + "\";";
		
		return SQL;
	}
	
	public static String SQLupdateTrailerOfMovie(String movieURI, String trailerURI ){
		String SQL = "UPDATE movies" +
			    " SET `trailer` = \"" + trailerURI + "\"" +
				" WHERE `uri` = \"" + movieURI + "\";";
		
		return SQL;
	}
	
	
	public static String SQLselectAllUriTitleAndYearFromMovies() {
		//String SQL = "SELECT DISTINCT uri,title FROM movies WHERE release_year > 1950 AND imdb_rating IS NULL;";
		String SQL = 	"SELECT DISTINCT uri,title,release_year FROM movies "
						+ "WHERE imdb_id IS NULL order by release_year desc;";
		return SQL;
	}
	
	public static String SQLselectAllMovies() {
		//String SQL = "SELECT DISTINCT uri,title FROM movies WHERE release_year > 1950 AND imdb_rating IS NULL;";
		String SQL = 	"SELECT DISTINCT uri,title FROM movies "
						+ "WHERE imdb_rating IS NULL;";
		return SQL;
	}

	public static String SQLselectAllMoviesWithTrailer() {
		String SQL = 	"SELECT DISTINCT uri,title FROM movies "
						+ "WHERE trailer IS NOT NULL "
						+ "AND imdb_rating IS NULL;";
		return SQL;
	}
	
	//Per aggiornare la tabella genres_movies
	public static String SQLselectAllMoviesNotInGenresMovies() {
		String SQL = 	"SELECT uri,title FROM movies" +
						" WHERE `poster` != \"N/A\" AND movies.uri <> all (SELECT movie_uri FROM genres_movies);";
						//" WHERE movies.release_year != 'null' AND movies.release_year < 1900;";
						//" WHERE `poster` = \"N/A\";";
		return SQL;
	}
	
	//seleziona i film su cui aggiornare il runtime_range
	public static String SQLselectMovieAndRuntimeMinutes(){
		String SQL =  "SELECT uri,runtime_minutes FROM movies "
						+ ";";
						//+ "WHERE runtime_range IS NULL;";
						//+ "WHERE runtime_range > 120;";
						
		return SQL;
	}
	
	//seleziona i film su cui aggiornare il reference period
	public static String SQLselectMovieAndReferencePeriod(){
		String SQL =  "SELECT uri,release_year FROM movies "
						+ ";";
						//+ "WHERE runtime_range IS NULL;";
						//+ "WHERE runtime_range > 120;";
						
		return SQL;
	}

	//Estrae il name dall'uri della categoria
	public static String SQLselectNameOfCategoryInVertexTable(){
		String SQL =  "SELECT uri, SUBSTRING(uri, 29,  Length (uri) ) AS name "
					+ "FROM vertices_trailer_selection "
					+ "WHERE name IS NULL;";
						
		return SQL;
	}
	
	

}
