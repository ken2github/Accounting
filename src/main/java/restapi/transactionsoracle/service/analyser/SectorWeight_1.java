package restapi.transactionsoracle.service.analyser;

public class SectorWeight_1 {
	public String sector;
	public double weight;

	public String getSector() {
		return sector;
	}

	public SectorWeight_1 setSector(String sector) {
		this.sector = sector;
		return this;
	}

	public double getWeight() {
		return weight;
	}

	public SectorWeight_1 setWeight(double weight) {
		this.weight = weight;
		return this;
	}

	@Override
	public int hashCode() {
		return this.sector.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SectorWeight_1)
			return this.sector.equals(((SectorWeight_1) obj).getSector());
		return false;
	}

	@Override
	public String toString() {
		return this.sector;
	}

}
