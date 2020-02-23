package dao;

import java.math.BigDecimal;
import java.util.Objects;

public class BalanceMetric implements Cloneable {
	public Integer year;
	public Integer month;
	public String countName;
	public BigDecimal amount;

	@Override
	public int hashCode() {
		return Objects.hash(year, month, countName, amount);
	}

	@Override
	public boolean equals(Object obj) {
		return Objects.deepEquals(this, obj);
	}

	public BalanceMetric doClone() {
		try {
			return (BalanceMetric) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException("UnexpectedException: " + e.getMessage(), e);
		}
	}
}