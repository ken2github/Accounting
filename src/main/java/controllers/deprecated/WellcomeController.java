package controllers.deprecated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import controllers.BalancesIndexAPIController;
import controllers.BankFileReaderAPIController;
import controllers.SectorsAPIController;
import controllers.StagedTransactionsAPIController;
import controllers.TransactionsAPIController;
import model2.DetailedSector;
import model2.DetailedTransaction;
import model2.Metric;
import model2.Transaction;
import restapi.bankfileconverter.api.OutputFileInfo;
import restapi.bankprovisioninghelper.service.BankTransactionAnalytics;

@Controller
@RequestMapping("/balancing")
public class WellcomeController {

	public enum UPLOAD_PAGE_STATUS {
		START, UPLOADED;
	}

	private String message = "Hello World";

	@Autowired
	private BankFileReaderAPIController bfrAPI;

	@Autowired
	private SectorsAPIController sAPI;

	@Autowired
	private StagedTransactionsAPIController stAPI;

	@Autowired
	private TransactionsAPIController tAPI;

	@Autowired
	private BalancesIndexAPIController biAPI;

	@Autowired
	private BankTransactionAnalytics bta;

	@RequestMapping("/home")
	public String home(Map<String, Object> model) {
		model.put("message", this.message);
		return "home";
	}

	@RequestMapping("/uploadfile")
	public String uploadfile(Map<String, Object> model) {

		model.put("status", UPLOAD_PAGE_STATUS.START);

		return "uploadfile";
	}

	@RequestMapping("/uploadedfile")
	public String uploadfile(Map<String, Object> model, @RequestParam("file") MultipartFile file) {
		if (file != null) {
			// convert file to transactions
			OutputFileInfo ofi = bfrAPI.convertToTransactions(file);
			List<Transaction> transactions = ofi.transactions;

			// Validate if
			// SUM(all transactions)-SUM(transactions already existing in
			// DB)+SALDOold=SALDOnew
			BigDecimal sumAll = transactions.stream().map(t -> t.getAmount()).reduce(BigDecimal.ZERO,
					((subtotal, t_amount) -> subtotal.add(t_amount)));

			System.out.println("All transaction sum=" + sumAll);

			List<Transaction> doubledTransactions = findDoubledTransactions(transactions);
			BigDecimal sumDoubled = doubledTransactions.stream().map(t -> t.getAmount()).reduce(BigDecimal.ZERO,
					((subtotal, t_amount) -> subtotal.add(t_amount)));

			System.out.println("All double transaction sum=" + sumDoubled);

			BigDecimal newSaldo = ofi.toDateBalance;

			System.out.println("new balance=" + newSaldo);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(ofi.toDate);

			List<Metric> metrics = biAPI.getBalance(calendar.get(Calendar.YEAR) + "",
					(calendar.get(Calendar.MONTH) + 1) + "", ofi.count.name());

			BigDecimal oldSaldo = metrics.get(0).getAmount();

			System.out.println("old balance=" + oldSaldo);

			BigDecimal expectedNewSaldo = sumAll.subtract(sumDoubled).add(oldSaldo);
			BigDecimal delta = expectedNewSaldo.subtract(newSaldo);
			if (delta.compareTo(new BigDecimal(0)) != 0) {
				// Incoerenza
				throw new RuntimeException(String.format(
						"Saldo non coerente: oldSalso=%s + transactions=%s - duplicatedTransactions=%s gives an expectedNewSaldo=%s that should be equal to realNewSaldo=%s but there is a delta=%s.",
						oldSaldo, sumAll, sumDoubled, expectedNewSaldo, newSaldo, delta));
			}

			// add transactions to staged-transactions
			transactions.stream().forEach(t -> stAPI.createTransaction(t));

			// add data for view
			model.put("status", UPLOAD_PAGE_STATUS.UPLOADED);
			model.put("result", Arrays.asList("Uploaded file: " + file.getOriginalFilename(),
					"File transactions:" + transactions.size(), "Staged transactions"));
		} else {
			model.put("status", UPLOAD_PAGE_STATUS.START);
		}
		return "uploadfile";
	}

	public class MonthOfYear implements Comparable<WellcomeController.MonthOfYear> {

		@Override
		public int compareTo(MonthOfYear o) {
			if (this.year == o.getYear()) {
				if (this.month == o.getMonth()) {
					return 0;
				} else {
					return (this.month > o.getMonth()) ? 1 : -1;
				}
			} else {
				return (this.year > o.getYear()) ? 1 : -1;
			}
		}

		public int getYear() {
			return year;
		}

		public int getMonth() {
			return month;
		}

		@Override
		public int hashCode() {
			return toString().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MonthOfYear) {
				return ((MonthOfYear) obj).toString().equals(toString());
			}
			return false;
		}

		@Override
		public String toString() {
			return year + ":" + month;
		}

		private int year;
		private int month;

		public MonthOfYear(int year, int month) {
			this.month = month;
			this.year = year;
		}

	}

	private List<Transaction> findDoubledTransactions(List<Transaction> transactions) {
		List<Transaction> doubledTransactions = Arrays.asList();

		Map<MonthOfYear, List<Transaction>> yearMonthsTransactions = new Hashtable<>();
		transactions.stream().forEach(t -> {
			Integer year = t.getDate().getYear();
			Integer month = t.getDate().getMonth();

			MonthOfYear moy = new MonthOfYear(year, month);

			if (!yearMonthsTransactions.containsKey(moy)) {
				yearMonthsTransactions.put(moy, new ArrayList<>());
			}
			yearMonthsTransactions.get(moy).add(t);
		});

		for (WellcomeController.MonthOfYear moy : yearMonthsTransactions.keySet()) {
			List<DetailedTransaction> dbTransactions = tAPI.findByYearMonth(moy.getYear(), moy.getMonth());
			doubledTransactions
					.addAll(findDoubledTransactionsInMonthOfYear(yearMonthsTransactions.get(moy), dbTransactions));
		}

		return doubledTransactions;
	}

	private List<Transaction> findDoubledTransactionsInMonthOfYear(List<Transaction> transactions,
			List<DetailedTransaction> dbTransactions) {
		List<Transaction> doubledTransactions = Arrays.asList();

		Hashtable<Integer, List<Transaction>> dayTransactions = new Hashtable<>();
		transactions.stream().forEach(t -> {
			Integer day = t.getDate().getDate();
			if (!dayTransactions.containsKey(day)) {
				dayTransactions.put(day, new ArrayList<>());
			}
			dayTransactions.get(day).add(t);
		});

		Hashtable<Integer, List<DetailedTransaction>> dayDBTransactions = new Hashtable<>();
		dbTransactions.stream().forEach(dt -> {
			Integer day = dt.getDate().getDate();
			if (!dayTransactions.containsKey(day)) {
				dayTransactions.put(day, new ArrayList<>());
			}
			dayTransactions.get(day).add(dt);
		});

		for (Integer day : dayTransactions.keySet()) {
			if (dayDBTransactions.containsKey(day)) {
				doubledTransactions
						.addAll(dayTransactions.get(day).stream()
								.filter(t -> dayDBTransactions.get(day).stream()
										.anyMatch(dt -> sameTransactionInSameMonthOfYear(t, dt)))
								.collect(Collectors.toList()));
			}
		}

		return doubledTransactions;

	}

	private boolean sameTransactionInSameMonthOfYear(Transaction t1, Transaction t2) {
		return (t1.getAmount().compareTo(t2.getAmount()) == 0) && t1.getCountName().equals(t2.getCountName())
				&& t1.getTitle().equals(t2.getTitle()) && (t1.getDate().getDate() == (t2.getDate().getDate()));
	}

	@RequestMapping("/staged-transactions")
	public String stagedTransactions(Map<String, Object> model) {
		System.out.println("Edit inside Controller");

		List<DetailedSector> sectors = sAPI.findAll().stream()
				.sorted((ds1, ds2) -> ds1.getName().compareTo(ds2.getName())).collect(Collectors.toList());
		model.put("sectors", sectors);

		List<DetailedTransaction> stagedTransactions = stAPI.findAll();
		model.put("stagedTransactions", stagedTransactions);

		return "staged-transactions";
	}

	@RequestMapping("/staged-transactions/edit")
	public String stagedTransactions(Map<String, Object> model, @RequestParam("edit") String edit) {
		if (edit != null) {
			System.out.println("Edit inside");
		}

		List<DetailedSector> sectors = sAPI.findAll().stream()
				.sorted((ds1, ds2) -> ds1.getName().compareTo(ds2.getName())).collect(Collectors.toList());
		model.put("sectors", sectors);

		List<DetailedTransaction> stagedTransactions = stAPI.findAll();
		model.put("stagedTransactions", stagedTransactions);

		return "staged-transactions";
	}

	@RequestMapping("/staged-transactions/edit/{transactionId}")
	public String viewStagedTransaction(Map<String, Object> model,
			@PathVariable("transactionId") final String transactionId) {

		List<DetailedSector> sectors = sAPI.findAll().stream()
				.sorted((ds1, ds2) -> ds1.getName().compareTo(ds2.getName())).collect(Collectors.toList());
		model.put("sectors", sectors);

		DetailedTransaction stagedTransaction = stAPI.findById(transactionId);
		model.put("detailed_staged_transaction", stagedTransaction);

		return "staged-transaction";
	}

	@RequestMapping("/staged-transactions/save/{transactionId}")
	public String saveStagedTransaction(Map<String, Object> model,
			@PathVariable("transactionId") final String transactionId, @RequestParam("save") String save,
			@RequestParam("isCommon") String isCommon, @RequestParam("sectorName") String sectorName) {

		List<DetailedSector> sectors = sAPI.findAll().stream()
				.sorted((ds1, ds2) -> ds1.getName().compareTo(ds2.getName())).collect(Collectors.toList());
		model.put("sectors", sectors);

		DetailedTransaction stagedTransaction = stAPI.findById(transactionId);
		model.put("detailed_staged_transaction", stagedTransaction);

		if (save != null) {
			if (isCommon != null && !isCommon.equals("")) {
				System.out.println("Received iscommon=" + isCommon);
				stagedTransaction.setIsCommon(Boolean.parseBoolean(isCommon));
				System.out.println("Update in object to=" + (stagedTransaction.isCommon()));
			}
			if (sectorName != null && !sectorName.equals("")) {
				stagedTransaction.setSectorName(sectorName);
				stagedTransaction.setIdSector(sAPI.findByName(sectorName).getId());
			}
			stAPI.updateTransaction(stagedTransaction);
		}
		return "staged-transaction";
	}

	@RequestMapping("/staged-transactions/commitAllTransactions")
	public String commitAllTransactions(Map<String, Object> model) {

		List<DetailedTransaction> stagedTransactions = stAPI.findAll();

		boolean allClassified = stagedTransactions.size() == stagedTransactions.stream()
				.filter(st -> (st.getSectorName() != null && st.isCommon() != null)).count();

		if (allClassified) {
			stagedTransactions.stream().forEach(st -> {
				tAPI.createTransaction(st);
				stAPI.deleteTransaction(st.getId());
			});
		}

		List<DetailedSector> sectors = sAPI.findAll().stream()
				.sorted((ds1, ds2) -> ds1.getName().compareTo(ds2.getName())).collect(Collectors.toList());
		model.put("sectors", sectors);

		stagedTransactions = stAPI.findAll();
		model.put("stagedTransactions", stagedTransactions);

		return "staged-transactions";
	}

}
