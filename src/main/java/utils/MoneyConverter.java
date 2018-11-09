package utils;

public class MoneyConverter {
	
	public static final String COMMA_SEPARATOR = ",";
	public static final String COMMA_SEPARATOR_REGEXP = "[,]";
	public static final String DOT_SEPARATOR = ".";
	public static final String DOT_SEPARATOR_REGEXP = "[.]";
	
	public static final String NORMALIZED_SEPARATOR = COMMA_SEPARATOR;
	public static final String NORMALIZED_SEPARATOR_REGEXP = COMMA_SEPARATOR_REGEXP;
	
	public static long parseDecimalStringToLong2(String decimalString) {
		decimalString=decimalString.replace(COMMA_SEPARATOR,DOT_SEPARATOR);
		return (long)(Double.parseDouble(decimalString)*100);
		
		
		
		
		
//		long result = 0;
//		decimalString=decimalString.replace(COMMA_SEPARATOR,NORMALIZED_SEPARATOR);
//		decimalString=decimalString.replace(DOT_SEPARATOR,NORMALIZED_SEPARATOR);
//		if(!decimalString.contains(NORMALIZED_SEPARATOR)) {
//			decimalString = decimalString+NORMALIZED_SEPARATOR+"0"; 
//		}else if(decimalString.indexOf(NORMALIZED_SEPARATOR)+2==decimalString.length()) {
//			decimalString = decimalString+"0"; 
//		} 
//		String[] parts = decimalString.split(NORMALIZED_SEPARATOR);
//		
//		int sign = 1;
//		if(parts[0].startsWith("-")){
//			sign=-1;
//		}
//		parts[0]=parts[0].replace("-", "");
//		result = (Long.parseLong(parts[0])*100 + Long.parseLong(parts[1]));
//		
//		return sign*result;
	}
	
	
	public static long parseDecimalStringToLong(String decimalString) {
//		System.out.println(decimalString);
		long result = 0;
		long unsignedResult = 0;
		
		long sign = 1;
		if(decimalString.startsWith("-")) {
			decimalString = decimalString.replace("-", "");
			sign = -1;
		}
		
		
		decimalString=decimalString.replace(COMMA_SEPARATOR,NORMALIZED_SEPARATOR);
		decimalString=decimalString.replace(DOT_SEPARATOR,NORMALIZED_SEPARATOR);
//		if(!decimalString.contains(NORMALIZED_SEPARATOR)) {
//			parts = new String[] {decimalString,"0"};
//		}else 
		String[] parts= new String[] {decimalString,"0"};
		if(decimalString.contains(NORMALIZED_SEPARATOR)) {
			parts = decimalString.split(NORMALIZED_SEPARATOR);
			if(parts[0].length()==0) {
				parts[0]="0";
			}
			if(parts[1].length()==1) {
				parts[1]=parts[1]+"0";
			}
		} 
				
		unsignedResult = (Long.parseLong(parts[0])*100 + Long.parseLong(parts[1]));
		
		result=sign*unsignedResult; 
		
		return result;
	}
	
	public static long parseDecimalStringToLong(String unitString,String fractionalString) {
		if(fractionalString.equals("")) {fractionalString="0";}
		if(unitString.equals("")) {unitString="0";}
		if(unitString.equals("-")) {unitString="-0";}
		return parseDecimalStringToLong(unitString+NORMALIZED_SEPARATOR+fractionalString);
	}
	
	public static String parseLongToNormalizedDecimalString(long l) {		
		long unsigned = (l<0)?-l:l;
		return 
			((l<0)?"-":"")+ 
			String.valueOf((unsigned-(unsigned%100))/100)+
			NORMALIZED_SEPARATOR+
			(((unsigned%100)>9)?"":"0")+
			String.valueOf(unsigned%100);
	}
	
}
