package test;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import utils.FileUtils;

public class LogMessageAnalyzer {
	public static void main(String[] args) throws IOException {
		String logPath = args[0];
		String output = args[1];
		List<UserData> userDataList = new ArrayList<>();
		Map<String, List<LogMessage>> userMessages = new HashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		
    	FileReader reader = new FileReader(logPath);
		JsonReader jReader = new JsonReader(reader);
		jReader.beginArray();
		//Suddivido i messaggi per utente (nota: i messaggi sono già ordinati per timestamp crescente)
		while (jReader.hasNext()) {
			LogMessage message = gson.fromJson(jReader, LogMessage.class);
			if (userMessages.containsKey(message.getUserID())) {
				userMessages.get(message.getUserID()).add(message);
			} else {
				List<LogMessage> list = new ArrayList<>();
				list.add(message);
				userMessages.put(message.getUserID(), list);
			}
		}
		
		//Per ogni utente
		for (Entry<String, List<LogMessage>> entry: userMessages.entrySet()) {
			int trainQuestionsCount = 0;
			int recQuestionsCount = 0;
			int disambiguationsCount = 0;
			int preferencesCount = 0;
			int trainingPreferencesCount = 0;
			int trainingDisambiguationsCount = 0;
			long answerTimeTotal = 0;
			List<Long> interactionTimes = new ArrayList<>(); //Lista dei tempi di interazione per ogni sessione
			long interactionTimeStart = 0;
			boolean questionAsked = false; //È usato per determinare se il messaggio precedente era una domanda
			List<LogMessage> messagesList = entry.getValue();
			Set<String> recognizedObjects = new HashSet<>();
			System.out.println("Results for user " + entry.getKey());
			for (int i = 0; i < messagesList.size(); i++) {
				LogMessage message = messagesList.get(i);
				String[] events = message.getEvents().split(",");
				
				//Se era stata fatta una domanda, calcolare il tempo impiegato dall'utente a rispondere
				if (questionAsked) {
					long questionEndTime = messagesList.get(i - 1).getTimestampEnd(); //timestamp_end del messaggio precedente
					long answerStartTime = messagesList.get(i).getTimestampStart(); //timestamp_start del messaggio corrente
					long answerTime = answerStartTime - questionEndTime;
					if (answerTime < 0) {
						answerTime = messagesList.get(i - 1).getTimestampStart() - answerStartTime;
						System.out.println("answerTime less than 0! Question is " + messagesList.get(i - 1).getMessage() + " Answer is " + messagesList.get(i).getMessage());
					}
					answerTimeTotal += answerTime;
				}
				
				if (hasEvent(events, "question")) {
					if (hasEvent(events, "recommendation") || hasEvent(events, "new_recommendation_cycle") || hasEvent(events, "finished_recommendation")) {
						recQuestionsCount++;
					} else {
						trainQuestionsCount++;
					}
					questionAsked = true;
				} else {
					questionAsked = false;
				}
				if (hasEvent(events, "preference")) {
					if (!hasEvent(events, "recommendation") && !hasEvent(events, "new_recommendation_cycle") && !hasEvent(events, "finished_recommendation")) {
						trainingPreferencesCount++;
					}
					preferencesCount++;
				}
				if (hasEvent(events, "disambiguation")) {
					if (!hasEvent(events, "recommendation") && !hasEvent(events, "new_recommendation_cycle") && !hasEvent(events, "finished_recommendation")) {
						trainingDisambiguationsCount++;
					}
					disambiguationsCount++;
				}
				if (hasEvent(events, "finished_recommendation")) {
					//Una sessione è completata, calcolare il tempo complessivo di interazione
					interactionTimes.add(message.getTimestampEnd() - interactionTimeStart);
					interactionTimeStart = 0; //Resetto, segnalando l'inizio di una nuova sessione
				}
				if (interactionTimeStart == 0) {
					interactionTimeStart = message.getTimestampEnd();
				}
				for (String r: message.getRecognizedObjects().split(",")) {
					if (r.endsWith("+") || r.endsWith("-") || r.endsWith("/")) {
						recognizedObjects.add(r.trim());
					}
				}
			}
			if (interactionTimeStart != 0) {
				//Se c'è una sessione non completata, la aggiungo comunque
				interactionTimes.add(messagesList.get(messagesList.size() - 1).getTimestampEnd() - interactionTimeStart);
			}
			int totalQuestionsCount = recQuestionsCount + trainQuestionsCount;
			long totalInteractionTime = calculateTotalInteractionTime(interactionTimes);
			UserData userData = new UserData(
					entry.getKey(), 
					trainQuestionsCount, 
					recQuestionsCount,
					((double) answerTimeTotal /  totalQuestionsCount),
					totalInteractionTime,
					((double) recognizedObjects.size() / messagesList.size()),
					((double) recognizedObjects.size() / preferencesCount),
					((double) disambiguationsCount / preferencesCount),
					((double) trainingDisambiguationsCount / trainingPreferencesCount ),
					((double) totalInteractionTime / interactionTimes.size())
					);
			userDataList.add(userData);
			System.out.println("Number of training questions: " + trainQuestionsCount);
			System.out.println("Number of recommendation questions: " + recQuestionsCount);
			System.out.println("Query Efficiency: " + ((double) disambiguationsCount / preferencesCount ));
			System.out.println("Query Efficiency (training only): " + ((double) trainingDisambiguationsCount / trainingPreferencesCount ));
			System.out.println("Average answer time: " + ((double) answerTimeTotal /  totalQuestionsCount));
			System.out.println("Duration of each session: " + interactionTimes);
			System.out.println("Number of unique concepts: " + recognizedObjects.size());
			System.out.println("Query density: " 
					+ ((double) recognizedObjects.size() / messagesList.size()) + " (total) "
					+ ((double) recognizedObjects.size() / preferencesCount) + " (per preference messages)" 
					);
		}
		
		String csv = UserData.getCSVHeader() + "\n";
		for (UserData u: userDataList) {
			csv += u.toCSVRow() + "\n";
		}
		FileUtils.writeToFile(output, csv);
	}
	
	private static long calculateTotalInteractionTime(List<Long> interactionTimes) {
		long total = 0;
		for (Long it: interactionTimes) {
			total += it;
		}
		return total;
	}
	
	private static boolean hasEvent(String[] events, String event) {
		for(String e: events) {
			if (e.trim().equals(event)) {
				return true;
			}
		}
		return false;
	}
}
