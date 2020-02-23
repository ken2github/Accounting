package restapi.transactionsoracle.service.analyser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import model2.Transaction;
import restapi.transactionsoracle.api.Cacher_1;
import restapi.transactionsoracle.api.SimilarityRelevance;
import restapi.transactionsoracle.api.StringSimilarityCalculator;
import restapi.transactionsoracle.api.StringTokenizer;
import restapi.transactionsoracle.service.TransactionsOracle;

public class BankTransactionAnalyser_Refactored implements TransactionsOracle {

	@Autowired
	private StringTokenizer tokenizer;

	@Autowired
	private StringSimilarityCalculator similarityCalculator;

	@Autowired
	private Cacher_1 cache;

	public BankTransactionAnalyser_Refactored() {
	}

	@Autowired
	public BankTransactionAnalyser_Refactored(StringTokenizer tokenizer,
			StringSimilarityCalculator similarityCalculator, Cacher_1 cache) {
		this.tokenizer = tokenizer;
		this.similarityCalculator = similarityCalculator;
		this.cache = cache;
	}

	@Override
	synchronized public List<String> suggestSectorsForTransaction(Transaction transaction, int maxResults,
			SimilarityRelevance relevance) {
		// ((Cache_ManuelRefreshImpl) cache).refreshInternalMapIfNeeded(false, true);

		List<TransactionWeight_1> weightedTransactionsHavingAtLeastOneCommonToken = getWeightedTransactionsHavingAtLeastOneCommonToken(
				transaction, relevance, false, true);

		return weightedTransactionsHavingAtLeastOneCommonToken.stream()
				.filter(wt -> wt.getWeight() > relevance.toDouble()).map(wt -> wt.getTransaction().getSectorName())
				.distinct().limit(maxResults).collect(Collectors.toList());

	}

	@Override
	synchronized public Optional<Boolean> suggestIsCommonForTransaction(Transaction transaction,
			SimilarityRelevance relevance) {
		// ((Cache_ManuelRefreshImpl) this.cache).refreshInternalMapIfNeeded(true,
		// false);

		List<TransactionWeight_1> weightedTransactionsHavingAtLeastOneCommonToken = getWeightedTransactionsHavingAtLeastOneCommonToken(
				transaction, relevance, true, false);

		return weightedTransactionsHavingAtLeastOneCommonToken.stream()
				.filter(wt -> wt.getWeight() > relevance.toDouble()).map(wt -> wt.getTransaction().isCommon())
				.findFirst();
	}

	private List<TransactionWeight_1> getWeightedTransactionsHavingAtLeastOneCommonToken(Transaction transaction,
			SimilarityRelevance relevance, boolean getAlsoTransactionWithNullSector,
			boolean getAlsoTransactionWithNullIsCommon) {
		List<String> transactionTokens = tokenizer.tokenize(transaction.getTitle());

		List<TokenTreeItem_1> transactionsHavingAtLeastOneCommonToken = cache
				.getTransactionsHavingAtLeastOneCommonToken(transactionTokens, getAlsoTransactionWithNullSector,
						getAlsoTransactionWithNullIsCommon);

		List<Double> similarities = similarityCalculator.getSimilarities(transactionTokens,
				transactionsHavingAtLeastOneCommonToken.stream().map(tti -> tti.tokens).collect(Collectors.toList()),
				(String a) -> {
					return cache.transactionsContainingTokenInSet(a, getAlsoTransactionWithNullSector,
							getAlsoTransactionWithNullIsCommon);
				}, cache.getTransactionSetSize());

		final int[] idx = { 0 };
		List<TransactionWeight_1> weightedTransactionsHavingAtLeastOneCommonToken = similarities.stream()
				.map(similarity -> new TransactionWeight_1().setWeight(similarity)
						.setTransaction(transactionsHavingAtLeastOneCommonToken.get(idx[0]++).getTransaction()))
				.sorted((wt1, wt2) -> Double.compare(wt2.getWeight(), wt1.getWeight())).collect(Collectors.toList());

		// List<TransactionWeight_1> weightedTransactionsHavingAtLeastOneCommonToken =
		// transactionsHavingAtLeastOneCommonToken
		// .stream().
		// .map(treeItem -> new
		// TransactionWeight_1().setTransaction(treeItem.getTransaction())
		// .setWeight(similarityCalculator.getSimilarity(transactionTokens,
		// treeItem.tokens,
		// cache::transactionsContainingTokenInSet, cache.getTransactionSetSize())))
		// .sorted((wt1, wt2) -> Double.compare(wt2.getWeight(),
		// wt1.getWeight())).collect(Collectors.toList());

		return weightedTransactionsHavingAtLeastOneCommonToken;
	}

}
