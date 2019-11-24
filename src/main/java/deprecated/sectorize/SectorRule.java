package deprecated.sectorize;

import deprecated.model.schema.Transaction;

public interface SectorRule {
	
	boolean match(Transaction transaction, String count);
	String getSector(); 
	
}
