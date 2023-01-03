package dialog;

import java.util.ArrayList;
import java.util.List;

import entity.Entity;
import functions.EntityService;
import functions.PropertyService;
import utils.Alias;
import utils.Candidate;

/**
 * Questa policy assegna la propertyType ad un'entità prefissata.
 *
 */
public class AssignToEntityPolicy implements PropertyTypeAssociationPolicy {
	private Candidate entity;
	
	public AssignToEntityPolicy(Candidate entity) {
		this.entity = entity;
	}

	@Override
	public List<PendingConfirmation> assignPropertyType(Candidate propertyType, List<Candidate> entities) {
		if (entity != null) {
			List<Alias> properties = getProperties(entity.getAlias().getURI(), propertyType.getAlias().getURI());
			List<PendingConfirmation> result = new ArrayList<>();
			result.add(new PendingConfirmation(entity.getAlias(), propertyType.getAlias(), 
					getRatingForConfirmation(propertyType, entity), properties));
			return result;
		}
		return null;
	}
	
	private List<Alias> getProperties(String entityURI, String propertyTypeURI) {
		PropertyService ps = new PropertyService();
		List<Alias> properties = new ArrayList<>();
		EntityService es = new EntityService();
		Entity e = es.getEntityDetails(entityURI);
		List<String> propertyList = e.get(propertyTypeURI);
		if (propertyList != null) {
			for (String propertyURI: propertyList) {
				properties.add(new Alias(propertyURI, ps.getPropertyLabel(propertyURI)));
			}
		}

		return properties;
	}
	
	private int getRatingForConfirmation(Candidate propertyType, Candidate entity) {
//		if (propertyType.getRating() != -1) {
//			//Se è stato trovato un sentiment per la propertyType, restituisci quello
//			return propertyType.getRating();
//		} else {
//			//Altrimenti, usa il sentiment associato all'entità
//			return entity.getRating();
//		}
		return propertyType.getRating();
	}

}
