package store.io.readers;

import store.io.TransactionFilterException;

public class EdenredValidator implements TransactionFilterException {

	private static final String CONFIRMED_TRANSACTION_PREFIX = "transaction confirm";
	private static final int TRANSACTION_STATUS_FIELD = 2;
	
	@Override
	public boolean isValid(String[] fields) {
		return fields[TRANSACTION_STATUS_FIELD].contains(CONFIRMED_TRANSACTION_PREFIX);
	}

	@Override
	public String getError(String[] fields) {
		return "Transaction is not valid as the status ["+(fields[TRANSACTION_STATUS_FIELD])+"] is not allowed! It should start with ["+CONFIRMED_TRANSACTION_PREFIX+"]";
	}

}
