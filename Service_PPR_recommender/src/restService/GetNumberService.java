package restService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import graph.AdaptiveSelectionController;

@Path("/numbers")
public class GetNumberService {
	
	@Context ServletContext servletContext;
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/getNumberPagerankCicle")
	public String getNumberPagerankCicle (@QueryParam("userID") String userID) throws Exception 
	{
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		int numberPagerankCicle = asController.getNumberPagerankCicle(userID);
	
		Gson gson = new Gson();
		String json = gson.toJson(numberPagerankCicle);
		 
		System.out.print("/numbers/getNumberPagerankCicle/");
		System.out.println(json);
		
		return json;		
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/putNumberPagerankCicle")
	public String putNumberPagerankCicle (@QueryParam("userID") String userID,
										 @QueryParam("pagerankCicle") String pagerankCicle) throws Exception 
	{
		int pagerank_cicle = Integer.parseInt(pagerankCicle);
		
		AdaptiveSelectionController asController = new AdaptiveSelectionController();	
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		asController.putNumberPagerankCicleByUser(userID, pagerank_cicle);
		int numberPagerankCicle = asController.getNumberPagerankCicle(userID);
		
		Gson gson = new Gson();
		String json = gson.toJson(numberPagerankCicle);
		
		System.out.print("/numbers/putNumberPagerankCicle/");
		System.out.println(json);
		
		return json;
		
	}
	
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/getNumberRefineFromRecEntityList")
	public String getNumberRefineFromRecEntityList (@QueryParam("userID") String userID) throws Exception 
	{
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		int numberRatedRecEntity = asController.getNumberRefineFromRecEntityListByUserAndRecList(userID);
	
		Gson gson = new Gson();
		String json = gson.toJson(numberRatedRecEntity);
		 
		System.out.print("/numbers/getNumberRefineFromRecEntityList/");
		System.out.println(json);
		
		return json;		
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/getNumberRecommendationList")
	public String getNumberRecommendationList (@QueryParam("userID") String userID) throws Exception 
	{
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		int numberRecommendationList = asController.getNumberRecommendationList(userID);
	
		Gson gson = new Gson();
		String json = gson.toJson(numberRecommendationList);
		 
		System.out.print("/numbers/getNumberRecommendationList/");
		System.out.println(json);
		
		return json;		
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/putNumberRecommendationList")
	public String putNumberRecommendationList (@QueryParam("userID") String userID,
										 @QueryParam("numberRecommendationList") String numberRecommendationList) throws Exception 
	{
		int number_recommendation_list = Integer.parseInt(numberRecommendationList);
		
		AdaptiveSelectionController asController = new AdaptiveSelectionController();	
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		asController.putNumberRecommendationListByUser(userID, number_recommendation_list);
		int new_number_recommendation_list = asController.getNumberRecommendationList(userID);
		
		Gson gson = new Gson();
		String json = gson.toJson(new_number_recommendation_list);

		System.out.print("/numbers/putNumberRecommendationList/");
		System.out.println(json);
		
		return json;
		
	}
	
}
