package co.edureka.quiz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edureka.quiz.dao.BaseDAO;

@Service("baseService")
public class BaseService {

	@Autowired
	BaseDAO baseDAO;

	public void home() {
		System.out.println("BaseService:home() starting.");
		baseDAO.home();
		System.out.println("BaseService:home() ending.");
	}

}
