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

public class Internal_1_0_XLS_Reader extends AbstractXLSReader {

	private static final int TRANSACTIONS_INITIAL_ROW_INDEX = 1;

	private static final int COMPLIANT_HEADERS_ROW_INDEX = 0;
	private static final String[] COMPLIANT_HEADERS = { "Date", "Montante", "Titolo", "Categoria", "Altro" };

	private static final int COMPLIANT_ROW_MIN_CARDINALITY = TRANSACTIONS_INITIAL_ROW_INDEX;

	public Internal_1_0_XLS_Reader(InputStream is) {
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
				for (int c = 0; c < cols; c++) {
					cell = row.getCell((short) c);
					if (cell != null) {
						switch (c) {
						case 0: // data
							date = ReadersHelper.valueOfDateFromString(cell.getStringCellValue(), "dd-MM-yyyy");
							break;
						case 1: // importo
							amount = ReadersHelper.valueOfBigDecimalRoundedAtCentimes(cell.getNumericCellValue());
							break;
						case 2: // causale
							description = cell.getStringCellValue();
							break;
						case 3: // categoria
							titleItems.add(new ReadersHelper.TitleItem("Categoria", cell.getStringCellValue()));
							break;
						case 4: // Altro
							titleItems.add(new ReadersHelper.TitleItem("Altro", cell.getStringCellValue()));
							break;
						default:
							throw new RuntimeException("NOT_IMPLEMENTED");
						}
					}
				}
				String title = ReadersHelper.getAutoTitle(description, titleItems);

				result.add(new Transaction().setDate(date).setTitle(title).setAmount(amount));
			}
		}

		return result;
	}

}
