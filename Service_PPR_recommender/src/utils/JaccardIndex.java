package utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Sets;

import database.AccessRecsysDB;
/**
 * @author Francesco Baccaro
 */
public class JaccardIndex {
	private AccessRecsysDB dbAccess = new AccessRecsysDB();
	
    private static Map<String, Double> getTopTwentyOfJaccard(Map<String, Double> resourceJaccardIndextMap){
    	Map<String, Double> jaccardIndexTopTwentyMap = new HashMap<String, Double>();
    
		int k = 0;
		for (String key : resourceJaccardIndextMap.keySet()){
			if (k < 20) {
				Double value = resourceJaccardIndextMap.get(key);
				//System.out.println("" + key + " index: " + resourceJaccardIndextMap.get(key));
				jaccardIndexTopTwentyMap.put(key, value);
				k++;
			}
			else {
				break;
			}    		
		}
		
		jaccardIndexTopTwentyMap = sortByComparator(jaccardIndexTopTwentyMap, false);
    	
		return jaccardIndexTopTwentyMap;
    	
    }
	
	public static Map<String, Double> sortByComparator(Map<String, Double> distanceMap, final boolean order){
	    List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(distanceMap.entrySet());
	
	    // Sorting the list based on values
	    Collections.sort(list, new Comparator<Entry<String, Double>>(){
	        public int compare(Entry<String, Double> o1, Entry<String, Double> o2){
	            if (order){
	            	return o1.getValue().compareTo(o2.getValue());
	            }
	            else{
	            	return o2.getValue().compareTo(o1.getValue());
	            }
	        }
	    });
	
	    // Maintaining insertion order with the help of LinkedList
	    Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
	    for (Entry<String, Double> entry : list){
	        sortedMap.put(entry.getKey(), entry.getValue());
	    }
	
	    return sortedMap;
	}
     
    
	public Map<String, Double> findJaccardIndex(Set<String> ratingsMoviesUriSet, Set<String> testSet) throws Exception{

    	Map<String, Double> resourceJaccardIndexMap = new HashMap<String, Double>();
    	
    	Map<String, Set<String>> ratingsResourceAndPropertySetMap = new HashMap<String, Set<String>>();
		Map<String, Set<String>> testResourceAndPropertySetMap = new HashMap<String, Set<String>>();
		
		ratingsResourceAndPropertySetMap = dbAccess.selectAllResourceAndPropertValueUriFromDbpediaMoviesSelection(ratingsMoviesUriSet);
        testResourceAndPropertySetMap = dbAccess.selectAllResourceAndPropertValueUriFromDbpediaMoviesSelection(testSet);
        
        Set<String> startMoviePropertySet = new HashSet<String>();
		
        for (String resourceURI : ratingsMoviesUriSet){
            Set<String> resProperties = Sets.newHashSet();
            resProperties = ratingsResourceAndPropertySetMap.get(resourceURI);
            if (resProperties != null) {
            	startMoviePropertySet.addAll(resProperties);
            }
        }        

        System.out.println("startMoviePropertySet size:" + startMoviePropertySet.size());
        		
		for (String resourceURI : testSet){
            Set<String> resProperties = Sets.newHashSet();
            resProperties = testResourceAndPropertySetMap.get(resourceURI);
	        if (resProperties != null) {
	        	double index = Utils.jaccard(startMoviePropertySet, resProperties);
	        	//System.out.println("i:" + i + " - " + resourceURI + ":" + index);
				if (index > 0) {
					resourceJaccardIndexMap.put(resourceURI, index);
				}     
	        }
		} 
		
		resourceJaccardIndexMap = sortByComparator(resourceJaccardIndexMap, false);
		
		//System.out.println("resourceJaccardIndexMap size:" + resourceJaccardIndexMap.size());
		//System.out.println("resourceJaccardIndexMap:" + resourceJaccardIndexMap.toString());	
    	return resourceJaccardIndexMap;
    	
    }

	public Map<String, Double> findJaccardDistance(Set<String> ratingsMoviesUriSet, Set<String> testSet) throws Exception{

    	Map<String, Double> resourceJaccardDistanceMap = new HashMap<String, Double>();
    	//System.out.println("ratingsMoviesUriSet size:" + ratingsMoviesUriSet.size());
    	//System.out.println("testSet size:" + testSet.size());
		
		Map<String, Set<String>> testResourceAndPropertySetMap = new HashMap<String, Set<String>>();
        testResourceAndPropertySetMap = dbAccess.selectAllResourceAndPropertValueUriFromDbpediaMoviesSelection(testSet);
        
        Map<String, Set<String>> ratingsResourceAndPropertySetMap = new HashMap<String, Set<String>>();
        ratingsResourceAndPropertySetMap = dbAccess.selectAllResourceAndPropertValueUriFromDbpediaMoviesSelection(ratingsMoviesUriSet);
        
    	//System.out.println("testResourceAndPropertySetMap size:" + testResourceAndPropertySetMap.size());
    	//System.out.println("ratingsResourceAndPropertySetMap size:" + ratingsResourceAndPropertySetMap.size());
        
        Set<String> startMoviePropertySet = new HashSet<String>();
		
        for (String resourceURI : ratingsMoviesUriSet){
            Set<String> resProperties = Sets.newHashSet();
            resProperties = ratingsResourceAndPropertySetMap.get(resourceURI);
            if (resProperties != null) {
            	startMoviePropertySet.addAll(resProperties);
            	//System.out.println(resourceURI + " - resProperties size:" + resProperties.size() + " - " + resProperties.toString());
            }
        }        

        //System.out.println("startMoviePropertySet size:" + startMoviePropertySet.size());
        //System.out.println("startMoviePropertySet:" + startMoviePropertySet.toString());
		
		for (String resourceURI : testSet){
            Set<String> resProperties = Sets.newHashSet();
            resProperties = testResourceAndPropertySetMap.get(resourceURI);
            if (resProperties != null) {
            	double index = Utils.jaccard(startMoviePropertySet, resProperties);
            	//System.out.println("i:" + i + " - " + resourceURI + ":" + index);
            	double distance = 1 - index;
    			if (distance < 0.95) {
                	resourceJaccardDistanceMap.put(resourceURI, distance);
    			}
            }
		} 
		
		resourceJaccardDistanceMap = sortByComparator(resourceJaccardDistanceMap, false);
		
		//System.out.println("resourceJaccardDistanceMap size:" + resourceJaccardDistanceMap.size());
		//System.out.println("resourceJaccardDistanceMap:" + resourceJaccardDistanceMap.toString());	
    	return resourceJaccardDistanceMap;
    	
    }
	
	public Map<String, Double> getJaccardIndex(String user_id) throws Exception{
		
    	Map<String, Double> jaccardIndexTopTwentyMap = new HashMap<String, Double>();
    	      
        int numberRatedItems = dbAccess.selectNumberOfRatedMoviesByUser(user_id);
        
        if (numberRatedItems == 0) {						//se non ci sono film valutati dall'utente
			String movieURIpopular = dbAccess.selectMovieToRatingByUser(user_id);	//parti da un film random
			jaccardIndexTopTwentyMap.put(movieURIpopular, 1.0);
		} else {											//altrimenti
			ArrayListMultimap<String, Set<String>> ratingsMoviesMap = ArrayListMultimap.create();
	        Map<String, Set<String>> testMap = new HashMap<String, Set<String>>();
	        
	        ratingsMoviesMap = dbAccess.selectPosNegRatingForUserFromRatingsMovies(user_id); //Seleziona tutti i film valutati positivamente o negativamente
	        testMap = dbAccess.selectTestSetForUserFromMovies(user_id);  //Selezione tutti i film tranni quelli valutati
	        
	        Set<String> ratingsMoviesUriSet = new TreeSet<>();
	        Set<String> testSet = new TreeSet<>();
	        
	        for (String userID : ratingsMoviesMap.keySet()) {     
	        	ratingsMoviesUriSet.addAll(ratingsMoviesMap.get(userID).get(0)); //prende gli item valutati positivamente (get(0))    
	        	ratingsMoviesUriSet.addAll(ratingsMoviesMap.get(userID).get(1)); //prende gli item valutati negativamente (get(1))  
	        }
	        
	        for (Set<String> movies : testMap.values()) {
	        	testSet.addAll(movies);        
	        }        
	        
	        jaccardIndexTopTwentyMap = findJaccardIndex(ratingsMoviesUriSet,testSet);
	        jaccardIndexTopTwentyMap = getTopTwentyOfJaccard(jaccardIndexTopTwentyMap);
		}
    	
        System.out.println("jaccardIndexTopTwentyMap:" + jaccardIndexTopTwentyMap.toString());
    	return jaccardIndexTopTwentyMap;
    	
    }

	public Map<String, Double> getJaccardDistance(String user_id) throws Exception{
		
    	Map<String, Double> jaccardDistanceTopTwentyMap = new HashMap<String, Double>();
    	      
        int numberRatedItems = dbAccess.selectNumberOfRatedMoviesByUser(user_id);
        
        if (numberRatedItems == 0) {						//se non ci sono film valutati dall'utente
			String movieURIpopular = dbAccess.selectMovieToRatingByUser(user_id);	//parti da un film random
			jaccardDistanceTopTwentyMap.put(movieURIpopular, 1.0);
		} else {											//altrimenti
			ArrayListMultimap<String, Set<String>> ratingsMoviesMap = ArrayListMultimap.create();
	        Map<String, Set<String>> testMap = new HashMap<String, Set<String>>();
	        
	        //ratingsMoviesMap = dbAccess.selectPosNegRatingForUserFromRatingsMovies(user_id); //Seleziona tutti i film valutati positivamente o negativamente
	        ratingsMoviesMap = dbAccess.selectPosNegRatingForUserFromRatingsMoviesByUser(user_id); //Seleziona tutti i film valutati positivamente o negativamente dall'utente
	        testMap = dbAccess.selectTestSetForUserFromMovies(user_id);  //Selezione tutti i film tranne quelli valutati
	        
	        Set<String> ratingsMoviesUriSet = new TreeSet<>();
	        Set<String> testSet = new TreeSet<>();
	        
	        for (String userID : ratingsMoviesMap.keySet()) {     
	        	ratingsMoviesUriSet.addAll(ratingsMoviesMap.get(userID).get(0)); //prende gli item valutati positivamente (get(0))    
	        	ratingsMoviesUriSet.addAll(ratingsMoviesMap.get(userID).get(1)); //prende gli item valutati negativamente (get(1))  
	        } 
	        
	        for (Set<String> movies : testMap.values()) {
	        	testSet.addAll(movies);        
	        }        
        
	        jaccardDistanceTopTwentyMap = findJaccardDistance(ratingsMoviesUriSet,testSet);
	        jaccardDistanceTopTwentyMap = getTopTwentyOfJaccard(jaccardDistanceTopTwentyMap);
		}
        
        //System.out.println("jaccardDistanceTopTwentyMap:" + jaccardDistanceTopTwentyMap.toString());
        if(jaccardDistanceTopTwentyMap.size() < 1) {
        	int i = 0;
        	while(i<5) {
        	String movieURIpopula = dbAccess.selectMovieToRatingByPopxVarUser(user_id);	//parti da un film random
			jaccardDistanceTopTwentyMap.put(movieURIpopula, 1.0);
			i++;
        	}
        }
    	return jaccardDistanceTopTwentyMap;
    	
    }
	

	public static void main(String[] args) throws Exception {
		
		JaccardIndex jaccardIndex = new JaccardIndex();		
		Map<String, Double> jaccardDistanceMap = new HashMap<String, Double>();
		String user_id = "6";
		jaccardDistanceMap = jaccardIndex.getJaccardDistance(user_id);
		//System.out.println("jaccardDistanceMap size:" + jaccardDistanceMap.size());
		//System.out.println("jaccardDistanceMap:" + jaccardDistanceMap.toString());
		
//		Map<String, Double> jaccardIndexMap = new HashMap<String, Double>();
//		jaccardIndexMap = jaccardIndex.getJaccardIndex(user_id);
//		System.out.println("jaccardIndexMap size:" + jaccardIndexMap.size());
//		System.out.println("jaccardIndexMap:" + jaccardIndexMap.toString());
		
	}

}
