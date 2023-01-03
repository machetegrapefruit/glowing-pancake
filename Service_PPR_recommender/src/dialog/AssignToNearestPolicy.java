package dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import entity.Entity;
import functions.EntityService;
import functions.PropertyService;
import utils.Alias;
import utils.Candidate;

/**
 * Questa policy assegna la propertyType alle due entità più vicine.
 * @author isz_d
 *
 */
public class AssignToNearestPolicy implements PropertyTypeAssociationPolicy {

	@Override
	public List<PendingConfirmation> assignPropertyType(Candidate propertyType, List<Candidate> entities) {
		if (entities.size() > 0) {
			List<PendingConfirmation> result = new ArrayList<>();
			//Se ce n'è almeno una, calcolo la distanza tra il propertyType corrente e tutte le entità suddette
			//e prendo la più vicina
			Alias propTypeAlias = propertyType.getAlias();
			List<Distance> distances = findDistanceFromEntities(propertyType, entities);
			Collections.sort(distances);
			//Prendo gli indici delle due entity più vicine
			int i = 0;
			while (i < Math.min(2, distances.size())) {
				int index = distances.get(i).getIndex();
				//Cerco il rating da assegnare
				int rating = getRatingForConfirmation(propertyType, entities.get(index));
				List<Alias> properties = getProperties(entities.get(index).getAlias().getURI(), propertyType.getAlias().getURI());
				result.add(new PendingConfirmation(entities.get(index).getAlias(), propTypeAlias, rating, properties));
				i++;
			}

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
	
	private List<Distance> findDistanceFromEntities(Candidate propertyType, List<Candidate> entities) {
		List<Distance> result = new ArrayList<Distance>();
		
		for(int i = 0; i < entities.size(); i++) {
			Candidate entity = entities.get(i);
			int distanceB = Math.abs(propertyType.getEnd() - entity.getStart());
			int distanceA = Math.abs(propertyType.getStart() - entity.getEnd());
			int minDistance = Math.min(distanceA, distanceB);
			result.add(new Distance(entity, minDistance, i));
		}
		
		return result;
	}
	
	/**
	 * Restituisce il rating da associare ad una preferenza su un propertyType
	 */
	private int getRatingForConfirmation(Candidate propertyType, Candidate entity) {
		if (propertyType.getRating() != -1) {
			//Se è stato trovato un sentiment per la propertyType, restituisci quello
			return propertyType.getRating();
		} else {
			//Altrimenti, usa il sentiment associato all'entità
			return entity.getRating();
		}
	}
	
	private static class Distance implements Comparable<Distance> {
		private Candidate entity;
		private int distance;
		private int index;
		
		public Distance(Candidate entity, int distance, int index) {
			this.entity = entity;
			this.distance = distance;
			this.index = index;
		}
		
		public Candidate getEntity() {
			return entity;
		}

		public int getDistance() {
			return distance;
		}

		public int getIndex() {
			return index;
		}

		@Override
		public int compareTo(Distance o) {
			if (o.distance < this.distance) {
				return 1;
			} else if (o.distance - this.distance  == 0) {
				return 0;
			} else {
				return -1;
			}
		}
	}

}
