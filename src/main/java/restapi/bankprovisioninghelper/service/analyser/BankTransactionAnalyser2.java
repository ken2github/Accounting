package restapi.bankprovisioninghelper.service.analyser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang.mutable.MutableInt;
import org.springframework.beans.factory.annotation.Autowired;

import model2.DetailedTransaction;
import model2.Transaction;
import restapi.bankprovisioninghelper.api.Relevance;
import restapi.bankprovisioninghelper.api.SimilarityCalculator;
import restapi.bankprovisioninghelper.api.Tokenizer;
import restapi.bankprovisioninghelper.service.BankTransactionAnalytics;

public class BankTransactionAnalyser2 implements BankTransactionAnalytics {

	@Autowired
	private Tokenizer tokenizer;

	@Autowired
	private SimilarityCalculator similarityCalculator;

	static public class TokenTreeItem implements Comparable<TokenTreeItem> {

		public DetailedTransaction transaction;

		public String token;
		public List<String> tokens;
		public List<Integer> positions;
		public int occurrencies;
		public String id;

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

		public TokenTreeItem setTransaction(DetailedTransaction transaction) {
			this.transaction = transaction;
			return this;
		}

		public String getId() {
			return id;
		}

		public TokenTreeItem setId(String id) {
			this.id = id;
			return this;
		}

		public String getToken() {
			return token;
		}

		public TokenTreeItem setToken(String token) {
			this.token = token;
			return this;
		}

		public List<String> getTokens() {
			return tokens;
		}

		public TokenTreeItem setTokens(List<String> tokens) {
			this.tokens = tokens;
			return this;
		}

		public List<Integer> getPositions() {
			return positions;
		}

		public TokenTreeItem setPositions(List<Integer> positions) {
			this.positions = positions;
			return this;
		}

		public int getOccurrencies() {
			return occurrencies;
		}

		public TokenTreeItem setOccurrencies(int occurrencies) {
			this.occurrencies = occurrencies;
			return this;
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

	private Map<String, List<TokenTreeItem>> tokensMap = new TreeMap<>();
	private int transactionSetSize;

	/**
	 * Initialize tokensMap tree with all tokens found in all transactions titles.
	 * 
	 * Each node is a token. It has also an associated list of all transactions
	 * containing it, opportunely stored in a TreeItem object identified by couple
	 * token-transactionId.
	 * 
	 * @param transactions
	 *            All transaction existing in database
	 */
	private void initTokensMap(List<DetailedTransaction> transactions) {
		transactionSetSize = transactions.size();
		transactions.stream().forEach(transaction -> {
			List<TokenTreeItem> treeItems = treeItemize(transaction);
			treeItems.stream().forEach(treeItem -> {
				if (!tokensMap.containsKey(treeItem.getToken())) {
					tokensMap.put(treeItem.getToken(), new ArrayList<BankTransactionAnalyser2.TokenTreeItem>());
				}
				tokensMap.get(treeItem.getToken()).add(treeItem);
			});
		});
	}

	@Override
	public List<String> suggestSectorsForTransaction(Transaction transaction, int maxResults, Relevance relevance) {
		List<String> results = new ArrayList<>();

		List<String> tokens = tokenizer.tokenize(transaction.getTitle());
		List<List<TokenTreeItem>> treeItems = new ArrayList<>();

		tokens.stream().distinct().forEach(token -> {
			if (tokensMap.containsKey(token)) {
				treeItems.add(tokensMap.get(token));
			}
		});

		Map<String, TransactionWeight> weightedTransactions = new TreeMap<>();
		treeItems.stream().flatMap(subTreeItems -> subTreeItems.stream()).forEach(treeItem -> {
			if (!weightedTransactions.containsKey(treeItem.getTransaction().getId())) {
				TransactionWeight tw = new TransactionWeight().setTransaction(treeItem.getTransaction())
						.setWeight(similarityCalculator.getSimilarity(tokenizer.tokenize(transaction.getTitle()),
								treeItem.tokens, this::transactionsContainingTokenInSet, transactionSetSize));
				weightedTransactions.put(tw.getTransaction().getId(), tw);
			}
		});

		List<TransactionWeight> sortedWeightedTransactions = weightedTransactions.values().stream()
				.sorted((wt1, wt2) -> Double.compare(wt1.getWeight(), wt2.getWeight())).collect(Collectors.toList());
		for (int i = 0; i < maxResults; i++) {
			if (i < sortedWeightedTransactions.size()) {
				if (sortedWeightedTransactions.get(i).weight >= thresholdRelevance(relevance)) {
					results.add(sortedWeightedTransactions.get(i).getTransaction().getSectorName());
				}
			}
		}

		return results.stream().distinct().collect(Collectors.toList());
	}

	private double thresholdRelevance(Relevance relevance) {
		switch (relevance) {
		case HIGH:
			return 0.9d;
		case MEDIUM:
			return 0.7d;
		case LOW:
			return 0.4d;
		default:
			return 1d;
		}
	}

	private int transactionsContainingTokenInSet(String token) {
		if (tokensMap.containsKey(token)) {
			return tokensMap.get(token).size();
		}
		return 0;
	}

	private List<TokenTreeItem> treeItemize(DetailedTransaction transaction) {
		List<String> tokens = tokenizer.tokenize(transaction.getTitle());
		List<TokenTreeItem> treeItems = new ArrayList<>();

		MutableInt pos = new MutableInt(0);
		tokens.stream().forEach(token -> {
			Integer position = new Integer(pos.intValue());
			TokenTreeItem ti = new TokenTreeItem().setId(token + "-" + transaction.getId()).setTransaction(transaction)
					.setToken(token).setTokens(tokens).setOccurrencies(1)
					.setPositions(new ArrayList<>(Arrays.asList(position)));
			if (treeItems.contains(ti)) {
				TokenTreeItem exitingTi = treeItems.get(treeItems.indexOf(ti));
				exitingTi.setOccurrencies(exitingTi.getOccurrencies() + 1);
				exitingTi.getPositions().add(position);
			} else {
				treeItems.add(ti);
			}
			pos.increment();
		});

		return treeItems;
	}

	// private double getSimilarity(List<String> aTransactionTokens, List<String>
	// anotherTransactionTokens) {
	// return aTransactionTokens.stream().distinct().mapToDouble(token -> {
	// if (anotherTransactionTokens.contains(token)) {
	// return normalized_tfidf(token, aTransactionTokens) * normalized_tfidf(token,
	// anotherTransactionTokens);
	// } else {
	// return 0;
	// }
	// }).sum();
	// }
	//
	// private double tf(TokenTreeItem tokenTreeItem) {
	// return tf(tokenTreeItem.getOccurrencies(), tokenTreeItem.tokens.size());
	// }
	//
	// // n/N
	// // n = occurrencies if token in document
	// // N = tokens in documents
	// private double tf(int tokenOccurrencies, int tokensInTransactionTitle) {
	// return tokenOccurrencies / tokensInTransactionTitle;
	// }
	//
	// private double idf(TokenTreeItem tokenTreeItem) {
	// return idf(transactionSetSize, tokensMap.get(tokenTreeItem.token).size());
	// }
	//
	// // log(D/d)
	// // D = number of documents in set
	// // d = number of documents in set that contains token
	// private double idf(int transactionsSetSize, int transactionsWithToken) {
	// return Math.log10(transactionsSetSize / transactionsWithToken);
	// }
	//
	// private double tfidf(TokenTreeItem tokenTreeItem) {
	// return tf(tokenTreeItem) * idf(tokenTreeItem);
	// }
	//
	// private double tfidf(int tokenOccurrencies, int tokensInTransactionTitle, int
	// transactionsSetSize,
	// int transactionsWithToken) {
	// return tf(tokenOccurrencies, tokensInTransactionTitle) *
	// idf(transactionsSetSize, transactionsWithToken);
	// }
	//
	// private double normalized_tfidf(TokenTreeItem tokenTreeItem) {
	// return (tfidf(tokenTreeItem)
	// / Math.pow(tokenTreeItem.tokens.stream()
	// .mapToDouble(token ->
	// Math.pow(tfidf(Collections.frequency(tokenTreeItem.tokens, token),
	// tokenTreeItem.tokens.size(), transactionSetSize,
	// tokensMap.get(token).size()), 2))
	// .sum(), 0.5));
	// }
	//
	// private double normalized_tfidf(String atoken, List<String> tokens) {
	// return (tfidf(Collections.frequency(tokens, atoken), tokens.size(),
	// transactionSetSize,
	// tokensMap.get(atoken).size())
	// / Math.pow(
	// tokens.stream()
	// .mapToDouble(
	// token -> Math.pow(
	// tfidf(Collections.frequency(tokens, token), tokens.size(),
	// transactionSetSize, transactionsContainingTokenInSet(token)),
	// 2))
	// .sum(),
	// 0.5));
	// }

}
