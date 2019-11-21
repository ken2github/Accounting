package restapi.bankfileconverter.service.reader.xls.bank.reports.consistency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import model2.Transaction;
import restapi.bankfileconverter.service.reader.BankFileReader;

public abstract class AbstractReportConsistenycTestTemplate {

	public static String templateTestResourceBaseDirectory = "C:\\Users\\primo\\git\\Accounting\\src\\test\\resources\\bank-reports\\reportConsistency2018_2019\\";

	protected List<Transaction> transactions;
	boolean isCompliant;
	List<String> compliancyIssues;
	protected Config config;

	public class Config {
		String fileName;
		int numberOfTransactions;
		BigDecimal totalFlow;
		BigDecimal initialBalance;
		BigDecimal finalBalance;
		Map<Integer, String> dates;
	}

	abstract public Config getTemplateConfig();

	abstract public BankFileReader getBankFileReader();

	@Before
	public void beforeTest() {
		config = getTemplateConfig();
		transactions = getBankFileReader().readTransactions();
		isCompliant = getBankFileReader().isFileCompliantWithFormat();
		compliancyIssues = getBankFileReader().foundComplianceIssues();
	}

	@Test
	public void checkIsCompliant() {
		assertTrue("Unexpected compliancy check failure", isCompliant);
	}

	@Test
	public void checkCompliancyIssuesAreZero() {
		String issues = compliancyIssues.stream().collect(Collectors.joining(";"));
		assertEquals("Found unexpected compliancy issue/s (" + issues + ")", 0, compliancyIssues.size());
	}

	@Test
	public void checkNumberOfTransactions() {
		assertEquals("Wrong number of transactions:", config.numberOfTransactions, transactions.size());
	}

	@Test
	public void checkTotalFlow() {
		assertEquals("Wrong total flow:", config.totalFlow, getTotalFlow());
	}

	@Test
	public void checkDates() throws ParseException {
		for (Map.Entry<Integer, String> entry : config.dates.entrySet()) {
			Date expectedDate = null;
			if (entry.getValue().contains(" ")) {
				expectedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(entry.getValue());
			} else {
				expectedDate = new SimpleDateFormat("dd/MM/yyyy").parse(entry.getValue());
			}
			assertEquals("Wrong date:", expectedDate, transactions.get(entry.getKey()).getDate());
		}
	}

	@Test
	public void checkFinalBalance() {
		assertEquals("Wrong Final Balance:", config.finalBalance, config.initialBalance.add(getTotalFlow()));
	}

	private BigDecimal getTotalFlow() {
		BigDecimal totalFlow = new BigDecimal("0.00");
		if (transactions.size() > 0) {
			totalFlow = transactions.stream().map(t -> t.getAmount()).reduce(BigDecimal::add).get();
		}
		return totalFlow;
	}

}
