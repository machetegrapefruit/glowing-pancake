package test;

public class QuestionnaireAnswer {
	private String userID;
	private int questionID;
	private int answerID;
	public QuestionnaireAnswer(String userID, int questionID, int answerID) {
		super();
		this.userID = userID;
		this.questionID = questionID;
		this.answerID = answerID;
	}
	public String getUserID() {
		return userID;
	}
	public int getQuestionID() {
		return questionID;
	}
	public int getAnswerID() {
		return answerID;
	}
	
}
