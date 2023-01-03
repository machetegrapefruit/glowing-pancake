package functions;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.mahout.math.Arrays;

import com.google.gson.Gson;

import arq.rset;
import configuration.Configuration;
import dialog.DialogFacade;
import dialog.DialogState;
import entity.BackHandler;
import entity.DeleteType;
import entity.Message;
import entity.Property;
import entity.PropertyRatingManager;
import entity.ReplyMarkup;
import entity.UserStorage;
import functions.EntityRatingAcquisition.AllPopularEntitiesRatedException;
import functions.LogService.EventType;
import keyboards.EntityRecommendationKeyboard;
import keyboards.RatedEntityNewUserKeyboard;
import keyboards.RatedEntityOldUserKeyboard;
import replies.CustomReply;
import replies.DefaultReply;
import replies.EntityDetailsReply;
import replies.EntityRatingReply;
import replies.EntityRecommendationReply;
import replies.ExplanationReply;
import replies.FindPropertyOrEntityReply;
import replies.HelpReply;
import replies.MenuReply;
import replies.MoreReply;
import replies.ProfileReply;
import replies.ProfileReply.ProfileType;
import replies.PropertyRatingReply;
import replies.PropertyValueRatingReply;
import replies.PropertyValueReply;
import replies.QuestionnaireReply;
import replies.RefineEndReply;
import replies.RefineEntityPropertyReply;
import replies.RefinePropertyTypeReply;
import replies.Reply;
import replies.ResetConfirmReply;
import replies.ResetProfileReply;
import replies.ResetReply;
import replies.UserEntityRatingReply;
import replies.UserPropertyValueRatingReply;
import restService.GetRatings;
import restService.Users;
import utils.EmojiCodes;
import utils.FormatUtils;
import utils.TextUtils;
import utils.URIUtils;
import utils.UserRating;

/**
 * Analizza il messaggio dell'utente e determina il testo e la keyboard da fornire.
 * @author Altieri
 *
 */
@SuppressWarnings("deprecation")
public class UserMessageHandler {

	protected String userID;
	protected String firstname;
	protected String lastName;
	protected String userName;
	protected String botName;
	protected String messageID;
	protected String text;
	protected String timeStamp;
	
	private static final String INTERACTION_TYPE = Configuration.getDefaultConfiguration().getInteractionType();
	
	protected LogService logService;
	
	public UserMessageHandler(String userID, String firstname, String lastName, String userName, String botName) {
		this.userID = userID;
		this.firstname = firstname;
		this.botName = botName;
		this.messageID = "";
		this.text = "";
		this.timeStamp = "";
		
		if (lastName == null) {
			this.lastName = "";
		} else {
			this.lastName = lastName;
		}
		if (userName == null) {
			this.userName = "";
		} else {
			this.userName = userName;
		}
		
		this.logService = new LogService(userID);
	}
	
	public void setMessage(String messageID, String text, String timeStamp) throws InvalidMessageException {
//		if (!UserStorage.hasMessage(userID, messageID)) {
		if (!logService.hasMessage(messageID)) {
			this.messageID = messageID;
			this.text = text;
			this.timeStamp = timeStamp;
		} else {
			//return;
			throw new InvalidMessageException(messageID);
		}
	}
	
	public Reply handle() throws Exception {
				
		Reply reply = null;
		
		Users putUserDetail = new Users();
		putUserDetail.putUserDetail(userID, firstname, lastName, userName);
		
		logService.setMessageID(messageID);
		logService.setMessage(text);
		logService.setTimestampStart(System.currentTimeMillis()); // viene ricevuto in secondi anzichè millisecondi
		logService.setInteractionType(Configuration.getDefaultConfiguration().getInteractionType());
		
		DialogStateService dialogStateService = new DialogStateService();
		DialogState state = dialogStateService.getDialogState(userID);
		if (state == null) {
			state = new DialogState(userID);
		}
		
		final boolean doQuestionnaire = Configuration.getDefaultConfiguration().isQuestionnaireEnabled();
		QuestionnaireService questionnaireService = new QuestionnaireService();

		UserStorage.addMessage(userID, messageID);

		if (!doQuestionnaire || state.getCurrentQuestionIndex() < 0) {
			if (INTERACTION_TYPE.equals("buttons")) {
				
				reply = handleButtonMessage(state, logService);
				try {
					dialogStateService.saveDialogState(userID, state);
				} catch (Exception e) {
					System.err.println("Impossibile salvare DialogState");
				}
				logService.setTimestampEnd(System.currentTimeMillis());
				logService.insertMessageInLog();
			} else {
								
				reply = handleNLMessage(state, logService);
			}
		} else {
			int questionId = state.getCurrentQuestionIndex();
			int answerId = questionnaireService.findAnswerId(state.getCurrentQuestionIndex(), text);
			if (answerId != -1) {
				// Processare la risposta dell'utente al questionario
				questionnaireService.insertAnswer(userID, questionId, answerId);
				// Incrementare currentQuestionIndex
				state.setCurrentQuestionIndex(state.getCurrentQuestionIndex() + 1);
			} // altrimenti non ha fornito una delle risposte possibili, rinviare la stessa domanda
		}
		
		// Questo è se il questionario è iniziato in questo momento, quindi bisogna anche mostrare la risposta normale
		int questions = questionnaireService.getQuestionsCount();
		Reply questionReply = null;

		if (doQuestionnaire && state.getCurrentQuestionIndex() >= 0 && state.getCurrentQuestionIndex() < questions) {
			// Visualizzare prossima domanda del questionario
			questionReply = new QuestionnaireReply(userID, state.getCurrentQuestionIndex());
			// Impilare i messaggi alla risposta normale e sostituire keyboard
			if (reply != null) {
				reply = new CustomReply(
						(Message[]) ArrayUtils.addAll(reply.getMessages(), questionReply.getMessages()),
						questionReply.getReplyMarkup(),
						null);
			} else {
				reply = questionReply;
			}
			
		} else if (doQuestionnaire && state.getCurrentQuestionIndex() >= questions) {
			System.out.println("Il questionario è finito");
			// Impostare currentQuestionIndex a -1 nello state
			state.setCurrentQuestionIndex(-1);
			// Mando reply di fine questionario
			reply = new QuestionnaireReply(userID, -1);
		}
		
		if (reply.getAuxAPI() != null) {
			reply.getAuxAPI().setMessageID(messageID);
		}
		
		dialogStateService.saveDialogState(userID, state);
		
		//Aggiorno timestamp_end, pagerank_cicle e number_recommendation_list del messaggio di richiesta raccomandazione a questo istante
		ProfileService ps = new ProfileService();
		int newPRCycle = ps.getPagerankCycle(userID);
		int newNumRecList = ps.getNumberRecommendationList(userID);
		logService.updateLogMessage(userID, messageID, System.currentTimeMillis(), newPRCycle, newNumRecList);			
				
		return reply;
	}
	
	private String[] getEntityToRateAndPoster(EntityRatingAcquisition entityRatingAcquisition) throws UnknownTitleException, AllPopularEntitiesRatedException, Exception {
				
		entityRatingAcquisition.obtainEntityToRate();
		entityRatingAcquisition.putEntityToRate();
		
		String[] titleAndPoster = entityRatingAcquisition.getTitleAndPoster(entityRatingAcquisition.getEntityToRate());

		System.out.println("Ottengo l'entità da votare: " + titleAndPoster[0]);
		System.out.println("Ottengo il poster: " + titleAndPoster[1]);
		
		return titleAndPoster;
	}

	private void log(Reply reply) {
		System.out.println("Il server ha ricevuto questo testo:\n*****\n" + text);
		System.out.println("*****\n\n");
		Message[] replyMessages = reply.getMessages();
		System.out.println("Reply contiene questi messaggi:\n**********");
		if (replyMessages != null) {
			for (Message m : replyMessages) {
				System.out.println(m.getText());
			}
		}		
		System.out.println("**********\n\nE questo replyMarkup:\n**********");
		if (reply.getReplyMarkup() != null) {
			String[][] keyboardOptions = reply.getReplyMarkup().getKeyboard().getOptions();
			for (String[] list : keyboardOptions) {
				System.out.print("{ ");
				for (String string : list) {
					System.out.print(string + " ; ");
				}
				System.out.print("}\n");
			}
		}
		System.out.println("************\n\nE questo auxAPI:\n**********");
		Gson gson = new Gson();
		System.out.println(gson.toJson(reply.getAuxAPI()));
		System.out.println("**********");
	}
	
	private Reply handleNLMessage(DialogState state, LogService logService) {
		
		Reply reply = null;
		
		try {
			reply = DialogFacade.getReply(userID, messageID, text, state, logService);
			if (reply == null) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			reply = new CustomReply(new Message[] {
					new Message("Something went bad :/")
			}, null);
			e.printStackTrace();
		}
		
		return reply;
	}
	
	/*
	 * Ha lo stesso compito che prima era affidato alla funzione messageDispatcher() nel client.
	 */
	private Reply handleButtonMessage(DialogState state, LogService logService) throws Exception {
				
		Reply reply = null;
		Map<String, String> emojis = EmojiCodes.getEmojis();
						
		EntityRatingAcquisition entityRatingAcquisition = new EntityRatingAcquisition(userID, state);
		EntityRecommendation entityRecommendation = new EntityRecommendation(userID, timeStamp, botName, state);
		
		EntityService entityService = new EntityService();
		PropertyService propertyService = new PropertyService();
		ProfileService profileService = new ProfileService();
		ResponseService responseService = new ResponseService();
		
		// Commands
		if (text.toLowerCase().startsWith("/start") || text.toLowerCase().startsWith("/help") || 
				text.toLowerCase().startsWith("/info") || text.toLowerCase().startsWith("/reset")) {
			CommandsHandler commandsHandler = new CommandsHandler(userID, firstname, text, state);
			reply = commandsHandler.handle();
			
		// Home
		} else if (text.toLowerCase().equals("start") || text.toLowerCase().equals("home") ||
				text.toLowerCase().equals("menu") || text.toLowerCase().equals("preferences")) {
			reply = new MenuReply(userID);
			state.setGenericProperties(true);
			state.setPropertyToRate(null);
			state.setEntityToRate(null);
			
		// Rate Entities
		} else if (text.startsWith(emojis.get(EmojiCodes.BLUE_CIRCLE))) {
			state.setTraining(true);
			String[] entityAndPoster = null;
			try {
				entityAndPoster = getEntityToRateAndPoster(entityRatingAcquisition);
				String entity = entityAndPoster[0];
				String poster = entityAndPoster[1];	
				reply = new EntityRatingReply(entity, poster, userID);
				
			} catch (AllPopularEntitiesRatedException e) {
				reply = new CustomReply(new Message[] {
						new Message("I'm sorry, you have rated all popular " + responseService.getEntityTypePluralMessage(true) + ".\n"
								+ "You can still rate " + responseService.getEntityTypePluralMessage(true) + " by searching for them directly.")
				});
			}
			
			for (Message m : reply.getMessages()) {
				System.out.println("text: " + m.getText());
				System.out.println("photo: " + m.getPhoto());
			}
			logService.addEvent(EventType.PREFERENCE);
			logService.addEvent(EventType.QUESTION);
			state.setGenericProperties(false);
			
		// Details (Rate Entities)
		} else if (text.startsWith(emojis.get(EmojiCodes.CLIPBOARD))) {
			String entity = entityRatingAcquisition.getEntityToRateSelected();
			System.out.println("Invio la detailsRequest");
			entityRatingAcquisition.putDetailsRequest();
			System.out.println("detailsRequest inviata");
			System.out.println("Creo la EntityDetailsReply");
			reply = new EntityDetailsReply(entity, false);
			
			GetRatings service = new GetRatings();
			int neededRatings = 3 - Integer.parseInt(service.getNumberRatedEntities(userID)
					+ Integer.parseInt(service.getNumberRatedProperties(userID)));
			ReplyMarkup replyMarkup = null;
			boolean hasPositiveRating = (new ProfileService()).hasPositiveRating(userID);
			
			if (neededRatings > 0 || !hasPositiveRating) {
				replyMarkup = new ReplyMarkup(new RatedEntityNewUserKeyboard());
			} else {
				replyMarkup = new ReplyMarkup(new RatedEntityOldUserKeyboard());
			}
			reply = new CustomReply(reply.getMessages(), replyMarkup);
			logService.addEvent(EventType.QUESTION);
			
		// Like (Rate Entities)
		} else if (text.startsWith(emojis.get(EmojiCodes.THUMBSUP))) {
			String entity = entityRatingAcquisition.getEntityToRateSelected();
			String poster = entityRatingAcquisition.getTitleAndPoster(entity)[1];
			System.out.println("***Like entity: " + entity);
			System.out.println("poster: " + poster);
			
			int rating = 1;
			String lastChange = "user";
			
			entityRatingAcquisition.putEntityRating(rating, lastChange);

			UserEntityRatingReply partialReply = new UserEntityRatingReply(URIUtils.getNameFromURI(entity), userID, rating, logService);
			
			if (state.isTraining()) {
				try {
					String[] entityAndPoster = getEntityToRateAndPoster(entityRatingAcquisition);
					String newEntity = entityAndPoster[0];
					String newPoster = entityAndPoster[1];	
					
					partialReply.addReplyMessages(new EntityRatingReply(newEntity, newPoster, userID));
				} catch (AllPopularEntitiesRatedException e) {
					partialReply.addReplyMessages(new CustomReply(new Message[] {
							new Message("I'm sorry, you have rated all popular " + responseService.getEntityTypePluralMessage(true) + ".\n"
									+ "You can still rate " + responseService.getEntityTypePluralMessage(true) + " by searching for them directly.")
					}));
				} catch (UnknownTitleException e) {
					partialReply.addReplyMessages(new CustomReply(new Message[] {
							new Message("I'm sorry, I can't find the " + responseService.getEntityTypeSingularMessage(true))
					}));
				}
				reply = partialReply;

			} else {
				Reply menuReply = new MenuReply(userID);
				Message[] messages = (Message[]) ArrayUtils.addAll(partialReply.getMessages(), menuReply.getMessages());
				reply = new CustomReply(messages, menuReply.getReplyMarkup());
				state.setGenericProperties(true);
			}
			logService.addEvent(EventType.PREFERENCE);
			logService.addEvent(EventType.QUESTION);

		// Dislike (Rate Entities)
		} else if (text.startsWith(emojis.get(EmojiCodes.THUMBSDOWN))) {
			String entity = entityRatingAcquisition.getEntityToRateSelected();
			String poster = entityRatingAcquisition.getTitleAndPoster(entity)[1];
			
			System.out.println("***Dislike entity: " + entity);
			System.out.println("poster: " + poster);

			int rating = 0;
			String lastChange = "user";
			
			entityRatingAcquisition.putEntityRating(rating, lastChange);
			
			UserEntityRatingReply partialReply = new UserEntityRatingReply(URIUtils.getNameFromURI(entity), userID, rating, logService);

			if (state.isTraining()) {
				try {
					String[] entityAndPoster = getEntityToRateAndPoster(entityRatingAcquisition);
					String newEntity = entityAndPoster[0];
					String newPoster = entityAndPoster[1];	
					
					partialReply.addReplyMessages(new EntityRatingReply(newEntity, newPoster, userID));
				} catch (AllPopularEntitiesRatedException e) {
					partialReply.addReplyMessages(new CustomReply(new Message[] {
							new Message("I'm sorry, you have rated all popular " + responseService.getEntityTypePluralMessage(true) + ".\n"
									+ "You can still rate " + responseService.getEntityTypePluralMessage(true) + " by searching for them directly.")
					}));
				} catch (UnknownTitleException e) {
					partialReply.addReplyMessages(new CustomReply(new Message[] {
							new Message("I'm sorry, I can't find the " + responseService.getEntityTypeSingularMessage(true))
					}));
				}
				reply = partialReply;

			} else {
				Reply menuReply = new MenuReply(userID);
				Message[] messages = (Message[]) ArrayUtils.addAll(partialReply.getMessages(), menuReply.getMessages());
				reply = new CustomReply(messages, menuReply.getReplyMarkup());
				state.setGenericProperties(true);
			}
			logService.addEvent(EventType.PREFERENCE);
			logService.addEvent(EventType.QUESTION);

		// Skip (Rate Entities)
		} else if (text.startsWith(emojis.get(EmojiCodes.ARROW_RIGHT))) {
			String entity = entityRatingAcquisition.getEntityToRateSelected();

			int rating = 2;
			String lastChange = "user";
			entityRatingAcquisition.putEntityRating(rating, lastChange);
			
			UserEntityRatingReply partialReply = new UserEntityRatingReply(URIUtils.getNameFromURI(entity), userID, rating, logService);

			if (state.isTraining()) {
				try {
					String[] entityAndPoster = getEntityToRateAndPoster(entityRatingAcquisition);
					String newEntity = entityAndPoster[0];
					String newPoster = entityAndPoster[1];	
					
					partialReply.addReplyMessages(new EntityRatingReply(newEntity, newPoster, userID));
				} catch (AllPopularEntitiesRatedException e) {
					partialReply.addReplyMessages(new CustomReply(new Message[] {
							new Message("I'm sorry, you have rated all popular " + responseService.getEntityTypePluralMessage(true) + ".\n"
									+ "You can still rate " + responseService.getEntityTypePluralMessage(true) + " by searching for them directly.")
					}));
				} catch (UnknownTitleException e) {
					partialReply.addReplyMessages(new CustomReply(new Message[] {
							new Message("I'm sorry, I can't find the " + responseService.getEntityTypeSingularMessage(true))
					}));
				}
				reply = partialReply;
			} else {
				reply = new MenuReply(userID);
				state.setGenericProperties(true);
			}
			
			logService.addEvent(EventType.QUESTION);
			
		// Rate Properties
		} else if (text.startsWith(emojis.get(EmojiCodes.RED_CIRCLE))
				|| text.startsWith("properties")) {
			
			reply = new PropertyRatingReply(userID, state);
			state.setCritiquing(false);
			logService.addEvent(EventType.PREFERENCE);
			logService.addEvent(EventType.QUESTION);
			logService.addEvent(EventType.DISAMBIGUATION);
			
		// Modifica la valutazione di un film valutato
		} else if (text.startsWith(Configuration.getDefaultConfiguration().getEntityEmoji())) {
			String emoji = Configuration.getDefaultConfiguration().getEntityEmoji();
			String rating = text.replace(emoji, "");
			String entityName = rating.split(" - ")[0].trim();

			entityRatingAcquisition.setEntityToRate(entityName);
			entityRatingAcquisition.putEntityToRate();
			String poster = entityRatingAcquisition.getTitleAndPoster(entityName)[1];
			
			reply = new EntityRatingReply(entityName, poster, userID);
			logService.addEvent(EventType.QUESTION);
				
		// Back
		} else if (text.startsWith(emojis.get(EmojiCodes.BACKARROW))) {
			BackHandler backHandler = new BackHandler(userID, messageID, text, entityRecommendation, state, logService);
			reply = backHandler.getReply();
			
		// Reset
		} else if (text.startsWith(emojis.get(EmojiCodes.HEAVY_X))) {
			reply = new ResetReply();
			logService.addEvent(EventType.QUESTION);
			
		// Delete all properties
		} else if (text.startsWith(emojis.get(EmojiCodes.BLACK_SQUARE_BUTTON))) {
			state.setDeleteType(DeleteType.PROPERTIES);
			reply = new ResetProfileReply(DeleteType.PROPERTIES);
			logService.addEvent(EventType.QUESTION);

		// Delete all entities
		} else if (text.startsWith(emojis.get(EmojiCodes.WHITE_SQUARE_BUTTON))) {
			state.setDeleteType(DeleteType.ENTITIES);
			reply = new ResetProfileReply(DeleteType.ENTITIES);
			logService.addEvent(EventType.QUESTION);

		// Delete all preferences
		} else if (text.startsWith(emojis.get(EmojiCodes.WASTE_BASKET))) {
			state.setDeleteType(DeleteType.ALL);
			reply = new ResetProfileReply(DeleteType.ALL);
			logService.addEvent(EventType.QUESTION);

		// Conferma eliminazione profilo
		} else if (text.startsWith(emojis.get(EmojiCodes.CHECK_MARK))) {
			DeleteType deleteType = state.getDeleteType();
			String confirm = "yes";
			
			ResetConfirmReply partialReply = new ResetConfirmReply(userID, firstname, deleteType, confirm);
			Reply profileReply = new ProfileReply(userID, ProfileType.RATE, state);
			partialReply.addReplyMessages(profileReply);
			partialReply.setReplyMarkup(profileReply.getReplyMarkup());
			reply = partialReply;

		// Annullazione eliminazione profilo
		} else if (text.startsWith(emojis.get(EmojiCodes.NO_ENTRY_SIGN))) {
			DeleteType deleteType = state.getDeleteType();
			String confirm = "no";
			
			ResetConfirmReply partialReply = new ResetConfirmReply(userID, firstname, deleteType, confirm);
			Reply profileReply = new ProfileReply(userID, ProfileType.RATE, state);
			partialReply.addReplyMessages(profileReply);
			partialReply.setReplyMarkup(profileReply.getReplyMarkup());
			reply = partialReply;

		// Recommend Entities
		} else if (text.startsWith(emojis.get(EmojiCodes.GLOBE_WITH_MERIDIANS))) {			
			reply = new EntityRecommendationReply(entityRecommendation, 1, true, userID, messageID, state, logService, null, -1);
			logService.addEvent(EventType.QUESTION);

		// Film raccomandato valutato positivamente
		} else if (text.startsWith(emojis.get(EmojiCodes.SMILEY))) {
			String previousEntity = state.getEntityToRecommend();
			logService.addRecognizedObject(entityService.getEntityURI(state.getEntityToRecommend()) + "+");
			int rating = 1;
			
			entityService.addRecommendedEntityPreference(userID, entityService.getEntityURI(state.getEntityToRecommend()), rating, "user");

			entityRecommendation.setPage(entityRecommendation.getPage() + 1);
			reply = new EntityRecommendationReply(entityRecommendation, false, userID, messageID, state, logService, previousEntity, rating);
			logService.addEvent(EventType.PREFERENCE);
			logService.addEvent(EventType.QUESTION);

		// Film raccomandato valutato negativamente
		} else if (text.startsWith(emojis.get(EmojiCodes.SLIGHTLY_SAD))) {
			String previousEntity = state.getEntityToRecommend();
			logService.addRecognizedObject(entityService.getEntityURI(state.getEntityToRecommend()) + "-");
			int rating = 0;

			entityService.addRecommendedEntityPreference(userID, entityService.getEntityURI(state.getEntityToRecommend()), rating, "user");

			entityRecommendation.setPage(entityRecommendation.getPage() + 1);
			reply = new EntityRecommendationReply(entityRecommendation, false, userID, messageID, state, logService, previousEntity, rating);
			logService.addEvent(EventType.PREFERENCE);
			logService.addEvent(EventType.QUESTION);

		// I like, but...
		} else if (text.startsWith(emojis.get(EmojiCodes.CYCLONE)) ||
				text.startsWith(emojis.get(EmojiCodes.MAGNIFYING_GLASS))) {
			String entity = state.getEntityToRecommend();
			System.out.println("DEBUG entity: " + entity);
			reply = new RefineEntityPropertyReply(userID, entity);
			state.setCritiquing(true);
			
			entityService.setRefine(userID, entityService.getEntityURI(entity));
			System.out.println("*****entity: " + entity + "entityURI: " + entityService.getEntityURI(entity));
			logService.addEvent(EventType.PREFERENCE);
			logService.addEvent(EventType.RECOMMENDATION);
			logService.addEvent(EventType.DISAMBIGUATION);
			logService.addEvent(EventType.QUESTION);

		// Details (Recommend Entities)
		} else if (text.startsWith(emojis.get(EmojiCodes.BOOKMARK_TAGS))) {
			String entity = state.getEntityToRecommend();
			entityRecommendation.putDetailsRequest();
			reply = new EntityDetailsReply(entity, false);
			
			ReplyMarkup replyMarkup = new ReplyMarkup(new EntityRecommendationKeyboard(userID, entityRecommendation.getPage()));
			reply = new CustomReply(reply.getMessages(), replyMarkup);
			
			entityService.setDetails(userID, entityService.getEntityURI(entity));
			System.out.println("*****entity: " + entity + "entityURI: " + entityService.getEntityURI(entity));
			logService.addEvent(EventType.RECOMMENDATION);
			logService.addEvent(EventType.QUESTION);

		// Why?
		} else if (text.startsWith(emojis.get(EmojiCodes.MEGAPHONE))) {
			String entity = state.getEntityToRecommend();
			reply = new ExplanationReply(userID, entity);
			
			ReplyMarkup replyMarkup = new ReplyMarkup(new EntityRecommendationKeyboard(userID, entityRecommendation.getPage()));
			reply = new CustomReply(reply.getMessages(), replyMarkup);
			
			entityService.setWhy(userID, entityService.getEntityURI(entity));
			logService.addEvent(EventType.RECOMMENDATION);
			logService.addEvent(EventType.QUESTION);

		// Change - Refocus
		} else if (text.startsWith(emojis.get(EmojiCodes.ANGER))) {
			profileService.doRefocus(userID);
			reply = new EntityRecommendationReply(entityRecommendation, 1, true, userID, messageID, state, logService, null, -1);
			logService.addEvent(EventType.QUESTION);

		// Profile
		} else if (text.startsWith(emojis.get(EmojiCodes.SILHOUETTE)) ) { // Recommend Entities -> Profile
			reply = new ProfileReply(userID, ProfileType.REC, state);
			
		} else if (text.startsWith(emojis.get(EmojiCodes.GEAR))) { // Menu -> Profile oppure Rate entities -> Profile
			reply = new ProfileReply(userID, ProfileType.RATE, state);
			
		// Help
		} else if (text.startsWith(emojis.get(EmojiCodes.BLUE_BOOK))) { // Rate entities -> Help
			String help = "rateEntitySelected";
			reply = new HelpReply(help);
		} else if (text.startsWith(emojis.get(EmojiCodes.GREEN_BOOK))) { // Recommend Entities -> Help
			String help = "recEntitySelected";
			reply = new HelpReply(help);
		} else if (text.startsWith(emojis.get(EmojiCodes.ORANGE_BOOK))) { // Profile -> Help
			String help = "profileSelected";
			reply = new HelpReply(help);
		
		// Like a una proprietà
		} else if (text.startsWith(emojis.get(EmojiCodes.SLIGHT_SMILE))) {
			reply = new UserPropertyValueRatingReply(userID, UserRating.LIKE, state, logService);
			logService.addEvent(EventType.PREFERENCE);
			state.setGenericProperties(true);
			if (state.isCritiquing()) {
				reply = new RefineEndReply(userID, state);
				state.setCritiquing(false);
				logService.addEvent(EventType.RECOMMENDATION);
			}

		// Dislike a una proprietà
		} else if (text.startsWith(emojis.get(EmojiCodes.EXPRESSIONLESS))) {
			reply = new UserPropertyValueRatingReply(userID, UserRating.DISLIKE, state, logService);
			logService.addEvent(EventType.PREFERENCE);
			state.setGenericProperties(true);
			if (state.isCritiquing()) {
				reply = new RefineEndReply(userID, state);
				state.setCritiquing(false);
				logService.addEvent(EventType.RECOMMENDATION);
			}

		// Indifferente a una proprietà
		} else if (text.startsWith(emojis.get(EmojiCodes.THINKING))) {
			reply = new UserPropertyValueRatingReply(userID, UserRating.INDIFFERENT, state, logService);
			state.setGenericProperties(true);
			if (state.isCritiquing()) {
				reply = new RefineEndReply(userID, state);
				state.setCritiquing(false);
			}

		// Rate Properties -> More (mostra tutti i tipi di proprietà)
		} else if (text.equalsIgnoreCase("more " + emojis.get(EmojiCodes.POINT_RIGHT))) {
			reply = new MoreReply(userID, state);
			logService.addEvent(EventType.PREFERENCE);
			logService.addEvent(EventType.QUESTION);
			logService.addEvent(EventType.DISAMBIGUATION);

		// Recommend Entities -> Next oppure -> Back (per vedere gli altri film raccomandati)
		} else if (text.startsWith(emojis.get(EmojiCodes.POINT_LEFT)) || text.startsWith("Next")) {
			int page = 0;
			if (text.contains("1")) page = 1;
			else if (text.contains("2")) page = 2;
			else if (text.contains("3")) page = 3;
			else if (text.contains("4")) page = 4;
			else if (text.contains("5")) page = 5;
			else page = 6;
			reply = new EntityRecommendationReply(entityRecommendation, page, false, userID, messageID, state, logService, null, -1);
			logService.addEvent(EventType.QUESTION);

		// Tipo di proprietà
		} else if (TextUtils.isPropertyType(text)) {
			reply = new PropertyValueReply(userID, TextUtils.getNormalizedPropertyType(text), state);
			logService.addEvent(EventType.PREFERENCE);
			logService.addEvent(EventType.QUESTION);
			logService.addEvent(EventType.DISAMBIGUATION);
			
		// Tipo di proprietà di un'entità specifica
		// Refine di un tipo di proprietà
		} else if (TextUtils.isEntityPropertyType(text)) {
			String propertyType = TextUtils.getEntityPropertyType(text);
			String entityName = state.getEntityToRecommend();
			System.out.println("Refine di un tipo di proprietà");
			System.out.println("PropertyType: " + propertyType);
			System.out.println("Entity name: " + entityName);
			reply = new RefinePropertyTypeReply(entityName, propertyType);
			logService.addEvent(EventType.PREFERENCE);
			logService.addEvent(EventType.RECOMMENDATION);
			logService.addEvent(EventType.DISAMBIGUATION);
			logService.addEvent(EventType.QUESTION);

		// Valore di una proprietà con l'emoji davanti
		} else if (TextUtils.isPropertyValue(text)) {
			
			String emoji = text.substring(0, 2);
			String propertyType = TextUtils.getPropertyTypeFromEmoji(emoji);
			String propertyValue = text.substring(3);
			if (propertyValue.contains(" - ")) {
				propertyValue = propertyValue.substring(0, propertyValue.indexOf(" - "));
			}
			System.out.println("Emoji: " + emoji + ", propertyType: " + propertyType);
			System.out.println("propertyValue: " + propertyValue);
			
			Property property = new Property(propertyType, propertyValue);
			state.setPropertyToRate(property);
			PropertyRatingManager.putProposedValueURI(propertyType, TextUtils.getURIFromType(propertyType));
			PropertyRatingManager.putProposedValueURI(propertyValue, TextUtils.getURIFromResourceCapitalize(propertyValue));
			reply = new PropertyValueRatingReply(propertyValue);
			logService.addEvent(EventType.PREFERENCE);
			if (state.isCritiquing()) {
				logService.addEvent(EventType.RECOMMENDATION);
			}
			logService.addRecognizedObject(propertyService.getPropertyURI(propertyValue));
			logService.addEvent(EventType.QUESTION);

		// Ricerca file o proprietà inserita
		} else if (!text.startsWith("/")) {
			
			reply = new FindPropertyOrEntityReply(userID, text, entityRatingAcquisition, state, logService);
			state.setGenericProperties(false);
			
		// Testo non riconosciuto
		} else {
			reply = new DefaultReply();
		}

		log(reply);
		
		// Fix photo URL
		for (Message m : reply.getMessages()) {
			if (m.getPhoto() != null) {
				m.setPhoto(FormatUtils.correctPhotoRes(m.getPhoto()));
			}
		}
		
		return reply;
	}
}
