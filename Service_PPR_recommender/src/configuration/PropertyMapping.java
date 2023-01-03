package configuration;

public class PropertyMapping {
	private String propertyTypeId;
	private String tableName;
	private String columnName;
	
	public PropertyMapping(String propertyTypeId, String tableName, String columnName) {
		this.propertyTypeId = propertyTypeId;
		this.tableName = tableName;
		this.columnName = columnName;
	}
	
	public String getPropertyTypeId() {
		return propertyTypeId;
	}
	public String getTableName() {
		return tableName;
	}
	public String getColumnName() {
		return columnName;
	}
	
	

}
