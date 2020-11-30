package dao;

import java.util.List;

import model2.CountYearMonthBalance;

public interface CountsBalancesByYearMonthDAO {

	List<CountYearMonthBalance> findByCountYear(String countName, int year);

	List<CountYearMonthBalance> findByYear(int year);
}
