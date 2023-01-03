package database;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class UpdateAccessRecsysDB {
	private static Connection connect = null;
	private static Statement statement = null;
	private static Statement propertyStatement = null;
	private static ResultSet resultSet = null;
	private static ResultSet propertyResultSet = null;	

	private static String driver = "com.mysql.jdbc.Driver";
	private static String url    = DBConstants.dbURL + DBConstants.dbName;
	
	private static String username = "frencisdrame";
	private static String password = "recsys16";
	
	// per chiudere i vari resultSet
	private void close() {
		try {
		   if (resultSet != null) {
		     resultSet.close();}
		   if (statement != null) {
		     statement.close();}
		   if (connect != null) {
		     connect.close();}			 
		   if (propertyResultSet != null) {
		     propertyResultSet.close();}
		   if (propertyStatement != null) {
		     propertyStatement.close();}
		} 
		catch (Exception e) {
			System.err.println("Database Exception - Close");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void insertDatasetTripleToMoviesPropertiesDbpedia(String resourceURI, String predicateURI , String objectURI) throws IOException {
		String SQL = UpdateQueryClass.SQLinsertDatasetTripleToMoviesPropertiesDbpedia(resourceURI, predicateURI,objectURI);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQL);
		   } 
		catch (Exception e) { 
		   System.err.println("Database Exception - insertDatasetTripleToMoviesPropertiesDbpedia");
		   System.err.println("" +SQL);
		   System.err.println(e.getMessage());
		   e.printStackTrace(); 
		} 
		finally { close(); }
	}
	
	public void insertDirectorAndMovie(String directorURI,String resourceURI, String name) throws Exception {
		String SQLdirector = UpdateQueryClass.SQLinsertDirector(directorURI, name);
		String SQLmovie = UpdateQueryClass.SQLinsertMovie(resourceURI);
		String SQLdirectorAndMovie = UpdateQueryClass.SQLinsertDirectorAndMovie(directorURI, resourceURI);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLdirector);
		     statement.executeUpdate(SQLmovie);
		     statement.executeUpdate(SQLdirectorAndMovie);
		   } 
		catch (Exception e) { 
		   System.err.println("Database Exception - insertDirectorAndMovie");
		   System.err.println("" +SQLdirector);
		   System.err.println("" +SQLmovie);
		   System.err.println("" +SQLdirectorAndMovie);
           System.err.println(e.getMessage());
           e.printStackTrace(); 
		} 
		finally { close(); }
	}
	
	public void insertProducerAndMovie(String producerURI,String resourceURI, String name) throws Exception {
		String SQLproducer = UpdateQueryClass.SQLinsertProducer(producerURI, name);
		String SQLmovie = UpdateQueryClass.SQLinsertMovie(resourceURI);
		String SQLproducerAndMovie = UpdateQueryClass.SQLinsertProducerAndMovie(producerURI, resourceURI);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLproducer);
		     statement.executeUpdate(SQLmovie);
		     statement.executeUpdate(SQLproducerAndMovie);
		   } 
		catch (Exception e) { 
		   System.err.println("Database Exception!");
		   System.err.println("" +SQLproducer);
		   System.err.println("" +SQLmovie);
		   System.err.println("" +SQLproducerAndMovie);
           System.err.println(e.getMessage());
           e.printStackTrace(); 
		} 
		finally { close(); }
	}
	
	public void insertWriterAndMovie(String writerURI,String resourceURI, String name) throws Exception {
		String SQLwriter = UpdateQueryClass.SQLinsertWriter(writerURI, name);
		String SQLmovie = UpdateQueryClass.SQLinsertMovie(resourceURI);
		String SQLwriterAndMovie = UpdateQueryClass.SQLinsertWriterAndMovie(writerURI, resourceURI);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLwriter);
		     statement.executeUpdate(SQLmovie);
		     statement.executeUpdate(SQLwriterAndMovie);
		   } 
		catch (Exception e) { 
		   System.err.println("Database Exception - insertWriterAndMovie");
		   System.err.println("" +SQLwriter);
		   System.err.println("" +SQLmovie);
		   System.err.println("" +SQLwriterAndMovie);
           System.err.println(e.getMessage());
           e.printStackTrace(); 
		} 
		finally { close(); }
	}
	
	public void insertStarringAndMovie(String starringURI, String movieURI,String name) throws Exception {
		String SQLstarring = UpdateQueryClass.SQLinsertStarring(starringURI, name);
		String SQLmovie = UpdateQueryClass.SQLinsertMovie(movieURI);
		String SQLstarringAndMovie = UpdateQueryClass.SQLinsertStarringAndMovie(starringURI, movieURI);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLstarring);
		     statement.executeUpdate(SQLmovie);
		     statement.executeUpdate(SQLstarringAndMovie); 
		   } 
		catch (Exception e) { 
           System.err.println("Database Exception - insertStarringAndMovie");
		   System.err.println("" +SQLstarring);
		   System.err.println("" +SQLmovie);
		   System.err.println("" +SQLstarringAndMovie);
           System.err.println(e.getMessage());
           e.printStackTrace();
		} 
		finally { close(); }
	}
	
	public void insertMusicComposerAndMovie(String MusicComposerURI,String resourceURI, String name) throws Exception {
		String SQLmusicComposer = UpdateQueryClass.SQLinsertMusicComposer(MusicComposerURI, name);
		String SQLmovie = UpdateQueryClass.SQLinsertMovie(resourceURI);
		String SQLmusicComposerAndMovie = UpdateQueryClass.SQLinsertMusicComposerAndMovie(MusicComposerURI, resourceURI);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLmusicComposer);
		     statement.executeUpdate(SQLmovie);
		     statement.executeUpdate(SQLmusicComposerAndMovie);
		   } 
		catch (Exception e) { 
		   System.err.println("Database Exception - insertMusicComposerAndMovie");
		   System.err.println("" +SQLmusicComposer);
		   System.err.println("" +SQLmovie);
		   System.err.println("" +SQLmusicComposerAndMovie);
           System.err.println(e.getMessage());
           e.printStackTrace(); 
		} 
		finally { close(); }
	}
	
	public void insertCinematographyAndMovie(String cinematographyURI,String resourceURI, String name) throws Exception {
		String SQLcinematography = UpdateQueryClass.SQLinsertCinematography(cinematographyURI, name);
		String SQLmovie = UpdateQueryClass.SQLinsertMovie(resourceURI);
		String SQLcinematographyAndMovie = UpdateQueryClass.SQLinsertCinematographyAndMovie(cinematographyURI, resourceURI);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLcinematography);
		     statement.executeUpdate(SQLmovie);
		     statement.executeUpdate(SQLcinematographyAndMovie);
		   } 
		catch (Exception e) { 
		   System.err.println("Database Exception - insertCinematographyAndMovie");
		   System.err.println("" +SQLcinematography);
		   System.err.println("" +SQLmovie);
		   System.err.println("" +SQLcinematographyAndMovie);
           System.err.println(e.getMessage());
           e.printStackTrace(); 
		} 
		finally { close(); }
	}
	
	public void insertEditingAndMovie(String editingURI,String resourceURI, String name) throws Exception {
		String SQLediting = UpdateQueryClass.SQLinsertEditing(editingURI, name);
		String SQLmovie = UpdateQueryClass.SQLinsertMovie(resourceURI);
		String SQLeditingAndMovie = UpdateQueryClass.SQLinsertEditingAndMovie(editingURI, resourceURI);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLediting);
		     statement.executeUpdate(SQLmovie);
		     statement.executeUpdate(SQLeditingAndMovie);
		   } 
		catch (Exception e) { 
		   System.err.println("Database Exception - insertEditingAndMovie");
		   System.err.println("" +SQLediting);
		   System.err.println("" +SQLmovie);
		   System.err.println("" +SQLeditingAndMovie);
           System.err.println(e.getMessage());
           e.printStackTrace(); 
		} 
		finally { close(); }
	}
	
	public void insertDistributorAndMovie(String distributorURI,String resourceURI, String name) throws Exception {
		String SQLdistributor = UpdateQueryClass.SQLinsertDistributor(distributorURI, name);
		String SQLmovie = UpdateQueryClass.SQLinsertMovie(resourceURI);
		String SQLdistributorAndMovie = UpdateQueryClass.SQLinsertDistributorAndMovie(distributorURI, resourceURI);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLdistributor);
		     statement.executeUpdate(SQLmovie);
		     statement.executeUpdate(SQLdistributorAndMovie);
		   } 
		catch (Exception e) { 
		   System.err.println("Database Exception - insertDistributorAndMovie");
		   System.err.println("" +SQLdistributor);
		   System.err.println("" +SQLmovie);
		   System.err.println("" +SQLdistributorAndMovie);
           System.err.println(e.getMessage());
           e.printStackTrace(); 
		} 
		finally { close(); }
	}
	
	public void insertBasedOnAndMovie(String basedOnURI,String resourceURI, String name) throws Exception {
		String SQLbasedOn = UpdateQueryClass.SQLinsertBasedOn(basedOnURI, name);
		String SQLmovie = UpdateQueryClass.SQLinsertMovie(resourceURI);
		String SQLbasedOnAndMovie = UpdateQueryClass.SQLinsertBasedOnAndMovie(basedOnURI, resourceURI);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLbasedOn);
		     statement.executeUpdate(SQLmovie);
		     statement.executeUpdate(SQLbasedOnAndMovie);
		   } 
		catch (Exception e) { 
		   System.err.println("Database Exception - insertBasedOnAndMovie");
		   System.err.println("" +SQLbasedOn);
		   System.err.println("" +SQLmovie);
		   System.err.println("" +SQLbasedOnAndMovie);
           System.err.println(e.getMessage());
           e.printStackTrace(); 
		} 
		finally { close(); }
	}
	
	public void insertCategoryAndMovie(String categoryURI,String resourceURI, String name) throws Exception {
		String SQLcategory = UpdateQueryClass.SQLinsertCategory(categoryURI, name);
		String SQLmovie = UpdateQueryClass.SQLinsertMovie(resourceURI);
		String SQLcategoryAndMovie = UpdateQueryClass.SQLinsertCategoryAndMovie(categoryURI, resourceURI);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLcategory);
		     statement.executeUpdate(SQLmovie);
		     statement.executeUpdate(SQLcategoryAndMovie);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - insertCategoryAndMovie");
			System.err.println("" +SQLcategory);
			System.err.println("" +SQLmovie);
			System.err.println("" +SQLcategoryAndMovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }		
	}
	
	public void insertTitleOfMovie(String resourceURI, String title) throws IOException {
		String SQLtitleOfMovie = UpdateQueryClass.SQLupdateTitleOfMovie(resourceURI, title);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLtitleOfMovie);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - insertTitleOfMovie");
			System.err.println("" + SQLtitleOfMovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }		
	}
	
	public void insertReleaseDateAndMovie(String resourceURI, String releaseDate) throws Exception {
		String SQLreleaseDateAndMovie = UpdateQueryClass.SQLinsertReleaseDateAndMovie(resourceURI, releaseDate);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLreleaseDateAndMovie);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - insertReleaseDateAndMovie");
			System.err.println("" + SQLreleaseDateAndMovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }		
	}
	
	public void insertRuntimeAndMovie(String resourceURI, String runtimeURI,  int runtimeMinutes, int runtimeRange) throws Exception {
		String SQLruntimeAndMovie = UpdateQueryClass.SQLinsertRuntimeAndMovie(resourceURI, runtimeURI, runtimeMinutes, runtimeRange);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLruntimeAndMovie);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - insertRuntimeAndMovie");
			System.err.println("" + SQLruntimeAndMovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }		
	}
	
	//Aggiorna il runtime range dei film
	public void updateRuntimeRangeOfMovies(Map<String, Integer>moviesRuntimerangeMap) throws Exception{
		String SQL = null;
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();

			 for (Entry<String, Integer> entry : moviesRuntimerangeMap.entrySet()) {
	            String movieURI = entry.getKey();
	            int runtimeRange = entry.getValue();
				SQL = UpdateQueryClass.SQLupdateRuntimeRangeOfMovie(movieURI, runtimeRange);
				statement.executeUpdate(SQL);
				//System.out.print("movieURI: " + movieURI);
				//System.out.println(" - runtimeRange: " + runtimeRange);
	        }	 	     
		} 
		catch (Exception e) {
			System.err.println("Database Exception - updateRuntimeRangeOfMovies");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }			
	}
	
	//aggiorna il reference_period dei film
	public void updateReferencePeriodOfMovies(Map<String, String> moviesReferencePeriodMap) throws Exception{
		String SQL = null;
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
			 
			 int i = moviesReferencePeriodMap.size();
			 for (Entry<String, String> entry : moviesReferencePeriodMap.entrySet()) {
	            String movieURI = entry.getKey();
	            String referencePeriod = entry.getValue();
	            System.out.println("i: " + i + " - movieURI: " + movieURI + " - referencePeriod: " + referencePeriod);
				SQL = UpdateQueryClass.SQLupdateReferencePeriodOfMovie(movieURI, referencePeriod);
				statement.executeUpdate(SQL);
				i--;
	        }	 	     
		} 
		catch (Exception e) {
			System.err.println("Database Exception - updateRuntimeRangeOfMovies");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }			
	}
	
	//aggiorna release_year dall'uri
	public void updateReleaseYearOfMovies(Map<String, String> moviesReferencePeriodMap) throws Exception{
		String SQL = null;
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
			 int i = moviesReferencePeriodMap.size();
			 for (Entry<String, String> entry : moviesReferencePeriodMap.entrySet()) {
	            String movieURI = entry.getKey();
	            if (movieURI.length() >= 28 && movieURI.contains("_film)") && !movieURI.contains("TV") && !movieURI.contains("UK") &&!movieURI.contains("US")) {
	    			String releaseYear = movieURI.substring((movieURI.length()-10),(movieURI.length()-6));
	    			if (releaseYear.matches("\\d+")){
	    				int year = Integer.parseInt(releaseYear);
	    				SQL = UpdateQueryClass.SQLupdateReleaseYearOfMovie(movieURI, year);
		    			statement.executeUpdate(SQL);
		    			System.out.println("i: " + i + " - movieURI: " + movieURI + " - releaseYear: " + releaseYear);
					}	    			
	            }
	           i--;
	        }	 	     
		} 
		catch (Exception e) {
			System.err.println("Database Exception - updateReleaseYearOfMovies");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }			
	}
	
	//aggiorna il name della categoria in base all'uri
	public void updateNameOfCategoryInVertexTable(Map<String, String> categoryUriNameMap) throws Exception{
		String SQL = null;
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
			 int i = categoryUriNameMap.size();
			 for (Entry<String, String> entry : categoryUriNameMap.entrySet()) {
	            String categoryUri = entry.getKey();
	            String category_name = entry.getValue();
	            category_name = URLDecoder.decode(category_name, "UTF-8");
	            
	            String categoryName = null;        

	            if (category_name.contains("Category:")) {
	            	category_name = category_name.replaceAll("Category:", "");
	            	categoryName = category_name.replaceAll("_", " ");
	            }
            	else {
            		categoryName = category_name.replaceAll("_", " ");
				}
	    		SQL = UpdateQueryClass.SQLupdateNameOfCategoryInVertexTable(categoryUri, categoryName);
		    	statement.executeUpdate(SQL);
		    	System.out.println("i: " + i + " - categoryUri: " + categoryUri + " - categoryName: " + categoryName);	    			
	           i--;
	        }	 	     
		} 
		catch (Exception e) {
			System.err.println("Database Exception - updateReleaseYearOfMovies");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }			
	}

	public void updateTrailerOfMovie(Map<String, String> moviesTrailersMap) throws Exception{
		String SQL = null;
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();

			 for (Entry<String, String> entry : moviesTrailersMap.entrySet()) {
	            String movieURI = entry.getKey();
	            String trailerURI = entry.getValue();
				SQL = UpdateQueryClass.SQLupdateTrailerOfMovie(movieURI, trailerURI );
				statement.executeUpdate(SQL);
				//System.out.print("movieURI: " + movieURI);
				//System.out.println(" - trailerURI: " + trailerURI);
	        }	 	     
		} 
		catch (Exception e) {
			System.err.println("Database Exception - updateTrailerOfMovie");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }			
	}

	public void updateFiledIMDbOfMovie(String resourceURI,String metascore,String imdbRating,String imdbVotes,String imdbID) throws Exception {
		String SQL = UpdateQueryClass.SQLupdateFiledIMDbOfMovie(resourceURI, metascore, imdbRating, imdbVotes, imdbID);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
			 //System.out.println(resourceURI + ": metascore=" + metascore + ", imdbRating=" + imdbRating + ", imdbVotes=" + imdbVotes + ", imdbID=" + imdbID + "");
		     statement.executeUpdate(SQL);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - updateFiledIMDbOfMovie");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }		
	}
	
	
	public void insertInfoOfMovie(String resourceURI,String title,String year,String released,int runtime,String plot,String language,String country,String awards,String poster) throws Exception {
		String SQLMovie = UpdateQueryClass.SQLupdateInfoOfMovie(resourceURI, title, year, released, runtime, plot, language, country, awards, poster);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLMovie);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - insertTitleAndPosterOfMovie");
			System.err.println("" + SQLMovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }		
	}
	
	public void updateAllInfoOfMovie(String uri, String release_date,int runtime_minutes,String plot,String language,String country,String awards,String poster, String metascore, String imdb_rating, String imdb_votes, String imdb_id) throws Exception {
		String SQLMovie = UpdateQueryClass.SQLupdateAllInfoOfMovie(uri, release_date, runtime_minutes, plot, language, country, awards, poster, metascore, imdb_rating, imdb_votes, imdb_id);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQLMovie);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - updateAllInfoOfMovie");
			System.err.println("" + SQLMovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }		
	}
	
	public void insertGenreAndMovie(String genreName,String resourceURI) throws Exception {
		String SQLgenre = UpdateQueryClass.SQLinsertGenre(genreName);
		String SQLmovie = UpdateQueryClass.SQLinsertMovie(resourceURI);
		String SQLgenreAndMovie = UpdateQueryClass.SQLinsertGenreAndMovie(genreName, resourceURI);
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     //statement.executeUpdate(SQLgenre);
		     //statement.executeUpdate(SQLmovie);
		     statement.executeUpdate(SQLgenreAndMovie);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - insertGenreAndMovie");
			System.err.println("" +SQLgenre);
			System.err.println("" +SQLmovie);
			System.err.println("" +SQLgenreAndMovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }		
	}
	
	public void insertGenreAndMovieToProperties()  throws Exception {
		String SQL = UpdateQueryClass.SQLinsertGenreAndMovieToProperties();
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQL);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - insertGenreAndMovieToProperties");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }		
	}
	
	public Map<String, String> selectAllUriTitleAndYearFromMovies() throws Exception {
		Map<String, String> movieTitleMultiMap = new HashMap<String, String>();
		String SQLmovie = UpdateQueryClass.SQLselectAllUriTitleAndYearFromMovies();
		resultSet = null;
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQLmovie);
		     while (resultSet.next()) {
		    	 String movieURI = resultSet.getString("uri");
		    	 String movieTitle = resultSet.getString("title");
		    	 String year = resultSet.getString("release_year");
		    	 StringBuilder value = new StringBuilder();
		    	 value.append(movieTitle);
		    	 value.append("#"); //stringhe separate da uno spazio
		    	 value.append(year);
		    	 String valueString = value.toString();
		    	 
		    	 //String value = movieTitle +"|" + year;
		    	 if (movieURI != null) {
		    		 movieTitleMultiMap.put(movieURI, valueString);
		    	 }		    	 
		     }		     
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectAllMovies");
			System.err.println("" +SQLmovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }	
		
		System.out.println("selectAllUriTitleAndYearFromMovies: " + movieTitleMultiMap.size());
		return movieTitleMultiMap;
	}
	
	public Map<String, String> selectAllMovies() throws Exception {
		Map<String, String> movieTitleMultiMap = new HashMap<String, String>();
		//String SQLmovie = UpdateQueryClass.SQLselectAllMoviesWithTrailer();
		String SQLmovie = UpdateQueryClass.SQLselectAllMovies();
		resultSet = null;
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQLmovie);
		     while (resultSet.next()) {
		    	 String movieURI = resultSet.getString("uri");
		    	 String movieTitle = resultSet.getString("title");
		    	 if (movieURI != null) {
		    		 movieTitleMultiMap.put(movieURI, movieTitle);
		    	 }		    	 
		     }		     
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectAllMovies");
			System.err.println("" +SQLmovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }		
		//System.out.println("selectAllMovies: " + movieTitleMultiMap.toString());
		return movieTitleMultiMap;
	}
	
	public Map<String, Integer> selectAllMovieAndSetRuntimeRange()throws Exception {
		Map<String, Integer> movieRuntimeRangeMap = new HashMap<String, Integer>();
		String SQL = UpdateQueryClass.SQLselectMovieAndRuntimeMinutes();		
		resultSet = null;
		try {
			Class.forName(driver);
			connect = DriverManager.getConnection(url,username,password);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SQL);
			while (resultSet.next()){
				String movieURI = resultSet.getString("uri");
				String runtimeMinutesString = resultSet.getString("runtime_minutes");
				if (runtimeMinutesString != null) {
					int runtimeMinutes = Integer.parseInt(runtimeMinutesString);
					int runtimeRange = 0;				
					if (runtimeMinutes <= 90)
						runtimeRange = 90;
					else if (runtimeMinutes > 90 && runtimeMinutes <= 120 )
						runtimeRange = 120;
//					else if (runtimeMinutes > 100 && runtimeMinutes <= 120 )
//						runtimeRange = 120;
					else if (runtimeMinutes > 120 && runtimeMinutes <= 150 )
						runtimeRange = 150;
//					else if (runtimeMinutes > 150 && runtimeMinutes <= 180 )
//						runtimeRange = 180;
					else if (runtimeMinutes > 150){
						runtimeRange = 151;
//					else if (runtimeMinutes > 180){
//						int x = runtimeMinutes/10;
//						runtimeRange = ((x)+1)*10;
						//System.out.print("movieURI: " + movieURI);
						//System.out.print(" - runtimeMinutes: " + runtimeMinutes);
						//System.out.println(" - runtimeRange: " + runtimeRange);
					}
					movieRuntimeRangeMap.put(movieURI, runtimeRange);
				}				
			}
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectAllMovieAndSetRuntimeRange");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }	
		System.out.println("" + SQL);
		System.out.println("movieRuntimeRangeMap size: " + movieRuntimeRangeMap.size());
		System.out.println("movieRuntimeRangeMap: " + movieRuntimeRangeMap.toString());
		
		return movieRuntimeRangeMap;		
	}
	
	//selectAllMovieAndSetReferencePeriod
	public Map<String, String> selectAllMovieAndSetReferencePeriod()throws Exception {
		Map<String, String> movieReferencePeriodMap = new HashMap<String, String>();
		String SQL = UpdateQueryClass.SQLselectMovieAndReferencePeriod();		
		resultSet = null;
		try {
			Class.forName(driver);
			connect = DriverManager.getConnection(url,username,password);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SQL);
			while (resultSet.next()){
				String movieURI = resultSet.getString("uri");
				String releaseYearString = resultSet.getString("release_year");
				if (releaseYearString != null) {
					//1910s-1950s; 1950s-1980s; 1980s-2000s; 2000s-today
					int releaseYear = Integer.parseInt(releaseYearString);
					String referencePeriod = null;				
					if (releaseYear > 1890 && releaseYear <= 1950)
						referencePeriod = "1910-1950";
					else if (releaseYear > 1950 && releaseYear <= 1980)
						referencePeriod = "1950-1980";
					else if (releaseYear > 1980 && releaseYear <= 2000)
						referencePeriod = "1980-2000";
					else if (releaseYear > 2000 && releaseYear <= 2017)
						referencePeriod = "2000-2016";
					movieReferencePeriodMap.put(movieURI, referencePeriod);
				}				
			}
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectAllMovieAndSetReferencePeriod");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }	
		System.out.println("" + SQL);
		System.out.println("movieReferencePeriodMap size: " + movieReferencePeriodMap.size());
		System.out.println("movieReferencePeriodMap: " + movieReferencePeriodMap.toString());
		
		return movieReferencePeriodMap;		
	}
	
	//selectNameOfCategoryInVertexTable
	public Map<String, String> selectNameOfCategoryInVertexTable()throws Exception {
		Map<String, String> categoryUriNameMap = new HashMap<String, String>();
		String SQL = UpdateQueryClass.SQLselectNameOfCategoryInVertexTable();		
		resultSet = null;
		try {
			Class.forName(driver);
			connect = DriverManager.getConnection(url,username,password);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SQL);
			while (resultSet.next()){
				String categoryUri = resultSet.getString("uri");
				String category_name = resultSet.getString("name");
				categoryUriNameMap.put(categoryUri, category_name);			
			}
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectNameOfCategoryInVertexTable");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }	
		System.out.println("" + SQL);
		System.out.println("categoryUriNameMap size: " + categoryUriNameMap.size());
		System.out.println("categoryUriNameMap: " + categoryUriNameMap.toString());
		
		return categoryUriNameMap;		
	}
	
	public static void main(String[] args) throws Exception {
		//UpdateAccessRecsysDB accessDB = new UpdateAccessRecsysDB();
		

	}
}

