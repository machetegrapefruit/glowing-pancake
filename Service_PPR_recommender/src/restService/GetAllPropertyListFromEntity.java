package restService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import configuration.Configuration;
import entity.Entity;
import functions.EntityService;

/**
 * 
 * @param entityURI
 * L'url di DBpedia del film
 * @return
 * Un json contenente una {@code Map<String, List<String>>} contenente per ogni proprietà una lista di valori.
 * La chiave della mappa è l'URI della proprietà, cioè il codice utilizzato nel database, non la label.
 * Ad esempio per il titolo la entry può essere P1476=[Gladiator].
 * @throws Exception
 */
@Path("/entityDetail")
public class GetAllPropertyListFromEntity {
	
	@Context ServletContext servletContext;
	@GET
	@Produces({MediaType.APPLICATION_JSON, "text/json"})
	@Path("/getAllPropertyListFromEntity")
	public String getAllPropertyListFromEntity (@QueryParam("entityURI") String entityURI) throws Exception 
	{
		String entityName = entityURI.replace("http://dbpedia.org/resource/", "")
				.replace("_", " ");
		if (entityName.contains("(")) {
			entityName = entityName.substring(0, entityName.indexOf("(") - 1);
		}
		
		EntityService service = new EntityService();

		String entityObjectUri = service.getEntityURI(entityName);
		
		Map<String, List<String>> properties = new HashMap<String, List<String>>();
		
		Entity details = service.getEntityDetails(entityObjectUri);
		
		String[] propertyTypes = Configuration.getDefaultConfiguration().getPropertyTypesDetails();
		for (String propertyType : propertyTypes) {
			properties.put(propertyType, details.get(propertyType));
		}
		
		System.out.println(properties);
		
		Gson gson = new Gson();
		String json = gson.toJson("null");
		if (!properties.isEmpty()) {			
	  		json = gson.toJson(properties);
		}
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
		
  		System.out.print("/entityDetail/getAllPropertyListFromEntity/");
		if (json.equals("null")) {
			System.out.println(json);
		}
		else {
			System.out.println("ok");
		}
  		
  		return json;
	}

}
