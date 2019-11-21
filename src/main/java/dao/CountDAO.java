package dao;

import java.util.List;

import model2.Count;
import model2.DetailedCount;

public interface CountDAO {
	DetailedCount insert(Count c);

	DetailedCount update(DetailedCount c);

	boolean deleteById(String id);

	boolean deleteByName(String id);

	DetailedCount findById(String id);

	DetailedCount findByName(String countName);

	List<DetailedCount> findAll();
}
