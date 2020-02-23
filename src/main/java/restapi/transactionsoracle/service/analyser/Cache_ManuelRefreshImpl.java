package restapi.transactionsoracle.service.analyser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;

import dao.AccountedYearDAO;
import dao.SectorDAO;
import dao.StagedTransactionDAO;
import dao.TransactionDAO;
import model2.DetailedTransaction;
import restapi.transactionsoracle.api.Cacher_1;
import restapi.transactionsoracle.api.StringTokenizer;

public class Cache_ManuelRefreshImpl implements Cacher_1 {

	@Autowired
	private StringTokenizer tokenizer;

	@Autowired
	private SectorDAO sectorDAO;

	@Autowired
	private AccountedYearDAO accountedYearDAO;

	@Autowired
	private TransactionDAO transactionDAO;

	@Autowired
	private StagedTransactionDAO stagedTransactionDAO;

	private Map<String, List<TokenTreeItem_1>> tokensMap = new TreeMap<>();
	private Map<String, List<DetailedTransaction>> sectorMap = new TreeMap<>();
	private long refreshTimestamp = 0;
	private int transactionSetSize;
	private int sectorSetSize;

	@Override
	public List<TokenTreeItem_1> getTransactionsHavingAtLeastOneCommonToken(List<String> tokens,
			boolean getAlsoTransactionWithNullSector, boolean getAlsoTransactionWithNullIsCommon) {
		return tokens.stream().distinct().filter(token -> tokensMap.containsKey(token))
				.map(token -> tokensMap.get(token)).flatMap(treeItems -> treeItems.stream()).distinct()
				.filter(treeItem -> getAlsoTransactionWithNullSector
						|| (treeItem.getTransaction().getSectorName() != null))
				.filter(treeItem -> getAlsoTransactionWithNullIsCommon
						|| (treeItem.getTransaction().isCommon() != null))
				.collect(Collectors.toList());
	}

	@Override
	public int transactionsContainingTokenInSet(String token, boolean getAlsoTransactionWithNullSector,
			boolean getAlsoTransactionWithNullIsCommon) {
		if (tokensMap.containsKey(token)) {
			return (int) tokensMap.get(token).stream().filter(
					treeItem -> getAlsoTransactionWithNullSector || (treeItem.getTransaction().getSectorName() != null))
					.filter(treeItem -> getAlsoTransactionWithNullIsCommon
							|| (treeItem.getTransaction().isCommon() != null))
					.count();
		}
		return 0;
	}

	@Override
	public int getTransactionSetSize() {
		return transactionSetSize;
	}

	/**
	 * Initialize tokensMap tree with all tokens found in all transactions titles.
	 * 
	 * Each node is a token. It has also an associated list of all transactions
	 * containing it, opportunely stored in a TreeItem object identified by
	 * transactionId.
	 * 
	 * @param transactions
	 *            All transaction existing in database
	 */
	synchronized public void refreshTokensMap(List<DetailedTransaction> transactions, List<String> sectors) {
		sectorMap.clear();
		sectorSetSize = sectors.size();
		sectors.stream().forEach(sector -> sectorMap.put(sector, new ArrayList<DetailedTransaction>()));
		transactions.stream().filter(tr -> tr.getSectorName() != null)
				.forEach(transaction -> sectorMap.get(transaction.getSectorName()).add(transaction));
		sectorMap.keySet().stream().sorted().forEach(sector -> System.out
				.println(String.format("SECTOR:[%s] TRS:[%s]", sector, sectorMap.get(sector).size())));

		tokensMap.clear();
		transactionSetSize = transactions.size();
		transactions.stream().map(transaction -> treeItemize(transaction)).forEach(treeItem -> {
			treeItem.getTokens().stream().distinct().forEach(token -> {
				if (!tokensMap.containsKey(token)) {
					tokensMap.put(token, new ArrayList<TokenTreeItem_1>());
				}
				tokensMap.get(token).add(treeItem);
			});
		});
		tokensMap.keySet().stream().sorted()
				.forEach(t -> System.out.println(String.format("TOK:[%s] TRS:[%s]", t, tokensMap.get(t).size())));

		refreshTimestamp = System.currentTimeMillis();
	}

	public void refreshInternalMapIfNeeded(boolean nullableSector, boolean nullableIsCommon) {
		// if (System.currentTimeMillis() > refreshTimestamp + (1000 * 5)) {
		List<String> sectors = sectorDAO.findAll().stream().map(ds -> ds.getName()).collect(Collectors.toList());

		List<DetailedTransaction> stagedTransactions = stagedTransactionDAO.findAll().stream()
				.filter(dt -> (nullableSector || ((dt.getSectorName() != null) && (!dt.getSectorName().equals(""))))
						&& (nullableIsCommon || (dt.isCommon() != null)))
				.collect(Collectors.toList());

		List<DetailedTransaction> allTransactions = accountedYearDAO.findAll().stream()
				.map(y -> IntStream.range(1, 12).boxed().map(m -> Arrays.asList(y, m)).collect(Collectors.toList()))
				.flatMap(ymList -> ymList.stream()).map(ym -> transactionDAO.findByYearMonth(ym.get(0), ym.get(1)))
				.flatMap(dtList -> dtList.stream()).collect(Collectors.toList());

		System.out.println(String.format("REFR-TR-SIZE: STAGED-T[%s]", stagedTransactions.size()));
		System.out.println(String.format("REFR-TR-SIZE: T[%s]", allTransactions.size()));
		allTransactions.addAll(stagedTransactions);
		System.out.println(String.format("REFR-TR-SIZE:[%s]", allTransactions.size()));
		allTransactions.stream().forEach(tr -> System.out.println(String.format("REFR-TR:[%s]", tr.getTitle())));

		refreshTokensMap(allTransactions, sectors);
		// }
	}

	private TokenTreeItem_1 treeItemize(DetailedTransaction transaction) {
		return new TokenTreeItem_1(transaction, tokenizer.tokenize(transaction.getTitle()), transaction.getId());
	}

}
