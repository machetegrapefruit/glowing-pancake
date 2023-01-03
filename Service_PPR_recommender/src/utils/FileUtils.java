package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class FileUtils {
		
    public static List<String> readEntities(String entitiesPath) throws IOException {
    	List<String> entities = new ArrayList<>();
    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(entitiesPath), "UTF-8"));
    	String line = br.readLine();
    	
    	while (line != null) {
    		String[] lineSplit = line.split("\\|");
    		String id = lineSplit[0];
    		entities.add(getWikidataIDFromURL(id));
    		line = br.readLine();
    	}
    	
    	br.close();
    	
    	return entities;
    }
    
    public static String readFile(String path) throws IOException {
    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
    	StringJoiner sj = new StringJoiner("\n");
    	String line = br.readLine();
    	
    	while (line != null) {
    		sj.add(line);
    		line = br.readLine();
    	}
    	
    	br.close();
    	
    	return sj.toString();
    }
    
    public static boolean fileExists(String path) {
    	return new File(path).exists();
    }
    
    public static List<String> readFileAsList(String path) throws IOException {
    	List<String> entities = new ArrayList<>();
    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
    	String line = br.readLine();
    	
    	while (line != null) {
    		entities.add(line);
    		line = br.readLine();
    	}
    	
    	br.close();
    	
    	return entities;
    }
    
    
    public static String getWikidataIDFromURL(String url) {
    	String[] split = url.split("/");
    	return split[split.length - 1];
    }
	
    public static void writeToFile(String outputPath, String r) {
    	File output = new File(outputPath);
    	output.getParentFile().mkdirs();
    	try {
			output.createNewFile();
	    	OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8);
	    	bw.write(r);
	    	bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public static void writeToFile(String outputPath, List<String> list) {
    	File output = new File(outputPath);
    	output.getParentFile().mkdirs();
    	try {
			output.createNewFile();
	    	OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8);

	    	for (String wikidata: list) {
	    		bw.write(wikidata + "\n");
	    	}
	    	bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public static void appendToFile(String outputPath, List<String> list) {
    	File output = new File(outputPath);
    	output.getParentFile().mkdirs();
    	try {
			output.createNewFile();
			OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(output, true), StandardCharsets.UTF_8);

	    	for (String wikidata: list) {
	    		bw.write(wikidata + "\n");
	    	}
	    	bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void appendToFile(String outputPath, String r) {
    	File output = new File(outputPath);
    	output.getParentFile().mkdirs();
    	try {
			output.createNewFile();
			OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(output, true), StandardCharsets.UTF_8);
			bw.write(r);
	    	bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static BufferedWriter getBufferedWriter(String outputPath) {
    	File output = new File(outputPath);
    	output.getParentFile().mkdirs();
    	try {
			output.createNewFile();
			return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output, true), StandardCharsets.UTF_8));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
}
