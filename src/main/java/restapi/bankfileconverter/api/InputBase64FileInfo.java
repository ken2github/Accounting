package restapi.bankfileconverter.api;

import java.math.BigDecimal;
import java.util.Date;

public class InputBase64FileInfo {
	public Count count;
	public Date fromDate;
	public Date toDate;
	public BigDecimal toDateBalance;
	public String base64EncodedFileContent;

	public Count getCount() {
		return count;
	}

	public InputBase64FileInfo setCount(Count count) {
		this.count = count;
		return this;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public InputBase64FileInfo setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public Date getToDate() {
		return toDate;
	}

	public InputBase64FileInfo setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

	public BigDecimal getToDateBalance() {
		return toDateBalance;
	}

	public InputBase64FileInfo setToDateBalance(BigDecimal toDateBalance) {
		this.toDateBalance = toDateBalance;
		return this;
	}

	public String getBase64EncodedFileContent() {
		return base64EncodedFileContent;
	}

	public InputBase64FileInfo setBase64EncodedFileContent(String base64EncodedFileContent) {
		this.base64EncodedFileContent = base64EncodedFileContent;
		return this;
	}

}
