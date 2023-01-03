package replies;

import java.util.ArrayList;
import java.util.List;

import configuration.Configuration;
import dialog.DialogState;
import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import functions.EntityRecommendation;
import functions.LogService;
import functions.LogService.EventType;
import functions.ProfileService;
import functions.ResponseService;
import keyboards.EntityRecommendationKeyboard;
import keyboards.StartProfileAcquisitionKeyboard;
import keyboards.UserPropertyValueKeyboard;
import restService.GetRatings;
import utils.EmojiCodes;

public class EntityRecommendationReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
	private AuxAPI auxAPI;
	
	EntityRecommendation entityRecommendation;
	boolean updatePagerank;
		
	public EntityRecommendationReply(EntityRecommendation entityRecommendation,
			boolean updatePagerank, String userID, String messageID, DialogState state, LogService logService, String previousEntity, int previousRating) throws Exception {

		this(entityRecommendation, entityRecommendation.getPage(), updatePagerank, userID, messageID, state, logService, previousEntity, previousRating);
	}
	
	public EntityRecommendationReply(EntityRecommendation entityRecommendation, int page,
			boolean updatePagerank, String userID, String messageID, DialogState state, LogService logService, String previousEntity, int previousRating) throws Exception {
		
		this.entityRecommendation = entityRecommendation;
		this.updatePagerank = updatePagerank;
		List<Message> messages = new ArrayList<Message>();
		
		ResponseService responseService = new ResponseService();
		
		if (page <= 5) {
			
			entityRecommendation.setPage(page);

			if (updatePagerank) {
				logService.addEvent(EventType.NEW_RECOMMENDATION_CYCLE);

				String winkCode = EmojiCodes.getEmojis().get(EmojiCodes.WINK);
				messages.add(new Message("Please wait. I'm working for you " + winkCode + "\n(Please, don't send more messages)"));
				replyMarkup = null;
				
				String apiURL = Configuration.getDefaultConfiguration().getRecSysServiceBasePath()
						+ "/aux/recommendation"
						+ "?userID=" + userID
						+ "&messageID=" + messageID
						+ "&timestamp=1500000000"
						+ "&botName=movierecsysbot"
						+ "&page=" + page;
				
				auxAPI = new AuxAPI(apiURL);
				
			} else {
				
				String rating = null;
				if (previousRating == 1) {
					rating = "Like";
				} else if (previousRating == 0) {
					rating = "Dislike";
				}
				
				if (rating != null) {
					String wink = EmojiCodes.getEmojis().get(EmojiCodes.WINK);
					messages.add(new Message("You " + rating + " \"" + previousEntity + "\" " + responseService.getEntityTypeSingularMessage(true) + " " + wink));
				}
				logService.addEvent(EventType.RECOMMENDATION);

				System.out.println("[EntityRecommendationReply] entityToRecommend: " + entityRecommendation.getEntityToRecommend());
				
				Reply reply = new EntityDetailsReply(entityRecommendation.getEntityToRecommend(), true);
							
				for (Message m : reply.getMessages()) {
					messages.add(m);
				}
				
				replyMarkup = new ReplyMarkup(new EntityRecommendationKeyboard(userID, page));
			}
			
		} else {
			
			String rating = null;
			if (previousRating == 1) {
				rating = "Like";
			} else if (previousRating == 0) {
				rating = "Dislike";
			}
			
			if (rating != null) {
				String wink = EmojiCodes.getEmojis().get(EmojiCodes.WINK);
				messages.add(new Message("You " + rating + " \"" + previousEntity + "\" " + responseService.getEntityTypeSingularMessage(true) + " " + wink));
			}
			
			// I film da raccomandare sono finiti
			String text = responseService.getEndRecommendationMessage();
			messages.add(new Message(text));
			
			GetRatings service = new GetRatings();
			int neededRatings = 3 - Integer.parseInt(service.getNumberRatedEntities(userID)) - Integer.parseInt(service.getNumberRatedProperties(userID));
			if (neededRatings > 0) {
				replyMarkup = new ReplyMarkup(new StartProfileAcquisitionKeyboard());
			} else {
				replyMarkup = new ReplyMarkup(new UserPropertyValueKeyboard());
			}
			
			ProfileService profileService = new ProfileService();
			boolean doQuestionnaire = Configuration.getDefaultConfiguration().isQuestionnaireEnabled();
			boolean questionnaireConditions = profileService.checkQuestionnaireConditions(userID);
			if (doQuestionnaire && questionnaireConditions) {
				// Inizia questionario
				state.setCurrentQuestionIndex(0);
			}
			
			logService.addEvent(EventType.FINISHED_RECOMMENDATION);
		}
		state.setGenericProperties(true);
		
		System.out.println("messages:\n" + messages);
		this.messages = new Message[messages.size()];
		for (int i = 0; i < this.messages.length; i++) {
			this.messages[i] = messages.get(i);
		}
		
	}
	
	@Override
	public Message[] getMessages() {
		return messages;
	}

	@Override
	public ReplyMarkup getReplyMarkup() {
		return replyMarkup;
	}

	@Override
	public AuxAPI getAuxAPI() {
		return auxAPI;
	}
}
