package questionnaire;

import entity.Pair;

public class Question {
	private int id;										//id ordinale della domanda
	private String label;								//Messaggio da visualizzare con la domanda
	private boolean showProfile;						//Se true, mostra il profilo utente con la domanda
	private Pair<Integer, String>[] possibleAnswers;	//id ordinale e testo della risposta
	
	public Question(int id, String label, Pair<Integer, String>[] possibleAnswers) {
		super();
		this.id = id;
		this.label = label;
		this.possibleAnswers = possibleAnswers;
	}
	public int getId() {
		return id;
	}
	public String getLabel() {
		return label;
	}
	public boolean isShowProfile() {
		return showProfile;
	}
	public Pair<Integer, String>[] getPossibleAnswers() {
		return possibleAnswers;
	}
	
}
