package restapi.bankfileconverter.service.reader.xls;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;

import model2.Transaction;

public class Edenred_1_0_XLS_Reader extends AbstractXLSReader {

	public static final String AMOUNT = "AMOUNT";

	public static final String AMOUNT_REGEXP_PART = "(?<" + AMOUNT + ">[\\d]*,[\\d]{2})";

	public static final String PATTERN_REGEXP = "^[+] " + AMOUNT_REGEXP_PART + "€ [\\d]* x [\\d]*,[\\d]{2}€ $";

	private static final int TRANSACTIONS_INITIAL_ROW_INDEX = 0;

	private static final int COMPLIANT_HEADERS_ROW_INDEX = -1;
	private static final String[] COMPLIANT_HEADERS = { "x", "x", "x", "x", "x" };
	private static final int COMPLIANT_ROW_MIN_CARDINALITY = TRANSACTIONS_INITIAL_ROW_INDEX;

	public Edenred_1_0_XLS_Reader(InputStream is) {
		initXLSReaderWithData(is, new AbstractXLSReader.CompliancyFormatConstants(COMPLIANT_HEADERS_ROW_INDEX,
				COMPLIANT_HEADERS, COMPLIANT_ROW_MIN_CARDINALITY));
	}

	@Override
	protected List<Transaction> internalReadTransactions(HSSFSheet sheet) {
		List<Transaction> result = new ArrayList<>();

		int rows = sheet.getPhysicalNumberOfRows();
		HSSFRow row;
		HSSFCell cell;
		int cols = trickToColumns(sheet, rows);
		for (int r = TRANSACTIONS_INITIAL_ROW_INDEX; r <= rows; r++) {
			row = sheet.getRow(r);
			if (row != null) {
				Date date = null;
				String description = null;
				BigDecimal amount = null;
				List<ReadersHelper.TitleItem> titleItems = new ArrayList<>();
				boolean isConfirmedTransaction = true;
				for (int c = 0; c < cols; c++) {
					cell = row.getCell((short) c);
					if (cell != null) {
						switch (c) {
						case 0: // Empty column
							break;
						case 1: // Date
							date = ReadersHelper.excelDateParse(new Double(cell.getNumericCellValue()).intValue(), 2);
							break;
						case 2: // Libelle
							description = cell.getStringCellValue().replaceAll("([\\r]*[\\n]*)", "");
							break;
						case 3: // Statusde la transaction
							titleItems.add(new ReadersHelper.TitleItem("Status", cell.getStringCellValue()));
							if (!cell.getStringCellValue().contains("transaction confirmée")) {
								if (cell.getStringCellValue().contains("transaction refusée")) {
									// Skip this transaction
									isConfirmedTransaction = false;
								} else if (cell.getStringCellValue().contains("transaction en cours de traitement")) {
									// Skip this transaction
									isConfirmedTransaction = false;
								} else {
									throw new RuntimeException("NOT_IMPLEMENTED : Unknown Transaction Status '"
											+ (cell.getStringCellValue()) + "' for transaction '" + r + "'");
								}
							}
							break;
						case 4: // Montant
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								Pattern p = Pattern.compile(PATTERN_REGEXP);
								Matcher m = p.matcher(cell.getStringCellValue());
								if (!m.matches()) {
									throw new RuntimeException("NOT_IMPLEMENTED : Expected pattern was not matched");
								} else {
									amount = new BigDecimal(m.group(AMOUNT).replaceAll(",", "."));
								}

							} else {
								amount = ReadersHelper.valueOfBigDecimalRoundedAtCentimes(cell.getNumericCellValue());
							}
							break;
						default:
							throw new RuntimeException("NOT_IMPLEMENTED");
						}
					}
				}
				String title = ReadersHelper.getAutoTitle(description, titleItems);

				if (isConfirmedTransaction) {
					result.add(new Transaction().setDate(date).setTitle(title).setAmount(amount));
				}
			}
		}

		return result;
	}

}
