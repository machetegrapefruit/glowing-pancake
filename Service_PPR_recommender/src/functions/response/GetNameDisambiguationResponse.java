package functions.response;

import dialog.PendingEvaluation;
import utils.Alias;

public class GetNameDisambiguationResponse {
	private PendingEvaluation evaluation;
	private Alias perfectMatch;
	private boolean success;
	
	public GetNameDisambiguationResponse(PendingEvaluation evaluation) {
		this.evaluation = evaluation;
		this.perfectMatch = null;
		this.success = true;
	}
	
	public GetNameDisambiguationResponse(Alias perfectMatch) {
		this.perfectMatch = perfectMatch;
		this.evaluation = null;
		this.success = true;
	}
	
	public GetNameDisambiguationResponse(boolean success) {
		this.success = success;
	}
	
	public PendingEvaluation getEvaluation() {
		return this.evaluation;
	}
	
	public Alias getPerfectMatch() {
		return this.perfectMatch;
	}
	
	public boolean success() {
		return this.success;
	}
	
	
}
