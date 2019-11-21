package controllers.helpers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import controllers.exceptionhandler.ApiError;
import controllers.exceptionhandler.ApiError.ApiErrorCode;
import controllers.exceptionhandler.ApiException;
import model2.Count;
import model2.Sector;
import model2.Transaction;

public class ValidationHelper {

	public static final String INVALID_SECTOR_FATHER_NAME = "Sector father name '%s' is not valid for given sector name '%s'.";
	public static final String INVALID_SECTOR_NAME = "Sector name '%s' cannot be NULL or EMPTY.";
	public static final String INVALID_SECTOR_DESCRIPTION = "Sector description '%s' cannot be NULL or EMPTY.";
	public static final String INVALID_SECTOR_DATES = "Sector activation date '%s' cannot be greater than sector deactivation date '%s'.";

	public static final String INVALID_COUNT_NAME = "Count name '%s' cannot be NULL or EMPTY.";
	public static final String INVALID_COUNT_DESCRIPTION = "Count description '%s' cannot be NULL or EMPTY.";
	public static final String INVALID_COUNT_DATES = "Count open date '%s' cannot be greater than sector close date '%s'.";

	public static final String INVALID_TRANSACTION_TITLE = "Transaction title '%s' cannot be NULL or EMPTY.";
	public static final String INVALID_TRANSACTION_DATE = "Transaction date '%s' cannot be NULL or EMPTY.";
	public static final String INVALID_TRANSACTION_COUNT = "Transaction count '%s' cannot be NULL or EMPTY.";
	public static final String INVALID_TRANSACTION_SECTOR = "Transaction sector '%s' cannot be NULL or EMPTY.";
	public static final String INVALID_TRANSACTION_ISCOMMON = "Transaction is_common '%s' cannot be NULL.";
	public static final String INVALID_TRANSACTION_AMOUNT = "Transaction amount '%s' cannot be NULL or 0.";

	public static final String INVALID_YEAR = "Year '%s' cannot be lesser than 2000 or greater than 3000.";

	public static void checkIfAnyErrorAndThrowApiException(List<String> errors) {
		if (errors.size() > 0) {
			String errorsToString = errors.stream().map(String::toString).collect(Collectors.joining(", "));
			throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, ApiErrorCode.INVALID_INPUT, errorsToString,
					"Invalid input data. " + errorsToString));
		}
	}

	public static List<String> validateYear(int year) {
		List<String> errors = new ArrayList<String>();

		if (year <= 2000 || year > 3000) {
			errors.add(String.format(INVALID_YEAR, year));
		}

		return errors;
	}

	public static List<String> validateSector(Sector sector) {
		List<String> errors = new ArrayList<String>();

		if (sector.getName() == null || sector.getName().equals("")) {
			errors.add(String.format(INVALID_SECTOR_NAME, sector.getName()));
		}

		if (sector.getDescription() == null || sector.getDescription().equals("")) {
			errors.add(String.format(INVALID_SECTOR_DESCRIPTION, sector.getDescription()));
		}

		if (sector.getActivationDate() != null && sector.getDeactivationDate() != null
				&& (sector.getActivationDate().compareTo(sector.getDeactivationDate()) == 1)) {
			errors.add(String.format(INVALID_SECTOR_DATES, sector.getActivationDate().toString(),
					sector.getDeactivationDate().toString()));
		}

		if (sector.getName().contains(".")) {
			if (sector.getFatherName() == null || (!sector.getName().startsWith(sector.getFatherName() + "."))) {
				errors.add(String.format(INVALID_SECTOR_FATHER_NAME, sector.getFatherName(), sector.getName()));
			}
		} else if (!sector.getName().equals(sector.getFatherName())) {
			errors.add(String.format(INVALID_SECTOR_FATHER_NAME, sector.getFatherName(), sector.getName()));
		}
		return errors;
	}

	public static List<String> validateCount(Count count) {
		List<String> errors = new ArrayList<String>();

		if (count.getName() == null || count.getName().equals("")) {
			errors.add(String.format(INVALID_COUNT_NAME, count.getName()));
		}

		if (count.getDescription() == null || count.getDescription().equals("")) {
			errors.add(String.format(INVALID_COUNT_DESCRIPTION, count.getDescription()));
		}

		if (count.getOpenDate() != null && count.getCloseDate() != null
				&& (count.getOpenDate().compareTo(count.getCloseDate()) == 1)) {
			errors.add(String.format(INVALID_COUNT_DATES, count.getOpenDate().toString(),
					count.getCloseDate().toString()));
		}

		return errors;
	}

	public static List<String> validateTransaction(Transaction transaction) {
		List<String> errors = new ArrayList<String>();

		if (transaction.getAmount() == null || transaction.getAmount().equals(BigDecimal.ZERO)) {
			errors.add(String.format(INVALID_TRANSACTION_AMOUNT, transaction.getAmount()));
		}

		if (transaction.getTitle() == null || transaction.getTitle().equals("")) {
			errors.add(String.format(INVALID_TRANSACTION_TITLE, transaction.getTitle()));
		}

		if (transaction.getCountName() == null || transaction.getCountName().equals("")) {
			errors.add(String.format(INVALID_TRANSACTION_COUNT, transaction.getCountName()));
		}

		if (transaction.getSectorName() == null || transaction.getSectorName().equals("")) {
			errors.add(String.format(INVALID_TRANSACTION_SECTOR, transaction.getSectorName()));
		}

		if (transaction.isCommon() == null) {
			errors.add(String.format(INVALID_TRANSACTION_ISCOMMON, transaction.isCommon()));
		}

		if (transaction.getDate() == null) {
			errors.add(String.format(INVALID_TRANSACTION_DATE, transaction.getDate().toString()));
		}

		return errors;
	}

	public static List<String> validateStagedTransaction(Transaction transaction) {
		List<String> errors = new ArrayList<String>();

		if (transaction.getAmount() == null || transaction.getAmount().equals(BigDecimal.ZERO)) {
			errors.add(String.format(INVALID_TRANSACTION_AMOUNT, transaction.getAmount()));
		}

		if (transaction.getTitle() == null || transaction.getTitle().equals("")) {
			errors.add(String.format(INVALID_TRANSACTION_TITLE, transaction.getTitle()));
		}

		if (transaction.getCountName() == null || transaction.getCountName().equals("")) {
			errors.add(String.format(INVALID_TRANSACTION_COUNT, transaction.getCountName()));
		}

		if (transaction.getDate() == null) {
			errors.add(String.format(INVALID_TRANSACTION_DATE, transaction.getDate().toString()));
		}

		return errors;
	}

}
