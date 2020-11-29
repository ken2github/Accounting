package restapi.transactionsoracle.service.analyser;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import dao.AccountedYearDAO;
import dao.Observable;
import dao.Observer;
import dao.SectorDAO;
import dao.StagedTransactionDAO;
import dao.TransactionDAO;
import model2.DetailedSector;
import model2.DetailedTransaction;
import restapi.transactionsoracle.api.Cacher_1;
import restapi.transactionsoracle.api.StringTokenizer;

public class Cache_ObserverImpl implements Cacher_1, Observer {

	private StringTokenizer tokenizer;

	private SectorDAO sectorDAO;

	private AccountedYearDAO accountedYearDAO;

	private TransactionDAO transactionDAO;

	private StagedTransactionDAO stagedTransactionDAO;

	private Map<String, List<TokenTreeItem_1>> tokensMap = new TreeMap<>();
	private Map<String, List<DetailedTransaction>> sectorMap = new TreeMap<>();
	private Map<String, DetailedTransaction> transactionsMap = new TreeMap<>();
	private long refreshTimestamp = 0;
	private int transactionSetSize;
	private int sectorSetSize;

	@Autowired
	public Cache_ObserverImpl(AccountedYearDAO accountedYearDAO, SectorDAO sectorDAO, TransactionDAO transactionDAO,
			StagedTransactionDAO stagedTransactionDAO, StringTokenizer tokenizer) {
		((Observable<DetailedTransaction>) transactionDAO).subscribe(this);
		((Observable<DetailedTransaction>) stagedTransactionDAO).subscribe(this);
		((Observable<DetailedSector>) sectorDAO).subscribe(this);
		((Observable<Integer>) accountedYearDAO).subscribe(this);

		this.tokenizer = tokenizer;
		this.sectorDAO = sectorDAO;
		this.accountedYearDAO = accountedYearDAO;
		this.transactionDAO = transactionDAO;
		this.stagedTransactionDAO = stagedTransactionDAO;

		initMaps();
	}

	private void initMaps() {
		List<String> allSectors = sectorDAO.findAll().stream().map(ds -> ds.getName()).collect(toList());

		List<DetailedTransaction> allStagedTransactions = stagedTransactionDAO.findAll();

		List<Integer> allAccountedYears = accountedYearDAO.findAll();
		List<YearMonth> allAccountedYearMonths = allAccountedYears.stream()
				.flatMap(y -> range(1, 12).boxed().map(m -> YearMonth.of(y, m))).collect(toList());
		List<DetailedTransaction> allTransactions = allAccountedYearMonths.stream()
				.flatMap(ym -> transactionDAO.findByYearMonth(ym.getYear(), ym.getMonthValue()).stream())
				.collect(toList());

		// List<DetailedTransaction> allTransactions =
		// accountedYearDAO.findAll().stream()
		// .map(y -> IntStream.range(1, 12).boxed().map(m -> Arrays.asList(y,
		// m)).collect(Collectors.toList()))
		// .flatMap(ymList -> ymList.stream()).map(ym ->
		// transactionDAO.findByYearMonth(ym.get(0), ym.get(1)))
		// .flatMap(dtList -> dtList.stream()).collect(Collectors.toList());

		int allStagedTransactionsSize = allStagedTransactions.size();
		int allTransactionsSize = allTransactions.size();
		allTransactions.addAll(allStagedTransactions);
		int allCombinedTransactionsSize = allTransactions.size();

		// allTransactions.stream().forEach(tr ->
		// System.out.println(String.format("REFR-TR:[%s]", tr.getTitle())));

		refreshTokensMap(allTransactions, allSectors);

		System.out.println(String.format("REFR-TR-SIZE: STAGED-TRANSACTIONS [%s]", allStagedTransactionsSize));
		System.out.println(String.format("REFR-TR-SIZE: TRANSACTIONS [%s]", allTransactionsSize));
		System.out.println(String.format("REFR-TR-SIZE: COMBINED TRANSACTIONS [%s]", allCombinedTransactionsSize));
	}

	private void _refreshSectorMap(List<DetailedTransaction> transactions, List<String> sectors) {
		sectorMap.clear();
		sectorSetSize = sectors.size();
		sectors.stream().forEach(sector -> sectorMap.put(sector, new ArrayList<DetailedTransaction>()));
		transactions.stream().filter(tr -> tr.getSectorName() != null)
				.forEach(transaction -> sectorMap.get(transaction.getSectorName()).add(transaction));

		// print list
		sectorMap.keySet().stream().sorted().forEach(sector -> System.out
				.println(String.format("SECTOR:[%s] TRS:[%s]", sector, sectorMap.get(sector).size())));
	}

	private void _refreshTokenMapAndTransactionsMap(List<DetailedTransaction> transactions) {
		tokensMap.clear();
		transactionsMap.clear();
		transactionSetSize = transactions.size();
		transactions.stream().map(transaction -> _treeItemize(transaction)).forEach(treeItem -> {
			transactionsMap.put(treeItem.getTransaction().getId(), treeItem.getTransaction());
			treeItem.getTokens().stream().distinct().forEach(token -> {
				if (!tokensMap.containsKey(token)) {
					tokensMap.put(token, new ArrayList<TokenTreeItem_1>());
				}
				tokensMap.get(token).add(treeItem);
			});
		});

		// print list
		tokensMap.keySet().stream().sorted()
				.forEach(t -> System.out.println(String.format("TOK:[%s] TRS:[%s]", t, tokensMap.get(t).size())));
	}

	synchronized public void refreshTokensMap(List<DetailedTransaction> transactions, List<String> sectors) {
		_refreshSectorMap(transactions, sectors);
		_refreshTokenMapAndTransactionsMap(transactions);
		refreshTimestamp = System.currentTimeMillis();
	}

	@Override
	public List<TokenTreeItem_1> getTransactionsHavingAtLeastOneCommonToken(List<String> tokens,
			boolean getAlsoTransactionWithNullSector, boolean getAlsoTransactionWithNullIsCommon) {
		// System.out.println("inside getTransactionsHavingAtLeastOneCommonToken");
		NullFilter nullFilter = new NullFilter(getAlsoTransactionWithNullSector, getAlsoTransactionWithNullIsCommon);
		return tokens.stream().distinct().filter(token -> tokensMap.containsKey(token))
				.flatMap(token -> tokensMap.get(token).stream()).distinct()
				.filter(treeItem -> nullFilter.filter(treeItem)).collect(toList());
	}

	@Override
	public int transactionsContainingTokenInSet(String token, boolean getAlsoTransactionWithNullSector,
			boolean getAlsoTransactionWithNullIsCommon) {
		// System.out.println("inside transactionsContainingTokenInSet");
		NullFilter nullFilter = new NullFilter(getAlsoTransactionWithNullSector, getAlsoTransactionWithNullIsCommon);
		if (tokensMap.containsKey(token)) {
			return (int) tokensMap.get(token).stream().filter(treeItem -> nullFilter.filter(treeItem)).count();
		}
		return 0;
	}

	private static class NullFilter {
		boolean getAlsoTransactionWithNullSector;
		boolean getAlsoTransactionWithNullIsCommon;

		public NullFilter(boolean getAlsoTransactionWithNullSector, boolean getAlsoTransactionWithNullIsCommon) {
			this.getAlsoTransactionWithNullSector = getAlsoTransactionWithNullSector;
			this.getAlsoTransactionWithNullIsCommon = getAlsoTransactionWithNullIsCommon;
		}

		boolean filter(TokenTreeItem_1 tti) {
			if ((tti.getTransaction().getSectorName() == null) && !getAlsoTransactionWithNullSector)
				return false;
			if ((tti.getTransaction().isCommon() == null) && !getAlsoTransactionWithNullIsCommon)
				return false;
			return true;
		}
	}

	@Override
	public int getTransactionSetSize() {
		return transactionSetSize;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void update(@SuppressWarnings("rawtypes") List changes) {

		((List<Change>) changes).stream().forEach(change -> {
			if (change.getT() instanceof DetailedTransaction) {
				DetailedTransaction item = (DetailedTransaction) change.getT();
				switch (change.getChangeType()) {
				case add:
					System.out.println("Adding transaction " + item.getId() + " in cache");
					transactionsMap.put(item.getId(), item);
					Arrays.asList(_treeItemize(item)).stream()
							.forEach(treeItem -> treeItem.getTokens().stream().distinct().forEach(token -> {
								if (!tokensMap.containsKey(token)) {
									tokensMap.put(token, new ArrayList<TokenTreeItem_1>());
								}
								tokensMap.get(token).add(treeItem);
							}));
					break;
				case mod:
					System.out.println("Modifing transaction " + item.getId() + " in cache");
					transactionsMap.replace(item.getId(), item);
					tokensMap.keySet().stream().forEach(token -> {
						tokensMap.get(token).removeIf(ti -> ti.getTransaction().getId().equals(item.getId()));
					});
					Arrays.asList(_treeItemize(item)).stream()
							.forEach(treeItem -> treeItem.getTokens().stream().distinct().forEach(token -> {
								if (!tokensMap.containsKey(token)) {
									tokensMap.put(token, new ArrayList<TokenTreeItem_1>());
								}
								tokensMap.get(token).add(treeItem);
							}));
					break;
				case rem:
					System.out.println("Deleting transaction " + item.getId() + " in cache");
					DetailedTransaction itemById = transactionsMap.get(item.getId());
					if (itemById == null) {
						System.out.println("Impossibile trovare transazione = " + item.getId());
					}
					Arrays.asList(_treeItemize(itemById)).stream()
							.forEach(treeItem -> treeItem.getTokens().stream().distinct().forEach(token -> {
								if (tokensMap.containsKey(token)) {
									tokensMap.get(token).removeIf(ti -> treeItem.equals(ti));
								}
							}));
					transactionsMap.remove(itemById.getId());
					break;
				}
			}
			if (change.getT() instanceof DetailedSector) {
				DetailedSector item = (DetailedSector) change.getT();
				initMaps();
			}
			if (change.getT() instanceof Integer) {
				initMaps();
			}
		});
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	private TokenTreeItem_1 _treeItemize(DetailedTransaction transaction) {
		return new TokenTreeItem_1(transaction, tokenizer.tokenize(transaction.getTitle()), transaction.getId());
	}

}
