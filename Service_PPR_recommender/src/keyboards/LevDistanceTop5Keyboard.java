package keyboards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

import restService.GetLevDistanceFromAllVertexUriByName;
import utils.EmojiCodes;

public class LevDistanceTop5Keyboard implements Keyboard {

	String[][] options;
	boolean found;
	
	public boolean found() {
		return found;
	}
	
	public LevDistanceTop5Keyboard(String name) throws Exception {
		
		found = false;
		
		GetLevDistanceFromAllVertexUriByName get = new GetLevDistanceFromAllVertexUriByName();
		String data = get.getAllPropertyListFromEntity(name);
		
		if (data != null) {
			found = true;
		}

		Gson gson = new Gson();
		List<String> properties = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		Map<String, String> map = gson.fromJson(data, Map.class);
		for (Entry<String, String> entry : map.entrySet()) {
			String property = entry.getKey();
			property = property.replace("http://dbpedia.org/resource/", "")
					.replace("_", " ");
			properties.add(property);
		}
		
		options = new String[properties.size() + 1][1];
		for (int i = 0; i < properties.size(); i++) {
			options[i][0] = properties.get(i);
		}
		
		String backarrowCode = EmojiCodes.hexHtmlSurrogatePairs.get("backarrow");
		options[options.length - 1][0] = backarrowCode + " Go to the list of \"Properties\"";		
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}
}
