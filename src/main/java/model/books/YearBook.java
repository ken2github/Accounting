package model.books;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;

import model.schema.Schema;

public class YearBook {

	private Vector<MonthBook> monthBooks = new Vector<>();
	private Schema schema;
	private int year;
	
	public YearBook(File yeardirectory) throws IOException, ParseException, NotAlignedDateInFileException {
		// Read schema and books
		this.year=Integer.parseInt(yeardirectory.getName());
		
		this.schema = new Schema(yeardirectory);
		
		for (int month = 1; month < 13; month++) {
			try {
				MonthBook mb = new MonthBook(yeardirectory, month); 
				monthBooks.add(mb);
			} catch (FileMissingForMonthException e) {
				//System.out.println("No file for month "+month);
				break;
			}
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
