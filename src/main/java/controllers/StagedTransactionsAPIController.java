package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import controllers.exceptionhandler.ApiException;
import controllers.helpers.DateHelper;
import controllers.helpers.ValidationHelper;
import dao.DAOException;
import dao.DAONotFoundException;
import dao.StagedTransactionDAO;
import model2.DetailedTransaction;
import model2.Transaction;

@RestController
@RequestMapping("/staged-transactions")
public class StagedTransactionsAPIController {

	Logger logger = LoggerFactory.getLogger(StagedTransactionsAPIController.class);

	@Autowired
	private StagedTransactionDAO stagedTransactionDAO;

	@RequestMapping(method = RequestMethod.GET, value = "/year={year}/month={month}/")
	public List<DetailedTransaction> findByYearMonth(@PathVariable int year, @PathVariable int month) {
		List<DetailedTransaction> lds = new ArrayList<>();
		try {
			lds = stagedTransactionDAO.findByYearMonth(year, month);
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
		return lds;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/")
	public List<DetailedTransaction> findAll() {
		List<DetailedTransaction> lds = new ArrayList<>();
		try {
			lds = stagedTransactionDAO.findAll();
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
		return lds;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public DetailedTransaction findById(@PathVariable String id) {
		DetailedTransaction ds = null;
		try {
			ds = stagedTransactionDAO.findById(id);
			// } catch (DataAccessException dae) {
			// SQLException sqle = (SQLException) dae.getCause();
			// System.out.println("Error code : " + sqle.getErrorCode());
			// System.out.println("SQL state : " + sqle.getSQLState());
		} catch (DAONotFoundException e) {
			throw new ApiException(new ApiError().setErrorCode(ApiErrorCode.TRANSACTION_NOT_FOUND)
					.setExternalMessage(e.getDBError().getMessage()).setHttpStatus(HttpStatus.NOT_FOUND));
		}
		return ds;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{id}")
	public DetailedTransaction createTransaction(@RequestBody Transaction transaction) {
		checkIfAnyErrorAndThrowApiException(ValidationHelper.validateStagedTransaction(transaction));

		DetailedTransaction ds = null;
		try {
			ds = stagedTransactionDAO.insert(DateHelper.resetTimeToNoon_InTransaction(transaction));
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
		return ds;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public DetailedTransaction updateTransaction(@RequestBody DetailedTransaction detailedTransaction) {
		checkIfAnyErrorAndThrowApiException(ValidationHelper.validateStagedTransaction(detailedTransaction));

		DetailedTransaction ds = null;
		try {
			ds = stagedTransactionDAO.update(DateHelper.resetTimeToNoon_InTransaction(detailedTransaction));
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
		return ds;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public void deleteTransaction(@PathVariable String id) {
		try {
			stagedTransactionDAO.deleteById(id);
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
	}

	private void checkIfAnyErrorAndThrowApiException(List<String> errors) {
		if (errors.size() > 0) {
			String errorsToString = errors.stream().map(String::toString).collect(Collectors.joining(", "));
			throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, ApiErrorCode.INVALID_INPUT, errorsToString,
					"Invalid input data. " + errorsToString));
		}
	}
}
