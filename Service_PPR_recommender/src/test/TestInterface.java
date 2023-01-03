package test;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dialog.DialogFacade;
import dialog.DialogState;
import dialog.FilteredAlias;
import entity.Message;
import functions.*;
import functions.LogService.EventType;
import graph.AdaptiveSelectionController;
import replies.Reply;
import restService.GetFirstRecommendation;
import restService.GetReply;

public class TestInterface {
	private static int getMessageIDNumber() {
		LogService logService = new LogService("1234");
		String testMessageID = logService.getLastTestMessageID();
		if (testMessageID == null) {
			return 0;
		} else {
			int idNumber = Integer.parseInt(testMessageID.split("_")[1]);
			return idNumber + 1;
		}
	}
	
	public static void main(String args []) throws Exception {
		Scanner s = new Scanner(System.in);
		DialogState state = new DialogState("1234");
		ProfileService profileService = new ProfileService();
		int messageIDNumber = getMessageIDNumber();
		
		ContentBasedServiceSingleton contentBasedServiceSingleton = ContentBasedServiceSingleton.getInstance();
		while (true) {
			System.out.print(">");
			String text = s.nextLine();
			//TestUserMessageHandler tumh = new TestUserMessageHandler("1234", "test", "movierecsysbot");
			UserMessageHandler tumh = new UserMessageHandler("1234", "test", "test", "test", "movierecsysbot");
			String messageID = "test_" + messageIDNumber;
			tumh.setMessage(messageID, text, System.currentTimeMillis() + "");
			Reply r = tumh.handle();
			if (r.getAuxAPI() != null) {
				JsonObject params = r.getAuxAPI().getParameters();
				GetFirstRecommendation gfr = new GetFirstRecommendation();
				String recOutput = gfr.getFirstRecommendationPost(params.toString());
				System.out.println("Recommendation output is " + recOutput);
			}
			System.out.println("---MESSAGE---");
			for (Message m: r.getMessages()) {
				System.out.println(m.getText());
				System.out.println("-------");
			}
			if (r.getReplyMarkup() != null) {
				System.out.println("---BUTTONS---");
				for (String[] opts: r.getReplyMarkup().getKeyboard().getOptions()) {
					for (String o: opts) {
						System.out.print("[" + o + "]");
					}
					System.out.println();
				}
			}
			messageIDNumber++;
			/*JsonElement j = gson.toJsonTree(wdm);
			System.out.println(j);
			wdm = gson.fromJson(j, DialogFlowDialogManager.class);*/
		}
	}
}