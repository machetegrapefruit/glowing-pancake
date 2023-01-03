package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Sets;

import database.AccessRecsysDB;
import edu.uci.ics.jung.algorithms.scoring.AbstractIterativeScorer;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import entity.Rating;
import entity.RequestStruct;
import graph.scorer.SimpleVertexTransformer;

/**
 * Class which represents the user-item-lod configuration
 */
public class AdaptiveSelectionUserItemPropertyDB extends RecGraph {    
    private ArrayListMultimap<String, Set<String>> trainingPosNeg;
    private ArrayListMultimap<String, Set<String>> trainingPosNegProperty;
    private Map<String, Set<String>> testSet;
    private Map<String, String> uriIdMap;
    private Map<String, Set<List<String>>> resourceAndPropertyListMultimap;

 
    //private AccessRecsysDB dbAccess = new AccessRecsysDB(); //prevedere di richiamare l'oggetto
    private PageRankWithPriors<String, String> priors;
     
    public AdaptiveSelectionUserItemPropertyDB(String user_id){
        try {
            generateGraph(new RequestStruct(user_id));
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }  
 
    @Override
    public void generateGraph(RequestStruct requestStruct) throws Exception{
        String user_id = (String) requestStruct.params.get(0);
        AccessRecsysDB dbAccess = new AccessRecsysDB();
        dbAccess.insertUser(user_id);
        trainingPosNeg = dbAccess.selectPosNegRatingForUserFromRatingsMovies(user_id);
        trainingPosNegProperty = dbAccess.selectPosNegRatingFromRatingsPropertiesForPageRank(user_id);
        testSet = dbAccess.selectTestSetForUserFromMoviesForPageRank(user_id);  //TODO: Nuovo schema

        Set<String> allItemsID = new TreeSet<>();  
         
        //il training set ha gi� tutti i film su cui creare un grafo,
        //per� quando si lavora con pochi nodi potrebbero mancare i film valutati dall'utente
        for (String userID : trainingPosNeg.keySet()) {     
            allItemsID.addAll(trainingPosNeg.get(userID).get(0)); //prende solo gli item valutati positivamente (get(0))    
            allItemsID.addAll(trainingPosNeg.get(userID).get(1));
        }        
            
        System.out.println("trainingPosNeg: "+trainingPosNeg.toString());
        System.out.println("trainingPosNegProperty: "+trainingPosNegProperty.toString());
        //System.out.println("testSet: "+testSet.toString()); 
        //System.out.println("allItemsID : " + allItemsID.toString());
        //System.out.println("allItem Size: "+allItemsID.size());
        
        for (Set<String> items : testSet.values()) {
            allItemsID.addAll(items);        
        }      
        
         
        uriIdMap = new HashMap<>();
        int vertexCounter = 1;
        double percent, oldPercent = 0;
        int itemsSize = allItemsID.size();
        
        //intPercent = (int) (percent + 0.5); //Adds .5 in order to round.
 
        System.out.println("Server running on " + itemsSize + " items.");        
        Set<String> negUriSet = new TreeSet<>();
        negUriSet = dbAccess.selectNegRatingFromRatingsMoviesAndPropertiesForPageRank(user_id);
        
        //crea un hashmap dove ad ogni film sono associate le proprietà
        System.out.println("Graph creation: ");
        resourceAndPropertyListMultimap = dbAccess.selectAllResourceAndPropertyFromDbpediaMoviesSelection(allItemsID); //TODO: Nuovo schema
        System.out.println("Get properties of:" + resourceAndPropertyListMultimap.size() + " items.");
        
       
        
        //creazione sezione grafo a cui si eliminano i nodi valutati negativamente
        for (String resourceURI : allItemsID){
        	//se non contiene una risorsa valutata negativamente
        	if (!negUriSet.contains(resourceURI)) { 
	            uriIdMap.put(resourceURI, resourceURI);
	            recGraph.addVertex(resourceURI);
                Set<List<String>> resProperties = Sets.newHashSet();
                resProperties = resourceAndPropertyListMultimap.get(resourceURI);
                long i = 1;
                if (resProperties != null) {
                	//Aggingi le proprieta' legate alla risorsa
                	//System.out.println("\nmovieUri:" +resourceURI + "resProperties size:" + resProperties.size());
                    for (List<String> list : resProperties) {
                        String predicate = list.get(1).toString();
                        String object = list.get(2).toString();
                      //se non contiene un nodo proprietà valutato negativamente
                        if (!negUriSet.contains(object)) {
                        	//System.out.println("-- " +predicate + " - " + object);
	                        recGraph.addVertex(object);
	                        recGraph.addEdge("I:" + resourceURI + "-prop" + i + ":" + predicate, resourceURI, object);
	                    }
                        i++;
                    }
    			}				
			}
            percent = (100*vertexCounter)/itemsSize;
            if(percent % 10 == 0 && oldPercent != percent){
                oldPercent = percent;
                System.out.print(percent +"%   ");}
            vertexCounter++;          
        }         
        System.out.println(""); 
        
        
/*        //creazione sezione grafo standard
        for (String resourceURI : allItemsID){
            uriIdMap.put(resourceURI, resourceURI);
            recGraph.addVertex(resourceURI);
            Set<List<String>> resProperties = Sets.newHashSet();
            resProperties = dbAccess.selectAllPropertyFromDbpediaMoviesSelection(resourceURI);            
            long i = 1;
            if (resProperties != null) {
            	//Aggingi le proprieta' legate alla risorsa
                for (List<String> list : resProperties) {
                    String predicate = list.get(1).toString();
                    String object = list.get(2).toString();
                    recGraph.addVertex(object);
                    recGraph.addEdge("I:" + resourceURI + "-prop" + i + ":" + predicate, resourceURI, object);
                    i++;
                }
			}
            percent = (100*vertexCounter)/itemsSize;
            if(percent % 10 == 0 && oldPercent != percent){
                oldPercent = percent;
                System.out.print(percent +"%   ");}
            vertexCounter++;          
        }         
*/
         
        //creazione sezione grafo per utente
        //Inserisce un collegamento tra il nodo uteme e il nodo item(film)
        for (String userID : trainingPosNeg.keySet()){    
        	int edgeCounter = 1;
            String user = "U:"+userID;
            recGraph.addVertex(user);
            //prende solo gli item valutati positivamente (get(0))        
            for (String resourceURI : trainingPosNeg.get(userID).get(0)){                       
                //inserisce un arco tra i due nodi: g.addEdge("e0", "v0", "v1");
                recGraph.addEdge("U:" + userID + "-" + "edge" + edgeCounter + ":" + resourceURI, "U:" + userID, resourceURI);   
                edgeCounter++;
             }
        }
        currLogger.info(String.format("Total number of vertex %s - Total number of edges %s", recGraph.getVertexCount(), recGraph.getEdgeCount()));
    }  
   

     
/*    private void addItemProperties(String resourceURI) throws Exception {
        //COLLO DI BOTTIGLIA NEL CARICARE LE PROPRIETA DAL DB
        Set<List<String>> resProperties = Sets.newHashSet();
        resProperties = dbAccess.selectAllPropertyFromPropertiesByMovie(resourceURI);       
        //System.out.println("resProperties: "+ resProperties);
        
        long i = 1;
        for (List<String> list : resProperties) {
            String predicate = list.get(1).toString();
            String object = list.get(2).toString();
            recGraph.addEdge("E:" + resourceURI + "-prop" + i + ":" + predicate, resourceURI, object);
            i++;
        }        
    }  */  
 
/*    @Override
    public Map<String, Set<Rating>> runPageRank(String user_id, RequestStruct requestParam) throws IOException {
    	currLogger.info("Start Page Rank...");
        Map<String, Set<Rating>> usersRecommendation = new HashMap<>();
        double massProb = (double) requestParam.params.get(0); // max proportion of positive items for user
        // compute recommendation for one users
		currLogger.info("Page rank for user: " + user_id);
        for (String userID : testSet.keySet()) {
            if(userID.equals(Integer.toString(user_id))){                   
                List<Set<String>> posNegativeRatings = trainingPosNeg.get(userID);
                List<Set<String>> testItems = testSet.get(userID);
                usersRecommendation.put(userID, profileUser(userID, posNegativeRatings.get(0), posNegativeRatings.get(1), testItems.get(0), massProb));
            }
        }         
        return usersRecommendation;         
    }*/
    
    
    @Override
    public Map<String, Set<Rating>> runPageRank(RequestStruct requestParam) {
        Map<String, Set<Rating>> usersRecommendation = new HashMap<>();

        double massProb = (double) requestParam.params.get(0); // max proportion of positive items for user

        // compute recommendation for all users
        for (String userID : testSet.keySet()) {
            currLogger.info("Page rank for user: " + userID);
            List<Set<String>> posNegativeRatings = trainingPosNeg.get(userID);
            Set<String> testItems = testSet.get(userID);
            usersRecommendation.put(userID, profileUser(userID, posNegativeRatings.get(0), posNegativeRatings.get(1), testItems, massProb));
        }
        return usersRecommendation;
    }
    
    //Esegui il pagerank per il singolo utente
    public Map<String, Set<Rating>> runPageRankForSingleUser(String user_id, RequestStruct requestParam) throws Exception {
    	currLogger.info("Start Page Rank for user: " + user_id);
        Map<String, Set<Rating>> usersRecommendation = new HashMap<>();
        double massProb = (double) requestParam.params.get(0); // max proportion of positive items for user
        String userID = user_id;                 
        List<Set<String>> posNegativeRatings = trainingPosNeg.get(userID);
        
        //aggiungo i nodi proprietà valutati positivamente o negativamente
        posNegativeRatings.get(0).addAll(trainingPosNegProperty.get(userID).get(0));
        posNegativeRatings.get(1).addAll(trainingPosNegProperty.get(userID).get(1));        
        
        Set<String> testItems = testSet.get(userID);
        usersRecommendation.put(userID, profileUser(userID, posNegativeRatings.get(0), posNegativeRatings.get(1), testItems, massProb));
        
        return usersRecommendation;         
    }
 
 
    private Set<Rating> profileUser(String userID, Set<String> trainingPos, Set<String> trainingNeg, Set<String> testItems, double massProb) {
        Set<Rating> allRecommendation = new TreeSet<>();
        
        //trasformer rappresenta la priorità a priori di ogni nodo
        SimpleVertexTransformer transformer = new SimpleVertexTransformer(trainingPos, trainingNeg, this.recGraph.getVertexCount(), massProb, uriIdMap);
        priors = new PageRankWithPriors<>(this.recGraph, transformer, 0.15);
        
        priors.setMaxIterations(25);
        priors.evaluate();        
 
        for (String currItemID : testItems) {           
            String resourceURI = uriIdMap.get(currItemID);           
            if (resourceURI != null){                 
                //Recupera lo score del nodo
                //String scoreString = String.valueOf(priors.getVertexScore(resourceURI));
                String scoreString = this.getScoreResourceURI(resourceURI, priors);
                allRecommendation.add(new Rating(resourceURI, scoreString));
            }
        } 
        return allRecommendation;
    }
  
    //credo si possano eliminare get e set -- mmmn
    public PageRankWithPriors<String, String> getUserPageRankWithPriors() {
        return priors;
    }
 
    /*public void setUserPageRankWithPriors(PageRankWithPriors<String, String> priors) {
    	AdaptiveSelectionUserItemPropertyDB.priors = priors;
    }*/
 
    public String getScoreResourceURI (String URI, AbstractIterativeScorer<String, String, Double> priors){
        String score = String.valueOf(priors.getVertexScore(URI));
         
        return score;
    }
    
    //prendi i primi cinque film da raccomandare
    private List<String> getTopFiveFromRating(Set<Rating> set, String user_id) throws NumberFormatException, Exception{       
    	List<String> topFiveitems = new ArrayList<String>();
    	Iterator<Rating> setIterator = set.iterator();         

        String movie_uri = null;
        
        int i=1;
        while (i<=5 ) {
            setIterator.hasNext();
            Rating rating = (Rating) setIterator.next();  
            movie_uri = rating.getItemID();
            topFiveitems.add(movie_uri);           
            i++;            
        }
        
		return topFiveitems;
    }
 
    public static void main(String[] args) throws Exception {       
        long meanTimeElapsed = 0, startTime;        
        startTime = System.nanoTime();
         
        String user_id = "6";

        AdaptiveSelectionUserItemPropertyDB graph = new AdaptiveSelectionUserItemPropertyDB(user_id);
         
        meanTimeElapsed += (System.nanoTime() - startTime);
        double second = (double)meanTimeElapsed / 1000000000.0;          
        currLogger.info("Runtime Graph creation: " + second + "''");
	        meanTimeElapsed = 0;        
	        startTime = System.nanoTime();
	         
	        Map<String, Set<Rating>> ratings = graph.runPageRankForSingleUser(user_id, new RequestStruct(0.85));
	          
	        meanTimeElapsed += (System.nanoTime() - startTime);
	        second = (double)meanTimeElapsed / 1000000000.0;
        currLogger.info("...End - Runtime PageRank: " + second + "''");
        
        Set<Rating> rating = ratings.get(user_id);
        List<String> UriItemsRacc = graph.getTopFiveFromRating(rating,user_id);
        System.out.println("\nUriItemsRacc: " + UriItemsRacc);
        
      //Test Explanation lavoro Dibiase  
        //        ExplanationService userGraph = new ExplanationService();
        //        ValidazioneFilmDebug dati = new ValidazioneFilmDebug();
        //        userGraph.mappingIdUri(uriIdMap);
        //        userGraph.getExplanation(graph.getGraph(), dati.getNameDirLog(), "U:6", UriItemsRacc, 0, "", "");          
        //		  String natural_language_explanation = userGraph.getExplanationString(graph.getGraph(), dati.getNameDirLog(), "U:6", UriItemsRacc, 0, "normalizzazione", "idf");      
        //		System.out.println("\nnatural_language_explanation: " + natural_language_explanation);
		//graph.createAndShowGUI();
    }
}

