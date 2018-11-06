package store.io.readers;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import model.schema.Transaction;
import store.IO;
import store.io.FormatReader;
import store.io.RecordMapper;

public class Parametric_FormatReader implements FormatReader {
	
	protected Map<Integer,Vector<String[]>> transactionsListMap = new HashMap<Integer, Vector<String[]>>();
	protected Map<Integer, Double> balancesMap = new HashMap<>();
	protected Map<Integer, Boolean> isValidMap = new HashMap<>();	
	protected Map<Integer, Double> deltasMap = new HashMap<>();	
	private RecordMapper recordMapper;
		
	public Parametric_FormatReader(RecordMapper recordMapper) {
		this.recordMapper = recordMapper;
		for (int month=1; month<=12; month++) {
			isValidMap.put(month, false);
			balancesMap.put(month, 0d);
			deltasMap.put(month, 0d);
			transactionsListMap.put(month, new Vector<>());
		}
	}
		
	@Override
	public void loadSourceFile(File sourceFile) throws Exception {
		String[] items =  sourceFile.getName().split(NORMALIZED_SEPARATOR_REGEXP);
		double endBalance = Double.parseDouble(items[items.length-3]+"."+items[items.length-2]);		
		Vector<String[]> records=IO.readItems(sourceFile.getAbsolutePath(), recordMapper.getFields());		
		
		loadTransactionMap(records);
		loadDeltaMap();
		
		String[] months = Arrays.copyOfRange(items, 2, items.length-4);
		loadValidationMap(months);
		loadBalanceMap(endBalance);
	}
	
	@Override
	public Vector<String[]> readRecords(int month) throws NotValidMonthException{
		if(isValidMonth(month)) {
			return this.transactionsListMap.get(month);
		}else {
			String validMonths ="";
			for (int m : isValidMap.keySet()) {
				validMonths+=(isValidMap.get(m))?m+",":"";
			}
			throw new NotValidMonthException("The month ["+month+"] is not among the valid ones ["+validMonths+"]");
		}
	}

	@Override
	public double getBalance(int month) throws NotValidMonthException {
		if(isValidMonth(month)) {
			return this.balancesMap.get(month);
		}else {
			String validMonths ="";
			for (int m : isValidMap.keySet()) {
				validMonths+=(isValidMap.get(m))?m+",":"";
			}
			throw new NotValidMonthException("The month ["+month+"] is not among the valid ones ["+validMonths+"]");
		}
	}
	
	private boolean isValidMonth(int month) throws NotValidMonthException{
		if((month>0)&&(month<13)) {
			return isValidMap.get(month);
		}else {
			String validMonths ="";
			for (int m : isValidMap.keySet()) {
				validMonths+=(isValidMap.get(m))?m+",":"";
			}
			throw new NotValidMonthException("The month ["+month+"] is not among the valid ones ["+validMonths+"]");
		}
	}
	
	private void loadTransactionMap(Vector<String[]> records) throws Exception {
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
				
				//if(!transactionsMap.containsKey(month)){transactionsMap.put(month, new Vector<>());}
				transactionsListMap.get(month).addElement(normalizedRecord);		
			}
		}
	}
	
	private void loadDeltaMap() {
		for (int  month : transactionsListMap.keySet()) {
			for (String[] normalizedRecord : transactionsListMap.get(month)) {
				double amount = Double.parseDouble(normalizedRecord[1]+"."+normalizedRecord[2]);
				deltasMap.put(month, deltasMap.get(month)+amount);
			}
		}
	}
	
	private void loadValidationMap(String[] months) {
		for (String month : months) {
			isValidMap.put(Integer.parseInt(month), true);
		}
	}
	
	private void loadBalanceMap(double endBalance) {
		boolean isLastMonth = true;
		for (int currentMonth=12; currentMonth>=1; currentMonth--) {
			if(isLastMonth) {
				balancesMap.put(currentMonth,endBalance);
				isLastMonth = false;
			}else {
				balancesMap.put(currentMonth, balancesMap.get(currentMonth+1)-deltasMap.get(currentMonth+1));
			}
		}
	}

}