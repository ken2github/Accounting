package dao;

import java.util.List;

import model2.DetailedTransaction;
import model2.Transaction;

public interface StagedTransactionDAO {
	DetailedTransaction insert(Transaction t);

	DetailedTransaction update(DetailedTransaction t);

	boolean deleteById(String id);

	DetailedTransaction findById(String id);

	List<DetailedTransaction> findByYearMonth(int year, int month);

	List<DetailedTransaction> findAll();
}
