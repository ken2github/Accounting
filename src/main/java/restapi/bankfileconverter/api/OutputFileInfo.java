package restapi.bankfileconverter.api;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import model2.Transaction;

public class OutputFileInfo {

	public Count count;
	public Date fromDate;
	public Date toDate;
	public BigDecimal toDateBalance;
	public List<Transaction> transactions;
	public BigDecimal totalFlow;

	public Count getCount() {
		return count;
	}

	public OutputFileInfo setCount(Count count) {
		this.count = count;
		return this;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public OutputFileInfo setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public Date getToDate() {
		return toDate;
	}

	public OutputFileInfo setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

	public BigDecimal getToDateBalance() {
		return toDateBalance;
	}

	public OutputFileInfo setToDateBalance(BigDecimal toDateBalance) {
		this.toDateBalance = toDateBalance;
		return this;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public OutputFileInfo setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
		return this;
	}

	public BigDecimal getTotalFlow() {
		return totalFlow;
	}

	public OutputFileInfo setTotalFlow(BigDecimal totalFlow) {
		this.totalFlow = totalFlow;
		return this;
	}

}
