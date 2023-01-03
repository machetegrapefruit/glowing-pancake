package graph;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import com.google.common.collect.ArrayListMultimap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import configuration.Configuration;
import database.AccessRecsysDB;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.Graph;
import entity.Pair;
import entity.Rating;
import entity.RequestStruct;
import functions.ContentBasedServiceSingleton;
import functions.ProfileService;
import functions.ResponseService;
import functions.Structured_Pref;
import test.EntityRating;
import test.LogMessage;
import test.PropertyRating;
import test.QuestionnaireAnswer;
import test.RecEntityRating;
import utils.DidYouMeanBaccaro;
import utils.JaccardIndex;
import utils.PropertyFilter;

/**
 * author: Francesco Baccaro
 */
public class AdaptiveSelectionController extends HttpServlet {
    protected static Logger currLogger = Logger.getLogger(AdaptiveSelectionController.class.getName());

    // private List<List<String>> itemLikePropertyList;
    // private List<List<String>> itemRecPropertyList;
    // private List<List<String>> itemCommonPropertyList;

    // private Map<String, List<String>> itemToPropertyListMap;
    // private Map<String, List<String>> propertyToItemListMap;

    private static Set<Rating> rating;

    private AccessRecsysDB dbAccess = new AccessRecsysDB();
    // private AccessRecsysDB dbAccess;

    private static AdaptiveSelectionUserItemPropertyDB graph = null;

    public AdaptiveSelectionController() {
        // dbAccess = new AccessRecsysDB();
        // List<List<String>> itemLikePropertyList = new ArrayList<List<String>>();
        // List<List<String>> itemRecPropertyList = new ArrayList<List<String>>();
        // List<List<String>>itemCommonPropertyList = new ArrayList<List<String>>();
    }

    public AdaptiveSelectionUserItemPropertyDB getGraph() {
        return graph;
    }

    public void getUserRaccomandations(String user_id, List<PropertyFilter> propertyFilters) throws Exception {
        Configuration configuration = Configuration.getDefaultConfiguration();
        String algoritmType = configuration.getRecsysAlgorithm();
        if (algoritmType.equals("contentBased")) {
            this.getRaccomandationsContentBased(user_id);
        } else {
            this.createGraphAndRunPageRank(user_id, propertyFilters);
        }
    }

    public void getRaccomandationsContentBased(String user_id) throws Exception {
        String json_pre = "";
        ContentBasedServiceSingleton contentBasedServiceSingleton = ContentBasedServiceSingleton.getInstance();
        if (contentBasedServiceSingleton.get_structured()) {
            ProfileService profileService = new ProfileService();
            JsonArray userProfile = profileService.getUserProfile(user_id);
            //Prendi tutte le preferenze dell'utente
            ArrayList<String> prefMovies = new ArrayList<>();
            ArrayList<String> prefProperty = new ArrayList<>();
            ArrayList<String> removeMovie = new ArrayList<>();
            ArrayList<String> negEntity = new ArrayList<>();
            for (int i = 0; i < userProfile.size(); i++) {
                JsonObject elem = userProfile.get(i).getAsJsonObject();
                System.out.println(elem);
                if (dbAccess.selectMovie(elem.get("name").getAsString()) != null) {
                    if (elem.get("rating").getAsInt() > 0){
                        prefMovies.add(elem.get("name").getAsString());
                    }
                    else
                    {
                        removeMovie.add(elem.get("name").getAsString());
                    }
                }
                else {
                    String type = elem.get("typeLabel").getAsString();
                    if (type.equals("director") || type.equals("actor") || type.equals("genre")) {
                        if (elem.get("rating").getAsInt() > 0){
                            prefProperty.add(elem.get("name").getAsString());
                        }
                        else
                        {
                            negEntity.add(elem.get("name").getAsString());
                        }
                    }
                }
            }
            Structured_Pref structured_pref = new Structured_Pref(prefMovies, prefProperty, removeMovie, negEntity);
            Gson gson = new Gson();
            json_pre = gson.toJson(structured_pref);
        }
        else {
            //preferenze non struttarate
            //		ArrayList<String> message_list = new ArrayList<>();
            //		ArrayList<String> recognised_entity = new ArrayList<>();
            //		AdaptiveSelectionController asController = new AdaptiveSelectionController();
            //		List<LogMessage> messagesList = asController.getLogMessages(new Date().toInstant().toEpochMilli());
            //		for (int i = 0; i < messagesList.size(); i++){
            //			LogMessage message = messagesList.get(i);
            //			if (message.getUserID().equals(user_id)){
            //				System.out.println(message.getIntent()+ " "+ message.getMessage()+ " "+ message.getRecognizedObjects());
            //			}
            //		}
        }

        HashMap<Integer, String> recommendations_map = new HashMap<>();
        recommendations_map = contentBasedServiceSingleton.get_sussestions(json_pre);
        List<String> recommendations = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : recommendations_map.entrySet()) {
            String movie_id = entry.getValue();
            recommendations.add(movie_id);
        }
        this.insertTopFiveScoresRecEntitiesFromRating(recommendations_map, user_id);
    }

    public void createGraphAndRunPageRank(String user_id, List<PropertyFilter> propertyFilters) throws Exception {
        long meanTimeElapsed = 0, startTime;
        startTime = System.nanoTime();

        graph = new AdaptiveSelectionUserItemPropertyDB(user_id);

        meanTimeElapsed += (System.nanoTime() - startTime);
        double second = (double) meanTimeElapsed / 1000000000.0;
        currLogger.info("Graph create in: " + second + "''");
        meanTimeElapsed = 0;
        startTime = System.nanoTime();
        Map<String, Set<Rating>> ratings = graph.runPageRankForSingleUser(user_id, new RequestStruct(0.85));
        meanTimeElapsed += (System.nanoTime() - startTime);
        second = (double) meanTimeElapsed / 1000000000.0;
        // currLogger.info("\nRuntime PageRank: " + second + "''");

        currLogger.info("PageRank done in " + second + "'' - Stored scores into database in progress..");
        rating = ratings.get(user_id);
        dbAccess.insertUser(user_id);
        this.insertTopFiveScoresRecEntitiesFromRating(rating, graph.getUserPageRankWithPriors(), graph.getGraph(),
                user_id, propertyFilters);
        this.insertScoresUserEntitiesFromPageRank(graph.getUserPageRankWithPriors(), graph.getGraph(), user_id);
        this.insertScoresUserPropertiesFromPageRank(graph.getUserPageRankWithPriors(), graph.getGraph(), user_id);
        this.putLastChange(user_id, "pagerank");
        currLogger.info("Database updated!");
    }
    

    private void insertTopFiveScoresRecEntitiesFromRating(Set<Rating> ratingSet,
                                                          PageRankWithPriors<String, String> userPageRankWithPriors, Graph<String, String> userGraph, String user_id,
                                                          List<PropertyFilter> propertyFilters) throws NumberFormatException, Exception {
        Iterator<Rating> setIterator = ratingSet.iterator();
        dbAccess.deleteAllScoreByUserFromScoresRecMovies(user_id);

        String itemURI = null;
        String movieURI = null;
        String moviePropertyTypeURI = "entity";
        String movieScore = null;
        String[] propTypes = Configuration.getDefaultConfiguration().getPropertyTypes();
        int recListSize = Configuration.getDefaultConfiguration().getRecListSize();

        int i = 1;
        while (i <= recListSize && setIterator.hasNext()) {
            setIterator.hasNext();
            Rating rating = (Rating) setIterator.next();
            itemURI = rating.getItemID();
            // System.out.println("k:" + k + " - " + itemURI);
            movieScore = rating.getRating();
            movieURI = dbAccess.selectMovie(itemURI);
            if (movieURI != null) {
                List<List<String>> PropertyList = this.getAllEntityProperties(itemURI, propTypes);

                if (satisfiesFilter(PropertyList, propertyFilters)) {
                    // Se il film soddisfa gli eventuali filtri, viene aggiunto alle raccomandazioni
                    // DA ottimizzare per ridurre i tempi delle insert
                    dbAccess.insertScoresRecMovies(user_id, movieURI, moviePropertyTypeURI, movieURI,
                            Double.parseDouble(movieScore));

                    for (List<String> list : PropertyList) {
                        String propertyTypeURI = list.get(1);
                        String propertyURI = list.get(2);
                        if (userGraph.containsVertex(propertyURI) && movieURI != null) {
                            Double propertyScore = userPageRankWithPriors.getVertexScore(propertyURI);
                            dbAccess.insertScoresRecMovies(user_id, movieURI, propertyTypeURI, propertyURI,
                                    propertyScore);
                        }
                    }
                    i++;
                }
            }
        }

        // Qui prendiamo lo score di tutti i film raccomandabili
        /*
         * for (Rating rating : ratingSet) { itemURI = rating.getItemID(); score =
         * rating.getRating(); movieURI = dbAccess.selectMovie(itemURI); if (movieURI !=
         * null) { //System.out.println("" + itemURI); dbAccess.insertScoresNew(user_id,
         * movieURI, propertyTypeURI, movieURI, Double.parseDouble(score)); } else {
         * System.out.println("N0 - " + itemURI); } }
         */
    }

    private void insertTopFiveScoresRecEntitiesFromRating(HashMap<Integer, String> map_rec, String user_id) throws Exception {
        dbAccess.deleteAllScoreByUserFromScoresRecMovies(user_id);
        String movieURI = null;
        String moviePropertyTypeURI = "entity";
        int i = map_rec.size();
        for (Map.Entry<Integer, String> entry : map_rec.entrySet()) {
            movieURI = dbAccess.selectMovie(entry.getValue());
            dbAccess.insertScoresRecMovies(user_id, movieURI, moviePropertyTypeURI, movieURI,
                    Double.parseDouble(String.valueOf(i)));
            i -= 1;
        }
    }

    private boolean satisfiesFilter(List<List<String>> properties, List<PropertyFilter> propertyFilters) {
        if (propertyFilters == null) {
            return true;
        } else {
            boolean found = false;
            int i = 0;
            PropertyFilter currentFilter = propertyFilters.get(i);
            while (!found && i < propertyFilters.size()) {
                int j = 0;
                while (!found && j < properties.size()) {
                    List<String> property = properties.get(j);
                    if (property.get(1).equals(currentFilter.getPropertyType())
                            && property.get(2).equals(currentFilter.getPropertyValue())) {
                        found = true;
                    }
                    j++;
                }
                i++;
            }
            return found;
        }
    }

    private void insertScoresUserEntitiesFromPageRank(PageRankWithPriors<String, String> userPageRankWithPriors,
                                                      Graph<String, String> userGraph, String user_id) throws NumberFormatException, Exception {

        dbAccess.deleteAllScoreByUserFromScoresUserMovies(user_id);

        ArrayListMultimap<String, Set<String>> trainingPosNeg = dbAccess
                .selectPosNegRatingForUserFromRatingsMovies(user_id);
        Set<String> allItemsID = new TreeSet<>();

        String itemPropertyTypeURI = "entity";

        for (String userID : trainingPosNeg.keySet()) {
            allItemsID.addAll(trainingPosNeg.get(userID).get(0)); // prende solo gli item valutati positivamente
            // (get(0))
        }

        String[] propTypes = Configuration.getDefaultConfiguration().getPropertyTypes();
        for (String itemURI : allItemsID) {
            List<List<String>> PropertyList = getAllEntityProperties(itemURI, propTypes);
            Double itemScore = userPageRankWithPriors.getVertexScore(itemURI);
            dbAccess.insertScoresUserMovies(user_id, itemURI, itemPropertyTypeURI, itemURI, itemScore);

            for (List<String> list : PropertyList) {
                String movieURI = dbAccess.selectMovie(itemURI);
                String propertyTypeURI = list.get(1);
                String propertyURI = list.get(2);
                if (userGraph.containsVertex(propertyURI) && movieURI != null) {
                    Double propertyScore = userPageRankWithPriors.getVertexScore(propertyURI);
                    dbAccess.insertScoresUserMovies(user_id, movieURI, propertyTypeURI, propertyURI, propertyScore);
                }
            }
        }
    }

    private void insertScoresUserPropertiesFromPageRank(PageRankWithPriors<String, String> userPageRankWithPriors,
                                                        Graph<String, String> userGraph, String user_id) throws NumberFormatException, Exception {

        dbAccess.deleteAllScoreByUserFromScoresUserProperties(user_id);

        List<List<String>> posPropertyList = dbAccess.selectPosPropertyListForUserFromRatingsProperties(user_id);

        for (List<String> list : posPropertyList) {
            String propertyURI = list.get(0);
            String propertyTypeURI = list.get(1);
            if (userGraph.containsVertex(propertyURI)) {
                System.out.println("Graph contains vertex " + propertyURI + "!");
                Double propertyScore = userPageRankWithPriors.getVertexScore(propertyURI);
                dbAccess.insertScoresUserProperties(user_id, propertyTypeURI, propertyURI, propertyScore);
                // System.out.print("user_id: " + user_id);
                // System.out.print(" - propertyTypeURI: " + propertyTypeURI);
                // System.out.print(" - propertyURI: " + propertyURI);
                // System.out.println(" - score: " + propertyScore.toString());
            }
        }

    }

    // Inserisci i nodi film e nodi proprietÃ  con relativo score al tempo 0 in base
    // ai soli film con locandina
    public void insertVertexScorePosterSelectionFromGraph(PageRankWithPriors<String, String> pageRankWithPriors,
                                                          Graph<String, String> moviesGraph) throws Exception {
        int vertexCount = moviesGraph.getVertexCount();
        dbAccess.insertVerticesPosterSelection(pageRankWithPriors, moviesGraph);
        System.out.println("insertVertexScorePosterSelectionFromGraph - VertexCount: " + vertexCount);

    }

    // Inserisci i nodi film e nodi proprietÃ  con relativo score al tempo 0 in base
    // ai soli film con trailer
    public void insertVertexScoreTrailerSelectionFromGraph(PageRankWithPriors<String, String> pageRankWithPriors,
                                                           Graph<String, String> moviesGraph) throws Exception {
        int vertexCount = moviesGraph.getVertexCount();
        dbAccess.insertVerticesTrailerSelection(pageRankWithPriors, moviesGraph);
        System.out.println("insertVertexScoreTrailerSelectionFromGraph - VertexCount: " + vertexCount);

    }

    /*
     * public void insertPropertyScoreFromItem(PageRankWithPriors<String, String>
     * userPageRankWithPriors, Graph<String, String> userGraph, String user_id) throws
     * Exception { //Multimap<String, List<String>> propertyScoreFromItem =
     * ArrayListMultimap.create(); //String resourceURI =
     * "http://dbpedia.org/resource/Batman_Begins";
     *
     * TreeMap<Double, String> itemScoreTreeMap =
     * dbAccess.selectMoviesAndScoreFromScores(user_id);
     *
     * System.out.println("itemScoreTreeMap size: " + itemScoreTreeMap.size());
     *
     * for (Entry<Double, String> entry : itemScoreTreeMap.entrySet()) { //Double
     * score = entry.getKey(); String itemURI = entry.getValue(); List<List<String>>
     * PropertyList = getAllPropertyListFromItem(itemURI); for (List<String> list :
     * PropertyList) { String movieURI = dbAccess.selectMovie(itemURI); String
     * propertyTypeURI = list.get(1); String propertyURI = list.get(2); if
     * (userGraph.containsVertex(propertyURI) && movieURI != null) { Double
     * propertyScore = userPageRankWithPriors.getVertexScore(propertyURI);
     * dbAccess.insertScoresRecMovies(user_id, movieURI, propertyTypeURI,
     * propertyURI, propertyScore); System.out.print("user_id: " + user_id);
     * System.out.print(" - movie_uri: " + movieURI);
     * System.out.print(" - propertyTypeURI: " + propertyTypeURI);
     * System.out.print(" - propertyURI: " + propertyURI);
     * System.out.println(" - score: " + propertyScore.toString());
     *
     * } // else{ // System.out.print("Nodo NON presente"); //
     * System.out.print(" - user_id: " + user_id); //
     * System.out.print(" - movie_uri: " + movieURI); //
     * System.out.print(" - propertyTypeURI:" + propertyTypeURI); //
     * System.out.println(" - propertyURI: " + propertyURI); // } }
     *
     * } }
     */

    // public void insertPropertyScoreFromItem(PageRankWithPriors<String, String>
    // userPageRankWithPriors, Graph<String, String> userGraph, String user_id) throws
    // Exception {
    // //Multimap<String, List<String>> propertyScoreFromItem =
    // ArrayListMultimap.create();
    // //String resourceURI = "http://dbpedia.org/resource/Batman_Begins";
    //
    // //TreeMap<Double, String> itemScoreTreeMap =
    // dbAccess.selectMoviesAndScoreFromScores(user_id);
    // //Recupera lo score di tutti i nodi
    //// int k = 1;
    //// for(String v : userGraph.getVertices()){
    //// if (userGraph.containsVertex(v)) {
    //// Double propertyScore = userPageRankWithPriors.getVertexScore(v);
    //// String Edges = userGraph.getEdges().toString();
    ////
    //// System.out.print("" + k);
    //// System.out.print(" - v: " + v);
    //// System.out.println(" - score: " + propertyScore.toString());
    //// //System.out.println(" - Edges: " + Edges);
    //// }
    //// k++;
    //// }
    //
    //
    //// }
    //// }
    //
    //
    // }

    public void insertOrUpdateUser(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
    }

    public void putLastChange(String user_id, String lastChange) throws Exception {
        dbAccess.insertUser(user_id);
        dbAccess.updateLastChange(user_id, lastChange);
    }

    public void printTopFiveFromRating(Set<Rating> set, String user_id) throws NumberFormatException, Exception {
        System.out.println("\nRecommended Movie: ");
        // Map<String, Double> itemRecScoreMap =
        // dbAccess.selectMoviesAndScoreFromScores(user_id);
        TreeMap<Double, String> itemScoreTreeMap = dbAccess.selectMoviesAndScoreFromScoresRecMovies(user_id);
        int i = 1;
        for (Entry<Double, String> entry : itemScoreTreeMap.entrySet()) {
            Double score = entry.getKey();
            String movieURI = entry.getValue();
            System.out.println("    " + i + "-> " + score + " - " + movieURI);
            i++;
        }
        // System.err.println("" + itemScoreTreeMap.toString());
    }

    // TODO - DONE cerca le proprietÃ  in comune tra un film e i film piaciuti
    public String getEntityExplanationByUserEntity(String user_id, String itemURI, String[] propertyTypes) throws Exception {
        String explanationMovieString = null;
        String movieURI = dbAccess.selectMovie(itemURI);
        Map<String, String> propTypeLabels = Configuration.getDefaultConfiguration().getPropertyTypesLabels();
        ResponseService rs = new ResponseService();

        if (movieURI != null && !movieURI.isEmpty()) {
            List<List<String>> recMoviepropertyList = dbAccess.selectEntityProperties(movieURI, propertyTypes); //TODO: Nuovo schema
            List<List<String>> movieLikePropertyList = dbAccess.selectMoviesAndPropertyByUser(user_id, "like", propertyTypes); //TODO: Nuovo schema
            Map<String, String> itemCommonPropertyMap = new TreeMap<String, String>();
            // System.out.println("recMoviepropertyList: " + recMoviepropertyList);
            // System.out.println("movieLikePropertyList: " + movieLikePropertyList);
            //String recMovieName = getNamefromURI(movieURI);
            String recMovieName = getEntityLabel(movieURI);

            for (List<String> recMovieProperty : recMoviepropertyList) {
                // String subjectRecMovie = recMovieProperty.get(0).toString();
                // String predicateRecMovie = recMovieProperty.get(1).toString();
                String objectRecMovie = recMovieProperty.get(2).toString();
                for (List<String> likeMovieProperty : movieLikePropertyList) {
                    String subjectLikeMovie = likeMovieProperty.get(0).toString();
                    String predicateLikeMovie = likeMovieProperty.get(1).toString();
                    String objectLikeMovie = likeMovieProperty.get(2).toString();
                    if (objectRecMovie.equals(objectLikeMovie)) {
                        String subject = getEntityLabel(subjectLikeMovie.toString());
                        String predicate = propTypeLabels.get(predicateLikeMovie.toString());
                        String object = getPropertyLabel(objectLikeMovie.toString());
                        //String subject = getNamefromURI(subjectLikeMovie.toString());
                        //String predicate = getsubNamefromURI(predicateLikeMovie.toString());
                        //String object = getNamefromURI(objectLikeMovie.toString());
                        String key = rs.getExplanationKey(predicate, object);
						/*if (predicate.equals("starring")) {
							key = "actor" + " is " + object;
						} else {
							key = predicate + " is " + object;
						}*/
                        if (!itemCommonPropertyMap.containsKey(key)) {
                            itemCommonPropertyMap.put(key, subject);
                            // System.out.println("-" + recMovieProperty.toString());
                            // explanationMovieString = explanationMovieString + "\nThe " + key + " as in "+
                            // itemCommonPropertyMap.get(key);
                        }
                    }
                }
            }

            explanationMovieString = rs.getEntityExplanationByUserMovieMessage(recMovieName, itemCommonPropertyMap);
        }

        // System.out.println("explanationMovieString:\n" + explanationMovieString);
        return explanationMovieString;
    }

    public String getEntityExplanationByLikeUserProperty(String user_id, String itemURI, String[] propertyTypes) throws Exception {
        String explanationMovieString = null;
        String movieURI = dbAccess.selectMovie(itemURI);
        ResponseService rs = new ResponseService();

        if (movieURI != null && !movieURI.isEmpty()) {
            List<List<String>> recMoviepropertyList = dbAccess.selectEntityProperties(movieURI, propertyTypes);
            List<List<String>> posPropertyList = dbAccess.selectPosPropertyListForUserFromRatingsProperties(user_id);
            Map<String, String> itemCommonPropertyMap = new TreeMap<String, String>();
            //String recMovieName = getNamefromURI(movieURI);
            String recMovieName = getEntityLabel(movieURI);
            Map<String, String> propTypeLabels = Configuration.getDefaultConfiguration().getPropertyTypesLabels();

            // System.out.println("recMoviepropertyList: " + recMoviepropertyList);
            // System.out.println("LikePropertyList: " + posPropertyList.toString());

            for (List<String> recMovieProperty : recMoviepropertyList) {
                String propertyTypeRecMovieURI = recMovieProperty.get(1).toString();
                String propertyValueRecMovieURI = recMovieProperty.get(2).toString();
                for (List<String> likeProperty : posPropertyList) {
                    String propertyValueURI = likeProperty.get(0).toString();
                    // String propertyTypeURI = likeProperty.get(1).toString();
                    if (propertyValueRecMovieURI.equals(propertyValueURI)) {
                        //String propertyTypeRecMovie = getsubNamefromURI(propertyTypeRecMovieURI.toString());
                        String propertyTypeRecMovie = propTypeLabels.get(propertyTypeRecMovieURI.toString());
                        String propertyValue = getPropertyLabel(propertyValueURI.toString());
                        String propertyValueRecMovie = getPropertyLabel(propertyValueRecMovieURI.toString());
                        //String propertyValueRecMovie = getNamefromURI(propertyValueRecMovieURI.toString());
                        //String propertyValue = getNamefromURI(propertyValueURI.toString());
                        String key = rs.getExplanationKey(propertyTypeRecMovie, propertyValueRecMovie);

                        // System.out.println("out" + recMovieProperty.toString());
                        if (!itemCommonPropertyMap.containsKey(key)) {
                            itemCommonPropertyMap.put(key, propertyValue);
                            // System.out.println("in" + recMovieProperty.toString());
                            // explanationMovieString = explanationMovieString + "\nThe " + key + " and you
                            // like "+ itemCommonPropertyMap.get(key);
                        }
                    }
                }
            }

            explanationMovieString = rs.getEntityExplanationByLikeUserPropertyMessage(recMovieName, itemCommonPropertyMap);
        }

        // System.out.println("explanationLikePropertyString:\n" +
        // explanationMovieString);
        return explanationMovieString;
    }

    public String getEntityExplanationByDislikeUserProperty(String user_id, String itemURI, String[] propertyTypes) throws Exception {
        String explanationMovieString = null;
        String movieURI = dbAccess.selectMovie(itemURI); //TODO: Nuovo schema
        ResponseService rs = new ResponseService();

        if (movieURI != null && !movieURI.isEmpty()) {
            List<List<String>> recMoviepropertyList = dbAccess.selectEntityProperties(movieURI, propertyTypes);  //TODO: Nuovo schema
            List<List<String>> negPropertyList = dbAccess.selectNegPropertyListForUserFromRatingsProperties(user_id);
            Map<String, String> itemCommonPropertyMap = new TreeMap<String, String>();
            //String recMovieName = getNamefromURI(movieURI);
            String recMovieName = getEntityLabel(movieURI);
            Map<String, String> propTypeLabels = Configuration.getDefaultConfiguration().getPropertyTypesLabels();

            // System.out.println("recMoviepropertyList: " + recMoviepropertyList);
            // System.out.println("LikePropertyList: " + posPropertyList.toString());

            for (List<String> recMovieProperty : recMoviepropertyList) {
                String propertyTypeRecMovieURI = recMovieProperty.get(1).toString();
                String propertyValueRecMovieURI = recMovieProperty.get(2).toString();
                for (List<String> dislikeProperty : negPropertyList) {
                    String propertyValueURI = dislikeProperty.get(0).toString();
                    String propertyTypeURI = dislikeProperty.get(1).toString();
                    if ((!propertyValueRecMovieURI.equals(propertyValueURI))
                            && propertyTypeRecMovieURI.equals(propertyTypeURI)) {
                        //String propertyTypeRecMovie = getsubNamefromURI(propertyTypeRecMovieURI.toString());
                        String propertyTypeRecMovie = propTypeLabels.get(propertyTypeRecMovieURI.toString());
                        String propertyValue = getPropertyLabel(propertyValueURI.toString());
                        String propertyValueRecMovie = getPropertyLabel(propertyValueRecMovieURI.toString());
                        //String propertyValueRecMovie = getNamefromURI(propertyValueRecMovieURI.toString());
                        //String propertyValue = getNamefromURI(propertyValueURI.toString());
                        String key = rs.getExplanationKey(propertyTypeRecMovie, propertyValueRecMovie);
                        // System.out.println("out " + propertyTypeURI.toString());
                        if (!itemCommonPropertyMap.containsKey(key)) {
                            itemCommonPropertyMap.put(key, propertyValue);
                            // System.out.println("in" + propertyTypeURI.toString());
                            // explanationMovieString = explanationMovieString + "\nThe " + key + " and you
                            // like "+ itemCommonPropertyMap.get(key);
                        }
                    }
                }
            }

            if (!itemCommonPropertyMap.isEmpty()) {
                String key = null;
                String value = null;
                explanationMovieString = "I suggest \"" + recMovieName + "\" because in this movie: ";
                String propertyTypeNew = "null";
                String propertyTypeOld = "null";
                String propertyValueOld = "null";
                // System.out.println("itemCommonPropertyMap:" + itemCommonPropertyMap);
                int k = 0;
                TreeMap<String, String> itemCommonPropertySet = new TreeMap<String, String>(Collections.reverseOrder());
                itemCommonPropertySet.putAll(itemCommonPropertyMap);
                // itemCommonPropertySet.descendingKeySet();
                for (Entry<String, String> entry : itemCommonPropertySet.entrySet()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    propertyTypeNew = key.split("\\s+")[0];
                    // System.out.println("" + propertyTypeNew);
                    if (!propertyTypeNew.equals(propertyTypeOld) && (!propertyTypeOld.equals("null"))) {
                        explanationMovieString = explanationMovieString + "\nand no " + propertyValueOld
                                + " that you dislike";
                        propertyTypeOld = propertyTypeNew;
                        k = 0;
                    } else {
                        propertyValueOld = value;
                        propertyTypeOld = propertyTypeNew;
                    }
                    if (k < 3) {
                        explanationMovieString = explanationMovieString + "\nThe " + key;
                        k++;
                    }

                }

                explanationMovieString = explanationMovieString + "\nand not " + value + " that you dislike";
            }
        }

        // System.out.println("explanationDislikePropertyString:\n" +
        // explanationMovieString);
        return explanationMovieString;
    }

    public void printCommonPropertyBetweenItemLikedAndRecommended(String user_id) throws Exception {
        String[] propTypes = Configuration.getDefaultConfiguration().getPropertyTypesForExplanation();

        List<List<String>> itemLikePropertyList = dbAccess.selectMoviesAndPropertyByUser(user_id, "like", propTypes);
        List<List<String>> itemRecPropertyList = dbAccess.selectMoviesAndPropertyByUser(user_id, "rec", propTypes);
        List<List<String>> itemCommonPropertyList = new ArrayList<List<String>>();

        System.out.println("\nCommon property beetween movie liked and recommended: ");

        for (List<String> listItemLike : itemLikePropertyList) {
            String predicate = listItemLike.get(1).toString();
            String object = listItemLike.get(2).toString();
            for (List<String> listItemRec : itemRecPropertyList) {
                String predicateItemRec = listItemRec.get(1).toString();
                String objectItemRec = listItemRec.get(2).toString();
                if (!predicate.contains("score") && predicate.equals(predicateItemRec)
                        && object.equals(objectItemRec)) {
                    List<String> propertyList = new ArrayList<String>();
                    propertyList.add(predicateItemRec);
                    propertyList.add(objectItemRec);
                    if (!itemCommonPropertyList.contains(propertyList)) {
                        itemCommonPropertyList.add(propertyList);
                        System.out.println("    ->" + propertyList.toString());
                    }
                }
            }
        }
        // System.out.println("itemCommonPropertyList: " +
        // itemCommonPropertyList.toString());
    }

    @Deprecated
    public Map<String, List<String>> getPropertyValueAndScoreListByRecEntityFromUserAndPropertyType(String user_id,
                                                                                                    String propertyType) throws IOException {
        Map<String, List<String>> itemToPropertyListMap = new HashMap<String, List<String>>();
        try {
            itemToPropertyListMap = dbAccess
                    .selectRecMovieToPropertyValueAndScoreListFromScoresRecMoviesByUserAndPropertyType(user_id,
                            propertyType);
        } catch (Exception e) {

            System.err.println("Error itemToPropertyListMap:" + itemToPropertyListMap);
            e.printStackTrace();
        }
        return itemToPropertyListMap;
    }

    @Deprecated
    public String getNamefromURI(String uri) throws Exception {
        String name = dbAccess.selectNameFromUriInVertexPosterSelection(uri);

        return name;
    }

    @Deprecated
    public String getsubNamefromURI(String fullName) {
        String subName = fullName;

        if (fullName.contains("http://purl.org/dc/terms/subject")) {
            subName = "category";
        } else if (fullName.length() >= 28) {
            subName = fullName.substring(28, fullName.length());
        }
        subName = subName.replaceAll("_", " ");

        return subName;
    }

    public Map<Double, String> getRecEntityListByUser(String user_id) throws IOException {
        Map<Double, String> userRecMovieMap = new HashMap<Double, String>();
        try {
            userRecMovieMap = dbAccess.selectRecMovieListMap(user_id);
        } catch (Exception e) {
            System.err.println("Error getRecMovieListByUser:" + userRecMovieMap);
            e.printStackTrace();
        }

        // System.out.println("userRecMovieMap size: " + userRecMovieMap.size());
        return userRecMovieMap;
    }

    public void setRefocusRecListByUser(String user_id) throws IOException {
        Map<Double, String> userRecMovieMap = new HashMap<Double, String>();
        int r = 0;
        String lastChange = "refocus";

        try {
            dbAccess.insertUser(user_id);
            int number_recommendation_list = dbAccess.selectNumberRecommendationListByUser(user_id);
            userRecMovieMap = dbAccess.selectRecMovieListMap(user_id);
            Integer numberRatedRecMovie = this.getNumberRatedRecEntityByUserAndRecList(user_id);
            // se il numero di film raccomandati valutati Ã¨ zero puoi avviare il refocus
            if (numberRatedRecMovie.equals(0)) {
                for (Double scoreKey : userRecMovieMap.keySet()) {
                    String movieURI = userRecMovieMap.get(scoreKey);
                    this.putEntityRatedByUser(user_id, movieURI, r, lastChange);
                    this.insertRefocusRecEntityRatingByUser(user_id, movieURI, number_recommendation_list, lastChange);
                }
            }

        } catch (Exception e) {
            System.err.println("Error getRecMovieListByUser:" + userRecMovieMap);
            e.printStackTrace();
        }
    }

    // public void setItemListFromProperty(String user_id, String propertyType, String
    // propertyURI) throws Exception{
    // propertyToItemListMap = new HashMap<String, List<String>>();
    // propertyToItemListMap =
    // dbAccess.selectMovieFromScoresByUserAndProperty(user_id, propertyType,
    // propertyURI);
    //
    // }

    public Map<String, List<String>> getItemListFromProperty(String user_id, String propertyType, String propertyURI)
            throws Exception {
        Map<String, List<String>> propertyToItemListMap = new HashMap<String, List<String>>();
        propertyToItemListMap = dbAccess.selectMovieFromScoresByUserAndProperty(user_id, propertyType, propertyURI);
        if (propertyToItemListMap != null)
            return propertyToItemListMap;
        else
            return null;
    }

    public String getBotNameByUser(String user_id) throws Exception {
        String botName = null;
        Map<String, String> userDetailMap = new HashMap<String, String>();
        dbAccess.insertUser(user_id);
        userDetailMap = dbAccess.selectUserDetalByUser(user_id);
        if (userDetailMap.containsKey("botName")) {
            botName = userDetailMap.get("botName");
        }

        // System.out.println("botName:" + botName);
        return botName;
    }

    public String setBotNameByUser(String user_id) throws Exception {
        Map<String, String> userDetailMap = new HashMap<String, String>();
        Map<String, Integer> botNameNumberMap = new HashMap<String, Integer>();
        String[] botNameArray = {"conf1testrecsysbot", "conf2testrecsysbot", "conf3testrecsysbot",
                "conf4testrecsysbot"};
        String botName = null;

        dbAccess.insertUser(user_id);
        userDetailMap = dbAccess.selectUserDetalByUser(user_id);
        if (userDetailMap.containsKey("botName")) {
            botName = userDetailMap.get("botName");
        }
        if (botName == null || botName.isEmpty()) {
            int numberBotNameConf1 = dbAccess.selectNumberOfBotNameByBotName("conf1testrecsysbot");
            int numberBotNameConf2 = dbAccess.selectNumberOfBotNameByBotName("conf2testrecsysbot");
            int numberBotNameConf3 = dbAccess.selectNumberOfBotNameByBotName("conf3testrecsysbot");
            int numberBotNameConf4 = dbAccess.selectNumberOfBotNameByBotName("conf4testrecsysbot");

            botNameNumberMap.put("conf1testrecsysbot", numberBotNameConf1);
            botNameNumberMap.put("conf2testrecsysbot", numberBotNameConf2);
            botNameNumberMap.put("conf3testrecsysbot", numberBotNameConf3);
            botNameNumberMap.put("conf4testrecsysbot", numberBotNameConf4);
            // Prendi la minore configuarzione
            Integer min = Collections.min(botNameNumberMap.values());
            // Se c'Ã¨ lo stesso numero di configurazioni
            if (min.equals(numberBotNameConf1) && min.equals(numberBotNameConf2) && min.equals(numberBotNameConf3)
                    && min.equals(numberBotNameConf4)) {

                Random random = new Random();
                int index = random.nextInt(3);
                botName = botNameArray[index];

                // System.out.println("Stesso numero di configuarzioni:" +
                // botNameNumberMap.toString());
                // System.out.println("botName:" + botName + " - index:" + index);
            } else {
                // altrimenti assegna la configuarazione minima
                for (String key : botNameNumberMap.keySet()) {
                    Integer value = botNameNumberMap.get(key);
                    if (value.equals(min)) {
                        botName = key;
                    }
                }

            }

        }

        // System.out.println("configuarzioni:" + botNameNumberMap.toString());
        // System.out.println("user_id:"+ user_id + "botName:" + botName);
        return botName;
    }

    public String getBotNameOrSetNewSession(String user_id) throws Exception {
        Map<String, String> userDetailMap = new HashMap<String, String>();
        Map<String, Integer> botNameNumberMap = new HashMap<String, Integer>();
        Set<String> botNameSet = new TreeSet<>();
        Set<String> userBotNameSet = new TreeSet<>();
        String botName = null;

        String[] botNameArray = {"conf1testrecsysbot", "conf2testrecsysbot", "conf3testrecsysbot",
                "conf4testrecsysbot"};

        botNameSet.add("conf1testrecsysbot");
        botNameSet.add("conf2testrecsysbot");
        botNameSet.add("conf3testrecsysbot");
        botNameSet.add("conf4testrecsysbot");

        userBotNameSet = dbAccess.selectBotConfigurationSetByUser(user_id);

        // elimina dalle conf le configurazioni utene
        botNameSet.removeAll(userBotNameSet);

        if (botNameSet != null && !botNameSet.isEmpty()) {
            // prendi la prima disponibile
            for (String conf : botNameSet) {
                botName = conf;
                break;
            }
            // in quel caso azzera tutto il profilo
            this.deleteAllProfileByUser(user_id);
        } else {
            // altrimenti restituisci quella attuale
            botName = this.getBotNameByUser(user_id);
        }

        // System.out.println("configuarzioni:" + botNameNumberMap.toString());
        // System.out.println("user_id:"+ user_id + "botName:" + botName);
        return botName;
    }

    public Map<String, String> getUserDetail(String user_id) throws Exception {
        Map<String, String> userDetailMap = new HashMap<String, String>();
        userDetailMap = dbAccess.selectUserDetalByUser(user_id);
        return userDetailMap;
    }

    public String getUserFirstname(String user_id) throws Exception {
        String firstname = "User";
        Map<String, String> userDetailMap = new HashMap<String, String>();
        userDetailMap = dbAccess.selectUserDetalByUser(user_id);

        firstname = userDetailMap.get("firstname");

        return firstname;
    }

    public Map<String, String> getChatMessageByUser(String user_id, String context, int pagerank_cicle) throws Exception {
        Map<String, String> messageDetailMap = new HashMap<String, String>();
        messageDetailMap = dbAccess.selectMessageDetailAndContextByUser(user_id, context, pagerank_cicle);

        return messageDetailMap;
    }

    public void updateMessageLog(String userID, String messageID, long timestampEnd) {
        dbAccess.updateMessageLog(userID, messageID, timestampEnd);
    }

    public void updateMessageLog(String userID, String messageID, long timestampEnd, int pagerankCycle, int numberRecommendationList) {
        dbAccess.updateMessageLog(userID, messageID, timestampEnd, pagerankCycle, numberRecommendationList);
    }

    public void updateMessageLog(String userID, String messageID, long timestampEnd, int pagerankCycle, int numberRecommendationList, List<String> events) {
        dbAccess.updateMessageLog(userID, messageID, timestampEnd, pagerankCycle, numberRecommendationList, events);
    }

    public Map<String, String> getPropertyRatingByUserAndProperty(String user_id, String propertyTypeUri,
                                                                  String propertyUri, String lastChange) throws Exception {
        Map<String, String> userPropertyRatingMap = new HashMap<String, String>();
        userPropertyRatingMap = dbAccess.selectPropertyRatingByUserAndProperty(user_id, propertyTypeUri, propertyUri,
                lastChange);

        return userPropertyRatingMap;
    }

    public Map<String, Integer> getPosNegRatingForUserFromRatings(String user_id) throws Exception {
        Map<String, Integer> movieOrPropertyToRatingMap = new HashMap<String, Integer>();
        Map<String, Integer> releaseYearFilterToRatingMap = new HashMap<String, Integer>();
        Map<String, Integer> runtimeRangeFilterToRatingMap = new HashMap<String, Integer>();
        movieOrPropertyToRatingMap = dbAccess.selectPosNegRatingForUserFromRatings(user_id);
        releaseYearFilterToRatingMap = dbAccess.selectReleaseYearFilterFromUsersForPropertyRating(user_id);
        runtimeRangeFilterToRatingMap = dbAccess.selectRuntimeRangeFilterFromUsersForPropertyRating(user_id);

        if (releaseYearFilterToRatingMap != null && !releaseYearFilterToRatingMap.isEmpty()) {
            movieOrPropertyToRatingMap.putAll(releaseYearFilterToRatingMap);
        }

        if (runtimeRangeFilterToRatingMap != null && !runtimeRangeFilterToRatingMap.isEmpty()) {
            movieOrPropertyToRatingMap.putAll(runtimeRangeFilterToRatingMap);
        }

        return movieOrPropertyToRatingMap;
    }

    public Map<String, Integer> getRatingsForUserFromRatings(String user_id) throws Exception {
        Map<String, Integer> movieOrPropertyToRatingMap = new HashMap<String, Integer>();
        Map<String, Integer> releaseYearFilterToRatingMap = new HashMap<String, Integer>();
        Map<String, Integer> runtimeRangeFilterToRatingMap = new HashMap<String, Integer>();
        movieOrPropertyToRatingMap = dbAccess.selectRatingsForUserFromRatings(user_id);
        releaseYearFilterToRatingMap = dbAccess.selectReleaseYearFilterFromUsersForPropertyRating(user_id);
        runtimeRangeFilterToRatingMap = dbAccess.selectRuntimeRangeFilterFromUsersForPropertyRating(user_id);

        if (releaseYearFilterToRatingMap != null && !releaseYearFilterToRatingMap.isEmpty()) {
            movieOrPropertyToRatingMap.putAll(releaseYearFilterToRatingMap);
        }

        if (runtimeRangeFilterToRatingMap != null && !runtimeRangeFilterToRatingMap.isEmpty()) {
            movieOrPropertyToRatingMap.putAll(runtimeRangeFilterToRatingMap);
        }

        return movieOrPropertyToRatingMap;
    }

    public List<List<String>> getAllEntityProperties(String entityURI, String[] propertyTypes) throws Exception {
        return dbAccess.selectEntityProperties(entityURI, propertyTypes);
    }

    public String getEntityToRatingByPopularEntities(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        String movieURI = dbAccess.selectMovieToRatingByUser(user_id);

        return movieURI;
    }

    public double getEntityFromMovieLens(String label) throws Exception {
        double avg = dbAccess.selectMovieLensMovie(label);

        return avg;
    }

    public String getLastAcceptRecEntityToRating(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        String movieURI = dbAccess.selectAcceptRecMovieToRatingByUser(user_id);

        return movieURI;
    }

    public String[] getEntityControlling(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        return dbAccess.selectAcceptRecMovieToControlling(user_id);
    }

    public String[] getEntitySkipping(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        return dbAccess.selectAcceptRecMovieToSkipping(user_id);
    }

    public String getEntityToRatingByPopularEntitiesOrJaccard(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        String movieURI = null;
        int numberRatedItems = dbAccess.selectNumberOfRatedMoviesByUser(user_id);
        if (numberRatedItems < 1) { // if there' re not so much evaluated films
            movieURI = dbAccess.selectMovieToRatingByRandPopularUser(user_id);
        } else {
            JaccardIndex jaccardIndex = new JaccardIndex();
            Map<String, Double> jaccardDistanceMap = new HashMap<String, Double>();
            jaccardDistanceMap = jaccardIndex.getJaccardDistance(user_id);
            if (jaccardDistanceMap != null && !jaccardDistanceMap.isEmpty()) {
                String i[] = jaccardDistanceMap.keySet().toArray(new String[0]);
                int randidx = new Random().nextInt(i.length);
                movieURI = i[randidx];//jaccardDistanceMap.keySet().iterator().next();
            }
        }
        return movieURI;
    }

    public String getEntityToRatingByRandomPopularEntitiesOrJaccard(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        String movieURI = null;
        int numberRatedItems = dbAccess.selectNumberOfRatedMoviesByUser(user_id);
        if (numberRatedItems < 15) { // if there' re not so much evaluated films
            movieURI = dbAccess.selectMovieToRatingByRandPopularUser(user_id);
        } else {
            JaccardIndex jaccardIndex = new JaccardIndex();
            Map<String, Double> jaccardDistanceMap = new HashMap<String, Double>();
            jaccardDistanceMap = jaccardIndex.getJaccardDistance(user_id);
            if (jaccardDistanceMap != null && !jaccardDistanceMap.isEmpty()) {
                movieURI = jaccardDistanceMap.keySet().iterator().next();
            }
        }
        //System.out.println("movieURI:" + movieURI);
        return movieURI;
    }

    public String getEntityToRatingByPopxVarOrJaccard(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        String movieURI = null;
        int numberRatedItems = dbAccess.selectNumberOfRatedMoviesByUser(user_id);
        if (numberRatedItems < 15) { // if there' re not so much evaluated films
            String label = dbAccess.selectMovieToRatingByPopxVarUser(user_id);
            movieURI = dbAccess.selectMovieLabelByPopxVarUser(label);
        } else {
            JaccardIndex jaccardIndex = new JaccardIndex();
            Map<String, Double> jaccardDistanceMap = new HashMap<String, Double>();
            jaccardDistanceMap = jaccardIndex.getJaccardDistance(user_id);
            if (jaccardDistanceMap != null && !jaccardDistanceMap.isEmpty()) {
                movieURI = jaccardDistanceMap.keySet().iterator().next();
            }
        }
        System.out.println("movieURI:" + movieURI);
        return movieURI;
    }


    // Non usata
    public Map<String, Double> getEntitiesToRatingFromJaccardDistanceMapByUser(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        JaccardIndex jaccardIndex = new JaccardIndex();
        Map<String, Double> jaccardDistanceMap = new HashMap<String, Double>();
        jaccardDistanceMap = jaccardIndex.getJaccardDistance(user_id);

        return jaccardDistanceMap;
    }

    public void insertAcceptRecEntityRatedByUser(String user_id, String itemURI, int rating) throws Exception {
        dbAccess.insertUser(user_id);

        String movieURI = dbAccess.selectMovie(itemURI);
        if (movieURI != null) {
            dbAccess.insertAcceptRecMovieRated(user_id, movieURI, rating);
        }
        // Evitata per come Ã¨ gestita lato client
        // if (movieURI != null && rating != 3) {
        // dbAccess.insertMovieRated(user_id, movieURI, rating);
        // dbAccess.updateNumberRatedMoviesByUser(user_id);
        // }
    }

    public void insertRecEntityRatedByUser(String user_id, String itemURI, int number_recommendation_list, int rating,
                                           int position, int pagerank_cicle, String refine, String refocus, String botName, String message_id,
                                           int bot_timestamp, String recommendatinsList, String ratingsList) throws Exception {
        dbAccess.insertUser(user_id);

        String movieURI = dbAccess.selectMovie(itemURI);
        if (movieURI != null) {
            dbAccess.insertRecMovieRated(user_id, movieURI, number_recommendation_list, rating, position,
                    pagerank_cicle, refine, refocus, botName, message_id, bot_timestamp, recommendatinsList,
                    ratingsList);
        }
    }

    public void insertRecEntityToRatingByUser(String user_id, String itemURI, int number_recommendation_list, int position,
                                              int pagerank_cicle, String botName, String message_id, int bot_timestamp) throws Exception {
        dbAccess.insertUser(user_id);

        int response_time = 0;
        if (position > 1) {
            int oldPosition = position - 1;
            int oldBotTimestamp = dbAccess.selectBotTimestampFromRatingsRecMovies(user_id, number_recommendation_list,
                    oldPosition);
            response_time = getDifferentFromTwoTimestampToSecond(oldBotTimestamp, bot_timestamp);
        }
        // aggiungi anche la lista dei film raccomandati
        Map<Double, String> userRecMovieMap = new HashMap<Double, String>();
        userRecMovieMap = dbAccess.selectRecMovieListMap(user_id);
        List<String> recommendationList = new ArrayList<String>();
        String recommendationListString = null;
        for (Double key : userRecMovieMap.keySet()) {
            String value = userRecMovieMap.get(key);
            recommendationList.add(value);
        }
        recommendationListString = recommendationList.toString();

        String movieURI = dbAccess.selectMovie(itemURI);
        if (movieURI != null) {
            dbAccess.insertRecMovieToRating(user_id, movieURI, number_recommendation_list, position, pagerank_cicle,
                    botName, message_id, bot_timestamp, response_time, recommendationListString);
        }
    }

    // inseriesce la richiesta di why
    public void insertWhyRecEntityRequestByUser(String user_id, String itemURI, int number_recommendation_list, String why)
            throws Exception {
        dbAccess.insertUser(user_id);

        String movieURI = dbAccess.selectMovie(itemURI);
        if (movieURI != null) {
            dbAccess.updateWhyRecMovieRequestByUser(user_id, movieURI, number_recommendation_list, why);
        }
    }

    // inseriesce la richiesta di details
    public void insertDetailsEntityRequestByUser(String user_id, String itemURI, int number_recommendation_list,
                                                 String details) throws Exception {
        dbAccess.insertUser(user_id);

        String movieURI = dbAccess.selectMovie(itemURI);
        String botName = this.getBotNameByUser(user_id);
        if (movieURI != null) {
            dbAccess.insertDetailsMovieRequest(user_id, movieURI, details, number_recommendation_list, botName);

            // inseriamo anche nel log
            dbAccess.insertDetailsMovieRequestLog(user_id, movieURI, details, number_recommendation_list, botName);
        }
    }

    public void insertDetailsRecEntityRequestByUser(String user_id, String itemURI, int number_recommendation_list,
                                                    String details) throws Exception {
        dbAccess.insertUser(user_id);

        String movieURI = dbAccess.selectMovie(itemURI);
        if (movieURI != null) {
            dbAccess.updateDetailsRecMovieRequestByUser(user_id, movieURI, number_recommendation_list, details);
        }
    }

    public void insertRefineRecEntityRatingByUser(String user_id, String itemURI, int number_recommendation_list,
                                                  String refine) throws Exception {
        dbAccess.insertUser(user_id);

        String movieURI = dbAccess.selectMovie(itemURI);
        if (movieURI != null) {
            dbAccess.updateRefineRecMovieRatingByUser(user_id, movieURI, number_recommendation_list, refine);
        }
    }

    public void insertRefocusRecEntityRatingByUser(String user_id, String itemURI, int number_recommendation_list,
                                                   String refocus) throws Exception {
        dbAccess.insertUser(user_id);

        String movieURI = dbAccess.selectMovie(itemURI);
        if (movieURI != null) {
            dbAccess.updateRefocusRecMovieRatingByUser(user_id, movieURI, number_recommendation_list, refocus);
        }
    }

    public void insertLikeRecEntityRatingByUser(String user_id, String itemURI, int number_recommendation_list, int like)
            throws Exception {
        dbAccess.insertUser(user_id);

        String movieURI = dbAccess.selectMovie(itemURI);
        if (movieURI != null) {
            dbAccess.updateLikeRecMovieRatingByUser(user_id, movieURI, number_recommendation_list, like);
            System.out.println("[AdaptiveSelection] Inserito");
        }
    }

    public void insertDislikeRecEntityRatingByUser(String user_id, String itemURI, int number_recommendation_list,
                                                   int disdislike) throws Exception {
        dbAccess.insertUser(user_id);

        String movieURI = dbAccess.selectMovie(itemURI);
        if (movieURI != null) {
            dbAccess.updateDislikeRecMovieRatingByUser(user_id, movieURI, number_recommendation_list, disdislike);
        }
    }

    public void putEntityRatedByUser(String user_id, String itemURI, int rating, String lastChange) throws Exception {
        currLogger.log(Level.INFO, "Call putEntityRatedByUser, itemURI is " + itemURI);
        dbAccess.insertUser(user_id);
        String movieURI = dbAccess.selectMovie(itemURI);
        currLogger.log(Level.INFO, "movieURI is " + movieURI);
        if (movieURI != null) {
            currLogger.log(Level.INFO, "Entity exists");
            String botName = this.getBotNameByUser(user_id);
            int numberRecommendationList = dbAccess.selectNumberRecommendationListByUser(user_id);
            dbAccess.insertMovieRated(user_id, movieURI, rating, lastChange, numberRecommendationList, botName);
            dbAccess.updateNumberRatedMoviesByUser(user_id);

            // inseriamo il film valutato anche nella rispettiva tabella di log
            dbAccess.insertMovieRatedToLog(user_id, movieURI, rating, lastChange, numberRecommendationList, botName);

        }
    }

    // Prendi la forma corretta di intervallo di anni
    public String changeReleaseYearStringToReleaseYearValue(String releaseYearString) throws Exception {
        String releaseYearValue = null;

        switch (releaseYearString) {
            case "1910s - 1950s":
                releaseYearValue = "1910-1950";
                break;
            case "1950s - 1980s":
                releaseYearValue = "1950-1980";
                break;
            case "1980s - 2000s":
                releaseYearValue = "1980-2000";
                break;
            case "2000s - today":
                releaseYearValue = "2000-2016";
                break;
            case "no_release_year_filter":
                releaseYearValue = "no_release_year_filter";
                break;

            default:
                break;
        }

        return releaseYearValue;
    }

    public String changeRuntimeRangeStringToRuntimeRangeValue(String runtimeRangeString) throws Exception {
        String runtimeRangeValue = null;

        switch (runtimeRangeString) {
            case "runtime <= 90 min":
                runtimeRangeValue = "90";
                break;
            case "runtime 90 - 120 min":
                runtimeRangeValue = "120";
                break;
            case "runtime 120 - 150 min":
                runtimeRangeValue = "150";
                break;
            case "runtime > 150 min":
                runtimeRangeValue = "151";
                break;
            case "no_runtime_range_filter":
                runtimeRangeValue = "no_runtime_range_filter";
                break;

            default:
                break;
        }

        return runtimeRangeValue;
    }

    // Prende la forma di URI corretta se presente
    public String getResourceUriFromDbpediaEntitiesSelection(String resourceURI) throws Exception {
        String URI = dbAccess.selectResourceUriFromDbpediaMoviesSelection(resourceURI);

        return URI;
    }

    // Prende la forma della movieURI corretta se presente
    public String getEntityUriFromEntities(String itemURI) throws Exception {
        String movieURI = dbAccess.selectMovie(itemURI);

        return movieURI;
    }

    public void putNumberPagerankCicleByUser(String user_id, int pagerank_cicle) throws Exception {
        dbAccess.insertUser(user_id);
        dbAccess.updateNumberPagerankCicleByUser(user_id, pagerank_cicle);
    }

    public void putNumberRecommendationListByUser(String user_id, int number_recommendation_list) throws Exception {
        dbAccess.insertUser(user_id);
        dbAccess.updateNumberRecommendationListByUser(user_id, number_recommendation_list);
    }

    public int getDifferentFromTwoTimestampToSecond(int oldTime, int lastTime) {
        java.util.Date lastBotTimestamp = new java.util.Date((long) lastTime * 1000);
        java.util.Date oldTimestamp = new java.util.Date((long) oldTime * 1000);

        long seconds = (lastBotTimestamp.getTime() - oldTimestamp.getTime()) / 1000;

        return (int) seconds;
    }

    public String putChatMessageByUser(String user_id, String message_id, String context, String replyText,
                                       String replyFunctionCall, int pagerank_cicle, String botName, int bot_timestamp, String responseType)
            throws Exception {
        dbAccess.insertUser(user_id);

        int number_rated_movies = dbAccess.selectNumberOfRatedMoviesByUser(user_id);
        int number_rated_properties = dbAccess.selectNumberOfRatedPropertiesByUser(user_id);
        int oldBotTimestamp = dbAccess.selectLastBotTimestampByUser(user_id);
        int response_time = getDifferentFromTwoTimestampToSecond(oldBotTimestamp, bot_timestamp);
        int number_recommendation_list = dbAccess.selectNumberRecommendationListByUser(user_id);
        String returnString = dbAccess.insertChatMessage(user_id, message_id, context, replyText, replyFunctionCall,
                pagerank_cicle, number_recommendation_list, botName, bot_timestamp, response_time, responseType,
                number_rated_movies, number_rated_properties);

        // inseriamo il messaggio anche nella chat log in modo da non perderlo
        dbAccess.insertChatMessageToChatLog(user_id, message_id, context, replyText, replyFunctionCall, pagerank_cicle,
                number_recommendation_list, botName, bot_timestamp, response_time, responseType, number_rated_movies,
                number_rated_properties);
        return returnString;
    }

    public void putUserDetailByUser(String user_id, String firstname, String lastname, String username) throws Exception {
        // fare una select e nel caso dopo l'insert
        dbAccess.insertUserDetail(user_id, firstname, lastname, username);
    }

    public void putPropertyRatedByUser(String user_id, String propertyTypeURI, String propertyURI, int rating,
                                       String lastChange) throws Exception {
        dbAccess.insertUser(user_id);
        String botName = this.getBotNameByUser(user_id);
        int numberRecommendationList = dbAccess.selectNumberRecommendationListByUser(user_id);
        dbAccess.insertPropertyRated(user_id, propertyTypeURI, propertyURI, rating, lastChange,
                numberRecommendationList, botName);

        // inserisci anche nel log
        dbAccess.insertPropertyRatedLog(user_id, propertyTypeURI, propertyURI, rating, lastChange,
                numberRecommendationList, botName);

        dbAccess.updateNumberRatedPropertiesByUser(user_id);
    }

    // metodi per gestire release year e time
    public void putReleaseYearFilterByUser(String user_id, String propertyType, String propertyValue) throws Exception {
        dbAccess.insertUser(user_id);
        dbAccess.insertReleaseYearFilter(user_id, propertyType, propertyValue);
    }

    public void putRuntimeRangeFilterByUser(String user_id, String propertyType, String propertyValue) throws Exception {
        dbAccess.insertUser(user_id);
        dbAccess.insertRuntimeRangeFilter(user_id, propertyType, propertyValue);
    }

    public void putBotNameByUser(String user_id, String botName) throws Exception {
        dbAccess.insertUser(user_id);
        dbAccess.insertBotName(user_id, botName);
    }

    // recupera le configurazioni distinte per l'utente
    public Set<String> getBotConfigurationSetByUser(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        Set<String> botNameSet = new TreeSet<>();

        botNameSet = dbAccess.selectBotConfigurationSetByUser(user_id);

        return botNameSet;

    }

    // se il botName non Ã¨ presente tra le configurazioni utente inseriscilo
    public void putBotConfigurationByUser(String user_id, String botName, int bot_timestamp) throws Exception {
        Set<String> botNameSet = new TreeSet<>();

        dbAccess.insertUser(user_id);
        botNameSet = dbAccess.selectBotConfigurationSetByUser(user_id);

        if (!botNameSet.contains(botName)) {
            int number_recommendation_list = dbAccess.selectNumberRecommendationListByUser(user_id);
            dbAccess.insertBotConfiguration(user_id, botName, number_recommendation_list, bot_timestamp);
        }
    }

    public void putAgeRangeByUser(String user_id, String ageRange) throws Exception {
        dbAccess.insertUser(user_id);
        dbAccess.insertAgeRange(user_id, ageRange);
    }

    public void putEducationByUser(String user_id, String education) throws Exception {
        dbAccess.insertUser(user_id);
        dbAccess.insertEducation(user_id, education);
    }

    public void putGenderByUser(String user_id, String gender) throws Exception {
        dbAccess.insertUser(user_id);
        dbAccess.insertGender(user_id, gender);
    }

    public void putInterestInEntitiesByUser(String user_id, String interestInMovies) throws Exception {
        dbAccess.insertUser(user_id);
        dbAccess.insertInterestInMovies(user_id, interestInMovies);
    }

    public void putUsedRecSysByUser(String user_id, String usedRecSys) throws Exception {
        dbAccess.insertUser(user_id);
        dbAccess.insertUsedRecSys(user_id, usedRecSys);
    }

    public void putExperimentalSessionRatingByUser(String user_id, int number_recommendation_list, int rating)
            throws Exception {
        dbAccess.insertUser(user_id);
        String botName = this.getBotNameByUser(user_id);
        dbAccess.insertExperimentalSessionRating(user_id, number_recommendation_list, rating, botName);
    }

    // elimina tutte le proprietÃ  valutate e il relativo score
    public void deleteAllPropertyRatedByUser(String user_id) throws Exception {
        String userID = dbAccess.selectUser(user_id);
        if (userID != null) {
            dbAccess.deleteAllPropertyRatedByUser(user_id);
            dbAccess.deleteAllScoreByUserFromScoresUserProperties(user_id);
            dbAccess.insertReleaseYearFilter(user_id, "releaseYear", "no_release_year_filter");
            dbAccess.insertRuntimeRangeFilter(user_id, "runtimeRange", "no_runtime_range_filter");
            dbAccess.updateNumberRatedPropertiesByUser(user_id);
            dbAccess.updateNumberPagerankCicleByUser(user_id, 0);
            this.putLastChange(user_id, "property_rating");
        }
    }

    public void deletePropertyRatedByUser(String user_id, String uri) throws Exception {
        String userID = dbAccess.selectUser(user_id);
        if (userID != null) {
            dbAccess.deletePropertyRatedByUser(user_id, uri);
        }
    }

    public void deleteAllEntityRatedByUser(String user_id) throws Exception {
        String userID = dbAccess.selectUser(user_id);
        if (userID != null) {
            dbAccess.deleteAllMovieRatedByUser(user_id);
            dbAccess.deleteAllScoreByUserFromScoresUserMovies(user_id);
            dbAccess.updateNumberRatedMoviesByUser(user_id);
            dbAccess.updateNumberPagerankCicleByUser(user_id, 0);
            this.putLastChange(user_id, "movie_rating");
        }
    }

    public void deleteEntityRatedByUser(String user_id, String uri) throws Exception {
        String userID = dbAccess.selectUser(user_id);
        if (userID != null) {
            dbAccess.deleteMovieRatedByUser(user_id, uri);
        }
    }

    public void deleteAllChatMessageByUser(String user_id) throws Exception {
        String userID = dbAccess.selectUser(user_id);
        if (userID != null) {
            dbAccess.deleteAllChatMessageByUser(user_id);
            dbAccess.updateNumberPagerankCicleByUser(user_id, 0);
            this.putLastChange(user_id, "chat_delete");
        }
    }

    public void deleteAllRecEntitiesByUser(String user_id) throws Exception {
        String userID = dbAccess.selectUser(user_id);
        if (userID != null) {
            dbAccess.deleteAllScoreByUserFromScoresRecMovies(user_id);
            dbAccess.updateNumberPagerankCicleByUser(user_id, 0);
            this.putLastChange(user_id, "scores_delete");
        }
    }

    public void deleteAllProfileByUser(String user_id) throws Exception {
        String userID = dbAccess.selectUser(user_id);
        if (userID != null) {
            dbAccess.deleteAllPropertyRatedByUser(user_id);
            dbAccess.deleteAllScoreByUserFromScoresUserProperties(user_id);
            dbAccess.insertReleaseYearFilter(user_id, "releaseYear", "no_release_year_filter");
            dbAccess.insertRuntimeRangeFilter(user_id, "runtimeRange", "no_runtime_range_filter");
            dbAccess.updateNumberRatedPropertiesByUser(user_id);
            dbAccess.deleteAllMovieRatedByUser(user_id);
            dbAccess.deleteAllScoreByUserFromScoresUserMovies(user_id);
            dbAccess.updateNumberRatedMoviesByUser(user_id);
            dbAccess.deleteAllChatMessageByUser(user_id);
            dbAccess.deleteAllScoreByUserFromScoresRecMovies(user_id);
            dbAccess.updateNumberPagerankCicleByUser(user_id, 0);
            this.putLastChange(user_id, "pagerank");
        }
    }

    public void deleteAllUserDetail(String user_id) throws Exception {
        String userID = dbAccess.selectUser(user_id);
        if (userID != null) {
            dbAccess.updateUserDetailToNullByUser(user_id);
            this.putLastChange(user_id, "pagerank");
        }
    }

    public int getNumberBotName(String user_id, String botName) throws Exception {
        int numberBotName = dbAccess.selectNumberOfBotNameByBotName(botName);

        return numberBotName;
    }

    public int getNumberRatedEntities(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        dbAccess.updateNumberRatedMoviesByUser(user_id);
        int numberRatedItems = dbAccess.selectNumberOfRatedMoviesByUser(user_id);

        return numberRatedItems;
    }

    public int getNumberRatedProperties(String user_id) throws Exception {
        dbAccess.updateNumberRatedPropertiesByUser(user_id);
        int numberRatedProperties = dbAccess.selectNumberOfRatedPropertiesByUser(user_id);

        return numberRatedProperties;
    }

    public int getNumberRatedPropertiesWithoutDuplicates(String user_id) throws Exception {
        dbAccess.updateNumberRatedPropertiesByUser(user_id);
        int numberRatedProperties = dbAccess.selectNumberOfRatedPropertiesByUserWithoutDuplicates(user_id);

        return numberRatedProperties;
    }


    public int getNumberPagerankCicle(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        int numberPagerankCicle = dbAccess.selectNumberOfPagerankCicleByUser(user_id);

        return numberPagerankCicle;
    }

    // ottiene il numero di film raccomandati valutati pos o neg
    public int getNumberRatedRecEntityByUserAndRecList(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        int numberRecommendationList = dbAccess.selectNumberRecommendationListByUser(user_id);
        int numberRatedRecMovie = dbAccess.selectNumberRatedRecMovieByUserAndRecList(user_id, numberRecommendationList);

        return numberRatedRecMovie;
    }

    // ottiene il numero di film raccomandati a cui si Ã¨ applicato un refine
    public int getNumberRefineFromRecEntityListByUserAndRecList(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        int numberRecommendationList = dbAccess.selectNumberRecommendationListByUser(user_id);
        int numberRatedRecMovie = dbAccess.selectNumberRefineFromRecMovieListByUserAndRecList(user_id,
                numberRecommendationList);

        return numberRatedRecMovie;
    }

    public int getNumberRecommendationList(String user_id) throws Exception {
        dbAccess.insertUser(user_id);
        int numberRecommendationList = dbAccess.selectNumberRecommendationListByUser(user_id);

        return numberRecommendationList;
    }

    public String getLastChange(String user_id) throws Exception {
        String lastChange = null;
        lastChange = dbAccess.selectLastChangeByUser(user_id);

        return lastChange;
    }

    public Map<String, Double> getLevDistanceFromAllVertexUriByName(String text) throws Exception {
        Map<String, Double> distanceAllVertexUriMap = new HashMap<String, Double>();

        // LevensteinDistanceLucene levDistance = new LevensteinDistanceLucene();
        // distanceAllVertexUriMap = levDistance.getLevDistance(text);

        DidYouMeanBaccaro didYouMean = new DidYouMeanBaccaro();
        distanceAllVertexUriMap = didYouMean.getDistance(text);

        return distanceAllVertexUriMap;
    }

    public Map<String, List<String>> getPropertyTypeFromPropertyValue(String propertyValue) throws Exception {
        Map<String, List<String>> propertyValueToPropertyTypeMap = new HashMap<String, List<String>>();
        propertyValueToPropertyTypeMap = dbAccess.selectPropertyTypeFromPropertyValue(propertyValue);

        return propertyValueToPropertyTypeMap;
    }

    /* Nuove chiamate per MovieRecSysService */

    public List<Pair<Double, String>> getEntitiesToRecommend(String userID) {
        return dbAccess.getEntitiesToRecommend(userID);
    }

    public String getDialogState(String userID) {
        return dbAccess.getDialogState(userID);
    }

    public void setDialogState(String userID, String state) {
        dbAccess.setDialogState(userID, state);
    }

    public String getEntityLabel(String uri) {
        return dbAccess.getEntityLabel(uri);
    }

    public String getEntityMLLabel(String uri) {
        return dbAccess.getEntityMLLabel(uri);
    }

    public String getPropertyLabel(String uri) {
        return dbAccess.getPropertyLabel(uri);
    }

    public String getPropertyFromMovie(String uri, String user_id) {
        return dbAccess.getPropertyFromMovie(uri);
    }

    public String getPropertyFromMovies(String uri, String user_id) {
        return dbAccess.getPropertyFromMovies(uri);
    }

    public void insertMessageIntoLog(String messageID, String userID, String message, long timestampStart, long timestampEnd,
                                     String intent, String contexts, List<String> recognized, List<String> events,
                                     int pagerankCycle, int numRecommendationList, String interactionType, String recommendedEntity) {
        dbAccess.insertMessageInLog(messageID, userID, message, timestampStart, timestampEnd,
                intent, contexts, recognized, events, pagerankCycle, numRecommendationList,
                interactionType, recommendedEntity);
    }

    public void setLogMessageToCheck(String userID, String messageID, boolean toCheck) {
        dbAccess.setLogMessageToCheck(userID, messageID, toCheck);
    }

    public List<LogMessage> getLogMessages(long timestampStart) {
        return dbAccess.getLogMessages(timestampStart);
    }

    public List<RecEntityRating> getCurrentRecEntityRatings(String userID, int numRecList) {
        return dbAccess.getCurrentRecEntityRatings(userID, numRecList);
    }

    public List<RecEntityRating> getRecEntityRatings(String userID) {
        return dbAccess.getRecEntityRatings(userID);
    }

    public List<RecEntityRating> getRecEntityRatingsLog(long timestampStart) {
        return dbAccess.getRecEntityRatingLog(timestampStart);
    }

    public List<PropertyRating> getPropertyRatingsLog(long timestampStart) {
        return dbAccess.getPropertyRatingLog(timestampStart);
    }

    public List<EntityRating> getEntityRatingsLog(long timestampStart) {
        return dbAccess.getEntityRatingLog(timestampStart);
    }

    public List<QuestionnaireAnswer> getAnswers() {
        return dbAccess.getAnswers();
    }

    public void insertAnswer(String userID, int questionId, int answerId) {
        dbAccess.insertAnswer(userID, questionId, answerId);
    }

    public static void main(String[] args) throws Exception {

        // String user_id = 6;
        String user_id = "129877748";

        AdaptiveSelectionController asController = new AdaptiveSelectionController();

        // asController.getMovieToRatingByPopularMovies(user_id);
        asController.getEntityToRatingByPopularEntitiesOrJaccard(user_id);

        // asController.createGraphAndRunPageRank(user_id);
        // asController.printTopFiveFromRating(rating, user_id);
        // asController.printCommonPropertyBetweenItemLikedAndRecommended(user_id);
        // String movieURI =
        // asController.getMovieToRatingByPopularMoviesOrJaccard(user_id);
        // System.out.println("movieURI: " + movieURI.toString());
        // System.out.println("" +
        // asController.getPropertyTypeFromPropertyValue("Drama").toString());

        // asController.getPropertyValueListMapFromPropertyType(user_id, "starring");

        // metodi per aggiornare le tabelle degli score al tempo 0
        // asController.insertVertexScorePosterSelectionFromGraph(graph.getUserPageRankWithPriors(),
        // graph.getGraph());
        // asController.insertVertexScoreTrailerSelectionFromGraph(graph.getUserPageRankWithPriors(),
        // graph.getGraph());

        // String movieURI = "http://dbpedia.org/resource/Django_Unchained";
        // String movieURI = "http://dbpedia.org/resource/Titanic_(1997_film)";
        // asController.getExplanationFromExpLODTopFive(user_id);

        // String explanationMovie =
        // asController.getMovieExplanationByUserMovie(user_id, movieURI);
        // String explanationLikeProperty =
        // asController.getMovieExplanationByLikeUserProperty(user_id, movieURI);
        // String explanationDislikeProperty =
        // asController.getMovieExplanationByDislikeUserProperty(user_id, movieURI);
        // String explanationExpLODTopOne =
        // asController.getExplanationFromExpLODTopOne(user_id, movieURI);
        // System.out.println("\n\nExplanationExpLODTopOne:\n" +
        // explanationExpLODTopOne.toString());
        // System.out.println("\nExplanationMovie:\n" + explanationMovie.toString());
        // System.out.println("\nExplanationLikeProperty:\n" +
        // explanationLikeProperty.toString());
        // System.out.println("\nExplanationDislikeProperty:\n" +
        // explanationDislikeProperty.toString());
        //
        // asController.getMovieExplanationByUserMovie(user_id, movieURI);
        // asController.getMovieExplanationByLikeUserProperty(user_id, movieURI);
        // asController.getMovieExplanationByDislikeUserProperty(user_id, movieURI);

        // List<List<String>> allPropertyFromItem =
        // asController.getAllPropertyListFromItem("http://dbpedia.org/resource/Barry_Lyndon");
        // System.out.println("\nProperties of Top-1 movie recommended: ");
        // System.out.println(" " + allPropertyFromItem.toString());

        // Integer diff =
        // asController.getDifferentFromTwoTimestampToSecond(1489399620,1489399652);
        // System.out.println("diff: " + diff.toString());

        // System.out.println("\nScoreMap: " + ScoreMap.toString());
        // graph.createAndShowGUI();
    }

    public String getEntityURI(String label) {
        return dbAccess.getEntityURI(label);
    }

    public String getPropertyURI(String label) {
        return dbAccess.getPropertyURI(label);
    }

    public List<String> getPropertyValues(String propertyTypeURI) {
        return dbAccess.getPropertyValues(propertyTypeURI);
    }

    public List<String> getPropertyValuesSortedByPopularity(String propertyTypeURI) {
        return dbAccess.getPropertyValuesSortedByPopularity(propertyTypeURI);
    }

    public List<String> getBestProperties(String userID, String propertyTypeURI) {
        return dbAccess.getBestProperties(userID, propertyTypeURI);
    }
    
    public boolean hasMessage(String messageID) {
    	return dbAccess.getMessageID(messageID);
    }
    
    public String getLastTestMessageID() {
    	return dbAccess.getLastTestMessageID();
    }

}