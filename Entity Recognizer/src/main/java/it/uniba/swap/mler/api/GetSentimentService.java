package it.uniba.swap.mler.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonObject;

import it.uniba.swap.mler.entityrecognizer.SentimentMentionFinder;
import it.uniba.swap.mler.entityrecognizer.SentimentMentionMap;

@Path("/getSentiment")
public class GetSentimentService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSentiment(@QueryParam("text")String text) {
		SentimentMentionFinder ef = new SentimentMentionFinder();
		int sentiment = ef.getSentiment(text);
		JsonObject json = new JsonObject();
		json.addProperty("sentiment", sentiment);
		return json.toString();
	}
}
