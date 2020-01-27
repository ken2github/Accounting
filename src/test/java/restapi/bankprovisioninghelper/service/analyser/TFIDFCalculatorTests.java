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
import restapi.transactionsoracle.service.analyser.TFIDFCalculator;

public class TFIDFCalculatorTests {

	public static Map<String, Set<String>> tokensMap = new TreeMap<String, Set<String>>();
	public static List<String> sentences;
	public static StringTokenizer tokenizer;

	@BeforeClass
	public static void init() {
		tokenizer = new BasicTokenizer();
		sentences = Arrays.asList("pagamento internet - carta*5842-12:21-mgp*leetchi.com0355938 paris fra",
				"PAIEMENT CB CARREFOUR (FRANCE) DU 25/10", "PAIEMENT CB SATORIZ VALLAUR DU 23/10 A VALLAURIS",
				"PAIEMENT CB CARREFOUR (FRANCE) DU 18/10", "CARREFOUR TPE - CHEMIN DE SAINT CLAUDE  ANTIBES 06600",
				"TABLE DES INDES ANTIBES, Commerçant non autorisé",
				"SODEXO FR600881 - 790 AV DU DOCTEUR MAURICE DONAT  MOUGINS 06250",
				"pagamento internet - carta*5842-21:37-easyjet000ewmr2l4 luton, beds gbr",
				"pagamento internet - carta*5842-00:00-amzn mktp fr amazon.fr lux",
				"pagamento internet - carta*5842-12:21-mgp*leetchi.com0355938 paris fra",
				"pagamento internet - carta*5842-15:36-mgp*leetchi.com0355938 paris fra");

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
	public void similarityOkForSame() {
		TFIDFCalculator tfidf = new TFIDFCalculator();

		double actualSimilarity = tfidf.getSimilarity(tokenizer.tokenize(sentences.get(0)),
				tokenizer.tokenize(sentences.get(0)), (String s) -> {
					return tokensMap.get(s).size();
				}, sentences.size());

		System.out.println(actualSimilarity);

		assertEquals(1, actualSimilarity, 0.1d);
	}

	@Test
	public void similarityZeroForDifferent() {
		TFIDFCalculator tfidf = new TFIDFCalculator();

		double actualSimilarity = tfidf.getSimilarity(tokenizer.tokenize(sentences.get(0)),
				tokenizer.tokenize(sentences.get(3)), (String s) -> {
					return tokensMap.get(s).size();
				}, sentences.size());

		System.out.println(actualSimilarity);

		assertEquals(0, actualSimilarity, 0.1d);
	}

	@Test
	public void commutatovityOK() {
		TFIDFCalculator tfidf = new TFIDFCalculator();

		assertEquals(tfidf.getSimilarity(tokenizer.tokenize(sentences.get(0)), tokenizer.tokenize(sentences.get(3)),
				(String s) -> {
					return tokensMap.get(s).size();
				}, sentences.size()), tfidf.getSimilarity(tokenizer.tokenize(sentences.get(3)),
						tokenizer.tokenize(sentences.get(0)), (String s) -> {
							return tokensMap.get(s).size();
						}, sentences.size()),
				0.1d);
	}

	// @Test
	// public void similarityHighForSimilar() {
	// TFIDFCalculator tfidf = new TFIDFCalculator();
	//
	// MutableInt count = new MutableInt(1);
	// sentences.stream().forEach(ss -> {
	// for (int i = count.intValue(); i < sentences.size(); i++) {
	// System.out.println(ss);
	// System.out.println(sentences.get(i));
	// System.out.println(tfidf.getSimilarity(tokenizer.tokenize(ss),
	// tokenizer.tokenize(sentences.get(i)),
	// (String s) -> {
	// return tokensMap.get(s).size();
	// }, sentences.size()));
	// }
	// count.increment();
	// });
	//
	// }

	@Test
	public void similarityLowForLowSimilar() {
		TFIDFCalculator tfidf = new TFIDFCalculator();

		double actualSimilarity = tfidf.getSimilarity(tokenizer.tokenize(sentences.get(1)),
				tokenizer.tokenize(sentences.get(2)), (String s) -> {
					return tokensMap.get(s).size();
				}, sentences.size());

		System.out.println(actualSimilarity);

		assertEquals(0.2, actualSimilarity, 0.1d);
	}

}
