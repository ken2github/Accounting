package sectorize;

import java.util.Optional;

import model.schema.Transaction;

public interface Sectorizer {

	Optional<String> map(Transaction transaction, String count);
	
}
