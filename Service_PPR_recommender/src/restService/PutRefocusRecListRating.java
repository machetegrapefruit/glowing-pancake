package restService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletContext;
//import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
//import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import graph.AdaptiveSelectionController;

/**
 * Inserisce il film raccomandato nella tabella
 * @author Francesco
 *
 */

@Path("/userRefocusRecListRating")
public class PutRefocusRecListRating {
	
	//Tomcat non permette la put, ci sara' un modo per configurarlo attraverso web.xml
	//ma per il momento la camuffiamo in get
	@Context ServletContext servletContext;
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/putRefocusRecListRating")
	public String putRefocusRecListRating(@QueryParam("userID") String userID) throws Exception 
	{
		AdaptiveSelectionController asController = new AdaptiveSelectionController();

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
		//se il numero di film raccomandati valutati è zero puoi avviare il refocus
		asController.setRefocusRecListByUser(userID);
		asController.putLastChange(userID, "entity_rating");
		int numberRatedEntities = asController.getNumberRatedEntities(userID);
		
		Gson gson = new Gson();
		String json = gson.toJson(numberRatedEntities);
		
		System.out.print("/putEntityRating/");
		System.out.println(json);
		
		return json;		
	}	
}







