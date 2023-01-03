package test;

import utils.Alias;
import utils.MatchedElement;

public class TestAddedElement extends MatchedElement {
	private boolean autoAdded;

	public TestAddedElement(Alias element, int rating, boolean autoAdded) {
		super(element, rating);
		this.autoAdded = autoAdded;
	}
	
	public TestAddedElement(MatchedElement e, boolean autoAdded) {
		this(e.getElement(), e.getRating(), autoAdded);
	}
	
	public boolean isAutoAdded() {
		return this.autoAdded;
	}

}
