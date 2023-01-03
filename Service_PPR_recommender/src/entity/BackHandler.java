package entity;

import dialog.DialogState;
import functions.EntityRecommendation;
import functions.LogService;
import functions.LogService.EventType;
import functions.ResponseService;
import replies.EntityRecommendationReply;
import replies.MenuReply;
import replies.PropertyRatingReply;
import replies.RefineEntityPropertyReply;
import replies.Reply;

public class BackHandler {

	private String userID;
	private String messageID;
	private String text;
	
	private EntityRecommendation entityRecommendation;
	
	private Reply reply;
	
	private DialogState state;
	private LogService logService;
	
	public BackHandler(String userID, String messageID, String text, EntityRecommendation entityRecommendation, DialogState state, LogService logService) throws Exception {
		
		this.userID = userID;
		this.messageID = messageID;
		this.text = text;
		this.entityRecommendation = entityRecommendation;
		this.state = state;
		this.logService = logService;
		
		handle();
	}
	
	private void handle() throws Exception {
		
		ResponseService rs = new ResponseService();
		
		String lowerText = text.toLowerCase();
		System.out.println("debug: " + lowerText);
		
		// Ritorna al film raccomandato
		if (lowerText.contains(rs.getEntityTypePluralMessage(true))) {
			int page = state.getPage();
			reply = new EntityRecommendationReply(this.entityRecommendation, page, false, userID, messageID, state, logService, null, -1);
			logService.addEvent(EventType.RECOMMENDATION);
			logService.addEvent(EventType.QUESTION);
			state.setCritiquing(false);

		// Ritorna alla home
		} else if (lowerText.contains("home") || lowerText.contains("start")) {
			reply = new MenuReply(userID);
			state.setGenericProperties(true);
			state.setPropertyToRate(null);
			state.setEntityToRate(null);
			
		// Ritorna al full menu delle properties
		} else if (lowerText.contains("property types") || lowerText.contains("short list")) {
			reply = new PropertyRatingReply(userID, state);
			logService.addEvent(EventType.PREFERENCE);
			if (state.isCritiquing()) {
				logService.addEvent(EventType.RECOMMENDATION);
			}
			logService.addEvent(EventType.QUESTION);
			logService.addEvent(EventType.DISAMBIGUATION);

		// Ritorna al refine o al film raccomandato
		} else {
			String entity = state.getEntityToRecommend();

			if (entity != null) {
				reply = new RefineEntityPropertyReply(userID, entity);
			} else {
				reply = new EntityRecommendationReply(this.entityRecommendation, false, userID, messageID, state, logService, null, -1);
			}
			logService.addEvent(EventType.QUESTION);
		}
	}
	
	public Reply getReply() {
		
		return reply;
	}
}
