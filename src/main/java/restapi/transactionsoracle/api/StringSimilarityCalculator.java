package restapi.transactionsoracle.api;

import java.util.List;
import java.util.function.Function;

public interface StringSimilarityCalculator {

	double getSimilarity(List<String> aSentence, List<String> anotherSentence,
			Function<String, Integer> numberOfSentencesWithAToken, int numberOfSentences);

}
