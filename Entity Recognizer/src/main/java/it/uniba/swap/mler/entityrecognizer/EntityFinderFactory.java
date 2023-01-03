package it.uniba.swap.mler.entityrecognizer;

public class EntityFinderFactory {
	private static IEntityFinder finder;
	public static IEntityFinder getEntityFinder() {
		if (finder == null) {
			finder = new EntityFinder();
		}
		return finder;
	}
	public static void setEntityFinder(IEntityFinder aFinder) {
		finder = aFinder;
	}
}
