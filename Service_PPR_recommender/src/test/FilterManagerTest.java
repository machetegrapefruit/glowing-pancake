package test;

import dialog.FilterManager;

public class FilterManagerTest {
	public static void main(String[] args) {
		//Test configuration
		String[] propTypes = {"P136", "P361"};
		String[] filterable = {"P136"};
		System.out.println(FilterManager.getFiltersFromSentence("Can you recommend me a Rock song?"));
	}
}
