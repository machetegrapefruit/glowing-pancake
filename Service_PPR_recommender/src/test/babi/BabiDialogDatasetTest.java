package test.babi;

import java.io.FileReader;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class BabiDialogDatasetTest {
	private static final int NUM_THREADS = 5;
	public static void main(String[] args) throws Exception {
		int userID = 1234567890;
		String datasetPath = args[0];
		String outputPath = args[1];
		boolean forceSentiment = false;
		boolean forceEntities = false;
		if (args.length == 3 && args[2].equals("-forceSentiment")) {
			forceSentiment = true;
			System.out.println("Forced sentiment mode activated");
		} else if (args.length == 3 && args[2].equals("-forceEntities")) {
			forceEntities = true;
			System.out.println("Forced entities mode activated");
		}
		FileReader reader = new FileReader(datasetPath);
		JsonReader jReader = new JsonReader(reader);
		jReader.beginArray();
		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		ConcurrentLinkedQueue<BabiDialogRecommendation> recQueue = new ConcurrentLinkedQueue<BabiDialogRecommendation>();
		ConcurrentLinkedQueue<String> userIDQueue = new ConcurrentLinkedQueue<String>();
		for (int i = 1234567890; i < 1234567890 + NUM_THREADS * 2; i++) {
			userIDQueue.offer(i + "");
		}
		
		BabiDialogWriterTask writerTask = new BabiDialogWriterTask(recQueue, outputPath);
		new Thread(writerTask).start();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		while (jReader.hasNext()) {
			BabiSentence sentence = gson.fromJson(jReader, BabiSentence.class);
			
			if (sentence.getEntities().length >= 1) {
				BabiDialogRecommenderTask task = new BabiDialogRecommenderTask(recQueue, userIDQueue, sentence, forceSentiment, forceEntities);
				executor.execute(task);
				userID++;
				if (userID >= 1234567890 + 1500) {
					userID = 1234567890;
				}
			}
		}
		jReader.endArray();
		jReader.close();
		
		executor.shutdown();
		System.out.println("Called executor.shutdown!");
		boolean finished = executor.awaitTermination(30, TimeUnit.DAYS);
		System.out.println("awaitTermination() finished!");
		writerTask.setFinished(finished);
	}

}
