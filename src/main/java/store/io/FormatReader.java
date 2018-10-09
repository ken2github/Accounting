package store.io;

import java.io.File;
import java.util.Vector;

import store.io.readers.NotValidMonthException;

public interface FormatReader extends Format {
	
	void loadSourceFile(File sourceFile) throws Exception;
	Vector<String[]> readRecords(int month)throws NotValidMonthException ;
	double getBalance(int month)throws NotValidMonthException ;
	
}
