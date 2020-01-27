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

public class BPN_1_1_XLS_Reader extends AbstractXLSReader {

	private static final int TRANSACTIONS_INITIAL_ROW_INDEX = 1;

	private static final int COMPLIANT_HEADERS_ROW_INDEX = 0;
	private static final String[] COMPLIANT_HEADERS = { "Data Contabile", "Data Valuta", "Importo", "Divisa",
			"Causale / Descrizione", "Stato", "Canale" };

	private static final int COMPLIANT_ROW_MIN_CARDINALITY = TRANSACTIONS_INITIAL_ROW_INDEX;

	public BPN_1_1_XLS_Reader(InputStream is) {
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
						case 0: // Data Contabile
							date = ReadersHelper.valueOfDateFromString(cell.getStringCellValue(), "dd/MM/yyyy");
							break;
						case 1: // Data Valuta
							titleItems.add(new ReadersHelper.TitleItem("Data Valuta", cell.getStringCellValue()));
							break;
						case 2: // Importo
							amount = ReadersHelper.valueOfBigDecimalRoundedAtCentimes(cell.getNumericCellValue());
							break;
						case 3: // Divisa
							titleItems.add(new ReadersHelper.TitleItem("Divisa", cell.getStringCellValue()));
							break;
						case 4: // Causale
							description = cell.getStringCellValue();
							break;
						case 5: // Stato
							if (!cell.getStringCellValue().contains("Cont."))
								throw new RuntimeException("NOT_IMPLEMENTED");
							break;
						case 6: // Canale
							titleItems.add(new ReadersHelper.TitleItem("Canale", cell.getStringCellValue()));
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
