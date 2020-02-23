package model2;

public class DetailedSector extends Sector {

	private String id;
	private String fatherId;

	public DetailedSector() {
		super();
	}

	public String getId() {
		return id;
	}

	public DetailedSector id(String id) {
		this.id = id;
		return this;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFatherId() {
		return fatherId;
	}

	public void setFatherId(String fatherId) {
		this.fatherId = fatherId;
	}

}
