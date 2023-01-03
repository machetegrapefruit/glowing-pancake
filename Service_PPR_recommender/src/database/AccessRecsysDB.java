package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.dbcp.BasicDataSource;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import configuration.Configuration;
import configuration.PropertyMapper;
import configuration.PropertyMapping;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.Graph;
import entity.Pair;
import test.EntityRating;
import test.LogMessage;
import test.PropertyRating;
import test.QuestionnaireAnswer;
import test.RecEntityRating;
import weka.core.logging.Logger;

public class AccessRecsysDB {
	
	//Campi deprecati, usati nel vecchio schema
	private String SQLproperty = null;
	private String propertiesTable = null;
	private String propertiesMoviesTable = null;
	private String columnPropertyURI = null;
	private String columnMovieURI = null;
	private String propertyTypeURI = null;
	
	private PropertyMapper propertyMap = Configuration.getDefaultConfiguration().getPropertyMapper();

	private static String driver = "com.mysql.jdbc.Driver";
	private static String url    = DBConstants.dbURL + DBConstants.dbName + "?useSSL=false";
	private static BasicDataSource dataSource;
//	private static String url    = "jdbc:mysql://recsys-amazon-db.c9suqx5fkol0.us-west-2.rds.amazonaws.com:3306/recsys_db";
	
	//Settare il numero di film da considerare nel calcolo del pagerank, 9000 in questo momento
	private static String username;
	private static String password;
	
//	private static String username = "frencisdrame";
//	private static String password = "recsys16";
//	
	static {
		dataSource = new BasicDataSource();
		username = Configuration.getDefaultConfiguration().getDbUser();
		password = Configuration.getDefaultConfiguration().getDbPass();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	    dataSource.setUrl(url);
	    dataSource.setUsername(username);
	    dataSource.setPassword(password);
	    dataSource.setValidationQuery("SELECT 1"); //Validation query, senza di essa la connessione al db potrebbe andare in timeout
	}
	
	public AccessRecsysDB() {


	}
	
	// per chiudere i vari resultSet
	private void close(Connection connect, Statement statement, ResultSet resultSet) {
		try {
			   if (resultSet != null) {
				     resultSet.close();}
				   if (statement != null) {
				     statement.close();}
				   if (connect != null) {
				     connect.close();}	
		} catch (SQLException e) {
			System.err.println("Database Exception - Close");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}
	

	

	
	public void insertUser(String user_id) throws Exception{
		String SQL = QueryClass.SQLinsertUser(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
		      		
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQL);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - insertUser");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
	}

//	public void insertMovie(String movie_uri) throws Exception{
//		String SQL = QueryClass.SQLinsertMovie(movie_uri);
//		try { 
//		      		
//			 connect = dataSource.getConnection();	
//			 statement = connect.createStatement();
//		     statement.executeUpdate(SQL);
//		   } 
//		catch (Exception e) {
//			System.err.println("Database Exception - insertMovie");
//			System.err.println("" + SQL);
//			System.err.println(e.getMessage());
//			e.printStackTrace();
//		} 
//		finally { close(connect, statement, resultSet); }
//	}
	
	@Deprecated
	public void insertVerticesPosterSelection(PageRankWithPriors<String, String> pageRankWithPriors, Graph<String, String> moviesGraph) throws Exception{
		String SQL = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
		      		
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 for ( String vertexString : moviesGraph.getVertices() ) {
				double vertexScore = pageRankWithPriors.getVertexScore(vertexString);
				SQL = QueryClass.SQLinsertVertexPosterSelection(vertexString, vertexScore);
				statement.executeUpdate(SQL);
				//System.out.print("vertexString: " + vertexString);
				//System.out.println(" - scorevertexCount: " + vertexScore);
			 }		     
		} 
		catch (Exception e) {
			System.err.println("Database Exception - insertVerticesPosterSelection");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }			
	}
	
	@Deprecated
	public void insertVerticesTrailerSelection(PageRankWithPriors<String, String> pageRankWithPriors, Graph<String, String> moviesGraph) throws Exception{
		String SQL = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
		      		
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 for ( String vertexString : moviesGraph.getVertices() ) {
				double vertexScore = pageRankWithPriors.getVertexScore(vertexString);
				SQL = QueryClass.SQLinsertVertexTrailerSelection(vertexString, vertexScore);
				statement.executeUpdate(SQL);
				//System.out.print("vertexString: " + vertexString);
				//System.out.println(" - scorevertexCount: " + vertexScore);
			 }		     
		} 
		catch (Exception e) {
			System.err.println("Database Exception - insertVertices");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }			
	}
	
	public void insertScoresRecMovies(String user_id, String movieURI, String propertyTypeURI, String propertyURI,double propertyScore) throws Exception{
		String SQL = QueryClass.SQLinsertScoresRecMovies(user_id, movieURI, propertyTypeURI, propertyURI, propertyScore);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 		
			connect = dataSource.getConnection();	
			statement = connect.createStatement();
			statement.executeUpdate(SQL);   
		   } 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertScoresRecMovies");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
	}
	
	public void insertScoresUserMovies(String user_id, String movieURI, String propertyTypeURI, String propertyURI,double propertyScore) throws Exception{
		String SQL = QueryClass.SQLinsertScoresUserMovies(user_id, movieURI, propertyTypeURI, propertyURI, propertyScore);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 		
			connect = dataSource.getConnection();	
			statement = connect.createStatement();
			statement.executeUpdate(SQL);   
		   } 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertScoresUserMovies");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
	}
	
	public void insertScoresUserProperties(String user_id, String propertyTypeURI, String propertyURI, double propertyScore) throws Exception{
		String SQL = QueryClass.SQLinsertScoresUserProperties(user_id, propertyTypeURI, propertyURI, propertyScore);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 		
			connect = dataSource.getConnection();	
			statement = connect.createStatement();
			statement.executeUpdate(SQL);   
		   } 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertScoresUserProperties");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
	}
	
	
	public void insertAcceptRecMovieRated(String user_id, String movieURI, int rating) throws Exception{
		String SQL = QueryClass.SQLinsertRatingAcceptRecMovies(user_id, movieURI, rating);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
		      		
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (movieURI != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertAcceptRecMovieRated");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally { 
			close(connect, statement, resultSet);
		}
	}
	
	public void insertRecMovieRated (String user_id, String movieURI, int number_recommendation_list, int rating, int position, int pagerank_cicle, String refine,String refocus, String botName,String message_id, int bot_timestamp, String recommendatinsList,String ratingsList){
		String SQL = QueryClass.SQLinsertRecMovieRated(user_id, movieURI, number_recommendation_list, rating, position, pagerank_cicle, refine, refocus, botName, message_id, bot_timestamp, recommendatinsList, ratingsList);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
		      		
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (movieURI != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertRecMovieRated");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally { 
			close(connect, statement, resultSet);
		}
	}
	
	public void insertRecMovieToRating (String user_id, String movieURI, int number_recommendation_list, int position, int pagerank_cicle, String botName,String message_id, int bot_timestamp, int response_time, String recommendationListString){
		String SQL = QueryClass.SQLinsertRecMovieToRating(user_id, movieURI, number_recommendation_list, position, pagerank_cicle, botName, message_id, bot_timestamp, response_time, recommendationListString);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
		      		
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (movieURI != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertRecMovieToRating");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally { 
			close(connect, statement, resultSet);
		}
	}
	
	public void updateWhyRecMovieRequestByUser(String user_id, String movieURI, int number_recommendation_list, String why)throws Exception{
		String SQL = QueryClass.SQLupdateWhyRecMovieRequestByUser(user_id, movieURI, number_recommendation_list, why);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);	     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - updateWhyRecMovieRequestByUser");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
	
	public void insertDetailsMovieRequest(String user_id, String movieURI, String details, int number_recommendation_list, String botName)throws Exception{
		String SQL = QueryClass.SQLinsertDetailsMovieRequest(user_id, movieURI, details, number_recommendation_list, botName);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
		      		
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);
			 System.err.println("" +SQL);
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertDetailsMovieRequest");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
	
	public void insertDetailsMovieRequestLog(String user_id, String movieURI, String details, int number_recommendation_list, String botName)throws Exception{
		String SQL = QueryClass.SQLinsertDetailsMovieRequestLog(user_id, movieURI, details, number_recommendation_list, botName);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
		      		
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);
			 System.err.println("" +SQL);
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertDetailsMovieRequestLog");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
	
	public void updateDetailsRecMovieRequestByUser(String user_id, String movieURI, int number_recommendation_list, String details)throws Exception{
		String SQL = QueryClass.SQLupdateDetailsRecMovieRequestByUser(user_id, movieURI, number_recommendation_list, details);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);	     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - updateDetailsRecMovieRequestByUser");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}

	public void updateRefineRecMovieRatingByUser(String user_id, String movieURI, int number_recommendation_list, String refine)throws Exception{
		String SQL = QueryClass.SQLupdateRefineRecMovieRatingByUser(user_id, movieURI, number_recommendation_list, refine);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);	     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - updateRefineRecMovieRatingByUser");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
	
	public void updateRefocusRecMovieRatingByUser(String user_id, String movieURI, int number_recommendation_list, String refocus)throws Exception{
		String SQL = QueryClass.SQLupdateRefocusRecMovieRatingByUser(user_id, movieURI, number_recommendation_list, refocus);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);	     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - updateRefocusRecMovieRatingByUser");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
	
	public void updateLikeRecMovieRatingByUser(String user_id, String movieURI, int number_recommendation_list, int like)throws Exception{
		String SQL = QueryClass.SQLupdateLikeRecMovieRatingByUser(user_id, movieURI, number_recommendation_list, like);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {    		
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);	
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - updateLikeRecMovieRatingByUser");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
	
	public void updateDislikeRecMovieRatingByUser(String user_id, String movieURI, int number_recommendation_list, int dislike)throws Exception{
		String SQL = QueryClass.SQLupdateDislikeRecMovieRatingByUser(user_id, movieURI, number_recommendation_list, dislike);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);	     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - updateDisdislikeRecMovieRatingByUser");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
    	

	public void insertMovieRated(String user_id, String movieURI, int rating, String lastChange, int numberRecommendationList, String botName) throws Exception{
		String SQL = QueryClass.SQLinsertRatingMovies(user_id, movieURI, rating, lastChange,numberRecommendationList, botName);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (movieURI != null) {
				 int updated = statement.executeUpdate(SQL);
				 System.out.println("Number of updated rows: " + updated);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertMovieRated");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally { 
			close(connect, statement, resultSet);
		}
	}
	
	public void insertMovieRatedToLog(String user_id, String movieURI, int rating, String lastChange, int numberRecommendationList, String botName) throws Exception{
		String SQL = QueryClass.SQLinsertRatingMoviesToLog(user_id, movieURI, rating, lastChange,numberRecommendationList, botName);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (movieURI != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertMovieRatedToLog");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally { 
			close(connect, statement, resultSet);
		}
	}

	public void insertPropertyRated(String user_id, String propertyTypeURI, String propertyURI, int rating, String lastChange,int numberRecommendationList, String botName) throws Exception{
		String SQL = QueryClass.SQLinsertRatingProperties(user_id, propertyTypeURI, propertyURI, rating, lastChange,numberRecommendationList, botName);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (propertyURI != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertPropertyRated");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
	
	public void insertPropertyRatedLog(String user_id, String propertyTypeURI, String propertyURI, int rating, String lastChange,int numberRecommendationList, String botName) throws Exception{
		String SQL = QueryClass.SQLinsertRatingPropertiesLog(user_id, propertyTypeURI, propertyURI, rating, lastChange,numberRecommendationList, botName);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (propertyURI != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertPropertyRatedLog");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
	
	//TODO -DONE propertyType probabilmente non serve
	public void insertReleaseYearFilter(String user_id, String propertyType, String propertyValue) throws Exception{
		String SQL = QueryClass.SQLupdateReleaseYearFilter(user_id, propertyValue);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (propertyType != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertReleaseYearFilter");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
	
	//TODO Da capire se serve propertyType... per il momento scrivo
	public void insertRuntimeRangeFilter(String user_id, String propertyType, String propertyValue) throws Exception{
		String SQL = QueryClass.SQLupdateRuntimeRangeFilter(user_id, propertyValue);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 	
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (propertyType != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertRuntimeRangeFilter");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
	
		public void insertBotName(String user_id,String botName) throws Exception{
		String SQL = QueryClass.SQLupdateBotName(user_id, botName);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (botName != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertBotName");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}

	public void insertBotConfiguration(String user_id,String botName, int number_recommendation_list,int bot_timestamp) throws Exception{
		String SQL = QueryClass.SQLinsertBotConfiguration(user_id, botName, number_recommendation_list, bot_timestamp);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (botName != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertBotConfiguration");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}

		
	public void insertAgeRange(String user_id, String ageRange) throws Exception{
		String SQL = QueryClass.SQLupdateAgeRange(user_id, ageRange);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (ageRange != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertAgeRange");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}

	public void insertEducation(String user_id, String education) throws Exception{
		String SQL = QueryClass.SQLupdateEducation(user_id, education);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (education != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertEducation");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}

	public void insertGender(String user_id, String gender) throws Exception{
		String SQL = QueryClass.SQLupdateGender(user_id, gender);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (gender != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertGender");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}

	public void insertInterestInMovies(String user_id, String interestInMovies) throws Exception{
		String SQL = QueryClass.SQLupdateInterestInMovies(user_id, interestInMovies);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (interestInMovies != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - interestInMovies");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}

	public void insertUsedRecSys(String user_id, String usedRecSys) throws Exception{
		String SQL = QueryClass.SQLupdateUsedRecSys(user_id, usedRecSys);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 if (usedRecSys != null) {
				 statement.executeUpdate(SQL);
			 }		     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertUsedRecSys");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}

	public void insertExperimentalSessionRating(String user_id, int number_recommendation_list,int rating, String botName) throws Exception{
		String SQL = QueryClass.SQLinsertExperimentalSessionRating(user_id, number_recommendation_list, rating, botName);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);
	     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - insertExperimentalSessionRating");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
	
	public Map<String, Integer> selectReleaseYearFilterFromUsersForPropertyRating(String user_id) throws Exception{
		Map<String, Integer> movieOrPropertyToRatingMap = new HashMap<String, Integer>();
		String SQL = QueryClass.SQLselectReleaseYearFilterFromUsersForPropertyRating(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 resultSet = statement.executeQuery(SQL);
			 while (resultSet.next()){
		    	String propertyType = "releaseYear";
				String releaseYearString = resultSet.getString("release_year_filter");
				if (releaseYearString != null && !releaseYearString.isEmpty()) {
					String releaseYearValue = null;
					switch (releaseYearString) {
						case "1910-1950":
							releaseYearValue = "1910s - 1950s";
							break;
						case "1950-1980":
							releaseYearValue = "1950s - 1980s";
							break;
						case "1980-2000":
							releaseYearValue = "1980s - 2000s";
							break;
						case "2000-2016":
							releaseYearValue = "2000s - today";
							break;
						default:
							break;
					}
					String key = propertyType +"," + releaseYearValue;
					movieOrPropertyToRatingMap.put(key, 3);
				}
		     }
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - selectReleaseYearFilterFromUsersForPropertyRating");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
		
		//System.out.println("selectReleaseYearFilterFromUsersForPropertyRating: " + movieOrPropertyToRatingMap);
		return movieOrPropertyToRatingMap;
	}
	
	public Map<String, Integer> selectRuntimeRangeFilterFromUsersForPropertyRating(String user_id) throws Exception {
		Map<String, Integer> movieOrPropertyToRatingMap = new HashMap<String, Integer>();
		String SQL = QueryClass.SQLselectRuntimeRangeFilterFromUsersForPropertyRating(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
		      		
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 resultSet = statement.executeQuery(SQL);
			 while (resultSet.next()){	
		    	String propertyType = "runtimeRange";
				String runtimeRangeString = resultSet.getString("runtime_range_filter");
				if (runtimeRangeString != null && !runtimeRangeString.isEmpty()) {
					String runtimeRangeValue = null;
					switch (runtimeRangeString) {
						case "90":
							runtimeRangeValue = "<= 90 min"; 
							break;
						case "120":
							runtimeRangeValue = "90 - 120 min";
							break;
						case "150":
							runtimeRangeValue = "120 - 150 min";
							break;
						case "151":
							runtimeRangeValue = "> 150 min";
							break;				
						default:
							break;
					}
					String key = propertyType +"," + runtimeRangeValue;
					movieOrPropertyToRatingMap.put(key, 3);
				}
		    }       
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectPropertyByUser");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("selectRuntimeRangeFilterFromUsersForPropertyRating: " + movieOrPropertyToRatingMap);
		return movieOrPropertyToRatingMap;		
	}
	
	
	public void insertUserDetail(String user_id, String firstname,String lastname,String user_name){
		String SQL = QueryClass.SQLinsertUserDetail(user_id, firstname, lastname, user_name);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
		      		
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQL);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - insertUserDetail");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
	}
	
	public void insertChatMessageToChatLog(String user_id, String message_id, String context, String replyText, String replyFunctionCall, int pagerank_cicle, int number_recommendation_list, String botName, int bot_timestamp, int response_time, String responseType, int number_rated_movies, int number_rated_properties) throws Exception{
		String SQL = QueryClass.SQLinsertChatMessageToChatLog(user_id, message_id, context, replyText, replyFunctionCall, pagerank_cicle, number_recommendation_list, botName, bot_timestamp, response_time, responseType, number_rated_movies, number_rated_properties);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
		      		
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);
		} 
		catch (Exception e) {
	       System.err.println("Database Exception - insertChatMessageToChatLog");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);	
		}
	}

	public String insertChatMessage(String user_id, String message_id, String context, String replyText, String replyFunctionCall, int pagerank_cicle, int number_recommendation_list, String botName, int bot_timestamp, int response_time, String responseType, int number_rated_movies, int number_rated_properties) throws Exception{
		String SQL = QueryClass.SQLinsertChatMessage(user_id, message_id, context, replyText, replyFunctionCall, pagerank_cicle,number_recommendation_list, botName, bot_timestamp, response_time, responseType, number_rated_movies, number_rated_properties);
		String returnString = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);
			 returnString = "ok";
	     
		} 
		catch (Exception e) {
			returnString = "Database Exception - insertChatMessage";
	       System.err.println("Database Exception - insertChatMessage");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);	
		}
		return returnString;
	}
	
	public Map<String,String> selectUserDetalByUser(String user_id) throws Exception {
		Map<String,String> userDetailMap = new HashMap<String, String>();
		String SQL = QueryClass.SQLselectUserDetalByUser(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 			
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);     

			while(resultSet.next()) {									
				userDetailMap.put("id", resultSet.getString("id"));
				userDetailMap.put("firstname", resultSet.getString("firstname"));
				userDetailMap.put("lastname", resultSet.getString("lastname"));
				userDetailMap.put("username", resultSet.getString("username"));
				userDetailMap.put("botName", resultSet.getString("bot_name"));
				userDetailMap.put("age", resultSet.getString("age"));
				userDetailMap.put("gender", resultSet.getString("gender"));
				userDetailMap.put("education", resultSet.getString("education"));
				userDetailMap.put("interestInMovies", resultSet.getString("interest_in_movies"));
				userDetailMap.put("usedRecommenderSystem", resultSet.getString("used_recommender_system"));
			}
		}
			
		catch (Exception e) {
			System.err.println("Database Exception - selectMessageDetailByUserAndReplyFunctionCall");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally {close(connect, statement, resultSet);}
		
//		System.out.println("" +SQL);
//		System.out.println("userDetailMap Size: " + userDetailMap.size());
//		System.out.println("userDetailMap: " + userDetailMap.toString());		
		return userDetailMap;
	}

	
	//Testata: se non trova restituisce map vuota
	public Map<String,String> selectMessageDetailAndContextByUser(String user_id, String context, int pagerank_cicle) throws Exception {
		Map<String,String> messageDetailMap = new HashMap<String, String>();
		String SQL = QueryClass.SQLselectMessageDetailAndContextByUser(user_id, context, pagerank_cicle);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 			
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);     

			while(resultSet.next()) {									
				messageDetailMap.put("id", resultSet.getString("id"));
				messageDetailMap.put("chat_id", resultSet.getString("chat_id"));
				messageDetailMap.put("message_id", resultSet.getString("message_id"));
				messageDetailMap.put("context", resultSet.getString("context"));
				messageDetailMap.put("reply_text", resultSet.getString("reply_text"));
				messageDetailMap.put("reply_function_call", resultSet.getString("reply_function_call"));
				messageDetailMap.put("pagerank_cicle", resultSet.getString("pagerank_cicle"));
				messageDetailMap.put("current_timestamp", resultSet.getString("current_timestamp"));
				messageDetailMap.put("bot_name", resultSet.getString("bot_name"));
				messageDetailMap.put("bot_timestamp", resultSet.getString("bot_timestamp"));
				messageDetailMap.put("response_time", resultSet.getString("response_time"));
				messageDetailMap.put("response_type", resultSet.getString("response_type"));
				messageDetailMap.put("number_rated_movies", resultSet.getString("number_rated_movies"));
				messageDetailMap.put("number_rated_properties", resultSet.getString("number_rated_properties"));
			}
		}
			
		catch (Exception e) {
			System.err.println("Database Exception - selectMessageDetailByUserAndReplyFunctionCall");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally {close(connect, statement, resultSet);}
		
//		System.out.println("" +SQL);
//		System.out.println("messageDetailMap Size: " + messageDetailMap.size());
//		System.out.println("messageDetailMap: " + messageDetailMap.toString());		
		return messageDetailMap;
	}
	
	//Testata: se non trova restituisce map vuota
	//Utile per controllare se l'utente ha giï¿½ fatto una valutazione di quella proprietï¿½
	public Map<String,String> selectPropertyRatingByUserAndProperty(String user_id, String propertyTypeUri, String propertyUri,String lastChange){
		Map<String,String> propertyRatingMap = new HashMap<String, String>();
		String SQL = QueryClass.SQLselectPropertyRatingByUserAndProperty(user_id, propertyTypeUri, propertyUri, lastChange);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 			
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);     

			while(resultSet.next()) {									
				propertyRatingMap.put("user_id", resultSet.getString("user_id"));
				propertyRatingMap.put("property_type_uri", resultSet.getString("property_type_uri"));
				propertyRatingMap.put("property_uri", resultSet.getString("property_uri"));
				propertyRatingMap.put("rating", resultSet.getString("rating"));
				propertyRatingMap.put("rated_at", resultSet.getString("rated_at"));
				propertyRatingMap.put("last_change", resultSet.getString("last_change"));
			}
		}
			
		catch (Exception e) {
			System.err.println("Database Exception - selectPropertyRatingByUserAndProperty");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally {close(connect, statement, resultSet);}
		
//		System.out.println("" +SQL);
//		System.out.println("propertyRatingMap Size: " + propertyRatingMap.size());
//		System.out.println("propertyRatingMap: " + propertyRatingMap.toString());		
		return propertyRatingMap;
	}

	public ArrayListMultimap<String, Set<String>> selectPosNegRatingForUserFromRatingsMovies(String user_id)throws Exception{
		Set<String> posRatingSet, negRatingSet;
		ArrayListMultimap<String, Set<String>> ratingsMoviesMap = ArrayListMultimap.create();
		List<Set<String>> posNegRatings = new ArrayList<Set<String>>();
        posRatingSet = new TreeSet<>();
        negRatingSet = new TreeSet<>();
		String SQLmovie = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			 connect = dataSource.getConnection();	
			 SQLmovie = QueryClass.SQLselectPosRatingForUserFromRatingsMovies(user_id);	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQLmovie);
		     while (resultSet.next()) {
		    	 String movieString = resultSet.getString("movie_uri");
		    	 posRatingSet.add(movieString);
		     }
		     //http://dbpedia.org/resource/Tom_Hanks 0.0004511343650114009
		     //http://dbpedia.org/resource/Tom_Hanks 0.011714313966078816
		     //posRatingSet.add("http://dbpedia.org/resource/Tom_Hanks");
		     SQLmovie = null;
			 SQLmovie = QueryClass.SQLselectNegRatingForUserFromRatingsMovies(user_id);	
			 Statement propertyStatement = connect.createStatement();
		     ResultSet propertyResultSet = propertyStatement.executeQuery(SQLmovie);
		     while (propertyResultSet.next()) {
		    	 String movieString = propertyResultSet.getString("movie_uri");
		    	 negRatingSet.add(movieString);
		     }
		     
		     posNegRatings.add(posRatingSet);		   
		     posNegRatings.add(negRatingSet);
		     ratingsMoviesMap.putAll(String.valueOf(user_id), posNegRatings);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - selectPosNegRatingForUserFromRatings");
			System.err.println("" + SQLmovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { 
			close(connect, statement, resultSet);
		}
		
		//System.out.println("trainingPosNeg - ratingsMoviesMap: " + ratingsMoviesMap.toString());
		//{6=[[http://dbpedia.org/resource/Fight_Club, http://dbpedia.org/resource/Forrest_Gump, http://dbpedia.org/resource/Inception], []]}
		return ratingsMoviesMap;			
		
	}
	
	public ArrayListMultimap<String, Set<String>> selectPosNegRatingForUserFromRatingsMoviesByUser(String user_id)throws Exception{
		Set<String> posRatingSet, negRatingSet;
		ArrayListMultimap<String, Set<String>> ratingsMoviesMap = ArrayListMultimap.create();
		List<Set<String>> posNegRatings = new ArrayList<Set<String>>();
        posRatingSet = new TreeSet<>();
        negRatingSet = new TreeSet<>();
		String SQLmovie = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			  		
			 connect = dataSource.getConnection();	
			 SQLmovie = QueryClass.SQLselectPosRatingForUserFromRatingsMoviesByUser(user_id);	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQLmovie);
		     while (resultSet.next()) {
		    	 String movieString = resultSet.getString("movie_uri");
		    	 posRatingSet.add(movieString);
		     }
		     //http://dbpedia.org/resource/Tom_Hanks 0.0004511343650114009
		     //http://dbpedia.org/resource/Tom_Hanks 0.011714313966078816
		     //posRatingSet.add("http://dbpedia.org/resource/Tom_Hanks");
		     SQLmovie = null;
			 SQLmovie = QueryClass.SQLselectNegRatingForUserFromRatingsMoviesByUser(user_id);	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQLmovie);
		     while (resultSet.next()) {
		    	 String movieString = resultSet.getString("movie_uri");
		    	 negRatingSet.add(movieString);
		     }
		     
		     posNegRatings.add(posRatingSet);		   
		     posNegRatings.add(negRatingSet);
		     ratingsMoviesMap.putAll(String.valueOf(user_id), posNegRatings);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - selectPosNegRatingForUserFromRatings");
			System.err.println("" + SQLmovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("trainingPosNeg - ratingsMoviesMap: " + ratingsMoviesMap.toString());
		//{6=[[http://dbpedia.org/resource/Fight_Club, http://dbpedia.org/resource/Forrest_Gump, http://dbpedia.org/resource/Inception], []]}
		return ratingsMoviesMap;			
		
	}
	
	public ArrayListMultimap<String, Set<String>> selectPosNegRatingFromRatingsPropertiesForPageRank(String user_id)throws Exception{
		Set<String> posRatingSet, negRatingSet;
		ArrayListMultimap<String, Set<String>> propertiesRatingsMap = ArrayListMultimap.create();
		List<Set<String>> posNegRatings = new ArrayList<Set<String>>();
        posRatingSet = new TreeSet<>();
        negRatingSet = new TreeSet<>();
		String SQL = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			  		
			 connect = dataSource.getConnection();	
			 SQL = QueryClass.selectPosNegRatingFromRatingsPropertiesForPageRank(user_id);	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 int rating = resultSet.getInt("rating");
		    	 switch (rating) {
				case 0:
					String negPropertyString = resultSet.getString("property_uri");
			    	negRatingSet.add(negPropertyString);
					break;
				case 1:
					String posPropertyString = resultSet.getString("property_uri");
			    	posRatingSet.add(posPropertyString);
					break;
				default:
					break;
				}
		     }		     
		     posNegRatings.add(posRatingSet);		   
		     posNegRatings.add(negRatingSet);
		     propertiesRatingsMap.putAll(String.valueOf(user_id), posNegRatings);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - selectPosNegRatingFromRatingsPropertiesForPageRank");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("propertiesRatingsMapPR: " + propertiesRatingsMap.toString());
		//{6=[[http://dbpedia.org/resource/Edward_Norton, http://dbpedia.org/resource/Leonardo_DiCaprio], [http://dbpedia.org/resource/Ben_Stiller]]}
		return propertiesRatingsMap;			
		
	}
	
	//seleziona solo i nodi valutati negativamente
	public Set<String> selectNegRatingFromRatingsMoviesAndPropertiesForPageRank(String user_id)throws Exception{
		Set<String> negUriSet = new TreeSet<>();
		String SQL = QueryClass.selectNegRatingFromRatingsMoviesAndPropertiesForPageRank(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {	
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 String uri = resultSet.getString("uri");
		    	 negUriSet.add(uri);
		     }		    	 	    
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - selectNegRatingFromRatingsMoviesAndPropertiesForPageRank");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("negUriSet: " + negUriSet.toString());
		//negUriSet: [Comedy, http://dbpedia.org/resource/Matt_Damon, http://dbpedia.org/resource/The_Matrix]
		return negUriSet;			
		
	}
	
	
	//TODO Invertita la chiave per l'ordine - utile! per unire score e property come chiave
	public Map<String, Integer> selectPosNegRatingForUserFromRatings(String user_id) throws Exception {
		Map<String, Integer> movieOrPropertyToRatingMap = new HashMap<String, Integer>();
		String SQLratingsMovies = QueryClass.SQLselectPosNegRatingForUserFromRatingsMovies(user_id);
		String SQLratingsProperties = QueryClass.SQLselectPosNegRatingForUserFromRatingsProperties(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 
		     resultSet = statement.executeQuery(SQLratingsMovies);
		     while (resultSet.next()){					    	
				String movieURI = resultSet.getString("movie_uri");
				String movieType = "entity";
				Integer movieRating = resultSet.getInt("rating");
				if (movieURI != null) {
					//String key = movieURI +"," + movieType;
					String key = movieType +"," + movieURI;
					movieOrPropertyToRatingMap.put(key, movieRating);
				}
		     }
		     resultSet = statement.executeQuery(SQLratingsProperties);
		     while (resultSet.next()){					    	
				String propertyURI = resultSet.getString("property_uri");
				String propertyTypeURI = resultSet.getString("property_type_uri");
				Integer propertyRating = resultSet.getInt("rating");
				if (propertyURI != null) {
					String key = propertyTypeURI +"," + propertyURI;
					movieOrPropertyToRatingMap.put(key, propertyRating);
				}
		     }       
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectPropertyByUser");
			System.err.println("" +SQLratingsMovies);
			System.err.println("" +SQLratingsProperties);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("" +SQLratingsMovies);
		//System.out.println("" +SQLratingsProperties);
		//System.out.println("selectPosNegRatingForUserFromRatings: " + movieOrPropertyToRatingMap);
		return movieOrPropertyToRatingMap;		
	}
	
	public Map<String, Integer> selectRatingsForUserFromRatings(String user_id) throws Exception {
		Map<String, Integer> movieOrPropertyToRatingMap = new HashMap<String, Integer>();
		String SQLratingsMovies = QueryClass.SQLselectRatingsForUserFromRatingsMovies(user_id);
		String SQLratingsProperties = QueryClass.SQLselectRatingsForUserFromRatingsProperties(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 
		     resultSet = statement.executeQuery(SQLratingsMovies);
		     while (resultSet.next()){					    	
				String movieURI = resultSet.getString("movie_uri");
				String movieType = "entity";
				Integer movieRating = resultSet.getInt("rating");
				if (movieURI != null) {
					//String key = movieURI +"," + movieType;
					String key = movieType +"," + movieURI;
					movieOrPropertyToRatingMap.put(key, movieRating);
				}
		     }
		     resultSet = statement.executeQuery(SQLratingsProperties);
		     while (resultSet.next()){					    	
				String propertyURI = resultSet.getString("property_uri");
				String propertyTypeURI = resultSet.getString("property_type_uri");
				Integer propertyRating = resultSet.getInt("rating");
				if (propertyURI != null) {
					String key = propertyTypeURI +"," + propertyURI;
					movieOrPropertyToRatingMap.put(key, propertyRating);
				}
		     }       
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectPropertyByUser");
			System.err.println("" +SQLratingsMovies);
			System.err.println("" +SQLratingsProperties);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("" +SQLratingsMovies);
		//System.out.println("" +SQLratingsProperties);
		//System.out.println("selectPosNegRatingForUserFromRatings: " + movieOrPropertyToRatingMap);
		return movieOrPropertyToRatingMap;		
	}
	
	public Map<String, Set<String>> selectTestSetForUserFromMovies(String user_id) throws Exception{
		Set<String> testSet = new TreeSet<>();
		Map<String, Set<String>> testSetMap = new HashMap<String, Set<String>>();
		String SQLmovie = null;
		Connection connect = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			SQLmovie = QueryClass.SQLselectTestSetForUserFromMovies(user_id);
			statement = connect.prepareStatement(SQLmovie);
			statement.setString(1, user_id);
			resultSet = statement.executeQuery();
			while (resultSet.next()){
				String movieString = resultSet.getString("uri");
				testSet.add(movieString);
			}
			testSetMap.put(String.valueOf(user_id), testSet);
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectTestSetForUserFromMovies");
			System.err.println("" + SQLmovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("" + SQLmovie);
		//System.out.println("testSet: " + testSetMap.toString());
		//{6=[http://dbpedia.org/resource/%22Weird_Al%22_Yankovic_Live!:_The_Alpocalypse_Tour, http://dbpedia.org/resource/%3F:_A_Question_Mark,
		return testSetMap;				
	}

	public Map<String, Set<String>> selectTestSetForUserFromMoviesForPageRank(String user_id) throws Exception{ //TODO
		Set<String> testSet = new TreeSet<>();
		Map<String, Set<String>> testSetMap = new HashMap<String, Set<String>>();
		String SQLmovie = null;
		String SQLmovieReleaseYearFilter = null;
		String SQLmovieRuntimeRangeFilter = null;
		String SQLmovieAllFilter = null;
		Connection connect = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();			
		    resultSet = null;
		    //se non sono settati i filtri - 7670
	    	SQLmovie = QueryClass.SQLselectTestSetForUserFromMovies(user_id);
	    	statement = connect.prepareStatement(SQLmovie);
	    	statement.setString(1, user_id);
	    	resultSet = statement.executeQuery();
	    	System.out.println("" + SQLmovie );
			while (resultSet.next()){
				String movieString = resultSet.getString("uri");
				testSet.add(movieString);
			}
		    
			testSetMap.put(String.valueOf(user_id), testSet);
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectTestSetForUserFromMoviesForPageRank");
			System.err.println("" + SQLmovie );
			System.err.println("" + SQLmovieReleaseYearFilter );
			System.err.println("" + SQLmovieRuntimeRangeFilter );
			System.err.println("" + SQLmovieAllFilter );
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		
		//System.out.println("testSetMap: " + testSetMap.toString());
		//System.out.println("testSet size: " + testSet.size());
		//{6=[http://dbpedia.org/resource/%22Weird_Al%22_Yankovic_Live!:_The_Alpocalypse_Tour, http://dbpedia.org/resource/%3F:_A_Question_Mark,
		return testSetMap;				
	}

	//OBSOLETA C'ï¿½ un problema, non recupera tutti i tipi
	public Set<List<String>> selectAllPropertyFromDbpediaMoviesSelection(String movieURI) throws Exception {
		List<String> propertyList = new ArrayList<String>();
		Set<List<String>> setPropertyList = Sets.newHashSet();
		String SQL = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 				
			 connect = dataSource.getConnection();	
			 SQL = QueryClass.SQLselectAllPropertyFromDbpediaMoviesSelection(movieURI);
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);     
		     while(resultSet.next()) {
		    	propertyList = new ArrayList<String>();
				propertyList.add(resultSet.getString("subject"));
				propertyList.add(resultSet.getString("predicate"));
				propertyList.add(resultSet.getString("object"));
				setPropertyList.add(propertyList);						
				//System.out.println("propertyList: " + propertyList.toString());
			}
		}
			
		catch (Exception e) {
			System.err.println("Database Exception - SQLselectAllPropertyFromPropertiesByMovie");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, statement, resultSet); }
		//System.out.println("" +SQL);
		//System.out.println("listPropertyList Size: " + setPropertyList.size());
		//System.out.println("setPropertyList: " + setPropertyList.toString());
		
		//setPropertyList: [[http://dbpedia.org/resource/Forrest_Gump, http://purl.org/dc/terms/subject, http://dbpedia.org/resource/Category:Films_featuring_a_Best_Actor_Academy_Award_winning_performance],... 
		return setPropertyList;
	}
	//TODO - DONE: per il pagerank
	public Map<String,Set<List<String>>> selectAllResourceAndPropertyFromDbpediaMoviesSelection(Set<String> allItemsID) throws Exception { //TODO
		List<String> propertyList = new ArrayList<String>();
		Set<List<String>> setPropertyList = Sets.newHashSet();
		Map<String,Set<List<String>>> resourceAndPropertyListMultimap = new HashMap<String, Set<List<String>>>();
		String SQL = null;
		Connection connect = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try { 			
			 connect = dataSource.getConnection();
			 for (String resourceURI : allItemsID){
				 //System.out.print("" + resourceURI.toString());
				 SQL = QueryClass.SQLselectAllPropertyFromDbpediaMoviesSelection(resourceURI);
				 statement = connect.prepareStatement(SQL);
				 statement.setString(1, resourceURI);
			     resultSet = statement.executeQuery(); 
			     while(resultSet.next()) {
			    	propertyList = new ArrayList<String>();
					propertyList.add(resultSet.getString("subject"));
					propertyList.add(resultSet.getString("predicate"));
					propertyList.add(resultSet.getString("object"));
					setPropertyList.add(propertyList);						
					//System.out.println("propertyList: " + propertyList.toString());
				}
			     //System.out.println(resourceURI.toString()+ " - setPropertyList size: " + setPropertyList.size());
			     //System.out.println("setPropertyList: " + setPropertyList.toString());
			     resourceAndPropertyListMultimap.put(resourceURI, setPropertyList);
			     setPropertyList = Sets.newHashSet();
			 }			 
		}
			
		catch (Exception e) {
			System.err.println("Database Exception - SQLselectAllPropertyFromPropertiesByMovie");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, statement, resultSet); }
		//System.out.println("" +SQL);
		System.out.println("resourceAndPropertyListMultimap Size: " + resourceAndPropertyListMultimap.size());
		//System.out.println("resourceAndPropertyListMultimap: " + resourceAndPropertyListMultimap.toString()); 
		return resourceAndPropertyListMultimap;
	}
	
	//Obsoleta - nella nuova versione prendiamo solo il propertyValueURI
	public Map<String, Set<String>> selectAllResourceAndPropertTypeValueLikeStringFromDbpediaMoviesSelection(Set<String> allItemsID) throws Exception {
		Set<String> setProperty = Sets.newHashSet();
		Map<String,Set<String>> resourceAndPropertySetMap = new HashMap<String, Set<String>>();
		String SQL = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 			
			 connect = dataSource.getConnection();
			 statement = connect.createStatement();
			 for (String resourceURI : allItemsID){
				 setProperty = Sets.newHashSet();
				 SQL = QueryClass.SQLselectAllPropertyFromDbpediaMoviesSelection(resourceURI);				 
			     resultSet = statement.executeQuery(SQL);
			     while(resultSet.next()) {
			    	String propertyTypeAndValueString = null;
			    	StringBuilder propertyTypeAndValue = new StringBuilder();
			    	String propertyTypeURI = resultSet.getString("predicate");
			    	String propertyURI = resultSet.getString("object");
			    	if (propertyURI != null) {
			    		 propertyTypeAndValue = new StringBuilder();
			    		 propertyTypeAndValue.append(propertyTypeURI);
			    		 propertyTypeAndValue.append(" "); //stringhe separate da uno spazio
			    		 propertyTypeAndValue.append(propertyURI);
			    		 propertyTypeAndValueString = propertyTypeAndValue.toString();	
			    		 setProperty.add(propertyTypeAndValueString);						
					//System.out.println("setProperty: " + setProperty.toString());
					}				     
			     }
			   //System.out.println("resourceURI:" + resourceURI + " - setProperty size:" + setProperty.size());
			     resourceAndPropertySetMap.put(resourceURI, setProperty);
			 }
		}			
		catch (Exception e) {
			System.err.println("Database Exception - selectAllResourceAndPropertTypeValueLikeStringFromDbpediaMoviesSelection");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, statement, resultSet); }
		//System.out.println("" +SQL);
		System.out.println("resourceAndPropertySetMap Size: " + resourceAndPropertySetMap.size());
		//System.out.println("resourceAndPropertySetMap: " + resourceAndPropertySetMap.toString()); 
		return resourceAndPropertySetMap;
	}
	
	public Map<String,Set<String>> selectAllResourceAndPropertValueUriFromDbpediaMoviesSelection(Set<String> allItemsID) throws Exception {
		Set<String> setPropertyValue = Sets.newHashSet();
		Map<String,Set<String>> resourceAndPropertyValueSetMap = new HashMap<String, Set<String>>();
		String SQL = null;
		Connection connect = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try { 			
			 connect = dataSource.getConnection();
			 for (String resourceURI : allItemsID){
				 setPropertyValue = Sets.newHashSet();
				 SQL = QueryClass.SQLselectAllPropertyFromDbpediaMoviesSelection(resourceURI);		
				 statement = connect.prepareStatement(SQL);
				 statement.setString(1, resourceURI);
			     resultSet = statement.executeQuery();
			     while(resultSet.next()) {
			    	String propertyValueURI = resultSet.getString("object");
			    	if (propertyValueURI != null) {
			    		 setPropertyValue.add(propertyValueURI);						
					//System.out.println("setPropertyValue: " + setPropertyValue.toString());
					}				     
			     }
			     //System.out.println("resourceURI:" + resourceURI + " - setPropertyValue size:" + setPropertyValue.size());
			     resourceAndPropertyValueSetMap.put(resourceURI, setPropertyValue);
			 }
		}			
		catch (Exception e) {
			System.err.println("Database Exception - selectAllResourceAndPropertValueUriFromDbpediaMoviesSelection");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("" +SQL);
		//System.out.println("resourceAndPropertyValueSetMap Size: " + resourceAndPropertyValueSetMap.size());
		//System.out.println("resourceAndPropertySetMap: " + resourceAndPropertySetMap.toString()); 
		return resourceAndPropertyValueSetMap;
	}
	
	//Cerca una risorsa nella tabella property in modo tale da verificare che l'uri fa parte del grafo
	//Testata, se non c'ï¿½ resitituisce null e recupera la stringa col case sesitive corretto
	@Deprecated
	public String selectResourceUriFromDbpediaMoviesSelection(String uri) throws Exception{
		String SQL = QueryClass.SQLselectResourceUriFromDbpediaMoviesSelection(uri);
		String resourseURI = null;
		String subject = "null";
   	 	String predicate = "null";
   	 	String object = "null";
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 	
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 resourseURI = "null";
		    	 subject = resultSet.getString("subject");
		    	 predicate = resultSet.getString("predicate");
		    	 object = resultSet.getString("object");
		     }

			 if (subject.equalsIgnoreCase(uri)) {
			    resourseURI = subject;}
			 else if (predicate.equalsIgnoreCase(uri)) {
			 	resourseURI = predicate;}
			 else if (object.equalsIgnoreCase(uri)) {
				resourseURI = object;} 
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectResourceUriFromPropertiesMovies");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("resourseURI: " + resourseURI);
		return resourseURI;
	}
	
	//Testata: se non esiste restituisce null altrimenti l'id sotto forma di stringa
	public String selectUser(String user_id) throws Exception {
		String SQL = QueryClass.SQLselectUser(user_id);
		String userID = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 	
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 userID = resultSet.getString("id");		    	 
		     }		
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectMovie");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("userID: " + userID);
		return userID;
	}
	
	//Testata: se non esiste restituisce null e converte minuscole in maiuscole per il pagerank
	public String selectMovie(String uri) throws Exception {
		String SQL = QueryClass.SQLselectMovie(uri);
		String movieURI = null;
		ResultSet resultSet = null;
		Connection connect = null;
		PreparedStatement ps = null;
		try { 
			 connect = dataSource.getConnection();	
			 ps = connect.prepareStatement(SQL);
			 ps.setString(1, uri);
		     resultSet = ps.executeQuery();
		     String query = ps.toString();
		     System.out.println("Query is " + query);
		     while (resultSet.next()) {
		    	 movieURI = resultSet.getString("uri");		    	 
		     }		
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectMovie");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, ps, resultSet); }
		
		//System.out.println("movieURI: " + movieURI);
		return movieURI;
	}


	public void deleteAllScoreByUserFromScoresRecMovies(String user_id) throws Exception{
		String SQL = QueryClass.SQLdeleteAllScoreByUserFromScoresRecMovies(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQL); 
		   } 
		catch (Exception e) { 
			System.err.println("Database Exception - deleteAllScoreByUserFromScoresRecMovies");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
           e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }		
	}
	
	public void deleteAllScoreByUserFromScoresUserMovies(String user_id) throws Exception{
		String SQL = QueryClass.SQLdeleteAllScoreByUserFromScoresUserMovies(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQL); 
		   } 
		catch (Exception e) { 
			System.err.println("Database Exception - deleteAllScoreByUserFromScoresUserMovies");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
           e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }		
	}
	
	public void deleteAllScoreByUserFromScoresUserProperties(String user_id) throws Exception{
		String SQL = QueryClass.SQLdeleteAllScoreByUserFromScoresUserProperties(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQL); 
		   } 
		catch (Exception e) { 
			System.err.println("Database Exception - deleteAllScoreByUserFromScoresUserProperties");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
           e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }		
	}
	
	public TreeMap<Double, String> selectMoviesAndScoreFromScoresRecMovies(String user_id) throws Exception{
		List<String> scoreList = new ArrayList<String>();
		//TreeMap<Double, String> moviesScoreMap = new HashMap<Double, String>();
		TreeMap<Double, String> moviesScoreTreeMap = new TreeMap<Double, String>(Collections.reverseOrder());
		//List<List<String>> moviesScoreList = new ArrayList<List<String>>();
		String SQLmovie = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			
			connect = dataSource.getConnection();
			SQLmovie = QueryClass.SQLselectMoviesAndScoreFromScoresRecMovies(user_id);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SQLmovie);
			while (resultSet.next()){
				scoreList = new ArrayList<String>();
				String movieURI = resultSet.getString("movie_uri");
				String score = resultSet.getString("score");
				if (score != null) {						
					scoreList.add(movieURI);
					scoreList.add(score);
					moviesScoreTreeMap.put(Double.parseDouble(score),movieURI);
				}
			}
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectMoviesAndScoreFromScores");
			System.err.println("" + SQLmovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }	
		
		//System.out.println("" + SQLmovie);
		//System.out.println("moviesScoreTreeMap: " + moviesScoreTreeMap.toString());
		return moviesScoreTreeMap;		
	}
	
	public Set<String> selectRecommenderMovieForExpLOD(String user_id)throws Exception {
		Set<String> recommendationsSet = new HashSet<String>();
		String propertyType = "entity";
		SetSQLPropertyAndColomnString("null", propertyType);
		String SQL = QueryClass.SQLselectMoviesMapAndScoreFromScoresRecMovies(user_id, propertyTypeURI, propertiesTable);		
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SQL);
			while (resultSet.next()){
				String movieURI = resultSet.getString("uri");
				recommendationsSet.add(movieURI);
			}		
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectRecommenderMovieForExpLOD");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }	
		//System.out.println("" + SQL);
		//System.out.println("recommendationsSet size: " + recommendationsSet.size());
		//System.out.println("recommendationsSet: " + recommendationsSet.toString());
		
		//SELECT distinct property_uri as uri, score FROM scores_rec_movies WHERE scores_rec_movies.user_id = 6 AND scores_rec_movies.property_type_uri = "entity" ORDER BY score DESC LIMIT 10;
		//recommendationsSet size: 5
		//recommendationsSet: [http://dbpedia.org/resource/Jumper_(film), http://dbpedia.org/resource/The_Patriot_(2000_film), http://dbpedia.org/resource/Lincoln_(2012_film), http://dbpedia.org/resource/Days_of_Thunder, http://dbpedia.org/resource/The_Rock_(film)]				
		return recommendationsSet;		
	}
	
	public Set<String> selectRatingsPosMoviesForExpLOD(String user_id)throws Exception {
		Set<String> posRatingSet = new TreeSet<>();
		String SQL = QueryClass.SQLselectPosRatingForUserFromRatingsMovies(user_id);	
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     int i = 1;
		     while (resultSet.next()) {
		    	 String movieUri = resultSet.getString("movie_uri");
		    	 //I:11188	http://dbpedia.org/resource/Fight_Club
		    	 String movieString = "I:" + i + "	" + movieUri;
		    	 posRatingSet.add(movieString);
		    	 i++;
		     }
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - selectRatingsPosMoviesForExpLOD");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		//System.out.println("" + SQL);
		//System.out.println("posRatingSet size: " + posRatingSet.size());
		//System.out.println("posRatingSet: " + posRatingSet.toString());
		
		//SELECT movie_uri FROM ratings_movies WHERE `user_id` = 6 AND rating = 1;
		//posRatingSet size: 4
		//posRatingSet: [I:1	http://dbpedia.org/resource/Fight_Club, I:2	http://dbpedia.org/resource/Forrest_Gump, I:3	http://dbpedia.org/resource/Saving_Private_Ryan, I:4	http://dbpedia.org/resource/Top_Gun]

		return posRatingSet;			
		
	}
	public Map<Double, String> selectRecMovieListMap(String user_id) throws Exception {
		TreeMap<String, Double> recMovieScoreTreeMap = new TreeMap<String,Double>();
		Map<Double,String> recMovieScoreMap = new HashMap<Double,String>();
		String propertyType = "entity";
		//SetSQLPropertyAndColomnString("null", propertyType);
		String SQL = null;
		if (propertyType.equals("entity")) {
			SQL = QueryClass.SQLselectMoviesMapAndScoreFromScoresRecMovies(user_id, "entity", "");
		}		
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SQL);
			while (resultSet.next()){
				String propertyURI = resultSet.getString("uri");
				String score = resultSet.getString("score");
				if (score != null) {
					recMovieScoreTreeMap.put(propertyURI, Double.parseDouble(score));
				}				
			}
			for (Entry<String, Double> entry : recMovieScoreTreeMap.entrySet()) {
				String propertyURI = entry.getKey();
				Double score = entry.getValue();
				recMovieScoreMap.put(score, propertyURI);          
			}
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectRecMovieListMap");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }	
		
//		System.out.println("" + SQL);
//		System.out.println("recMovieScoreMap size: " + recMovieScoreMap.size());
//		System.out.println("recMovieScoreMap: " + recMovieScoreMap.toString());
		return recMovieScoreMap;		
	}
	
	//PropertyValueListMapFromPropertyType
	@Deprecated
	public Map<Double, String> selectPropertyValueListMapFromPropertyType(String user_id, String propertyType)throws Exception {
		TreeMap<String, Double> propertyScoreTreeMap = new TreeMap<String,Double>();
		Map<Double,String> propertyScoreMap = new HashMap<Double,String>();
		SetSQLPropertyAndColomnString("null", propertyType);
		String SQL = null;
		if (propertyType.equals("entity")) {
			SQL = QueryClass.SQLselectMoviesMapAndScoreFromScoresRecMovies(user_id, "entity", "");
		}
		else if (propertyType.equals("releaseYear") || propertyType.equals("runtimeRange")) {
			SQL = QueryClass.SQLselectPropertyValueListMapForReleaseYearAndRuntimeRange(user_id, propertyTypeURI, propertiesTable, columnPropertyURI);
		}
		else  {
			PropertyMapping map = this.propertyMap.getMapping(propertyType);
			SQL = QueryClass.SQLselectPropertyValueListMapFromPropertyType(user_id, map.getPropertyTypeId(), map.getTableName());
		}
		
		
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SQL);
			while (resultSet.next()){
				String propertyURI = resultSet.getString("uri");
				String score = resultSet.getString("score");
				if (score != null) {
					propertyScoreTreeMap.put(propertyURI, Double.parseDouble(score));
				}				
			}
			for (Entry<String, Double> entry : propertyScoreTreeMap.entrySet()) {
				String propertyURI = entry.getKey();
				Double score = entry.getValue();
				propertyScoreMap.put(score, propertyURI);          
			}
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectPropertyValueListMapFromPropertyType");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }	
		
//		System.out.println("" + SQL);
//		System.out.println("propertyScoreMap size: " + propertyScoreMap.size());
//		System.out.println("propertyScoreMap: " + propertyScoreMap.toString());
		return propertyScoreMap;		
	}
	
	@Deprecated
	private void SetSQLPropertyAndColomnString(String movieURI, String propertyType){
		SQLproperty = null;
		propertiesTable = null;
		propertiesMoviesTable = null;		
		columnMovieURI = null;
		columnPropertyURI = null;
		propertyTypeURI = null;
		switch (propertyType) {
			case "http://dbpedia.org/ontology/director":		case "director":	case "directors":
				SQLproperty = QueryClass.SQLselectDirectorFromDirectorsMovies(movieURI);
				propertiesTable = "directors";
				propertiesMoviesTable = "directors_movies";
				columnMovieURI = "movie_uri";
				columnPropertyURI = "director_uri";
				propertyTypeURI = "http://dbpedia.org/ontology/director";
				break;
			case "http://dbpedia.org/ontology/producer": 		case "producer":	case "producers":			
				SQLproperty = QueryClass.SQLselectProducerFromProducersMovies(movieURI);
				propertiesTable = "producers";
				propertiesMoviesTable = "producers_movies";
				columnMovieURI = "movie_uri";
				columnPropertyURI = "producer_uri";
				propertyTypeURI = "http://dbpedia.org/ontology/producer";
				break;
			case "http://dbpedia.org/ontology/writer":			case "writer":		case "writers":			
				SQLproperty = QueryClass.SQLselectWriterFromWritersMovies(movieURI);
				propertiesTable = "writers";
				propertiesMoviesTable = "writers_movies";
				columnMovieURI = "movie_uri";
				columnPropertyURI = "writer_uri";
				propertyTypeURI = "http://dbpedia.org/ontology/writer";
				break;
			case "http://dbpedia.org/ontology/starring": 		case "starring": 		case "actor":	case "actors":
				SQLproperty = QueryClass.SQLselectStarringFromStarringMovies(movieURI);
				propertiesTable = "starring";
				propertiesMoviesTable = "starring_movies";
				columnMovieURI = "movie_uri";
				columnPropertyURI = "starring_uri";
				propertyTypeURI = "http://dbpedia.org/ontology/starring";
				break;
			case "http://dbpedia.org/ontology/musicComposer":	case "musicComposer":	case "musiccomposer":	case "musicComposers":	case "music_composer":	case "music":	
				SQLproperty = QueryClass.SQLselectMusicComposerFromMusicComposersMovies(movieURI);
				propertiesTable = "music_composers";
				propertiesMoviesTable = "music_composers_movies";
				columnMovieURI = "movie_uri";
				columnPropertyURI = "music_composer_uri";
				propertyTypeURI = "http://dbpedia.org/ontology/musicComposer";
				break;
			case "http://dbpedia.org/ontology/cinematography":	case "cinematography":	case "cinematographers":
				SQLproperty = QueryClass.SQLselectCinematographyFromCinematographersMovies(movieURI);
				propertiesTable = "cinematographers";
				propertiesMoviesTable = "cinematographers_movies";
				columnMovieURI = "movie_uri";
				columnPropertyURI = "cinematography_uri";
				propertyTypeURI = "http://dbpedia.org/ontology/cinematography";
				break;
			case "http://dbpedia.org/ontology/editing":		case "editing":		case "editings": case "editor":			
				SQLproperty = QueryClass.SQLselectEditingFromEditingsMovies(movieURI);
				propertiesTable = "editings";
				propertiesMoviesTable = "editings_movies";
				columnMovieURI = "movie_uri";
				columnPropertyURI = "editing_uri";
				propertyTypeURI = "http://dbpedia.org/ontology/editing";
				break;
			case "http://dbpedia.org/ontology/distributor":		case "distributor":		case "distributors":		
				SQLproperty = QueryClass.SQLselectDistributorFromDistributorsMovies(movieURI);
				propertiesTable = "distributors";
				propertiesMoviesTable = "distributors_movies";
				columnMovieURI = "movie_uri";
				columnPropertyURI = "distributor_uri";
				propertyTypeURI = "http://dbpedia.org/ontology/distributor";
				break;
			case "http://dbpedia.org/ontology/basedOn":		case "basedOn":		case "basedon": 	case "based_on":
				SQLproperty = QueryClass.SQLselectBasedOnFromBasedOnMovies(movieURI);
				propertiesTable = "based_on";
				propertiesMoviesTable = "based_on_movies";
				columnMovieURI = "movie_uri";
				columnPropertyURI = "based_on_uri";
				propertyTypeURI = "http://dbpedia.org/ontology/basedOn";
				break;
			case "http://purl.org/dc/terms/subject":	 case "category":	case "categories":
				SQLproperty = QueryClass.SQLselectCategoryFromCategoriesMovies(movieURI);
				propertiesTable = "categories";
				propertiesMoviesTable = "categories_movies";
				columnMovieURI = "movie_uri";
				columnPropertyURI = "category_uri";
				propertyTypeURI = "http://purl.org/dc/terms/subject";
				break;
			case "genre":		case "genres":
				SQLproperty = QueryClass.SQLselectAllGenresFromGenresMoviesByMovie(movieURI);
				propertiesTable = "genres";
				propertiesMoviesTable = "genres_movies";
				columnMovieURI = "movie_uri";
				columnPropertyURI = "genre_name";
				propertyTypeURI = "genre";
				break;
			case "title":
				SQLproperty = QueryClass.SQLselectTitleFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "title";
				propertyTypeURI = "null";
				break;
			case "releaseYear":		case "releaseyear": 	case "release_year": 	case "year":
				SQLproperty = QueryClass.SQLselectReleaseYearFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "release_year";
				propertyTypeURI = "null";
				break;
			case "referencePeriod":		case "referenceperiod": 	case "reference_period":
				SQLproperty = QueryClass.SQLselectReferencePeriodFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "reference_period";
				propertyTypeURI = "null";
				break;
			case "http://dbpedia.org/ontology/releaseDate":		case "releaseDate": 	case "releasedate":		case "release_date":		
				SQLproperty = QueryClass.SQLselectReleaseDateFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "release_date";
				propertyTypeURI = "null";//"http://dbpedia.org/ontology/releaseDate";
				break;
			case "runtimeMinutes":	case "runtime_minutes":		case "runtimeminutes": 	case "runtime":	
				SQLproperty = QueryClass.SQLselectRuntimeMinutesFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "runtime_minutes";
				propertyTypeURI = "null";
				break;
			case "runtimeRange":	case "runtimerange":	case "runtime_range":		
				SQLproperty = QueryClass.SQLselectRuntimeRangeFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "runtime_range";
				propertyTypeURI = "null";
				break;
			case "http://dbpedia.org/ontology/runtime":			case "runtime_uri":		case "runtimeURI":		case "runtimeuri":	case "runtimeUri":
				SQLproperty = QueryClass.SQLselectRuntimeURIFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "runtime_uri";
				propertyTypeURI = "null";//"http://dbpedia.org/ontology/runtime";
				break;
			case "plot":
				SQLproperty = QueryClass.SQLselectPlotFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "plot";
				propertyTypeURI = "null";
				break;
			case "language":
				SQLproperty = QueryClass.SQLselectLanguageFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "language";
				propertyTypeURI = "null";
				break;
			case "country":
				SQLproperty = QueryClass.SQLselectCountryFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "country";
				propertyTypeURI = "null";
				break;
			case "awards":
				SQLproperty = QueryClass.SQLselectAwardsFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "awards";
				propertyTypeURI = "null";
				break;
			case "poster":
				SQLproperty = QueryClass.SQLselectPosterFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "poster";
				propertyTypeURI = "null";
				break;
			case "trailer":
				SQLproperty = QueryClass.SQLselectTrailerFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "trailer";
				propertyTypeURI = "null";
				break;
			case "score":
				SQLproperty = QueryClass.SQLselectScoreFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "score";
				propertyTypeURI = "null";
				break;
			case "metascore":
				SQLproperty = QueryClass.SQLselectMetascoreFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "metascore";
				propertyTypeURI = "null";
				break;
			case "imdbRating":	case "imdb_rating":
				SQLproperty = QueryClass.SQLselectImdbRatingFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "imdb_rating";
				propertyTypeURI = "null";
				break;
			case "imdbVotes": 	case "imdb_votes":
				SQLproperty = QueryClass.SQLselectImdbVotesFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "imdb_votes";
				propertyTypeURI = "null";
				break;
			case "imdbId":	case "imdb_id":
				SQLproperty = QueryClass.SQLselectImdbIdFromMovies(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "imdb_id";
				propertyTypeURI = "null";
				break;
			case "entity":
				SQLproperty = QueryClass.SQLselectMovie(movieURI);
				propertiesTable = "movies";
				propertiesMoviesTable = "movies";
				columnMovieURI = "uri";
				columnPropertyURI = "uri";
				propertyTypeURI = "entity";
				break;
			default:
				System.err.println("Allert! " + propertyType + " not found");
				break;
		}		
		

	}
	
	/**
	 * 
	 * @param user_id
	 * @param movieType
	 * Seleziona le proprietï¿½ dei film piaciuti e/o raccomandati
	 * in modo da vedere le proprieta' in comune
	 * @return moviesPropertyList
	 * @throws Exception
	 */
	public List<List<String>> selectMoviesAndPropertyByUser(String user_id, String movieType, String[] propertyTypes) throws Exception{
		List<List<String>> listPropertyList = new ArrayList<List<String>>();
		List<List<String>> moviesPropertyList = new ArrayList<List<String>>();
		String SQLmovie = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			
			connect = dataSource.getConnection();
			switch (movieType) {
			case "like":
				SQLmovie = QueryClass.SQLselectPosRatingForUserFromRatingsMovies(user_id);
				break;
			case "rec":
				SQLmovie = QueryClass.SQLselectMoviesAndScoreFromScoresRecMovies(user_id);
				break;
			default:
				break;
			}
			
			Statement s = connect.createStatement();
			ResultSet rs = s.executeQuery(SQLmovie);
			while (rs.next()){
				String movieURI = rs.getString("movie_uri");
				listPropertyList = this.selectEntityProperties(movieURI, propertyTypes);
				moviesPropertyList.addAll(listPropertyList);
			}
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectMoviesAndPropertyByUser");
			System.err.println("" + SQLmovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }			
		//System.out.println("moviesPropertyList: " + moviesPropertyList.toString());
		return moviesPropertyList;		
	}

	public Map<String, List<String>> selectPropertyTypeFromPropertyValue(String propertyValue) throws Exception{
		List<String> propertyTypeList = new ArrayList<String>();
		Map<String, List<String>> propertyValueToPropertyTypeMap = new HashMap<String, List<String>>();
		String SQL = QueryClass.getPropertyTypes();
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 PreparedStatement ps = connect.prepareStatement(SQL);
			 ps.setString(1, propertyValue);
			 ResultSet rs = ps.executeQuery();
		     propertyTypeList = new ArrayList<String>();	
		     while (rs.next()){		
				String propertyTypeUri = rs.getString("property_type");			
				if (propertyTypeUri != null) {
					propertyTypeList.add(propertyTypeUri);
				}					
		     }
		     if (! propertyTypeList.isEmpty()) {
				propertyValueToPropertyTypeMap.put(propertyValue,propertyTypeList);
		     }			
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectPropertyTypeFromPropertyValue");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("" +SQL);
		//System.out.println("selectPropertyTypeFromPropertyValue: " + propertyValueToPropertyTypeMap);
		//{"http://dbpedia.org/resource/Leonardo_DiCaprio":["http://dbpedia.org/ontology/producer","http://dbpedia.org/ontology/starring","http://dbpedia.org/ontology/writer"]}
		return propertyValueToPropertyTypeMap;
	}
	
	/**
	 * 
	 * @param user_id
	 * @param propertyType
	 * Prendi tutti i propertyValue e il rispettivo score in base al propertyType tra tutti i film raccomandabili
	 * @return movieToPropertyListMap
	 * @throws Exception
	 */
	public Map<String, List<String>> selectRecMovieToPropertyValueAndScoreListFromScoresRecMoviesByUserAndPropertyType(String user_id, String propertyType) throws Exception {
		List<String> propertyList = new ArrayList<String>();
		Map<String, List<String>> movieToPropertyListMap = new HashMap<String, List<String>>();
		String SQLMovie = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 SQLMovie = QueryClass.SQLselectMoviesAndScoreFromScoresRecMovies(user_id);	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQLMovie);		  
		     while (resultSet.next()){				
		    	propertyList = new ArrayList<String>();		    	
				String movieURI = resultSet.getString("movie_uri");
				if (movieURI != null && propertyType != null) {
					SetSQLPropertyAndColomnString(movieURI, propertyType);
					statement = connect.createStatement();
					SQLproperty = QueryClass.SQLselectPropertyValueAndScoreFromScoresRecMovies(user_id, movieURI, propertyTypeURI);
					resultSet = statement.executeQuery(SQLproperty);
					while(resultSet.next()) {
						String propertyURI = resultSet.getString("property_uri");
						double score = resultSet.getDouble("score");			
						if (propertyURI != null) {
				    		 StringBuilder propertyScoreAndURI = new StringBuilder();
				    		 propertyScoreAndURI.append(score);
				    		 propertyScoreAndURI.append(",");
				    		 propertyScoreAndURI.append(propertyURI);
				    		 String propertyURIAndScoreString = propertyScoreAndURI.toString();				    		 
				    		 //System.out.println("propertyURIAndScoreString: " +propertyURIAndScoreString);
							propertyList.add(propertyURIAndScoreString);
							Collections.sort(propertyList);
						}					
					}
					if (propertyList.isEmpty()!= true) {
						movieToPropertyListMap.put(movieURI,propertyList);
					}	
				}			
			}
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectPropertyByUser");
			System.err.println("" +SQLMovie);
			System.err.println("" +SQLproperty);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, statement, resultSet); }
		
//		System.out.println("" +SQLMovie);
//		System.out.println("" +SQLproperty);
//		System.out.println("selectPropertyByUser: " + movieToPropertyListMap);
		return movieToPropertyListMap;		
	}

	public Map<String, List<String>> selectMovieFromScoresByUserAndProperty(String user_id, String propertyType, String propertyURI) throws Exception {
		Map<String, List<String>> propertyToFilmListMap = new HashMap<String, List<String>>();
		List<String> filmList = new ArrayList<String>();
		String SQLproperty = null;
		String SQLmovie = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();
			 SetSQLPropertyAndColomnString(propertyURI, propertyType);
			 SQLproperty = QueryClass.SQLselectMovieFromScoresByUserAndProperty(user_id, propertyTypeURI, propertyURI);
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQLproperty);
		     while (resultSet.next()) {
		    	 String movieURI = resultSet.getString("movie_uri");
		    	 if (movieURI != null) {
					statement = connect.createStatement();
					SQLmovie = QueryClass.SQLselectMovieAndScoreFromScoresByUserAndMovie(user_id, movieURI);
					resultSet = statement.executeQuery(SQLmovie);
					while(resultSet.next()) {
						String movie = resultSet.getString("movie_uri");
						double score = resultSet.getDouble("score");
				    	 if (movie != null) {
				    		 StringBuilder movieAndScore = new StringBuilder();
				    		 movieAndScore.append(score);
				    		 movieAndScore.append(",");
				    		 movieAndScore.append(movie);
				    		 String movieAndScoreString = movieAndScore.toString();
				    		 filmList.add(movieAndScoreString);
				    		 Collections.sort(filmList);
				    	 }	
					}		    		 
		    	 }
		     }
		     if (filmList.isEmpty()!= true) {
				propertyToFilmListMap.put(propertyURI,filmList);
		     }  
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectMovieFromScoresByUserAndProperty");
			System.err.println("" +SQLproperty);
			System.err.println("" +SQLmovie);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		//System.out.println("" +SQLproperty);
		//System.out.println("" +SQLmovie);
		//System.out.println("selectMovieFromScoresByUserAndProperty: " + propertyToFilmListMap.toString());
		return propertyToFilmListMap;
	}
	
	//Testata: se non trova restituisce lista vuota
	@Deprecated
	public List<List<String>> selectPropertyByMovieForClient(String movieURI) throws Exception {
		String[] propertyUrlList = {"http://dbpedia.org/ontology/director",
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
									"releaseYear",
									"referencePeriod",
									"releaseDate",
									"runtimeMinutes",
									"runtimeRange",
									"runtimeUri",
									"plot",
									"language",
									"country",
									"awards",
									"poster",
									"trailer",
									"score",
									"metascore",
									"imdbRating",
									"imdbId",
									"imdbVotes"};			
		
		List<String> propertyList = new ArrayList<String>();
		List<List<String>> listPropertyList = Lists.newArrayList();
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 		
			connect = dataSource.getConnection();				
			for (String propertyURL : propertyUrlList) {								
				String colomnValue = null;
				SetSQLPropertyAndColomnString(movieURI, propertyURL);
				
				statement = connect.createStatement();	
				resultSet = statement.executeQuery(SQLproperty);
				while(resultSet.next()) {
					propertyList = new ArrayList<String>();
					colomnValue = resultSet.getString(columnPropertyURI.toString());
					if (colomnValue != null) {						
						propertyList.add(movieURI);
						propertyList.add(propertyURL);
						propertyList.add(colomnValue);
						listPropertyList.add(propertyList);
						//System.out.println("colomnValue: " + colomnValue.toString());
						//System.out.println("propertyList: " + propertyList.toString());
					}					
				}
			}
		}
			
		catch (Exception e) {
			System.err.println("Database Exception - selectPropertyByMovie");
			System.err.println("" +SQLproperty);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("" +SQLproperty);
		//System.out.println("selectPropertyByMovieForClient listPropertyList: \n" + listPropertyList.toString());
		return listPropertyList;		
	}
	
	public List<List<String>> selectPropertyByMovieForExplanation(String movieURI) throws Exception {
		//regista, attore, produttore, genere,categoria, sceneggiatore, musica, fotografia, basato su..
		String[] propertyUrlList = {"http://dbpedia.org/ontology/director",
									"http://dbpedia.org/ontology/producer",
									"http://dbpedia.org/ontology/writer",
									"http://dbpedia.org/ontology/starring",
									"http://dbpedia.org/ontology/musicComposer",
									"http://dbpedia.org/ontology/cinematography",
									"http://dbpedia.org/ontology/basedOn",
									"http://purl.org/dc/terms/subject", 
									"genre",
									"title"};			
		
		List<String> propertyList = new ArrayList<String>();
		List<List<String>> listPropertyList = Lists.newArrayList();
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			connect = dataSource.getConnection();				
			for (String propertyURL : propertyUrlList) {								
				String colomnValue = null;
				SetSQLPropertyAndColomnString(movieURI, propertyURL);
				
				statement = connect.createStatement();	
				resultSet = statement.executeQuery(SQLproperty);
				while(resultSet.next()) {
					propertyList = new ArrayList<String>();
					colomnValue = resultSet.getString(columnPropertyURI.toString());
					if (colomnValue != null) {						
						propertyList.add(movieURI);
						propertyList.add(propertyURL);
						propertyList.add(colomnValue);
						listPropertyList.add(propertyList);
						//System.out.println("colomnValue: " + colomnValue.toString());
						//System.out.println("propertyList: " + propertyList.toString());
					}					
				}
			}
		}
			
		catch (Exception e) {
			System.err.println("Database Exception - selectPropertyByMovie");
			System.err.println("" +SQLproperty);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("" +SQLproperty);
		//System.out.println("selectPropertyByMovieForClient listPropertyList: \n" + listPropertyList.toString());
		return listPropertyList;		
	}
	
	public List<List<String>> selectPosPropertyListForUserFromRatingsProperties(String user_id) throws Exception {
		String SQL = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		List<String> propertyList = new ArrayList<String>();
		List<List<String>> listPropertyList = Lists.newArrayList();		
		try {
			  		
			 connect = dataSource.getConnection();	
			 SQL = QueryClass.SQLselectPosNegRatingForUserFromRatingsProperties(user_id);	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 propertyList = new ArrayList<String>();
		    	 int rating = resultSet.getInt("rating");
		    	 switch (rating) {
					case 1:
						String PropertyURI = resultSet.getString("property_uri");
						String PropertyTypeURI = resultSet.getString("property_type_uri");
						propertyList.add(PropertyURI);
						propertyList.add(PropertyTypeURI);
						listPropertyList.add(propertyList);
						break;
					default:
						break;
				}
		     }		     
		} 			
		catch (Exception e) {
			System.err.println("Database Exception - selectPosPropertyListForUserFromRatingsProperties");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("selectPosPropertyListForUserFromRatingsProperties listPropertyList: \n" + listPropertyList.toString());
		//[[http://dbpedia.org/resource/Ron_Howard, http://dbpedia.org/ontology/director], [http://dbpedia.org/resource/Tom_Hanks, http://dbpedia.org/ontology/producer], [http://dbpedia.org/resource/Tom_Hanks, http://dbpedia.org/ontology/starring]]
		return listPropertyList;		
	}
	
	public List<List<String>> selectNegPropertyListForUserFromRatingsProperties(String user_id) throws Exception {
		String SQL = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;	
		List<String> propertyList = new ArrayList<String>();
		List<List<String>> listPropertyList = Lists.newArrayList();		
		try {
			  		
			 connect = dataSource.getConnection();	
			 SQL = QueryClass.SQLselectPosNegRatingForUserFromRatingsProperties(user_id);	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 propertyList = new ArrayList<String>();
		    	 int rating = resultSet.getInt("rating");
		    	 switch (rating) {
					case 0:
						String PropertyURI = resultSet.getString("property_uri");
						String PropertyTypeURI = resultSet.getString("property_type_uri");
						propertyList.add(PropertyURI);
						propertyList.add(PropertyTypeURI);
						listPropertyList.add(propertyList);
						break;
					default:
						break;
				}
		     }		     
		} 			
		catch (Exception e) {
			System.err.println("Database Exception - selectNegPropertyListForUserFromRatingsProperties");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("selectNegPropertyListForUserFromRatingsProperties listPropertyList: \n" + listPropertyList.toString());
		//[[http://dbpedia.org/resource/Ron_Howard, http://dbpedia.org/ontology/director], [http://dbpedia.org/resource/Tom_Hanks, http://dbpedia.org/ontology/producer], [http://dbpedia.org/resource/Tom_Hanks, http://dbpedia.org/ontology/starring]]
		return listPropertyList;		
	}
	
	public String[] selectAcceptRecMovieToControlling(String user_id)throws Exception{
		String SQL = QueryClass.SQLselectAcceptRecMovieToControlling(user_id);
		int numberRatedItems = selectNumberOfRatedMoviesAndSkippedByUser(user_id);
		String[] movieURI = new String[numberRatedItems];
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {							
			connect = dataSource.getConnection();
			statement = connect.createStatement();
		    resultSet = statement.executeQuery(SQL);
		    int i = 0;
		    while (resultSet.next()) {
		    	 movieURI[i] = resultSet.getString("movie_uri");
		    	 i++;
		    }
		    System.out.println("Control movieURI: " + Arrays.toString(movieURI));
		}catch (Exception e) {
			System.err.println("Database Exception - selectAcceptRecMovieToControlling");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("movieURI: " + movieURI.toString());
		return movieURI;
	}
	
	public String[] selectAcceptRecMovieToSkipping(String user_id)throws Exception{
		String SQL = QueryClass.SQLselectAcceptRecMovieToSkipping(user_id);
		int numberRatedItems = selectNumberOfRatedMoviesSkippedByUser(user_id);
		String[] movieURI = new String[numberRatedItems];
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {							
			connect = dataSource.getConnection();
			statement = connect.createStatement();
		    resultSet = statement.executeQuery(SQL);
		    int i = 0;
		    while (resultSet.next()) {
		    	 movieURI[i] = resultSet.getString("movie_uri");
		    	 i++;
		    }
		    System.out.println("Skipped movieURI: " + Arrays.toString(movieURI));
		}catch (Exception e) {
			System.err.println("Database Exception - selectAcceptRecMovieToSkipping");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("movieURI: " + movieURI.toString());
		return movieURI;
	}
	
	public String selectAcceptRecMovieToRatingByUser(String user_id)throws Exception{
		String SQL = QueryClass.SQLselectAcceptRecMovieToRatingByUser(user_id);
		String movieURI = "null";
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {							
			connect = dataSource.getConnection();
			statement = connect.createStatement();
		    resultSet = statement.executeQuery(SQL);			  
		    while (resultSet.next()) {
		    	 movieURI = resultSet.getString("movie_uri");
		    }			    		
		}catch (Exception e) {
			System.err.println("Database Exception - selectAcceptRecMovieToRatingByUser");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("movieURI: " + movieURI.toString());
		return movieURI;
	}
	
	
	public String selectMovieToRatingByUser(String user_id)throws Exception{
		String SQL = null, movieURIpopular = "null", movieURI = "null";
		String[] popularFilmArray = Configuration.getDefaultConfiguration().getPopularEntities();
		int i = 0;
		int size = popularFilmArray.length;
		boolean notFound = true;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			while ( i < size && notFound == true) {
				Random random = new Random();
				int index = random.nextInt(popularFilmArray.length);
				//System.out.println("size: " + size);
				//System.out.println("index: " + index);
				movieURIpopular = popularFilmArray[index];							
				connect = dataSource.getConnection();
				SQL = QueryClass.SQLselectMovieToRatingByUserEntities(user_id, movieURIpopular);
				statement = connect.createStatement();
			    resultSet = statement.executeQuery(SQL);			  
			    while (resultSet.next()) {
			    	 movieURI = resultSet.getString("uri");
			    	// System.out.println(movieURI);
			    }
			    if(movieURI.equals(movieURIpopular) == true){
			    	notFound = true;
			    	movieURIpopular = "null";
			    }
			    else{//Found!
					notFound = false;
					}
	            i++;
	        }			
		}catch (Exception e) {
			System.err.println("Database Exception - selectMovieToRatingByUser");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }

		//System.out.println("movieURIpopular: " + movieURIpopular.toString());
		return movieURIpopular;
	}
	public String selectMovieToRatingByRandPopularUser(String user_id)throws Exception{
		String SQL = QueryClass.SQLselectMovieToRatingByRandPopularUser();
		String movieURI = "null";
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {							
			connect = dataSource.getConnection();
			statement = connect.createStatement();
		    resultSet = statement.executeQuery(SQL);			  
		    while (resultSet.next()) {
		    	 movieURI = resultSet.getString("movie_uri");
		    }
		    movieURI = selectMovieLabelByPopxVarUser(movieURI);
		}catch (Exception e) {
			System.err.println("Database Exception - selectMovieToRatingByRandPopularUser");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("movieURI: " + movieURI.toString());
		return movieURI;
	}
	
	public String selectMovieToRatingByPopxVarUser(String user_id) throws Exception{
		String SQL = QueryClass.SQLselectMovieToRatingByPopxVarUser();
		String movieURI = "null";
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {							
			connect = dataSource.getConnection();
			statement = connect.createStatement();
		    resultSet = statement.executeQuery(SQL);			  
		    while (resultSet.next()) {
		    	 movieURI = resultSet.getString("title");
		    }
		}catch (Exception e) {
			System.err.println("Database Exception - selectMovieToRatingByPopxVarUser");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("movieURI: " + movieURI.toString());
		return movieURI;
	}

	public String selectMovieLabelByPopxVarUser(String label) throws Exception{
		String movieURI=label.replaceAll("\\s+[(]+.+[)]", "");
	    String SQL = QueryClass.getURIMLQuery(movieURI);
	    
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {							
			connect = dataSource.getConnection();
			statement = connect.createStatement();
		    resultSet = statement.executeQuery(SQL);	
		    int end=0;
		    while (resultSet.next()) {
		    	movieURI = resultSet.getString("uri");
		    	
		    }
		    if(movieURI.equals(null)!=true) {
		    	try {
		    	while(Pattern.matches("^Q[0-9]*",movieURI)==false && end<1) {
		    		movieURI=movieURI.replaceAll(movieURI, "The "+movieURI);
		    		try {
		    			movieURI=getEntityURI(movieURI);
		    		}
		    		catch(Exception e){
		    			System.out.println("Film non presente");
		    			break;
		    		}
		    		end++;
		    	}
		    	}
		    	catch(Exception e) {
		    		System.out.println("Film non presente");
		    	}
		    }
		}catch (Exception e) {
			System.err.println("Database Exception - selectMovieLabelByPopxVarUser");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("movieURI: " + movieURI.toString());
		return movieURI;
	}
	
	public void deleteAllPropertyRatedByUser(String user_id) throws Exception{
		String SQL = QueryClass.SQLdeleteAllPropertyRatedByUser(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);
			     
		} 
		catch (Exception e) { 
           System.err.println("Database Exception - deleteAllPropertyRatedByUser");
		   System.err.println("" +SQL);
           System.err.println(e.getMessage());
           e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet);}
	}
	
	public void deletePropertyRatedByUser(String user_id, String uri) throws Exception {
		String SQL = QueryClass.SQLdeletePropertyRatedByUser(user_id, uri);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);
		} 
		catch (Exception e) { 
           System.err.println("Database Exception - deletePropertyRatedByUser");
		   System.err.println("" +SQL);
           System.err.println(e.getMessage());
           e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet);}
	}
	
	public void deleteAllMovieRatedByUser(String user_id) throws Exception{
		String SQL = QueryClass.SQLdeleteAllMovieRatedByUser(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 	
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);    
		} 
		catch (Exception e) { 
           System.err.println("Database Exception - deleteAllMovieRatedByUser");
		   System.err.println("" +SQL);
           System.err.println(e.getMessage());
           e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
	
	public void deleteMovieRatedByUser(String user_id, String uri) throws Exception{
		String SQL = QueryClass.SQLdeleteMovieRatedByUser(user_id, uri);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);
		} 
		catch (Exception e) { 
           System.err.println("Database Exception - deleteMovieRatedByUser");
		   System.err.println("" +SQL);
           System.err.println(e.getMessage());
           e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}
	
	public void deleteAllChatMessageByUser(String user_id) throws Exception{
		String SQL = QueryClass.SQLdeleteAllChatMessageByUser(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);
		} 
		catch (Exception e) { 
           System.err.println("Database Exception - deleteAllChatMessageByUser");
		   System.err.println("" +SQL);
           System.err.println(e.getMessage());
           e.printStackTrace();
		} 
		finally { 
			close(connect, statement, resultSet);
		}
	}

	public void updateUserDetailToNullByUser(String user_id) throws Exception{
		String SQL = QueryClass.SQLupdateUserDetailToNullByUser(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);
		} 
		catch (Exception e) { 
           System.err.println("Database Exception - deleteAllUserDetailByUser");
		   System.err.println("" +SQL);
           System.err.println(e.getMessage());
           e.printStackTrace();
		} 
		finally { 
			close(connect, statement, resultSet);
		}
	}
	
	public void updateNumberPagerankCicleByUser(String user_id,int pagerank_cicle) throws Exception{
		String SQL = QueryClass.SQLupdateNumberPagerankCicleByUser(user_id, pagerank_cicle);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);	     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - updateNumberPagarankCicleByUser");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}

	
	public void updateNumberRecommendationListByUser(String user_id,int number_recommendation_list) throws Exception{
		String SQL = QueryClass.SQLupdateNumberRecommendationListByUser(user_id, number_recommendation_list);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
			 statement.executeUpdate(SQL);	     
		} 
		catch (Exception e) { 
	       System.err.println("Database Exception - updateNumberRecommendationListByUser");
		   System.err.println("" +SQL);
	       System.err.println(e.getMessage());
	       e.printStackTrace();
		} 
		finally {close(connect, statement, resultSet);}
	}



	//Testata: aggiorna solo se presente id
	public void updateNumberRatedMoviesByUser(String user_id) throws Exception {
		String SQL = QueryClass.SQLupdateNumberRatedMoviesByUser(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQL);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - updateNumberRatedMoviesByUser");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }		
	}
	
	//Testata: aggiorna solo se presente id
	public void updateNumberRatedPropertiesByUser(String user_id) throws Exception {
		String SQL = QueryClass.SQLupdateNumberRatedPropertiesByUser(user_id);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQL);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - updateNumberRatedPropertiesByUser");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
	}
		
	public void updateLastChange(String user_id,String lastChange) throws Exception{
		String SQL = QueryClass.SQLupdateLastChange(user_id,lastChange);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();	
			 statement = connect.createStatement();
		     statement.executeUpdate(SQL);
		   } 
		catch (Exception e) {
			System.err.println("Database Exception - updateLastChange");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		finally { close(connect, statement, resultSet); }
		//System.out.println("user_id: " + user_id + " - lastChange: " + lastChange);
		
	}
	
	public int selectNumberOfBotNameByBotName(String botName) throws Exception {
		String SQL = QueryClass.SQLselectNumberOfBotNameByBotName(botName);
		int numberBotName = 0;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 numberBotName = resultSet.getInt("number_bot_name");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberOfBotNameByBotName");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("selectNumberOfRatedMoviesByUser - user_id: " + user_id + " - numberRatedMovies: " + numberRatedMovies);
		return numberBotName;
	}
	
	public int selectNumberOfRatedMoviesByUser(String user_id) throws Exception {
		String SQL = QueryClass.SQLselectNumberOfRatedMoviesByUser(user_id);
		int numberRatedMovies = 0;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 numberRatedMovies = resultSet.getInt("rated_movies");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberOfRatedMovieByUser");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("selectNumberOfRatedMoviesByUser - user_id: " + user_id + " - numberRatedMovies: " + numberRatedMovies);
		return numberRatedMovies;	
	}
	
	public int selectNumberOfRatedMoviesAndSkippedByUser(String user_id) throws Exception {
		String SQL = QueryClass.SQLselectNumberOfRatedMoviesAndSkippedByUser(user_id);
		int numberRatedMovies = 0;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 numberRatedMovies = resultSet.getInt("rated_movies");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberOfRatedMovieSkippedByUser");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("selectNumberOfRatedMoviesByUser - user_id: " + user_id + " - numberRatedMovies: " + numberRatedMovies);
		return numberRatedMovies;	
	}
	
	public int selectNumberOfRatedMoviesSkippedByUser(String user_id) throws Exception {
		String SQL = QueryClass.SQLselectNumberOfRatedMoviesSkippedByUser(user_id);
		int numberRatedMovies = 0;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 numberRatedMovies = resultSet.getInt("rated_movies");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberOfRatedMovieSkippedByUser");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("selectNumberOfRatedMoviesByUser - user_id: " + user_id + " - numberRatedMovies: " + numberRatedMovies);
		return numberRatedMovies;	
	}
	
	public double selectMovieLensMovie(String label) throws Exception {
		String SQL = QueryClass.SQLselectMovieLensReference(label);
		double avg = 0;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 avg = resultSet.getDouble("rating");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - SQLselectMovieLensReference");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		return avg;	
	}
	
	
	public int selectNumberOfRatedPropertiesByUser(String user_id) throws Exception {
		String SQL = QueryClass.SQLselectNumberOfRatedPropertiesByUser(user_id);
		int numberRatedProperties = 0;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 numberRatedProperties = resultSet.getInt("rated_properties");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberOfRatedPropertiesByUser");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("selectNumberOfRatedPropertiesByUser - user_id: " + user_id + " - numberRatedProperties: " + numberRatedProperties);
		return numberRatedProperties;	
	}
	
	public int selectNumberOfRatedPropertiesByUserWithoutDuplicates(String user_id) throws Exception {
		String SQL = QueryClass.SQLgetNumberRatedPropertiesWithoutDuplicates(user_id);
		int numberRatedProperties = 0;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 numberRatedProperties = resultSet.getInt("count(distinct property_uri)");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberOfRatedPropertiesByUser");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("selectNumberOfRatedPropertiesByUser - user_id: " + user_id + " - numberRatedProperties: " + numberRatedProperties);
		return numberRatedProperties;	
	}
	
	public int selectNumberOfPagerankCicleByUser(String user_id) throws Exception {
		String SQL = QueryClass.SQLselectNumberOfPagerankCicleByUser(user_id);
		int numberPagerankCicle = 0;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 numberPagerankCicle = resultSet.getInt("pagerank_cicle");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberOfPagerankCicleByUser");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		//System.out.println("user_id: " + user_id + " - numberPagerankCicle: " + numberPagerankCicle);
		return numberPagerankCicle;	
	}
	
	public int selectNumberRatedRecMovieByUserAndRecList(String user_id, int numberRecommendationList) throws Exception {
		String SQL = QueryClass.SQLselectNumberRatedRecMovieByUserAndRecListByUser(user_id, numberRecommendationList);
		int numberRatedRecMovie = 0;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 numberRatedRecMovie = resultSet.getInt("number_rated_rec_movie");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberRatedRecMovieByUserAndRecList");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("user_id: " + user_id + " - numberRecommendationList: " + numberRecommendationList);
		return numberRatedRecMovie;	
	}
	
	public int selectNumberRefineFromRecMovieListByUserAndRecList(String user_id, int numberRecommendationList) throws Exception {
		String SQL = QueryClass.SQLselectNumberRefineFromRecMovieListByUserAndRecList(user_id, numberRecommendationList);
		int numberRefineRecMovie = 0;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 numberRefineRecMovie = resultSet.getInt("number_refine_rec_movie");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberRefineFromRecMovieListByUserAndRecList");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		return numberRefineRecMovie;	
	}
	
	public int selectNumberRecommendationListByUser(String user_id) throws Exception {
		String SQL = QueryClass.SQLselectNumberRecommendationListByUserByUser(user_id);
		int numberRecommendationList = 0;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 	
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 numberRecommendationList = resultSet.getInt("number_recommendation_list");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberRecommendationListByUser");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		//System.out.println("user_id: " + user_id + " - numberPagerankCicle: " + numberPagerankCicle);
		return numberRecommendationList;	
	}

	public String selectLastChangeByUser(String user_id) throws Exception {
		String SQL = QueryClass.SQLselectLastChangeByUser(user_id);
		String lastChange = "null";
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 lastChange = resultSet.getString("last_change");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectLastChangeByUser");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		//System.out.println("user_id: " + user_id + " - selectLastChangeByUser: " + lastChange);
		return lastChange;	
	}
	
	public Set<String> selectBotConfigurationSetByUser(String user_id)throws Exception {
		Set<String> botNameSet = new HashSet<String>();
		String SQL = QueryClass.SQLselectBotConfigurationSetByUser(user_id);		
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SQL);
			while (resultSet.next()){
				String botName = resultSet.getString("bot_name");
				botNameSet.add(botName);
			}		
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectBotConfigurationSetByUser");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }	

		return botNameSet;		
	}
	
	public int selectLastBotTimestampByUser(String user_id) throws Exception {
		String SQL = QueryClass.SQLselectLastBotTimestamp(user_id);
		int botTimestamp = 0;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try { 
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 botTimestamp = resultSet.getInt("bot_timestamp");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectLastBotTimestampByUser");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("user_id: " + user_id + " - selectLastBotTimestampByUser: " + botTimestamp);
		return botTimestamp;	
	}
	

	public int selectBotTimestampFromRatingsRecMovies(String user_id, int number_recommendation_list, int position) throws Exception {
		String SQL = QueryClass.SQLselectBotTimestampFromRatingsRecMovies(user_id, number_recommendation_list, position);
		int botTimestamp = 0;
		ResultSet resultSet = null;
		Connection connect = null;
		Statement statement = null;
		try { 
			 connect = dataSource.getConnection();		
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 botTimestamp = resultSet.getInt("bot_timestamp");		    	 
		     }			    
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectBotTimestampFromRatingsRecMovies");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
		
		//System.out.println("user_id: " + user_id + " - selectBotTimestampFromRatingsRecMovies: " + botTimestamp);
		return botTimestamp;	
	}
	
	@Deprecated
	public String selectNameFromUriInVertexPosterSelection(String uri) throws Exception{
		String name = null;
		String SQL = QueryClass.SQLselectNameFromUriInVertexPosterSelection(uri);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SQL);
			while (resultSet.next()){
				//String uriString = resultSet.getString("uri");
				name = resultSet.getString("name");
			}
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectNameFromUriInVertexPosterSelection");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }	
		//System.out.println("" + SQL);
		//System.out.println("vertexUriSet: " + vertexUriSet.toString());
		
		return name;		
	}
	
	@Deprecated
	public TreeMap<String, String> selectUriAndNameFromVertexPosterSelection() throws Exception{
		TreeMap<String, String> vertexUriAndNameSet = new TreeMap<String, String>();
		String SQL = QueryClass.SQLselectUriAndNameFromVertexPosterSelection();
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SQL);
			while (resultSet.next()){
				String uriString = resultSet.getString("uri");
				String nameString = resultSet.getString("name");
				vertexUriAndNameSet.put(uriString, nameString);
			}
		} 
		catch (Exception e) {
			System.err.println("Database Exception - SQLselectUriAndNameFromVertexPosterSelection");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }	
		//System.out.println("" + SQL);
		//System.out.println("vertexUriSet: " + vertexUriSet.toString());
		
		return vertexUriAndNameSet;		
	}
	
	public TreeMap<String, String> selectUriAndNameFromVertexTrailerSelection() throws Exception{
		TreeMap<String, String> vertexUriAndNameSet = new TreeMap<String, String>();
		String SQL = QueryClass.SQLselectUriFromVertexTrailerSelection();
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SQL);
			while (resultSet.next()){
				String uriString = resultSet.getString("uri");
				String nameString = resultSet.getString("name");
				vertexUriAndNameSet.put(uriString, nameString);
			}
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectUriAndNameFromVertexTrailerSelection");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }	
		//System.out.println("" + SQL);
		//System.out.println("vertexUriSet: " + vertexUriSet.toString());
		
		return vertexUriAndNameSet;		
	}
	
	public TreeMap<String, String> searchUriAndNameFromVertexTrailerSelectionByText(String text) throws Exception{
		TreeMap<String, String> vertexUriAndNameSet = new TreeMap<String, String>();
		String SQL = QueryClass.SQLsearchUriAndNameFromVertexTrailerSelectionByText(text);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SQL);
			while (resultSet.next()){
				String uriString = resultSet.getString("uri");
				String nameString = resultSet.getString("name");
				vertexUriAndNameSet.put(uriString, nameString);
			}
		} 
		catch (Exception e) {
			System.err.println("Database Exception - searchUriAndNameFromVertexTrailerSelectionByText");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }	
		//System.out.println("" + SQL);
		//System.out.println("vertexUriAndNameSet: " + vertexUriAndNameSet.toString());
		
		return vertexUriAndNameSet;		
	}
	
	@Deprecated
	public Set<String> selectUriFromVertexTrailerSelection() throws Exception{
		Set<String> vertexUriSet = new TreeSet<>();
		String SQL = QueryClass.SQLselectUriFromVertexTrailerSelection();
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SQL);
			while (resultSet.next()){
				String uriString = resultSet.getString("uri");
				vertexUriSet.add(uriString);
			}
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectUriFromVertexTrailerSelection");
			System.err.println("" + SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }	
		//System.out.println("" + SQL);
		//System.out.println("vertexUriSet: " + vertexUriSet.toString());
		
		return vertexUriSet;		
	}
	
	/* Nuove query del sistema MovieRecSysService */
	
	public List<Pair<Double,String>> getEntitiesToRecommend(String userID) {
		List<Pair<Double, String>> result = new ArrayList<>();
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			PreparedStatement ps = connect.prepareStatement(QueryClass.getEntitiesToRecommendQuery());
			ps.setString(1, userID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String entityURI = rs.getString("movie_uri");
				double score = rs.getDouble("score");
				result.add(new Pair<>(score, entityURI));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, statement, resultSet); }	
		
		return result;
	}
	
	public String getDialogState(String userID) {
		String sql = QueryClass.getReadDialogStateQuery(userID);
		String state = null;
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()){
				state = resultSet.getString("state");
			}
		} 
		catch (Exception e) {
			System.err.println("Database Exception - getDialogState");
			System.err.println("" + sql);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }	
		return state;
	}
	
	public void setDialogState(String userID, String state) {
		String sql = QueryClass.getAddDialogStateQuery(userID, state);
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			java.sql.PreparedStatement st = connect.prepareStatement(sql);
			st.setString(1, userID + "");
			st.setString(2, state);
			st.executeUpdate();
		} 
		catch (Exception e) {
			System.err.println("Database Exception - setDialogState");
			System.err.println("" + sql);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(connect, statement, resultSet); }
	}
	
	public List<List<String>> selectEntityProperties(String movieURI, String[] propertyUrlList) throws Exception {
		List<String> propertyList = new ArrayList<String>();
		List<List<String>> listPropertyList = Lists.newArrayList();
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try { 
			connect = dataSource.getConnection();				
			for (String propertyURL : propertyUrlList) {								
				String columnValue = null;
				PropertyMapping map = this.propertyMap.getMapping(propertyURL);
				if (map == null) {
					System.out.println("Property type not found: " + propertyURL);
				}
				ps = connect.prepareStatement(QueryClass.getPropertiesForMovieQuery(map.getTableName()));
				ps.setString(1, map.getPropertyTypeId());
				ps.setString(2, movieURI);
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					propertyList = new ArrayList<String>();
					columnValue = rs.getString(map.getColumnName());
					if (columnValue != null) {						
						propertyList.add(movieURI);
						propertyList.add(propertyURL);
						propertyList.add(columnValue);
						listPropertyList.add(propertyList);
						//System.out.println("colomnValue: " + colomnValue.toString());
						//System.out.println("propertyList: " + propertyList.toString());
					}					
				}
			}
		}
			
		catch (Exception e) {
			System.err.println("Database Exception - selectPropertyByMovie. movieURI = " + movieURI + ", propertyUrlList = " + Arrays.toString(propertyUrlList));
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(connect, ps, resultSet); }
		
		//System.out.println("" +SQLproperty);
		//System.out.println("selectPropertyByMovieForClient listPropertyList: \n" + listPropertyList.toString());
		return listPropertyList;		
	}
	
	public String getEntityLabel(String uri) {
		String result = null;
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getEntityLabelQuery(uri));
			ps.setString(1, uri);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString("label");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	finally { close(connect, ps, resultSet); }	

		return result;
	}
	
	public String getEntityMLLabel(String uri) {
		String result = null;
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getEntityMLLabelQuery(uri));
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString("label");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	finally { close(connect, ps, resultSet); }	

		return result;
	}
	public String getPropertyLabel(String uri) {
		String result = null;
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getPropertyLabelQuery());
			ps.setString(1, uri);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString("label");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }	

		return result;
	}
	public String getPropertyFromMovies(String movie_uri) {
	String SQL = QueryClass.getPropertyFromMovieQuery(movie_uri);
	String movieURI = "null";
	Connection connect = null;
	Statement statement = null;
	ResultSet resultSet = null;
	try {							
		connect = dataSource.getConnection();
		statement = connect.createStatement();
	    resultSet = statement.executeQuery(SQL);			  
	    while (resultSet.next()) {
	    	 movieURI = resultSet.getString("property_id");
	    }			    		
	}catch (Exception e) {
		System.err.println("Database Exception - getPropertyFromMovie");
		System.err.println("" + SQL);
		System.err.println(e.getMessage());
		e.printStackTrace();
	} 
	finally { close(connect, statement, resultSet); }
	
	//System.out.println("movieURI: " + movieURI.toString());
	return movieURI;
	}
	
	public String getPropertyFromMovie(String movie_uri) {
		String result = null;
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getPropertyFromMovieQuery(movie_uri));
			ps.setString(1, movie_uri);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString("property_id");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }	

		return result;
	}
	
	public void insertMessageInLog(String messageID, String userID, String message, long timestampStart, long timestampEnd, 
			String intent, String contexts, List<String> recognized, List<String> events,
			int pagerankCicle, int numRecommendationList, String interactionType, String recommendedEntity) {
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		StringJoiner sj = new StringJoiner(", ");
		for (String object: recognized) {
			sj.add(object);
		}

		String recognizedString = sj.toString();
		sj = new StringJoiner(", ");
		for (String event: events) {
			sj.add(event);
		}
		String eventsString = sj.toString();
		
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getInsertMessageInLogQuery());
			ps.setString(1, messageID);
			ps.setString(2, userID);
			ps.setString(3, message);
			ps.setTimestamp(4, new Timestamp(timestampStart));
			if (timestampEnd <= 0) {
				ps.setTimestamp(5, null);
			} else {
				ps.setTimestamp(5, new Timestamp(timestampEnd));
			}
			ps.setString(6, intent);
			ps.setString(7, contexts);
			ps.setString(8, recognizedString);
			ps.setString(9, eventsString);
			ps.setInt(10, pagerankCicle);
			ps.setInt(11, numRecommendationList);
			ps.setString(12, interactionType);
			ps.setString(13,  recommendedEntity);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }	
	}
	
	public void updateMessageLog(String userID, String messageID, long timestampEnd) {
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getUpdateMessageLogQuery(), Statement.RETURN_GENERATED_KEYS);
			ps.setTimestamp(1, new Timestamp(timestampEnd));
			ps.setString(2, messageID);
			ps.setString(3, userID);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }	
	}

	public void updateMessageLog(String userID, String messageID, long timestampEnd, int pagerankCycle, int numberRecommendationList) {
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getUpdateMessageLogWithPRCycleQuery(), Statement.RETURN_GENERATED_KEYS);
			ps.setTimestamp(1, new Timestamp(timestampEnd));
			ps.setInt(2, pagerankCycle);
			ps.setInt(3, numberRecommendationList);
			ps.setString(4, messageID);
			ps.setString(5, userID);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }	
	}
	
	public void updateMessageLog(String userID, String messageID, long timestampEnd, int pagerankCycle, int numberRecommendationList, List<String> events) {
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getEventsFromMessageQuery());
			ps.setString(1, messageID);
			ps.setString(2, userID);
			resultSet = ps.executeQuery();
			List<String> oldEvents = new ArrayList<>();
			//Aggiungo gli eventi già esistenti nella lista
			if (resultSet.next()) {
				String eventStr = resultSet.getString("events");
				String[] oldEventArray = eventStr.split(",");
				for (String e: oldEventArray) {
					events.add(e.trim());
				}
			}
			StringJoiner sj = new StringJoiner(", ");
			for (String event: events) {
				sj.add(event);
			}
			ps.close();
			
			ps = connect.prepareStatement(QueryClass.getUpdateMessageLogWithPRCycleAndEventsQuery(), Statement.RETURN_GENERATED_KEYS);
			ps.setTimestamp(1, new Timestamp(timestampEnd));
			ps.setInt(2, pagerankCycle);
			ps.setInt(3, numberRecommendationList);
			ps.setString(4, sj.toString());
			ps.setString(5, messageID);
			ps.setString(6, userID);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }	
	}
	
	public List<LogMessage> getLogMessages(long start) {
		List<LogMessage> messages = new ArrayList<>();
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getLogMessagesQuery());
			ps.setTimestamp(1, new Timestamp(start));
			resultSet = ps.executeQuery();
			while (resultSet.next()) {
				String messageID = resultSet.getString("message_id");
				String userID = resultSet.getString("user_id");
				String message = resultSet.getString("message");
				long timestampStart = resultSet.getTimestamp("timestamp_start").getTime();
				long timestampEnd = resultSet.getTimestamp("timestamp_end").getTime();
				String intent = resultSet.getString("intent");
				String contexts = resultSet.getString("contexts");
				boolean toCheck = resultSet.getBoolean("to_check");
				String recognizedObjects = resultSet.getString("recognized");
				String events = resultSet.getString("events");
				int pagerankCycle = resultSet.getInt("pagerank_cicle");
				int numberRecommendationList = resultSet.getInt("number_recommendation_list");
				String interactionType = resultSet.getString("interaction_type");
				messages.add(new LogMessage(messageID,
						userID,
						message, timestampStart,
						timestampEnd, 
						intent,
						contexts, toCheck, 
						recognizedObjects,
						events, 
						pagerankCycle, 
						numberRecommendationList, 
						interactionType
						)
					);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }	
		
		return messages;
	}
	
	public List<RecEntityRating> getRecEntityRatings(String userID) {
		List<RecEntityRating> result = new ArrayList<>();
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getRecEntityRatingsQuery());
			ps.setString(1, userID);

			resultSet = ps.executeQuery();
			while (resultSet.next()) {
				String entityURI = resultSet.getString("movie_uri");
				int numberRecommendationList = resultSet.getInt("number_recommendation_list");
				boolean like = resultSet.getBoolean("like");
				boolean dislike = resultSet.getBoolean("dislike");
				long ratedAt = resultSet.getTimestamp("rated_at").getTime();
				int position = resultSet.getInt("position");
				String refine = resultSet.getString("refine");
				int pagerankCycle = resultSet.getInt("pagerank_cicle");
				String refocus = resultSet.getString("refocus");
				String details = resultSet.getString("details");
				String why = resultSet.getString("why"); 
				String botName = resultSet.getString("bot_name");
				String messageID = resultSet.getString("message_id");
				long botTimestamp = resultSet.getLong("bot_timestamp");
				long responseTime = resultSet.getLong("response_time");
				String recommendations = resultSet.getString("recommendations_list");
				
				result.add(
						new RecEntityRating(
								userID, 
								entityURI, 
								numberRecommendationList, 
								like, 
								dislike, 
								ratedAt, 
								position, 
								pagerankCycle, 
								refine != null, 
								refocus != null, 
								details != null, 
								why != null, 
								botName, 
								messageID, 
								botTimestamp, 
								responseTime, 
								recommendations)
						);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }
		
		return result;
	}
	
	public List<RecEntityRating> getCurrentRecEntityRatings(String userID, int numRecList) {
		List<RecEntityRating> result = new ArrayList<>();
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getCurrentRecEntityRatingsQuery());
			ps.setString(1, userID);
			ps.setInt(2, numRecList);

			resultSet = ps.executeQuery();
			while (resultSet.next()) {
				String entityURI = resultSet.getString("movie_uri");
				int numberRecommendationList = resultSet.getInt("number_recommendation_list");
				boolean like = resultSet.getBoolean("like");
				boolean dislike = resultSet.getBoolean("dislike");
				long ratedAt = resultSet.getTimestamp("rated_at").getTime();
				int position = resultSet.getInt("position");
				String refine = resultSet.getString("refine");
				int pagerankCycle = resultSet.getInt("pagerank_cicle");
				String refocus = resultSet.getString("refocus");
				String details = resultSet.getString("details");
				String why = resultSet.getString("why"); 
				String botName = resultSet.getString("bot_name");
				String messageID = resultSet.getString("message_id");
				long botTimestamp = resultSet.getLong("bot_timestamp");
				long responseTime = resultSet.getLong("response_time");
				String recommendations = resultSet.getString("recommendations_list");
				
				result.add(
						new RecEntityRating(
								userID, 
								entityURI, 
								numberRecommendationList, 
								like, 
								dislike, 
								ratedAt, 
								position, 
								pagerankCycle, 
								refine != null, 
								refocus != null, 
								details != null, 
								why != null, 
								botName, 
								messageID, 
								botTimestamp, 
								responseTime, 
								recommendations)
						);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }
		
		return result;
	}
	
	public List<RecEntityRating> getRecEntityRatingLog(long start) {
		List<RecEntityRating> result = new ArrayList<>();
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getRecEntityRatingLogQuery());
			ps.setTimestamp(1, new Timestamp(start));
			resultSet = ps.executeQuery();
			while (resultSet.next()) {
				String userID = resultSet.getString("user_id");
				String entityURI = resultSet.getString("movie_uri");
				int numberRecommendationList = resultSet.getInt("number_recommendation_list");
				boolean like = resultSet.getBoolean("like");
				boolean dislike = resultSet.getBoolean("dislike");
				long ratedAt = resultSet.getTimestamp("rated_at").getTime();
				int position = resultSet.getInt("position");
				int pagerankCycle = resultSet.getInt("pagerank_cicle");
				String refine = resultSet.getString("refine");
				String refocus = resultSet.getString("refocus");
				String details = resultSet.getString("details");
				String why = resultSet.getString("why"); 
				String botName = resultSet.getString("bot_name");
				String messageID = resultSet.getString("message_id");
				long botTimestamp = resultSet.getLong("bot_timestamp");
				long responseTime = resultSet.getLong("response_time");
				String recommendations = resultSet.getString("recommendations_list");
				
				result.add(
						new RecEntityRating(
								userID, 
								entityURI, 
								numberRecommendationList, 
								like, 
								dislike, 
								ratedAt, 
								position, 
								pagerankCycle, 
								refine != null, 
								refocus != null, 
								details != null, 
								why != null, 
								botName, 
								messageID, 
								botTimestamp, 
								responseTime, 
								recommendations)
						);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }
		
		return result;
	}
	
	public List<PropertyRating> getPropertyRatingLog(long start) {
		List<PropertyRating> result = new ArrayList<>();
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getPropertyRatingLogQuery());
			ps.setTimestamp(1, new Timestamp(start));
			resultSet = ps.executeQuery();
			while (resultSet.next()) {
				String userID = resultSet.getString("user_id");
				String propertyTypeURI = resultSet.getString("property_type_uri");
				String propertyURI = resultSet.getString("property_uri");
				int numberRecommendationList = resultSet.getInt("number_recommendation_list");
				int rating = resultSet.getInt("rating");
				long ratedAt = resultSet.getTimestamp("rated_at").getTime();
				String lastChange = resultSet.getString("last_change");
				String botName = resultSet.getString("bot_name");
				
				result.add(
						new PropertyRating(
								userID,
								propertyTypeURI,
								propertyURI,
								rating,	
								ratedAt,
								lastChange, 
								numberRecommendationList,
								botName
								)
						);
						
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }
		
		return result;
	}
	
	public List<EntityRating> getEntityRatingLog(long start) {
		List<EntityRating> result = new ArrayList<>();
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getEntityRatingLogQuery());
			ps.setTimestamp(1, new Timestamp(start));
			resultSet = ps.executeQuery();
			while (resultSet.next()) {
				String userID = resultSet.getString("user_id");
				String entityURI = resultSet.getString("movie_uri");
				int numberRecommendationList = resultSet.getInt("number_recommendation_list");
				int rating = resultSet.getInt("rating");
				long ratedAt = resultSet.getTimestamp("rated_at").getTime();
				String lastChange = resultSet.getString("last_change");
				String botName = resultSet.getString("bot_name");
				String details = resultSet.getString("details");
				
				result.add(
						new EntityRating(
								userID,
								entityURI,
								rating,	
								ratedAt,
								lastChange, 
								numberRecommendationList,
								details,
								botName
								)
						);
						
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }
		
		return result;
	}
	
	public List<QuestionnaireAnswer> getAnswers() {
		List<QuestionnaireAnswer> result = new ArrayList<>();
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getAnswersQuery());
			resultSet = ps.executeQuery();
			while (resultSet.next()) {
				String userID = resultSet.getString("user_id");
				int questionID = resultSet.getInt("question_id");
				int answerID = resultSet.getInt("answer_id");
				result.add(new QuestionnaireAnswer(userID, questionID, answerID));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }
		return result;
	}
	
	public void setLogMessageToCheck(String userID, String messageID, boolean toCheck) {
		int value = 0;
		if (toCheck) {
			value = 1;
		}
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getSetLogMessageToCheckQuery());
			ps.setInt(1, value);
			ps.setString(2, messageID);
			ps.setString(3, userID);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }	
	}
		
	public void insertAnswer(String userID, int questionId, int answerId) {
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getInsertAnswerQuery());
			ps.setString(1, userID);
			ps.setInt(2, questionId);
			ps.setInt(3, answerId);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }	
	}
	
	public String getEntityURI(String label) {
		String result = null;
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			ps = connect.prepareStatement(QueryClass.getEntityURIQuery(label));
			//ps.setString(1, label);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString("uri");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 	finally { close(connect, ps, resultSet); }	

		return result;
	}

	public String getPropertyURI(String label) {
		String result = null;
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			ps = connect.prepareStatement(QueryClass.getPropertyURIQuery());
			ps.setString(1, label);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString("uri");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 	finally { close(connect, ps, resultSet); }	

		return result;
	}

	public List<String> getPropertyValues(String propertyTypeURI) {
		List<String> result = new ArrayList<String>();
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getPropertyValuesQuery());
			ps.setString(1, propertyTypeURI);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("label"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 	finally { close(connect, ps, resultSet); }	

		return result;
	}
	
	public List<String> getPropertyValuesSortedByPopularity(String propertyTypeURI) {
		List<String> result = new ArrayList<String>();
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getPropertyValuesSortedByPopularityQuery());
			ps.setString(1, propertyTypeURI);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("label"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 	finally { close(connect, ps, resultSet); }	

		return result;
	}

	public boolean getMessageID(String messageID) {
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		boolean hasMessage = false;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getMessageIDQuery());
			ps.setString(1, messageID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				hasMessage = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 	finally { close(connect, ps, resultSet); }	

		return hasMessage;
	}
	
	public String getLastTestMessageID() {
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		String messageID = null;
		try {
			connect = dataSource.getConnection();	
			ps = connect.prepareStatement(QueryClass.getLastTestMessageIDQuery());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				messageID = rs.getString("message_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 	finally { close(connect, ps, resultSet); }	

		return messageID;
	}
	
	/**
	 * Resituisce una lista di property URI contenente
	 * quelle più raccomandabili per l'utente, in ordine
	 * decrescente
	 * @param userID
	 * @param propertyTypeURI
	 * @return
	 */
	public List<String> getBestProperties(String userID, String propertyTypeURI) {
		List<String> result = new ArrayList<String>();
		Connection connect = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			ps = connect.prepareStatement(QueryClass.getBestProperties());
			ps.setInt(1, Integer.parseInt(userID));
			ps.setString(2, propertyTypeURI);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("property_uri"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally { close(connect, ps, resultSet); }
		return result;
	}
}
