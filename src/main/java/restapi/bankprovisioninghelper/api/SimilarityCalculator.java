package restapi.bankprovisioninghelper.api;

import java.util.List;
import java.util.function.Function;

public interface SimilarityCalculator {

	double getSimilarity(List<String> aSentence, List<String> anotherSentence,
			Function<String, Integer> numberOfSentencesWithAToken, int numberOfSentences);

}
