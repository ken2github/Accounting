package restapi.bankfileconverter.service.reader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import model2.Transaction;
import restapi.bankfileconverter.api.Count;
import restapi.bankfileconverter.api.FileNamePatternConstants;
import restapi.bankfileconverter.api.InpuImplicitFileInfo;
import restapi.bankfileconverter.api.InputBase64FileInfo;
import restapi.bankfileconverter.api.InputImplicitBase64FileInfo;
import restapi.bankfileconverter.api.OutputFileInfo;
import restapi.bankfileconverter.service.BankFileReaderService;
import restapi.bankfileconverter.service.reader.BankFileError.BankFileErrorCode;

public class BankFileReaderServiceImpl_WithFileReaders implements BankFileReaderService {

	@Override
	public OutputFileInfo convertToTransactions(InpuImplicitFileInfo iifi) {
		OutputFileInfo ofi = new OutputFileInfo();

		Pattern p = Pattern.compile(FileNamePatternConstants.FILENAME_WITH_EXTENSION_PATTERN_REGEXP);
		Matcher m = p.matcher(iifi.canonicalFileName);
		if (!m.matches()) {
			throw new ReaderException(new BankFileError(BankFileErrorCode.FILE_FORMAT_NOT_COMPLIANT,
					"The file '" + iifi.canonicalFileName + "' does not match regexp: "
							+ FileNamePatternConstants.FILENAME_WITH_EXTENSION_PATTERN_REGEXP));
		}
		Count count = Count.valueOf(m.group(FileNamePatternConstants.COUNT).toUpperCase());
		Date fromDate = valueOfDate(m.group(FileNamePatternConstants.FROM));
		Date toDate = valueOfDate(m.group(FileNamePatternConstants.TO));
		BigDecimal balance = valueOfBalance(m.group(FileNamePatternConstants.BALANCE));

		// compute transactions
		List<Transaction> transactions = readTransactionsFromBankFileReader(
				getBankFileReaderFromFile(iifi.file, BankFile.getFileFormat(count)));

		// assign count to transactions
		transactions.stream().forEach(t -> t.setCountName(count.name()));

		return ofi.setCount(count).setFromDate(fromDate).setToDate(toDate).setToDateBalance(balance)
				.setTotalFlow(computeTotalFlow(transactions)).setTransactions(transactions);
	}

	@Override
	public OutputFileInfo convertToTransactions(InputBase64FileInfo ifi) {
		OutputFileInfo ofi = new OutputFileInfo();
		// compute transactions
		List<Transaction> transactions = readTransactionsFromBankFileReader(getBankFileReaderFromBase64EncodedString(
				ifi.base64EncodedFileContent, BankFile.getFileFormat(ifi.count)));

		// List<Transaction> transactions =
		// readTransactionsFromBase64EncodedString(ifi.base64EncodedFileContent,
		// BankFile.getFileFormat(ifi.count));

		return ofi.setCount(ifi.count).setFromDate(ifi.fromDate).setToDate(ifi.toDate)
				.setToDateBalance(ifi.toDateBalance).setTotalFlow(computeTotalFlow(transactions))
				.setTransactions(transactions);
	}

	@Override
	public OutputFileInfo convertToTransactions(InputImplicitBase64FileInfo iifi) {
		OutputFileInfo ofi = new OutputFileInfo();

		Pattern p = Pattern.compile(FileNamePatternConstants.FILENAME_WITHOUT_EXTENSION_PATTERN_REGEXP);
		Matcher m = p.matcher(iifi.canonicalFileName);
		if (!m.matches()) {
			throw new ReaderException(new BankFileError(BankFileErrorCode.FILE_FORMAT_NOT_COMPLIANT,
					"The file '" + iifi.canonicalFileName + "' does not match regexp: "
							+ FileNamePatternConstants.FILENAME_WITHOUT_EXTENSION_PATTERN_REGEXP));
		}
		Count count = Count.valueOf(m.group(FileNamePatternConstants.COUNT).toUpperCase());
		Date fromDate = valueOfDate(m.group(FileNamePatternConstants.FROM));
		Date toDate = valueOfDate(m.group(FileNamePatternConstants.TO));
		BigDecimal balance = valueOfBalance(m.group(FileNamePatternConstants.BALANCE));

		// compute transactions
		List<Transaction> transactions = readTransactionsFromBankFileReader(
				getBankFileReaderFromBase64EncodedString(iifi.base64EncodedFileContent, BankFile.getFileFormat(count)));

		return ofi.setCount(count).setFromDate(fromDate).setToDate(toDate).setToDateBalance(balance)
				.setTotalFlow(computeTotalFlow(transactions)).setTransactions(transactions);
	}

	private BigDecimal computeTotalFlow(List<Transaction> transactions) {
		BigDecimal totalFlow = new BigDecimal("0.00");
		if (transactions.size() > 0) {
			totalFlow = transactions.stream().map(t -> t.getAmount()).reduce(BigDecimal::add).get();
		}
		return totalFlow;
	}

	private Date valueOfDate(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		// String dateInString = "07/06/2013";
		String dateInString = date.substring(0, 2) + "/" + date.substring(2, 4) + "/" + date.substring(4);

		try {
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			throw new RuntimeException("NOT_IMPLEMENTED");
		}

	}

	private BigDecimal valueOfBalance(String balance) {
		return new BigDecimal(balance.replace(FileNamePatternConstants.BALANCE_SEPARATOR, "."));
	}

	// private List<Transaction> readTransactionsFromBase64EncodedString(String
	// base64EncodedFileContent,
	// BankFileFormat fileFormat) {
	//
	// BankFileReader bfr;
	// try {
	// bfr = BankFile.getReader(fileFormat,
	// new
	// ByteArrayInputStream(Base64.getDecoder().decode(base64EncodedFileContent)));
	// } catch (Exception e) {
	// throw new ReaderException(new
	// BankFileError(BankFileErrorCode.ILLEGAL_BASE_64_ENCODING,
	// "Not valid base64 encoding of file. Exception: " + e.toString() + " : " +
	// e.getMessage()));
	// }
	//
	// return readTransactionsFromBankFileReader(bfr);
	// }

	private BankFileReader getBankFileReaderFromBase64EncodedString(String base64EncodedFileContent,
			BankFileFormat fileFormat) {
		try {
			return BankFile.getReader(fileFormat,
					new ByteArrayInputStream(Base64.getDecoder().decode(base64EncodedFileContent)));
		} catch (Exception e) {
			throw new ReaderException(new BankFileError(BankFileErrorCode.ILLEGAL_BASE_64_ENCODING,
					"Not valid base64 encoding of file. Exception: " + e.toString() + " : " + e.getMessage()));
		}
	}

	private BankFileReader getBankFileReaderFromFile(File file, BankFileFormat fileFormat) {
		try {
			return BankFile.getReader(fileFormat, new FileInputStream(file));
		} catch (Exception e) {
			throw new ReaderException(new BankFileError(BankFileErrorCode.ILLEGAL_BASE_64_ENCODING,
					"Not valid base64 encoding of file. Exception: " + e.toString() + " : " + e.getMessage()));
		}
	}

	// private List<Transaction> readTransactionsFromFile(File file, BankFileFormat
	// fileFormat) {
	//
	// BankFileReader bfr;
	// try {
	// bfr = BankFile.getReader(fileFormat, new FileInputStream(file));
	// } catch (Exception e) {
	// throw new ReaderException(new
	// BankFileError(BankFileErrorCode.ILLEGAL_BASE_64_ENCODING,
	// "Not valid base64 encoding of file. Exception: " + e.toString() + " : " +
	// e.getMessage()));
	// }
	//
	// return readTransactionsFromBankFileReader(bfr);
	// }

	private List<Transaction> readTransactionsFromBankFileReader(BankFileReader bfr) {
		if (bfr.isFileCompliantWithFormat()) {
			try {
				return bfr.readTransactions();
			} catch (Exception e) {
				throw new ReaderException(new BankFileError(BankFileErrorCode.ILLEGAL_BASE_64_ENCODING,
						"Not valid base64 encoding of file. Exception: " + e.toString() + " : " + e.getMessage()));
			}
		} else {
			String listOfIssues = bfr.foundComplianceIssues().stream().collect(Collectors.joining("; "));
			throw new ReaderException(new BankFileError(BankFileErrorCode.FILE_FORMAT_NOT_COMPLIANT,
					"File format was not compliant. Issues :  " + listOfIssues));
		}
	}

}
