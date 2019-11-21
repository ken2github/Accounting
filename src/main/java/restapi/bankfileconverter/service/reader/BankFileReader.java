package restapi.bankfileconverter.service.reader;

import java.util.List;

import model2.Transaction;

public interface BankFileReader {

	List<Transaction> readTransactions();

	boolean isFileCompliantWithFormat();

	List<String> foundComplianceIssues();

}
