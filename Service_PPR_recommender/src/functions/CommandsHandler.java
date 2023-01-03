package functions;

import java.util.ArrayList;
import java.util.List;

import dialog.DialogState;
import entity.Message;
import entity.ReplyMarkup;
import keyboards.Keyboard;
import replies.CustomReply;
import replies.Reply;
import replies.StartProfileAcquisitionReply;
import replies.UserPropertyValueReply;
import restService.Delete;
import restService.GetRatings;
import restService.Users;
import utils.EmojiCodes;

/**
 * Gestisce i casi in cui l'utente abbia inviato un command.
 * I command definiti per questo bot sono "/start", "/info", "/help", e "/reset".
 * Per definirne altri, aggiungerli alla enum, e definire il loro comportamento nel metodo handle().
 * @author Altieri
 *
 */
public class CommandsHandler {

	String userID;
	String firstname;
	String command;
	
	private ResponseService responseService;
	
	private DialogState state;
	
	/**
	 * Costruisce un nuovo CommandsHandler
	 * @param command I command sicuramente accettati sono "/start", "/info", "/help", e "/reset".
	 * Aggiungendo command alla enum, verranno automaticamente accettati.
	 * Variazioni come "start" o "Start" dovrebbero essere accettati ma non è garantito.
	 * @throws Exception Se il command non è valido.
	 */
	public CommandsHandler(String userID, String firstname, String command, DialogState state) throws Exception {
		this.userID = userID;
		this.firstname = firstname;
		this.command = "";
		for (Command comm : Command.values()) {
			if (command.toLowerCase().contains(comm.toString())) {
				this.command = command;
			}
		}
		if (this.command.equals("")) {
			throw new Exception("Command " + command + " is undefined.");
		}
		this.state = state;
		this.responseService = new ResponseService();
	}
	
	public Reply handle() throws Exception {
		Reply reply = null;

		String command = this.command.toLowerCase();
		if (command.contains("/start")) {
			reply = startCommand();
		} else if (command.contains("/info")) {
			reply = infoCommand();
		} else if (command.contains("/help")) {
			reply = helpCommand();
		} else if (command.contains("/reset")) {
			reply = resetCommand();
		} else {
			reply = unknownCommand();
		}
		
		return reply;
	}
	
	private Reply unknownCommand() throws Exception {
		Reply reply = null;
		
		Reply startCommandReply = startCommand();
		Keyboard startCommandKeyboard = startCommandReply.getReplyMarkup().getKeyboard();
		
		String confusedCode = EmojiCodes.hexHtmlSurrogatePairs.get("confused");
		
		String text = "Sorry, command was not recognized " + confusedCode;
		
		Message replyMessage = new Message(text);
		ReplyMarkup replyMarkup = new ReplyMarkup(startCommandKeyboard, true, false);
		reply = new CustomReply(new Message[] {replyMessage}, replyMarkup);
		
		return reply;
	}
	
	private Reply startCommand() throws Exception {
		Reply reply = null;
		
		GetRatings service = new GetRatings();
		String ratedEntities = service.getNumberRatedEntities(userID);
		String ratedProperties = service.getNumberRatedProperties(userID);
		final int TOTAL_NEEDED_RATINGS = 3;
		int neededRatings = TOTAL_NEEDED_RATINGS - (Integer.parseInt(ratedEntities) + Integer.parseInt(ratedProperties));
		boolean hasPositiveRating = (new ProfileService()).hasPositiveRating(userID);

		if (neededRatings <= 0 && hasPositiveRating) {
			reply = new UserPropertyValueReply(userID, firstname);
		} else {
			reply = new StartProfileAcquisitionReply(userID, firstname);
		}
		state.setGenericProperties(true);
		
		return reply;
	}
	
	private Reply infoCommand() throws Exception {
		Reply reply = null;
		
		ResponseService rs = new ResponseService();
		
		String text = rs.getInfoMessage();
		Message infoMessage = new Message(text);
		
		Reply startCommandReply = startCommand();
		Message[] startCommandMessages = startCommandReply.getMessages();
		Message[] replyMessages = new Message[startCommandMessages.length + 1];
		replyMessages[0] = infoMessage;
		for (int i = 0; i < startCommandMessages.length; i++) {
			replyMessages[i + 1] = startCommandMessages[i];
		}
		reply = new CustomReply(replyMessages);
		return reply;
	}
	
	private Reply helpCommand() throws Exception {
		Reply reply = null;
				
		String greeting = responseService.getGreetingMessage(firstname);
		
		String text = "Here you find the available commands";
		
		String commands = "/help - Help - Info & commands\n";
		commands += "/info - Information about the Bot\n";
		commands += "/reset - Reset all rated entities or properties\n";
		commands += "/start - Bot Start";
		
		Message[] helpMessages = new Message[] {
			new Message(greeting),
			new Message(text),
			new Message(commands)
		};
		
		reply = new CustomReply(helpMessages);
		
		return reply;
	}
	
	private Reply resetCommand() throws Exception {
		Reply reply = null;		

		String confusedCode = EmojiCodes.hexHtmlSurrogatePairs.get("confused");
		List<Message> messages = new ArrayList<Message>();
		Keyboard keyboard = null;
		
		Delete deleteService = new Delete();
		Reply startCommandReply = null;
		
		ResponseService rs = new ResponseService();

		if (command.equalsIgnoreCase("/reset entities")) {
			
			deleteService.deleteAllEntityRated(userID);
			startCommandReply = startCommand();
			
			String text = "All right " + firstname + ", I deleted all your " + rs.getEntityTypeSingularMessage(true) + " preferences";
			messages.add(new Message(text));
			for (Message startCommandReplyMessage : startCommandReply.getMessages()) {
				messages.add(startCommandReplyMessage);
			}
		} else if (command.equalsIgnoreCase("/reset properties")) {
			
			deleteService.deleteAllPropertyRated(userID);
			startCommandReply = startCommand();

			String text = "All right " + firstname + ", I deleted all your preferences";
			messages.add(new Message(text));
			for (Message startCommandReplyMessage : startCommandReply.getMessages()) {
				messages.add(startCommandReplyMessage);
			}
		} else if (command.equalsIgnoreCase("/reset all")) {
			
			deleteService.deleteAllProfile(userID);
			startCommandReply = startCommand();

			String text = "All right " + firstname + ", I deleted all your preferences";
			messages.add(new Message(text));
			for (Message startCommandReplyMessage : startCommandReply.getMessages()) {
				messages.add(startCommandReplyMessage);
			}
		} else {

			startCommandReply = startCommand();

			String text = confusedCode + " Reset format is incorrect";
			messages.add(new Message(text));
			String format = "You can use the following format:\n";
			format += "- /reset entities\n- /reset properties\n- /reset all";
			messages.add(new Message(format));
		}
		Message[] replyMessages = new Message[messages.size()];
		for (int i = 0; i < messages.size(); i++) {
			replyMessages[i] = new Message(messages.get(i).getText(), messages.get(i).getPhoto());
		}
		
		keyboard = startCommandReply.getReplyMarkup().getKeyboard();
		
		ReplyMarkup replyMarkup = new ReplyMarkup(keyboard, true, false);
		reply = new CustomReply(replyMessages, replyMarkup);
		return reply;
	}
	
	/*
	 * Contiene i possibili command per questa applicazione.
	 */
	private enum Command {
		start,
		info,
		help,
		reset
	}
}
