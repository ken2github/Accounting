package restapi.transactionsoracle.service.analyser;

import model2.DetailedTransaction;

public class TransactionWeight_1 {
	public DetailedTransaction transaction;
	public double weight;

	public DetailedTransaction getTransaction() {
		return transaction;
	}

	public TransactionWeight_1 setTransaction(DetailedTransaction transaction) {
		this.transaction = transaction;
		return this;
	}

	public double getWeight() {
		return weight;
	}

	public TransactionWeight_1 setWeight(double weight) {
		this.weight = weight;
		return this;
	}

	@Override
	public int hashCode() {
		return this.transaction.getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TransactionWeight_1)
			return this.transaction.getId().equals(((TransactionWeight_1) obj).getTransaction().getId());
		return false;
	}

	@Override
	public String toString() {
		return this.transaction.getId();
	}

}
