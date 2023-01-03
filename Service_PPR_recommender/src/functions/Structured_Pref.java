package functions;

import configuration.Configuration;

import java.util.ArrayList;

public class Structured_Pref {
    ArrayList<String> movies = new ArrayList<>();
    ArrayList<String> entities = new ArrayList<>();
    ArrayList<String> movietoIgnore = new ArrayList<>();
    ArrayList<String> negativeEntity = new ArrayList<>();
    int recListSize = Configuration.getDefaultConfiguration().getRecListSize();


    public Structured_Pref(ArrayList<String> movies, ArrayList<String> property, ArrayList<String> movietoIgnore, ArrayList<String> negativeEntity){
        this.movies = movies;
        this.entities = property;
        this.movietoIgnore = movietoIgnore;
        this.negativeEntity = negativeEntity;
    }
}
