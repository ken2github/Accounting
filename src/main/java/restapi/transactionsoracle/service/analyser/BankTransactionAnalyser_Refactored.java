package restapi.transactionsoracle.service.analyser;

import static java.lang.Double.compare;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

		System.out.println("REFACTO2");
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
		System.out.println("REFACTO");
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

		System.out.println("transactionsHavingAtLeastOneCommonToken=" + transactionsHavingAtLeastOneCommonToken.size());

		List<String> aSentence = transactionTokens;
		List<List<String>> otherSentences = transactionsHavingAtLeastOneCommonToken.stream().map(tti -> tti.tokens)
				.collect(Collectors.toList());
		System.out.println(otherSentences.size());
		Function<String, Integer> numberOfSentencesWithAToken = (String a) -> {
			return cache.transactionsContainingTokenInSet(a, getAlsoTransactionWithNullSector,
					getAlsoTransactionWithNullIsCommon);
		};
		int numberOfSentences = cache.getTransactionSetSize();
		List<Double> similarities = similarityCalculator.getSimilarities(aSentence, otherSentences,
				numberOfSentencesWithAToken, numberOfSentences);

		System.out.println(similarities.size());

		List<TransactionWeight_1> weightedTransactionsHavingAtLeastOneCommonToken = IntStream
				.range(0, similarities.size()).boxed()
				.map(pos -> new TransactionWeight_1().setWeight(similarities.get(pos))
						.setTransaction(transactionsHavingAtLeastOneCommonToken.get(pos).getTransaction()))
				.sorted((wt1, wt2) -> compare(wt2.getWeight(), wt1.getWeight())).collect(toList());

		System.out.println(weightedTransactionsHavingAtLeastOneCommonToken.size());
		weightedTransactionsHavingAtLeastOneCommonToken
				.forEach(wt -> System.out.println(wt.transaction.getTitle() + " ->" + wt.weight));

		// List<TransactionWeight_1> weightedTransactionsHavingAtLeastOneCommonToken =
		// similarities.stream()
		// .map(similarity -> new TransactionWeight_1().setWeight(similarity)
		// .setTransaction(transactionsHavingAtLeastOneCommonToken.get(position.).getTransaction()))
		// .sorted((wt1, wt2) -> Double.compare(wt2.getWeight(),
		// wt1.getWeight())).collect(Collectors.toList());

		// final int[] idx = { 0 };
		// List<TransactionWeight_1> weightedTransactionsHavingAtLeastOneCommonToken =
		// similarities.stream()
		// .map(similarity -> new TransactionWeight_1().setWeight(similarity)
		// .setTransaction(transactionsHavingAtLeastOneCommonToken.get(idx[0]++).getTransaction()))
		// .sorted((wt1, wt2) -> Double.compare(wt2.getWeight(),
		// wt1.getWeight())).collect(Collectors.toList());

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
