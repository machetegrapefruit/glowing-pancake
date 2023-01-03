package replies;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import utils.EmojiCodes;

public class DefaultReply implements Reply {

	Message[] messages;
	ReplyMarkup replyMarkup;
	
	private final static String confusedCode = EmojiCodes.hexHtmlSurrogatePairs.get("confused");
	private final static String defaultMessage = "Mi dispiace, devo ancora implementare questa funzionalità " + confusedCode;

	
	public DefaultReply() {

		this(defaultMessage);
	}
	
	public DefaultReply(String override) {
		messages = new Message[] {
				new Message(override)
		};
		replyMarkup = null;
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
		return null;
	}

}
