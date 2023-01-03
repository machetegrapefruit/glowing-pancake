package dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import configuration.Configuration;
import entity.Pair;
import functions.PropertyService;
import utils.Alias;
import utils.PropertyFilter;

public class FilterManager {
	public static List<PropertyFilter> getFiltersFromSentence(String text) {
		String[] stopWords = Configuration.getDefaultConfiguration().getStopWordsForRecommendationFilters();
		List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
		//SentimentAnalyzerConnector saConnector = new SentimentAnalyzerConnector(true, false, true);
		MLERConnector saConnector = new MLERConnector(true, false, true);
		try {
			List<FilteredSentimentObject> sentimentArray = saConnector.getFilteredSentiment(text, stopWords, 10);
			
			for (int i = 0; i < sentimentArray.size(); i++) {
				List<Alias> aliases = sentimentArray.get(i).getAliases();
				if (aliases.size() > 0) {
					//Filtra gli alias usando la distanza di Levenshtein (per evitare di riconoscere filtri quando non ce ne sono)
					Pair<Alias, String> bestAlias = getBestAlias(text, aliases);
					if (bestAlias != null) {
						filters.add(new PropertyFilter(bestAlias.value, bestAlias.key.getURI()));
					}
				}
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filters;
	}
	
	private static boolean isFilterable(String propertyType) {
		String[] filterable = Configuration.getDefaultConfiguration().getFilterablePropertyTypes();
		boolean found = false;
		int i = 0;
		while (!found && i < filterable.length) {
			found = propertyType.equalsIgnoreCase(filterable[i]);
			i++;
		}
		return found;
	}
	
	public static void main(String[] args) {
		List<PropertyFilter> filters = FilterManager.getFiltersFromSentence("Can you recommend me an action film");
		System.out.println(filters);
	}
	
	private static Pair<Alias, String> getBestAlias(String message, List<Alias> aliases) {
		List<Pair<Alias, String>> filterableProperties = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		PropertyService ps = new PropertyService();
		for (Alias a: aliases) {
			String uri = a.getURI();
			List<String> propertyTypes = ps.getPropertyTypes(uri).get(uri);
		    if (propertyTypes != null) {
			    for (String propertyType: propertyTypes) {
			    	if (isFilterable(propertyType)) {
			    		filterableProperties.add(new Pair<>(a, propertyType));
						labels.add(a.getLabel());
			    	}
			    }
		    }
		}
		if (filterableProperties.size() > 0) {
			return filterableProperties.get(0);
		} else {
			return null;
		}
		/*
		List<DistanceMeasure> distances = LevenshteinDistanceCalculator.getMostSimilar(message, labels, 1, 0.5);
		if (distances.size() > 0) {
			//Prendo l'alias migliore
			return filterableProperties.get(distances.get(0).getIndex());
		} else {
			return null;
		}*/
	
	}
}
