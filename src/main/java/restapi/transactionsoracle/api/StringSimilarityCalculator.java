package restapi.transactionsoracle.api;

import java.util.List;
import java.util.function.Function;

public interface StringSimilarityCalculator {

	List<Double> getSimilarities(List<String> aSentence, List<List<String>> otherSentences,
			Function<String, Integer> numberOfSentencesWithAToken, int numberOfSentences);

	double getSimilarity(List<String> aSentence, List<String> anotherSentence,
			Function<String, Integer> numberOfSentencesWithAToken, int numberOfSentences);

}
