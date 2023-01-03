package dialog;

import java.util.List;

import utils.Candidate;

/**
 * Questa interfaccia modella una generica policy per l'assegnazione dei propertyType
 * a delle rispettive entità. In questo modo un oggetto Preference può usare diverse 
 * strategie per l'assegnazione di ogni propertyType.
 * @author isz_d
 *
 */
public interface PropertyTypeAssociationPolicy {
	public List<PendingConfirmation> assignPropertyType(Candidate propertyType, List<Candidate> entities);
}
