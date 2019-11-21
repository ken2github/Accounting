package model2;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.List;

public class Metric implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2803523736500497831L;

	public static enum KEY implements Serializable {
		year, month, count_name, sector_name, sector_father_name, is_common, sign, amount
	}

	public static enum TYPE implements Serializable {
		average, flow, balance
	}

	// mandatory
	private TYPE type;

	private BigDecimal amount;

	private List<AbstractMap.SimpleEntry<KEY, String>> fixedKeys;

	public Metric() {
		super();
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Metric setAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	public List<AbstractMap.SimpleEntry<KEY, String>> getFixedKeys() {
		return fixedKeys;
	}

	public Metric setFixedKeys(List<AbstractMap.SimpleEntry<KEY, String>> fixedKeys) {
		this.fixedKeys = fixedKeys;
		return this;
	}

	public TYPE getType() {
		return type;
	}

	public Metric setType(TYPE type) {
		this.type = type;
		return this;
	}

}
