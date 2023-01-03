package it.uniba.swap.mler.entityrecognizer;

import java.util.List;

public interface IMentionFinder {
	public List<MentionMap> findEntities(String text, String[] types, boolean findDates, boolean fullTextMode);
	public List<MentionMap> findEntities(String text, boolean findDates, boolean fullTextMode);
}
