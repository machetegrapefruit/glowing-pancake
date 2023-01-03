package replies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;

import configuration.Configuration;
import entity.AuxAPI;
import entity.Entity;
import entity.Message;
import entity.ReplyMarkup;
import functions.EntityService;
import functions.PropertyService;
import functions.ResponseService;
import utils.EmojiCodes;
import utils.TextUtils;

public class EntityDetailsReply implements Reply {

	Message[] messages;
	ReplyMarkup replyMarkup;
	
	public EntityDetailsReply(String entity, boolean shortVersion) throws Exception {
		System.out.println("DEBUG shortVersion: " + shortVersion);
		String thinkingCode = EmojiCodes.hexHtmlSurrogatePairs.get("thinking");
		
		String entityURI = "http://dbpedia.org/resource/" + entity.replace(" ", "_");
		
		EntityService entityService = new EntityService();
		ResponseService responseService = new ResponseService();
		Entity entityDetails = entityService.getEntityDetails(entityService.getEntityURI(TextUtils.getNameFromURI(entityURI)));
		System.out.println("entityDetails: " + entityDetails);

		if (entityDetails == null) {
			messages = new Message[] {
					new Message("Sorry...\nI'm not able to find details " + thinkingCode)
			};
		} else {
			
			Map<String, List<String>> typeURIToDetails = new HashMap<String, List<String>>();
			for (String propertyTypeURI : Configuration.getDefaultConfiguration().getPropertyTypesDetails()) {
				List<String> details = entityDetails.get(propertyTypeURI);
				typeURIToDetails.put(propertyTypeURI, details);
			}

			PropertyService propertyService = new PropertyService();
			
			String text = "";
			List<String> title = entityDetails.get(Configuration.getDefaultConfiguration().getPropertyTypeName());
			if (title != null) {
				text += "*" + title.get(0) + "* "; 
			} else {
				text += "*" + WordUtils.capitalize(entity) + "* ";
			}
			List<String> releaseYear = entityDetails.get(Configuration.getDefaultConfiguration().getPropertyTypeReleaseYear());
			if (releaseYear != null) {
				text += "*(" + releaseYear.get(0) + ")*";
			}
			text += "\n";
			List<String> runtimeMinutes = entityDetails.get(Configuration.getDefaultConfiguration().getPropertyTypeRuntimeMinutes());
			if (runtimeMinutes != null && !shortVersion) {
				text += "_" + runtimeMinutes.get(0) + " min_ \n\n";
			}
			final int maxPropertiesShortVersion = 3;
			int count = 0;
			for (String propertyTypeURI : Configuration.getDefaultConfiguration().getPropertyTypesDetails()) {
				if (!propertyTypeURI.equals(Configuration.getDefaultConfiguration().getPropertyTypeName())
						&& !propertyTypeURI.equals(Configuration.getDefaultConfiguration().getPropertyTypeReleaseYear())
						&& !propertyTypeURI.equals(Configuration.getDefaultConfiguration().getPropertyTypeImage())
						&& !propertyTypeURI.equals(Configuration.getDefaultConfiguration().getPropertyTypeTrailer())
						&& !propertyTypeURI.equals(Configuration.getDefaultConfiguration().getPropertyTypeRuntimeMinutes())
						&& (!shortVersion || count++ < maxPropertiesShortVersion)) {
				
					List<String> values = entityDetails.get(propertyTypeURI);
					
					System.out.println("propertyTypeURI: " + propertyTypeURI + " -> values: " + values);
					if (values != null && !values.isEmpty() && !(shortVersion && propertyTypeURI.equals("plot"))) {
						if (shortVersion) {
							values = values.subList(0, Math.min(2, values.size()));
						}
						String propertyTypeLabel = Configuration.getDefaultConfiguration().getPropertyTypesLabels().get(propertyTypeURI);
						
						List<String> valueLabels = new ArrayList<String>();
						if (!shortVersion) {
							text += TextUtils.getEmoji(propertyTypeURI) + " ";
						}
						text += "*" + WordUtils.capitalize(propertyTypeLabel) + "*: ";

						for (String URI : values) {
							// Controllo se è un property object
							String label = propertyService.getPropertyLabel(URI);
							if (label == null) {
								// Era già un valore, non un URI (es. plot)
								label = URI;
							}
							if (label != null) {
								valueLabels.add(WordUtils.capitalize(label));
							}
						}
						text += String.join(", ", valueLabels);
						text += "\n";
						if (!shortVersion) {
							text += "\n";
						}
					}
				}
			}
			System.out.println("text: " + text);

			List<Message> messages = new ArrayList<Message>();
			
			List<String> image = entityDetails.get(Configuration.getDefaultConfiguration().getPropertyTypeImage());
			if (image != null) {
				String imageURL = image.get(0);
				if (imageURL != "" && imageURL != "N/A") {
					messages.add(new Message("", imageURL));
				} else {
					// Nessun poster trovato
					messages.add(new Message("", "defaultImage"));
				}
			} else {
				// Nessun poster trovato
				messages.add(new Message("", "defaultImage"));
			}
			
			messages.add(new Message(text));
			
			List<String> trailer = entityDetails.get(Configuration.getDefaultConfiguration().getPropertyTypeTrailer());
			if (trailer != null) {
				String trailerURL = trailer.get(0);
				if (trailerURL != "" && trailerURL != "N/A" && !shortVersion) {
					messages.add(new Message(responseService.getLinkLabel(), null, trailerURL));
				}
			}
			
			this.messages = new Message[messages.size()];
			for (int i = 0; i < this.messages.length; i++) {
				this.messages[i] = messages.get(i);
			}
			
			this.replyMarkup = null;
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
