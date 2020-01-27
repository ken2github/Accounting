package model2;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {

	private static SimpleDateFormat COMPARE_DATE_FORMAT = new SimpleDateFormat("yyy-MM-dd");

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

	public boolean isDuplicateOf(Transaction transaction) {
		int date = (COMPARE_DATE_FORMAT.format(this.date).equals(COMPARE_DATE_FORMAT.format(transaction.getDate()))) ? 1
				: 0;
		int title = (this.title.equals(transaction.getTitle())) ? 1 : 0;
		int amount = ((this.amount.compareTo(transaction.getAmount()) == 0)) ? 1 : 0;

		if (this.countName.equals(transaction.getCountName()) && (date + title + amount) == 2) {
			System.out.println(String.format("Found PARTIAL MATCH: '%s' '%s' '%s' '%s'", this.countName,
					COMPARE_DATE_FORMAT.format(this.date), this.title, this.amount));
			System.out.println(
					String.format("Equality date(%s) title(%s) amount(%s)", date == 1, title == 1, amount == 1));
		}

		if (this.countName.equals(transaction.getCountName()) && this.title.equals(transaction.getTitle())
				&& COMPARE_DATE_FORMAT.format(this.date).equals(COMPARE_DATE_FORMAT.format(transaction.getDate()))) {
			if ((this.amount.compareTo(transaction.getAmount()) == 0))
				return true;
			System.out.println(String.format("Found PARTIAL MATCH: '%s' '%s' '%s'", this.countName,
					COMPARE_DATE_FORMAT.format(this.date), this.title));
			System.out.println(String.format("  Amount: '%s' vs '%s' = %s", this.amount, transaction.amount,
					this.amount.compareTo(transaction.amount)));
		}
		return false;

	}

}
