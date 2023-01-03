package functions;

import configuration.Configuration;
import entity.Pair;
import graph.AdaptiveSelectionController;
import questionnaire.Question;

public class QuestionnaireService {
	/**
	 * Inserisce una risposta alla domanda specificata per l'utente
	 * @param userId
	 * @param questionId
	 * @param answerId
	 */
	public void insertAnswer(String userId, int questionId, int answerId) {
		new AdaptiveSelectionController().insertAnswer(userId, questionId, answerId);
	}
	
	/**
	 * Restituisce la domanda con indice questionId
	 * @param questionId
	 * @return
	 */
	public Question getQuestion(int questionId) {
		return Configuration.getDefaultConfiguration().getQuestionnaire()[questionId];
	}

	/**
	 * Cerca e restituisce l'ID della risposta appartenente alla domanda specificata e con il testo specificato.
	 * @param currentQuestionIndex L'indice della domanda contenente la risposta da cercare.
	 * @param text Il testo della risposta.
	 * @return L'ID della risposta, o -1 se non trova un ID valido.
	 */
	public int findAnswerId(int currentQuestionIndex, String text) {
		int id = -1;
		Question question = getQuestion(currentQuestionIndex);
		for (Pair<Integer, String> answer : question.getPossibleAnswers()) {
			if (text.equals(answer.value)) {
				id = answer.key;
			}
		}
		return id;
	}
	
	/**
	 * Restituisce il numero totale di domande del questionario.
	 * @return
	 */
	public int getQuestionsCount() {
		Question[] questionnaire = Configuration.getDefaultConfiguration().getQuestionnaire();
		if (questionnaire == null) {
			return 0;
		} else {
			return questionnaire.length;
		}
	}
}
