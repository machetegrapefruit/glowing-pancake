package dialog.handler;

import dialog.ApiAiResponse;

public class DefaultHandlerResponse implements HandlerResponse {
	private ApiAiResponse response;
	private boolean appendNextTaskReminder;
	private boolean setNextRecommendedEntity;
	
	@Override
	public ApiAiResponse getResponse() {
		return response;
	}
	@Override
	public boolean appendNextTaskReminder() {
		return appendNextTaskReminder;
	}
	public DefaultHandlerResponse(ApiAiResponse response, boolean appendNextTaskReminder, boolean setNextRecommendedEntity) {
		super();
		this.response = response;
		this.appendNextTaskReminder = appendNextTaskReminder;
		this.setNextRecommendedEntity = setNextRecommendedEntity;
	}
	@Override
	public boolean setNextRecommendedEntity() {
		return this.setNextRecommendedEntity;
	}
	
	
}
