package co.edureka.quiz;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import co.edureka.quiz.dao.BaseDAO;

@Component
public class Exam {
	Document dom;
	public int currentQuestion = 0;
	public int totalNumberOfQuestions = 0;
	public int quizDuration = 0;
	BaseDAO baseDAO;

	private List<Integer> quizSelectionsList = new ArrayList<Integer>();

	private List<Integer> getQuizSelectionsList() {
		return quizSelectionsList;
	}

	public Map<Integer, Integer> selections = new LinkedHashMap<Integer, Integer>();

	public ArrayList<QuizQuestion> questionList;

	public Exam(String test, int totalNumberOfQuestions, BaseDAO baseDAO) throws SAXException,
			ParserConfigurationException, IOException, URISyntaxException {
		for (int i = 0; i < totalNumberOfQuestions; i++) {
			selections.put(i, -1);
		}
		questionList = new ArrayList<QuizQuestion>(totalNumberOfQuestions);
		this.baseDAO = baseDAO;
	}

	public void setQuestion(int questionId) {
		int number = questionId;

		QuizQuestion quizQuestion = baseDAO.getQuestions(1, "java", questionId);

		questionList.add(number, quizQuestion);

	}

	private ArrayList<QuizQuestion> getQuestionList() {
		return this.questionList;
	}

	public int getCurrentQuestion() {
		return currentQuestion;
	}

	private Map<Integer, Integer> getSelections() {
		return this.selections;
	}

	public int calculateResult(Exam exam, int taken) {
		int totalCorrect = 0;
		Map<Integer, Integer> userSelectionsMap = exam.selections;
		List<Integer> userSelectionsList = new ArrayList<Integer>();
		for (Map.Entry<Integer, Integer> entry : userSelectionsMap.entrySet()) {
			userSelectionsList.add(entry.getValue());
		}

		quizSelectionsList = userSelectionsList;

		List<QuizQuestion> questionList = exam.questionList;

		List<Integer> correctAnswersList = new ArrayList<Integer>();
		for (QuizQuestion question : questionList) {
			correctAnswersList.add(question.getCorrectOptionIndex());
		}

		for (int i = 0; i < taken - 1; i++) {

			if ((userSelectionsList.get(i) - 1) == correctAnswersList.get(i)) {
				totalCorrect++;
			}
		}

		return totalCorrect;
	}

	public int getUserSelectionForQuestion(int i) {
		Map<Integer, Integer> map = getSelections();

		return (Integer) map.get(i);
	}

	public int getTotalNumberOfQuestions() {
		return totalNumberOfQuestions;
	}

	public void setTotalNumberOfQuestions(int i) {
		this.totalNumberOfQuestions = i;
	}

}
