package replies;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import explanationService.Explanation;

public class ExplanationReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
	
	public ExplanationReply(String userID, String entity) throws Exception {
		
		Explanation service = new Explanation();
		String explanation = service.getExplanation(userID, entity);
		
		messages = new Message[] {
				new Message(explanation)
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
