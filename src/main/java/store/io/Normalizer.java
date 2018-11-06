package store.io;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Vector;
import static store.io.Format.*;
import store.IO;

public class Normalizer {

	public static void directoryNormalize(File directoryToNormalize,File directoryOfNormalizedFiles) throws Exception {
		// directlry should be a year directory
		// search for files that starts by COUNT.YEAR.MONTH.MONTH.BALANCE.AMOUNT.AMOUNTDECIMAL.xls
		// months=items-3-(3 con balance)		
		for (File fileToNormalize : directoryToNormalize.listFiles()) {
			if(!(fileToNormalize.getName().endsWith("counts."+DB_EXTENSION) || fileToNormalize.getName().endsWith("sectors."+DB_EXTENSION))) {
				multiFileNormalize(fileToNormalize, directoryOfNormalizedFiles);
			}			
		}		
	}
	
	public static void multiFileNormalize(File fileToNormalize,File directoryOfNormalizedFiles) throws Exception {
		// file must bei
		// read for count, year, months
		String[] items = fileToNormalize.getName().split(RAW_SEPARATOR_REGEXP);
		//System.out.println(fileToNormalize.getName());
		String count = items[0];
		int year = Integer.parseInt(items[1]);
		int months = items.length-6; // with balance
				
		// identify format file
		// each count has a FILE FORMAT associated
				
		// FormatReader reader = getFormatReader(count);
		// 
		FormatReader reader = FormatMapper.getFormatReader(count);
		reader.loadSourceFile(fileToNormalize);
		
		// for each month create normalized file		
		// read write cycle
		for (int i = 0; i < months; i++) {
			// reader.getNormalizedRecord();			
			// write in corresponding file
			int month = Integer.parseInt(items[2+i]);			
			double monthBalance = reader.getBalance(month);
			String mbString = (new DecimalFormat("##.00").format(monthBalance));
			//System.out.println("{"+mbString+"}");
			String balanceAmount=mbString.split(",")[0];
			String balanceAmountDecimal=mbString.split(",")[1];				
			String name = 
					year+NORMALIZED_SEPARATOR+
					new DecimalFormat("00").format(month)+NORMALIZED_SEPARATOR+
					(count.toUpperCase())+NORMALIZED_SEPARATOR+
					balanceAmount+NORMALIZED_SEPARATOR+
					balanceAmountDecimal+"."+DB_EXTENSION;
			String pathname = directoryOfNormalizedFiles.getAbsolutePath()+"\\"+name;			
			//files[i] = new File(pathname);			
			Vector<String[]> records = reader.readRecords(month);
			IO.writeItems(pathname, null, records);			
		}
	}
	
}
