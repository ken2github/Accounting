package deprecated.sectorize;

import java.util.Optional;

import deprecated.model.schema.Transaction;

public interface Sectorizer {

	Optional<String> map(Transaction transaction, String count);
	
}
