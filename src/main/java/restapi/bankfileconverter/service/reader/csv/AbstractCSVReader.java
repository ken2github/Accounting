package restapi.bankfileconverter.service.reader.csv;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import model2.Transaction;
import restapi.bankfileconverter.service.reader.AbstractReader;

public abstract class AbstractCSVReader extends AbstractReader {

	public class CompliancyFormatConstants {
		int COMPLIANT_HEADERS_ROW_INDEX = 0;
		String[] COMPLIANT_HEADERS;
		int COMPLIANT_ROW_MIN_CARDINALITY = 1;

		char SEPARATOR;
		boolean IGNORE_QUOTATION;
		int SKIP_LINES;

		public CompliancyFormatConstants(String[] cOMPLIANT_HEADERS, char sEPARATOR, boolean iGNORE_QUOTATION,
				int sKIP_LINES) {
			COMPLIANT_HEADERS = cOMPLIANT_HEADERS;
			SEPARATOR = sEPARATOR;
			IGNORE_QUOTATION = iGNORE_QUOTATION;
			SKIP_LINES = sKIP_LINES;
		}

	}

	protected void initCVSReaderWithData(Reader rd, CompliancyFormatConstants cfc) {
		List<Transaction> result = new ArrayList<>();

		try (CSVReader csvReader = new CSVReaderBuilder(rd).withSkipLines(cfc.SKIP_LINES).withCSVParser(
				new CSVParserBuilder().withSeparator(cfc.SEPARATOR).withIgnoreQuotations(cfc.IGNORE_QUOTATION).build())
				.build()) {
			this._checkForCompliance(csvReader, cfc.COMPLIANT_HEADERS_ROW_INDEX, cfc.COMPLIANT_HEADERS,
					cfc.COMPLIANT_ROW_MIN_CARDINALITY);
			this.formatIsCompliant = formatUnCompliances.size() == 0;

			// Read if compliancy checked
			if (formatIsCompliant) {
				result = internalReadTransactions(csvReader);
			}

			// String[] values = null;
			// while ((values = csvReader.readNext()) != null) {
			// records.add(Arrays.asList(values));
			// }
		} catch (Exception ioe) {
			ioe.printStackTrace();
			throw new RuntimeException("NOT_IMPLEMENTED : " + ioe.toString());
		}

		// try {
		// POIFSFileSystem fs = new POIFSFileSystem(is);
		// HSSFWorkbook wb = new HSSFWorkbook(fs);
		// HSSFSheet sheet = wb.getSheetAt(0);
		//
		// // Check and Set Status for Format Compliance
		// int rows = sheet.getPhysicalNumberOfRows();
		// int cols = trickToColumns(sheet, rows);
		// this.formatUnCompliances = _checkForCompliance(sheet, rows, cols,
		// cfc.COMPLIANT_HEADERS_ROW_INDEX,
		// cfc.COMPLIANT_HEADERS, cfc.COMPLIANT_ROW_MIN_CARDINALITY);
		// this.formatIsCompliant = formatUnCompliances.size() == 0;
		//
		// // Read if compliancy checked
		// if (formatIsCompliant) {
		// result = internalReadTransactions(sheet);
		// }
		// } catch (Exception ioe) {
		// ioe.printStackTrace();
		// throw new RuntimeException("NOT_IMPLEMENTED : " + ioe.toString());
		// }

		transactions = result;
	}

	private List<String> _checkForCompliance(CSVReader csvReader, final int COMPLIANT_HEADERS_ROW_INDEX,
			String[] COMPLIANT_HEADERS, final int COMPLIANT_ROW_MIN_CARDINALITY) throws IOException {

		List<String> formatUnCompliances = new ArrayList<String>();

		int cols = -1;
		String[] values = null;
		if ((values = csvReader.readNext()) != null) {
			cols = values.length;
		}

		if (values.length != COMPLIANT_HEADERS.length) { // Check for Column Headers Cardinality
			formatUnCompliances.add("Number of columns [" + cols + "] is not equal to expected table headers ["
					+ COMPLIANT_HEADERS.length + "]");
		}

		if ((COMPLIANT_HEADERS_ROW_INDEX >= 0) && (cols == COMPLIANT_HEADERS.length)) { // Check for Column Headers
			String header = "";
			for (int c = 0; c < cols; c++) {
				header = values[c];
				if (!header.equals(COMPLIANT_HEADERS[c])) {
					formatUnCompliances.add("Header of column [" + header + "] is not equal to expected table headers ["
							+ COMPLIANT_HEADERS[c] + "]");
				}
			}
		}

		int minrows = (values == null) ? 0 : 1;
		if (minrows < COMPLIANT_ROW_MIN_CARDINALITY) { // Check rows greater than
			formatUnCompliances.add("Number of rows [" + minrows + "] is lesser than expected minimal number ["
					+ COMPLIANT_ROW_MIN_CARDINALITY + "]");
		}
		return formatUnCompliances;
	}

	abstract protected List<Transaction> internalReadTransactions(CSVReader csvReader)
			throws ParseException, IOException;

}
