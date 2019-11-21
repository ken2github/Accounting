package dao;

import java.time.Instant;
import java.util.Date;

public class DBError {

	public static enum DBErrorCode {
		RESOURCE_NOT_FOUND, RESOURCE_NOT_DELETED, ILLEGAL_UPDATE, ILLEGAL_STATUS, ILLEGAL_QUERY
	}

	private DBErrorCode errorCode;
	private String message;
	private Date timestamp;

	public DBError(DBErrorCode errorCode, String message) {
		super();
		this.errorCode = errorCode;
		this.message = message;
		this.timestamp = Date.from(Instant.now());
	}

	public DBErrorCode getErrorCode() {
		return errorCode;
	}

	public DBError setErrorCode(DBErrorCode errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public DBError setMessage(String message) {
		this.message = message;
		return this;
	}

	public Date getTimestamp() {
		return timestamp;
	}

}
