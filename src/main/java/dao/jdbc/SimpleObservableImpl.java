package dao.jdbc;

import java.util.ArrayList;
import java.util.List;

import dao.Observable;
import dao.Observer;

public class SimpleObservableImpl<T> implements Observable<T> {

	private List<Observer<T>> observers = new ArrayList<Observer<T>>();

	@Override
	synchronized public void subscribe(Observer<T> observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	@Override
	synchronized public void unsubscribe(Observer<T> observer) {
		observers.remove(observer);
	}

	synchronized public List<Observer<T>> getObservers() {
		return observers.subList(0, observers.size());
	}

}
