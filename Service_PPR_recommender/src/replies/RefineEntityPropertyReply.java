package replies;

import org.apache.commons.lang.WordUtils;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import functions.EntityService;
import functions.ResponseService;
import keyboards.RefineEntityPropertyKeyboard;
import restService.GetNumberService;
import restService.PutRefineRecEntityRating;
import utils.URIUtils;

public class RefineEntityPropertyReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
	
	public RefineEntityPropertyReply(String userID, String entity) throws Exception {
		
		ResponseService rs = new ResponseService();
		
		String entityURI = URIUtils.entityNameToURI(entity);

		GetNumberService get = new GetNumberService();
		String numberRecList = get.getNumberRecommendationList(userID);
		PutRefineRecEntityRating service = new PutRefineRecEntityRating();
		service.putRefineRecEntityRating(userID, entityURI, numberRecList, "refine");

		String text1 = "Refine additional properties of ";
		text1 += "\"" + WordUtils.capitalize(entity) + "\"";
		
		String text2 = "Which properties of the " + rs.getEntityTypeSingularMessage(true) + " you want to change?";
		
		messages = new Message[] {
				new Message(text1),
				new Message(text2)
		};
		replyMarkup = new ReplyMarkup(new RefineEntityPropertyKeyboard(userID, entity));
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
