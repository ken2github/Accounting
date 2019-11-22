package restapi.bankfileconverter.service.reader.csv;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.CSVReader;

import model2.Transaction;
import restapi.bankfileconverter.service.reader.xls.ReadersHelper;

public class Edenred_2_0_CSV_Reader extends AbstractCSVReader {

	public static SimpleDateFormat INTERNAL_DATA_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public static final String AMOUNT = "AMOUNT";

	public static final String AMOUNT_REGEXP_PART = "(?<" + AMOUNT + ">[-]?[\\d]*,[\\d]{2})";

	public static final String PATTERN_REGEXP = "^" + AMOUNT_REGEXP_PART + " €$";

	// private static final int TRANSACTIONS_INITIAL_ROW_INDEX = 1;

	private static final String[] COMPLIANT_HEADERS = { "Date", "Type", "Montant", "Détails" };
	private static final char SEPARATOR = ';';
	private static final int SKIP_LINES = 0;
	private static final boolean IGNORE_QUOTATION = true;

	public Edenred_2_0_CSV_Reader(Reader r) {
		initCVSReaderWithData(r, new AbstractCSVReader.CompliancyFormatConstants(COMPLIANT_HEADERS, SEPARATOR,
				IGNORE_QUOTATION, SKIP_LINES));
	}

	@Override
	protected List<Transaction> internalReadTransactions(CSVReader csvReader) throws ParseException, IOException {
		List<Transaction> result = new ArrayList<>();

		String[] values = null;
		String cell = null;
		int row = 0;
		while ((values = csvReader.readNext()) != null) {
			Date date = null;
			String description = null;
			BigDecimal amount = null;
			List<ReadersHelper.TitleItem> titleItems = new ArrayList<>();
			boolean isConfirmedTransaction = true;
			for (int c = 0; c < values.length; c++) {
				cell = values[c];
				if (cell != null) {
					switch (c) {
					case 0: // Date
						date = INTERNAL_DATA_FORMAT.parse(cell);
						break;
					case 1: // Status de la transaction
						titleItems.add(new ReadersHelper.TitleItem("Status", cell));
						if (!(cell.contains("Transaction effectuée") || cell.contains("Chargement"))) {
							if (cell.contains("Transaction refusée")) {
								// Skip this transaction
								isConfirmedTransaction = false;
							} else if (cell.contains("Transaction en cours de traitement")) {
								// Skip this transaction
								isConfirmedTransaction = false;
							} else {
								throw new RuntimeException("NOT_IMPLEMENTED : Unknown Transaction Status '" + (cell)
										+ "' for transaction '" + row + "'");
							}
						}
						break;
					case 2: // Montant
						Pattern p = Pattern.compile(PATTERN_REGEXP);
						Matcher m = p.matcher(cell);
						if (!m.matches()) {
							throw new RuntimeException(
									String.format("NOT_IMPLEMENTED : Expected pattern '%s' was not matched by '%s'",
											PATTERN_REGEXP, cell));
						} else {
							amount = new BigDecimal(m.group(AMOUNT).replaceAll(",", "."));
						}
						break;
					case 3: // Details
						description = cell.replaceAll("([\\r]*[\\n]*)", "");
						break;
					default:
						throw new RuntimeException("NOT_IMPLEMENTED: unforeseen column when parsing");
					}
				}
			}
			String title = ReadersHelper.getAutoTitle(description, titleItems);

			if (isConfirmedTransaction) {
				result.add(new Transaction().setDate(date).setTitle(title).setAmount(amount));
			}

			row++;
		}

		return result;
	}

}
