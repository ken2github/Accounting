package books;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;

import schema.Schema;

public class YearBook {

	private Vector<MonthBook> monthBooks = new Vector<>();
	private Schema schema;
	private int year;
	
	public YearBook(File yeardirectory) throws IOException, ParseException {
		// Read schema and books
		this.year=Integer.parseInt(yeardirectory.getName());
		
		this.schema = new Schema(yeardirectory);
		
		for (int month = 1; month < 13; month++) {
			monthBooks.add(new MonthBook(yeardirectory, month));
		}
		
		
	}

	public Vector<MonthBook> getMonthBooks() {
		return monthBooks;
	}

	public Schema getSchema() {
		return schema;
	}

	public int getYear() {
		return year;
	}

	
}
