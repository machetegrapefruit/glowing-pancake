package test.convrecsys;

import java.io.FileReader;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import utils.FileUtils;

public class CRSDatasetTest {
	private static final int NUM_THREADS = 8;
	public static void main(String[] args) throws Exception {
		int userID = 1234567890;
		String datasetPath = args[0];
		String outputPath = args[1];
		String problemsPath = args[3];
		String type = args[4];
		List<String> done = null;
		if (FileUtils.fileExists(args[2])) {
			int counter = 1;
			while (FileUtils.fileExists(outputPath + "_" + counter)) {
				counter++;
			}
			done = FileUtils.readFileAsList(args[2]);
			outputPath = outputPath + "_" + counter;
		}
		FileReader reader = new FileReader(datasetPath);
		JsonReader jReader = new JsonReader(reader);
		jReader.beginArray();
		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		ConcurrentLinkedQueue<CRSRecommendationConversation> recQueue = new ConcurrentLinkedQueue<>();
		ConcurrentLinkedQueue<String> userIDQueue = new ConcurrentLinkedQueue<String>();
		for (int i = 1234567890; i < 1234567890 + NUM_THREADS * 2; i++) {
			userIDQueue.offer(i + "");
		}
		
		CRSWriterTask writerTask = new CRSWriterTask(recQueue, outputPath, args[2]);
		new Thread(writerTask).start();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		while (jReader.hasNext()) {
			CRSConversation conversation = gson.fromJson(jReader, CRSConversation.class);
			if (done == null || !done.contains(conversation.getId())) {
				CRSRecommenderTask task = new CRSRecommenderTask(conversation, recQueue, userIDQueue, problemsPath, type);
				executor.execute(task);
				userID++;
				if (userID >= 1234567890 + 1500) {
					userID = 1234567890;
				}
			} else {
				System.out.println("Skipping " + conversation.getId() + " as it's already done");
			}
		}
		jReader.endArray();
		
		executor.shutdown();
		System.out.println("Called executor.shutdown!");
		boolean finished = executor.awaitTermination(10, TimeUnit.DAYS);
		writerTask.setFinished(finished);
	}
}
