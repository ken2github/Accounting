package restapi.transactionsoracle.service.analyser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import restapi.transactionsoracle.api.StringTokenizer;

public class BasicTokenizer implements StringTokenizer {

	private static String SEPARATOR = "<s>";
	private static String NON_WORD_REGEXP = "( - )|( e )|( : )|(, )|(\\[)|(\\])|([\\d]{2,4}[-.\\/]{1}[\\d]{2,4}([-\\/]{1}[\\d]{2,4})?)([:][\\d]{2,4})?|(\\s)";

	@Override
	public List<String> tokenize(String text) {
		return Arrays.asList(normalize(text).split(SEPARATOR)).stream().filter(token -> !token.equals(""))
				.collect(Collectors.toList());
	}

	private static String normalize(String text) {
		return text.replaceAll(NON_WORD_REGEXP, SEPARATOR).toLowerCase();
	}

}
