package utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.WordUtils;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.StringDistance;

import database.AccessRecsysDB;


/**
 * @author Francesco Baccaro
 */
public class DidYouMeanBaccaro {
	private AccessRecsysDB dbAccess = new AccessRecsysDB();
	private TreeMap<String, String> vertexUriAndNameSet = new TreeMap<String, String>();
	private TreeMap<String, String> searchUriAndNameSet = new TreeMap<String, String>();

	private StringDistance levDistance = new LevensteinDistance();

	public DidYouMeanBaccaro(){
		try {			
			vertexUriAndNameSet = dbAccess.selectUriAndNameFromVertexTrailerSelection();
		} 
		catch (Exception e) {
			System.err.println("Error in DidYouMeanBaccaro");
			e.printStackTrace();
		}
	}
	

	
	public static Map<String, Double> sortMapByValueComparasion(Map<String, Double> levDistanceMap, final boolean order){
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

	private static String putSpaceToUnderscoreAndUpcase(String text){
		String upText = text.replaceAll("_", " "); //sostituisci tutti gli underscore in spazi
		upText = WordUtils.capitalize(upText);

		return upText;
	}	
	
	public Map<String, Double> createUriKeyDistanceValueFromUriNameKey(Map<String, Double> textLevDistanceTopTenMap){
		Map<String, Double> uriDistancenMap = new HashMap<String, Double>();
		
		for (String key : textLevDistanceTopTenMap.keySet()){
			String[] keys = key.split(",");
			
			String uri = keys[0];
			Double value = textLevDistanceTopTenMap.get(key);
			//System.out.println("createUriKey:" + uri + " - " + value);
			//se l'uri non è già una chiava inseriscila
			if (!uriDistancenMap.containsKey(uri)) {
				
				uriDistancenMap.put(uri, value);
            }//altrimenti se è già una chiava inserisci in value il valore più alto
			else if (value > uriDistancenMap.get(uri)) {

				uriDistancenMap.put(uri, value);
			}else {
				//System.out.println("OUT createUriKey:" + uri + " - " + value);
			}
		}
		
		uriDistancenMap = sortMapByValueComparasion(uriDistancenMap, false);
		return uriDistancenMap;
	}
    
    private static Map<String, Double> getTopTenOfDistance(Map<String, Double> levDistanceMap){
    	Map<String, Double> levDistanceTopTenMap = new HashMap<String, Double>();
    	
    	levDistanceMap = sortMapByValueComparasion(levDistanceMap, false);
    	int k = 0;
    	for (String key : levDistanceMap.keySet()){
    		Double value = levDistanceMap.get(key);
    		//Dai un valore aggiunto al testo che coincide al name di un nodo
    		if (value.equals(2.0)) {  			
    			levDistanceTopTenMap.put(key, value);
    			k++;
    		}
    		else if (k < 10) {
    			levDistanceTopTenMap.put(key, value);
    			k++;
			}
    	}

    	levDistanceTopTenMap = sortMapByValueComparasion(levDistanceTopTenMap, false);

		return levDistanceTopTenMap;	
    }
    
    //Calcola la distanza del testo dai nomi dei vertici estratti dalla ricerca fulltext
    private Map<String, Double> findLevDistanceForFullTextFromMap(String text, TreeMap<String, String> searchTreeMap) throws Exception{
		
		Map <String, Double> levDistanceMap = new HashMap<String, Double>();
		
		//StringDistance levDistance = new LevensteinDistance();
		//StringDistance sDistance = new NGramDistance();
		for(String uri: searchTreeMap.keySet()){	
			String nameParts = searchTreeMap.get(uri);
			float distanza = levDistance.getDistance(text, nameParts);
			if (distanza > 0.5) {
				levDistanceMap.put(uri, (double) distanza);
				//Dai un valore aggiunto al testo che coincide al name di un nodo
				if (distanza == 1.0) {
					distanza++;
					levDistanceMap.put(uri, (double) distanza);
				}
			}
		}
		
		//System.out.println("findLevDistanceForFullTextFromMap:" + levDistanceMap.toString() );   
		return levDistanceMap;    	
	}
    
  //Calcola la distanza del testo dai nomi dei vertici tramite
	private Map<String, Double> findLevDistanceForFullName(String upFullText) throws Exception{
		
		Map <String, Double> levDistanceMap = new HashMap<String, Double>();
		
		//StringDistance levDistance = new LevensteinDistance();
		//StringDistance sDistance = new NGramDistance();
		for(String uri: vertexUriAndNameSet.keySet()){	
			String name = vertexUriAndNameSet.get(uri);
			float distanza = levDistance.getDistance(upFullText, name);
			if (distanza > 0.5) {
				levDistanceMap.put(uri, (double) distanza);
				//Dai un valore aggiunto al testo che coincide al name di un nodo
				if (distanza == 1.0) {
					distanza++;
					levDistanceMap.put(uri, (double) distanza);
				}
			}
			
		}
		levDistanceMap = sortMapByValueComparasion(levDistanceMap, false);
		
		//System.out.println("findLevDistanceForFullName:" + levDistanceMap.toString() );   
		return levDistanceMap;    	
	}
	
	//Calcola la distanza del testo dal nome e da ogni parola componente i nomi dei vertici
	private Map<String, Double> findLevDistanceForSubAndFullName(String upText) throws Exception{
		
		//StringDistance levDistance = new LevensteinDistance();
		//StringDistance sDistance = new NGramDistance();
		
		Map <String, Double> levDistanceMap = new HashMap<String, Double>();
		
		for(String uri: vertexUriAndNameSet.keySet()){	
			String nameParts = vertexUriAndNameSet.get(uri);
			
			//Calcola la distanza tra il testo e l'intero nome
			float distanza = levDistance.getDistance(upText, nameParts);
			if (distanza > 0.5) {
				levDistanceMap.put(uri, (double) distanza);
			}
			//Calcola la distanza tra il testo e le singole parti del nome
    		String[] parts = nameParts.split("\\s+");
			for(int i =0; i < parts.length ; i++){
				//System.out.println(parts[i]);
				String subString = parts[i];
				distanza = levDistance.getDistance(upText, subString);
				if (distanza > 0.5) {
					//crea una key unica ad hoc che conserva l'uri che verrà estratto
					String key = uri +"," + subString; 
					levDistanceMap.put(key, (double) distanza);
				}		
			}			  
			
		}			
		
		//System.out.println("findLevDistanceForSubName:" + levDistanceMap.toString() );   
		return levDistanceMap;    	
	}
    
    
	
	//Calcola la distanza del testo dai nomi dei vertici tramite NGram
	private Map<String, Double> findNGramDistanceForFullName(String upFullText) throws Exception{
		
		Map <String, Double> levDistanceMap = new HashMap<String, Double>();
		
		StringDistance nGramDistance = new NGramDistance();
		for(String uri: vertexUriAndNameSet.keySet()){	
			String name = vertexUriAndNameSet.get(uri);
			float distanza = nGramDistance.getDistance(upFullText, name);
			//System.out.println("findNGramDistanceForFullName:" + uri.toString() +" - " + distanza );
			if (distanza > 0.4) {
				levDistanceMap.put(uri, (double) distanza);
				//Dai un valore aggiunto al testo che coincide al name di un nodo
				if (distanza == 1.0) {
					distanza++;
					levDistanceMap.put(uri, (double) distanza);
				}
			}
			
		}
		
		levDistanceMap = sortMapByValueComparasion(levDistanceMap, false);
		
		//System.out.println("findNGramDistanceForFullName - size:" + levDistanceMap.size() + " - Map" + levDistanceMap.toString() );   
		return levDistanceMap;    	
	}

	/*
	 * Prendi il testo scritto dell'utente
	 * spittalo
	 * abbiamo una lista di stringhe
	 * per ogni stringa del testo confronto con il name dei vertici
	 * prendo name spitto, array di liste di parole
	 * Calcola la distanza
	 *...
	 */
	public Map<String, Double> getDistanceMonoGram(String text) throws Exception{
		Map<String, Double> uriDistancenMap = new HashMap<String, Double>();
		Map<String, Double> fullTextDistancenMap = new HashMap<String, Double>();
		
		String upFullText = putSpaceToUnderscoreAndUpcase(text);
		
		//Fai una ricerca fulltext nel db e calcola la distanza se ci sono risultati
		searchUriAndNameSet = dbAccess.searchUriAndNameFromVertexTrailerSelectionByText(upFullText);
		
		if (searchUriAndNameSet != null && !searchUriAndNameSet.isEmpty()){
			fullTextDistancenMap = findLevDistanceForFullTextFromMap(upFullText, searchUriAndNameSet);
		}
		
		//Trova la distanza del testo dal nome e da ogni parola componente i nomi dei vertici
		uriDistancenMap = findLevDistanceForSubAndFullName(upFullText);
		uriDistancenMap = getTopTenOfDistance(uriDistancenMap);
		
		//Aggiungi la distanza dalla ricerca full text
		uriDistancenMap.putAll(fullTextDistancenMap);
		uriDistancenMap = createUriKeyDistanceValueFromUriNameKey(uriDistancenMap);
		
		//System.out.println("getDistanceMonoGram - size:" + uriDistancenMap.size() + " - Map:" + uriDistancenMap.toString());
		return uriDistancenMap;
	}
	
	public Map<String, Double> getDistanceBiGram(String text) throws Exception{
		Map<String, Double> uriDistancenMap = new HashMap<String, Double>();
		Map<String, Double> fullTextDistancenMap = new HashMap<String, Double>();
		
		Map<String, Double> nameLevDistanceTopTenMap = new HashMap<String, Double>();
    	Map<String, Double> invertedNameLevDistanceTopTenMap = new HashMap<String, Double>(); 
		
		String upFullText = putSpaceToUnderscoreAndUpcase(text);
		
		//Fai una ricerca fulltext nel db e calcola la distanza se ci sono risultati
		searchUriAndNameSet = dbAccess.searchUriAndNameFromVertexTrailerSelectionByText(upFullText);
		if (searchUriAndNameSet != null && !searchUriAndNameSet.isEmpty()){
			fullTextDistancenMap = findLevDistanceForFullTextFromMap(upFullText, searchUriAndNameSet);
		}
		//uriDistancenMap = findLevDistanceForSubAndFullName(upFullText);
		//uriDistancenMap = getTopTenOfDistance(uriDistancenMap);
		    	
    	int count = upFullText.split("\\s+").length;    	

    	//Se è un bigramma fa una ricerca invertendo le parole del testo e confrontanto le distanze
    	if (count == 2) {
    		//System.out.println(name + " - bigram: " + count);
    		String[] parts = upFullText.split("\\s+");
    		String part1 = parts[0]; 
        	String part2 = parts[1];
        	StringBuilder namePart = new StringBuilder();
   		 	namePart.append(part2);
   		 	namePart.append(" "); //stringhe separate da uno spazio
   		 	namePart.append(part1);
   		 	String invertedName = namePart.toString();
   		    nameLevDistanceTopTenMap = findLevDistanceForFullName(upFullText);
   		    invertedNameLevDistanceTopTenMap = findLevDistanceForFullName(invertedName);
   		    nameLevDistanceTopTenMap = getTopTenOfDistance(nameLevDistanceTopTenMap);
   		    invertedNameLevDistanceTopTenMap = getTopTenOfDistance(invertedNameLevDistanceTopTenMap);
   		    nameLevDistanceTopTenMap.putAll(invertedNameLevDistanceTopTenMap);
   		    uriDistancenMap = getTopTenOfDistance(nameLevDistanceTopTenMap);
		}
    	else{
    		nameLevDistanceTopTenMap =  findLevDistanceForSubAndFullName(upFullText);
    		uriDistancenMap = getTopTenOfDistance(nameLevDistanceTopTenMap);
    	} 
    	
    	//Aggiungi la distanza dalla ricerca full text
    	uriDistancenMap.putAll(fullTextDistancenMap);
		uriDistancenMap = createUriKeyDistanceValueFromUriNameKey(uriDistancenMap);
		
		//System.out.println("getDistanceBiGram - size:" + uriDistancenMap.size() + " - Map:" + uriDistancenMap.toString());
		return uriDistancenMap;
	}
	
	public Map<String, Double> getDistanceKGram(String text) throws Exception{
		Map<String, Double> uriDistancenMap = new HashMap<String, Double>();
		Map<String, Double> fullTextDistancenMap = new HashMap<String, Double>();
		Map<String, Double> tempTextDistanceMap = new HashMap<String, Double>();
		
		String upFullText = putSpaceToUnderscoreAndUpcase(text);
		
		//Fai una ricerca fulltext nel db e calcola la distanza se ci sono risultati
		searchUriAndNameSet = dbAccess.searchUriAndNameFromVertexTrailerSelectionByText(upFullText);
		if (searchUriAndNameSet != null && !searchUriAndNameSet.isEmpty()){
			fullTextDistancenMap = findLevDistanceForFullTextFromMap(upFullText, searchUriAndNameSet);
		}
		//uriDistancenMap = findLevDistanceForSubAndFullName(upFullText);
		//uriDistancenMap = getTopTenOfDistance(uriDistancenMap);
		
		//Lavora sulle varie parti del testo
/*		String[] textParts = upFullText.split("\\s+");
    	if (textParts.length > 1) {
    		String firstWord = textParts[0];
    		for(int i = 1; i < textParts.length ; i++){
    			//Lavora sul testo a bigrammi fissando la prima parola
    			tempTextDistanceMap.clear();
    			String subText = textParts[i];
    			StringBuilder wordsBuilder = new StringBuilder();
    			wordsBuilder.append(firstWord);
    			wordsBuilder.append(" "); //stringhe separate da uno spazio
    			wordsBuilder.append(subText);
       		 	String words = wordsBuilder.toString();
       		 	tempTextDistanceMap = getDistanceBiGram(words);
    			uriDistancenMap.putAll(tempTextDistanceMap);
    			uriDistancenMap = getTopTenOfDistance(uriDistancenMap);
    		}
    	}*/

		
		//Aggiungi la distanza NGram considerando l'intero test con distanza > 0.4
		tempTextDistanceMap =  findNGramDistanceForFullName(upFullText);
    	tempTextDistanceMap = getTopTenOfDistance(tempTextDistanceMap);
    	uriDistancenMap.putAll(tempTextDistanceMap);
		
		//Aggiungi la ricerca ricavata dal db
		uriDistancenMap.putAll(fullTextDistancenMap);
		uriDistancenMap = createUriKeyDistanceValueFromUriNameKey(uriDistancenMap);
		
		//System.out.println("getDistanceKGram - size:" + uriDistancenMap.size() + " - Map:" + uriDistancenMap.toString());
		return uriDistancenMap;
	}
	
	/*
	 * Prendi il testo scritto dell'utente
	 * spittalo
	 * abbiamo una lista di stringhe
	 * per ogni stringa del testo confronto con name
	 * prendo name spitto, array di liste
	 * Calcola la distanza
	 * Considera 3 casi...
	 */
	
	public Map<String, Double> getDistance(String text) throws Exception{
    	
		Map<String, Double> uriDistancenMap = new HashMap<String, Double>();
    	
    	String upFullText = putSpaceToUnderscoreAndUpcase(text);
    	int count = upFullText.split("\\s+").length;    	

    	if (count == 1) {
    		uriDistancenMap = getDistanceMonoGram(upFullText);
    	}
    	else if (count == 2) {
    		uriDistancenMap = getDistanceBiGram(upFullText);
		}
    	else {
    		uriDistancenMap = getDistanceKGram(upFullText);
		}
    	
    	return uriDistancenMap;
    }	
	
	
	public static void main(String [] args) throws Exception {
		DidYouMeanBaccaro didYouMean = new DidYouMeanBaccaro();

    	Map <String, Double> distanceMap = new HashMap<String, Double>();  
    	
    	String text = "banfi";
    	//distanceMap = didYouMean.getDistanceMonoGram(text);
    	distanceMap = didYouMean.getDistance(text);
    	System.out.println(text + " - size:" + distanceMap.size() + " - distanceMap:" + distanceMap.toString() +"\n");
    	

    	text = "romantic drama film";
    	distanceMap = didYouMean.getDistance(text);
    	System.out.println(text + " - size:" + distanceMap.size() + " - distanceMap:" + distanceMap.toString() +"\n");
    	
    	text = "film drama thriller";
    	distanceMap = didYouMean.getDistance(text);
    	System.out.println(text + " - size:" + distanceMap.size() + " - distanceMap:" + distanceMap.toString() +"\n");
    	
    	text = "allen woody";
    	distanceMap = didYouMean.getDistance(text);
    	System.out.println(text + " - size:" + distanceMap.size() + " - distanceMap:" + distanceMap.toString() +"\n");
    	
    	text = "woody";
    	distanceMap = didYouMean.getDistance(text);
    	System.out.println(text + " - size:" + distanceMap.size() + " - distanceMap:" + distanceMap.toString() +"\n");
    	
    	text = "spiderman";
    	distanceMap = didYouMean.getDistance(text);
    	System.out.println(text + " - size:" + distanceMap.size() + " - distanceMap:" + distanceMap.toString() +"\n");
    	

    	
    	text = "iu grent";
    	distanceMap = didYouMean.getDistance(text);
    	System.out.println(text + " - size:" + distanceMap.size() + " - distanceMap:" + distanceMap.toString() +"\n");
    	
    	text = "tarantino qention";
    	distanceMap = didYouMean.getDistance(text);
    	System.out.println(text + " - size:" + distanceMap.size() + " - distanceMap:" + distanceMap.toString() +"\n");
    	
    	text = "qentin";
    	distanceMap = didYouMean.getDistance(text);
    	System.out.println(text + " - size:" + distanceMap.size() + " - distanceMap:" + distanceMap.toString() +"\n");
    	
    	}

}
