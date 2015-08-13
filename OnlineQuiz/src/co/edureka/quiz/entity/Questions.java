package co.edureka.quiz.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@SuppressWarnings("serial")
@Table(name = "questions")
public class Questions implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "ques_id")
	private Long questionId;

	@Column(name = "lang_id")
	private Long languageId;

	@Column(name = "question")
	private String question;

	@Column(name = "right_option")
	private Long rightOption;

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	public Long getLanguageId() {
		return languageId;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getQuestion() {
		return question;
	}

	public void setRightOption(Long rightOption) {
		this.rightOption = rightOption;
	}

	public Long getRightOption() {
		return rightOption;
	}
}
