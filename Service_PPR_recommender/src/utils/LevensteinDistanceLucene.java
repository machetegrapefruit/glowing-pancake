package utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.search.spell.StringDistance;

import database.AccessRecsysDB;

public class LevensteinDistanceLucene {
	
	private static String getSubNameFromUri(String uri){
		String subName = uri;
		if (uri.length() >= 28) {
			subName = uri.substring(28, uri.length());
			subName = subName.replaceAll("_", " "); //sostituisci tutti gli underscore in spazi
		}
		return subName;
	}
	
	private static String putSpaceToUnderscoreAndUpcase(String name){
		String upName = name.substring(0, 1).toUpperCase() + name.substring(1);
		upName = upName.replaceAll("_", " "); //sostituisci tutti gli underscore in spazi
		//String lowName = name.replaceAll("_", " ");
		//lowName.toLowerCase();
		return upName;
	}	
    
    private static Map<String, Double> getTopTenOfDistance(Map<String, Double> levDistanceMap){
    	Map<String, Double> levDistanceTopTenMap = new HashMap<String, Double>();
    
    	levDistanceMap = sortByComparator(levDistanceMap, false);
    	
    	int k = 0;
    	for (String key : levDistanceMap.keySet()){
    		if (k < 10) {
    			Double value = levDistanceMap.get(key);
    			//System.out.println(key + " - distanza: " + levDistanceMap.get(key));
    			levDistanceTopTenMap.put(key, value);
    			k++;
    		}
    		else {
    			break;
			}    		
    	}
    	levDistanceTopTenMap = sortByComparator(levDistanceTopTenMap, false);
    	
		return levDistanceTopTenMap;
    	
    }
    
    
    private Map<String, Double> findLevDistanceUriFromNameLucene(String upName) throws Exception{
		
		AccessRecsysDB dbAccess = new AccessRecsysDB();
		Map <String, Double> levDistanceMap = new HashMap<String, Double>();
		
		Set<String> vertexUriSet = dbAccess.selectUriFromVertexTrailerSelection();
		StringDistance levDistance = new LevensteinDistance();
		//StringDistance sDistance = new NGramDistance();
		for (String uri : vertexUriSet) {
			String subUri = getSubNameFromUri(uri);
			float distanza = levDistance.getDistance(upName, subUri);
			if (distanza >= 0.5) {
				levDistanceMap.put(subUri, (double) distanza);
			}			
		}
		
		System.out.println("levDistanceMap:" + levDistanceMap.toString() );   
		return levDistanceMap;    	
	}

	public static Map<String, Double> sortByComparator(Map<String, Double> levDistanceMap, final boolean order){
	    List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(levDistanceMap.entrySet());
	
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

	public Map<String, Double> getLevDistance(String name) throws Exception{
    	
    	Map<String, Double> nameLevDistanceTopTenMap = new HashMap<String, Double>();
    	Map<String, Double> invertedNameLevDistanceTopTenMap = new HashMap<String, Double>(); 
    	
    	String upName = putSpaceToUnderscoreAndUpcase(name);
    	int count = upName.split("\\s+").length;    	

    	if (count == 2) {
    		//System.out.println(name + " - bigram: " + count);
    		String[] parts = upName.split("\\s+");
    		String part1 = parts[0]; 
        	String part2 = parts[1];
        	StringBuilder namePart = new StringBuilder();
   		 	namePart.append(part2);
   		 	namePart.append(" "); //stringhe separate da uno spazio
   		 	namePart.append(part1);
   		 	String invertedName = namePart.toString();
   		    nameLevDistanceTopTenMap = findLevDistanceUriFromNameLucene(upName);
   		    invertedNameLevDistanceTopTenMap = findLevDistanceUriFromNameLucene(invertedName);
   		    nameLevDistanceTopTenMap = getTopTenOfDistance(nameLevDistanceTopTenMap);
   		    invertedNameLevDistanceTopTenMap = getTopTenOfDistance(invertedNameLevDistanceTopTenMap);
   		    nameLevDistanceTopTenMap.putAll(invertedNameLevDistanceTopTenMap);
   		    nameLevDistanceTopTenMap = getTopTenOfDistance(nameLevDistanceTopTenMap);
		}
    	else{
    		//System.out.println(name + ": " + count);
    		nameLevDistanceTopTenMap = findLevDistanceUriFromNameLucene(upName);
    		nameLevDistanceTopTenMap = getTopTenOfDistance(nameLevDistanceTopTenMap);
    	}
    	
    	//System.out.println("nameLevDistanceTopTenMap:" + nameLevDistanceTopTenMap.toString() );   
		return nameLevDistanceTopTenMap;
    }
    
    
	public static void main(String [] args) throws Exception {
    	LevensteinDistanceLucene levDistance = new LevensteinDistanceLucene();

    	Map <String, Double> distanceMap = new HashMap<String, Double>();
    	
//		String name = "robert down";
//		distanceMap = levDistance.getLevDistance(name);
//		System.out.println(name + " - distanceMap:" + distanceMap.size() + " - distanceMap:" + distanceMap.toString() );
    	
    	String name1 = "iu grent";
    	distanceMap = levDistance.getLevDistance(name1);
    	System.out.println(name1 + " - distanceMap:" + distanceMap.size() + " - distanceMap:" + distanceMap.toString() );
    	
    	String name2 = "allen woody";
    	distanceMap = levDistance.getLevDistance(name2);
    	System.out.println(name2 + " - distanceMap:" + distanceMap.size() + "- distanceMap:" + distanceMap.toString() );
    	    		
    	}
			
	
}

