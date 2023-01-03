package configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the association between each property type defined in the configuration file
 * and the tables in the database. Each property can be put in a different database
 * table, depending on the type of data that it requires (e.g. string, numbers, dates)
 *
 */
public class PropertyMapper {
	private Map<String, PropertyMapping> propertyTableMapping;
	
	public PropertyMapper(Map<String, PropertyMapping> map) {
		this.propertyTableMapping = map;
	}
	
	public PropertyMapping getMapping(String propertyName) {
		return this.propertyTableMapping.get(propertyName);
	}

}
