package model2;

import java.util.List;

public class DetailedTransaction extends Transaction {

	private String id;
	private String idCount;
	private String idSector;
	private boolean isAutomatic;
	private boolean isDeprecated;
	private List<String> isDeprecatedBy;

	public DetailedTransaction() {
		super();
	}

	public String getId() {
		return id;
	}

	public DetailedTransaction setId(String id) {
		this.id = id;
		return this;
	}

	public String getIdCount() {
		return idCount;
	}

	public DetailedTransaction setIdCount(String idCount) {
		this.idCount = idCount;
		return this;
	}

	public String getIdSector() {
		return idSector;
	}

	public DetailedTransaction setIdSector(String idSector) {
		this.idSector = idSector;
		return this;
	}

	public boolean isAutomatic() {
		return isAutomatic;
	}

	public DetailedTransaction setAutomatic(boolean isAutomatic) {
		this.isAutomatic = isAutomatic;
		return this;
	}

	public boolean isDeprecated() {
		return isDeprecated;
	}

	public DetailedTransaction setDeprecated(boolean isDeprecated) {
		this.isDeprecated = isDeprecated;
		return this;
	}

	public List<String> getIsDeprecatedBy() {
		return isDeprecatedBy;
	}

	public DetailedTransaction setIsDeprecatedBy(List<String> isDeprecatedBy) {
		this.isDeprecatedBy = isDeprecatedBy;
		return this;
	}

}
