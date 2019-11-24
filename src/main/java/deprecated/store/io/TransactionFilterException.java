package deprecated.store.io;

public interface TransactionFilterException {
	
	default boolean isValid(String[] fields) {return true;};
	
	default String getError(String[] fields) {return "";};

}
