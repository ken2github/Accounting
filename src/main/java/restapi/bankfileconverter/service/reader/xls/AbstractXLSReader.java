package restapi.bankfileconverter.service.reader.xls;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import model2.Transaction;
import restapi.bankfileconverter.service.reader.AbstractReader;

public abstract class AbstractXLSReader extends AbstractReader {

	public class CompliancyFormatConstants {
		int COMPLIANT_HEADERS_ROW_INDEX;
		String[] COMPLIANT_HEADERS;
		int COMPLIANT_ROW_MIN_CARDINALITY;

		public CompliancyFormatConstants(int cOMPLIANT_HEADERS_ROW_INDEX, String[] cOMPLIANT_HEADERS,
				int cOMPLIANT_ROW_MIN_CARDINALITY) {
			super();
			COMPLIANT_HEADERS_ROW_INDEX = cOMPLIANT_HEADERS_ROW_INDEX;
			COMPLIANT_HEADERS = cOMPLIANT_HEADERS;
			COMPLIANT_ROW_MIN_CARDINALITY = cOMPLIANT_ROW_MIN_CARDINALITY;
		}

	}

	protected void initXLSReaderWithData(InputStream is, CompliancyFormatConstants cfc) {
		List<Transaction> result = new ArrayList<>();

		try {
			POIFSFileSystem fs = new POIFSFileSystem(is);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);

			// Check and Set Status for Format Compliance
			int rows = sheet.getPhysicalNumberOfRows();
			int cols = trickToColumns(sheet, rows);
			this.formatUnCompliances = _checkForCompliance(sheet, rows, cols, cfc.COMPLIANT_HEADERS_ROW_INDEX,
					cfc.COMPLIANT_HEADERS, cfc.COMPLIANT_ROW_MIN_CARDINALITY);
			this.formatIsCompliant = formatUnCompliances.size() == 0;

			// Read if compliancy checked
			if (formatIsCompliant) {
				result = internalReadTransactions(sheet);
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
			throw new RuntimeException("NOT_IMPLEMENTED : " + ioe.toString());
		}

		transactions = result;
	}

	private List<String> _checkForCompliance(HSSFSheet sheet, int rows, int cols, final int COMPLIANT_HEADERS_ROW_INDEX,
			String[] COMPLIANT_HEADERS, final int COMPLIANT_ROW_MIN_CARDINALITY) {
		HSSFRow row;
		HSSFCell cell;
		List<String> formatUnCompliances = new ArrayList<String>();
		if (cols != COMPLIANT_HEADERS.length) { // Check for Column Headers Cardinality
			formatUnCompliances.add("Number of columns [" + cols + "] is not equal to expected table headers ["
					+ COMPLIANT_HEADERS.length + "]");
		}

		if ((COMPLIANT_HEADERS_ROW_INDEX >= 0) && (cols == COMPLIANT_HEADERS.length)) { // Check for Column Headers
			row = sheet.getRow(COMPLIANT_HEADERS_ROW_INDEX);
			String header = "";
			for (int c = 0; c < cols; c++) {
				cell = row.getCell((short) c);
				header = cell.getStringCellValue();
				if (!header.equals(COMPLIANT_HEADERS[c])) {
					formatUnCompliances.add("Header of column [" + header + "] is not equal to expected table headers ["
							+ COMPLIANT_HEADERS[c] + "]");
				}
			}
		}
		if (rows < COMPLIANT_ROW_MIN_CARDINALITY) { // Check rows greater than
			formatUnCompliances.add("Number of rows [" + rows + "] is lesser than expected minimal number ["
					+ COMPLIANT_ROW_MIN_CARDINALITY + "]");
		}
		return formatUnCompliances;
	}

	protected int trickToColumns(HSSFSheet sheet, int rows) {
		HSSFRow row;
		int cols = 0; // No of columns
		int tmp = 0;

		// This trick ensures that we get the data properly even if it doesn't start
		// from first few rows
		for (int i = 0; i < 10 || i <= rows; i++) {
			row = sheet.getRow(i);
			if (row != null) {
				tmp = sheet.getRow(i).getPhysicalNumberOfCells();
				if (tmp > cols)
					cols = tmp;
			}
		}
		return cols;
	}

	abstract protected List<Transaction> internalReadTransactions(HSSFSheet sheet);
}
