package restapi.bankfileconverter.service.reader.xls;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import model2.Transaction;

public class Spese_1_0_XLS_Reader extends AbstractXLSReader {

	private static final int TRANSACTIONS_INITIAL_ROW_INDEX = 1;

	private static final int COMPLIANT_HEADERS_ROW_INDEX = 0;
	private static final String[] COMPLIANT_HEADERS = { "Data", "Importo", "Tipo", "Categoria", "Nota" };
	private static final int COMPLIANT_ROW_MIN_CARDINALITY = TRANSACTIONS_INITIAL_ROW_INDEX;

	public Spese_1_0_XLS_Reader(InputStream is) {
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
				boolean isIncome = false;
				List<ReadersHelper.TitleItem> titleItems = new ArrayList<>();
				for (int c = 0; c < cols; c++) {
					cell = row.getCell((short) c);
					if (cell != null) {
						switch (c) {
						case 0: // Data - String or Numeric - 17/05/19
							if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
								date = ReadersHelper.valueOfDateFromString(cell.getStringCellValue(), "dd/MM/yy");
							} else {
								// NOTICE:
								// it seems that Spese format uses
								// -"String" type for dates containing days greater than 12, and
								// - "Numeric" type for ambiguous dates having days lesser equal than 12
								//
								// Indeed, in last case it's not unique the way to parse the date
								// (as month and day can be inversely be parsed).
								date = ReadersHelper.excelDateParseWithMonthDayInversion(
										new Double(cell.getNumericCellValue()).intValue());
							}
							break;
						case 1: // Importo - String
							amount = new BigDecimal(cell.getStringCellValue().replace(",", "."));
							break;
						case 2: // Tipo - String
							if (cell.getStringCellValue().contains("Entrata")) {
								isIncome = true;
							} else if (cell.getStringCellValue().contains("Spesa")) {
								isIncome = false;
							} else {
								throw new RuntimeException("NOT_IMPLEMENTED");
							}
							break;
						case 3: // Categoria - String
							titleItems.add(new ReadersHelper.TitleItem("Categoria", cell.getStringCellValue()));
							break;
						case 4: // Nota - String
							titleItems.add(new ReadersHelper.TitleItem("Nota", cell.getStringCellValue()));
							break;
						default:
							throw new RuntimeException("NOT_IMPLEMENTED");
						}
					}
				}

				if (!isIncome) {
					amount = amount.negate();
				}

				String title = ReadersHelper.getAutoTitle(description, titleItems);

				result.add(new Transaction().setDate(date).setTitle(title).setAmount(amount));
			}
		}

		return result;
	}

}
