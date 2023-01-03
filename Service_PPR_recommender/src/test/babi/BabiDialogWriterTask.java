package test.babi;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

public class BabiDialogWriterTask implements Runnable {
	private ConcurrentLinkedQueue<BabiDialogRecommendation> recQueue;
	private String outputPath;
	private boolean finished = false;
	public BabiDialogWriterTask(ConcurrentLinkedQueue<BabiDialogRecommendation> recQueue, String outputPath) {
		this.recQueue = recQueue;
		this.outputPath = outputPath;
	}
	
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		FileWriter writer;
		long startTime = System.currentTimeMillis();
		try {
			int added = 0;
			writer = new FileWriter(outputPath);
			JsonWriter jWriter = new JsonWriter(writer);
			jWriter.beginArray();
			jWriter.setIndent("  ");
			Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			while (!finished) {
				if (recQueue.isEmpty()) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					while (!recQueue.isEmpty()) {
						BabiDialogRecommendation current = recQueue.poll();
						gson.toJson(current, BabiDialogRecommendation.class, jWriter);
						added++;
					}
					if (added > 0) {
						long currentTime = System.currentTimeMillis();
						double interval = (currentTime - startTime) / 1000;
						System.out.println("Done " + added + " tasks in " + interval + " seconds ");
					}
				}
			}
			//Ultimo loop dopo il segnale di completamento
			while (!recQueue.isEmpty()) {
				BabiDialogRecommendation current = recQueue.poll();
				gson.toJson(current, BabiDialogRecommendation.class, jWriter);
				added++;
			}
			if (added > 0) {
				long currentTime = System.currentTimeMillis();
				double interval = (currentTime - startTime) / 1000;
				System.out.println("Done " + added + " tasks in " + interval + " seconds ");
			}
			
			
			jWriter.endArray();
			jWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
