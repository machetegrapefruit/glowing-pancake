package database;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class UpdateDBgaps {	
	
	protected static Logger currLogger = Logger.getLogger(UpdateDBFromMovieDataset.class.getName());
	private static UpdateAccessRecsysDB dbAccess;
	
	public UpdateDBgaps(){		
		dbAccess = new UpdateAccessRecsysDB();		
	}
	
	public void getMoviePoster(){
        try {
		        String selectedItem = "Forrest gump";
		
		        InputStream input = new URL("http://www.omdbapi.com/?t=" + URLEncoder.encode(selectedItem, "UTF-8")).openStream();
		        Map<String, String> map = new Gson().fromJson(new InputStreamReader(input, "UTF-8"), new TypeToken<Map<String, String>>(){}.getType());
		
		        String title = map.get("Title");
		        String year = map.get("Year");
		        String released = map.get("Released");
		        String runtime = map.get("Runtime");
		        String genre = map.get("Genre");
		        String actors = map.get("Actors");
		        String plot = map.get("Plot");
		        String imdbRating = map.get("imdbRating");
		        String poster = map.get("Poster");
		
		        System.out.println("poster url: " + poster);
		
		    } catch (JsonIOException | JsonSyntaxException | IOException e){
		        System.out.println(e);
		    }
	}
	


	
	public void updateDBmovieTableruntimeRange() throws Exception {
		Map<String, Integer> movieRuntimeRangeMap = dbAccess.selectAllMovieAndSetRuntimeRange();
		dbAccess.updateRuntimeRangeOfMovies(movieRuntimeRangeMap);
	}
	
	public void updateDBmovieTablereferencePeriod() throws Exception{
		Map<String, String> movieReferencePeriodMap = dbAccess.selectAllMovieAndSetReferencePeriod();
		dbAccess.updateReferencePeriodOfMovies(movieReferencePeriodMap);
	}
	
	public Map<String, String> getMovieInfoFromOMDb(String movieTitle, String year){
		Map<String, String> map = null;
        try {		
        		InputStream input = new URL("http://www.omdbapi.com/?t=" + URLEncoder.encode(movieTitle, "UTF-8") + "&y=" + year).openStream();
		        //InputStream input = new URL("http://www.omdbapi.com/?t=" + URLEncoder.encode(movieTitle, "UTF-8")).openStream();
        		//InputStream input = new URL("http://www.omdbapi.com/?t=" + movieTitle).openStream();
        		map = new Gson().fromJson(new InputStreamReader(input, "UTF-8"), new TypeToken<Map<String, String>>(){}.getType());
		
		    } catch (JsonIOException | JsonSyntaxException | IOException e){
		        System.out.println(e);
		    }
		return map;
	}
	
	public Map<String, String> getMovieInfoFromOMDb(String movieTitle){
		Map<String, String> map = null;
        try {		
		        InputStream input = new URL("http://www.omdbapi.com/?t=" + URLEncoder.encode(movieTitle, "UTF-8")).openStream();
        		//InputStream input = new URL("http://www.omdbapi.com/?t=" + movieTitle).openStream();
        		map = new Gson().fromJson(new InputStreamReader(input, "UTF-8"), new TypeToken<Map<String, String>>(){}.getType());
		
		    } catch (JsonIOException | JsonSyntaxException | IOException e){
		        System.out.println(e);
		    }
		return map;
	}
	
	public void updateDBFromOMDb() throws Exception{
		Map<String, String> movieTitleMultiMap = new HashMap<String, String>();
		Map<String, String> OMDbMap = null;
		int i= 1;
		double percent, oldPercent = 0;
		try{
			//movieTitleMultiMap = dbAccess.selectAllMovies();
			movieTitleMultiMap = dbAccess.selectAllUriTitleAndYearFromMovies();
			
			System.out.println("updateDBFromOMDb: "+ movieTitleMultiMap.size());
			for(String key : movieTitleMultiMap.keySet()) {
				String value = movieTitleMultiMap.get(key);
				String[] parts = value.split("|");
	    		//String movieTitle = parts[0]; 
	        	String releaseYear = parts[1];
	        	
				String movieTitle = key.substring(28, key.length());
				
				movieTitle = movieTitle.replaceAll("_", " ");
				if (movieTitle.contains("(")) {
					int end = movieTitle.indexOf("(");
					movieTitle = movieTitle.substring(0,end);
				}
				OMDbMap = getMovieInfoFromOMDb(URLDecoder.decode(movieTitle, "UTF-8"),releaseYear);
				//System.out.println("movieTitle: " + movieTitle + " - OMDbMap: "+ OMDbMap.toString());
				if (OMDbMap.containsKey("Error") == false) {
					String title = OMDbMap.get("Title");
			        String year = OMDbMap.get("Year");
			        String released = OMDbMap.get("Released");
			        String runtime = OMDbMap.get("Runtime");
			        String genre = OMDbMap.get("Genre");
			        String plot = OMDbMap.get("Plot");
			        String language = OMDbMap.get("Language");
			        String country = OMDbMap.get("Country");
			        String awards = OMDbMap.get("Awards");
			        String poster = OMDbMap.get("Poster");
			        
			        if (released.equals("N/A") == false) {
			        	released = released.replace(" ","-");		        
				        DateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
				        Date date1 = format1.parse(released);
				        DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
				        released = format2.format(date1);
				        //System.out.println(released);
					}
			        else
			        	released = "1900-01-01";
			        	
			        
			        if (runtime.equals("N/A") == false && runtime.length() <= 7 && runtime.contains("min")) {
			        	runtime = runtime.replace(" min","");
					}        
			        else {
			        	runtime = "0";
					}
			        year = year.substring(0,4);
			        plot = plot.replace("\"","");
			        runtime = runtime.replace(" min","");
			        
					if (poster != null && poster.equals("N/A") == false) {
						dbAccess.insertInfoOfMovie(key, title, year, released, Integer.parseInt(runtime), plot, language, country, awards, poster);
					}
					else {
						poster = "N/A";
						dbAccess.insertInfoOfMovie(key, title, year, released, Integer.parseInt(runtime), plot, language, country, awards, poster);
					}
					//System.out.println(i + "- " + OMDbMap.toString());
					List<String> genreList = Arrays.asList(genre.split(","));
					for (Iterator<String> iterator = genreList.iterator(); iterator.hasNext();) {
						String genreName = (String) iterator.next();
						genreName = genreName.replace(" ","");
						dbAccess.insertGenreAndMovie(genreName, key);
						//System.out.println(i + "- " + title + " - " + genreName);
					}
				}  
				
	            percent = (100*i)/movieTitleMultiMap.size();
	            if(percent % 2 == 0 && oldPercent != percent){
	            	oldPercent = percent;
	            	System.out.print(percent +"%   ");
	            }
	            i++;  
			}
		} catch (JsonIOException | JsonSyntaxException | IOException e){
			System.err.println("OMDbMap: "+ OMDbMap.toString());
	        System.out.println(e);
	    }			   
	}

	public void updateDBallFieldFromOMDb() throws Exception{
		Map<String, String> movieTitleMultiMap = new HashMap<String, String>();
		Map<String, String> OMDbMap = null;
		int i= 1;
		double percent, oldPercent = 0;
		try{
			//basta cambiare la query di selezione dei film per farlo per tutti
			movieTitleMultiMap = dbAccess.selectAllUriTitleAndYearFromMovies();
			
			//System.out.println("updateDBfiledIMDbFromOMDb: "+ movieTitleMultiMap.size());
			for(String key : movieTitleMultiMap.keySet()) {
				String value = movieTitleMultiMap.get(key);
				String[] parts = value.split("#");
	    		String movieTitle = parts[0]; 
	        	String year = parts[1];

				//OMDbMap = getMovieInfoFromOMDb(URLDecoder.decode(movieTitle, "UTF-8"),year);
				//OMDbMap = getMovieInfoFromOMDb(movieTitle,year);
				OMDbMap = getMovieInfoFromOMDb(movieTitle);
				if (OMDbMap.containsKey("Error") == false) {
			        String release_date = OMDbMap.get("Released");
			        String runtime_minutes = OMDbMap.get("Runtime");
			        String genre = OMDbMap.get("Genre");
			        String plot = OMDbMap.get("Plot");
			        String language = OMDbMap.get("Language");
			        String country = OMDbMap.get("Country");
			        String awards = OMDbMap.get("Awards");
			        String poster = OMDbMap.get("Poster");
					String metascore = OMDbMap.get("Metascore");
			        String imdb_rating = OMDbMap.get("imdbRating");
			        String imdb_votes = OMDbMap.get("imdbVotes");
			        String imdb_id = OMDbMap.get("imdbID");			        

			        //System.out.println("movieTitle: " + movieTitle + " - year: "+ year.toString() + " - OMDbMap: "+ OMDbMap.toString());
			        
			        if (release_date.equals("N/A") == false) {
			        	release_date = release_date.replace(" ","-");		        
				        DateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
				        Date date1 = format1.parse(release_date);
				        DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
				        release_date = format2.format(date1);
				        //System.out.println("release_date:" + release_date);
					}
			        else
			        	release_date = "1900-01-01";
			        	
			        
			        if (runtime_minutes.equals("N/A") == false && runtime_minutes.length() <= 7 && runtime_minutes.contains("min")) {
			        	runtime_minutes = runtime_minutes.replace(" min","");
					}        
			        else {
			        	runtime_minutes = "0";
					}

			        plot = plot.replace("\"","");
			        runtime_minutes = runtime_minutes.replace(" min","");
			        
					if (poster != null && poster.equals("N/A") == false) {
						dbAccess.updateAllInfoOfMovie(key, release_date, Integer.parseInt(runtime_minutes), plot, language, country, awards, poster, metascore, imdb_rating, imdb_votes, imdb_id);
					}
					else {
						poster = "N/A";
						dbAccess.updateAllInfoOfMovie(key, release_date, Integer.parseInt(runtime_minutes), plot, language, country, awards, poster, metascore, imdb_rating, imdb_votes, imdb_id);
					}
					//System.out.println(i + "- " + OMDbMap.toString());
					List<String> genreList = Arrays.asList(genre.split(","));
					for (Iterator<String> iterator = genreList.iterator(); iterator.hasNext();) {
						String genreName = (String) iterator.next();
						genreName = genreName.replace(" ","");
						dbAccess.insertGenreAndMovie(genreName, key);
						//System.out.println(i + "- " + key + " - " + genreName);
					}
					System.out.println("movieTitle: " + movieTitle + " - year: "+ year.toString());
	            	System.out.println(i + "- " + key + " - " + OMDbMap.toString());
				}  
				
	            percent = (100*i)/movieTitleMultiMap.size();
	            if(percent % 2 == 0 && oldPercent != percent){
	            	oldPercent = percent;
	            	System.out.print(percent +"%   ");
	            	//System.out.println("movieTitle: " + movieTitle + " - year: "+ year.toString());
	            	//System.out.println(i + "- " + key + " - " + OMDbMap.toString());
	            }
	            i++;  
			}
		} catch (JsonIOException | JsonSyntaxException | IOException e){
			System.err.println("OMDbMap: "+ OMDbMap.toString());
	        System.out.println(e);
	    }			   
	}
	
	
	public void updateDBfiledIMDbFromOMDb() throws Exception{
		Map<String, String> movieTitleMultiMap = new HashMap<String, String>();
		Map<String, String> OMDbMap = null;
		int i= 1;
		double percent, oldPercent = 0;
		try{
			//basta cambiare la query di selezione dei film per farlo per tutti
			movieTitleMultiMap = dbAccess.selectAllUriTitleAndYearFromMovies();
			
			System.out.println("updateDBfiledIMDbFromOMDb: "+ movieTitleMultiMap.size());
			for(String key : movieTitleMultiMap.keySet()) {
				String value = movieTitleMultiMap.get(key);
				String[] parts = value.split("|");
	    		String movieTitle = parts[0]; 
	        	String year = parts[1];
				
				System.out.println(movieTitle);
				//OMDbMap = getMovieInfoFromOMDb(URLDecoder.decode(movieTitle, "UTF-8"));
				OMDbMap = getMovieInfoFromOMDb(movieTitle,year);
				//System.out.println("movieTitle: " + movieTitle + " - OMDbMap: "+ OMDbMap.toString());
				if (OMDbMap.containsKey("Error") == false) {
					String metascore = OMDbMap.get("Metascore");
			        String imdbRating = OMDbMap.get("imdbRating");
			        String imdbVotes = OMDbMap.get("imdbVotes");
			        String imdbID = OMDbMap.get("imdbID");			        

					//System.out.println(movieTitle +  " - " + OMDbMap.toString());
			        dbAccess.updateFiledIMDbOfMovie(key, metascore, imdbRating, imdbVotes, imdbID);
				}  
				
	            percent = (100*i)/movieTitleMultiMap.size();
	            if(percent % 2 == 0 && oldPercent != percent){
	            	oldPercent = percent;
	            	System.out.print(percent +"%   ");
	            }
	            i++;  
			}
		} catch (JsonIOException | JsonSyntaxException | IOException e){
			System.err.println("OMDbMap: "+ OMDbMap.toString());
	        System.out.println(e);
	    }			   
	}
	
	public void updateTableMoviesPropertiesNotable() throws Exception{
		/*
		 * Selezionare solo i film con la locandina da movies
		 * Per ogni film
		 * controllare che � un film con la locandina 
		 * inserire le propriet� rilevanti dalla tabella movies_properties_dbpedia
		 * inserire genre,runtimeRange,releaseYear
		*/
		dbAccess.insertGenreAndMovieToProperties();
	}
	
	public static void updateDBreleaseYear() throws Exception {
		Map<String, String> movieReferencePeriodMap = dbAccess.selectAllMovieAndSetReferencePeriod();
		dbAccess.updateReleaseYearOfMovies(movieReferencePeriodMap);
	}
	
	public static void updateDBnameOfCategoryInVertexTable() throws Exception{
		Map<String,String> categoryUriNameMap = dbAccess.selectNameOfCategoryInVertexTable();
		dbAccess.updateNameOfCategoryInVertexTable(categoryUriNameMap);
	}
			
	
	public static void main(String[] args) throws Exception {
		
		UpdateDBgaps updateDatabase = new UpdateDBgaps();
		
		int count = 0;
		int maxTries = 20;
		while(true) {
		    try {
		    	System.err.println("Try: "+ count + " cicle");
				updateDatabase.updateDBallFieldFromOMDb();
		    } catch (Exception e) {
		        // handle exception
		        if (++count == maxTries) throw e;
		    }
		}

		//updateDatabase.updateDBFromOMDb();
//		try {
//			for (int i = 0; i < 10; i++) {
//				System.err.println("Try: "+ i + " cicle");
//				updateDatabase.updateDBallFieldFromOMDb();
//			}
//		} catch (Exception e) {
//			for (int i = 0; i < 10; i++) {
//				System.err.println("catch: "+ i + " cicle");
//				updateDatabase.updateDBallFieldFromOMDb();
//			}
//		}
		
		
		//updateDatabase.updateDBfiledIMDbFromOMDb();
		
		//updateDatabase.updateTableMoviesPropertiesNotable();
		//updateDatabase.updateDBmovieTableruntimeRange();
		//updateDatabase.updateDBreleaseYear();
		//updateDatabase.updateDBmovieTablereferencePeriod();
		//updateDatabase.updateDBnameOfCategoryInVertexTable();
		
		
    
   }	
	
}

		


