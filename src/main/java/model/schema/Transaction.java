package schema;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {

	public static final int FIELDS=6;
	public static final String DATE_FORMAT="yyyy-MM-dd";
	private double amount;
	
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
		this.amount=Float.parseFloat(strings[1]+"."+strings[2]+"D");
		// String is in format YYYY-MM-DD
		this.date=(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)).parse(strings[0]);
	}

	public String getTitle() {
		return title;
	}
	
	public double getAmount() {
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

	
	
}
