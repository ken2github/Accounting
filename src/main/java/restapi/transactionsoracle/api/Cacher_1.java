package restapi.transactionsoracle.api;

import java.util.List;

import restapi.transactionsoracle.service.analyser.TokenTreeItem_1;

public interface Cacher_1 {

	List<TokenTreeItem_1> getTransactionsHavingAtLeastOneCommonToken(List<String> tokens,
			boolean getAlsoTransactionWithNullSector, boolean getAlsoTransactionWithNullIsCommon);

	int transactionsContainingTokenInSet(String token, boolean getAlsoTransactionWithNullSector,
			boolean getAlsoTransactionWithNullIsCommon);

	int getTransactionSetSize();
}
