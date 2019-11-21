package restapi.bankfileconverter.api;

public class FileNamePatternConstants {

	public static final String SEPARATOR = "_";
	public static final String COUNT = "count";
	public static final String TO = "to";
	public static final String FROM = "from";
	public static final String BALANCE = "balance";
	public static final String BALANCE_SEPARATOR = "_"; // It must be a character that can be put simply in regexp []
														// paranthesis

	public static final String COUNT_REGEXP_PART = COUNT + SEPARATOR + "(?<" + COUNT + ">[\\w\\d]*)";
	public static final String FROM_REGEXP_PART = FROM + SEPARATOR + "(?<" + FROM + ">[\\d]{8})";
	public static final String TO_REGEXP_PART = TO + SEPARATOR + "(?<" + TO + ">[\\d]{8})";
	public static final String BALANCE_REGEXP_PART = BALANCE + SEPARATOR + "(?<" + BALANCE + ">[-]?[\\d]*["
			+ BALANCE_SEPARATOR + "][\\d]{2})";

	public static final String EXTENSION_REGEXP_PART = "[.]\\w*";

	public static final String FILENAME_WITHOUT_EXTENSION_PATTERN_REGEXP = COUNT_REGEXP_PART + SEPARATOR
			+ FROM_REGEXP_PART + SEPARATOR + TO_REGEXP_PART + SEPARATOR + BALANCE_REGEXP_PART;

	public static final String FILENAME_WITH_EXTENSION_PATTERN_REGEXP = COUNT_REGEXP_PART + SEPARATOR + FROM_REGEXP_PART
			+ SEPARATOR + TO_REGEXP_PART + SEPARATOR + BALANCE_REGEXP_PART + EXTENSION_REGEXP_PART;

}
