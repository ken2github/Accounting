package restapi.transactionsoracle.service.analyser;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import restapi.transactionsoracle.api.StringTokenizer;

public class BasicTokenizer implements StringTokenizer {

	/**
	 * 0 - Prepare text by removing FILE METADATA FORMATTERS and VERY COMMON WORDS;
	 * 
	 * 1 - Recognize GENERIC SEPARATORS;
	 * 
	 * 2 - Filter out non useful tokens such as :HOURs, DATEs
	 */

	// PREPARETORS
	// **********************************************************************************
	// METADATA FORMATTER
	private static final String METADATA_FILE_FORMATTER_REGEXP = "(- OTHER DATA -)|(\\[CATEGORIE)|(\\[CATEGORIA)|(\\[NOTA)|(\\[TIPO ADDEBITO)|(\\[STATUS)|(\\[DATA VALUTA)|(\\[DIVISA)|(\\[CANALE)";
	// ULTRA COMMON WORDS
	private static final String ULTRA_COMMON_WORDS_REGEXP = "( du )|( di )|( de )|( et )|( a )";
	// SEPARATORS
	// **********************************************************************************
	private static final String SEPARATOR_STUB = "<s>";
	// E REGEXP
	private static final String E_REGEXP = "( e )";
	// INTERPUNZIONE REGEXP
	private static final String INTERPUNZIONE_REGEXP = "( - )|( : )|(, )";
	// PARENTESI REGEXP
	private static final String PARENTESI_REGEXP = "(\\[)|(\\])|(\\()|(\\)])";
	// SPAZIO REGEXP
	private static final String SPAZI_REGEXP = "(\\s)";
	// ALL SEPARATOR REGEXP
	private static final String GENERIC_SEPARATOR_REGEXP = Arrays
			.asList(E_REGEXP, INTERPUNZIONE_REGEXP, PARENTESI_REGEXP, SPAZI_REGEXP).stream()
			.collect(Collectors.joining("|"));
	// FILTERS
	// **********************************************************************************
	// EMPTY FILTER
	private static final String EMPTY_REGEXP = "()";
	// Date Hour REGEXP FILTER
	private static final String HOUR_1_REGEXP = "([\\d]{1,2}[hH]{1}[\\d]{2})";
	private static final String HOUR_2_REGEXP = "([\\d]{6})";
	private static final String DATE_1_REGEXP = "([\\d]{2,4}[-.\\/]{1}[\\d]{2,4}([-\\/]{1}[\\d]{2,4})?)([:][\\d]{2,4})?";
	// ALL FILTERS
	private static final String FILTERS_REGEXP = Arrays
			.asList(EMPTY_REGEXP, HOUR_1_REGEXP, HOUR_2_REGEXP, DATE_1_REGEXP).stream().map(re -> "^" + re + "?")
			.collect(Collectors.joining("|"));
	private static final Pattern FILTERS_PATTERN = Pattern.compile(FILTERS_REGEXP);

	@Override
	public List<String> tokenize(String text) {
		return filter_out_tokens(split_in_tokens(remove_file_formatter_and_ultra_common_words_from_text(text)));
	}

	private static List<String> filter_out_tokens(List<String> tokens) {
		return tokens.stream().filter(token -> !FILTERS_PATTERN.matcher(token).matches()).collect(Collectors.toList());
	}

	private static List<String> split_in_tokens(String text) {
		return Arrays
				.asList(text.replaceAll(GENERIC_SEPARATOR_REGEXP, SEPARATOR_STUB).toLowerCase().split(SEPARATOR_STUB));
	}

	private static String remove_file_formatter_and_ultra_common_words_from_text(String text) {
		return text.replaceAll(METADATA_FILE_FORMATTER_REGEXP, " ").toLowerCase().replaceAll(ULTRA_COMMON_WORDS_REGEXP,
				" ");
	}

}
