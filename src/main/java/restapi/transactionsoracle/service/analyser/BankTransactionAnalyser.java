package restapi.transactionsoracle.service.analyser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;

import dao.AccountedYearDAO;
import dao.SectorDAO;
import dao.StagedTransactionDAO;
import dao.TransactionDAO;
import model2.DetailedTransaction;
import model2.Transaction;
import restapi.transactionsoracle.api.SimilarityRelevance;
import restapi.transactionsoracle.api.StringSimilarityCalculator;
import restapi.transactionsoracle.api.StringTokenizer;
import restapi.transactionsoracle.service.TransactionsOracle;

public class BankTransactionAnalyser implements TransactionsOracle {

	@Autowired
	private StringTokenizer tokenizer;

	@Autowired
	private StringSimilarityCalculator similarityCalculator;

	@Autowired
	private SectorDAO sectorDAO;

	@Autowired
	private AccountedYearDAO accountedYearDAO;

	@Autowired
	private TransactionDAO transactionDAO;

	@Autowired
	private StagedTransactionDAO stagedTransactionDAO;

	public BankTransactionAnalyser() {
	}

	@Autowired
	public BankTransactionAnalyser(StringTokenizer tokenizer, StringSimilarityCalculator similarityCalculator) {
		this.tokenizer = tokenizer;
		this.similarityCalculator = similarityCalculator;
	}

	static public class TokenTreeItem implements Comparable<TokenTreeItem> {

		public DetailedTransaction transaction;
		public List<String> tokens;
		public String id;

		public TokenTreeItem(DetailedTransaction transaction, List<String> tokens, String id) {
			super();
			this.transaction = transaction;
			this.tokens = tokens;
			this.id = id;
		}

		@Override
		public int hashCode() {
			return id.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TokenTreeItem)
				return this.id.equals(((TokenTreeItem) obj).getId());
			return false;
		}

		@Override
		public String toString() {
			return id;
		}

		@Override
		public int compareTo(TokenTreeItem o) {
			return this.id.compareTo(o.getId());
		}

		public DetailedTransaction getTransaction() {
			return transaction;
		}

		public String getId() {
			return id;
		}

		public List<String> getTokens() {
			return tokens;
		}

	}

	static public class TransactionWeight {
		public DetailedTransaction transaction;
		public double weight;

		public DetailedTransaction getTransaction() {
			return transaction;
		}

		public TransactionWeight setTransaction(DetailedTransaction transaction) {
			this.transaction = transaction;
			return this;
		}

		public double getWeight() {
			return weight;
		}

		public TransactionWeight setWeight(double weight) {
			this.weight = weight;
			return this;
		}

		@Override
		public int hashCode() {
			return this.transaction.getId().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TransactionWeight)
				return this.transaction.getId().equals(((TransactionWeight) obj).getTransaction().getId());
			return false;
		}

		@Override
		public String toString() {
			return this.transaction.getId();
		}

	}

	static public class SectorWeight {
		public String sector;
		public double weight;

		public String getSector() {
			return sector;
		}

		public SectorWeight setSector(String sector) {
			this.sector = sector;
			return this;
		}

		public double getWeight() {
			return weight;
		}

		public SectorWeight setWeight(double weight) {
			this.weight = weight;
			return this;
		}

		@Override
		public int hashCode() {
			return this.sector.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof SectorWeight)
				return this.sector.equals(((SectorWeight) obj).getSector());
			return false;
		}

		@Override
		public String toString() {
			return this.sector;
		}

	}

	private Map<String, List<TokenTreeItem>> tokensMap = new TreeMap<>();
	private Map<String, List<DetailedTransaction>> sectorMap = new TreeMap<>();
	private long refreshTimestamp = 0;
	private int transactionSetSize;
	private int sectorSetSize;

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
					tokensMap.put(token, new ArrayList<BankTransactionAnalyser.TokenTreeItem>());
				}
				tokensMap.get(token).add(treeItem);
			});
		});
		tokensMap.keySet().stream().sorted()
				.forEach(t -> System.out.println(String.format("TOK:[%s] TRS:[%s]", t, tokensMap.get(t).size())));

		refreshTimestamp = System.currentTimeMillis();
	}

	@Override
	synchronized public List<String> suggestSectorsForTransaction(Transaction transaction, int maxResults,
			SimilarityRelevance relevance) {
		refreshInternalMapIfNeeded(false, true);

		List<TransactionWeight> weightedTransactionsHavingAtLeastOneCommonToken = getWeightedTransactionsHavingAtLeastOneCommonToken(
				transaction, relevance);

		// System.out.println(String.format("TR:[%s]", transaction.getTitle()));
		// weightedTransactionsHavingAtLeastOneCommonToken.stream().forEach(
		// wt -> System.out.println(String.format("SIM:[%s] TR:[%s]", wt.weight,
		// wt.transaction.getTitle())));

		// return secsec(weightedTransactionsHavingAtLeastOneCommonToken, maxResults);

		return weightedTransactionsHavingAtLeastOneCommonToken.stream()
				.filter(wt -> wt.getWeight() > relevance.toDouble()).map(wt -> wt.getTransaction().getSectorName())
				.distinct().limit(maxResults).collect(Collectors.toList());

	}

	private double probSect(String sector, List<TransactionWeight> weightedTransactions) {
		double prob_s = ((double) sectorMap.get(sector).size()) / ((double) transactionSetSize);
		double prob_t = ((double) weightedTransactions.size() + 1) / ((double) transactionSetSize);
		double prob_t_s = ((double) weightedTransactions.stream()
				.filter(wt -> wt.getTransaction().getSectorName().equals(sector)).count())
				/ ((double) weightedTransactions.size());
		return (prob_s * prob_t_s) / prob_t;
	}

	private List<String> secsec(List<TransactionWeight> weightedTransactions, int maxResults) {
		Map<String, List<TransactionWeight>> sectroMap = new TreeMap<>();

		weightedTransactions.stream().forEach(wt -> {
			if (!sectroMap.containsKey(wt.getTransaction().getSectorName())) {
				sectroMap.put(wt.getTransaction().getSectorName(), new ArrayList<TransactionWeight>());
			}
			sectroMap.get(wt.getTransaction().getSectorName()).add(wt);
		});

		List<SectorWeight> weightedSectors = sectroMap.keySet().stream()
				.map(s -> new SectorWeight().setSector(s).setWeight(sectroMap.get(s).stream().mapToDouble(wt -> {
					return new Double(wt.getWeight());
				}).sum())).sorted((ws1, ws2) -> Double.compare(ws2.getWeight(), ws1.getWeight()))
				.collect(Collectors.toList());

		return weightedSectors.stream().map(ws -> ws.getSector()).limit(maxResults).collect(Collectors.toList());
	}

	private int transactionsContainingTokenInSet(String token) {
		if (tokensMap.containsKey(token)) {
			return tokensMap.get(token).size();
		}
		return 0;
	}

	private TokenTreeItem treeItemize(DetailedTransaction transaction) {
		return new TokenTreeItem(transaction, tokenizer.tokenize(transaction.getTitle()), transaction.getId());
	}

	private void refreshInternalMapIfNeeded(boolean nullableSector, boolean nullableIsCommon) {
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

	private List<TransactionWeight> getWeightedTransactionsHavingAtLeastOneCommonToken(Transaction transaction,
			SimilarityRelevance relevance) {
		List<TokenTreeItem> transactionsHavingAtLeastOneCommonToken = tokenizer.tokenize(transaction.getTitle())
				.stream().distinct().filter(token -> tokensMap.containsKey(token)).map(token -> tokensMap.get(token))
				.flatMap(treeItems -> treeItems.stream()).distinct().collect(Collectors.toList());

		List<TransactionWeight> weightedTransactionsHavingAtLeastOneCommonToken = transactionsHavingAtLeastOneCommonToken
				.stream()
				.map(treeItem -> new TransactionWeight().setTransaction(treeItem.getTransaction())
						.setWeight(similarityCalculator.getSimilarity(tokenizer.tokenize(transaction.getTitle()),
								treeItem.tokens, this::transactionsContainingTokenInSet, transactionSetSize)))
				.sorted((wt1, wt2) -> Double.compare(wt2.getWeight(), wt1.getWeight())).collect(Collectors.toList());

		return weightedTransactionsHavingAtLeastOneCommonToken;
	}

	@Override
	synchronized public Optional<Boolean> suggestIsCommonForTransaction(Transaction transaction,
			SimilarityRelevance relevance) {
		refreshInternalMapIfNeeded(true, false);

		List<TransactionWeight> weightedTransactionsHavingAtLeastOneCommonToken = getWeightedTransactionsHavingAtLeastOneCommonToken(
				transaction, relevance);

		// System.out.println(String.format("TR:[%s]", transaction.getTitle()));
		// weightedTransactionsHavingAtLeastOneCommonToken.stream().forEach(
		// wt -> System.out.println(String.format("SIM:[%s] TR:[%s]", wt.weight,
		// wt.transaction.getTitle())));

		// return secsec(weightedTransactionsHavingAtLeastOneCommonToken, maxResults);

		return weightedTransactionsHavingAtLeastOneCommonToken.stream()
				.filter(wt -> wt.getWeight() > relevance.toDouble()).map(wt -> wt.getTransaction().isCommon())
				.findFirst();
	}
}
