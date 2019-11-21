package dao;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public interface FileReader {
	ReadOnlyTransactionList getReadOnlyTransactionList(InputStream io, String nominalCount, List<Date> nominalMonths,
			String fileName);
}
