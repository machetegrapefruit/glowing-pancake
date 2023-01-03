package metrics;

public class MetricsQuertClass {
	
	public static String SQLselectNumberOfQuestions(int user_id, String botName) {
		String SQL = 	"SELECT * "
						+ "FROM chat_log "
						+ "WHERE chat_id = " + user_id + " "
						+ "AND bot_name =  \"" + botName + "\" "
						+ "order by message_id;";
		return SQL;
	}
	
	public static String SQLselectAllUsers(){
		String SQL = "SELECT * FROM users";
		
		return SQL;
	}
	
	public static String SQLselectCountAllRecMovieList(int user_id, String botName){
		String SQL = "SELECT * FROM ratings_rec_movies "
					+ "WHERE user_id = " + user_id + " "
					+ "AND ratings_rec_movies.bot_name =  \"" + botName + "\" "
					+ "order by rated_at;";
		
		return SQL;
	}
	
	
	public static String SQLselectAllRecMovieList(String botName){
		String SQL = "SELECT * FROM ratings_rec_movies where ratings_rec_movies.bot_name =  \"" + botName + "\";";
		
		return SQL;
	}
	
	public static String SQLselectCountAllRecMovieList(String botName){
		String SQL = "SELECT count(*) FROM ratings_rec_movies where ratings_rec_movies.bot_name =  \"" + botName + "\";";
		
		return SQL;
	}

	public static String SQLselectCountAllRecMovieListRated(String botName){
		String SQL = "	SELECT count(*) FROM ratings_rec_movies where ratings_rec_movies.bot_name = \"" + botName + "\" AND (ratings_rec_movies.like = 1 or ratings_rec_movies.dislike = 1);";
		
		return SQL;
	}
	
	public static String SQLselectNumberRecommendationListFromUser(int user_id, String botName){
		String SQL = "SELECT distinct number_recommendation_list FROM ratings_rec_movies "
					+ "WHERE user_id = " + user_id + " "
					+ "AND ratings_rec_movies.bot_name =  \"" + botName + "\" "
					+ "order by rated_at;";
		
		return SQL;
	}
	
	public static String SQLselectLikeAndDislikeFromRecommendationListByUserBotNameAndList(int user_id, String botName, int number_recommendation_list){
		String SQL = "	SELECT user_id, movie_uri,number_recommendation_list,ratings_rec_movies.like,dislike,position,response_time "
						+ "FROM ratings_rec_movies "
						+ "WHERE user_id = " + user_id + " "
						+ "AND ratings_rec_movies.bot_name =  \"" + botName + "\" "
						+ "AND number_recommendation_list = " + number_recommendation_list + " "
						+ "AND (ratings_rec_movies.like = 1 or ratings_rec_movies.dislike = 1) "		
						+ "order by rated_at;";
		
		return SQL;
	}
	
	public static String SQLselectUserBotNameAndListFromRecommendationList(int user_id, String botName, int number_recommendation_list){
		String SQL = "	SELECT user_id, movie_uri,number_recommendation_list,ratings_rec_movies.like,dislike,position,response_time "
						+ "FROM ratings_rec_movies "
						+ "WHERE user_id = " + user_id + " "
						+ "AND ratings_rec_movies.bot_name =  \"" + botName + "\" "
						+ "AND number_recommendation_list = " + number_recommendation_list + " "	
						+ "order by rated_at;";
		
		return SQL;
	}
	
}
