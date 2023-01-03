package it.uniba.swap.mler.entityrecognizer;

public class MentionFinderFactory {
	private static IMentionFinder finder;
	public static IMentionFinder getEntityFinder() {
		if (finder == null) {
			finder = new MentionFinder();
		}
		return finder;
	}
	public static void setEntityFinder(IMentionFinder aFinder) {
		finder = aFinder;
	}
}
