package restapi.bankfileconverter.service.reader;

import java.time.Instant;
import java.util.Date;

public class BankFileError {

	public static enum BankFileErrorCode {
		FILE_FORMAT_NOT_COMPLIANT, ILLEGAL_BASE_64_ENCODING;
	}

	private BankFileErrorCode errorCode;
	private String message;
	private Date timestamp;

	public BankFileError(BankFileErrorCode errorCode, String message) {
		super();
		this.errorCode = errorCode;
		this.message = message;
		this.timestamp = Date.from(Instant.now());
	}

	public BankFileErrorCode getErrorCode() {
		return errorCode;
	}

	public BankFileError setErrorCode(BankFileErrorCode errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public BankFileError setMessage(String message) {
		this.message = message;
		return this;
	}

	public Date getTimestamp() {
		return timestamp;
	}
}
