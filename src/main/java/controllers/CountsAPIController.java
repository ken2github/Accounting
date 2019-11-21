package controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import controllers.exceptionhandler.ApiError;
import controllers.exceptionhandler.ApiError.ApiErrorCode;
import controllers.helpers.DateHelper;
import controllers.helpers.ValidationHelper;
import controllers.exceptionhandler.ApiException;
import dao.CountDAO;
import dao.DAOException;
import dao.DAONotFoundException;
import model2.Count;
import model2.DetailedCount;

@RestController
@RequestMapping("/counts")
public class CountsAPIController {

	Logger logger = LoggerFactory.getLogger(CountsAPIController.class);

	@Autowired
	private CountDAO countDAO;

	@RequestMapping(method = RequestMethod.GET, value = "/")
	public List<DetailedCount> findAll() {
		List<DetailedCount> lds = new ArrayList<>();
		try {
			lds = countDAO.findAll();
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
		return lds;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/name={name}")
	public DetailedCount findByName(@PathVariable String name) {
		DetailedCount ds = null;
		try {
			ds = countDAO.findByName(name);
		} catch (DAONotFoundException e) {
			throw new ApiException(new ApiError().setErrorCode(ApiErrorCode.COUNT_NOT_FOUND)
					.setExternalMessage(e.getDBError().getMessage()).setHttpStatus(HttpStatus.NOT_FOUND));
		}
		return ds;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public DetailedCount findById(@PathVariable String id) {
		DetailedCount ds = null;
		try {
			ds = countDAO.findById(id);
			// } catch (DataAccessException dae) {
			// SQLException sqle = (SQLException) dae.getCause();
			// System.out.println("Error code : " + sqle.getErrorCode());
			// System.out.println("SQL state : " + sqle.getSQLState());
		} catch (DAONotFoundException e) {
			throw new ApiException(new ApiError().setErrorCode(ApiErrorCode.COUNT_NOT_FOUND)
					.setExternalMessage(e.getDBError().getMessage()).setHttpStatus(HttpStatus.NOT_FOUND));
		}
		return ds;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{id}")
	public DetailedCount createCount(@RequestBody Count count) {
		ValidationHelper.checkIfAnyErrorAndThrowApiException(ValidationHelper.validateCount(count));

		DetailedCount ds = null;
		try {
			ds = countDAO.insert(DateHelper.resetTimeToNoon_InCount(count));
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
		return ds;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public DetailedCount updateCount(@RequestBody DetailedCount detailedCount) {
		ValidationHelper.checkIfAnyErrorAndThrowApiException(ValidationHelper.validateCount(detailedCount));

		DetailedCount ds = null;
		try {
			ds = countDAO.update(DateHelper.resetTimeToNoon_InCount(detailedCount));
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
		return ds;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public void deleteCount(@PathVariable String id) {
		try {
			countDAO.deleteById(id);
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/name={name}")
	public void deleteByName(@PathVariable String name) {
		try {
			countDAO.deleteByName(name);
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
	}

}
