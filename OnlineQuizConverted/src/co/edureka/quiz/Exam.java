package co.edureka.quiz;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import co.edureka.quiz.CreateDOM;

public class Exam {
	Document dom;
	public int currentQuestion = 0;
	public int totalNumberOfQuestions = 0;
	public int quizDuration = 0;

	public List<Integer> quizSelectionsList = new ArrayList<Integer>();

	public List<Integer> getQuizSelectionsList() {
		return quizSelectionsList;
	}

	public Map<Integer, Integer> selections = new LinkedHashMap<Integer, Integer>();

	public ArrayList<QuizQuestion> questionList;

	public Exam(String test, int totalNumberOfQuestions) throws SAXException, ParserConfigurationException,
			IOException, URISyntaxException {
		dom = CreateDOM.getDOM(test);
		for (int i = 0; i < totalNumberOfQuestions; i++) {
			selections.put(i, -1);
		}
		questionList = new ArrayList<QuizQuestion>(totalNumberOfQuestions);

	}

	public Document getDom() {
		return dom;
	}

	public void setQuestion(int i) {
		int number = i;
		String options[] = new String[4];
		String question = null;
		int correct = 0;

		NodeList qList = dom.getElementsByTagName("question");
		NodeList childList = qList.item(i).getChildNodes();

		int counter = 0;

		for (int j = 0; j < childList.getLength(); j++) {
			Node childNode = childList.item(j);
			if ("answer".equals(childNode.getNodeName())) {
				options[counter] = childList.item(j).getTextContent();
				counter++;
			} else if ("quizquestion".equals(childNode.getNodeName())) {
				question = childList.item(j).getTextContent();
			} else if ("correct".equals(childNode.getNodeName())) {
				correct = Integer.parseInt(childList.item(j).getTextContent());
			}

		}

		QuizQuestion q = new QuizQuestion();
		q.setQuestionNumber(number);
		q.setQuestion(question);
		q.setCorrectOptionIndex(correct);
		q.setQuestionOptions(options);

		questionList.add(number, q);

	}

	public ArrayList<QuizQuestion> getQuestionList() {
		return this.questionList;
	}

	public int getCurrentQuestion() {
		return currentQuestion;
	}

	public Map<Integer, Integer> getSelections() {
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
