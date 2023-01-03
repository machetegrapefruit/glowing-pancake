package utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PropertyFilter {
	private String propertyType;
	private String propertyValue;
	
	public PropertyFilter(String propertyType, String propertyValue) {
		this.propertyType = propertyType;
		this.propertyValue = propertyValue;
	}
	public String getPropertyType() {
		return propertyType;
	}
	
	public String getPropertyValue() {
		return propertyValue;
	}
	
	public String toString() {
		return "propertyType: " + propertyType + "propertyValue: " + propertyValue;
	}
	
	public JsonElement toJson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(PropertyFilter.class, new PropertyFilterSerializer());
        return gsonBuilder.create().toJsonTree(this);
	}
	
	public static class PropertyFilterSerializer implements JsonSerializer<PropertyFilter> {

		@Override
		public JsonElement serialize(PropertyFilter src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject result = new JsonObject();
			result.addProperty("propertyType", src.propertyType);
			result.addProperty("propertyValue", src.propertyValue);
			return result;
		}
	}
	
	public static class PropertyFilterDeserializer implements JsonDeserializer<PropertyFilter> {

		@Override
		public PropertyFilter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			if (!object.has("propertyType") || !object.has("propertyValue")) {
				throw new JsonParseException("Not a PropertyFilter object!");
			} else {
				return new PropertyFilter(object.get("propertyType").getAsString(),
						object.get("propertyValue").getAsString());
			}
		}
		
	}
	
	public static void main(String[] args) {
		List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
		filters.add(new PropertyFilter("propertyType1", "propertyValue1"));
		filters.add(new PropertyFilter("propertyType2", "propertyValue2"));
		Gson gson = new Gson();
		System.out.println(gson.toJson(filters));
	}

}
