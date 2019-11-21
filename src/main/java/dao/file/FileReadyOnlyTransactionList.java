package dao.file;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import controllers.exceptionhandler.ApiError;
import controllers.exceptionhandler.ApiError.ApiErrorCode;
import controllers.exceptionhandler.ApiException;
import dao.DAOException;
import dao.DBError;
import dao.DBError.DBErrorCode;
import dao.ReadOnlyTransactionList;
import model2.Transaction;
import store.io.FormatMapper;
import store.io.FormatReader;
import store.io.UnmappedFormatReaderException;
import store.io.readers.NotValidMonthException;

public class FileReadyOnlyTransactionList implements ReadOnlyTransactionList {

	private List<Transaction> transactions = new ArrayList<>();
	private String nominalCount = "";
	private List<Date> nominalMonths = new ArrayList<>();

	private enum NORMALIZED_FIELD_POSITION_OF {
		DATE, AMOUNT, EMPTY_AMOUNT, TITLE, SECTOR, IS_COMMON
	}

	public FileReadyOnlyTransactionList(InputStream io, String nominalCount, List<Date> nominalMonths,
			String fileName) {
		super();
		this.nominalCount = nominalCount;
		this.nominalMonths = nominalMonths;

		try {
			loadTransactionList(io, nominalCount, nominalMonths, fileName);
		} catch (UnmappedFormatReaderException e) {
			throw new DAOException(new DBError(DBErrorCode.ILLEGAL_UPDATE,
					String.format("The count '%s' is not mapped on any FormatReader", nominalCount)));
		} catch (NotValidMonthException e) {
			throw new DAOException(new DBError(DBErrorCode.ILLEGAL_UPDATE,
					String.format("The nominal dates are not valid for file '%s'.", fileName)));
		} catch (Exception e) {
			throw new DAOException(new DBError(DBErrorCode.ILLEGAL_STATUS, String.format(
					"Got unexpected exception when reading from InputStream. Exception : '%s'. Stacktrace : '%s'", e,
					Arrays.asList(e.getStackTrace()).stream().map(StackTraceElement::toString)
							.collect(Collectors.joining(", ")))));
		}
	}

	private void loadTransactionList(InputStream io, String nominalCount, List<Date> nominalMonths, String fileName)
			throws Exception {
		FormatReader reader = FormatMapper.getFormatReader(nominalCount);
		reader.loadInputStream(io, fileName);
		for (Date nominalDate : nominalMonths) {
			Vector<String[]> records;
			records = reader.readRecords(nominalDate.getMonth() + 1);
			for (String[] transactionStrings : records) {
				System.out.println(
						"[" + Arrays.asList(transactionStrings).stream().collect(Collectors.joining("][")) + "]");
				boolean isCommon = Boolean
						.parseBoolean(transactionStrings[NORMALIZED_FIELD_POSITION_OF.IS_COMMON.ordinal()]);
				String sectorName = transactionStrings[NORMALIZED_FIELD_POSITION_OF.SECTOR.ordinal()];
				String title = transactionStrings[NORMALIZED_FIELD_POSITION_OF.TITLE.ordinal()];
				String countName = nominalCount;
				// long unscaledVal = Integer
				// .parseInt(transactionStrings[NORMALIZED_FIELD_POSITION_OF.EMPTY_AMOUNT.ordinal()])
				// * 100
				// +
				// Integer.parseInt(transactionStrings[NORMALIZED_FIELD_POSITION_OF.AMOUNT.ordinal()]);
				BigDecimal amount = BigDecimal.valueOf(
						Integer.parseInt(transactionStrings[NORMALIZED_FIELD_POSITION_OF.AMOUNT.ordinal()]), 2);
				Date date = parseDate(transactionStrings[NORMALIZED_FIELD_POSITION_OF.DATE.ordinal()]);
				transactions.add(new Transaction().setAmount(amount).setIsCommon(isCommon).setCountName(countName)
						.setDate(date).setSectorName(sectorName).setTitle(title));
			}
		}
	}

	private static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			throw new ApiException(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorCode.INTERNAL_ERROR,
					String.format("Error in parsing date '%d' with format \"yyyy-MM-dd\"", date), e.getMessage()));
		}

	}

	@Override
	public List<Date> getListedDates() {
		return transactions.stream().map(t -> t.getDate()).sorted().collect(Collectors.toList());
	}

	@Override
	public Date getLatestListedDate() {
		return (transactions.size() == 0) ? null
				: transactions.stream().map(t -> t.getDate()).max(Date::compareTo).get();
	}

	@Override
	public Date getEarliestListedDate() {
		return (hasTransactions()) ? transactions.stream().map(t -> t.getDate()).min(Date::compareTo).get() : null;
	}

	@Override
	public boolean hasTransactions() {
		return transactions.size() > 0;
	}

	@Override
	public List<Transaction> getTransactions() {
		return transactions.stream().collect(Collectors.toList());
	}

	@Override
	public String getNominalCount() {
		return nominalCount;
	}

	@Override
	public List<Date> getNominalMonths() {
		return nominalMonths.stream().collect(Collectors.toList());
	}

}
