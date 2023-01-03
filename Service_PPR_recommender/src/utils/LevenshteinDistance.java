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

import database.AccessRecsysDB;

public class LevenshteinDistance {
	

	public Map<String, Integer> findDistanceUriFromName(String name) throws Exception{
		String fullName = putUnderscoreToSpaceInName(name);
		
		AccessRecsysDB dbAccess = new AccessRecsysDB();
		Map <String, Integer> levDistanceMap = new HashMap<String, Integer>();
		Map <String, Integer> levDistanceTopFiveMap = new HashMap<String, Integer>();
		
		Set<String> vertexUriSet = dbAccess.selectUriFromVertexTrailerSelection();
		
    	for (String uri : vertexUriSet) {
    		String SubUri = getSubNameFromUri(uri);
			int distanza = distance(fullName, SubUri);
			if (distanza <= 15) {
				levDistanceMap.put(uri, distanza);
			}			
		}
    	
    	levDistanceMap = sortByComparator(levDistanceMap, true);
    	//System.out.println("levDistanceMap:" + levDistanceMap.toString() );
    	
    	int k = 0;
    	for (String key : levDistanceMap.keySet()){
    		if (k < 5) {
    			Integer value = levDistanceMap.get(key);
    			System.out.println("" + name + " - " + key + " distanza: " + levDistanceMap.get(key));
    			levDistanceTopFiveMap.put(key, value);
    			k++;
    		}
    		else {
    			break;
			}    		
    	}
    	levDistanceTopFiveMap = sortByComparator(levDistanceTopFiveMap, true);
    
		return levDistanceTopFiveMap;
    	
	}
	
	private static String getSubNameFromUri(String uri){
		String subName = uri;
		if (uri.length() >= 28) {
			subName = uri.substring(28, uri.length());
			subName = subName.replaceAll("_", " ");
		}
		return subName;
	}
	
	public static String putUnderscoreToSpaceInName(String name){
		name = name.replaceAll("_", " ");
		//String uriString = "http://dbpedia.org/resource/"+name;
		return name;
	}
	 
    public static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
    
    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
 
    public static void main(String [] args) throws Exception {
    	LevenshteinDistance levDistance = new LevenshteinDistance();

    	Map <String, Integer> distanceMap = new HashMap<String, Integer>();
    	
    	String name = "woody";
    	
    	distanceMap = levDistance.findDistanceUriFromName(name);
    	System.out.println("distanceMap:" + distanceMap.toString() );
//    	String nameUri = createUriFromName(name);
//    	
//    	Set<String> vertexUriSet = new TreeSet<>();
//    	try {
//			vertexUriSet = dbAccess.selectUriFromVertexTrailerSelection();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	for (String uri : vertexUriSet) {
//			int distanza = distance(nameUri, uri);
//			if (distanza <= 15) {
//				distanceTreeMap.put(uri, distanza);
//			}			
//		}
//    	distanceTreeMap = sortByComparator(distanceTreeMap, true);
//    	int k = 0;
//    	for (String key : distanceTreeMap.keySet()){
//    		if (k < 5) {
//    		System.out.println("" + nameUri + " - " + key + " distanza: " + distanceTreeMap.get(key));
//    		//k++;
//    		}
//    		else {
//    			break;
//			}    		
    	}
			
	
}
