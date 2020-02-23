package restapi.transactionsoracle.service.analyser;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import restapi.transactionsoracle.api.StringSimilarityCalculator;

public class TFIDFCalculator2 implements StringSimilarityCalculator {

	/**
	 * This TFIDF uses - do not use LOG to compute TF and IDF - use each LOG for
	 * when doing product TF*IDF - optimize performance
	 */

	@Override
	public double getSimilarity(List<String> aSentence, List<String> anotherSentence,
			Function<String, Integer> numberOfSentencesWithAToken, int numberOfSentences) {
		return (aSentence.stream().distinct().mapToDouble(token -> {
			if (anotherSentence.contains(token)) {
				return log10_tfidf(token, aSentence, numberOfSentencesWithAToken, numberOfSentences)
						* log10_tfidf(token, anotherSentence, numberOfSentencesWithAToken, numberOfSentences);
			} else {
				return 0;
			}
		}).sum()) / (tfidf_normalizer(aSentence, numberOfSentencesWithAToken, numberOfSentences)
				* tfidf_normalizer(anotherSentence, numberOfSentencesWithAToken, numberOfSentences));
	}

	// n/N
	// n = occurrencies if token in document
	// N = tokens in documents
	private double tf(int tokenOccurrenciesInSentence, int tokensInSentence) {
		return (((double) tokenOccurrenciesInSentence) / ((double) tokensInSentence)) + 1;
	}

	// log(D/d)
	// D = number of documents in set
	// d = number of documents in set that contains token
	private double idf(int numberOfSentences, int numberOfSentencesWithAToken) {
		return (((double) numberOfSentences / (double) numberOfSentencesWithAToken));
	}

	private double log10_tfidf(int tokenOccurrenciesInSentence, int tokensInSentence, int numberOfSentences,
			int numberOfSentencesWithAToken) {
		return Math.log10(tf(tokenOccurrenciesInSentence, tokensInSentence))
				* Math.log10(idf(numberOfSentences, numberOfSentencesWithAToken));
	}

	private double normalized_tfidf(String atoken, List<String> tokens,
			Function<String, Integer> numberOfSentencesWithAToken, int numberOfSentences) {
		// System.out.println(
		// "[NORMALIZED-TFIDF]:{" + atoken + "},{" +
		// tokens.stream().collect(Collectors.joining(",")) + "}");
		// System.out.println(" [TFIDF]:" + (tfidf(Collections.frequency(tokens,
		// atoken), tokens.size(), numberOfSentences,
		// numberOfSentencesWithAToken.apply(atoken))));
		// System.out.println();
		// System.out.println();
		// System.out.println();

		return (log10_tfidf(Collections.frequency(tokens, atoken), tokens.size(), numberOfSentences,
				numberOfSentencesWithAToken.apply(atoken))
				/ tfidf_normalizer(tokens, numberOfSentencesWithAToken, numberOfSentences));
	}

	private double log10_tfidf(String atoken, List<String> tokens,
			Function<String, Integer> numberOfSentencesWithAToken, int numberOfSentences) {
		return log10_tfidf(Collections.frequency(tokens, atoken), tokens.size(), numberOfSentences,
				numberOfSentencesWithAToken.apply(atoken));
	}

	private double tfidf_normalizer(List<String> tokens, Function<String, Integer> numberOfSentencesWithAToken,
			int numberOfSentences) {
		return Math.pow(tokens.stream().mapToDouble(token -> Math.pow(log10_tfidf(Collections.frequency(tokens, token),
				tokens.size(), numberOfSentences, numberOfSentencesWithAToken.apply(token)), 2)).sum(), 0.5);
	}

	@Override
	public List<Double> getSimilarities(List<String> aSentence, List<List<String>> otherSentences,
			Function<String, Integer> numberOfSentencesWithAToken, int numberOfSentences) {
		// TODO Auto-generated method stub
		return null;
	}
}
