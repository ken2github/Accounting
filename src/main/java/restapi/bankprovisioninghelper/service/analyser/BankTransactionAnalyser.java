package restapi.bankprovisioninghelper.service.analyser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import model2.DetailedTransaction;
import model2.Transaction;
import restapi.bankprovisioninghelper.api.Relevance;
import restapi.bankprovisioninghelper.api.SimilarityCalculator;
import restapi.bankprovisioninghelper.api.Tokenizer;
import restapi.bankprovisioninghelper.service.BankTransactionAnalytics;

public class BankTransactionAnalyser implements BankTransactionAnalytics {

	@Autowired
	private Tokenizer tokenizer;

	@Autowired
	private SimilarityCalculator similarityCalculator;

	@Autowired
	public BankTransactionAnalyser(Tokenizer tokenizer, SimilarityCalculator similarityCalculator) {
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

	private Map<String, List<TokenTreeItem>> tokensMap = new TreeMap<>();
	private int transactionSetSize;

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
	public void initTokensMap(List<DetailedTransaction> transactions) {
		transactionSetSize = transactions.size();
		transactions.stream().map(transaction -> treeItemize(transaction)).forEach(treeItem -> {
			treeItem.getTokens().stream().distinct().forEach(token -> {
				if (!tokensMap.containsKey(token)) {
					tokensMap.put(token, new ArrayList<BankTransactionAnalyser.TokenTreeItem>());
				}
				tokensMap.get(token).add(treeItem);
			});
		});
	}

	@Override
	public List<String> suggestSectorsForTransaction(Transaction transaction, int maxResults, Relevance relevance) {
		List<TokenTreeItem> transactionsHavingAtLeastOneCommonToken = tokenizer.tokenize(transaction.getTitle())
				.stream().distinct().filter(token -> tokensMap.containsKey(token)).map(token -> tokensMap.get(token))
				.flatMap(treeItems -> treeItems.stream()).distinct().collect(Collectors.toList());

		List<TransactionWeight> weightedTransactionsHavingAtLeastOneCommonToken = transactionsHavingAtLeastOneCommonToken
				.stream()
				.map(treeItem -> new TransactionWeight().setTransaction(treeItem.getTransaction())
						.setWeight(similarityCalculator.getSimilarity(tokenizer.tokenize(transaction.getTitle()),
								treeItem.tokens, this::transactionsContainingTokenInSet, transactionSetSize)))
				.sorted((wt1, wt2) -> Double.compare(wt1.getWeight(), wt2.getWeight())).collect(Collectors.toList());

		return weightedTransactionsHavingAtLeastOneCommonToken.stream()
				.filter(wt -> wt.getWeight() > thresholdRelevance(relevance))
				.map(wt -> wt.getTransaction().getSectorName()).distinct().limit(maxResults)
				.collect(Collectors.toList());
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

	private TokenTreeItem treeItemize(DetailedTransaction transaction) {
		return new TokenTreeItem(transaction, tokenizer.tokenize(transaction.getTitle()), transaction.getId());
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
