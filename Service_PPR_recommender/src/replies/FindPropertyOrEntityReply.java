package replies;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.text.WordUtils;

import configuration.Configuration;
import dialog.DialogState;
import entity.AuxAPI;
import entity.Message;
import entity.Property;
import entity.ReplyMarkup;
import functions.EntityRatingAcquisition;
import functions.EntityService;
import functions.LogService;
import functions.LogService.EventType;
import functions.PropertyService;
import functions.UnknownTitleException;
import keyboards.CustomKeyboard;
import utils.DidYouMeanBaccaro;
import utils.EmojiCodes;

public class FindPropertyOrEntityReply implements Reply {

	Message[] messages;
	ReplyMarkup replyMarkup;
	
	public FindPropertyOrEntityReply(String userID, String name, 
			EntityRatingAcquisition entityRatingAcquisition, DialogState state, LogService logService) throws Exception {
				
		state.setTraining(false);
		
		PropertyService propertyService = new PropertyService();
		String propertyURI = propertyService.getPropertyURI(name);
		
		EntityService entityService = new EntityService();
		String entityURI = entityService.getEntityURI(name);
		
		String label = null;
		
		System.out.println("propertyURI: " + propertyURI);
		System.out.println("entityURI: " + entityURI);
		
		// Se è un'entità
		if (entityURI != null) {
			label = entityService.getEntityLabel(entityURI);
			String title = null;
			String poster = null;

			try {
				String[] titleAndPoster = entityRatingAcquisition.getTitleAndPoster(label);
				title = titleAndPoster[0];
				poster = titleAndPoster[1];
			} catch (UnknownTitleException e) {
				title = label;
				poster = null;
			}
				
			Reply reply = new EntityRatingReply(title, poster, userID);
			messages = reply.getMessages();
			replyMarkup = reply.getReplyMarkup();
			
			state.setEntityToRate(label);
			logService.addRecognizedObject(entityService.getEntityURI(label));
			logService.addEvent(EventType.PREFERENCE);
			logService.addEvent(EventType.QUESTION);
			logService.addEvent(EventType.DISAMBIGUATION);
		
			messages = reply.getMessages();
			replyMarkup = reply.getReplyMarkup();
			
		// Se è una proprietà
		} else if (propertyURI != null) {
			label = propertyService.getPropertyLabel(propertyURI);
			Map<String, List<String>> propertyTypes = propertyService.getPropertyTypes(propertyURI);
			System.out.println("propertyTypes: " + propertyTypes);

			if (!propertyTypes.isEmpty()) {
				List<String> types = propertyTypes.get(propertyURI);
				
				messages = new Message[] {
						new Message("Which property type are you referring to?")
								};
				String[] options = new String[types.size()];
				for (int i = 0; i < options.length; i++) {
					Map<String, String> typesLabels = Configuration.getDefaultConfiguration().getPropertyTypesLabels();
					options[i] = WordUtils.capitalize(typesLabels.get(types.get(i)));
				}
				replyMarkup = new ReplyMarkup(new CustomKeyboard(options));
				
				Property property = new Property(null, label);
				state.setPropertyToRate(property);
				logService.addRecognizedObject(propertyService.getPropertyURI(label));
				logService.addEvent(EventType.PREFERENCE);
				logService.addEvent(EventType.QUESTION);
				logService.addEvent(EventType.DISAMBIGUATION);
			} else {
				messages = new Message[] {
						new Message("I recognize this property, but it's not linked to any entity\n"
								+ "Please point this out to the developers")
				};
			}
			
		// Se c'è un errore di battitura
		} else {
			DidYouMeanBaccaro distanceCalculator = new DidYouMeanBaccaro();
			Map<String, Double> map = distanceCalculator.getDistance(name);
			
			messages = new Message[] {
					new Message("Did you mean:")
			};
			
			String[] options = new String[map.size() + 1];
			int i = 0;
			for (Entry<String, Double> entry : map.entrySet()) {
				String uri = entry.getKey();
				label = entityService.getEntityLabel(uri);
				if (label == null) {
					label = propertyService.getPropertyLabel(uri);
				}
				options[i++] = label;
			}
			options[options.length - 1] = EmojiCodes.getEmojis().get(EmojiCodes.BACKARROW) + 
					" Home";
			replyMarkup = new ReplyMarkup(new CustomKeyboard(options));
			
			logService.addEvent(EventType.PREFERENCE);
			logService.addEvent(EventType.QUESTION);
			logService.addEvent(EventType.DISAMBIGUATION);
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
		return null;
	}
}
