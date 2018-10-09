//package store.io.readers;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.DecimalFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Vector;
//import schema.Transaction;
//import store.IO;
//import static store.io.Format.*;
//
//public class BNP_FormatReader extends Parametric_FormatReader {
//
//	protected String parseRawDateToNormalizedDate(String inputDate,String inputFormat,String outputFormat) throws ParseException {
//		Date date=new SimpleDateFormat(inputFormat).parse(inputDate);  
//	    DateFormat dateFormat = new SimpleDateFormat(outputFormat);
//	    return dateFormat.format(date);
//	}
//	
//	
//	
//	/* (non-Javadoc)
//	 * @see store.io.readers.Base_FormatReader#loadSourceFile(java.io.File)
//	 */
//	@Override
//	public void loadSourceFile(File sourceFile) throws IOException, ParseException {
//		String[] items =  sourceFile.getName().split(NORMALIZED_SEPARATOR_REGEXP);
//		double endBalance = Double.parseDouble(items[items.length-3]+"."+items[items.length-2]);		
//		Vector<String[]> records=IO.readItems(sourceFile.getAbsolutePath(), 6);		
//		Map<Integer, Double> deltaMap = new HashMap<>();
//		for (int i = 0; i < records.size(); i++) {
//			String[] record = records.get(i);
//			if(i>=3){
//				//  0         1          2                                               3   4     5
//				//06-03-2018,Non fini,FACTURE  DU 050818 GUSTO ANTIBES CARTE 4974XXXXXX,-22,sector,y
//				
//				String[] normalizedRecord = new String[Transaction.FIELDS];
//				normalizedRecord[0]=parseRawDateToNormalizedDate(record[0], "dd-MM-yyyy", "yyyy-MM-dd");	//date
//							    			    
//				int month = Integer.parseInt(record[0].split("-")[1]);
//				if(!transactionsMap.containsKey(month)){
//					transactionsMap.put(month, new Vector<>());
//				}
//				
//				//                            0          1                 2               3                4               5
//				//public Transaction(String date,String amount, String amountDecimal, String title, String sector, String common) throws ParseException {
//
//				int year = Integer.parseInt(record[0].split("-")[2]);
//				int day = Integer.parseInt(record[0].split("-")[0]);
//				normalizedRecord[0]=year+"-"+month+"-"+day;		//date
//				
//				String recString = (new DecimalFormat("##.00").format(Double.parseDouble(record[3].replace(",", "."))));
//				normalizedRecord[1]=recString.split(",")[0];	//amount
//				normalizedRecord[2]=recString.split(",")[1];	//amount decimal
//				if(!deltaMap.containsKey(month)) {
//					deltaMap.put(month, 0d);
//				}
//				deltaMap.put(month, deltaMap.get(month)+Double.parseDouble(record[3].replace(",", ".")));								
//				normalizedRecord[3]=record[2];	//title
//				normalizedRecord[4]=record[4];	//sector
//				normalizedRecord[5]=record[5];	//common
//				transactionsMap.get(month).addElement(normalizedRecord);
//			}
//		}
//		
//		List<Integer> keys = new ArrayList<Integer>(transactionsMap.keySet());
//		Collections.sort(keys);
//		Collections.reverse(keys);		
//		for (int j = 0; j < keys.size(); j++) {
//			validationMap.put(keys.get(j), false);
//			if(j==0) {
//				balancesMap.put(keys.get(j),endBalance);
//			}else {
//				balancesMap.put(keys.get(j), balancesMap.get(keys.get(j-1))-deltaMap.get(keys.get(j-1)));
//			}
//		}	
//		for (int i = 2; i < 3+items.length-7; i++) {
//			validationMap.put(Integer.parseInt(items[i]), true);
//		}
//	}
//	
//	/**
//	@Override
//	public void loadSourceFile(File sourceFile) throws IOException {
//		String[] items =  sourceFile.getName().split(NORMALIZED_SEPARATOR_REGEXP);
//		double endBalance = Double.parseDouble(items[items.length-3]+"."+items[items.length-2]);		
//		Vector<String[]> records=IO.readItems(sourceFile.getAbsolutePath(), 6);		
//		Map<Integer, Double> deltaMap = new HashMap<>();
//		for (int i = 0; i < records.size(); i++) {
//			String[] record = records.get(i);
//			if(i>=3){
//				//  0         1          2                                                      3   4     5
//				//06-03-2018,Non fini,FACTURE  DU 050818 GUSTO ANTIBES CARTE 4974XXXXXX,-22,sector,y
//				
//			    Date date=new SimpleDateFormat("dd-MM-yyyy").parse(record[0]);  
//			    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//			    String strDate = dateFormat.format(date);
//			    
//				int month = Integer.parseInt(record[0].split("-")[1]);
//				if(!transactionsMap.containsKey(month)){
//					transactionsMap.put(month, new Vector<>());
//				}
//				
//				//                            0          1                 2               3                4               5
//				//public Transaction(String date,String amount, String amountDecimal, String title, String sector, String common) throws ParseException {
//
//				String[] normalizedRecord = new String[Transaction.FIELDS];
//				int year = Integer.parseInt(record[0].split("-")[2]);
//				int day = Integer.parseInt(record[0].split("-")[0]);
//				normalizedRecord[0]=year+"-"+month+"-"+day;		//date
//				
//				String recString = (new DecimalFormat("##.00").format(Double.parseDouble(record[3].replace(",", "."))));
//				normalizedRecord[1]=recString.split(",")[0];	//amount
//				normalizedRecord[2]=recString.split(",")[1];	//amount decimal
//				if(!deltaMap.containsKey(month)) {
//					deltaMap.put(month, 0d);
//				}
//				deltaMap.put(month, deltaMap.get(month)+Double.parseDouble(record[3].replace(",", ".")));								
//				normalizedRecord[3]=record[2];	//title
//				normalizedRecord[4]=record[4];	//sector
//				normalizedRecord[5]=record[5];	//common
//				transactionsMap.get(month).addElement(normalizedRecord);
//			}
//		}
//		
//		List<Integer> keys = new ArrayList<Integer>(transactionsMap.keySet());
//		Collections.sort(keys);
//		Collections.reverse(keys);		
//		for (int j = 0; j < keys.size(); j++) {
//			validationMap.put(keys.get(j), false);
//			if(j==0) {
//				balancesMap.put(keys.get(j),endBalance);
//			}else {
//				balancesMap.put(keys.get(j), balancesMap.get(keys.get(j-1))-deltaMap.get(keys.get(j-1)));
//			}
//		}	
//		for (int i = 2; i < 3+items.length-7; i++) {
//			validationMap.put(Integer.parseInt(items[i]), true);
//		}
//	}
//	*/
//
//}
