package store.io.readers;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import model.schema.Transaction;
import store.IO;
import store.io.FormatReader;
import store.io.RecordMapper;

public class Parametric_FormatReader implements FormatReader {

	protected Map<Integer,Vector<String[]>> transactionsMap = new HashMap<Integer, Vector<String[]>>();
	protected Map<Integer, Double> balancesMap = new HashMap<>();
	protected Map<Integer, Boolean> validationMap = new HashMap<>();	
	private RecordMapper recordMapper;
		
	public Parametric_FormatReader(RecordMapper recordMapper) {
		this.recordMapper = recordMapper;
	}
		
	@Override
	public void loadSourceFile(File sourceFile) throws Exception {
		String[] items =  sourceFile.getName().split(NORMALIZED_SEPARATOR_REGEXP);
		double endBalance = Double.parseDouble(items[items.length-3]+"."+items[items.length-2]);		
		Vector<String[]> records=IO.readItems(sourceFile.getAbsolutePath(), recordMapper.getFields());		
		Map<Integer, Double> deltaMap = new HashMap<>();
		for (int i = 0; i < records.size(); i++) {
			String[] record = records.get(i);
			if(i>=recordMapper.getFirstValidRecord()){				
				String[] normalizedRecord = new String[Transaction.FIELDS];
				
				Date date = new SimpleDateFormat(recordMapper.getDateFormat()).parse(record[recordMapper.getDateField()]);
				double amount = Double.parseDouble(record[recordMapper.getAmountField()].replace(",", "."));
				String recString = (new DecimalFormat("##.00").format(amount));

				normalizedRecord[0]=(new SimpleDateFormat(Transaction.DATE_FORMAT)).format(date);	//date
				normalizedRecord[1]=recString.split(",")[0];										//amount
				normalizedRecord[2]=recString.split(",")[1];										//amount decimal
				normalizedRecord[3]=record[recordMapper.getTitleField()];							//title
				normalizedRecord[4]=record[recordMapper.getCategoryField()];						//sector
				normalizedRecord[5]=record[recordMapper.getCommonField()];							//common
				
				@SuppressWarnings("deprecation")
				
				int month = date.getMonth()+1;
				//System.out.println("ADD ["+month+"] FOR "+date.toString()+ " BY "+record[recordMapper.getDateField()]);
				
				if(!transactionsMap.containsKey(month)){transactionsMap.put(month, new Vector<>());}
				transactionsMap.get(month).addElement(normalizedRecord);	
								
				if(!deltaMap.containsKey(month)) {deltaMap.put(month, 0d);}				
				deltaMap.put(month, deltaMap.get(month)+amount);				
			}
		}
		
		//Add skipped month (if any)
		List<Integer> keys = new ArrayList<Integer>(transactionsMap.keySet());
		Collections.sort(keys);
		
		List<Integer> missingKeys = new ArrayList<Integer>();
		for (int i = 1; i < keys.size(); i++) {
			int month = keys.get(i);
			if(keys.get(i-1)+1 < month) {
				for (int missingKey = keys.get(i-1)+1; missingKey < month; missingKey++) {
					missingKeys.add(missingKey);
					System.out.println("Addedkey!!!!!!!!!!!!!!!! - ["+missingKey+"]");
				}				
			}						
		}
		
		for (Integer missingMonth : missingKeys) {
			if(!transactionsMap.containsKey(missingMonth)){transactionsMap.put(missingMonth, new Vector<>());}							
			if(!deltaMap.containsKey(missingMonth)) {deltaMap.put(missingMonth, 0d);}	
		}
		
		keys = new ArrayList<Integer>(transactionsMap.keySet());
		Collections.sort(keys);
		Collections.reverse(keys);		
		for (int j = 0; j < keys.size(); j++) {
			validationMap.put(keys.get(j), false);
			if(j==0) {
				balancesMap.put(keys.get(j),endBalance);
			}else {
				balancesMap.put(keys.get(j), balancesMap.get(keys.get(j-1))-deltaMap.get(keys.get(j-1)));
			}
		}	
		for (int i = 2; i < 3+items.length-7; i++) {
			validationMap.put(Integer.parseInt(items[i]), true);
		}
		
	}
	
	@Override
	public Vector<String[]> readRecords(int month) throws NotValidMonthException{
		if(isValidMonth(month)) {
			return this.transactionsMap.get(month);
		}else {
			throw new NotValidMonthException();
		}
	}

	@Override
	public double getBalance(int month) throws NotValidMonthException {
		if(isValidMonth(month)) {
			return this.balancesMap.get(month);
		}else {
			throw new NotValidMonthException();
		}
	}
	
	private boolean isValidMonth(int month){
		return validationMap.get(month);
	}

}