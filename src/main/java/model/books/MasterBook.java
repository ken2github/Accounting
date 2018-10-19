package model.books;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;

public class MasterBook {

	private Vector<YearBook> yearBooks = new Vector<>();

	public MasterBook(File dbdirectory) throws IOException, ParseException, NotAlignedDateInFileException {
		// read all data
		File[] files = dbdirectory.listFiles(new FileFilter(){				
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}			
		});
		// fill in count transactions 
		for(File s: files) {
			this.yearBooks.addElement(new YearBook(s));

		}
	}

	public Vector<YearBook> getYearBooks() {
		return yearBooks;
	}
	
	 
}
