package restapi.transactionsoracle.service;

import java.util.List;
import java.util.Optional;

import model2.Transaction;
import restapi.transactionsoracle.api.SimilarityRelevance;

public interface TransactionsOracle {

	List<String> suggestSectorsForTransaction(Transaction transaction, int maxResults, SimilarityRelevance relevance);

	Optional<Boolean> suggestIsCommonForTransaction(Transaction transaction, SimilarityRelevance relevance);

}
