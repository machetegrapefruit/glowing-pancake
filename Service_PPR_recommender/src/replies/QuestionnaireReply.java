package replies;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;

import configuration.Configuration;
import entity.AuxAPI;
import entity.Message;
import entity.Pair;
import entity.ReplyMarkup;
import functions.ProfileService;
import functions.QuestionnaireService;
import functions.ResponseService;
import keyboards.CustomKeyboard;
import keyboards.Keyboard;
import questionnaire.Question;
import utils.EmojiCodes;

public class QuestionnaireReply implements Reply {

	private Message[] messages;
	private ReplyMarkup replyMarkup;
	private AuxAPI auxAPI;
	
	public QuestionnaireReply(String userID, int questionIndex) {
		
		List<String> messagesList = new ArrayList<String>();
		List<String> answers = new ArrayList<String>();
		QuestionnaireService questionnaireService = new QuestionnaireService();

		if (questionIndex == -1) {
			// Risposta di fine questionario
			messagesList.add("The questionnaire is over. Thanks for your contribution.");
			
			if (Configuration.getDefaultConfiguration().getInteractionType().equals("buttons")) {
				answers.add(EmojiCodes.getEmojis().get(EmojiCodes.BACKARROW) + " Home");
			}
		} else {
			Question question = questionnaireService.getQuestion(questionIndex);
			String label = question.getLabel();
			Pair<Integer, String>[] possibleAnswers = question.getPossibleAnswers();
			
			// Controllo se devo mostrare il profilo
			boolean showProfile = question.isShowProfile();
			String profileSpeech = null;
			if (showProfile) {
				messagesList.add("Please, check the preferences in your profile:");
				
				ResponseService responseService = new ResponseService();
				JsonArray profile = new ProfileService().getUserProfile(userID);
				profileSpeech = responseService.getShowProfileMessage(profile, false, false, false);
				messagesList.add(profileSpeech);
			}
			
			messagesList.add(label);
			
			for (Pair<Integer, String> pair : possibleAnswers) {
				answers.add(pair.value);
			}
			
			
		}		
		
		messages = new Message[messagesList.size()];
		for (int i = 0; i < messages.length; i++) {
			messages[i] = new Message(messagesList.get(i));
		}

		String[][] options = new String[(int) Math.ceil(answers.size() / 2.0)][];

		int count = 0;
		for (int row = 0; row < options.length; row++) {
			if (count == answers.size() - 1) {
				options[row] = new String[] {
						answers.get(count)
				};
				count++;
			} else {
				options[row] = new String[] {
						answers.get(count),
						answers.get(count + 1)
				};
				count += 2;
			}
		}
		Keyboard keyboard = new CustomKeyboard(options);
		replyMarkup = answers.size() == 0 ? null : new ReplyMarkup(keyboard, true, true);
		
		auxAPI = null;
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
		return auxAPI;
	}

}
