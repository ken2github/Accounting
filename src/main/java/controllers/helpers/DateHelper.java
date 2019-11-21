package controllers.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.http.HttpStatus;

import controllers.exceptionhandler.ApiError;
import controllers.exceptionhandler.ApiError.ApiErrorCode;
import controllers.exceptionhandler.ApiException;
import model2.Count;
import model2.Sector;
import model2.Transaction;

public class DateHelper {

	private static final String PARSE_ERROR_MSG = "Parse error when resetting date time to 12:00. For date time '%s'. %s";

	public static Date resetTimeToNoon(Date date) {
		SimpleDateFormat sdf_from = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf_to = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			return sdf_to.parse(sdf_from.format(date) + " 12:00");
		} catch (ParseException e) {
			String errorsToString = String.format(PARSE_ERROR_MSG, date.toString(), e.getMessage());
			throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, ApiErrorCode.INVALID_INPUT, errorsToString,
					"Invalid input data. " + errorsToString));
		}
	}

	public static <T extends Count> T resetTimeToNoon_InCount(T count) {
		if (count.getCloseDate() != null) {
			count.setCloseDate(resetTimeToNoon(count.getCloseDate()));
		}
		if (count.getOpenDate() != null) {
			count.setOpenDate(resetTimeToNoon(count.getOpenDate()));
		}
		return count;
	}

	public static <T extends Sector> T resetTimeToNoon_InSector(T sector) {
		if (sector.getActivationDate() != null) {
			sector.setActivationDate(resetTimeToNoon(sector.getActivationDate()));
		}
		if (sector.getDeactivationDate() != null) {
			sector.setDeactivationDate(resetTimeToNoon(sector.getDeactivationDate()));
		}
		return sector;
	}

	public static <T extends Transaction> T resetTimeToNoon_InTransaction(T transaction) {
		if (transaction.getDate() != null) {
			transaction.setDate(resetTimeToNoon(transaction.getDate()));
		}
		return transaction;
	}

}
