package restapi.bankprovisioninghelper.api;

public enum Relevance {

	HIGHEST, VERY_HIGH, HIGH, MEDIUM, LOW, VERY_LOW, LOWEST;

	public double toDouble() {
		switch (this) {
		case HIGHEST:
			return 0.999999d;
		case VERY_HIGH:
			return 0.9d;
		case HIGH:
			return 0.7d;
		case MEDIUM:
			return 0.5d;
		case LOW:
			return 0.4d;
		case VERY_LOW:
			return 0.2d;
		case LOWEST:
			return 0d;
		default:
			return 1d;
		}
	}

}
