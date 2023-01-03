package functions.response;

import dialog.PendingEvaluation;

public class GetPropertyTypeDisambiguationResponse {
	private PendingEvaluation evaluation;
	private String perfectMatch;
	private boolean success;
	
	public GetPropertyTypeDisambiguationResponse(PendingEvaluation evaluation) {
		this.evaluation = evaluation;
		this.perfectMatch = null;
		this.success = true;
	}
	
	public GetPropertyTypeDisambiguationResponse(String perfectMatch) {
		this.perfectMatch = perfectMatch;
		this.evaluation = null;
		this.success = true;
	}
	
	public GetPropertyTypeDisambiguationResponse(boolean success) {
		this.success = success;
	}
	
	public PendingEvaluation getEvaluation() {
		return this.evaluation;
	}
	
	public String getPerfectMatch() {
		return this.perfectMatch;
	}
	
	public boolean success() {
		return this.success;
	}
	
	
}
