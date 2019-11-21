package dao;

import java.util.Date;
import java.util.List;

import model2.Transaction;

public interface ReadOnlyTransactionList {

	List<Date> getListedDates();

	Date getLatestListedDate();

	Date getEarliestListedDate();

	boolean hasTransactions();

	List<Transaction> getTransactions();

	String getNominalCount();

	List<Date> getNominalMonths();

}
