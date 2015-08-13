package co.edureka.quiz.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edureka.quiz.dao.BaseDAO;

import com.edureka.quiz.formbean.User;

@Service("baseService")
public class BaseService {

	@Autowired
	BaseDAO baseDAO;

	public boolean registration(User userBean) {
		co.edureka.quiz.entity.User userEntity = new co.edureka.quiz.entity.User();
		BeanUtils.copyProperties(userBean, userEntity);
		return baseDAO.registration(userEntity);
	}

	public boolean checkLogin(String userName, String passWord) {
		return baseDAO.checkLogin(userName, passWord);
	}

}
