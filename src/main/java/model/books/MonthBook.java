package model.books;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;

import static store.io.Format.*;

public class MonthBook {

	private Vector<CountMonthTransactions> listOfcountMonthTransactions = new Vector<>();
	private int year;
	private int month;

	public MonthBook(File yearDirectory,int month) throws IOException, ParseException, NotAlignedDateInFileException  {
		// directory name is YEAR
		// find all files starting by YEAR-MONTH
		this.year=Integer.parseInt(yearDirectory.getName());
		this.month=month;
		String monthString = (this.month>9)? ""+month:"0"+month; 

		for (File  file : yearDirectory.listFiles()) {
			if(!file.getName().startsWith(year+""))
				throw new NotAlignedDateInFileException("Year in file is ["+(file.getName().substring(0, 3))+"] while in directory is ["+year+"]");
		}
		
		File[] files = yearDirectory.listFiles(new FileFilter(){				
			@Override
			public boolean accept(File pathname) {
				boolean result =false;
				if(pathname.getName().startsWith(year+NORMALIZED_SEPARATOR+monthString))
					result = true;
				return result;
			}			
		});
		// fill in count transactions 
		for(File s: files) {
			this.listOfcountMonthTransactions.addElement(new CountMonthTransactions(s));

		}
	}



	public Vector<CountMonthTransactions> getListOfcountMonthTransactions() {
		return listOfcountMonthTransactions;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}


}
