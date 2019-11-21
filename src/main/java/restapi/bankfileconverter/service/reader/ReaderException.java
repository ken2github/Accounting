package restapi.bankfileconverter.service.reader;

public class ReaderException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BankFileError bankFileError;

	public ReaderException(BankFileError dbError) {
		super();
		this.bankFileError = dbError;
	}

	public BankFileError getBankFileError() {
		return bankFileError;
	}
}