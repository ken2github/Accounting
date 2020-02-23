package restapi.transactionsoracle.service.analyser;

import java.util.List;

import model2.DetailedTransaction;

public class TokenTreeItem_1 implements Comparable<TokenTreeItem_1> {

	public DetailedTransaction transaction;
	public List<String> tokens;
	public String id;

	public TokenTreeItem_1(DetailedTransaction transaction, List<String> tokens, String id) {
		super();
		this.transaction = transaction;
		this.tokens = tokens;
		this.id = id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TokenTreeItem_1)
			return this.id.equals(((TokenTreeItem_1) obj).getId());
		return false;
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public int compareTo(TokenTreeItem_1 o) {
		return this.id.compareTo(o.getId());
	}

	public DetailedTransaction getTransaction() {
		return transaction;
	}

	public String getId() {
		return id;
	}

	public List<String> getTokens() {
		return tokens;
	}

}
