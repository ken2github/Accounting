package dao.file;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import dao.FileReader;
import dao.ReadOnlyTransactionList;

public class FileReaderImpl implements FileReader {

	@Override
	public ReadOnlyTransactionList getReadOnlyTransactionList(InputStream io, String nominalCount,
			List<Date> nominalMonths, String fileName) {
		return new FileReadyOnlyTransactionList(io, nominalCount, nominalMonths, fileName);
	}

}
