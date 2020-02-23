package dao;

import java.math.BigDecimal;
import java.util.Objects;

public class MonthAverageFlowMetric implements Cloneable {
	public Integer year;
	public String sectorName;
	public String sectorFatherName;
	public Boolean isCommon;
	public Integer sign;
	public BigDecimal amount;

	public void setYear(Integer year) {
		this.year = year;
	}

	public void setSectorName(String sectorName) {
		this.sectorName = sectorName;
	}

	public void setSectorFatherName(String sectorFatherName) {
		this.sectorFatherName = sectorFatherName;
	}

	public void setIsCommon(Boolean isCommon) {
		this.isCommon = isCommon;
	}

	public void setSign(Integer sign) {
		this.sign = sign;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public MonthAverageFlowMetric doClone() {
		try {
			return (MonthAverageFlowMetric) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException("UnexpectedException: " + e.getMessage(), e);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(year, sectorName, sectorFatherName, isCommon, sign, amount);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj == null) ? false : this.toString().equals(obj.toString());
	}

	@Override
	public String toString() {
		return String.format("year=%s, sectorName=%s,  sectorFatherName=%s,  isCommon=%s,  sign=%s,  amount=%s", year,
				sectorName, sectorFatherName, isCommon, sign, amount);
	}
}