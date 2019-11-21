package store;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class IO {

	public static void writeItems(String filepath, String[] headers, Vector<String[]> items)
			throws IOException, SQLException {

		// try (BufferedWriter writer =
		// Files.newBufferedWriter(Paths.get(filepath));CSVPrinter csvPrinter = new
		// CSVPrinter(writer, CSVFormat.DEFAULT.withHeader((ResultSet)
		// ((List<String>)Arrays.asList(headers))));) {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filepath));
				CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);) {

			for (String[] strings : items) {
				csvPrinter.printRecord(Arrays.asList(strings));
			}

			csvPrinter.flush();
		}
	}

	public static Vector<String[]> readItems(String filepath, int stringValues) throws IOException {
		Vector<String[]> result = new Vector<>();
		try (Reader reader = Files.newBufferedReader(Paths.get(filepath), Charset.forName("ISO-8859-1"));
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);) {
			for (CSVRecord csvRecord : csvParser) {
				// Accessing Values by Column Index
				String[] item = new String[stringValues];

				for (int i = 0; i < item.length; i++) {
					item[i] = csvRecord.get(i);
				}

				result.add(item);
			}
		}
		return result;
	}

	public static Vector<String[]> readItems(InputStream is, int stringValues) throws IOException {
		Vector<String[]> result = new Vector<>();
		try (Reader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("ISO-8859-1")));
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);) {
			for (CSVRecord csvRecord : csvParser) {
				// Accessing Values by Column Index
				String[] item = new String[stringValues];

				for (int i = 0; i < item.length; i++) {
					item[i] = csvRecord.get(i);
				}

				result.add(item);
			}
		}
		return result;
	}

}
