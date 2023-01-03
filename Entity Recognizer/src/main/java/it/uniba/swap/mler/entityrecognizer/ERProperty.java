package it.uniba.swap.mler.entityrecognizer;

/**
 * This class models the information about a single entity type.
 * @author Andrea Iovine
 *
 */
public class ERProperty {
	private String propertyName;	//Name of the property type
	private String corenlpName;		//Name of the property type as recognized by CoreNLP
	private boolean requiresCorenlp;	//true if CoreNLP is required to recognize this entity type in answer mode
	private boolean requiresLinking;	//true if the mention must be linked to an entity
	public ERProperty(String propertyName, String corenlpName, boolean requiresCorenlp, boolean requiresLinking) {
		this.propertyName = propertyName;
		this.corenlpName = corenlpName;
		this.requiresCorenlp = requiresCorenlp;
		this.requiresLinking = requiresLinking;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public String getCorenlpName() {
		return corenlpName;
	}
	public boolean requiresCorenlp() {
		return requiresCorenlp;
	}
	public boolean requiresLinking() {
		return requiresLinking;
	}
	
	
}
