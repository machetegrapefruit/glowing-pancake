package it.uniba.swap.mler.entityrecognizer;

import java.util.List;

public interface EntityLinker {
	public List<Match> findEntities(String text, double minLengthRatio);
	public List<Match> findEntities(String text, String[] entityTypes, double minLengthRatio);
}
