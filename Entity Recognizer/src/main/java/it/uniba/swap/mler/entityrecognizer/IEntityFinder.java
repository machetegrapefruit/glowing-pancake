package it.uniba.swap.mler.entityrecognizer;

public interface IEntityFinder {
	public MatchMap findEntities(String text, String[] types, boolean findDates, boolean fullTextMode);
	public MatchMap findEntities(String text, boolean findDates, boolean fullTextMode);
}
