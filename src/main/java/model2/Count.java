package model2;

import java.util.Date;

public class Count {

	private String name;
	private String description;
	private Date openDate;
	private Date closeDate;

	public Count() {

	}

	public String getName() {
		return name;
	}

	public Count setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Count setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getOpenDate() {
		return openDate;
	}

	public Count setOpenDate(Date openDate) {
		this.openDate = openDate;
		return this;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public Count setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
		return this;
	}

}
