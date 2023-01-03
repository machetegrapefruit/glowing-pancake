package test;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import graph.AdaptiveSelectionController;
import utils.FileUtils;

public class RecEntityRatingsExporter {
	public static void main(String[] args) {
		long timestampStart = Long.parseLong(args[0]);
		String outputPath = args[1];
		AdaptiveSelectionController asController = new AdaptiveSelectionController();
		List<RecEntityRating> messagesList = asController.getRecEntityRatingsLog(timestampStart);
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		String messagesJson = gson.toJson(messagesList.toArray(new RecEntityRating[messagesList.size()]), RecEntityRating[].class);
		FileUtils.writeToFile(outputPath, messagesJson);
	}
}
