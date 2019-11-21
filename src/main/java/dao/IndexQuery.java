package dao;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model2.Metric;

public class IndexQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2204288086991372724L;

	private List<Metric.KEY> freeKeys = new ArrayList<Metric.KEY>();
	private List<AbstractMap.SimpleEntry<Metric.KEY, String>> fixedKeys = new ArrayList<>();

	public IndexQuery() {
		super();
	}

	public List<Metric.KEY> getFreeKeys() {
		return freeKeys;
	}

	public IndexQuery addFreeKey(Metric.KEY key) {
		this.freeKeys.add(key);
		return this;
	}

	public List<AbstractMap.SimpleEntry<Metric.KEY, String>> getFixedKeys() {
		return fixedKeys;
	}

	public IndexQuery addFixedKey(Metric.KEY key, String value) {
		this.fixedKeys.add(new AbstractMap.SimpleEntry<Metric.KEY, String>(key, value));
		return this;
	}

	public boolean isFixedKey(Metric.KEY key) {
		return fixedKeys.stream().map(amse -> amse.getKey()).collect(Collectors.toList()).contains(key);
	}

	public boolean isFreeKey(Metric.KEY key) {
		return freeKeys.contains(key);
	}

	public String getFixedValue(Metric.KEY key) {
		for (AbstractMap.SimpleEntry<Metric.KEY, String> simpleEntry : fixedKeys) {
			if (simpleEntry.getKey().equals(Metric.KEY.year)) {
				return simpleEntry.getValue();
			}
		}
		return null;
	}
}
