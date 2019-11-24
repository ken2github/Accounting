package restapi.bankprovisioninghelper.service.analyser;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import restapi.bankprovisioninghelper.api.SimilarityCalculator;

public class TFIDFCalculator implements SimilarityCalculator {

	@Override
	public double getSimilarity(List<String> aSentence, List<String> anotherSentence,
			Function<String, Integer> numberOfSentencesWithAToken, int numberOfSentences) {
		return aSentence.stream().distinct().mapToDouble(token -> {
			if (anotherSentence.contains(token)) {
				return normalized_tfidf(token, aSentence, numberOfSentencesWithAToken, numberOfSentences)
						* normalized_tfidf(token, anotherSentence, numberOfSentencesWithAToken, numberOfSentences);
			} else {
				return 0;
			}
		}).sum();
	}

	// n/N
	// n = occurrencies if token in document
	// N = tokens in documents
	private double tf(int tokenOccurrenciesInSentence, int tokensInSentence) {
		return ((double) tokenOccurrenciesInSentence) / ((double) tokensInSentence);
	}

	// log(D/d)
	// D = number of documents in set
	// d = number of documents in set that contains token
	private double idf(int numberOfSentences, int numberOfSentencesWithAToken) {
		return Math.log10(((double) numberOfSentences / (double) numberOfSentencesWithAToken));
	}

	private double tfidf(int tokenOccurrenciesInSentence, int tokensInSentence, int numberOfSentences,
			int numberOfSentencesWithAToken) {
		return tf(tokenOccurrenciesInSentence, tokensInSentence) * idf(numberOfSentences, numberOfSentencesWithAToken);
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

		return (tfidf(
				Collections.frequency(tokens, atoken), tokens.size(), numberOfSentences, numberOfSentencesWithAToken
						.apply(atoken))
				/ Math.pow(
						tokens.stream()
								.mapToDouble(
										token -> Math.pow(
												tfidf(Collections.frequency(tokens, token), tokens.size(),
														numberOfSentences, numberOfSentencesWithAToken.apply(token)),
												2))
								.sum(),
						0.5));
	}
}
