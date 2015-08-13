package co.edureka.quiz.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import co.edureka.quiz.QuizQuestion;
import co.edureka.quiz.entity.Language;
import co.edureka.quiz.entity.QuestionOptions;
import co.edureka.quiz.entity.Questions;
import co.edureka.quiz.entity.User;

@Repository("baseDAO")
public class BaseDAO {

	@Autowired
	HibernateTemplate hibernateTemplate;

	public boolean registration(User user) {
		try {
			hibernateTemplate.persist(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public boolean checkLogin(String userName, String passWord) {
		try {
			Session ses = hibernateTemplate.getSessionFactory().openSession();
			Connection con = ses.connection();
			Statement st = con.createStatement();
			String sql = "Select * from  user where user_name='" + userName + "' and password='" + passWord + "' ";
			System.out.println(sql);
			ResultSet set = st.executeQuery(sql);
			int i = 0;
			while (set.next()) {
				i = 1;
			}
			if (i != 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public QuizQuestion getQuestions(int languageId, String languageType, int questionId) {

		QuizQuestion quizQuestion = new QuizQuestion();

		// fetching language record based on languageId/languageType
		DetachedCriteria ct1 = DetachedCriteria.forClass(Language.class);
		Criterion cond1 = Restrictions.eq("languageName", languageType);
		ct1.add(cond1);
		List<Language> languages = hibernateTemplate.findByCriteria(ct1);
		Language languageEntity = (Language) languages.get(0);

		// fetching question record based on languageId
		DetachedCriteria ct2 = DetachedCriteria.forClass(Questions.class);
		Criterion cond2 = Restrictions.eq("languageId", languageEntity.getLanguageId());
		ct2.add(cond2);
		List<Questions> quList = hibernateTemplate.findByCriteria(ct2);
		Questions question = quList.get(questionId);

		// fetching options records based on questionId
		DetachedCriteria ct3 = DetachedCriteria.forClass(QuestionOptions.class);
		Criterion cond3 = Restrictions.eq("questionId", Long.valueOf(questionId + 1));
		ct3.add(cond3);
		List<QuestionOptions> options = hibernateTemplate.findByCriteria(ct3);

		quizQuestion.setQuestionNumber(questionId);
		quizQuestion.setQuestion(question.getQuestion());

		String questionOptions[] = new String[4];
		int counter = 0;
		for (QuestionOptions option : options) {
			questionOptions[counter] = option.getAnsDescription();
			if (option.getQuesOpId() == question.getRightOption()) {
				quizQuestion.setCorrectOptionIndex(counter);
			}
			counter++;
		}
		quizQuestion.setQuestionOptions(questionOptions);

		return quizQuestion;
	}

}
