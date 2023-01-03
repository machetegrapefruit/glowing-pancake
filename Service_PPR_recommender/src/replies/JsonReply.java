package replies;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import entity.AuxAPI;
import entity.Message;
import keyboards.KeyboardMarkup;
import utils.FormatUtils;

public class JsonReply {
	
	Message[] messages;
	KeyboardMarkup reply_markup;
	AuxAPI auxAPI;
	
	public JsonReply (Message[] messages, KeyboardMarkup replyKeyboard, AuxAPI auxAPI) {
		this.messages = messages;
		this.reply_markup = replyKeyboard;
		this.auxAPI = auxAPI;
	}
	
	public JsonReply(JsonObject nlData) {
		String text = (String) (nlData.get("speech").getAsString());
		JsonObject data = (JsonObject) nlData.get("data");
		String imageURL = data.get("image").getAsString();
		imageURL = FormatUtils.correctPhotoRes(imageURL);
		String imageCaption = data.get("imageCaption").getAsString();
		String postImageSpeech = data.get("postImageSpeech").getAsString();
		List<Message> messages = new ArrayList<Message>();
		messages.add(new Message(text));
		messages.add(new Message(imageCaption, imageURL));
		messages.add(new Message(postImageSpeech));
		this.messages = new Message[messages.size()];
		for (int i = 0; i < this.messages.length; i++) {
			this.messages[i] = messages.get(i);
		}
		reply_markup = null;
	}
	
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}