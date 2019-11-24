package deprecated.model.schema;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import deprecated.utils.MoneyConverter;

public class Transaction {

	public static final int FIELDS=6;
	public static final String DATE_FORMAT="yyyy-MM-dd";
	public static final String SECTOR_DELIMITER=".";
	public static final String SECTOR_DELIMITER_REGEXP="[.]";
	//private double amount;
	private long amount;
	
	private String title;
	private String sector;
	private boolean isCommon;
	private Date date;
	
	//                           0          1                 2               3                4               5
	//public Transaction(String date,String amount, String amountDecimal, String title, String sector, String common) throws ParseException {
	public Transaction(String... strings) throws ParseException {	
		this.title=strings[3];
		this.sector=strings[4];		
		this.isCommon=strings[5].equals("y");
		if((!this.isCommon) && (!strings[5].equals("n"))) {
			String joined = Stream.of(strings).collect(Collectors.joining(","));
			throw new ParseException("Transaction does not contain a parseable Common field ["+(strings[5])+"]. When parsing ["+joined+"]", 0);
		}
		//this.amount=Float.parseFloat(strings[1]+"."+strings[2]+"D");
		this.amount=MoneyConverter.parseDecimalStringToLong(strings[1],strings[2]);
		// String is in format YYYY-MM-DD
		this.date=(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)).parse(strings[0]);
	}

	public String getTitle() {
		return title;
	}
	
	public long getAmount() {
		return amount;
	}


	public String getSector() {
		return sector;
	}

	public boolean isCommon() {
		return isCommon;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public String toString() {
		return 
			(new SimpleDateFormat(Transaction.DATE_FORMAT)).format(this.date).toString()+","+	//date
			this.getAmount()+","+
			
			this.getTitle()+","+
			this.getSector();
	}
	
}
