package co.edureka.quiz.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import co.edureka.quiz.Exam;
import co.edureka.quiz.QuizQuestion;
import co.edureka.quiz.dao.BaseDAO;
import co.edureka.quiz.service.BaseService;

import com.edureka.quiz.formbean.User;

@Controller
public class BaseController {
	@SuppressWarnings("unused")	
	private final Logger logger = LoggerFactory.getLogger(BaseController.class);

	@Autowired
	BaseService baseService;

	@Autowired
	private MessageSource msgSrc;

	@Autowired
	BaseDAO baseDAO;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView defaultHome(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("returning home page ..");
		ModelAndView model = new ModelAndView("home");
		return model;
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("returning home page ..");
		ModelAndView model = new ModelAndView("home");
		return model;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(HttpServletRequest req, HttpServletResponse res) {
		System.out.println("returning login page ..");
		ModelAndView model = new ModelAndView("login");
		return model;
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register(HttpServletRequest req, HttpServletResponse res) {
		System.out.println("returning registration/signup page ..");
		ModelAndView model = new ModelAndView("register");
		return model;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest req, HttpServletResponse res) {
		System.out.println("returning home page after logout ..");
		req.getSession().invalidate();
		ModelAndView model = new ModelAndView("home");
		return model;
	}

	@RequestMapping(value = "/checkLogin", method = RequestMethod.POST)
	public ModelAndView checkLogin(HttpServletRequest req, HttpServletResponse res) {

		System.out.println("checking login username/password ..");

		String username = req.getParameter("username");
		String password = req.getParameter("password");
		boolean flag = baseService.checkLogin(username, password);
		if (flag) {
			System.out.println("valid username/password. So, able to take exam ..");
			HttpSession session = req.getSession();
			session.setAttribute("user", username);
			ModelAndView model = new ModelAndView("home");
			return model;
		} else {
			System.out.println("Invalid username/password. So, Unable to take exam .. So switching to login page ..");
			req.setAttribute("errorMessage", "Invalid username or password");
			ModelAndView model = new ModelAndView("login");
			return model;
		}
	}

	@RequestMapping(value = "/takeExam", method = RequestMethod.GET)
	public ModelAndView takeExam(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("taking the exam ..");

		request.getSession().setAttribute("currentExam", null);
		request.getSession().setAttribute("totalNumberOfQuizQuestions", null);
		request.getSession().setAttribute("quizDuration", null);
		request.getSession().setAttribute("min", null);
		request.getSession().setAttribute("sec", null);

		String exam = request.getParameter("test");
		request.getSession().setAttribute("exam", exam);

		System.out.print(request.getSession().getAttribute("user") + " taking " + exam + " exam");
		// If user not yet login
		if (request.getSession().getAttribute("user") == null) {
			System.out.print(" , So switching to login page ..\n");
			ModelAndView model = new ModelAndView("login");
			return model;

		} else {
			try {
				String totalQuizQuestions = msgSrc.getMessage("quiz.totalQuizQuestions", null, null);
				String quizDuration = msgSrc.getMessage("quiz.quizDuration", null, null);

				request.getSession().setAttribute("totalNumberOfQuizQuestions", totalQuizQuestions);
				request.getSession().setAttribute("quizDuration", quizDuration);
				request.getSession().setAttribute("min", quizDuration);
				request.getSession().setAttribute("sec", 0);

			} catch (Exception e) {
				e.printStackTrace();
			}
			ModelAndView model = new ModelAndView("quizDetails");
			return model;
		}
	}

	@RequestMapping(value = "/exam", method = RequestMethod.GET)
	public ModelAndView examGet(HttpServletRequest request, HttpServletResponse response) {
		return examPost(request, response);
	}

	@RequestMapping(value = "/exam", method = RequestMethod.POST)
	public ModelAndView examPost(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("move next or previous during exam .. - ExamController");
		boolean finish = false;

		HttpSession session = request.getSession();
		try {
			if (session.getAttribute("currentExam") == null) {
				session = request.getSession();
				String selectedExam = (String) request.getSession().getAttribute("exam");

				Object ob = session.getAttribute("totalNumberOfQuizQuestions");

				String sob = (String) ob;
				Exam newExam = new Exam(selectedExam, Integer.parseInt(sob), baseDAO);
				session.setAttribute("currentExam", newExam);
				String sq = (String) request.getSession().getAttribute("totalNumberOfQuizQuestions");

				newExam.setTotalNumberOfQuestions(Integer.parseInt(sq));
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");
				Date date = new Date();
				String started = dateFormat.format(date);
				session.setAttribute("started", started);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Exam exam = (Exam) request.getSession().getAttribute("currentExam");

		if (exam.currentQuestion == 0) {
			exam.setQuestion(exam.currentQuestion);
			QuizQuestion q = exam.questionList.get(exam.currentQuestion);
			session.setAttribute("quest", q);
		}

		String action = request.getParameter("action");

		int minute = -1;
		int second = -1;
		if (request.getParameter("minute") != null) {
			minute = Integer.parseInt(request.getParameter("minute"));
			second = Integer.parseInt(request.getParameter("second"));
			request.getSession().setAttribute("min", request.getParameter("minute"));
			request.getSession().setAttribute("sec", request.getParameter("second"));
		}

		String radio = request.getParameter("answer");
		int selectedRadio = -1;
		exam.selections.put(exam.currentQuestion, selectedRadio);
		if ("1".equals(radio)) {
			selectedRadio = 1;
			exam.selections.put(exam.currentQuestion, selectedRadio);
			exam.questionList.get(exam.currentQuestion).setUserSelected(selectedRadio);
		} else if ("2".equals(radio)) {
			selectedRadio = 2;
			exam.selections.put(exam.currentQuestion, selectedRadio);
			exam.questionList.get(exam.currentQuestion).setUserSelected(selectedRadio);
		} else if ("3".equals(radio)) {
			selectedRadio = 3;
			exam.selections.put(exam.currentQuestion, selectedRadio);
			exam.questionList.get(exam.currentQuestion).setUserSelected(selectedRadio);
		} else if ("4".equals(radio)) {
			selectedRadio = 4;
			exam.selections.put(exam.currentQuestion, selectedRadio);
			exam.questionList.get(exam.currentQuestion).setUserSelected(selectedRadio);
		}

		if ("Next".equals(action)) {
			exam.currentQuestion++;
			exam.setQuestion(exam.currentQuestion);
			QuizQuestion q = exam.questionList.get(exam.currentQuestion);
			session.setAttribute("quest", q);
		} else if ("Previous".equals(action)) {
			System.out.println("You clicked Previous Button");
			exam.currentQuestion--;
			exam.setQuestion(exam.currentQuestion);
			QuizQuestion q = exam.questionList.get(exam.currentQuestion);
			session.setAttribute("quest", q);

		} else if ("Finish Exam".equals(action) || (minute == 0 && second == 0)) {
			finish = true;
			int result = exam.calculateResult(exam, exam.questionList.size());
			request.setAttribute("result", result);
			System.out.println("returning result page, If exam Done or time up ..");
			ModelAndView model = new ModelAndView("result");
			return model;
		}

		if (finish != true) {
			System.out.println("returning exam page, If exam not yet Done & still time not yet over ..");
			ModelAndView model = new ModelAndView("exam");
			return model;
		}

		ModelAndView model = new ModelAndView("home");
		return model;
	}

	@RequestMapping(value = "/exam/review", method = RequestMethod.GET)
	public ModelAndView review(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("returning review form for answers ..");

		Exam exam = (Exam) request.getSession().getAttribute("currentExam");

		request.setAttribute("totalQuestion", exam.getTotalNumberOfQuestions());

		ArrayList<QuizQuestion> reviewQuestionList = new ArrayList<QuizQuestion>();

		for (int questionId = 0; questionId < exam.getTotalNumberOfQuestions(); questionId++) {
			int number = questionId;

			QuizQuestion quizQuestion = baseDAO.getQuestions(1, "java", questionId);

			quizQuestion.setUserSelected(exam.getUserSelectionForQuestion(questionId));
			reviewQuestionList.add(number, quizQuestion);
		}
		request.setAttribute("reviewQuestions", reviewQuestionList);
		ModelAndView model = new ModelAndView("examReview2");
		return model;
	}

	@RequestMapping(value = "/checkRegister", method = RequestMethod.POST)
	public ModelAndView checkRegister(@ModelAttribute User user, HttpServletRequest request,
			HttpServletResponse response) {

		System.out.println("returning registration success page .. ");

		boolean flag = baseService.registration(user);
		if (flag) {
			System.out.println("Inserted the record in database successfully ..");
			request.setAttribute("newUser", user.getUserName());
			ModelAndView model = new ModelAndView("regSuccess");
			return model;

		} else {
			System.out.println("Error : While Inserting record in database");
			request.setAttribute("newUser", user.getUserName());
			ModelAndView model = new ModelAndView("regSuccess");
			return model;
		}
	}

}
