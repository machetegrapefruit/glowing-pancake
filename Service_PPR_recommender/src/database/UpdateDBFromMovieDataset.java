package database;

import java.io.BufferedReader;
import java.io.FileReader;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
//import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.FileManager;

public class UpdateDBFromMovieDataset {	
	
	protected static Logger currLogger = Logger.getLogger(UpdateDBFromMovieDataset.class.getName());
	private static String filename = "D:\\Dropbox\\MAGISTRALE\\TESI\\Dataset\\mapping-dbpediaMovies-complete.nt";
	private static String fileListTrailerMovies = "D:\\Dropbox\\MAGISTRALE\\TESI\\Dataset\\list_trailer_movies.mapping";
	private static UpdateAccessRecsysDB dbAccess;
    private static String subjectString = null;
    private static String predicateString = null;
    private static String objectString = null;
    private static String name = null;
    private static int i=0;
    private static int d=0;
    private static int k=0;
    private static int w=0;
    
    private static Map<String, String> UriTrailerMap = new HashMap<>();
	
	public UpdateDBFromMovieDataset(){		
		dbAccess = new UpdateAccessRecsysDB();		
	}
	
	public void updateDBTableMoviesPropertiesDbpedia() throws Exception{
		if (FileManager.get().open(filename) != null) {
			System.out.println("File Reading... ");
			currLogger.info("Update DB Table Properties started");
	    	
	    	
	    	// Create a PipedRDFStream to accept input and a PipedRDFIterator to consume it
	        PipedRDFIterator<Triple> iter = new PipedRDFIterator<>();
	        final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);

	        // PipedRDFStream and PipedRDFIterator need to be on different threads
	        ExecutorService executor = Executors.newSingleThreadExecutor();

	        // Create a runnable for our parser thread
	        Runnable parser = new Runnable() 
	        {
	        	@Override
	            public void run() {
	                // Call the parsing process.
	                RDFDataMgr.parse(inputStream, filename);
	            }
	        };
	        
	        // Start the parser on another thread
	        executor.submit(parser);    	        
	        
	        while (iter.hasNext()) {
	            Triple next = iter.next();
	            i++;
	            subjectString = next.getSubject().toString();
	            predicateString = next.getPredicate().toString();
	            objectString = next.getObject().toString();
	            updateTableMoviesPropertiesDbpedia();
	        
		    }
	        currLogger.info("Update database completed");
	        System.err.println("Total Row d: " + d);
	        System.err.println("Insert Row k: " + k);
	        System.err.println("Not Insert Row w: " + w);
		    
		} else {
		    System.err.println("Error - Cannot read file " + filename);
		    }
	}
	
	public void updateDBFromDataset() throws Exception{
	    if (FileManager.get().open(filename) != null) {
	    	currLogger.info("Update Database started");
	    	System.out.println("File Reading... ");
	    				
			// Create a PipedRDFStream to accept input and a PipedRDFIterator to consume it
	        PipedRDFIterator<Triple> iter = new PipedRDFIterator<>();
	        final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);

	        // PipedRDFStream and PipedRDFIterator need to be on different threads
	        ExecutorService executor = Executors.newSingleThreadExecutor();

	        // Create a runnable for our parser thread
	        Runnable parser = new Runnable() 
	        {
	        	@Override
	            public void run() {
	                // Call the parsing process.
	                RDFDataMgr.parse(inputStream, filename);
	            }
	        };
	        
	        // Start the parser on another thread
	        executor.submit(parser);    
	        
	        
	        while (iter.hasNext()) {
	            Triple next = iter.next();
	            i++;
	            subjectString = next.getSubject().toString();
	            predicateString = next.getPredicate().toString();
	            objectString = next.getObject().toString();
	            name = null;
				
				//Va rivista in base alle proprieta' considerate
				switch (predicateString) {
	    			case "http://dbpedia.org/ontology/director": updateDBdirector();
						break;
	    			case "http://dbpedia.org/ontology/producer": updateDBproducer();
		    			break;
		    		case "http://dbpedia.org/ontology/writer":updateDBwriter();
						break;
	    			case "http://dbpedia.org/ontology/starring":updateDBstarring();		
						break;
		    		case "http://dbpedia.org/ontology/musicComposer":updateDBmusicComposer();
						break;
		    		case "http://dbpedia.org/ontology/cinematography":updateDBcinematography();
						break;
		    		case "http://dbpedia.org/ontology/editing":updateDBediting();
						break;
		    		case "http://dbpedia.org/ontology/distributor":updateDBdistributor();
						break;
					case "http://dbpedia.org/ontology/basedOn":updateDBbasedOn();
						break;
					case "http://dbpedia.org/ontology/releaseDate":updateDBreleaseDate();
						break;					
	    			case "http://purl.org/dc/terms/subject":updateDBcategory();	    							
	    	    		break;
	    			case "http://dbpedia.org/ontology/runtime":updateDBruntime();
	    				break;
	    			default:
//	    				String iString = Integer.toString(i);
//	    				currLogger.info(iString);
	    				//System.out.println("Row: " +i + " Null");i++;	
	    				break;
	    		}
	        }
	        currLogger.info("Update database completed");
	    } else {
	        System.err.println("Error - Cannot read file " + filename);
	    }
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
	
	//Metodo per l'inserimento delle triple del dataset nella tabella movies_properties_dbpedia
	private static void updateTableMoviesPropertiesDbpedia() throws Exception {
		d++;
		if (objectString.length() >= 28
			&& (!predicateString.contains("http://dbpedia.org/ontology/budget"))
			&& (!predicateString.contains("http://dbpedia.org/ontology/gross"))
			&& (!predicateString.contains("http://dbpedia.org/ontology/runtime"))
			&& (!predicateString.contains("http://xmlns.com/foaf/0.1/name"))
			&& (!predicateString.contains("http://dbpedia.org/ontology/releaseDate"))
			&& (!predicateString.contains("http://dbpedia.org/ontology/viafId"))
			&& (!predicateString.contains("http://dbpedia.org/ontology/bnfId"))
			&& (!predicateString.contains("http://dbpedia.org/ontology/startDate"))
			&& (!predicateString.contains("http://dbpedia.org/ontology/endDate"))
			&& (!predicateString.contains("http://dbpedia.org/ontology/imdbId"))
			&& (!predicateString.contains("http://dbpedia.org/ontology/lccnId"))
		) {
			k++;
			//System.out.println("Insert Row: " + i + " - properties");			
			dbAccess.insertDatasetTripleToMoviesPropertiesDbpedia(subjectString, predicateString, objectString);
			//System.out.println("<"+subjectString+ "> <"+ predicateString + "> <" +objectString + "> .");
		}
		else{
			w++;
			//System.out.println("Warning - Not Insert Row: " + d);
			//System.out.println("<"+subjectString+ "> <"+ predicateString + "> <" +objectString + "> .");
		}
			
		
	}
	
		
	private static String getName(String fullName){
		String subName = fullName.substring(28, fullName.length());
		subName = subName.replaceAll("_", " ");
		return subName;
	}

	private static void updateDBdirector() throws Exception {
		if (objectString.length() >= 28) {
			System.out.println("Row: " + i + " - director");
			name = getName(objectString);
			
			dbAccess.insertDirectorAndMovie(objectString, subjectString, name);
			System.out.println("name:  "+ name );

//		    	        System.out.println("Subject:  "+ subjectString );
//		    	        System.out.println("Predicate:  "+ predicateString);
//		    	        System.out.println("Object:  "+ objectString);
//	  	   	           	System.out.println("");
		}		
	}

	private static void updateDBproducer() throws Exception {
		if (objectString.length() >= 28) {
			System.out.println("Row: " + i + " - producer");
			name = getName(objectString);
			
			dbAccess.insertProducerAndMovie(objectString, subjectString, name);	    				
			//System.out.println("name:  "+ name );		    				
		}		
	}

	private static void updateDBwriter() throws Exception {
		if (objectString.length() >= 28) {
			System.out.println("Row: " + i + " - writer");
			name = getName(objectString);
			
			dbAccess.insertWriterAndMovie(objectString, subjectString, name);	
			//System.out.println("name:  "+ name );	
		} 		
	}

	private static void updateDBstarring() throws Exception {
		if (objectString.length() >= 28) {
			System.out.println("Row: " + i + " - starring");
			name = getName(objectString);
			
			dbAccess.insertStarringAndMovie(objectString, subjectString, name);	    				
			//System.out.println("name:  "+ name );
			
			String title = subjectString.substring(28, subjectString.length());
			title = title.replaceAll("_", " ");
			dbAccess.insertTitleOfMovie(subjectString, title);
			//String titleAscii = java.net.URLDecoder.decode(title, "UTF-8");
			System.out.println("title: " + title);
		}  		
	}

	private static void updateDBmusicComposer() throws Exception {
		if (objectString.length() >= 28) {
			System.out.println("Row: " + i + " - musicComposer");
			name = getName(objectString);
			
			dbAccess.insertMusicComposerAndMovie(objectString, subjectString, name);	
			//System.out.println("name:  "+ name );	
		}
	}

	private static void updateDBcinematography() throws Exception {
		if (objectString.length() >= 28) {
			System.out.println("Row: " + i + " - cinematography");
			name = getName(objectString);
			
			dbAccess.insertCinematographyAndMovie(objectString, subjectString, name);	    				
			//System.out.println("name:  "+ name );	
		}		
	}

	private static void updateDBediting() throws Exception {
		if (objectString.length() >= 28) {
			System.out.println("Row: " + i + " - editing");
			name = getName(objectString);
			
			dbAccess.insertEditingAndMovie(objectString, subjectString, name);		    				
			//System.out.println("name:  "+ name );	
		}		
	}

	private static void updateDBdistributor() throws Exception {
		if (objectString.length() >= 28) {
			System.out.println("Row: " + i + " - distributor");
			name = getName(objectString);
			
			dbAccess.insertDistributorAndMovie(objectString, subjectString, name);	    				
			//System.out.println("name:  "+ name );	
		}		
	}

	private static void updateDBbasedOn() throws Exception {
		if (objectString.length() >= 28) {
			System.out.println("Row: " + i + " - basedOn");
			name = getName(objectString);
			
			dbAccess.insertBasedOnAndMovie(objectString, subjectString, name);    				
			//System.out.println("name:  "+ name );	
		}		
	}

	private static void updateDBreleaseDate() throws Exception {
		if (objectString.length() >= 28) {
			System.out.println("Row: " + i + " - releaseDate");
			String releaseDate = objectString.substring(1,11);
			
			dbAccess.insertReleaseDateAndMovie(subjectString, releaseDate);  				
			//System.out.println("releaseDate:  "+ releaseDate );	
		}		
	}

	private static void updateDBcategory() throws Exception {
		if (objectString.length() >= 28) {
			String categoryString = objectString.toLowerCase();
			if (categoryString.contains("1") == false && categoryString.contains("2") == false) {
				if (categoryString.contains("action") == true || 
					categoryString.contains("adventure") == true || 
					categoryString.contains("comedy") == true || 
					categoryString.contains("crime") == true || 
					categoryString.contains("drama") == true || 
					categoryString.contains("fantasy") == true || 
					categoryString.contains("historical") == true || 
					categoryString.contains("horror") == true || 
					categoryString.contains("magical") == true || 
					categoryString.contains("mystery") == true || 
					categoryString.contains("paranoid") == true || 
					categoryString.contains("philosophical") == true || 
					categoryString.contains("political") == true || 
					categoryString.contains("romance") == true || 
					categoryString.contains("saga") == true || 
					categoryString.contains("satire") == true || 
					categoryString.contains("science") == true || 	    		
					categoryString.contains("speculative") == true || 
					categoryString.contains("thriller") == true || 
					categoryString.contains("urban") == true || 
					categoryString.contains("western") == true || 
					categoryString.contains("animation") == true || 
					categoryString.contains("black-and-white") == true || 
					categoryString.contains("erotic") == true || 
					categoryString.contains("documentary") == true || 
					categoryString.contains("musical") == true || 
					categoryString.contains("sports") == true || 
					categoryString.contains("religion") == true ||
					categoryString.contains("war") == true || 
					categoryString.contains("win") == true ||
					categoryString.contains("romantic") == true || 
					categoryString.contains("christmas") == true || 
					categoryString.contains("biographical") == true){
					System.out.println("Row: " + i + " - category");
					String categoryName = objectString.substring(37, objectString.length());
					categoryName = categoryName.replaceAll("_", " ");
					dbAccess.insertCategoryAndMovie(objectString, subjectString, categoryName);	    				
					//System.out.println("categoryName:  "+ categoryName );
				}
			}
		}		
	}

	private static void updateDBruntime() throws Exception {
		if (subjectString.length() >= 28) {
			System.out.println("Row: " + i + " - runtime");
			//String runtimeUri = objectString;
			String runtimeUri = objectString.substring(1,objectString.indexOf("."));	    		
			int runtimeMinutes = Integer.parseInt(runtimeUri)/60;
			int runtimeRange = runtimeMinutes;
			if (runtimeMinutes <= 90)
				runtimeRange = 90;
			else if (runtimeMinutes > 90 && runtimeMinutes <= 120 )
				runtimeRange = 150;
			else if (runtimeMinutes > 120 && runtimeMinutes <= 150 )
				runtimeRange = 120;
			else if (runtimeMinutes > 150)
				runtimeRange = 151;
			dbAccess.insertRuntimeAndMovie(subjectString, runtimeUri, runtimeMinutes, runtimeRange);
		}
		//System.out.println("runtime: " + runtime);
		//System.out.println("minutes: " + minutes);
		//System.out.println("runtime_range: " + runtime_range);
		
		//System.out.println("Subject:  "+ subjectString );
		//System.out.println("Predicate:  "+ predicateString);
		//System.out.println("Object:  "+ objectString);
		//System.out.println("");
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
			movieTitleMultiMap = dbAccess.selectAllMovies();
			
			System.out.println("updateDBFromOMDb: "+ movieTitleMultiMap.size());
			for(String key : movieTitleMultiMap.keySet()) {
				String movieTitle = key.substring(28, key.length());
				movieTitle = movieTitle.replaceAll("_", " ");
				if (movieTitle.contains("(")) {
					int end = movieTitle.indexOf("(");
					movieTitle = movieTitle.substring(0,end);
				}
				OMDbMap = getMovieInfoFromOMDb(URLDecoder.decode(movieTitle, "UTF-8"));
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
	
	public void getMappedTrailer() throws Exception {
		UriTrailerMap.clear();
		try {
			BufferedReader readerFilmStored = new BufferedReader(new FileReader(fileListTrailerMovies));
			String line = "";
			while ((line = readerFilmStored.readLine()) != null) {
				String[] splitted = line.split("\t");				
				String uriString = splitted[0];
				String trailerUri = splitted[1].replaceAll("//", "");
				UriTrailerMap.put(uriString, trailerUri);
			}
			readerFilmStored.close();
			dbAccess.updateTrailerOfMovie(UriTrailerMap);
			
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}
	
	public static void main(String[] args) throws Exception {
		
		UpdateDBFromMovieDataset updateDatabase = new UpdateDBFromMovieDataset();
		
		//updateDatabase.updateDBFromDataset();
		//updateDatabase.updateDBTableMoviesPropertiesDbpedia();
		//updateDatabase.updateDBFromOMDb();
		//updateDatabase.updateTableMoviesPropertiesNotable();
		updateDatabase.getMappedTrailer();
		
    
   }	
	
}

		


