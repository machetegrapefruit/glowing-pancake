package it.uniba.swap.mler.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import it.uniba.swap.mler.entityrecognizer.SentimentMentionFinder;
import it.uniba.swap.mler.entityrecognizer.SentimentMentionMap;

@Path("/getEntities")
public class GetEntitiesService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSentiment(@QueryParam("text")String text,
			@DefaultValue("true") @QueryParam("findEntities")boolean findEntities,
			@DefaultValue("true") @QueryParam("findPropertyTypes")boolean findPropertyTypes) {
		SentimentMentionFinder ef = new SentimentMentionFinder();
		List<SentimentMentionMap> mentions = ef.findEntities(text, false, false);
		Gson gson = new Gson();
		return gson.toJson(mentions);
	}
	
	public static void main(String[] args) {
		System.out.println(new GetEntitiesService().getSentiment("I like the Matrix", true, true));
	}
}
