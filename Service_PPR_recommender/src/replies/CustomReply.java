package replies;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;

/**
 * Modella una risposta da inviare al client.
 * Contiene le informazioni che servono al client per mostrare uno o più messaggi tramite il bot.
 * Rispetta le specifiche dell'interfaccia Platform definita nel client, che ha bisogno di:
 * text - Il testo da inviare al bot
 * keyboard - Le opzioni da fornire all'utente per interagire con il bot
 * resizeKeyboard e oneTimeKeyboard - parametri booleani che definiscono il comportamento della keyboard
 * Questa classe funge da incapsulamento per i suddetti dati.
 * @author Altieri
 *
 */
public class CustomReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
	private AuxAPI auxAPI;
	
	public CustomReply(Message[] messages) {
		this(messages, null, null);
	}
	
	public CustomReply(Message[] messages, ReplyMarkup replyMarkup) {
		this(messages, replyMarkup, null);
	}
	
	public CustomReply(Message[] messages, ReplyMarkup replyMarkup, AuxAPI auxAPI) {
		this.messages = messages;
		this.replyMarkup = replyMarkup;
		this.auxAPI = auxAPI;
	}
	
	public Message[] getMessages() {
		return this.messages;
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