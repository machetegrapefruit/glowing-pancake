package dialog;

import java.util.List;

import utils.Alias;

/**
 * Questa classe modella una valutazione in sospeso, che sarà gestita in seguito
 *
 */
public class PendingEvaluation {
	public enum PendingEvaluationType {
		NAME_DISAMBIGUATION,
		PROPERTY_TYPE_DISAMBIGUATION,
		DELETE_NAME_DISAMBIGUATION
	};
	
	private Alias elementName;
	private int rating;
	private PendingEvaluationType type;
	private List<Alias> possibleValues;
	
	public PendingEvaluation(Alias elementName, 
			int rating, 
			List<Alias> possibleValues, 
			PendingEvaluationType type) {
		this.elementName = elementName;
		this.possibleValues = possibleValues;
		this.rating = rating;
		this.type = type;
	}

	
	public Alias getElementName() {
		return this.elementName;
	}
	
	public List<Alias> getPossibleValues()  {
		return this.possibleValues;
	}
	
	public int getRating() {
		return this.rating;
	}
	
	public PendingEvaluationType getType() {
		return this.type;
	}
	
	
	public char getRatingSymbol() {
		if (rating == 1) {
			return '+';
		} else if (rating == 0) {
			return '-';
		} else {
			return '/';
		}
	}
}
