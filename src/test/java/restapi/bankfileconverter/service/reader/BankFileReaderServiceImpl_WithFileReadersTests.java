package restapi.bankfileconverter.service.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import org.junit.Test;

import controllers.exceptionhandler.ApiException;
import restapi.bankfileconverter.api.Count;
import restapi.bankfileconverter.api.InputBase64FileInfo;
import restapi.bankfileconverter.api.InputImplicitBase64FileInfo;
import restapi.bankfileconverter.api.OutputFileInfo;
import restapi.bankfileconverter.service.reader.xls.ReadersHelper;

public class BankFileReaderServiceImpl_WithFileReadersTests {

	@Test
	public void convertBNPFile() {
		String base64EncodedFileContent = getBase64EncodedStringFromFile(
				"C:\\Users\\primo\\git\\Accounting\\src\\test\\resources\\bank-reports\\reportConsistency2018_2019\\count_bnp_from_01012018_to_21052019_balance_23624_45.xls");

		// convert
		BankFileReaderServiceImpl_WithFileReaders bfrsi_wfr = new BankFileReaderServiceImpl_WithFileReaders();
		InputBase64FileInfo ifi = new InputBase64FileInfo().setCount(Count.BNP).setFromDate(Date.from(Instant.now()))
				.setToDate(Date.from(Instant.now())).setToDateBalance(new BigDecimal("00.00"))
				.setBase64EncodedFileContent(base64EncodedFileContent);
		try {
			OutputFileInfo ofi = bfrsi_wfr.convertToTransactions(ifi);

			// test
			assertEquals("Wrong Count: ", ofi.count, ifi.count);
			assertEquals("Wrong FROM date: ", ofi.fromDate, ifi.fromDate);
			assertEquals("Wrong TO date: ", ofi.toDate, ifi.toDate);
			assertEquals("Wrong TO date balance:", ofi.toDateBalance, ifi.toDateBalance);
			assertEquals("Wrong numbero of transactions: ", 435, ofi.transactions.size());
			assertEquals("Wrong total flow: ", new BigDecimal("-7534.73"), ofi.totalFlow);

		} catch (ApiException ae) {
			fail("Unexpected exception: " + ae.getApiError().getExternalMessage());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.toString() + " : " + e.getMessage());
		}

	}

	@Test
	public void convertBNPImplicitFile() {
		String canonicalFileName = "count_bnp_from_01012018_to_21052019_balance_23624_45";
		String base64EncodedFileContent = getBase64EncodedStringFromFile(
				"C:\\Users\\primo\\git\\Accounting\\src\\test\\resources\\bank-reports\\reportConsistency2018_2019\\count_bnp_from_01012018_to_21052019_balance_23624_45.xls");

		// convert
		BankFileReaderServiceImpl_WithFileReaders bfrsi_wfr = new BankFileReaderServiceImpl_WithFileReaders();
		InputImplicitBase64FileInfo ifi = new InputImplicitBase64FileInfo().setCanonicalFileName(canonicalFileName)
				.setBase64EncodedFileContent(base64EncodedFileContent);
		try {
			OutputFileInfo ofi = bfrsi_wfr.convertToTransactions(ifi);

			// test
			assertEquals("Wrong Count: ", Count.BNP, ofi.count);
			assertEquals("Wrong FROM date: ", ReadersHelper.valueOfDateFromString("01/01/2018", "dd/MM/yyyy"),
					ofi.fromDate);
			assertEquals("Wrong TO date: ", ReadersHelper.valueOfDateFromString("21/05/2019", "dd/MM/yyyy"),
					ofi.toDate);
			assertEquals("Wrong TO date balance:", new BigDecimal("23624.45"), ofi.toDateBalance);
			assertEquals("Wrong numbero of transactions: ", 435, ofi.transactions.size());
			assertEquals("Wrong total flow: ", new BigDecimal("-7534.73"), ofi.totalFlow);

		} catch (ReaderException ae) {
			fail("Unexpected exception: " + ae.getBankFileError().getErrorCode() + " : "
					+ ae.getBankFileError().getMessage());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.toString() + " : " + e.getMessage());
		}

	}

	private String getBase64EncodedStringFromFile(String path) {
		ByteArrayInputStream bais = getByteArrayInputSteam_from_localFile(path);

		ByteBuffer byteBuffer = ByteBuffer.allocate(1000000);
		while (bais.available() > 0) {
			byteBuffer.put((byte) bais.read());
		}
		return Base64.getEncoder().encodeToString(byteBuffer.array());
	}

	private ByteArrayInputStream getByteArrayInputSteam_from_localFile(String filePath) {
		// read bytes from local file
		Path path = Paths.get(filePath);
		try {
			byte[] bArray = Files.readAllBytes(path);
			return new ByteArrayInputStream(bArray);
		} catch (IOException e) {
			throw new RuntimeException("NOT_IMPLEMENTED: " + e.getMessage());
		}
	}

}
