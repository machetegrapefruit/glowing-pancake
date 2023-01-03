package functions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import configuration.Configuration;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ContentBasedServiceSingleton {
    private static ContentBasedServiceSingleton istance = null;
    private static boolean structured = true;
    private static Configuration configuration;
    private static String urlBase;

    private ContentBasedServiceSingleton() {
        configuration = Configuration.getDefaultConfiguration();
        urlBase = configuration.getRecsysUrl();
    }

    public static ContentBasedServiceSingleton getInstance() throws IOException {
        if (istance == null) {
            istance = new ContentBasedServiceSingleton();
        }
        String algoritm = configuration.getRecsysAlgorithm();
        if (algoritm.equals("contentBased")){
            select_model(configuration.getRecsysIndex(), structured);
        }
        return istance;
    }


    public boolean get_structured(){
        return structured;
    }


    private static void select_model(Integer index, boolean structured) throws IOException {
        String urlString = urlBase+"/selectModel/";
        URL url = new URL(urlString + index.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        StringBuffer content = new StringBuffer();
        int status = connection.getResponseCode();
        if (status != -1) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        }
        connection.disconnect();
        System.out.println(content);
    }

    public HashMap<Integer, String> get_sussestions(String json_pref) {
        URL url;
        String urlString;
        try {
            if (structured) {
                urlString = urlBase+"/getSuggestions";
               url=new URL(urlString);
            } else {
                urlString = urlBase+"/getSuggestionsFromSentence";
                url = new URL(urlString);
            }
            return get_external_connection(json_pref, url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashMap<Integer, String> get_external_connection(String json_pref, URL url) throws IOException {
        StringBuffer content = new StringBuffer();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = json_pref.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        connection.connect();
        int status = connection.getResponseCode();
        if (status != -1) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        }
        connection.disconnect();
        System.out.println(content);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(content.toString(), JsonObject.class);
        JsonArray return_result = jsonObject.getAsJsonArray("results");
        HashMap<Integer, String> results = new HashMap<>();
        for (int j = 0; j < return_result.size(); j++) {
            JsonObject result = (JsonObject) return_result.get(j);
            Integer rank = result.get("Rank").getAsInt();
            String ID = result.get("ID").getAsString();
            results.put(rank, ID);
        }
        return results;
    }
}
