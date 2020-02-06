package controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import controllers.exceptionhandler.ApiError;
import controllers.exceptionhandler.ApiException;
import controllers.helpers.ValidationHelper;
import dao.AccountedYearDAO;
import dao.DAOException;

@RestController
@RequestMapping("/accountedYears")
public class DaoDBAccountedYearsAPIController {

	Logger logger = LoggerFactory.getLogger(DaoDBAccountedYearsAPIController.class);

	@Autowired
	private AccountedYearDAO accountedYearDAO;

	@RequestMapping(method = RequestMethod.GET, value = "/")
	public List<Integer> findAll() {
		List<Integer> lds = new ArrayList<>();
		try {
			lds = accountedYearDAO.findAll();
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
		return lds;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{year}")
	public void createAccountedYear(@RequestBody int year) {
		ValidationHelper.checkIfAnyErrorAndThrowApiException(ValidationHelper.validateYear(year));

		try {
			accountedYearDAO.insert(year);
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{year}")
	public void deleteAccountedYear(@PathVariable int year) {
		try {
			accountedYearDAO.delete(year);
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
	}

}
