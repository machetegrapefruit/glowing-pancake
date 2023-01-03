package metrics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.DBConstants;


public class MetricsAccessRecsysDB {
	private static Connection connect = null;
	private static Statement statement = null;
	private static Statement propertyStatement = null;
	private static ResultSet resultSet = null;
	private static ResultSet propertyResultSet = null;	

	private static String driver = "com.mysql.jdbc.Driver";
	private static String url    = DBConstants.dbURL + DBConstants.dbName;
	
	private static String username = "frencisdrame";
	private static String password = "recsys16";
	
	public MetricsAccessRecsysDB (){
		
	}
	
	// per chiudere i vari resultSet
	private void close() {
		try {
		   if (resultSet != null) {
		     resultSet.close();}
		   if (statement != null) {
		     statement.close();}
		   if (connect != null) {
		     connect.close();}			 
		   if (propertyResultSet != null) {
		     propertyResultSet.close();}
		   if (propertyStatement != null) {
		     propertyStatement.close();}
		} 
		catch (Exception e) {
			System.err.println("Database Exception - Close");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	//seleziona numero di domande prima e dopo la raccomandazione
	public Map<Integer, List<Integer>> timeNumberOfQuestions(int user_id, String botName)throws Exception {
		Map<Integer, List<Integer>> keyUserTimeMap = new HashMap<Integer, List<Integer>>();
		List<Integer> userTimeList = new ArrayList<Integer>();
		String SQL = MetricsQuertClass.SQLselectNumberOfQuestions(user_id, botName);		
		resultSet = null;
		try { 			
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     int key = 0;
		     int oldMessage_id = 0;
		     while(resultSet.next()) {
				int message_id = resultSet.getInt("message_id");
				int second_bot = resultSet.getInt("response_time");				
				 //se il messaggio è cambiato
				if ((oldMessage_id != message_id) ) {
//					System.out.println("oldMessage_id: " + oldMessage_id);
//					System.out.println("message_id: " + message_id);
//					System.out.println("second_bot: " + second_bot);					
				    if (second_bot > 0 && second_bot <= 300) {
						userTimeList.add(user_id);
						userTimeList.add(second_bot);
						keyUserTimeMap.put(key, userTimeList);
						userTimeList = new ArrayList<Integer>();
						key ++;
					}
				}
				oldMessage_id = message_id;
			}	
		}				
		
					
			
		catch (Exception e) {
			System.err.println("Database Exception - selectMessageDetailByUserAndReplyFunctionCall");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally {close();}
		
		//System.out.println("" +SQL);
//		System.out.print("user_id: " + user_id);
//		System.out.println(" - keyUserTimeMap: " + keyUserTimeMap);		
		return keyUserTimeMap;		
	}
	
	//prendi il tempo di domanda solo nella fase di training
	public Map<Integer, List<Integer>> timeNumberOfQuestionTraining(int user_id, String botName)throws Exception {
		Map<Integer, List<Integer>> keyUserTimeMap = new HashMap<Integer, List<Integer>>();
		List<Integer> userTimeList = new ArrayList<Integer>();
		String SQL = MetricsQuertClass.SQLselectNumberOfQuestions(user_id, botName);		
		resultSet = null;
		try { 			
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     int key = 0;
		     int second_bot = 0;
		     int countBefore = 0;
		     int countAfter = 0;
		     int oldMessage_id = 0;
		     boolean recMovieSelected = false;
		     boolean newSessionSelected = false;
		     while(resultSet.next()) {
				int message_id = resultSet.getInt("message_id");
				String context = resultSet.getString("context");
				second_bot  = resultSet.getInt("response_time");
				//se l'esperimento non è stato valutato
		    	if(newSessionSelected == false){

					//se i messaggi sono diversi e non è stata avviata la raccomandazione
					if ((oldMessage_id != message_id) && recMovieSelected == false ) {
						//controlla che non sia una richiesta di raccomandazione
						if (context.equalsIgnoreCase("recMovieSelected") || context.equalsIgnoreCase("recommendMoviesSelected") ) {
							recMovieSelected = true;
						}
						else {
							countBefore++;
						    if (second_bot > 0 && second_bot <= 300) {
								userTimeList.add(user_id);
								userTimeList.add(second_bot);
								keyUserTimeMap.put(key, userTimeList);
								userTimeList = new ArrayList<Integer>();
								key ++;
							}
						}
					}
					if ((oldMessage_id != message_id) && recMovieSelected == true ) {
						countAfter++;
					}
					if (context.equalsIgnoreCase("newSessionSelected") ) {
					    if (countBefore != 0 && countAfter != 0) {
						    if (second_bot > 0 && second_bot <= 300) {
								userTimeList.add(user_id);
								userTimeList.add(second_bot);
								keyUserTimeMap.put(key, userTimeList);
								userTimeList = new ArrayList<Integer>();
								key ++;
							}
						}
						userTimeList = new ArrayList<Integer>();
						countBefore = 0;
						countAfter = 0;
						oldMessage_id = 0;
						newSessionSelected = false;
						recMovieSelected = false;
					}					
					oldMessage_id = message_id;					
		    	}	
			}
		     //se il numero di domande prima è dopo è diverso da zero
		     if (countBefore != 0 && countAfter != 0) {
			    if (second_bot > 0 && second_bot <= 300) {
					userTimeList.add(user_id);
					userTimeList.add(second_bot);
					keyUserTimeMap.put(key, userTimeList);
					userTimeList = new ArrayList<Integer>();
					key ++;
				}
			}

		}
			
		catch (Exception e) {
			System.err.println("Database Exception - selectMessageDetailByUserAndReplyFunctionCall");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally {close();}		
		
//		System.out.println("" +SQL);
//		System.out.print("user_id: " + user_id);
//		System.out.println(" - keyUserTimeMap: " + keyUserTimeMap);		
		return keyUserTimeMap;		
	}
	
	//seleziona numero di domande prima e dopo la raccomandazione
	public Map<Integer, List<Integer>> selectNumberOfQuestions(int user_id, String botName)throws Exception {
		Map<Integer, List<Integer>> beforeKeyAfterValueMap = new HashMap<Integer, List<Integer>>();
		List<Integer> beforeAfterList = new ArrayList<Integer>();
		String SQL = MetricsQuertClass.SQLselectNumberOfQuestions(user_id, botName);		
		resultSet = null;
		try { 			
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     int key = 0;
		     int countBefore = 0;
		     int countAfter = 0;
		     int oldMessage_id = 0;
		     boolean recMovieSelected = false;
		     boolean newSessionSelected = false;
		     while(resultSet.next()) {
				int message_id = resultSet.getInt("message_id");
				String context = resultSet.getString("context");
		    	 
				//se l'esperimento non è stato valutato
		    	if(newSessionSelected == false){

					//se i messaggi sono diversi e non è stata avviata la raccomandazione
					if ((oldMessage_id != message_id) && recMovieSelected == false ) {
						//controlla che non sia una richiesta di raccomandazione
						if (context.equalsIgnoreCase("recMovieSelected") || context.equalsIgnoreCase("recommendMoviesSelected") ) {
							recMovieSelected = true;
						}
						else {
							countBefore++;
						}
					}
					if ((oldMessage_id != message_id) && recMovieSelected == true ) {
						countAfter++;
					}
					if (context.equalsIgnoreCase("newSessionSelected") ) {
					    if (countBefore != 0 && countAfter != 0) {
							beforeAfterList.add(user_id);
							beforeAfterList.add(countBefore);
							beforeAfterList.add(countAfter);
							beforeKeyAfterValueMap.put(key, beforeAfterList);
						}
						beforeAfterList = new ArrayList<Integer>();
						countBefore = 0;
						countAfter = 0;
						oldMessage_id = 0;
						newSessionSelected = false;
						key++;

					}					
					oldMessage_id = message_id;					
		    	}	
			}
		     //se il numero di domande prima è dopo è diverso da zero
		     if (countBefore != 0 && countAfter != 0) {
				beforeAfterList.add(user_id);
				beforeAfterList.add(countBefore);
				beforeAfterList.add(countAfter);
				beforeKeyAfterValueMap.put(key, beforeAfterList);
			}

		}
			
		catch (Exception e) {
			System.err.println("Database Exception - selectMessageDetailByUserAndReplyFunctionCall");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally {close();}
		
		//System.out.println("" +SQL);
//		System.out.print("user_id: " + user_id);
//		System.out.println(" - beforeKeyAfterValueMap: " + beforeKeyAfterValueMap);		
		return beforeKeyAfterValueMap;		
	}
	
	//tempo complessivo d'interazione
	public Map<Integer, List<Integer>> selectTotalInteractionTime(int user_id, String botName)throws Exception {
		Map<Integer, List<Integer>> totalInteractionTimeMap = new HashMap<Integer, List<Integer>>();
		List<Integer> userTimeList = new ArrayList<Integer>();
		String SQL = MetricsQuertClass.SQLselectNumberOfQuestions(user_id, botName);		
		resultSet = null;
		try { 			
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     int key = 0;
		     int interaction_time = 0;
		     int oldMessage_id = 0;
		     boolean newSessionSelected = false;
		     while(resultSet.next()) {
				int message_id = resultSet.getInt("message_id");
				String context = resultSet.getString("context");
				int second_bot = resultSet.getInt("response_time");	
		    	 
				//se l'esperimento non è stato valutato
		    	if(newSessionSelected == false){

					//se i messaggi sono diversi
					if ((oldMessage_id != message_id)) {
						//System.out.println("\noldMessage_id: " + oldMessage_id);
						//System.out.println("message_id: " + message_id);
						//System.out.println("second_bot: " + second_bot);					
					    if (second_bot > 0 && second_bot <= 300) {
					    	interaction_time = interaction_time + second_bot;
					    	//System.out.println("interaction_time: " + interaction_time);
						}
					}
					if (context.equalsIgnoreCase("newSessionSelected") ) {
						userTimeList.add(user_id);
						userTimeList.add(interaction_time);
						totalInteractionTimeMap.put(key, userTimeList);
						userTimeList = new ArrayList<Integer>();
						newSessionSelected = false;
						interaction_time = 0;
						oldMessage_id = 0;
						key ++;
					}
				}					
				oldMessage_id = message_id;					
	    	}	
		   //se è stata sospesta l'interazione
		    if (interaction_time > 0) {
				userTimeList.add(user_id);
				userTimeList.add(interaction_time);
				totalInteractionTimeMap.put(key, userTimeList);
			}

			

		}
			
		catch (Exception e) {
			System.err.println("Database Exception - selectMessageDetailByUserAndReplyFunctionCall");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally {close();}
		
		//System.out.println("" +SQL);
		//System.out.print("user_id: " + user_id);
		//System.out.println(" - totalInteractionTimeMap: " + totalInteractionTimeMap);		
		return totalInteractionTimeMap;		
	}
	
	public Map<Integer, List<Integer>> selectNumberOfLikeBefore(int user_id, String botName)throws Exception {
		Map<Integer, List<Integer>> totalNumberOfLikeBefore = new HashMap<Integer, List<Integer>>();
		List<Integer> userRateList = new ArrayList<Integer>();
		String SQL = MetricsQuertClass.SQLselectCountAllRecMovieList(user_id, botName);		
		resultSet = null;
		try { 			
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     int key = 0;
		     int number_recommendation_list = 0;
		     int countLike = 0;
		     int oldBeforeList = 0;
		     int nRefineList = 0;
		     int nRefocusList = 0;
		     boolean beforeList = false;
		     boolean refineList = false;
		     boolean refocusList = false;
		     while(resultSet.next()) {
				number_recommendation_list = resultSet.getInt("number_recommendation_list");
				int like = resultSet.getInt("like");
				String refine = resultSet.getString("refine");
				String refocus = resultSet.getString("refocus");
		    	 
				//cerco una lista refine e refocus, che saranno le successive a quella su cui sono state attivate
		    	if(refine != null && refine.equalsIgnoreCase("refine")){
		    		nRefineList = number_recommendation_list + 1;
		    		refineList = true;
		    	}
		    	else if(refocus != null && refocus.equalsIgnoreCase("refocus")){
					nRefocusList = number_recommendation_list + 1;
					refocusList = true;
				}
		    	else{
		    		beforeList = true;
		    	}
		    	
		    	//se non è ne una lista di refine o di refocus
		    	if ((number_recommendation_list != nRefineList) && (number_recommendation_list != nRefocusList) && ( (number_recommendation_list == oldBeforeList) || (oldBeforeList == 0) )) {

					if (like == 1 ) {
						countLike ++;
						userRateList.add(user_id);
						userRateList.add(number_recommendation_list);
						userRateList.add(countLike);
						totalNumberOfLikeBefore.put(key, userRateList);
						userRateList = new ArrayList<Integer>();
						
					}
					System.out.println("\nnumber_recommendation_list: " + number_recommendation_list);
					System.out.println("oldBeforeList: " + oldBeforeList);	
					System.out.println("like: " + like);	
					System.out.println("countLikeDislike: " + countLike);	
					
				}
				//se è cambiata la lista puoi salvare i dati
				else if (number_recommendation_list != oldBeforeList) {	
//					userRateList.add(user_id);
//					userRateList.add(oldBeforeList);
//					userRateList.add(countLike);
//					totalNumberOfLikeBefore.put(key, userRateList);
					oldBeforeList = number_recommendation_list;
					userRateList = new ArrayList<Integer>();
					beforeList = false;
					countLike = 0;
					key ++;
				}
			
		    
		    }
		     
		    //se non ci sono altre righe ma è presente una valutazione
//		    if ( beforeList = true ){
//				userRateList.add(user_id);
//				userRateList.add(oldBeforeList);
//				userRateList.add(countLike);
//				totalNumberOfLikeBefore.put(key, userRateList);
//		   }
		     
		}
			
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberOfLikeBefore");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally {close();}
		
		//System.out.println("" +SQL);
		System.out.print("user_id: " + user_id);
		System.out.println(" - totalNumberOfLikeBefore: " + totalNumberOfLikeBefore);		
		return totalNumberOfLikeBefore;		
	}
	
	
	
	//numero di like post refocus
	public Map<Integer, List<Integer>> selectNumberOfLikePostRefocus(int user_id, String botName)throws Exception {
		Map<Integer, List<Integer>> totalNumberOfLikePostRefocus = new HashMap<Integer, List<Integer>>();
		List<Integer> userRateList = new ArrayList<Integer>();
		String SQL = MetricsQuertClass.SQLselectCountAllRecMovieList(user_id, botName);		
		resultSet = null;
		try { 			
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     int key = 0;
		     int number_recommendation_list = 0;
		     int countLike = 0;
		     int nRefocusList = 0;
		     boolean refocusList = false;
		     while(resultSet.next()) {
				number_recommendation_list = resultSet.getInt("number_recommendation_list");
				int like = resultSet.getInt("like");
				int dislike = resultSet.getInt("dislike");
				String refocus = resultSet.getString("refocus");
		    	 
				//cerco una lista refocus, che sarà la successiva a quella su cui è stata attivata
				//se sulla lista attuale è stato attivato un refocus sulla successiva sarò sul refocus
		    	if(refocus != null && refocus.equalsIgnoreCase("refocus")){
		    		nRefocusList = number_recommendation_list + 1;
		    		refocusList = true;
		    	}
		    	
		    	//se siamo nella lista di refocus controllo che ho ricevuto almeno un like, li conto in modo da dire il totale
				if ((number_recommendation_list == nRefocusList) && refocusList == true) {

					if (like == 1 ) {
						countLike ++;
						userRateList.add(user_id);
						userRateList.add(nRefocusList);
						userRateList.add(countLike);
						totalNumberOfLikePostRefocus.put(key, userRateList);
						userRateList = new ArrayList<Integer>();

					}
//					System.out.println("RefocusList: " + nRefocusList);
//					System.out.println("refocusList: " + refocusList);
//					System.out.println("refocus: " + refocus);	
//					System.out.println("like: " + like);	
//					System.out.println("countLikeDislike: " + countLike);	
				}		
				//se è cambiata la lista puoi salvare i dati
				else if((number_recommendation_list > nRefocusList) && refocusList == true){				
					userRateList.add(user_id);
					userRateList.add(nRefocusList);
					userRateList.add(countLike);
					totalNumberOfLikePostRefocus.put(key, userRateList);
					userRateList = new ArrayList<Integer>();
					refocusList = false;
					countLike = 0;
					key ++;
				}
		    }
		     
		    //se non ci sono altre righe ma è presente una valutazione
		    if (refocusList == true){
				userRateList.add(user_id);
				userRateList.add(nRefocusList);
				userRateList.add(countLike);
				totalNumberOfLikePostRefocus.put(key, userRateList);
		   }
		     
		}
			
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberOfLikePostRefocus");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally {close();}
		
		//System.out.println("" +SQL);
		System.out.print("user_id: " + user_id);
		System.out.println(" - totalNumberOfLikePostRefocus: " + totalNumberOfLikePostRefocus);		
		return totalNumberOfLikePostRefocus;		
	}
	
	//numero di like post refine
		public Map<Integer, List<Integer>> selectNumberOfLikePostRefine(int user_id, String botName)throws Exception {
			Map<Integer, List<Integer>> totalNumberOfLikePostRefine = new HashMap<Integer, List<Integer>>();
			List<Integer> userRateList = new ArrayList<Integer>();
			String SQL = MetricsQuertClass.SQLselectCountAllRecMovieList(user_id, botName);		
			resultSet = null;
			try { 			
			     Class.forName(driver); 		
				 connect = DriverManager.getConnection(url,username,password); 	
				 statement = connect.createStatement();
			     resultSet = statement.executeQuery(SQL);
			     int key = 0;
			     int number_recommendation_list = 0;
			     int countLike = 0;
			     int nRefineList = 0;
			     boolean refineList = false;
			     while(resultSet.next()) {
					number_recommendation_list = resultSet.getInt("number_recommendation_list");
					int like = resultSet.getInt("like");
					String refine = resultSet.getString("refine");
			    	 
					//cerco una lista refine, che sarà la successiva a quella su cui è stata attivata
					//se sulla lista attuale è stato attivato un refine sulla successiva sarò sul refine
			    	if(refine != null && refine.equalsIgnoreCase("refine")){
			    		nRefineList = number_recommendation_list + 1;
			    		refineList = true;
			    	}
			    	
			    	//se siamo nella lista di refine controllo che ho ricevuto almeno un like, li conto in modo da dire il totale
					if ((number_recommendation_list == nRefineList) && refineList == true) {

						if (like == 1 ) {
							countLike ++;
							userRateList.add(user_id);
							userRateList.add(nRefineList);
							userRateList.add(countLike);
							totalNumberOfLikePostRefine.put(key, userRateList);
							userRateList = new ArrayList<Integer>();

						}
//						System.out.println("RefocusList: " + nRefocusList);
//						System.out.println("refocusList: " + refocusList);
//						System.out.println("refocus: " + refocus);	
//						System.out.println("like: " + like);	
//						System.out.println("countLikeDislike: " + countLike);	
					}		
					//se è cambiata la lista puoi salvare i dati
					else if((number_recommendation_list > nRefineList) && refineList == true){				
						userRateList.add(user_id);
						userRateList.add(nRefineList);
						userRateList.add(countLike);
						totalNumberOfLikePostRefine.put(key, userRateList);
						userRateList = new ArrayList<Integer>();
						refineList = false;
						countLike = 0;
						key ++;
					}
			    }
			     
			    //se non ci sono altre righe ma è presente una valutazione
			    if (refineList == true){
					userRateList.add(user_id);
					userRateList.add(nRefineList);
					userRateList.add(countLike);
					totalNumberOfLikePostRefine.put(key, userRateList);
			   }
			     
			}
				
			catch (Exception e) {
				System.err.println("Database Exception - selectNumberOfLikePostRefine");
				System.err.println("" +SQL);
				System.err.println(e.getMessage());
				e.printStackTrace();
			} 	
			finally {close();}
			
			//System.out.println("" +SQL);
			System.out.print("user_id: " + user_id);
			System.out.println(" - totalNumberOfLikePostRefine: " + totalNumberOfLikePostRefine);		
			return totalNumberOfLikePostRefine;		
		}
	
	
	public List<Integer> selectAllUsers() throws Exception {
		List<Integer> usersList = new ArrayList<Integer>();
		String SQL = MetricsQuertClass.SQLselectAllUsers();
		resultSet = null;
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()) {
		    	 Integer user_id = resultSet.getInt("id");
		    	 if (user_id != null) {
		    		 usersList.add(user_id);
		    	 }		    	 
		     }		     
		}
		catch (Exception e) {
			System.err.println("Database Exception - selectAllUsers");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		finally { close(); }		
		//System.out.println("selectAllUsers: " + usersList.toString());
		return usersList;
	}
	
	public Map<Integer, List<Integer>> selectNumberRecommendationListFromUserAndBotName(int user_id, String botName) throws Exception{
		List<Integer> userListAndType = new ArrayList<Integer>();
		Map<Integer, List<Integer>> userListAndTypeMap = new HashMap<Integer, List<Integer>>();
		
		String SQL = MetricsQuertClass.SQLselectNumberRecommendationListFromUser(user_id, botName);
		resultSet = null;
		int type = 0; //lista normale
		int key = 0;
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()){		
				int number_recommendation_list = resultSet.getInt("number_recommendation_list");
				
				if (number_recommendation_list != 0) {
					userListAndType.add(user_id);
					userListAndType.add(number_recommendation_list);
					userListAndType.add(type);
				}
				
			     if (! userListAndType.isEmpty()) {
			    	 userListAndTypeMap.put(key, userListAndType);
			    	 userListAndType = new ArrayList<Integer>();
			    	 key++;
			     }
		     }
			
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberRecommendationListFromUserAndBotName");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(); }
		
		//System.out.println("" +SQL);
		//System.out.println("selectNumberRecommendationListFromUserAndBotName: " + userListAndTypeMap);
		return userListAndTypeMap;
	}
	
	public Map<Integer, List<Integer>> selectTypeOfNumberRecommendationListFromUserAndBotName(int user_id, String botName, Map<Integer, List<Integer>> userListAndTypeMap) throws Exception{
		List<Integer> userListAndType = new ArrayList<Integer>();
		String SQL = MetricsQuertClass.SQLselectCountAllRecMovieList(user_id, botName);		
		resultSet = null;
		try { 			
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     int key = 0;
		     int number_recommendation_list = 0;
		     int nRefocusList = 0;
		     int nRefineList = 0;
		     while(resultSet.next()) {
				number_recommendation_list = resultSet.getInt("number_recommendation_list");
				String refine = resultSet.getString("refine");
				String refocus = resultSet.getString("refocus");
				//cerco una lista refine, che sarà la successiva a quella su cui è stata attivata
				//se sulla lista attuale è stato attivato un refine sulla successiva sarò sul refine
		    	if(refine != null && refine.equalsIgnoreCase("refine")){
		    		nRefineList = number_recommendation_list + 1;
		    		userListAndType.add(user_id);
					userListAndType.add(nRefineList);
					userListAndType.add(1);
					
					List<Integer> usersList = new ArrayList<Integer>();
					for (Integer k : userListAndTypeMap.keySet()) {
						usersList = userListAndTypeMap.get(k);
						int list = usersList.get(1);
						if (list == nRefineList) {
							userListAndTypeMap.put(k, userListAndType);
						}
						k++;
					}
					userListAndType = new ArrayList<Integer>();
		    	}
		    	
				//cerco una lista refocus, che sarà la successiva a quella su cui è stata attivata
				//se sulla lista attuale è stato attivato un refocus sulla successiva sarò sul refocus
		    	else if(refocus != null && refocus.equalsIgnoreCase("refocus")){
		    		nRefocusList = number_recommendation_list + 1;
		    		userListAndType.add(user_id);
		    		userListAndType.add(nRefocusList);
					userListAndType.add(2);
					
					List<Integer> usersList = new ArrayList<Integer>();
					for (Integer k : userListAndTypeMap.keySet()) {
						usersList = userListAndTypeMap.get(k);
						int list = usersList.get(1);
						if (list == nRefocusList) {
							userListAndTypeMap.put(k, userListAndType);
						}
						k++;
					}
					userListAndType = new ArrayList<Integer>();
		    	}
		    	
		    	
		    }		     
		     
		}
			
		catch (Exception e) {
			System.err.println("Database Exception - selectNumberOfLikePostRefine");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally {close();}
		
		//System.out.println("" +SQL);
		//System.out.print("user_id: " + user_id);
		//System.out.println(" - userListAndTypeMap: " + userListAndTypeMap);		
		return userListAndTypeMap;		
	}
	
	public Map<Integer, List<Integer>> selectLikeAndDislikeFromRecommendationListByUserBotNameAndList(int user_id, String botName, int number_recommendation_list) throws Exception{
		List<Integer> userListAndType = new ArrayList<Integer>();
		Map<Integer, List<Integer>> userListAndTypeMap = new HashMap<Integer, List<Integer>>();
		
		String SQL = MetricsQuertClass.SQLselectLikeAndDislikeFromRecommendationListByUserBotNameAndList(user_id, botName, number_recommendation_list);
		resultSet = null;
		int key = 0;
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()){
				int like = resultSet.getInt("like");
				int dislike = resultSet.getInt("dislike");
				int position = resultSet.getInt("position");
				int responseTime = resultSet.getInt("response_time");

				userListAndType.add(user_id);
				userListAndType.add(number_recommendation_list);
				userListAndType.add(like);
				userListAndType.add(dislike);
				userListAndType.add(position);
				userListAndType.add(responseTime);
				
			     if (! userListAndType.isEmpty()) {
			    	 userListAndTypeMap.put(key, userListAndType);
			    	 userListAndType = new ArrayList<Integer>();
			    	 key++;
			     }
		     }
			
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectLikeAndDislikeFromRecommendationListByUserBotNameAndList");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(); }
		
		//System.out.println("" +SQL);
		//System.out.println("selectLikeAndDislikeFromRecommendationListByUserBotNameAndList: " + userListAndTypeMap);
		return userListAndTypeMap;
	}
	
	public Map<Integer, List<Integer>> selectListByUserBotNameFromRecommendationList(int user_id, String botName, int number_recommendation_list) throws Exception{
		List<Integer> userListAndType = new ArrayList<Integer>();
		Map<Integer, List<Integer>> userListAndTypeMap = new HashMap<Integer, List<Integer>>();
		
		String SQL = MetricsQuertClass.SQLselectUserBotNameAndListFromRecommendationList(user_id, botName, number_recommendation_list);
		resultSet = null;
		int key = 0;
		try { 
		     Class.forName(driver); 		
			 connect = DriverManager.getConnection(url,username,password); 	
			 statement = connect.createStatement();
		     resultSet = statement.executeQuery(SQL);
		     while (resultSet.next()){
				int like = resultSet.getInt("like");
				int dislike = resultSet.getInt("dislike");
				int position = resultSet.getInt("position");
				int responseTime = resultSet.getInt("response_time");

				userListAndType.add(user_id);
				userListAndType.add(number_recommendation_list);
				userListAndType.add(like);
				userListAndType.add(dislike);
				userListAndType.add(position);
				userListAndType.add(responseTime);
				
			     if (! userListAndType.isEmpty()) {
			    	 userListAndTypeMap.put(key, userListAndType);
			    	 userListAndType = new ArrayList<Integer>();
			    	 key++;
			     }
		     }
			
		} 
		catch (Exception e) {
			System.err.println("Database Exception - selectLikeAndDislikeFromRecommendationListByUserBotNameAndList");
			System.err.println("" +SQL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 	
		finally { close(); }
		
		System.out.println("" +SQL);
		System.out.println("selectLikeAndDislikeFromRecommendationListByUserBotNameAndList: " + userListAndTypeMap);
		return userListAndTypeMap;
	}
	
	

	public static void main(String[] args) throws Exception {
		MetricsAccessRecsysDB accessDB = new MetricsAccessRecsysDB();
		Map<Integer, List<Integer>> userListLikeMap = new HashMap<Integer, List<Integer>>();
		List<Integer> userLikeDislikePositionList = new ArrayList<Integer>();
		List<Double> userListAvPrecisionList = new ArrayList<Double>();
		//accessDB.selectNumberOfLikeBefore(129877748, "conf1testrecsysbot");
		//accessDB.selectNumberOfLikePostRefine(129877748, "conf1testrecsysbot");
		//accessDB.timeNumberOfQuestionTraining(154771597, "conf2testrecsysbot");
		//accessDB.selectNumberOfLikePostRefocus(129877748, "conf1testrecsysbot");
		userListLikeMap = accessDB.selectNumberRecommendationListFromUserAndBotName(242355054, "conf1testrecsysbot");
		userListLikeMap = accessDB.selectTypeOfNumberRecommendationListFromUserAndBotName(242355054, "conf1testrecsysbot", userListLikeMap);
		accessDB.selectLikeAndDislikeFromRecommendationListByUserBotNameAndList(242355054,"conf1testrecsysbot", 1);
		
		
		//userListLikeMap = accessDB.selectLikeAndDislikeFromRecommendationListByUserBotNameAndList(129877748, "conf1testrecsysbot", 37);
		
	}

}
