package restapi.bankprovisioninghelper.service;

import java.util.List;

import model2.Transaction;
import restapi.bankprovisioninghelper.api.Relevance;

public interface BankTransactionAnalytics {

	List<String> suggestSectorsForTransaction(Transaction transaction, int maxResults, Relevance relevance);

}
