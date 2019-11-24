package restapi.bankprovisioninghelper.service.analyser;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class BasicTokenizerTests {

	@Test
	public void tokenizeOk() {
		BasicTokenizer bt = new BasicTokenizer();
		List<String> tokens = bt.tokenize(
				"BOULANGERIE GOLF - 200 AV e ROUMANILLE  BIOT TECHNOLOGIES, Chargement pagamento internet - carta*5842-10:46-amende cede x fra DU 25/09 A PARIS da 01/01/2019 a 31/03/2019");

		// MutableInt count = new MutableInt(0);
		// tokens.stream().forEach(t -> {
		// System.out.println(count + "[" + t + "]");
		// count.increment();
		// });

		List<String> expectedTokens = Arrays
				.asList("BOULANGERIE", "GOLF", "200", "AV", "ROUMANILLE", "BIOT", "TECHNOLOGIES", "Chargement",
						"pagamento", "internet", "carta*", "-amende", "cede", "x", "fra", "DU", "A", "PARIS", "da", "a")
				.stream().map(token -> token.toLowerCase()).collect(Collectors.toList());

		// count.setValue(0);
		// ;
		// expectedTokens.stream().forEach(t -> {
		// System.out.println(count + "[" + t + "]");
		// count.increment();
		// });

		assertEquals(expectedTokens.size(), tokens.size());
		for (int i = 0; i < expectedTokens.size(); i++) {
			assertEquals(expectedTokens.get(i), tokens.get(i));
		}
	}

}
