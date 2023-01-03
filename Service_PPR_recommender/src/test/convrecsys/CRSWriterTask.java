package test.convrecsys;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import utils.FileUtils;

public class CRSWriterTask implements Runnable {
	private ConcurrentLinkedQueue<CRSRecommendationConversation> recQueue;
	private String outputPath;
	private boolean finished = false;
	private String tempPath;
	public CRSWriterTask(ConcurrentLinkedQueue<CRSRecommendationConversation> recQueue, String outputPath, String tempPath) {
		this.recQueue = recQueue;
		this.outputPath = outputPath;
		this.tempPath = tempPath;
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
						CRSRecommendationConversation current = recQueue.poll();
						gson.toJson(current, CRSRecommendationConversation.class, jWriter);
						/* Obbligo il JsonWriter a scrivere l'oggetto appena creato, in modo da evitare che 
						 * una terminazione del processo lasci un oggetto incompleto*/						 
						jWriter.flush();
						FileUtils.appendToFile(tempPath, current.getId() + "\n");
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
				CRSRecommendationConversation current = recQueue.poll();
				gson.toJson(current, CRSRecommendationConversation.class, jWriter);
				jWriter.flush();
				FileUtils.appendToFile(tempPath, current.getId());
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
