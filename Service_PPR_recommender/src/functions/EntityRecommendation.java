package functions;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gson.Gson;

import dialog.DialogState;
import recommendationService.Recommendation;
import restService.GetNumberService;
import restService.GetPropertyValueListFromPropertyType;
import restService.GetUserRecEntityList;
import restService.PutDetailsEntityRequest;
import restService.PutRecEntityToRating;
import utils.EmojiCodes;

public class EntityRecommendation {

	private String userID;
	private String date;
	private String botName;
	
	private DialogState state;

	private int page;
	private String entityToRecommend;
	private String[] top5EntityList;
	
	public EntityRecommendation(String userID, 
			String date, String botName, DialogState state) {
		
		this.userID = userID;
		this.date = date;
		this.botName = botName;
		
		this.page = 1;
		
		this.state = state;
	}
	
	public String getUserID() {
		return userID;
	}
	
	public void setPage(int page) {
		this.page = page;
		state.setPage(page);
		if (page <= 5) {

			if (state.getTop5Entities() != null) {
				state.setEntityToRecommend(state.getTop5Entities()[page - 1]);
				EntityService entityService = new EntityService();
				entityService.insertEntityToRate(userID, entityService.getEntityURI(state.getEntityToRecommend()), page - 1);
			}
		}	
	}
	
	public int getPage() {
		return state.getPage();
	}

	public void handle() throws Exception {
		
		
		top5EntityList = getUserTop5EntityList();
		System.out.println("[EntityRecommendation.handle()] top5EntityList: " + top5EntityList);
		
		state.setTop5Entities(top5EntityList);
		
		String entityName = null;
		if (top5EntityList.length < 5) {
			entityName = top5EntityList[top5EntityList.length - 1];
			page++;
		} else {
			entityName = top5EntityList[page];
		}
		entityToRecommend = entityName;

	}
	
	public void putRecEntityRating(String rating) throws Exception {

		String entity = state.getEntityToRecommend();
		System.out.println("Sto votando la seguente entità raccomandata: ");
		System.out.println(entity);
		
		EntityService service = new EntityService();
		String entityObjectUri = service.getEntityURI(entity);
		
		service.addRecommendedEntityPreference(userID, entityObjectUri, Integer.parseInt(rating), "user");
		System.out.println("Voto aggiunto");
	}
	
	public void pagerank() throws Exception {
		
		String[] top5 = null;
		try {
			top5 = getUserTop5EntityList();
			state.setTop5Entities(top5);
			entityToRecommend = top5[state.getPage() - 1];
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.top5EntityList = top5;
		
		state.setEntityToRecommend(entityToRecommend);
		state.setPage(1);
		
		System.out.println(entityToRecommend + " memorizzato in DialogState");

		// Salva il film come visualizzato
		putRecEntityToRating(entityToRecommend);
	}
	
	private String[] getPagerankForUserRecTop5EntityList() throws Exception {
		
		String propertyType = "entity";
		GetPropertyValueListFromPropertyType getter = new GetPropertyValueListFromPropertyType();
		String data = getter.getPropertyValueListFromPropertyType(userID, propertyType);
		System.out.println("getPagerankForUserRecTop5EntityList: " + data);
		Gson gson = new Gson();
		Map<Double, String> ordered = new TreeMap<Double, String>();
		@SuppressWarnings("unchecked")
		Map<String, String> list = gson.fromJson(data, Map.class);

		for (Entry<String, String> entry : list.entrySet()) {
			String score = entry.getKey();
			String value = entry.getValue()
					.replace("http://dbpedia.org/resource/", "")
					.replace("_", " ");
			ordered.put(Double.parseDouble(score), value);
			System.out.println(score + ": " + value);
		}
		
		String[] result = new String[ordered.size()];
		int i = ordered.size();
		for (Entry<Double, String> entry : ordered.entrySet()) {
			result[--i] = entry.getValue();
		}
		
		return result;
	}

	/**
	 * 
	 * @param entityName
	 * @return null if method fails.
	 * @throws Exception
	 */
	public String putRecEntityToRating(String entityName) throws Exception {
		String entityCameraCode = EmojiCodes.hexHtmlSurrogatePairs.get("movie_camera");
		String data = null;
		if (entityName != "null" && entityName != null) {
			entityName = entityName
					.replace(" ", "_")
					.replace(entityCameraCode + "_", "")
					.replace(entityCameraCode, "");
			String entityURI = "http://dbpedia.org/resource/" + entityName;
			
			GetNumberService getter = new GetNumberService();
			String numberRecommendationList = getter.getNumberRecommendationList(userID);
			System.out.println("numberRecommendationList: " + numberRecommendationList);
			int position = getPageFromEntityName(entityName);
			System.out.println("position: " + position);
			String pagerankCycle = getter.getNumberPagerankCicle(userID);
			
			PutRecEntityToRating putService = new PutRecEntityToRating();
			data = putService.putRecEntityToRating(userID, entityURI, numberRecommendationList, Integer.toString(position), pagerankCycle, botName, date);
		} else {
			System.out.println("[EntityRecommendation.putRecEntityToRating] Error");
			data = null;
		}
		System.out.println("data: " + data);
		return data;
	}
	
	private int getPageFromEntityName(String entityName) throws Exception {
		int page = 0;
		
		GetUserRecEntityList getter = new GetUserRecEntityList();
		String data = getter.getUserRecEntityList(userID);
				System.out.println("[getPageFromEntityName] data: " + data);
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, String> list = gson.fromJson(data, Map.class);
		Map<Double, String> ordered = new TreeMap<Double, String>();
		
		for (Entry<String, String> entry : list.entrySet()) {
			String property = entry.getValue()
					.replace("http://dbpedia.org/resource/", "")
					.replace("_", " ");
			ordered.put(Double.parseDouble(entry.getKey()), property);
		}
		
		int i = 0;
		for (Entry<Double, String> entry : ordered.entrySet()) {
			i++;
			if (entry.getValue().equals(entityName)) {
				page = i;
			}
		}
		
		return page;
	}
	
	private String[] getUserTop5EntityList() throws Exception {

		String[] top5 = new String[5];
		
		EntityService service = new EntityService();
		Recommendation recommendation = new Recommendation();
		String data = recommendation.getRecommendationList(userID);
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		List<String> entities = gson.fromJson(data, List.class);

		for (int i = 0; i < 5; i++) {
			String entity = entities.get(i);
			String label = service.getEntityLabel(entity);
			top5[i] = label;
		}
		
		return top5;
	}
	
//	public static String getRecEntitySelected(String userID) {
	public String getRecEntitySelected(String userID) {
		
//		String entity = UserStorage.getEntityToRecommend(userID);
		String entity = state.getEntityToRecommend();
		
		return entity;
	}
	
	public void putDetailsRequest() throws Exception {
//		String entity = UserStorage.getEntityToRecommend(userID);
		String entity = state.getEntityToRecommend();
		String entityURI = "http://dbpedia.org/resource/" + entity.replace(" ", "_");
		
		GetNumberService numberService = new GetNumberService();
		String numberRecommendationList = numberService.getNumberRecommendationList(userID);
		
		String details = "details";
		
		PutDetailsEntityRequest putDetailsEntityRequest = new PutDetailsEntityRequest();
		putDetailsEntityRequest.putDetailsEntityRequest(userID, entityURI, numberRecommendationList, details);
	}
	
	public String getEntityToRecommend() {

		return state.getTop5Entities()[state.getPage() - 1];
	}

}
