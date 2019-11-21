package model2;

import java.util.Date;

public class Sector {

	private String name;
	private String description;
	private String fatherName;
	private Date activationDate;
	private Date deactivationDate;

	public Sector() {

	}

	public String getName() {
		return name;
	}

	public Sector setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Sector setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getFatherName() {
		return fatherName;
	}

	public Sector setFatherName(String fatherName) {
		this.fatherName = fatherName;
		return this;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public Sector setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
		return this;
	}

	public Date getDeactivationDate() {
		return deactivationDate;
	}

	public Sector setDeactivationDate(Date deactivationDate) {
		this.deactivationDate = deactivationDate;
		return this;
	}

}
