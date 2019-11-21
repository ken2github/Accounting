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

	public void setId(String id) {
		this.id = id;
	}

	public String getIdCount() {
		return idCount;
	}

	public void setIdCount(String idCount) {
		this.idCount = idCount;
	}

	public String getIdSector() {
		return idSector;
	}

	public void setIdSector(String idSector) {
		this.idSector = idSector;
	}

	public boolean isAutomatic() {
		return isAutomatic;
	}

	public void setAutomatic(boolean isAutomatic) {
		this.isAutomatic = isAutomatic;
	}

	public boolean isDeprecated() {
		return isDeprecated;
	}

	public void setDeprecated(boolean isDeprecated) {
		this.isDeprecated = isDeprecated;
	}

	public List<String> getIsDeprecatedBy() {
		return isDeprecatedBy;
	}

	public void setIsDeprecatedBy(List<String> isDeprecatedBy) {
		this.isDeprecatedBy = isDeprecatedBy;
	}

}
