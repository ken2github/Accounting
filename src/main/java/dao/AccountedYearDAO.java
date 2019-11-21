package dao;

import java.util.List;

public interface AccountedYearDAO {
	void insert(int year);

	void delete(int year);

	List<Integer> findAll();
}
