package dao;

import java.util.List;

public interface Observer<T> {

	public enum ChangeType {
		add, rem, mod;
	}

	public class Change<T> {
		private T t;
		private ChangeType changeType;

		public Change(T t, ChangeType changeType) {
			super();
			this.t = t;
			this.changeType = changeType;
		}

		public T getT() {
			return t;
		}

		public ChangeType getChangeType() {
			return changeType;
		}
	}

	void update(List<Change<T>> changes);

	String getId();

}
