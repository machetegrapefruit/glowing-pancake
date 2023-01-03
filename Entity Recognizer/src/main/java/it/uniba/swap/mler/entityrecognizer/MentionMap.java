package it.uniba.swap.mler.entityrecognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import it.uniba.swap.mler.utils.IndexRange;

public class MentionMap {
	private MatchMap matchMap;			//Map of recognized entities organized by type
	private IndexRange range;			//Character index range of the mention
	private IndexRange tokenRange;		//Token index range of the mention
	private String matchedString;		//Sentence chunk that was recognized as an entity mention
	
	public MentionMap(IndexRange range, IndexRange tokenRange, String matchedString) {
		this.matchMap = new MatchMap();
		this.range = range;
		this.tokenRange = tokenRange;
		this.matchedString = matchedString;
	}
	
	public IndexRange getIndexRange() {
		return range;
	}
	
	public IndexRange getTokenRange() {
		return tokenRange;
	}
	
	public String getMatchedString() {
		return matchedString;
	}
	
	public boolean containsKey(String key) {
		return matchMap.containsKey(key);
	}
	
	public Set<String> getKeys() {
		return matchMap.getKeys();
	}
	
	public void add(MatchMap mm) {
		matchMap.add(mm);
	}
	
	public void add(List<Match> matches) {
		matchMap.add(matches);
	}
	
	public void add(Match m) {
		matchMap.add(m);
	}
	
	public List<Match> get(String type) {
		return matchMap.get(type);
	}
	
	public List<Match> getBest(String type, int n) {
		List<Match> allMatches = matchMap.get(type);
		Collections.sort(allMatches);
		Collections.reverse(allMatches);
		return allMatches.subList(0, Math.min(n, allMatches.size()));
	}
	
	public String toString() {
		return matchMap.toString();
	}

}
