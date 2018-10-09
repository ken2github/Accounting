//package store.io.readers;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Vector;
//import schema.Transaction;
//import store.IO;
//
//public class BPN_FormatReader  extends Parametric_FormatReader {
//
//	private static int BPN_FIELDS=7;
//	
//	/* (non-Javadoc)
//	 * @see store.io.readers.Base_FormatReader#loadSourceFile(java.io.File)
//	 */
//	@Override
//	public void loadSourceFile(File sourceFile) throws IOException {
//		String[] items =  sourceFile.getName().split("\\.");
//		double endBalance = Double.parseDouble(items[items.length-3]+"."+items[items.length-2]);
//		
//		
//		Vector<String[]> records=IO.readItems(sourceFile.getAbsolutePath(), BPN_FIELDS);		
//		Map<Integer, Double> deltaMap = new HashMap<>();
//		for (int i = 0; i < records.size(); i++) {
//			String[] record = records.get(i);
//			if(i>=1){
//				//  0                    1         2      3      4       5        6        
//				//Data Contabile	Data Valuta	Importo	Divisa	Causale	 Status	Canale
//				//09/04/2018	    09/04/2018	-8,43	EUR	    imposta di bollo cc e lr - da 01/01/2018 a 31/03/2018	Cont.	
//	
//				
//				int month = Integer.parseInt(record[0].split("/")[1]);
//				if(!transactionsMap.containsKey(month)){
//					transactionsMap.put(month, new Vector<>());
//				}
//				
//				//              0          1                 2               3                4               5
//				//public Transaction(String date,String amount, String amountDecimal, String title, String sector, String common) throws ParseException {
//
//				String[] normalizedRecord = new String[Transaction.FIELDS];
//				int year = Integer.parseInt(record[0].split("/")[2]);
//				int day = Integer.parseInt(record[0].split("/")[0]);
//				normalizedRecord[0]=year+"-"+month+"-"+day;		//date
//				
//				//	System.out.println("["+record[3]);
//					
//					String recString = (new DecimalFormat("##.00").format(Double.parseDouble(record[2].replace(",", "."))));
//					normalizedRecord[1]=recString.split(",")[0];	//amount
//					normalizedRecord[2]=recString.split(",")[1];	//amount decimal
//					if(!deltaMap.containsKey(month)) {
//						deltaMap.put(month, 0d);
//					}
//					deltaMap.put(month, deltaMap.get(month)+Double.parseDouble(record[2].replace(",", ".")));
//					
////					String removeQuotes = record[3].replace("\"", "");//.replace(",", "."); 
////					normalizedRecord[1]=removeQuotes.split(",")[0];
////					normalizedRecord[2]=(removeQuotes.split(",").length==1)?"00":removeQuotes.split(",")[1];
////					if(normalizedRecord[2].length()==1) {
////						normalizedRecord[2]=normalizedRecord[2]+"0";
////					}
////					if(!deltaMap.containsKey(month)) {
////						deltaMap.put(month, 0d);
////					}
////					deltaMap.put(month, deltaMap.get(month)+Double.parseDouble(removeQuotes.replace(",", ".")));
//									
//					normalizedRecord[3]=record[4];	//title
//					normalizedRecord[4]=record[5];	//sector
//					normalizedRecord[5]=record[6];	//common
//					transactionsMap.get(month).addElement(normalizedRecord);
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
//		
//		for (int i = 2; i < 3+items.length-7; i++) {
//			validationMap.put(Integer.parseInt(items[i]), true);
//		}
//		
//	}
//
//}
