package deprecated.indexes;

public class Utils {

	public static final String BALANCE = "YEAR";
	public static final String AVERAGE = "YEAR";
	
	public static final String YEAR = "YEAR";
	public static final String MONTH = "YEAR";
	public static final String COUNT = "YEAR";
	public static final String CATEGORY = "YEAR";
	public static final String SUBCATEGORY = "YEAR";
	public static final String COMMON = "YEAR";
	public static final String FLOW = "YEAR";
	
	public static final String INDEX_DELIMITER = "_";
	public static final String INDEX_DELIMITER_REGEXP = "_";
	
	
	public static String delimiterizing(String ... args){
		String result = "";
		for (String arg: args) {
			result+=arg+INDEX_DELIMITER;
		}
        return result.substring(0, result.length()-1);
	}
	
	public static String[] undelimiterizing(String index) {
		return index.split(INDEX_DELIMITER_REGEXP,-1);
	}
	
	public static String intToMonth(int month) {
		return (month>9)? ""+month:"0"+month; 
	}
}
