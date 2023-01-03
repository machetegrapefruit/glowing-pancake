package restService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import dialog.DialogState;
import entity.Message;
import entity.ReplyMarkup;
import functions.DialogStateService;
import functions.EntityRecommendation;
import functions.LogService;
import functions.ProfileService;
import keyboards.EntityRecommendationKeyboard;
import keyboards.Keyboard;
import keyboards.KeyboardMarkup;
import replies.CustomReply;
import replies.EntityDetailsReply;
import replies.JsonReply;
import replies.Reply;
import utils.FormatUtils;

@Path("aux")
public class AuxServices {

	@Path("recommendation")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getRecommendationAuxRequest(
			@QueryParam("userID") String userID,
			@QueryParam("messageID") String messageID,
			@QueryParam("timestamp") String timestamp,
			@QueryParam("botName") String botName,
			@QueryParam("page") int page) {
		
		System.out.println("/recommendation");
		
		//String truncatedID = FormatUtils.truncateID(userID);
		
		Reply reply = null;
		DialogStateService dialogStateService = new DialogStateService();
		DialogState state = dialogStateService.getDialogState(userID);
		EntityRecommendation entityRecommendation = new EntityRecommendation(userID, timestamp, botName, state);
		
		try {
			entityRecommendation.pagerank();
			reply = new EntityDetailsReply(entityRecommendation.getEntityToRecommend(), true);
			ReplyMarkup replyMarkup = new ReplyMarkup(new EntityRecommendationKeyboard(userID, page));
			reply = new CustomReply(reply.getMessages(), replyMarkup, null);
		} catch (Exception e) {
			System.err.println("Errore nella auxRequest");
			e.printStackTrace();
		}
		
		try {
			dialogStateService.saveDialogState(userID, state);
		} catch (Exception e) {
			System.err.println("Impossibile salvare DialogState in AuxServices");
			e.printStackTrace();
		}
		
		Message[] messages = reply.getMessages();
		for (Message message : messages) {
			if (message.getPhoto() != null) {
				message.setPhoto(FormatUtils.correctPhotoRes(message.getPhoto()));
			}
		}
		ReplyMarkup rm = reply.getReplyMarkup();
		Keyboard keyboard = rm.getKeyboard();
		String[][] options = keyboard.getOptions();
		KeyboardMarkup km = new KeyboardMarkup(options);
		JsonReply jsonReply = new JsonReply(messages, km, null);
		String json = jsonReply.toJson();
		
		System.out.println("json: \n" + json);
		
		//Aggiorno timestamp_end, pagerank_cicle e number_recommendation_list del messaggio di richiesta raccomandazione a questo istante
		ProfileService ps = new ProfileService();
		int newPRCycle = ps.getPagerankCycle(userID);
		int newNumRecList = ps.getNumberRecommendationList(userID);
		new LogService().updateLogMessage(userID, messageID, System.currentTimeMillis(), newPRCycle, newNumRecList);			
		
		return json;
	}
}
