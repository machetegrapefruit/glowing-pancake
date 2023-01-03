package it.uniba.swap.mler.entityrecognizer;

public class Entity {
	private String type;
	private String[] aliases;
	private int[] minMatches;
	private String id;
	public Entity(String id, String type, String[] aliases) {
		super();
		this.id = id;
		this.type = type;
		this.aliases = aliases;
		this.minMatches = new int[this.aliases.length];
		for (int i = 0; i < aliases.length; i++) {
			minMatches[i] = 80;
		}
	}
	public Entity(String id, String type, String[] aliases, int[] minMatches) {
		super();
		this.id = id;
		this.type = type;
		this.aliases = aliases;
		this.minMatches = minMatches;
	}
	public String getID() {
		return this.id;
	}
	public String getType() {
		return type;
	}
	public String[] getAliases() {
		return aliases;
	}
	public String getValue() {
		return aliases[0];
	}
	public int[] getMinMatches() {
		return minMatches;
	}
	
	public String toString() {
		return aliases[0] + "(" + type + ")";
	}
	
}
