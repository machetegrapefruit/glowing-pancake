package test;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import graph.AdaptiveSelectionController;
import utils.FileUtils;

public class AnswersExporter {
	public static void main(String[] args) {
		String outputPath = args[0];
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		List<QuestionnaireAnswer> messagesList = asController.getAnswers();
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		String messagesJson = gson.toJson(messagesList.toArray(new QuestionnaireAnswer[messagesList.size()]), QuestionnaireAnswer[].class);
		FileUtils.writeToFile(outputPath, messagesJson);
	}
}
