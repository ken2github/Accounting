package restapi.bankprovisioninghelper.service.analyser;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.BeforeClass;
import org.junit.Test;

import restapi.transactionsoracle.api.StringTokenizer;
import restapi.transactionsoracle.service.analyser.BasicTokenizer;
import restapi.transactionsoracle.service.analyser.TFIDFCalculator2;

public class TFIDFCalculatorTests2 {

	public static Map<String, Set<String>> tokensMap = new TreeMap<String, Set<String>>();
	public static List<String> sentences;
	public static StringTokenizer tokenizer;

	@BeforeClass
	public static void init() {
		tokenizer = new BasicTokenizer();
		sentences = Arrays.asList("a b c d", "a f g k", "r b g d", "k l g n");

		sentences.stream().forEach(s -> {
			List<String> tokens = tokenizer.tokenize(s);
			tokens.stream().forEach(t -> {
				if (!tokensMap.containsKey(t)) {
					tokensMap.put(t, new HashSet<String>());
				}
				tokensMap.get(t).add(s);
			});
		});
	}

	@Test
	public void similarityOkForFirstTwoSentences() {
		TFIDFCalculator2 tfidf = new TFIDFCalculator2();

		double actualSimilarity = tfidf.getSimilarity(tokenizer.tokenize(sentences.get(0)),
				tokenizer.tokenize(sentences.get(1)), (String s) -> {
					return tokensMap.get(s).size();
				}, sentences.size());

		System.out.println(actualSimilarity);

		assertEquals(0.163, actualSimilarity, 0.015d);
	}

}
