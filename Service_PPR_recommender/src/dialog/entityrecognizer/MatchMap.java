package dialog.entityrecognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The MatchMap contains all the entity mentions that have been recognized in a sentence.
 * The mentions are organized in a HashMap and are divided by entity type.
 * Each entry in the HashMap is a list of Match objects, that contain all the entities
 * of that type that have been found. This list is not sorted by match distance
 * @author Andrea Iovine
 *
 */
public class MatchMap {
	private Map<String, List<Match>> entities;
	private TimeExpression timeExpression;
	
	public MatchMap() {
		entities = new HashMap<String, List<Match>>();
	}
	
	public boolean containsKey(String type) {
		return entities.containsKey(type);
	}
	
	public Set<String> getKeys() {
		return this.entities.keySet();
	}
	
	public void add(MatchMap mm) {
		for (String type: mm.entities.keySet()) {
			if (entities.containsKey(type)) {
				entities.get(type).addAll(mm.get(type));
			} else {
				entities.put(type, mm.get(type));
			}
		}
		if (mm.getTimeExpression() != null) {
			this.timeExpression = mm.getTimeExpression();
		}
	}
	
	public void setTimeExpression(TimeExpression te) {
		this.timeExpression = te;
	}
	
	public TimeExpression getTimeExpression() {
		return this.timeExpression;
	}
	
	public void add(List<Match> matches) {
		for (Match m: matches) {
			add(m);
		}
	}
	
	public void add(Match m) {
		if (!entities.containsKey(m.getEntityType())) {
			List<Match> list = new ArrayList<Match>();
			entities.put(m.getEntityType(), list);
		}
		//Check if there is an entity with the same entity ID
		List<Match> te = entities.get(m.getEntityType());
		int index = -1;
		for (int i = 0; index == -1 && i < te.size(); i++) {
			//If there is, we check whether the new entity has a higher similarity
			//If it is more similar, we replace the old entity with the new one
			Match mm = te.get(i);
			if (mm.getEntityID() != null && mm.getEntityID().equals(m.getEntityID())) {
				index = i;
			}
		}
		if (index > -1) {
			Match mm = te.get(index);
			if (m.getMatch() > mm.getMatch()) {
				te.remove(index);
				te.add(index, m);
			}
		} else {
			te.add(m);
		}
	}
	
	public List<Match> get(String type) {
		return entities.get(type);
	}
	
	public List<Match> getBest(String type, int n) {
		List<Match> allMatches = entities.get(type);
		Collections.sort(allMatches);
		Collections.reverse(allMatches);
		return allMatches.subList(0, Math.min(n, allMatches.size()));
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String key: entities.keySet()) {
			sb.append(entities.get(key));
		}
		return sb.toString();
	}
}
