package store.io;

import java.io.File;
import java.io.InputStream;
import java.util.Vector;

import store.io.readers.NotValidMonthException;

public interface FormatReader extends Format {

	void loadInputStream(InputStream is, String fileName) throws Exception;

	void loadSourceFile(File sourceFile) throws Exception;

	Vector<String[]> readRecords(int month) throws NotValidMonthException;

	long getBalance(int month) throws NotValidMonthException;

}
