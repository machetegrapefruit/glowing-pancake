package test.convrecsys;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import entity.Pair;

public class CRSDifferencesFinder {
	public static void main(String[] args) throws IOException {
		FileReader reader = new FileReader(args[0]);
		JsonReader jReader = new JsonReader(reader);
		Map<Index, Integer> mapCount = new HashMap<>();
		Set<Index> toFind = new HashSet<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		jReader.beginArray();
		int totRecommendations = 0;
		while (jReader.hasNext()) {
			CRSConversation conv = gson.fromJson(jReader, CRSConversation.class);
			String convId = conv.getId();
			int turnIndex = 0;
			for (CRSTurn turn: conv.getTurns()) {
				int feedbacks = countFeedbacks(turn);
				Index i = new Index(convId, turnIndex);
				mapCount.put(i, feedbacks);
				toFind.add(i);
				turnIndex++;
				totRecommendations += feedbacks;
			}
		}
		System.out.println("Number of turns: " + toFind.size());
		System.out.println("Number of recommendations: " + totRecommendations);
		
		reader = new FileReader(args[1]);
		jReader = new JsonReader(reader);
		jReader.beginArray();
		while (jReader.hasNext()) {
			CRSRecommendationConversation conv = gson.fromJson(jReader, CRSRecommendationConversation.class);
			String convId = conv.getId();
			int turnIndex = 0;
			for (CRSRecommendationTurn turn: conv.getTurns()) {
				Index i = new Index(convId, turnIndex);
				if (turn != null) {
					if (turn.getRecommendations() != null) {
						int numRecommendations = turn.getRecommendations().length;
						Integer originalCount = mapCount.get(i);
						if (originalCount != null && originalCount != numRecommendations) {
							System.out.println("Found difference in conversation " + convId + " turn " + turnIndex + "!");
						} else if (mapCount.get(i) == null) {
							System.out.println("Conversation " + convId + " turn " + turnIndex + " not found!");
						}

					} else {
						System.out.println("Conversation " + convId + " turn " + turnIndex + " has no recommendations!");
					}
				} else {
					System.out.println("Conversation " + convId + " turn " + turnIndex + " is null!");
				}
				toFind.remove(i);
				turnIndex++;
			}
		}
		
		System.out.println("Remaining turns: ");
		for (Index i: toFind) {
			System.out.println("Conversation " + i.getConvId() + " turn " + i.getTurnIndex());
		}
	}
	
	private static int countFeedbacks(CRSTurn turn) {
		int count = 0;
		for (CRSMessage message: turn.getMessages()) {
			if (message.getFeedback() > -1) {
				count++;
			}
		}
		return count;
	}
	
	private static class Index {
		private String convId;
		private int turnIndex;
		public Index(String convId, int turnIndex) {
			super();
			this.convId = convId;
			this.turnIndex = turnIndex;
		}
		public String getConvId() {
			return convId;
		}
		public int getTurnIndex() {
			return turnIndex;
		}
		@Override
		public boolean equals(Object o) {
			Index oIndex = (Index) o;
			return convId.equals(oIndex.getConvId()) && turnIndex == oIndex.getTurnIndex();
		}
		@Override
		public int hashCode() {
			return convId.hashCode() + turnIndex;
		}
		@Override
		public String toString() {
			return "Index [convId=" + convId + ", turnIndex=" + turnIndex + "]";
		}
	}
}
