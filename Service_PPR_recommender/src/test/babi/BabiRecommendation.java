package test.babi;

public class BabiRecommendation {	
	int id = 0;
	private String sentence;
	private String recommendedMovie;
	private String recMovieUri;
	private String[] pagerankResults;
	public BabiRecommendation(int id, String sentence, String recommendedMovie, String recMovieUri, String[] pagerankResults) {
		super();
		this.id = id;
		this.sentence = sentence;
		this.recommendedMovie = recommendedMovie;
		this.recMovieUri = recMovieUri;
		this.pagerankResults = pagerankResults;
	}
	public String getSentence() {
		return sentence;
	}
	public String getRecommendedMovie() {
		return recommendedMovie;
	}
	public String getRecMovieUri() {
		return recMovieUri;
	}
	public String[] getPagerankResults() {
		return pagerankResults;
	}
	public int getId() {
		return this.id;
	}
	
}
