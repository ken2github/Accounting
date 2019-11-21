package controllers.exceptionhandler;

import java.time.Instant;
import java.util.Date;

import org.springframework.http.HttpStatus;

import dao.DBError;
import restapi.bankfileconverter.service.reader.BankFileError;

public class ApiError {

	public static enum ApiErrorCode {
		INTERNAL_ERROR, UNEXPECTED_EXCEPTION_INTERNAL_ERROR, UNEXPECTED_DATA_ACCESS_EXCEPTION_ERROR, INTERNAL_DAO_ERROR, INTERNAL_FILE_FORMAT_READER_ERROR, INVALID_INPUT, SECTOR_NOT_FOUND, COUNT_NOT_FOUND, TRANSACTION_NOT_FOUND
	}

	private static final String DAO_ERROR_EXTMSG = "%s : %s";
	private static final String FILE_FORMAT_READER__ERROR_EXTMSG = "%s : %s";

	public static ApiError parseFromDBError(DBError dbError) {
		return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorCode.INTERNAL_DAO_ERROR,
				String.format(DAO_ERROR_EXTMSG, dbError.getErrorCode().name(), dbError.getMessage()),
				String.format(DAO_ERROR_EXTMSG, dbError.getErrorCode().name(), dbError.getMessage()));
	}

	public static ApiError parseFromBankFileReaderError(BankFileError bfr) {
		return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorCode.INTERNAL_FILE_FORMAT_READER_ERROR,
				String.format(FILE_FORMAT_READER__ERROR_EXTMSG, bfr.getErrorCode().name(), bfr.getMessage()),
				String.format(FILE_FORMAT_READER__ERROR_EXTMSG, bfr.getErrorCode().name(), bfr.getMessage()));
	}

	private HttpStatus httpStatus;
	private ApiErrorCode errorCode;
	private String internalMessage;
	private String externalMessage;
	private Date timestamp;

	public ApiError() {
		super();
	}

	public ApiError(HttpStatus httpStatus, ApiErrorCode errorCode, String internalMessage, String externalMessage) {
		super();
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
		this.internalMessage = internalMessage;
		this.externalMessage = externalMessage;
		this.timestamp = Date.from(Instant.now());
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public ApiError setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
		return this;
	}

	public ApiErrorCode getErrorCode() {
		return errorCode;
	}

	public ApiError setErrorCode(ApiErrorCode errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public String getInternalMessage() {
		return internalMessage;
	}

	public ApiError setInternalMessage(String internalMessage) {
		this.internalMessage = internalMessage;
		return this;
	}

	public String getExternalMessage() {
		return externalMessage;
	}

	public ApiError setExternalMessage(String externalMessage) {
		this.externalMessage = externalMessage;
		return this;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public ApiError setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
		return this;
	}

}
