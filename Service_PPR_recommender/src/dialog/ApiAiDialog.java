package dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;

import com.google.cloud.dialogflow.v2.Context;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.gson.JsonObject;

import dialog.handler.DefaultHandlerResponse;
import dialog.handler.DialogHandler;
import dialog.handler.HandlerResponse;
import dialog.handler.actions.AddPreferenceHandler;
import dialog.handler.actions.AddRecommendationCritiqueHandler;
import dialog.handler.actions.AddRecommendationPreferenceHandler;
import dialog.handler.actions.CancelResetProfileHandler;
import dialog.handler.actions.ConfirmationHandler;
import dialog.handler.actions.DefaultFailureHandler;
import dialog.handler.actions.DefaultHandler;
import dialog.handler.actions.DeletePreferenceHandler;
import dialog.handler.actions.HelpHandler;
import dialog.handler.actions.PreferenceDisambiguationHandler;
import dialog.handler.actions.RequestDetailsHandler;
import dialog.handler.actions.RequestExplanationHandler;
import dialog.handler.actions.RequestRecommendationHandler;
import dialog.handler.actions.ResetProfileHandler;
import dialog.handler.actions.ShowProfileHandler;
import dialog.handler.actions.ShowTrailerHandler;
import dialog.handler.actions.SkipHandler;
import dialog.handler.actions.StartHandler;
import dialog.handler.actions.StopRecommendationHandler;
import dialog.handler.prompts.DialogPromptHandler;
import dialog.handler.prompts.ItemToRatePrompt;
import dialog.handler.prompts.NextRecommendationPromptHandler;
import dialog.handler.prompts.PendingPromptHandler;
import dialog.handler.prompts.RemainingPreferencePromptHandler;
import functions.EntityService;
import functions.LogService;
import functions.ProfileService;
import functions.ServiceSingleton;

public class ApiAiDialog implements Dialog{
	private final static Logger LOGGER = Logger.getLogger(ApiAiDialog.class.getName());
	private String clientID;
	
	private List<DialogHandler> actionHandlers;
	private List<DialogPromptHandler> promptHandlers;
	
	public ApiAiDialog(String clientID) {
		this.clientID = clientID;
		/*
		 * Setting the handlers
		 * The dialog model follows this structure
		 * 1. The action handlers are checked: each action handler can execute one of the functions
		 *    of the system. Usually, one intent -> one handler. Each handler in the list is checked
		 *    for its starting condition. 
		 * 2. When a handler is activated, all the following ones in the list are ignored, and a
		 *    HandlerResponse is generated.
		 * 3. Additional Prompts are added to the response message. To do this, a queue of dialog
		 *    prompt handlers is used. Each prompt handler has its own starting conditions
		 * 4. For each prompt handler that is activated, append its own response to the action 
		 *    response.
		 * 5. After all the prompts have been exhausted, the setContexts() method is called, which
		 *    sets all the necessary contexts in Dialogflow, depending on what happened during the
		 *    conversation.
		 */
		this.actionHandlers = new ArrayList<DialogHandler>();
		this.actionHandlers.add(new StartHandler());
		this.actionHandlers.add(new PreferenceDisambiguationHandler());
		this.actionHandlers.add(new AddPreferenceHandler());
//		this.actionHandlers.add(new AddRecommendationCritiqueHandler());
//		this.actionHandlers.add(new AddRecommendationPreferenceHandler());
		this.actionHandlers.add(new CancelResetProfileHandler());
		this.actionHandlers.add(new ConfirmationHandler());
		this.actionHandlers.add(new DefaultFailureHandler());
		this.actionHandlers.add(new DeletePreferenceHandler());
		this.actionHandlers.add(new DeletePreferenceHandler());
		this.actionHandlers.add(new HelpHandler());
		this.actionHandlers.add(new RequestDetailsHandler());
		this.actionHandlers.add(new RequestExplanationHandler());
		this.actionHandlers.add(new RequestRecommendationHandler());
		this.actionHandlers.add(new ResetProfileHandler());
		this.actionHandlers.add(new ShowProfileHandler());
		this.actionHandlers.add(new ShowTrailerHandler());
		this.actionHandlers.add(new SkipHandler(true));
		this.actionHandlers.add(new StopRecommendationHandler());
		this.actionHandlers.add(new DefaultHandler());       //DefaultHandler must always be last
		this.promptHandlers = new ArrayList<DialogPromptHandler>();
		this.promptHandlers.add(new NextRecommendationPromptHandler());
		this.promptHandlers.add(new PendingPromptHandler());
		this.promptHandlers.add(new ItemToRatePrompt());
		this.promptHandlers.add(new RemainingPreferencePromptHandler());
	}
	
	@Override
	public ApiAiResponse processMessage(DialogState state, LogService logService, String messageID, QueryResult message) {
		System.out.println("Entered ApiAiDialog.processMessage");
		String query = message.getQueryText();
		String intent = "";
		intent = message.getIntent().getDisplayName();
		if (intent.equals("")) {
			intent = message.getAction();
		}

		System.out.println("Message is " + query + ", intent is " + intent);
		String contexts = getContexts(message);
		
		if (state.getCurrentRecommendedIndex() > -1) {
			EntityService es = ServiceSingleton.getEntityService();
			List<String> recommended = es.getCachedRecommendedEntities(this.clientID);
			String currentRecommendedEntity = recommended.get(state.getCurrentRecommendedIndex());
			logService.setRecommendedEntity(currentRecommendedEntity);
		}

		ApiAiResponse response = dispatchIntent(intent, message, messageID, state);
		logService.setIntent(intent);
		logService.setContexts(contexts);
		ProfileService ps = new ProfileService();
		logService.setNumberRecommendationList(ps.getNumberRecommendationList(this.clientID));
		logService.setPagerankCycle(ps.getPagerankCycle(this.clientID));
		//dialogStateService.saveDialogState(this.clientID, state);

		for (String recognized: response.getRecognizedObjects()) {
			logService.addRecognizedObject(recognized);
		}
		for (String event: response.getEvents()) {
			logService.addEvent(event);
		}
		logService.insertMessageInLog();
		state.getDatasetPopulationManager().processMessage(state, response.toJson(), this.clientID, messageID, query, intent, contexts, true);
		//logService.insertMessageInLog(this.clientID, messageID, query, timestampStart, System.currentTimeMillis(), intent, contexts, response.getRecognizedObjects(), response.getEvents());
		return response;
	}
	
	public ApiAiResponse getNextPendingTask(DialogState state) {
		DefaultHandlerResponse actionResponse = new DefaultHandlerResponse(new ApiAiResponse(), true, true);
		return new NextRecommendationPromptHandler().handle(this.clientID, null, state, null, actionResponse);
	}
	 
	private ApiAiResponse dispatchIntent(String intent, QueryResult message, String messageID, DialogState state) {
		state.setContexts(null);
		HandlerResponse actionResponse = null;
		//For each of the action Dialog handlers
		for (int i = 0; actionResponse == null && i < this.actionHandlers.size(); i++) {
			DialogHandler handler = this.actionHandlers.get(i);
			//Get the HandlerResponse from the first of the Dialog Handler that matches
			if (handler.check(this.clientID, message, state, messageID)) {
				actionResponse = handler.handle(this.clientID, message, state, messageID);
			}
		}
		//Check for additional prompts to show
		//For each of the Prompt handlers
		for (DialogPromptHandler handler: this.promptHandlers) {
			//Add the response from all of the prompt handlers that match, not just the first one
			if (handler.check(this.clientID, message, state, messageID, actionResponse)) {
				handler.handle(this.clientID, message, state, messageID, actionResponse);
			}
		}
		//Set the appropriate contexts, depending on the current dialog state
		actionResponse.getResponse().merge(setContexts(state, message));
		state.setContexts(actionResponse.getResponse().getContexts());
		//Return the final response
		return actionResponse.getResponse();
	}
	
	
	/**
	 * Sets the appropriate contexts in the response, depending on the current state of the dialog
	 * @param state
	 */
	public ApiAiResponse setContexts(DialogState state, QueryResult result) {
		String intent = result.getIntent().getDisplayName();
		ApiAiResponse response = new ApiAiResponse();
		Preference p = state.getPendingPreferenceQueue().peek();
		if (p != null && (!p.allDisambiguated() || !p.allConfirmed())) {
			//Setting the followup contexts related to the disambiguation phase
			if (state.getCurrentRecommendedIndex() > -1) {
				//Disambiguation for a recommended item
				response.addContext("request_recommendation-followup");
				response.addContext("request_recommendation-critiquing-followup");
			} else {
				//Disambiguation for a preference
				response.addContext("preference-followup");
			}
		}
		if (p != null && !p.allDisambiguated()) {
			//Setting the disambiguation context
			response.addContext("property_type_disambiguation");
		} else if (p != null && !p.allConfirmed()) {
			//Setting the confirmation context
			response.addContext("confirmation");
		} else {
			//Delete the disambiguation and confirmation contexts
			response.addContext("property_type_disambiguation", 0);
			response.addContext("confirmation", 0);
			if (state.getCurrentRecommendedIndex() == -1 && !intent.equals("request_recommendation")) {
				//Delete the recommendation followup context
				response.addContext("request_recommendation-followup", 0);
			}
		}
		return response;
	}
		
	private String getContexts(QueryResult message) {
		StringJoiner sj = new StringJoiner(", ");
		List<Context> contexts = message.getOutputContextsList();
		for (Context c: contexts) {
			sj.add(c.getName());
		}
		return sj.toString();
	}
}
