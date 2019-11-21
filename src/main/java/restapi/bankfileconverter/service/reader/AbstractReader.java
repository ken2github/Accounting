package restapi.bankfileconverter.service.reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import model2.Transaction;

public abstract class AbstractReader implements BankFileReader {

	protected List<Transaction> transactions;
	protected boolean formatIsCompliant = false;
	protected List<String> formatUnCompliances = new ArrayList<>();

	@Override
	public boolean isFileCompliantWithFormat() {
		return formatIsCompliant;
	}

	@Override
	public List<String> foundComplianceIssues() {
		if (!isFileCompliantWithFormat()) {
			return Collections.unmodifiableList(this.formatUnCompliances);
		}
		return Arrays.asList();
	}

	@Override
	public List<Transaction> readTransactions() {
		return transactions;
	}

}
