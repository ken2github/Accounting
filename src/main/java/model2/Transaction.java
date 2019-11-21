package model2;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {

	// mandatory
	private BigDecimal amount;
	private String title;
	private Date date;

	// optionals
	private String sectorName;
	private String countName;
	private Boolean isCommon;

	public Transaction() {
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Transaction setAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Transaction setTitle(String title) {
		this.title = title;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public Transaction setDate(Date date) {
		this.date = date;
		return this;
	}

	public String getSectorName() {
		return sectorName;
	}

	public Transaction setSectorName(String sectorName) {
		this.sectorName = sectorName;
		return this;
	}

	public String getCountName() {
		return countName;
	}

	public Transaction setCountName(String countName) {
		this.countName = countName;
		return this;
	}

	public Boolean isCommon() {
		return isCommon;
	}

	public Transaction setIsCommon(Boolean isCommon) {
		this.isCommon = isCommon;
		return this;
	}

}
