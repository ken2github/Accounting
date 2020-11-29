package controllers;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import controllers.exceptionhandler.ApiException;
import model2.Count;
import model2.DetailedCount;
import model2.DetailedSector;
import model2.DetailedTransaction;
import model2.Metric;
import model2.Transaction;
import restapi.bankfileconverter.api.OutputFileInfo;
import restapi.transactionsoracle.api.Cacher_1;
import restapi.transactionsoracle.service.TransactionsOracle;

@Controller
@RequestMapping("/balancing")
public class WebAppController {

	Logger logger = LoggerFactory.getLogger(WebAppController.class);

	public enum UPLOAD_PAGE_STATUS {
		START, ERROR, UPLOADED;
	}

	private String message = "Hello World";

	@Autowired
	private DaoDBAccountedYearsAPIController ayAPI;

	@Autowired
	private DaoDBCountsAPIController cAPI;

	@Autowired
	private BankFileReaderAPIController bfrAPI;

	@Autowired
	private DaoDBSectorsAPIController sAPI;

	@Autowired
	private DaoDBStagedTransactionsAPIController stAPI;

	@Autowired
	private DaoDBTransactionsAPIController tAPI;

	@Autowired
	private BalancesIndexAPIController biAPI;

	@Autowired
	private Cacher_1 cacher;

	@Autowired
	private TransactionsOracle bta;

	@RequestMapping("/home")
	public String home(Map<String, Object> model) {
		logger.info("GET /home");
		model.put("message", this.message);
		model.put("indexMenu", getIndexMenu());
		return "home";
	}

	@RequestMapping("/user-guide")
	public String userGuide(Map<String, Object> model) {
		logger.info("GET /user-guide");
		model.put("message", this.message);
		model.put("indexMenu", getIndexMenu());
		return "user-guide";
	}

	private List<String> getIndexMenu() {
		List<String> indexMenu = new ArrayList();

		String item = "%s!http://localhost:8080/balancing/%s";
		indexMenu.add(String.format(item, "Home", "home"));
		indexMenu.add(String.format(item, "Upload", "uploadfile"));
		indexMenu.add(String.format(item, "Staging", "staged-transactions"));
		indexMenu.add(String.format(item, "StagingFiltered", "staged-transactions-filtered"));
		indexMenu.add(String.format(item, "Status", "status"));
		indexMenu.add(String.format(item, "Graphs", "graph/treeMap"));
		indexMenu.add(String.format(item, "UserGuide", "user-guide"));

		return indexMenu;
	}

	@RequestMapping("/to-do-page")
	public String toDoPage(Map<String, Object> model) {
		logger.info("GET /to-do-page");
		return "d3-example";
	}

	@RequestMapping("/graph/treeMap")
	public String graphTreeMap(Map<String, Object> model) {
		logger.info("GET /graph/treeMap");
		model.put("message", this.message);
		model.put("indexMenu", getIndexMenu());
		return "treeMap";
	}

	@RequestMapping("/graph/treeMap/submit")
	public String graphTreeMap(Map<String, Object> model, @RequestParam("year") String year,
			@RequestParam("month") String month, @RequestParam("isCommon") String isCommon,
			@RequestParam("flow") String flow, @RequestParam("auto") String auto) {
		logger.info("GET /graph/treeMap/submit");
		model.put("message", this.message);
		model.put("indexMenu", getIndexMenu());
		model.put("year", year);
		model.put("isCommon", isCommon);
		model.put("flow", flow);
		model.put("auto", auto);
		return "treeMap";
	}

	@RequestMapping("/google")
	public String google(Map<String, Object> model) {
		logger.info("GET /google");
		return "google-example";
	}

	// @RequestMapping("/google/treemap")
	// public String googleTreeMap(Map<String, Object> model) {
	// logger.info("GET /google");
	//
	// return "treeMap";
	// }

	@RequestMapping("/status")
	public String status(Map<String, Object> model) {
		logger.info("GET /status");

		// find all accounts
		List<DetailedCount> counts = cAPI.findAll();

		// find current date
		Date today = Date.from(Instant.now());

		// find for each accounts the last transaction date
		final Map<String, Date> countLastUpdateDateMap = new Hashtable<>();
		counts.stream().forEach(c -> countLastUpdateDateMap.put(c.getName(), findLastUpdateDateForAccounts(c)));

		// show diagram (each month is a "-")

		model.put("message", this.message);
		model.put("countLastUpdateDateMap", countLastUpdateDateMap);

		model.put("indexMenu", getIndexMenu());

		return "status";
	}

	private Date findLastUpdateDateForAccounts(Count c) {
		// find accounted years
		List<Integer> accountedYears = ayAPI.findAll().stream().sorted(Collections.reverseOrder())
				.collect(Collectors.toList());

		// Integer upToDateYear = upToDate.getYear() + 1900;
		//
		// if (!accountedYears.contains(upToDateYear)) {
		// throw new RuntimeException(String.format("Current year '%s' is not in DB
		// accounted years '%s'",
		// upToDateYear, accountedYears.stream().map(y ->
		// y.toString()).collect(Collectors.joining(", "))));
		// }

		Date lastUpdateDate;
		try {
			lastUpdateDate = (new SimpleDateFormat("yyyy-MM-dd")).parse(String.format("%s-12-31", "2017"));
			List<Integer> months = Arrays.asList(12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1);
			for (Integer year : accountedYears) {
				for (Integer month : months) {
					if (tAPI.findByYearMonth(year, month).stream()
							.anyMatch(dt -> dt.getCountName().equals(c.getName()))) {
						return (new SimpleDateFormat("yyyy-MM-dd"))
								.parse(String.format("%s-%s-31", year, (month < 10) ? "0" + month : month));
					}
				}
			}
			//
			// List<Integer> months2 = IntStream.range(12,
			// 1).boxed().collect(Collectors.toList());
			// accountedYears.stream().forEach(y -> months2.stream().forEach(m -> ););
			// IntStream.range(12, 1).boxed()
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		return lastUpdateDate;
	}

	@RequestMapping("/uploadfile")
	public String uploadfile(Map<String, Object> model) {
		logger.info("GET /uploadfile");

		model.put("status", UPLOAD_PAGE_STATUS.START);
		model.put("indexMenu", getIndexMenu());
		return "uploadfile";
	}

	@RequestMapping("/uploadedfile")
	public String uploadfile(Map<String, Object> model, @RequestParam("file") MultipartFile file) {
		logger.info("GET /uploadfile with file");
		if (file != null) {
			try {
				// convert file to transactions
				OutputFileInfo ofi = bfrAPI.convertToTransactions(file);
				List<Transaction> transactions = ofi.transactions;

				// Check if STAGING AREA is empty
				if (!stAPI.findAll().isEmpty()) {
					throw new RuntimeException(
							"Staging area must be empty in order to upload/stage new transactions. Please, check staging area.");
				}

				// Validate if
				// SUM(all transactions)-SUM(transactions already existing in
				// DB)+SALDOold=SALDOnew
				BigDecimal sumAll = transactions.stream().map(t -> t.getAmount()).reduce(BigDecimal.ZERO,
						((subtotal, t_amount) -> subtotal.add(t_amount)));

				// System.out.println("All transaction sum=" + sumAll);

				List<Transaction> uniqueTransactions = new ArrayList<>();
				List<Transaction> doubledTransactions = new ArrayList<>();
				findDoubledTransactions2(transactions, uniqueTransactions, doubledTransactions);
				// List<Transaction> doubledTransactions =
				// findDoubledTransactions(transactions);
				BigDecimal sumDoubled = doubledTransactions.stream().map(t -> t.getAmount()).reduce(BigDecimal.ZERO,
						((subtotal, t_amount) -> subtotal.add(t_amount)));

				// System.out.println("All double transaction sum=" + sumDoubled);

				BigDecimal newSaldo = ofi.toDateBalance;

				// System.out.println("new balance=" + newSaldo);

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(ofi.toDate);

				List<Metric> metrics = biAPI.getBalance(calendar.get(Calendar.YEAR) + "",
						(calendar.get(Calendar.MONTH) + 1) + "", ofi.count.name());

				BigDecimal oldSaldo = metrics.get(0).getAmount();

				// System.out.println("old balance=" + oldSaldo);

				BigDecimal expectedNewSaldo = sumAll.subtract(sumDoubled).add(oldSaldo);
				BigDecimal delta = expectedNewSaldo.subtract(newSaldo);

				if (delta.compareTo(new BigDecimal(0)) != 0) {
					// Incoerenza
					throw new RuntimeException(String.format(
							"File " + file.getOriginalFilename() + " ha un saldo risultante non coerente:\n"
									+ "old_saldo + transactions - duplicated_transactions == expected_new_saldo \n"
									+ "%s + %s - %s != %s \n" + "%s != %s \n" + "delta=%s \n"
									+ "Please, check input (transactions,dates,expected new saldo) ",
							// "old_saldo=%s + transactions=%s - duplicated_transactions=%s gives an
							// expected_new_saldo=%s that should be equal to real_new_saldo=%s but there is
							// a delta=%s.",
							oldSaldo.stripTrailingZeros(), sumAll.stripTrailingZeros(), sumDoubled.stripTrailingZeros(),
							newSaldo.stripTrailingZeros(), expectedNewSaldo.stripTrailingZeros(),
							newSaldo.stripTrailingZeros(), delta.stripTrailingZeros()));
				}

				// add transactions to staged-transactions
				uniqueTransactions.stream().forEach(t -> stAPI.createTransaction(t));

				// add data for view
				model.put("status", UPLOAD_PAGE_STATUS.UPLOADED);
				model.put("result",
						Arrays.asList("Uploaded file: " + file.getOriginalFilename(),
								"Transactions: staged " + uniqueTransactions.size() + " of all " + transactions.size()
										+ " (duplicated " + doubledTransactions.size() + ")"));
			} catch (ApiException ae) {
				model.put("status", UPLOAD_PAGE_STATUS.ERROR);
				model.put("result", Arrays.asList(ae.getApiError().getExternalMessage()));

			} catch (RuntimeException e) {
				model.put("status", UPLOAD_PAGE_STATUS.ERROR);
				if (e.getMessage() == null) {
					e.printStackTrace();
					model.put("result", Arrays.asList(e.toString()));
				} else {
					model.put("result", Arrays.asList(e.getMessage().split("\n")));
				}

			}
		} else {
			model.put("status", UPLOAD_PAGE_STATUS.START);
		}
		model.put("indexMenu", getIndexMenu());
		return "uploadfile";
	}

	public class MonthOfYear implements Comparable<WebAppController.MonthOfYear> {

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

	private void findDoubledTransactions2(List<Transaction> transactions, List<Transaction> uniqueTransactions,
			List<Transaction> doubledTransactions) {
		Map<MonthOfYear, List<Transaction>> yearMonthsTransactions = new Hashtable<>();
		transactions.stream().forEach(t -> {
			Integer year = t.getDate().getYear() + 1900;
			Integer month = t.getDate().getMonth() + 1;

			MonthOfYear moy = new MonthOfYear(year, month);

			if (!yearMonthsTransactions.containsKey(moy)) {
				yearMonthsTransactions.put(moy, new ArrayList<>());
			}
			yearMonthsTransactions.get(moy).add(t);
		});

		for (WebAppController.MonthOfYear moy : yearMonthsTransactions.keySet()) {
			List<DetailedTransaction> dbTransactionsInMonth = tAPI.findByYearMonth(moy.getYear(), moy.getMonth());
			System.out
					.println(String.format("Find all transaction in year '%s' and month '%s'. Found '%s' transactions.",
							moy.year, moy.month, dbTransactionsInMonth.size()));
			List<Transaction> uniqueTransactionsInMonth = new ArrayList<>();
			List<Transaction> doubledTransactionsInMonth = new ArrayList<>();
			findDoubledTransactionsInMonthOfYear2(yearMonthsTransactions.get(moy), dbTransactionsInMonth,
					uniqueTransactionsInMonth, doubledTransactionsInMonth);
			uniqueTransactions.addAll(uniqueTransactionsInMonth);
			doubledTransactions.addAll(doubledTransactionsInMonth);
			// .addAll(findDoubledTransactionsInMonthOfYear(yearMonthsTransactions.get(moy),
			// dbTransactions));
		}
		System.out.println(String.format("Split Transactions '%s' in unique '%s' and doubled '%s'", transactions.size(),
				uniqueTransactions.size(), doubledTransactions.size()));
	}

	private List<Transaction> findDoubledTransactions(List<Transaction> transactions) {
		List<Transaction> doubledTransactions = new ArrayList<>();

		Map<MonthOfYear, List<Transaction>> yearMonthsTransactions = new Hashtable<>();
		transactions.stream().forEach(t -> {
			Integer year = t.getDate().getYear() + 1900;
			Integer month = t.getDate().getMonth() + 1;

			MonthOfYear moy = new MonthOfYear(year, month);

			if (!yearMonthsTransactions.containsKey(moy)) {
				yearMonthsTransactions.put(moy, new ArrayList<>());
			}
			yearMonthsTransactions.get(moy).add(t);
		});

		for (WebAppController.MonthOfYear moy : yearMonthsTransactions.keySet()) {
			List<DetailedTransaction> dbTransactions = tAPI.findByYearMonth(moy.getYear(), moy.getMonth());
			System.out
					.println(String.format("Find all transaction in year '%s' and month '%s'. Found '%s' transactions.",
							moy.year, moy.month, dbTransactions.size()));
			doubledTransactions
					.addAll(findDoubledTransactionsInMonthOfYear(yearMonthsTransactions.get(moy), dbTransactions));
		}

		return doubledTransactions;
	}

	private void findDoubledTransactionsInMonthOfYear2(List<Transaction> transactionsInMonth,
			List<DetailedTransaction> dbTransactionsInMonth, List<Transaction> uniqueTransactionsInMonth,
			List<Transaction> doubledTransactionsInMonth) {

		Hashtable<Integer, List<Transaction>> dayTransactions = new Hashtable<>();
		transactionsInMonth.stream().forEach(t -> {
			Integer day = t.getDate().getDate();
			if (!dayTransactions.containsKey(day)) {
				dayTransactions.put(day, new ArrayList<>());
			}
			dayTransactions.get(day).add(t);
		});

		Hashtable<Integer, List<DetailedTransaction>> dayDBTransactions = new Hashtable<>();
		dbTransactionsInMonth.stream().forEach(dt -> {
			Integer day = dt.getDate().getDate();
			if (!dayDBTransactions.containsKey(day)) {
				dayDBTransactions.put(day, new ArrayList<>());
			}
			dayDBTransactions.get(day).add(dt);
		});

		for (Integer day : dayTransactions.keySet()) {
			if (dayDBTransactions.containsKey(day)) {
				for (Transaction transaction : dayTransactions.get(day)) {
					if (dayDBTransactions.get(day).stream().anyMatch(dt -> dt.isDuplicateOf(transaction))) {
						doubledTransactionsInMonth.add(transaction);
					} else {
						uniqueTransactionsInMonth.add(transaction);
					}
				}
			} else {
				uniqueTransactionsInMonth.addAll(dayTransactions.get(day));
			}
		}
	}

	private List<Transaction> findDoubledTransactionsInMonthOfYear(List<Transaction> transactions,
			List<DetailedTransaction> dbTransactions) {
		List<Transaction> doubledTransactions = new ArrayList<>();

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
			if (!dayDBTransactions.containsKey(day)) {
				dayDBTransactions.put(day, new ArrayList<>());
			}
			dayDBTransactions.get(day).add(dt);
		});

		for (Integer day : dayTransactions.keySet()) {
			if (dayDBTransactions.containsKey(day)) {
				doubledTransactions.addAll(dayTransactions.get(day).stream()
						.filter(t -> dayDBTransactions.get(day).stream().anyMatch(dt -> dt.isDuplicateOf(t)))
						.collect(Collectors.toList()));
			}
		}

		return doubledTransactions;

	}

	// private boolean sameTransactionInSameMonthOfYear(Transaction t1, Transaction
	// t2) {
	// return (t1.getAmount().compareTo(t2.getAmount()) == 0) &&
	// t1.getCountName().equals(t2.getCountName())
	// && t1.getTitle().equals(t2.getTitle()) && (t1.getDate().getDate() ==
	// (t2.getDate().getDate()));
	// }

	@RequestMapping("/staged-transactions")
	public String stagedTransactions(Map<String, Object> model) {
		logger.info("GET /staged-transactions");

		List<DetailedSector> sectors = sAPI.findAll().stream()
				.sorted((ds1, ds2) -> ds1.getName().compareTo(ds2.getName())).collect(Collectors.toList());
		model.put("sectors", sectors);

		List<DetailedTransaction> stagedTransactions = stAPI.findAll();
		model.put("stagedTransactions", stagedTransactions);
		model.put("indexMenu", getIndexMenu());

		return "staged-transactions";
	}

	@RequestMapping("/staged-transactions-filtered/search")
	public String stagedTransactionsFilteredSearch(Map<String, Object> model, @RequestParam("filter") String filter) {
		logger.info("GET /staged-transactions-filtered/search with filter");

		List<DetailedSector> sectors = sAPI.findAll().stream()
				.sorted((ds1, ds2) -> ds1.getName().compareTo(ds2.getName())).collect(Collectors.toList());
		model.put("sectors", sectors);

		List<DetailedTransaction> stagedTransactions = stAPI.findAll();
		model.put("stagedTransactions", stagedTransactions);
		model.put("indexMenu", getIndexMenu());

		List<DetailedTransaction> stagedTransactionsFiltered = new ArrayList<>();
		if (filter != null && !filter.equals("")) {
			for (DetailedTransaction dt : stagedTransactions) {
				if (dt.getTitle().contains(filter)) {

					stagedTransactionsFiltered.add(dt);
				}
			}
		}

		model.put("stagedTransactionsFiltered", stagedTransactionsFiltered);
		model.put("appliedFilter", filter);

		return "staged-transactions-filtered";
	}

	@RequestMapping("/staged-transactions-filtered/update")
	public String stagedTransactionsFilteredSearch(Map<String, Object> model,
			@RequestParam("appliedFilter") String appliedFilter, @RequestParam("isCommon") String isCommon,
			@RequestParam("sectorName") String sector) {
		logger.info("GET /staged-transactions-filtered/update with appliedFilter, isCommon, sector");

		List<DetailedSector> sectors = sAPI.findAll().stream()
				.sorted((ds1, ds2) -> ds1.getName().compareTo(ds2.getName())).collect(Collectors.toList());
		model.put("sectors", sectors);

		List<DetailedTransaction> stagedTransactions = stAPI.findAll();
		model.put("stagedTransactions", stagedTransactions);
		model.put("indexMenu", getIndexMenu());

		List<DetailedTransaction> stagedTransactionsFiltered = new ArrayList<>();
		if (appliedFilter != null && !appliedFilter.equals("")) {
			for (DetailedTransaction dt : stagedTransactions) {
				if (dt.getTitle().contains(appliedFilter)) {
					stagedTransactionsFiltered.add(dt);
					if (isCommon != null && !isCommon.equals("")) {
						dt.setIsCommon(isCommon.equals("true"));
					}
					if (sector != null && !sector.equals("")) {
						dt.setSectorName(sector);
					}
					stAPI.updateTransaction(dt);
				}
			}
		}

		model.put("stagedTransactionsFiltered", stagedTransactionsFiltered);
		model.put("appliedFilter", appliedFilter);

		return "staged-transactions-filtered";
	}

	@RequestMapping("/staged-transactions-filtered")
	public String stagedTransactionsFiltered(Map<String, Object> model) {
		logger.info("GET /staged-transactions-filtered");

		List<DetailedSector> sectors = sAPI.findAll().stream()
				.sorted((ds1, ds2) -> ds1.getName().compareTo(ds2.getName())).collect(Collectors.toList());
		model.put("sectors", sectors);

		List<DetailedTransaction> stagedTransactions = stAPI.findAll();
		model.put("stagedTransactions", stagedTransactions);
		model.put("indexMenu", getIndexMenu());

		model.put("stagedTransactionsFiltered", Arrays.asList());
		model.put("appliedFilter", "");

		return "staged-transactions-filtered";
	}

	@RequestMapping("/staged-transactions/edit")
	public String stagedTransactions(Map<String, Object> model, @RequestParam("edit") String edit) {
		logger.info("GET /staged-transactions-filtered/edit with edit");

		List<DetailedSector> sectors = sAPI.findAll().stream()
				.sorted((ds1, ds2) -> ds1.getName().compareTo(ds2.getName())).collect(Collectors.toList());
		model.put("sectors", sectors);

		List<DetailedTransaction> stagedTransactions = stAPI.findAll();
		model.put("stagedTransactions", stagedTransactions);
		model.put("indexMenu", getIndexMenu());

		return "staged-transactions";
	}

	@RequestMapping("/staged-transactions/edit/{transactionId}")
	public String viewStagedTransaction(Map<String, Object> model,
			@PathVariable("transactionId") final String transactionId) {
		logger.info("GET /staged-transactions-filtered/edit/{transactionId}");

		List<DetailedSector> sectors = sAPI.findAll().stream()
				.sorted((ds1, ds2) -> ds1.getName().compareTo(ds2.getName())).collect(Collectors.toList());
		model.put("sectors", sectors);

		DetailedTransaction stagedTransaction = stAPI.findById(transactionId);
		model.put("detailed_staged_transaction", stagedTransaction);
		model.put("indexMenu", getIndexMenu());

		return "staged-transaction";
	}

	@RequestMapping("/staged-transactions/save/{transactionId}")
	public String saveStagedTransaction(Map<String, Object> model,
			@PathVariable("transactionId") final String transactionId, @RequestParam("save") String save,
			@RequestParam("next") String next, @RequestParam("submit") String submit,
			@RequestParam("isCommon") String isCommon, @RequestParam("sectorName") String sectorName) {
		logger.info(
				"POST /staged-transactions-filtered/save/{transactionId} with transactionId, save, next, submit, isCommon, sectorName");

		List<DetailedSector> sectors = sAPI.findAll().stream()
				.sorted((ds1, ds2) -> ds1.getName().compareTo(ds2.getName())).collect(Collectors.toList());
		model.put("sectors", sectors);

		DetailedTransaction stagedTransaction = stAPI.findById(transactionId);

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

		model.put("indexMenu", getIndexMenu());

		switch (submit) {
		case "Save":
			break;
		case "Save >>":
			return viewStagedTransaction(model, next);
		default:
			break;
		}

		model.put("detailed_staged_transaction", stagedTransaction);

		return "staged-transaction";
	}

	@RequestMapping("/staged-transactions/commitAllTransactions")
	public String commitAllTransactions(Map<String, Object> model, @RequestParam("actionType") String actionType) {
		logger.info("POST /staged-transactions-filtered/commitAllTransactions with actionType");

		List<DetailedTransaction> stagedTransactions = stAPI.findAll();

		switch (actionType) {
		case "commitAllTransactions":
			boolean allClassified = stagedTransactions.size() == stagedTransactions.stream()
					.filter(st -> (st.getSectorName() != null && st.isCommon() != null)).count();

			if (allClassified) {
				stagedTransactions.stream().forEach(st -> {
					tAPI.createTransaction(st);
					stAPI.deleteTransaction(st.getId());
				});
			}
			break;

		case "setRandomValuesInAllTransactions":
			stagedTransactions.stream().filter(st -> (st.getSectorName() == null || st.isCommon() == null))
					.forEach(st -> st.setSectorName("SPS").setIsCommon(false));

			stagedTransactions.stream().forEach(st -> stAPI.updateTransaction(st));

			break;

		case "setIsCommonToYesInAllTransactions":
			stagedTransactions.stream().filter(st -> st.isCommon() == null).forEach(st -> st.setIsCommon(true));

			stagedTransactions.stream().forEach(st -> stAPI.updateTransaction(st));

			break;

		case "setIsCommonToNoInAllTransactions":
			stagedTransactions.stream().filter(st -> st.isCommon() == null).forEach(st -> st.setIsCommon(false));

			stagedTransactions.stream().forEach(st -> stAPI.updateTransaction(st));

			break;

		default:
			break;
		}

		List<DetailedSector> sectors = sAPI.findAll().stream()
				.sorted((ds1, ds2) -> ds1.getName().compareTo(ds2.getName())).collect(Collectors.toList());
		model.put("sectors", sectors);

		stagedTransactions = stAPI.findAll();
		model.put("stagedTransactions", stagedTransactions);

		model.put("indexMenu", getIndexMenu());
		return "staged-transactions";
	}

}
