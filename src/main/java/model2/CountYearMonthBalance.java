package model2;

import java.math.BigDecimal;

public class CountYearMonthBalance {
	private Integer year;
	private Integer month;
	private String countName;
	private String countId;
	private BigDecimal amount;

	public CountYearMonthBalance() {
	}

	public Integer getYear() {
		return year;
	}

	public CountYearMonthBalance setYear(Integer year) {
		this.year = year;
		return this;
	}

	public Integer getMonth() {
		return month;
	}

	public CountYearMonthBalance setMonth(Integer month) {
		this.month = month;
		return this;
	}

	public String getCountName() {
		return countName;
	}

	public CountYearMonthBalance setCountName(String countName) {
		this.countName = countName;
		return this;
	}

	public String getCountId() {
		return countId;
	}

	public CountYearMonthBalance setCountId(String countId) {
		this.countId = countId;
		return this;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public CountYearMonthBalance setAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

}
