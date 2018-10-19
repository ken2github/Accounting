package sectorize;

import model.schema.Transaction;

public interface SectorRule {
	
	boolean match(Transaction transaction, String count);
	String getSector(); 
	
}
