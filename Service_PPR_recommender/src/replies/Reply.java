package replies;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;

public interface Reply {

	Message[] getMessages();
	
	ReplyMarkup getReplyMarkup();
	
	AuxAPI getAuxAPI();
}
