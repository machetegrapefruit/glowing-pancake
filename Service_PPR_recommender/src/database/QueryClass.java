package database;

public class QueryClass {
	
	public static String SQLinsertUser(String user_id) {
		String SQLuser = "INSERT INTO users (`id`)" +
				 		 " VALUE (\"" + user_id + "\")" +
				 		 " ON DUPLICATE KEY UPDATE `id` = `id`;";
		return SQLuser;
	}
	
	public static String SQLupdateNumberPagerankCicleByUser(String user_id, int pagerank_cicle) {
		String SQL = "UPDATE " + DBConstants.dbName + ".users"
					+ " SET pagerank_cicle = " + pagerank_cicle
					+ " WHERE users.id = " + user_id + ";";		
		return SQL;
	}
	
	public static String SQLupdateDetailsRecMovieRequestByUser(String user_id, String movieURI, int number_recommendation_list, String details) {
		String SQL = "UPDATE " + DBConstants.dbName + ".ratings_rec_movies"
					+ " SET details = \"" + details + "\" "
					+ " WHERE `user_id` = \"" + user_id + "\" AND " +
					 "`movie_uri` = \"" + movieURI + "\" AND " +
					 "`number_recommendation_list` = \"" + number_recommendation_list + "\";";
		return SQL;
	}
	
	public static String SQLupdateWhyRecMovieRequestByUser(String user_id, String movieURI, int number_recommendation_list, String why) {
		String SQL = "UPDATE " + DBConstants.dbName + ".ratings_rec_movies"
					+ " SET why = \"" + why + "\" "
					+ " WHERE `user_id` = \"" + user_id + "\" AND " +
					 "`movie_uri` = \"" + movieURI + "\" AND " +
					 "`number_recommendation_list` = \"" + number_recommendation_list + "\";";
		return SQL;
	}

	public static String SQLupdateRefineRecMovieRatingByUser(String user_id, String movieURI, int number_recommendation_list, String refine) {
		String SQL = "UPDATE " + DBConstants.dbName + ".ratings_rec_movies"
					+ " SET refine = \"" + refine + "\" "
					+ " WHERE `user_id` = \"" + user_id + "\" AND " +
					 "`movie_uri` = \"" + movieURI + "\" AND " +
					 "`number_recommendation_list` = \"" + number_recommendation_list + "\";";
		return SQL;
	}
	public static String SQLupdateLikeRecMovieRatingByUser(String user_id, String movieURI, int number_recommendation_list, int like) {
		String SQL = "UPDATE " + DBConstants.dbName + ".ratings_rec_movies"
					+ " SET ratings_rec_movies.like = " + like
					+ " WHERE `user_id` = \"" + user_id + "\" AND " +
					 "`movie_uri` = \"" + movieURI + "\" AND " +
					 "`number_recommendation_list` = \"" + number_recommendation_list + "\";";
		return SQL;
	}
	public static String SQLupdateDislikeRecMovieRatingByUser(String user_id, String movieURI, int number_recommendation_list, int dislike) {
		String SQL = "UPDATE " + DBConstants.dbName + ".ratings_rec_movies"
					+ " SET ratings_rec_movies.dislike = " + dislike
					+ " WHERE `user_id` = \"" + user_id + "\" AND " +
					 "`movie_uri` = \"" + movieURI + "\" AND " +
					 "`number_recommendation_list` = \"" + number_recommendation_list + "\";";
		return SQL;
	}
	public static String SQLupdateRefocusRecMovieRatingByUser(String user_id, String movieURI, int number_recommendation_list, String refocus) {
		String SQL = "UPDATE " + DBConstants.dbName + ".ratings_rec_movies"
					+ " SET refocus = \"" + refocus + "\" "
					+ " WHERE `user_id` = \"" + user_id + "\" AND " +
					 "`movie_uri` = \"" + movieURI + "\" AND " +
					 "`number_recommendation_list` = \"" + number_recommendation_list + "\";";
		return SQL;
	}
	public static String SQLupdateNumberRecommendationListByUser(String user_id, int number_recommendation_list) {
		String SQL = "UPDATE " + DBConstants.dbName + ".users"
					+ " SET number_recommendation_list = " + number_recommendation_list
					+ " WHERE users.id = " + user_id + ";";		
		return SQL;
	}
	
	public static String SQLupdateLastChange(String user_id,String lastChange){
		String SQL = "UPDATE " + DBConstants.dbName + ".users "
				+ "SET last_change = \"" + lastChange + "\" "
				+ "WHERE users.id = " + user_id + ";";
		
		return SQL;
	}
	
	public static String SQLupdateReleaseYearFilter (String user_id, String propertyValue){
		String SQL = "UPDATE " + DBConstants.dbName + ".users "
					+ "SET release_year_filter = \"" + propertyValue + "\" "
					+ "WHERE users.id = " + user_id + ";";
		
		return SQL;
	}
	
	public static String SQLupdateRuntimeRangeFilter (String user_id, String propertyValue){
		String SQL = "UPDATE " + DBConstants.dbName + ".users "
					+ "SET runtime_range_filter = \"" + propertyValue + "\" "
					+ "WHERE users.id = " + user_id + ";";

		return SQL;
	}

	public static String SQLupdateBotName (String user_id, String botName){
		String SQL = "UPDATE " + DBConstants.dbName + ".users "
					+ "SET bot_name = \"" + botName + "\" "
					+ "WHERE users.id = " + user_id + ";";

		return SQL;
	}
	
	public static String SQLupdateAgeRange (String user_id, String ageRange){
		String SQL = "UPDATE " + DBConstants.dbName + ".users "
					+ "SET age = \"" + ageRange + "\" "
					+ "WHERE users.id = " + user_id + ";";

		return SQL;
	}

	public static String SQLupdateEducation (String user_id, String education){
		String SQL = "UPDATE " + DBConstants.dbName + ".users "
					+ "SET education = \"" + education + "\" "
					+ "WHERE users.id = " + user_id + ";";

		return SQL;
	}

	public static String SQLupdateGender (String user_id, String gender){
		String SQL = "UPDATE " + DBConstants.dbName + ".users "
					+ "SET gender = \"" + gender + "\" "
					+ "WHERE users.id = " + user_id + ";";

		return SQL;
	}

	public static String SQLupdateInterestInMovies (String user_id, String interestInMovies){
		String SQL = "UPDATE " + DBConstants.dbName + ".users "
					+ "SET interest_in_movies = \"" + interestInMovies + "\" "
					+ "WHERE users.id = " + user_id + ";";

		return SQL;
	}

	public static String SQLupdateUsedRecSys (String user_id, String usedRecSys){
		String SQL = "UPDATE " + DBConstants.dbName + ".users "
					+ "SET used_recommender_system = \"" + usedRecSys + "\" "
					+ "WHERE users.id = " + user_id + ";";

		return SQL;
	}
	public static String SQLinsertBotConfiguration(String user_id, String botName, int number_recommendation_list,int bot_timestamp){
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`user_bot_configurations` (`user_id`, `bot_name`, `number_recommendation_list`, `bot_timestamp`)" +
					" VALUES (\"" + user_id + "\",\"" + botName + "\",\"" + number_recommendation_list + "\",\"" + bot_timestamp + "\")" +
					" ON DUPLICATE KEY UPDATE `bot_timestamp` = " + bot_timestamp + ";";
		
		return SQL;
	}
	

	public static String SQLinsertExperimentalSessionRating(String user_id, int number_recommendation_list, int rating, String botName) {
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`ratings_experimental_session` (`user_id`, `number_recommendation_list`, `rating`, `bot_name`)" +
					" VALUES (\"" + user_id + "\",\"" + number_recommendation_list + "\",\"" + rating + "\",\"" + botName + "\")" +
					" ON DUPLICATE KEY UPDATE `rating` = " + rating + ",`bot_name` = \"" + botName + "\";";
		return SQL;
	}
	
	public static String SQLinsertRatingAcceptRecMovies(String user_id, String movie_uri, int rating) {
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`ratings_accept_rec_movies` (`user_id`, `movie_uri`, `rating`)" +
					" VALUES (\"" + user_id + "\",\"" + movie_uri + "\",\"" + rating + "\")" +
					" ON DUPLICATE KEY UPDATE `rating` = " + rating + ";";
		return SQL;
	}
	//Non dovrebbe servire più
	public static String SQLinsertRecMovieRated (String user_id, String movieURI, int number_recommendation_list, int rating,int position,int pagerank_cicle, String refine,String refocus, String botName,String message_id, int bot_timestamp, String recommendatinsList,String ratingsList){
		//`user_id`, `movieURI`, `rating`, `position`, `pagerank_cicle`, `refineRefocus`, `botName`, `message_id`, `bot_timestamp`, `recommendatinsList`, `ratingsList`, `number_recommendation_list`
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`ratings_rec_movies` (`user_id`, `movie_uri`, `number_recommendation_list`, `rating`, `position`, `pagerank_cicle`, `refine`, `refocus`, `bot_name`, `message_id`, `bot_timestamp`, `recommendations_list`, `ratings_list`)" +
				" VALUES (\"" + user_id + "\",\"" + movieURI + "\",\"" + number_recommendation_list + "\",\"" + rating + "\",\"" + position + "\",\"" + pagerank_cicle + "\",\"" + refine + "\",\"" + refocus + "\",\"" + botName + "\",\"" + message_id + "\",\"" + bot_timestamp + "\",\"" + recommendatinsList + "\",\"" + ratingsList + "\",\")" +
				" ON DUPLICATE KEY UPDATE `rating` = " + rating + ",`refine` = \"" + refine + "\",`refocus` = \"" + refocus + "\";";
		return SQL;
	}

	public static String SQLinsertRecMovieToRating (String user_id, String movieURI, int number_recommendation_list, int position,int pagerank_cicle, String botName, String message_id, int bot_timestamp, int response_time, String recommendationListString){
		//`user_id`, `movieURI`, `rating`, `position`, `pagerank_cicle`, `refineRefocus`, `botName`, `message_id`, `bot_timestamp`, `recommendatinsList`, `ratingsList`, `number_recommendation_list`
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`ratings_rec_movies` (`user_id`, `movie_uri`, `number_recommendation_list`, `position`, `pagerank_cicle`, `bot_name`, `message_id`, `bot_timestamp`, `response_time`, recommendations_list)" +
				" VALUES (\"" + user_id + "\",\"" + movieURI + "\",\"" + number_recommendation_list + "\",\"" + position + "\",\"" + pagerank_cicle + "\",\"" + botName + "\",\"" + message_id + "\",\"" + bot_timestamp + "\",\"" + response_time + "\",\"" + recommendationListString + "\")" +
				" ON DUPLICATE KEY UPDATE `message_id` = \"" + message_id + "\",`bot_timestamp` = " + bot_timestamp + ",`response_time` = " + response_time + ";";
		return SQL;
	}
		

	public static String SQLinsertRatingMovies(String user_id, String movie_uri, int rating, String lastChange,int number_recommendation_list, String botName) {
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`ratings_movies` (`user_id`, `movie_uri`, `rating`, `last_change`,`number_recommendation_list`, `bot_name`)" +
					" VALUES (\"" + user_id + "\",\"" + movie_uri + "\",\"" + rating + "\",\"" + lastChange + "\",\"" + number_recommendation_list + "\",\"" + botName + "\")" +
					" ON DUPLICATE KEY UPDATE `rating` = " + rating + ", `last_change` = \"" + lastChange + "\", `number_recommendation_list` = \"" + number_recommendation_list + "\", `bot_name` = \"" + botName + "\";";
		return SQL;
	}
	
	public static String SQLinsertRatingMoviesToLog(String user_id, String movie_uri, int rating, String lastChange,int number_recommendation_list, String botName) {
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`ratings_movies_log` (`user_id`, `movie_uri`, `rating`, `last_change`,`number_recommendation_list`, `bot_name`)" +
					" VALUES (\"" + user_id + "\",\"" + movie_uri + "\",\"" + rating + "\",\"" + lastChange + "\",\"" + number_recommendation_list + "\",\"" + botName + "\")" +
					" ON DUPLICATE KEY UPDATE `rating` = " + rating + ", `last_change` = \"" + lastChange + "\", `bot_name` = \"" + botName + "\";";
		return SQL;
	}
	
	public static String SQLinsertDetailsMovieRequest(String user_id, String movieURI, String details, int number_recommendation_list, String botName) {
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`ratings_movies` (`user_id`, `movie_uri`, `details`, `number_recommendation_list`, `bot_name`)" +
				" VALUES (\"" + user_id + "\",\"" + movieURI + "\",\"" + details + "\",\"" + number_recommendation_list + "\",\"" + botName + "\")" +
				" ON DUPLICATE KEY UPDATE `details` = \"" + details + "\", `number_recommendation_list` = \"" + number_recommendation_list + "\", `bot_name` = \"" + botName + "\";";		
		
		return SQL;
	}
	
	public static String SQLinsertDetailsMovieRequestLog(String user_id, String movieURI, String details, int number_recommendation_list, String botName) {
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`ratings_movies_log` (`user_id`, `movie_uri`, `details`, `number_recommendation_list`, `bot_name`)" +
				" VALUES (\"" + user_id + "\",\"" + movieURI + "\",\"" + details + "\",\"" + number_recommendation_list + "\",\"" + botName + "\")" +
				" ON DUPLICATE KEY UPDATE `details` = \"" + details + "\", `bot_name` = \"" + botName + "\";";		
		
		return SQL;
	}	
	
	public static String SQLinsertRatingProperties(String user_id, String propertyTypeURI, String propertyURI, int rating, String lastChange,int numberRecommendationList, String botName) {
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`ratings_properties` (`user_id`,  `property_type_uri`, `property_uri`, `rating`, `last_change`, `number_recommendation_list`, `bot_name`)" +
					" VALUES (\"" + user_id + "\",\"" + propertyTypeURI + "\",\"" + propertyURI + "\",\"" + rating + "\",\"" + lastChange + "\",\"" + numberRecommendationList + "\",\"" + botName + "\")" +
					" ON DUPLICATE KEY UPDATE `rating` = " + rating + ", `last_change` = \"" + lastChange + "\", `number_recommendation_list` = \"" + numberRecommendationList + "\", `bot_name` = \"" + botName + "\";";
		return SQL;
	}
	
	public static String SQLinsertRatingPropertiesLog(String user_id, String propertyTypeURI, String propertyURI, int rating, String lastChange,int numberRecommendationList, String botName) {
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`ratings_properties_log` (`user_id`,  `property_type_uri`, `property_uri`, `rating`, `last_change`, `number_recommendation_list`, `bot_name`)" +
					" VALUES (\"" + user_id + "\",\"" + propertyTypeURI + "\",\"" + propertyURI + "\",\"" + rating + "\",\"" + lastChange + "\",\"" + numberRecommendationList + "\",\"" + botName + "\")" +
					" ON DUPLICATE KEY UPDATE `rating` = " + rating + ", `last_change` = \"" + lastChange + "\",`bot_name` = \"" + botName + "\";";
		return SQL;
	}
	
	public static String SQLinsertChatMessageToChatLog(String user_id, String message_id, String context, String replyText, String replyFuctionCall, int pagerank_cicle,int number_recommendation_list, String botName, int bot_timestamp, int response_time, String responseType, int number_rated_movies, int number_rated_properties){
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`chat_log` (`chat_id`,  `message_id`, `context`, `reply_text`, `reply_function_call`,`pagerank_cicle`, `number_recommendation_list`, `bot_name`, `bot_timestamp`, `response_time`,`response_type`,`number_rated_movies`,`number_rated_properties`)" +
				" VALUES (\"" + user_id + "\",\"" + message_id + "\",\"" + context + "\",\"" + replyText + "\",\"" + replyFuctionCall + "\",\"" + pagerank_cicle + "\",\"" + number_recommendation_list + "\",\"" + botName + "\",\"" + bot_timestamp + "\",\"" + response_time + "\",\"" + responseType + "\",\"" + number_rated_movies + "\",\"" + number_rated_properties + "\");";
		
		return SQL;		
	}
	public static String SQLinsertChatMessage(String user_id, String message_id, String context, String replyText, String replyFuctionCall, int pagerank_cicle, int number_recommendation_list, String botName, int bot_timestamp, int response_time, String responseType, int number_rated_movies, int number_rated_properties){
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`chats` (`chat_id`,  `message_id`, `context`, `reply_text`, `reply_function_call`,`pagerank_cicle`, `number_recommendation_list`, `bot_name`,`bot_timestamp`,`response_time`,`response_type`,`number_rated_movies`,`number_rated_properties`)" +
				" VALUES (\"" + user_id + "\",\"" + message_id + "\",\"" + context + "\",\"" + replyText + "\",\"" + replyFuctionCall + "\",\"" + pagerank_cicle + "\",\"" + number_recommendation_list + "\",\"" + botName + "\",\"" + bot_timestamp + "\",\"" + response_time + "\",\"" + responseType + "\",\"" + number_rated_movies + "\",\"" + number_rated_properties + "\");";
		
		return SQL;		
	}
	
	public static String SQLinsertUserDetail(String user_id, String firstname, String lastname, String user_name){
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`users` (`id`, `firstname`,`lastname`,`username`)" +
				" VALUES (\"" + user_id + "\",\"" + firstname + "\",\"" + lastname + "\",\"" + user_name + "\")" +
				" ON DUPLICATE KEY UPDATE `firstname` = \"" + firstname + "\", `lastname` = \"" + lastname + "\",`username` = \"" + user_name + "\";";
		return SQL;
	}
	
	public static String SQLinsertScoresRecMovies(String user_id, String movieURI, String propertyTypeURI, String propertyURI,double propertyScore){
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`scores_rec_movies` "
				+ "(`user_id`, `movie_uri`,`property_type_uri`,`property_uri`,`score`)" +
				" VALUES (\"" + user_id + "\",\"" + movieURI + "\",\"" + propertyTypeURI + "\",\"" + propertyURI + "\",\"" + propertyScore + "\")" +
				" ON DUPLICATE KEY UPDATE `score` = " + propertyScore + ";";
		
		return SQL;
	}
	
	public static String SQLinsertScoresUserMovies(String user_id, String movieURI, String propertyTypeURI, String propertyURI,double propertyScore){
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`scores_user_movies` "
				+ "(`user_id`, `movie_uri`,`property_type_uri`,`property_uri`,`score`)" +
				" VALUES (\"" + user_id + "\",\"" + movieURI + "\",\"" + propertyTypeURI + "\",\"" + propertyURI + "\",\"" + propertyScore + "\")" +
				" ON DUPLICATE KEY UPDATE `score` = " + propertyScore + ";";
		
		return SQL;
	}
	
	public static String SQLinsertScoresUserProperties(String user_id, String propertyTypeURI, String propertyURI, double propertyScore){
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`scores_user_properties` "
				+ "(`user_id`,`property_type_uri`,`property_uri`,`score`)" +
				" VALUES (\"" + user_id + "\",\"" + propertyTypeURI + "\",\"" + propertyURI + "\",\"" + propertyScore + "\")" +
				" ON DUPLICATE KEY UPDATE `score` = " + propertyScore + ";";
		
		return SQL;
	}
	
	public static String SQLinsertVertexPosterSelection(String vertexString, double vertexScore){
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`vertices_poster_selection` "
				+ "(`uri`, `score`)" +
				" VALUES (\"" + vertexString + "\",\"" + vertexScore + "\")" +
				" ON DUPLICATE KEY UPDATE `score` = " + vertexScore + ";";
		
		return SQL;
	}
	
	public static String SQLinsertVertexTrailerSelection(String vertexString, double vertexScore){
		String SQL = "INSERT INTO `" + DBConstants.dbName + "`.`vertices_trailer_selection` "
				+ "(`uri`, `score`)" +
				" VALUES (\"" + vertexString + "\",\"" + vertexScore + "\")" +
				" ON DUPLICATE KEY UPDATE `score` = " + vertexScore + ";";
		
		return SQL;
	}
	
	//public static String 
	
	public static String SQLupdateNumberRatedMoviesByUser (String user_id){
		String SQL = "UPDATE " + DBConstants.dbName + ".users "
					+ "SET rated_movies = "
					+ "(SELECT COUNT(*) FROM " + DBConstants.dbName + ".ratings_movies "
						+ "WHERE ratings_movies.user_id = users.id "
						+ "AND (ratings_movies.rating = 0 "
						+ "OR ratings_movies.rating = 1)) "
					+ "WHERE users.id = " + user_id + ";";
		return SQL;
	}

	public static String SQLupdateNumberRatedPropertiesByUser (String user_id){
		String SQL = "UPDATE " + DBConstants.dbName + ".users "
					+ "SET rated_properties = "
					+ "(SELECT COUNT(*) FROM " + DBConstants.dbName + ".ratings_properties "
						+ "WHERE ratings_properties.user_id = users.id "
						+ "AND ratings_properties.last_change = \"user\" "
						+ "AND (ratings_properties.rating = 0 "
						+ "OR ratings_properties.rating = 1)) "
					+ "WHERE users.id = " + user_id + ";";
		return SQL;
	}
	
	public static String SQLgetNumberRatedPropertiesWithoutDuplicates(String user_id) {
		String SQL = "SELECT count(distinct property_uri) FROM " + DBConstants.dbName + ".ratings_properties where user_id = " + user_id + ";";
		return SQL;
	}

	//NON usata
	public static String SQLupdatePropertiesScores(String user_id, String movieURI, String propertyTypeURI, String propertyURI,double propertyScore){
		String SQL = "UPDATE `" + DBConstants.dbName + "`.`scores_rec_movies` " +
				 " SET `user_id` = \"" + user_id + "\","+
				 "`movie_uri` = \"" + movieURI + "\"," +
				 "`property_type_uri` = \"" + propertyTypeURI + "\"," +
				 "`property_uri` = \"" + propertyURI + "\"," +
				 "`score` = \"" + propertyScore + "\"," +
				 " WHERE `user_id` = \"" + user_id + "\" AND " +
				 "`movie_uri` = \"" + movieURI + "\" AND " +
				 "`property_type_uri` = \"" + propertyTypeURI + "\" AND " +
				 "`property_uri` = \"" + propertyURI + "\";";
		
		return SQL;
	}
	
	public static String SQLupdateUserDetailToNullByUser(String user_id){
		String SQL =	"UPDATE users "
						+ "SET age = NULL, "
							+ "gender = NULL, "
							+ "education = NULL, "
							+ "interest_in_movies = NULL, "
							+ "used_recommender_system = NULL, "
							+ "bot_name = NULL "
						+ "WHERE `id` = " + user_id + ";";
		
		return SQL;
	}

	public static String SQLdeleteAllScoreByUserFromScoresRecMovies(String user_id) {
		String SQL = 	"DELETE FROM `" + DBConstants.dbName + "`.`scores_rec_movies`"
						+ 	" WHERE `user_id` = " + user_id + ";";
		return SQL;
	}
	
	public static String SQLdeleteAllScoreByUserFromScoresUserMovies(String user_id) {
		String SQL = 	"DELETE FROM `" + DBConstants.dbName + "`.`scores_user_movies`"
						+ 	" WHERE `user_id` = " + user_id + ";";
		return SQL;
	}
	
	public static String SQLdeleteAllScoreByUserFromScoresUserProperties(String user_id) {
		String SQL = 	"DELETE FROM `" + DBConstants.dbName + "`.`scores_user_properties`"
						+ 	" WHERE `user_id` = " + user_id + ";";
		return SQL;
	}
	
	public static String SQLdeleteAllPropertyRatedByUser(String user_id) {
		String SQL = 	"DELETE FROM `" + DBConstants.dbName + "`.`ratings_properties`"
						+ " WHERE `user_id` = " + user_id + ";";
		return SQL;
	}
	
	public static String SQLdeletePropertyRatedByUser(String user_id, String uri) {
		String SQL = 	"DELETE FROM `" + DBConstants.dbName + "`.`ratings_properties`"
				+ " WHERE `user_id` = " + user_id
				+ " AND `property_uri` = '" + uri + "';";
		return SQL;
	}
	
	public static String SQLdeleteAllMovieRatedByUser(String user_id) {
		String SQL = 	"DELETE FROM `" + DBConstants.dbName + "`.`ratings_movies`"
						+ " WHERE `user_id` = " + user_id + ";";
		return SQL;
	}
	
	public static String SQLdeleteMovieRatedByUser(String user_id, String uri) {
		String SQL = 	"DELETE FROM `" + DBConstants.dbName + "`.`ratings_movies`"
						+ " WHERE `user_id` = " + user_id
						+ " AND `movie_uri` = '" + uri + "';";
		return SQL;
	}
	
	public static String SQLdeleteAllChatMessageByUser(String user_id){
		String SQL =	"DELETE FROM `" + DBConstants.dbName + "`.`chats`"
					  + " WHERE `chat_id` = " + user_id + ";";
		
		return SQL;
	}

	public static String SQLselectNumberRefineFromRecMovieListByUserAndRecList(String user_id, int numberRecommendationList) {
		String SQL = 	"SELECT COUNT(*) as number_refine_rec_movie "
						+ "FROM ratings_rec_movies "
						+ "WHERE number_recommendation_list = " + numberRecommendationList + " "
						+ "AND user_id = " + user_id + " "
						+ "AND ratings_rec_movies.refine = \"refine\";";
		return SQL;
	}
	
	//conta il numero di like e dislike dei film raccomandati (e anche refine)
	public static String SQLselectNumberRatedRecMovieByUserAndRecListByUser(String user_id, int numberRecommendationList) {
		String SQL = 	"SELECT COUNT(*) as number_rated_rec_movie "
						+ "FROM ratings_rec_movies "
						+ "WHERE number_recommendation_list = " + numberRecommendationList + " "
						+ "AND user_id = " + user_id + " "
						+ "AND ((ratings_rec_movies.like = 1 "
						+ "OR ratings_rec_movies.dislike = 1) OR ratings_rec_movies.refine = \"refine\");";
		return SQL;
	}
	
	//conta le configurazioni del bot assegnate
	public static String SQLselectNumberOfBotNameByBotName(String botName) {
		String SQL = 	"SELECT COUNT(*) as number_bot_name"
						+ " FROM users"
						+ " WHERE bot_name = \"" + botName + "\";";
		return SQL;
	}
	
	public static String SQLselectNumberOfRatedMoviesByUser(String user_id) {
		String SQL = 	"SELECT rated_movies "
						+ "FROM " + DBConstants.dbName + ".users "
						+ "WHERE id = " + user_id + ";";
		return SQL;
	}

	public static String SQLselectNumberOfRatedMoviesAndSkippedByUser(String user_id) {
		String SQL = 	"SELECT count(movie_uri) as rated_movies "
						+ "FROM " + DBConstants.dbName + ".ratings_movies "
						+ "WHERE user_id = " + user_id + ";";
		return SQL;
	}
	
	public static String SQLselectNumberOfRatedMoviesSkippedByUser(String user_id) {
		String SQL = 	"SELECT count(rating) AS rated_movies "
						+ "FROM " + DBConstants.dbName + ".ratings_movies "
						+ "WHERE user_id = " + user_id + " AND rating=2;";
		return SQL;
	}
	
	public static String SQLselectMovieLensReference(String label) {
		String SQL = 	"SELECT AVG(s3.rating) as rating "
						+ "FROM (SELECT s1.movieid,rating as rating FROM ml_dataset_ratings as s1 "
						+ "INNER JOIN (SELECT movieid FROM ml_dataset_movies where title LIKE "+"\""+"%"+label+"%"+"\""+" LIMIT 1) AS s2 ON "
						+ "s1.movieid = s2.movieid) AS s3;";
		return SQL;
	}
		
	public static String SQLselectNumberOfRatedPropertiesByUser(String user_id) {
		String SQL = 	"SELECT rated_properties "
						+ "FROM " + DBConstants.dbName + ".users "
						+ "WHERE id = " + user_id + ";";
		return SQL;
	}
	
	public static String SQLselectNumberOfPagerankCicleByUser(String user_id) {
		String SQL = 	"SELECT pagerank_cicle "
						+ "FROM " + DBConstants.dbName + ".users "
						+ "WHERE id = " + user_id + ";";
		return SQL;
	}
	
	public static String SQLselectNumberRecommendationListByUserByUser(String user_id) {
		String SQL = 	"SELECT number_recommendation_list "
						+ "FROM " + DBConstants.dbName + ".users "
						+ "WHERE id = " + user_id + ";";
		return SQL;
	}

	public static String SQLselectLastChangeByUser(String user_id) {
		String SQL = 	"SELECT last_change "
						+ "FROM " + DBConstants.dbName + ".users "
						+ "WHERE id = " + user_id + ";";
		return SQL;
	}
	
	public static String SQLselectLastBotTimestamp(String user_id){
		String SQL = "SELECT bot_timestamp FROM chat_log "
					+ "WHERE chat_id = " + user_id + " "
					+ "ORDER BY id DESC LIMIT 1;";
		
		return SQL;
	}
	
	public static String SQLselectBotTimestampFromRatingsRecMovies(String user_id, int number_recommendation_list, int position){
		String SQL = "SELECT bot_timestamp "
					+ "FROM " + DBConstants.dbName + ".ratings_rec_movies "
					+ "WHERE user_id = " + user_id + " "
					+ "AND number_recommendation_list = " + number_recommendation_list + " "
					+ "AND position = " + position + " ;";
				
	 	return SQL;
	}

	public static String SQLselectAllMovies() {
		String SQL = 	"SELECT uri,title FROM movies" +
						" WHERE `poster` != \"N/A\";";
		return SQL;
	}
	
	public static String SQLselectResourceUriFromDbpediaMoviesSelection(String uri){
		String SQL = "SELECT * FROM " + DBConstants.dbName + ".mapping_dbpedia_movies_poster_selection"
				+ " WHERE  \"" + uri + "\" IN (subject,predicate,object) limit 1;";
		
		return SQL;
	}
	
	public static String SQLselectUserDetalByUser(String user_id){
		String SQL = "SELECT id, firstname, lastname, username, bot_name, age, gender, education, interest_in_movies, used_recommender_system"
				+ " FROM " + DBConstants.dbName + ".users"
				+ " WHERE users.id = " + user_id + " ;";
		
		return SQL;
	}
	
	public static String SQLselectMessageDetailAndContextByUser(String user_id, String context, int pagerank_cicle){
		String SQL = "SELECT *"
				+ " FROM " + DBConstants.dbName + ".chats"
				+ " WHERE chats.chat_id = " + user_id
				+ " AND chats.context = \"" + context + "\""
				+ " AND chats.pagerank_cicle = " + pagerank_cicle
				+ " ORDER BY ID DESC LIMIT 1;";
		
		return SQL;
	}
	
	public static String SQLselectPropertyRatingByUserAndProperty(String user_id, String property_type_uri, String property_uri, String last_change){
		String SQL = "SELECT user_id, property_type_uri, property_uri, rating, rated_at, last_change"
				+ " FROM " + DBConstants.dbName + ".ratings_properties"
				+ " WHERE ratings_properties.user_id = " + user_id
				+ " AND ratings_properties.property_type_uri = \"" + property_type_uri + "\""
				+ " AND ratings_properties.property_uri = \"" + property_uri + "\""
				+ " AND ratings_properties.last_change = \"" + last_change + "\""
				+ " ;";
		
		return SQL;
	}

	public static String SQLselectUser(String user_id) {
		String SQL = 	"SELECT id FROM users" +
						" WHERE `id` = " + user_id + ";";
		return SQL;
	}
	
	public static String SQLselectMovie(String uri) {   //TODO
		return "SELECT uri FROM  " + DBConstants.dbName + ".entities WHERE uri = ?";
	}
		

	
	public static String SQLselectMovieFromScoresByUserAndProperty(String user_id, String propertyTypeURI, String propertyURI) {
		String SQL = "SELECT movie_uri, score FROM scores_rec_movies" +
					" WHERE scores_rec_movies.user_id = " + user_id + 
					" AND scores_rec_movies.property_type_uri = \"" + propertyTypeURI + "\"" +
					" AND scores_rec_movies.property_uri = \"" + propertyURI + "\";";
		return SQL;
	}
		
	public static String SQLselectMovieAndScoreFromScoresByUserAndMovie(String user_id, String movieURI) {
		String SQL = "SELECT movie_uri, score FROM scores_rec_movies" +
					" WHERE scores_rec_movies.user_id = " + user_id + 
					" AND scores_rec_movies.property_type_uri = \"entity\"" +
					" AND scores_rec_movies.property_uri = \"" + movieURI + "\";";
		
		
//		String SQL = 	"SELECT scores_rec_movies.movie_uri, scores_rec_movies.score FROM scores_rec_movies" +
//						" INNER JOIN " + propertyTable + " ON scores_rec_movies.movie_uri = " + propertyTable + "." + columnMovieURI +
//						" WHERE scores_rec_movies.user_id = " + user_id + 
//						" AND scores_rec_movies.property_type_uri = \"" + propertyTypeURI + "\"" +
//						" AND " + propertyTable + "." + columnPropertyURI + " = \"" + propertyURI + "\";";
		
		return SQL;
	}
	
	public static String SQLselectPropertyValueAndScoreFromScoresRecMovies(String user_id, String movieURI, String propertyTypeURI){
		String SQL = "SELECT property_uri, score FROM scores_rec_movies" +
					" WHERE scores_rec_movies.user_id = " + user_id + 
					" AND scores_rec_movies.property_type_uri = \"" + propertyTypeURI + "\"" +
					" AND scores_rec_movies.movie_uri = \"" + movieURI + "\";";
		
		return SQL;
	}
	
	public static String SQLselectMoviesAndScoreFromScoresRecMovies(String user_id) {
		String SQL = 	"SELECT movie_uri, score FROM scores_rec_movies" +
						" WHERE scores_rec_movies.user_id = " + user_id + 
						" AND scores_rec_movies.property_type_uri = \"entity\";";
		return SQL;
	}
	
	public static String SQLselectMoviesMapAndScoreFromScoresRecMovies(String user_id, String propertyTypeURI, String propertyTable) {
		String SQL = 	"SELECT distinct property_uri as uri, score FROM scores_rec_movies "
						+ "WHERE scores_rec_movies.user_id = " + user_id + " AND scores_rec_movies.property_type_uri = \"" + propertyTypeURI + "\""
						+ " ORDER BY score DESC LIMIT 10;";
		return SQL;
	}
	
	public static String SQLselectBotConfigurationSetByUser(String user_id){
		String SQL = 	"SELECT distinct bot_name  FROM user_bot_configurations "
						+ "WHERE user_bot_configurations.user_id = " + user_id + ";";
		
		return SQL;
		
	}
	
	//TODO - DONE unisce i diversi insieme di propriet� considerando 
	//in unione quelle con score maggiore al tempo 0, quelle gradite, quelle dei film graditi e quelli dei film raccomandabili
	public static String SQLselectPropertyValueListMapFromPropertyType(String user_id, String propertyTypeURI, String propertyTable){
		String SQL = "(SELECT distinct s.uri AS uri, v.score AS score "
					+ "FROM " + propertyTable + " AS s "
					+ "INNER JOIN vertices_trailer_selection v ON s.uri = v.uri "
					+ "AND s.uri <> all"
						+ "(SELECT distinct property_uri AS uri FROM scores_user_properties "
						+ "WHERE scores_user_properties.user_id = " + user_id + " "
						+ "AND scores_user_properties.property_type_uri = \"" + propertyTypeURI + "\")"
					+ "ORDER BY score DESC LIMIT 50) "
				+ "UNION "
				+  "(SELECT distinct property_uri AS uri, score FROM scores_user_movies "
					+ "WHERE scores_user_movies.user_id = " + user_id + " AND scores_user_movies.property_type_uri = \"" + propertyTypeURI + "\" "
					+ "AND property_uri <> all"
						+ "(SELECT distinct property_uri AS uri FROM scores_user_properties "
						+ "WHERE scores_user_properties.user_id = " + user_id + " "
						+ "AND scores_user_properties.property_type_uri = \"" + propertyTypeURI + "\")"
					+ " ORDER BY score DESC LIMIT 15) "
				+ "UNION "
					+ "(SELECT distinct property_uri AS uri, score FROM scores_rec_movies "
					+ "WHERE scores_rec_movies.user_id = " + user_id + " AND scores_rec_movies.property_type_uri = \"" + propertyTypeURI + "\" "
					+ "AND property_uri <> all"
						+ "(SELECT distinct property_uri AS uri FROM scores_user_properties "
						+ "WHERE scores_user_properties.user_id = " + user_id + " "
						+ "AND scores_user_properties.property_type_uri = \"" + propertyTypeURI + "\")"
					+ " ORDER BY score DESC LIMIT 15) "
				+ "ORDER BY score DESC LIMIT 50;";
		
		return SQL;
	}
	
	public static String getBestProperties() {
		return "SELECT property_uri FROM scores_user_properties"
				+ " WHERE user_id = ? AND property_type_uri = ?"
				+ " ORDER BY score DESC";
	}

	public static String getEntitiesToRecommendQuery() {
		return "SELECT * FROM scores_rec_movies "
				+ "WHERE property_type_uri = \"entity\" "
				+ "AND user_id = ? "
				+ "ORDER BY score DESC LIMIT 50;";
	}
	
	public static String SQLselectPropertyValueListMapForReleaseYearAndRuntimeRange(String user_id, String propertyTypeURI, String propertyTable, String columnPropertyURI){
		String SQL = "(SELECT distinct s." + columnPropertyURI + " AS uri, v.score AS score "
					+ "FROM " + propertyTable + " AS s "
					+ "INNER JOIN vertices_trailer_selection v ON s.uri = v.uri "
					+ "ORDER BY score DESC LIMIT 30) "
				+ "UNION "
				+  "(SELECT distinct property_uri AS uri, score FROM scores_user_movies "
					+ "WHERE scores_user_movies.user_id = " + user_id + " AND scores_user_movies.property_type_uri = \"" + propertyTypeURI + "\""
					+ " ORDER BY score DESC LIMIT 10) "
				+ "UNION "
					+ "(SELECT distinct property_uri AS uri, score FROM scores_rec_movies "
					+ "WHERE scores_rec_movies.user_id = " + user_id + " AND scores_rec_movies.property_type_uri = \"" + propertyTypeURI + "\""
					+ " ORDER BY score DESC LIMIT 10) "
				+ "UNION "
					+ "(SELECT distinct property_uri AS uri, score FROM scores_user_properties "
					+ "WHERE scores_user_properties.user_id = " + user_id + " AND scores_user_properties.property_type_uri = \"" + propertyTypeURI + "\""
					+ " ORDER BY score DESC LIMIT 10) "
				+ "ORDER BY score DESC LIMIT 30;";
		
		return SQL;
	}

	@Deprecated
	public static String SQLselectDirectorFromDirectorsMovies(String movie_uri) {
		String SQL = 	"SELECT director_uri FROM directors_movies" +
						" WHERE `movie_uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	public static String SQLselectProducerFromProducersMovies(String movie_uri) {
		String SQL = 	"SELECT producer_uri FROM producers_movies" +
						" WHERE `movie_uri` = \"" + movie_uri + "\";";
		return SQL;	
	}
	
	public static String SQLselectWriterFromWritersMovies(String movie_uri) {
		String SQL = 	"SELECT writer_uri FROM writers_movies" +
						" WHERE `movie_uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	public static String SQLselectStarringFromStarringMovies(String movie_uri) {
		String SQL = 	"SELECT starring_uri FROM starring_movies" +
						" WHERE `movie_uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	public static String SQLselectMusicComposerFromMusicComposersMovies(String movie_uri) {
		String SQL = 	"SELECT music_composer_uri FROM music_composers_movies" +
						" WHERE `movie_uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	public static String SQLselectCinematographyFromCinematographersMovies(String movie_uri) {
		String SQL = 	"SELECT cinematography_uri FROM cinematographers_movies" +
						" WHERE `movie_uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	public static String SQLselectEditingFromEditingsMovies(String movie_uri) {
		String SQL = 	"SELECT editing_uri FROM editings_movies" +
						" WHERE `movie_uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	public static String SQLselectDistributorFromDistributorsMovies(String movie_uri) {
		String SQL = 	"SELECT distributor_uri FROM distributors_movies" +
						" WHERE `movie_uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	public static String SQLselectBasedOnFromBasedOnMovies(String movie_uri) {
		String SQL = 	"SELECT based_on_uri FROM based_on_movies" +
						" WHERE `movie_uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//title
	public static String SQLselectTitleFromMovies(String movie_uri) {
		String SQL = 	"SELECT title FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	releaseYear
	public static String SQLselectReleaseYearFromMovies(String movie_uri) {
		String SQL = 	"SELECT release_year FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;
	}
	
	//	referencePeriod
	public static String SQLselectReferencePeriodFromMovies(String movie_uri) {
		String SQL = 	"SELECT reference_period FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;
	}
		
	//	releaseDate
	public static String SQLselectReleaseDateFromMovies(String movie_uri) {
		String SQL = 	"SELECT release_date FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;
	}
	
	//	runtimeMinutes
	public static String SQLselectRuntimeMinutesFromMovies(String movie_uri) {
		String SQL = 	"SELECT runtime_minutes FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	runtimeRange
	public static String SQLselectRuntimeRangeFromMovies(String movie_uri) {
		String SQL = 	"SELECT runtime_range FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	runtimeUri
	public static String SQLselectRuntimeURIFromMovies(String movie_uri) {
		String SQL = 	"SELECT runtime_uri FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	plot
	public static String SQLselectPlotFromMovies(String movie_uri) {
		String SQL = 	"SELECT plot FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	language
	public static String SQLselectLanguageFromMovies(String movie_uri) {
		String SQL = 	"SELECT language FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	country
	public static String SQLselectCountryFromMovies(String movie_uri) {
		String SQL = 	"SELECT country FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	awards
	public static String SQLselectAwardsFromMovies(String movie_uri) {
		String SQL = 	"SELECT awards FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	poster
	public static String SQLselectPosterFromMovies(String movie_uri) {
		String SQL = 	"SELECT poster FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	trailer
	public static String SQLselectTrailerFromMovies(String movie_uri) {
		String SQL = 	"SELECT trailer FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	score
	public static String SQLselectScoreFromMovies(String movie_uri) {
		String SQL = 	"SELECT score FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	metascore
	public static String SQLselectMetascoreFromMovies(String movie_uri) {
		String SQL = 	"SELECT metascore FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	imdbRating
	public static String SQLselectImdbRatingFromMovies(String movie_uri) {
		String SQL = 	"SELECT imdb_rating FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	imdbId
	public static String SQLselectImdbIdFromMovies(String movie_uri) {
		String SQL = 	"SELECT imdb_id FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	//	imdbVotes
	public static String SQLselectImdbVotesFromMovies(String movie_uri) {
		String SQL = 	"SELECT imdb_votes FROM movies" +
						" WHERE `uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	

	public static String SQLselectCategoryFromCategoriesMovies(String movie_uri) {
		String SQL = 	"SELECT category_uri FROM categories_movies" +
						" WHERE `movie_uri` = \"" + movie_uri + "\";";
		return SQL;		
	}
	
	public static String SQLselectAcceptRecMovieToControlling(String user_id){
		String SQL = 	"SELECT movie_uri FROM ratings_movies "
						+ "WHERE `user_id` = " + user_id;
		
		return SQL;
	}
	
	public static String SQLselectAcceptRecMovieToSkipping(String user_id){
		String SQL = 	"SELECT movie_uri FROM ratings_movies "
						+ "WHERE `user_id` = " + user_id + " AND rating = 2";
		
		return SQL;
	}
	
	
	public static String SQLselectAcceptRecMovieToRatingByUser(String user_id){
		String SQL = 	"SELECT movie_uri FROM ratings_accept_rec_movies "
						+ "WHERE `user_id` = " + user_id + " AND rating = 3 "
						+ "ORDER BY rated_at DESC limit 1;";
		
		return SQL;
	}
	
	public static String SQLselectMovieToRatingByUser(String user_id, String movie_uri){
		String SQL = 	"SELECT movie_uri FROM ratings_movies" +
						" WHERE `user_id` = " + user_id + " AND `movie_uri` = \"" + movie_uri + "\" ORDER BY movie_uri ASC;";
		return SQL;
	}
	
	public static String SQLselectMovieToRatingByUserEntities(String user_id, String movie_uri){
		String SQL = 	"SELECT uri FROM entities" +
						" WHERE label like \"" + movie_uri + "%" + "\" ORDER BY label ASC LIMIT 1;";
		return SQL;
	}
	
	public static String SQLselectMovieToRatingByRandPopularUser(){
		String SQL = 	/*"SELECT movie_uri FROM ratings_movies" +
						" WHERE rating=1 ORDER BY rand() limit 1;";*/
						"SELECT s5.title as movie_uri FROM ml_dataset_movies AS s5 INNER JOIN "+
						"(SELECT s3.movieid as movieid FROM (SELECT s1.movieid,rating as rating FROM "+
						"ml_dataset_ratings as s1 INNER JOIN (SELECT movieid FROM ml_dataset_movies ORDER BY RAND() LIMIT 100) AS s2 "+
						"ON s1.movieid = s2.movieid) AS s3 ORDER BY s3.rating DESC) AS s4 ON s5.movieid=s4.movieid LIMIT 1;";
		return SQL;
	}
	
	public static String SQLselectMovieToRatingByPopxVarUser(){
		String SQL = 	/*"SELECT tot.movie_uri as movie_uri FROM " +
						"(SELECT uri_dislike.uri_dislike as movie_uri " +
						"FROM (SELECT count(movie_uri) as clike, movie_uri as uri_like from ratings_rec_movies " +
						"WHERE ratings_rec_movies.like=1 group by uri_like) uri_like " +
						"INNER JOIN (SELECT count(movie_uri) as cdislike, movie_uri as uri_dislike from ratings_rec_movies " + 
						"WHERE ratings_rec_movies.like=0 group by uri_dislike) uri_dislike ON uri_like.uri_like=uri_dislike.uri_dislike " + 
						"ORDER BY movie_uri DESC LIMIT 30) tot ORDER BY RAND() LIMIT 1;";*/
						"SELECT s3.title as title FROM (SELECT s1.title AS title, s2.count FROM ml_dataset_movies AS s1 INNER JOIN (" +
						"SELECT movieid,count(rating) AS count from ml_dataset_ratings GROUP BY movieid ORDER BY count DESC LIMIT 100)" +
						" AS s2 ON s1.movieid=s2.movieid) AS s3 ORDER BY RAND() LIMIT 1";
		return SQL;
	}
	
	public static String SQLselectPosRatingForUserFromRatingsMovies(String user_id) {
		String SQL = 	"SELECT movie_uri FROM ratings_movies" +
						" WHERE `user_id` = " + user_id + " AND rating = 1;";
		return SQL;
		
	}
	
	public static String SQLselectNegRatingForUserFromRatingsMovies(String user_id) {
		String SQL = 	"SELECT movie_uri FROM ratings_movies" +
						" WHERE `user_id` = " + user_id + " AND rating = 0;";
		return SQL;
		
	}
	
	public static String SQLselectPosRatingForUserFromRatingsMoviesByUser(String user_id) {
		String SQL = 	"SELECT movie_uri FROM ratings_movies" +
						" WHERE `user_id` = " + user_id + " AND rating = 1 AND last_change = 'user';";
		return SQL;
		
	}
	
	public static String SQLselectNegRatingForUserFromRatingsMoviesByUser(String user_id) {
		String SQL = 	"SELECT movie_uri FROM ratings_movies" +
						" WHERE `user_id` = " + user_id + " AND rating = 0 AND last_change = 'user';";
		return SQL;
		
	}
	
	//seleziona il filtro se presente per visualizzarlo e modificarlo dal profilo utente
	public static String SQLselectReleaseYearFilterFromUsersForPropertyRating(String user_id){
		String SQL = "SELECT release_year_filter FROM " + DBConstants.dbName + ".users "
					+ "WHERE id =  " + user_id + " "
					+ "AND release_year_filter <> 'no_release_year_filter';";
		
		return SQL;
	}
	
	//seleziona il filtro se presente per visualizzarlo e modificarlo dal profilo utente
	public static String SQLselectRuntimeRangeFilterFromUsersForPropertyRating(String user_id){
		String SQL = "SELECT runtime_range_filter FROM " + DBConstants.dbName + ".users "
					+ "WHERE id =  " + user_id + " "
					+ "AND runtime_range_filter <> 'no_runtime_range_filter';";
		
		return SQL;
	}

	public static String SQLselectPosNegRatingForUserFromRatingsProperties(String user_id) {
		String SQL = 	"SELECT property_uri, property_type_uri, rating FROM ratings_properties" +
						" WHERE `user_id` = " + user_id + " "
						+ "AND `last_change` = 'user' "
						+ "AND (ratings_properties.rating = 0 OR ratings_properties.rating = 1);";
		return SQL;		
	}
	
	public static String SQLselectRatingsForUserFromRatingsProperties(String user_id) {
		String SQL =    "SELECT property_uri, property_type_uri, rating FROM ratings_properties" +
	                    " WHERE `user_id` = " + user_id + " "
	                    + "AND `last_change` = 'user'";
		return SQL;
	}
	
	public static String selectPosNegRatingFromRatingsPropertiesForPageRank(String user_id) {
		String SQL = 	"SELECT property_uri, property_type_uri, rating FROM ratings_properties" +
						" WHERE `user_id` = " + user_id + " "
						+ "AND (ratings_properties.rating = 0 OR ratings_properties.rating = 1);";
		return SQL;		
	}
	
	public static String selectNegRatingFromRatingsMoviesAndPropertiesForPageRank(String user_id) {
		String SQL = 	"(SELECT movie_uri as uri FROM ratings_movies "
						+ "WHERE `user_id` = " + user_id + " AND rating = 0) "
						+ "union "
						+ "(SELECT property_uri as uri FROM ratings_properties "
						+ "WHERE `user_id` = " + user_id + " "
						+ "AND ratings_properties.rating = 0);";
		return SQL;		
	}
	
	public static String SQLselectPosNegRatingForUserFromRatingsMovies(String user_id) {
		String SQL = 	"SELECT movie_uri, rating FROM " + DBConstants.dbName + ".ratings_movies" +
						" WHERE `user_id` = " + user_id + " "
						+ "AND `last_change` = 'user' "
						+ "AND (ratings_movies.rating = 0 OR ratings_movies.rating = 1);";
		return SQL;		
	}	
	
	public static String SQLselectRatingsForUserFromRatingsMovies(String user_id) {
		String SQL =    "SELECT movie_uri, rating FROM " + DBConstants.dbName + ".ratings_movies" + 
	                    " WHERE `user_id` = " + user_id + " "
	                    + "AND `last_change` = 'user'";
		return SQL;
	}

//	public static String SQLselectTestSetForUserFromMovies(String user_id) {
//		String SQL = 	"SELECT DISTINCT uri FROM movies" +
//						" WHERE `title` IS NOT NULL AND `poster` != \"N/A\" AND `release_year` > 1970 OR `release_year` Is NULL AND `uri` <> all (" + 
//											"SELECT movie_uri FROM ratings_movies " +											
//											//" WHERE `user_id` = " + user_id + ");";
//											" WHERE `user_id` = " + user_id + ")LIMIT 1000;"; //Limit 50;
//		return SQL;
//	}
	
	
	public static String SQLselectAllGenresFromGenresMoviesByMovie(String movie_uri){
		String SQL = 	"SELECT genre_name FROM genres_movies" +
						" WHERE movie_uri = '"+ movie_uri +"';";
		
		return SQL;
	}
	
	//TODO - DONE la selezione è impostata su questa tabella in modo da aggiungere anche in film inseriti dall'utente e non presenti tra i trailer
	public static String SQLselectAllPropertyFromDbpediaMoviesSelection(String movie_uri){ //TODO
		String SQL = 	"SELECT entity_id as subject , property_type as predicate, property_id as object FROM property_object_entity_mappings" +
						" WHERE entity_id = ?;";
		
		return SQL;
	}
	
	//obsoleta
//	public static String SQLselectAllPropertyFromDbpediaMoviesSelection(){
//		String SQL = 	"SELECT subject,predicate,object FROM mapping_dbpedia_movies_trailer_selection;";
//		
//		return SQL;
//	}
	
	//seleziona il runtime_range_filter
	public static String SQLselectRuntimeRangeFilterFromUsers(String user_id) {
		String SQL = 	"SELECT runtime_range_filter "
						+ "FROM users "
						+ "WHERE `id` = " + user_id + ";";										
		return SQL;
	}
		
	//seleziona il release_year_filter
	public static String SQLselectReleaseYearFilterFromUsers(String user_id) {
		String SQL = 	"SELECT release_year_filter "
						+ "FROM users "
						+ "WHERE `id` = " + user_id + ";";										
		return SQL;
	}
	
	//TODO - DONE Seleziona tutti i film che hanno un trailer non valutati dall'utente
	public static String SQLselectTestSetForUserFromMovies(String user_id) { //TODO
		String SQL = 	"SELECT DISTINCT e.uri FROM entities e" +
						" WHERE e.uri <> all (" +
											"SELECT r.movie_uri FROM ratings_movies r" +	
											" WHERE r.user_id = ?);";										
		return SQL;
	}
	
	/*
	 	public static String SQLselectTestSetForUserFromMovies(String user_id) { //TODO
		String SQL = 	"SELECT DISTINCT e.uri FROM entities e" +
						" WHERE e.uri <> all (" +
											"SELECT r.movie_uri FROM ratings_movies r" +	
											" WHERE r.user_id = ?)"
					    +" AND EXISTS (" //TODO: Eliminare filtri hardcoded
				        + "    SELECT value FROM property_values_varchar "
				        + "    WHERE entity_id = e.uri "
				        + "    AND property_type = \"trailer\""
				        + "    AND value IS NOT NULL"
					    + ")"
					    + "AND EXISTS ("
					        + "SELECT value FROM property_values_varchar  "
					        + "WHERE entity_id = e.uri "
					        + "AND property_type = \"poster\" "
					        + "AND (value IS NOT NULL AND value <> \"N/A\") "
					    + ")";										
		return SQL;
	}
	 */
	
	//Select di tutti i film che hanno un trailer e uno specifico runtime_range_filter non valutati dall'utente
	public static String SQLselectTestSetForUserFromMoviesWithRuntimeRangeFilter(String user_id, String runtime_range_filter) {
		String SQL = 	"SELECT DISTINCT uri FROM movies"
						+ " WHERE trailer IS NOT NULL"
						+ " AND runtime_range = " + runtime_range_filter + ""
						+ " AND `uri` <> all (" +
											"SELECT movie_uri FROM ratings_movies" +	
											" WHERE `user_id` = " + user_id + ");";										
		return SQL;
	}
	
	//Select di tutti i film che hanno un trailer e uno specifico release_year_filter, non valutati dall'utente
	public static String SQLselectTestSetForUserFromMoviesWithReleaseYearFilter(String user_id, String release_year_filter) {
		String SQL = 	"SELECT DISTINCT uri FROM movies"
						+ " WHERE trailer IS NOT NULL"
						+ " AND reference_period = \"" + release_year_filter + "\""
						+ " AND `uri` <> all (" +
											"SELECT movie_uri FROM ratings_movies" +	
											" WHERE `user_id` = " + user_id + ");";										
		return SQL;
	}
	
	//Select di tutti i film che hanno un trailer e uno specifico release_year_filter e runtime_range_filter, non valutati dall'utente
	public static String SQLselectTestSetForUserFromMoviesWithAllFilter(String user_id, String release_year_filter, String runtime_range_filter) {
		String SQL = 	"SELECT DISTINCT uri FROM movies"
						+ " WHERE trailer IS NOT NULL "
						+ " AND reference_period = \"" + release_year_filter + "\""
						+ " AND runtime_range = " + runtime_range_filter + ""
						+ " AND `uri` <> all (" +
											"SELECT movie_uri FROM ratings_movies" +	
											" WHERE `user_id` = " + user_id + ");";										
		return SQL;
	}
	
	
	//vecchia query creazione grafo
	//Selezione film fatta sullo score, sulla data e sul poster
//	public static String SQLselectTestSetForUserFromMovies(String user_id) {
//		String SQL = 	"SELECT DISTINCT uri FROM movies" +
//						//" WHERE `poster` != \"N/A\" AND `uri` <> all (" + 
//						" WHERE `poster` != \"N/A\" AND `release_year` > 2010 AND `uri` <> all (" + 
//											"SELECT movie_uri FROM ratings_movies " +											
//											" WHERE `user_id` = " + user_id + ") " +
//											//" WHERE `user_id` = " + user_id + ");";
//											" ORDER BY score DESC LIMIT 2000;";
//											
//		return SQL;
//	}
	
//	public static String SQLselectTestSetAndPosRatingForUser(String user_id){
//	String SQL = 	"SELECT DISTINCT uri FROM movies" +
//					" WHERE `title` IS NOT NULL AND `release_year` > 1970 OR `release_year` Is NULL AND `uri` <> all (" + 
//								"SELECT movie_uri FROM ratings_movies " +									
//								//" WHERE `user_id` = " + user_id + " AND rating = 0);";
//								" WHERE `user_id` = " + user_id + " AND rating = 0)LIMIT 10000;"; //Limit 50;
//	return SQL;
//}
	
	public static String SQLselectNameFromUriInVertexPosterSelection(String uri){
		String SQL = "SELECT uri, name, score "
					+ "FROM " + DBConstants.dbName + ".vertices_poster_selection "
					+ "WHERE uri = \"" + uri + "\";";

		return SQL;
	}
	public static String SQLselectUriAndNameFromVertexPosterSelection(){
		String SQL = "SELECT uri, name "
					+ "FROM " + DBConstants.dbName + ".vertices_poster_selection "
					+ "WHERE name IS NOT NULL;";

		return SQL;
	}
	
	public static String SQLselectUriFromVertexTrailerSelection(){
		String SQL = "(SELECT uri AS uri, label AS name FROM entities) "
						+ "UNION "
						+ "(SELECT uri AS uri, label AS name FROM property_objects);";
		
		return SQL;
	}
	
	/*
	public static String SQLselectUriFromVertexTrailerSelection(){
		String SQL = "(SELECT uri, name FROM vertices_trailer_selection WHERE name IS NOT null) "
						+ "UNION "
						+ "(SELECT uri, title as name FROM movies WHERE title IS NOT null) "
						+ "UNION "
						+ "(SELECT uri, name FROM starring WHERE name IS NOT null);";
		
		return SQL;
	}*/
	
	public static String SQLsearchUriAndNameFromVertexTrailerSelectionByText(String text){
		String SQL = "SELECT * FROM "
						+ "(SELECT uri AS uri, label AS name FROM entities "
						+ "UNION "
						+ "SELECT uri AS uri, label AS name FROM property_objects ) "
						+ "AS u "
						+ "WHERE u.name LIKE \"%" + text + "%\" LIMIT 10;";
		
		return SQL;
	}
	
	/*
	public static String SQLsearchUriAndNameFromVertexTrailerSelectionByText(String text){
		String SQL = "SELECT * FROM "
						+ "(SELECT uri, name, score FROM vertices_trailer_selection WHERE name IS NOT null "
						+ "UNION "
						+ "SELECT uri, title as name, score FROM movies WHERE title IS NOT null AND poster <> \"N/A\" ) "
						+ "AS u "
						+ "WHERE u.name LIKE \"%" + text + "%\" ORDER BY score DESC LIMIT 10;";
		
		return SQL;
	}*/
	
	public static String SQLselectPropertyTypeFromPropertyValue(String propertyValue){
		String SQL = "SELECT distinct predicate "
				//+ "FROM " + DBConstants.dbName + ".mapping_dbpedia_movies_trailer_selection "
				+ "FROM " + DBConstants.dbName + ".mapping_dbpedia_movies_poster_selection "
				+ "WHERE object = \"" + propertyValue + "\";";
		
		return SQL;
	}
	public static String getPropertyTypes() {
		return "SELECT DISTINCT property_type "
				+ "FROM " + DBConstants.dbName + ".property_object_entity_mappings "
				+ "WHERE property_id = ?";
	}
	
	public static String SQLselectAllPropertiesFromMoviesBymovie(String movie_uri){
		String SQL = 	"SELECT "
						+ " m.release_year,"
						+ " dm.director_uri,"
						+ " pm.producer_uri,"
						+ " wm.writer_uri,"
						+ " sm.starring_uri,"
						+ " mcm.music_composer_uri,"
						+ " cm.cinematography_uri,"
						+ " em.editing_uri,"
						+ " dim.distributor_uri,"
						+ " bm.based_on_uri,"
						+ " cam.category_uri"
						+ " FROM movies m"
						+ " LEFT OUTER JOIN directors_movies dm ON m.uri = dm.movie_uri"
						+ " LEFT OUTER JOIN producers_movies pm ON m.uri = pm.movie_uri"
						+ " LEFT OUTER JOIN writers_movies wm ON m.uri = wm.movie_uri"
						+ " LEFT OUTER JOIN starring_movies sm ON m.uri = sm.movie_uri"
						+ " LEFT OUTER JOIN music_composers_movies mcm ON m.uri = mcm.movie_uri"
						+ " LEFT OUTER JOIN cinematographers_movies cm ON m.uri = cm.movie_uri"
						+ " LEFT OUTER JOIN editings_movies em ON m.uri = em.movie_uri"
						+ " LEFT OUTER JOIN distributors_movies dim ON m.uri = dim.movie_uri"
						+ " LEFT OUTER JOIN based_on_movies bm ON m.uri = bm.movie_uri"
						+ " LEFT OUTER JOIN categories_movies cam ON m.uri = cam.movie_uri"
						+ " WHERE m.uri = \"" + movie_uri + "\";";
		return SQL;
	}
	
	public static String getAddDialogStateQuery(String userID, String state) {
		return "INSERT INTO dialog_states (`user_id`, `state`)" +
		 " VALUES (?, ?)" +
		 " ON DUPLICATE KEY UPDATE `state` = VALUES(state);";
	}
	
	public static String getReadDialogStateQuery(String userID) {
		return "SELECT * FROM dialog_states WHERE `user_id` = \"" + userID +"\""; 
	}
	
	public static String getPropertiesForMovieQuery(String propertyTable) {
		return "SELECT * FROM " + propertyTable + " WHERE property_type = ? AND entity_id = ?";
	}
	
	public static String getInsertAnswerQuery() {
		return "INSERT INTO answers (user_id, question_id, answer_id) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE answer_id = VALUES(answer_id)";
	}
	
	public static String getEntityLabelQuery(String uri) {
		return "SELECT label FROM entities WHERE uri = ?;";
	}
	
	public static String getURILabelQuery(String label) {
		return "SELECT uri FROM entities WHERE label = ?;";
	}
	
	public static String getEntityMLLabelQuery(String uri) {
		return "SELECT label FROM entities WHERE uri LIKE '"+uri+"';";
	}
	
	public static String getURIMLQuery(String label) {
		return "SELECT uri FROM entities WHERE label LIKE \""+label+"%\" ORDER BY label ASC LIMIT 1";
	}
	
	public static String getPropertyLabelQuery() {
		return "SELECT label FROM property_objects where uri = ?;";
	}
	public static String getPropertyFromMovieQuery(String movie_uri) {
		return "SELECT property_id FROM property_object_entity_mappings where entity_id = '"+movie_uri+"' AND property_type = 'P57' LIMIT 1;";
	}
	
	public static String getInsertMessageInLogQuery() {
		return "INSERT INTO messages_log (message_id, user_id, message, timestamp_start, timestamp_end, intent, contexts, recognized, events, pagerank_cicle, number_recommendation_list, interaction_type, recommended_entity) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}

	public static String getSetLogMessageToCheckQuery() {
		return "UPDATE messages_log SET to_check = ? WHERE message_id = ? AND user_id = ?";
	}
	
	public static String getUpdateMessageLogQuery() {
		return "UPDATE messages_log SET timestamp_end = ? WHERE message_id = ? AND user_id = ?;";
	}
	
	public static String getUpdateMessageLogWithPRCycleQuery() {
		return "UPDATE messages_log SET timestamp_end = ?, pagerank_cicle = ?, number_recommendation_list = ? WHERE message_id = ? AND user_id = ?;";
	}
	
	public static String getUpdateMessageLogWithPRCycleAndEventsQuery() {
		return "UPDATE messages_log SET timestamp_end = ?, pagerank_cicle = ?, number_recommendation_list = ?, events = ? WHERE message_id = ? AND user_id = ?;";
	}
	
	public static String getLogMessagesQuery() {
		return "SELECT * FROM messages_log WHERE timestamp_start > ? ORDER BY timestamp_start ASC";
	}
	
	public static String getRecEntityRatingLogQuery() {
		return "SELECT * FROM ratings_rec_movies WHERE rated_at > ? ORDER BY rated_at ASC";
	}
	
	public static String getCurrentRecEntityRatingsQuery() {
		return "SELECT * FROM ratings_rec_movies WHERE user_id = ? AND number_recommendation_list = ? ORDER BY rated_at ASC";
	}
	
	public static String getRecEntityRatingsQuery() {
		return "SELECT * FROM ratings_rec_movies WHERE user_id = ? ORDER BY rated_at DESC";
	}
	
	public static String getPropertyRatingLogQuery() {
		return "SELECT * FROM ratings_properties_log WHERE rated_at > ? ORDER BY rated_at ASC";
	}
	
	public static String getEntityRatingLogQuery() {
		return "SELECT * FROM ratings_movies_log WHERE rated_at > ? ORDER BY rated_at ASC";
	}
	
	public static String getAnswersQuery() {
		return "SELECT * FROM answers";
	}
	
	public static String getEntityRatingsLog() {
		return "SELECT * FROM ratings_movies_log WHERE rated_at > ? ORDER BY rated_at ASC";
	}
	
	public static String getPropertyRatingsLog() {
		return "SELECT * FROM ratings_properties_log WHERE rated_at > ? ORDER BY rated_at ASC";
	}
	
	public static String getEventsFromMessageQuery() {
		return "SELECT events FROM messages_log WHERE message_id = ? AND user_id = ?";
	}

	public static String getEntityURIQuery(String label) {
		return "SELECT uri FROM entities where label LIKE \"%"+label+"%\" ORDER BY label ASC LIMIT 1;";
	}
	
	
	public static String getPropertyURIQuery() {
		return "SELECT uri FROM property_objects where label = ?";
	}

	public static String getPropertyValuesQuery() {
		return "SELECT label FROM property_objects " + 
				"WHERE uri IN (" + 
				"SELECT property_id " + 
				"FROM property_object_entity_mappings " + 
				"WHERE property_type = ?) " +
				"LIMIT 50;";
	}
	
	public static String getPropertyValuesSortedByPopularityQuery() {
		return "SELECT property_id, label, COUNT(property_id) AS count "
				+ "FROM property_object_entity_mappings JOIN property_objects ON property_id = uri "
				+ "WHERE property_type = ? "
				+ "GROUP BY property_id ORDER BY count DESC "
				+ "LIMIT 50";
	}
	
	public static String getMessageIDQuery() {
		return "SELECT message_id FROM " + DBConstants.dbName + ".messages_log WHERE message_id = ?";
	}
	
	public static String getLastTestMessageIDQuery() {
		return "SELECT message_id FROM " + DBConstants.dbName + ".messages_log WHERE message_id LIKE \"test_%\" ORDER BY message_id DESC LIMIT 1";
	}
	
	
//	public static String SQLselectTestSetForUserFromMovies(String user_id) {
//	String SQL = 	"SELECT DISTINCT uri FROM movies" +
//					" WHERE `uri` = 'http://dbpedia.org/resource/Barry_Lyndon' "
//					+ "OR `uri` = 'http://dbpedia.org/resource/The_Dark_Knight_(film)' "
//					+ "OR `uri` = 'http://dbpedia.org/resource/Jumper_(film)' "
//					+ "OR `uri` = 'http://dbpedia.org/resource/The_Dark_Knight_Rises' "
//					+ "OR `uri` = 'http://dbpedia.org/resource/Eyes_Wide_Shut' "
//					+ "OR `uri` = 'http://dbpedia.org/resource/Panic_Room' "
//					+ "OR `uri` = 'http://dbpedia.org/resource/The_Prestige_(film)' "
//					+ "OR `uri` = 'http://dbpedia.org/resource/Iron_Man_3' "
//					+ "OR `uri` = 'http://dbpedia.org/resource/Batman_Begins' "
//					+ "OR `uri` = 'http://dbpedia.org/resource/The_Incredible_Hulk_(film)' "
//					+ "AND `uri` <> all (" + 
//										"SELECT movie_uri FROM ratings_movies " +
//										" WHERE `user_id` = " + user_id + ")LIMIT 10;"; //Limit 50;
//										//" WHERE `user_id` = " + user_id + ");";
//	return SQL;
//}




	
}
