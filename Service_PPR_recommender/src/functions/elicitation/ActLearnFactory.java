package functions.elicitation;

import configuration.Configuration;

public class ActLearnFactory {
	
	/**
	 * Factory class for the ActLearnInterface. Returns an implementation of this
	 * interface based on the configuration file
	 * @param userID ID of the user
	 * @return An implementation of the ActLearnInterface class
	 */
	public static ActLearnInterface getFunction(String userID) {
		String function = Configuration.getDefaultConfiguration().getActLearnFunction();
		switch(function) {
		case "pop":
			return new GiveAdvicePop(userID);
		case "randPop":
			return new GiveAdviceRandPop(userID);
		case "varxPop":
			return new GiveAdviceVarxPop(userID);
		case "jaccard":
			return new GiveAdviceJaccard(userID);
		default:
			return null;
		}
	}
}
