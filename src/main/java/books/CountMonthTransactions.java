package books;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;
import schema.Transaction;
import store.IO;
import static store.io.Format.*;

public class CountMonthTransactions {
	
	private Vector<Transaction> transactions = new Vector<>();	
	private String count;
	private int month;
	private int year;
	private double finalBalance;
	
	public CountMonthTransactions(File file) throws IOException, ParseException{
		//              0       1  2    3           4
		// Filename is YEAR-MONTH-COUNT-BALANCE-BALANCEDECIMAL
		String[] values = file.getName().replaceAll("."+DB_EXTENSION, "").split(NORMALIZED_SEPARATOR_REGEXP);
		this.year=Integer.parseInt(values[0]);
		this.month=Integer.parseInt(values[1]);
		this.count=values[2];
		this.finalBalance=Double.parseDouble(values[3]+"."+values[4]+"D");
		
		// Content is list of transactions
		// DATE,AMOUNT,AMOUNTDECIMAL,TITLE,SECTOR,COMMON
		
		Vector<String[]> items = IO.readItems(file.getAbsolutePath(), Transaction.FIELDS);
		
		for (String[] strings : items) {
			Transaction transaction = new Transaction(strings);
			transactions.add(transaction);
		}
	}

	public Vector<Transaction> getTransactions() {
		return transactions;
	}

	public String getCount() {
		return count;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	public double getFinalBalance() {
		return finalBalance;
	}

	
}
