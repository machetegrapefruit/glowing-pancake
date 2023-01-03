package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import configuration.Configuration;
import functions.EntityService;
import functions.PropertyService;

/**
 * Classe che rappresenta un'entità generica (ad esempio, un film)
 *
 */
public class Entity {
	private Map<String, List<String>> properties;
	private String[] propertiesToShow; //Descrive le proprietà e l'ordine da mostrare nel metodo toString()
	
	public Entity(List<List<String>> propertiesList, String[] propertiesToShow) {
		properties = new HashMap<String, List<String>>();
		this.propertiesToShow = propertiesToShow;
		
		for (List<String> property: propertiesList) {
			String propertyType = property.get(1);
			String propertyValue = property.get(2);
			
			if (!this.properties.containsKey(propertyType)) {
				List<String> l = new ArrayList<String>();
				l.add(propertyValue);
				this.properties.put(propertyType, l);
			} else {
				this.properties.get(propertyType).add(propertyValue);
			}
		}
	}
	
	public String toString() {
		PropertyService ps = new PropertyService();
		Map<String, String> propTypeLabels = Configuration.getDefaultConfiguration().getPropertyTypesLabels();
		StringBuilder sb = new StringBuilder();
		
		for (String propertyToShow: this.propertiesToShow) {
			StringJoiner sj = new StringJoiner(", ");
			sb.append(propTypeLabels.get(propertyToShow) + ": ");
			if (this.properties.containsKey(propertyToShow)) {
				for (String p: this.properties.get(propertyToShow)) {
					String propertyLabel = ps.getPropertyLabel(p);
					if (propertyLabel != null) {
						sj.add(propertyLabel);
					} else {
						sj.add(p);
					}
				}
				sb.append(sj.toString());
			} else {
				sb.append("N/A");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param propertyName L'URI del property type (es. "P361")
	 * @return
	 */
	public List<String> get(String propertyName) {
		return this.properties.get(propertyName);
	}
	
	public static void main(String[] args) {
		Entity e = new EntityService().getEntityDetails("http://dbpedia.org/resource/Vanilla_Sky");
		System.out.println(e.toString());
	}

}
