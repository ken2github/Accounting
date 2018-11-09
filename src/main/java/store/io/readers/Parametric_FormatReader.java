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
import store.io.TransactionFilterException;
import utils.MoneyConverter;

public class Parametric_FormatReader implements FormatReader {
	
	protected Map<Integer,Vector<String[]>> transactionsListMap = new HashMap<Integer, Vector<String[]>>();
	protected Map<Integer, Long> balancesMap = new HashMap<>();
	protected Map<Integer, Boolean> isValidMap = new HashMap<>();	
	protected Map<Integer, Long> deltasMap = new HashMap<>();	
	private RecordMapper recordMapper;
	private TransactionFilterException tfe;
		
	public Parametric_FormatReader(RecordMapper recordMapper,TransactionFilterException tfe) {
		this.recordMapper = recordMapper;
		this.tfe = tfe;
		for (int month=1; month<=12; month++) {
			isValidMap.put(month, false);
			balancesMap.put(month, 0L);
			deltasMap.put(month, 0L);
			transactionsListMap.put(month, new Vector<>());
		}
	}
		
	@Override
	public void loadSourceFile(File sourceFile) throws Exception {
		String[] items =  sourceFile.getName().split(NORMALIZED_SEPARATOR_REGEXP);
		long endBalance = MoneyConverter.parseDecimalStringToLong(items[items.length-3],items[items.length-2]);		
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
	public long getBalance(int month) throws NotValidMonthException {
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
				
				if(tfe.isValid(record)) {
					String[] normalizedRecord = new String[Transaction.FIELDS];
					
					Date date = new SimpleDateFormat(recordMapper.getDateFormat()).parse(record[recordMapper.getDateField()]);
					//long amount = MoneyConverter.parseDecimalStringToLong(record[recordMapper.getAmountField()]);
					long amount = MoneyConverter.parseDecimalStringToLong(record[recordMapper.getAmountField()]);
					String recString = (new DecimalFormat("##.00").format(((double)amount)/100));

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
				}else {
					throw new Exception("Not valid transaction record. "+tfe.getError(record));
				}
			}
		}
	}
	
	private void loadDeltaMap() {
		for (int  month : transactionsListMap.keySet()) {
			for (String[] normalizedRecord : transactionsListMap.get(month)) {
				long amount = MoneyConverter.parseDecimalStringToLong(normalizedRecord[1],normalizedRecord[2]);
				deltasMap.put(month, deltasMap.get(month)+amount);
			}
		}
	}
	
	private void loadValidationMap(String[] months) {
		for (String month : months) {
			isValidMap.put(Integer.parseInt(month), true);
		}
	}
	
	private void loadBalanceMap(long endBalance) {
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