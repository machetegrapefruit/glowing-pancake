package test;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import utils.FileUtils;

public class AnswersAnalyzer {
	public static void main(String[] args) throws IOException {
		String path = args[0];
		String output = args[1];
		int maxQuestionIndex = 0;
		
		Map<String, List<QuestionnaireAnswer>> userMessages = new HashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		
    	FileReader reader = new FileReader(path);
		JsonReader jReader = new JsonReader(reader);
		jReader.beginArray();
		while (jReader.hasNext()) {
			QuestionnaireAnswer answer = gson.fromJson(jReader, QuestionnaireAnswer.class);
			maxQuestionIndex = Math.max(maxQuestionIndex, answer.getQuestionID());
			if (userMessages.containsKey(answer.getUserID() + "")) {
				userMessages.get(answer.getUserID() + "").add(answer);
			} else {
				List<QuestionnaireAnswer> list = new ArrayList<>();
				list.add(answer);
				userMessages.put(answer.getUserID() + "", list);
			}
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(getCSVHeader(maxQuestionIndex + 1) + "\n");
		for (Entry<String, List<QuestionnaireAnswer>> entry: userMessages.entrySet()) {
			int[] userAnswers = new int[maxQuestionIndex + 1];
			for (QuestionnaireAnswer a: entry.getValue()) {
				userAnswers[a.getQuestionID()] = a.getAnswerID();
			}
			sb.append(getCSVRow(entry.getKey(), userAnswers) + "\n");
		}
		FileUtils.writeToFile(output, sb.toString());
	}
	
	private static String getCSVHeader(int numQuestions) {
		StringJoiner sj = new StringJoiner(",");
		sj.add("user_id");
		for (int i = 0; i < numQuestions; i++) {
			sj.add("Q" + i);
		}
		return sj.toString();
	}
	
	private static String getCSVRow(String userID, int[] answers) {
		StringJoiner sj = new StringJoiner(",");
		sj.add(userID);
		for (int a: answers) {
			sj.add(a + "");
		}
		return sj.toString();
	}
}
